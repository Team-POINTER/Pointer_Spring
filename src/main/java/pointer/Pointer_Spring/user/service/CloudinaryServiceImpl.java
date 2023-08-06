package pointer.Pointer_Spring.user.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.Image.ImageType;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.ImageDto.*;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.user.response.ResponseImage;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class CloudinaryServiceImpl implements CloudinaryService{//기존 이미지 db에서 삭제?
    @Value("${default.profile.image.path}")
    private String DEFAULT_PROFILE_IMG_PATH;
    @Value("${default.background.image.path}")
    private String DEFAULT_BACKGROUND_IMG_PATH;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;

    private final Integer STATUS = 1;

    public CloudinaryServiceImpl(UserRepository userRepository, ImageRepository imageRepository, @Value("${cloudinary.cloud.name}") String cloudinaryName,
                                 @Value("${cloudinary.apikey}") String cloudinaryApiKey,
                                 @Value("${cloudinary.api.secret}") String cloudinaryApiSecret) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret
        ));
    }
    @Override
    public ResponseImage uploadProfileImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException {//이미지 업로드(수정)
         if(!userRepository.existsById(userId)){
             throw new CustomException(ExceptionCode.USER_NOT_FOUND);
         }
        String publicId = uploadImageInCloudinary(userId, "profile-photos", multipartFile);
        String extension = checkExtension(multipartFile);
        return new ResponseImage( ExceptionCode.USER_IMAGE_UPDATE_SUCCESS , uploadImage(userId, publicId, extension, ImageType.PROFILE));
    }
    @Override
    public ResponseImage uploadBackgroundImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException {
        if(!userRepository.existsById(userId)){
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }
        String publicId = uploadImageInCloudinary(userId, "background-photos", multipartFile);
        String extension = checkExtension(multipartFile);
        return new ResponseImage(ExceptionCode.BACKGROUND_IMAGE_UPDATE_SUCCESS , uploadImage(userId, publicId, extension, ImageType.BACKGROUND));
    }
    @Override
    public ImageUrlResponse getImages(Long userId){
        Image profileImage = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, ImageType.PROFILE, STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
                }
        );
        String publicId = profileImage.getImageUrl();
        String profileImageUrl = cloudinary.url().generate(publicId);

        Image backgroundImage = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, ImageType.BACKGROUND, STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
                }
        );
        publicId = backgroundImage.getImageUrl();
        String backgroundImageUrl = cloudinary.url().generate(publicId);

        return new ImageUrlResponse(profileImageUrl, backgroundImageUrl);
    }
    @Override
    public ResponseImage changeDefaultProfileImage(Long userId) throws IOException{//private deleteImage 호출 + defualt이미지로 바꾸기
        deleteImageInCloudinary(userId, ImageType.PROFILE);

        Image image = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, ImageType.PROFILE, STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
                }
        );
        image.updateImageUrl(DEFAULT_PROFILE_IMG_PATH);// + extension
        return new ResponseImage(ExceptionCode.USER_IMAGE_UPDATE_SUCCESS , cloudinary.url().generate(DEFAULT_PROFILE_IMG_PATH));
    }
    @Override
    public ResponseImage changeDefaultBackgroundImage(Long userId) throws IOException{//private deleteImage 호출 + defualt이미지로 바꾸기
        deleteImageInCloudinary(userId, ImageType.BACKGROUND);

        Image image = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, ImageType.BACKGROUND, STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
                }
        );
        image.updateImageUrl(DEFAULT_BACKGROUND_IMG_PATH);// + extension

        return new ResponseImage(ExceptionCode.BACKGROUND_IMAGE_UPDATE_SUCCESS, cloudinary.url().generate(DEFAULT_BACKGROUND_IMG_PATH));
    }



    private String uploadImageInCloudinary(Long userId, String folderNm, @NonNull MultipartFile multipartFile) throws IOException{
        String fileNm = userId.toString() + "_" + UUID.randomUUID();

        Map<String, Object> params = new HashMap<>();
        params.put("folder", folderNm); // 프로필 사진을 저장할 폴더
        params.put("public_id", fileNm); // 파일의 고유 ID

        // 이미지 업로드
        Map<String, Object> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), params); //public_id는 매개변수 이름, 뒤의 값이 저장될 이미지 이름
        // 업로드 응답에서 public_id 가져오기
        return uploadResult.get("public_id").toString();
    }
    private String uploadImage(Long userId, String publicId, String extension, ImageType imageType) throws IOException {
        String filePath = publicId + extension;
        String imageUrl = cloudinary.url().generate(filePath);
        Image foundImage = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, imageType, STATUS).orElse(null);

        if(foundImage != null){
            deleteImageInCloudinary(userId, imageType);
            foundImage.updateImageUrl(filePath);
        }else {
            Image image = new Image(filePath, imageType, userRepository.findById(userId).get());
            imageRepository.save(image);
        }

        return imageUrl;
    }
    private void deleteImageInCloudinary(Long userId, ImageType imageType) throws IOException{// 디폴트 이미지 아닐때 클라우드너리에 있던 원래 있던 이미지 삭제

        Image image = imageRepository.findByUserUserIdAndImageSortAndStatus(userId, imageType, 1).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.IMAGE_NOT_FOUND);
                }
        );
        if (!isDefaultImage(image.getImageUrl())) { // 이미지가 default가 아닌 경우에만 삭제
            String filePath = image.getImageUrl();

            int dotIndex = filePath.lastIndexOf(".");
            String publicId = filePath.substring(0, dotIndex);

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }
    private boolean isDefaultImage(String imagePublicId){
        int slashIndex = imagePublicId.lastIndexOf("/");
        String folderNm = imagePublicId.substring(0, slashIndex);
        return folderNm.equalsIgnoreCase("default");
    }
    private String checkExtension(MultipartFile multipartFile){
        if (multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.USER_IMAGE_UPDATE_INVALID);
        }

        String contentName = multipartFile.getOriginalFilename();
        String originalFileExtension;
        // 확장자 명이 없으면 이 파일은 잘 못 된 것

        if (contentName.contains("jpg")) {
            originalFileExtension = ".jpg";
        } else if (contentName.contains("jpeg")) {
            originalFileExtension = ".jpeg";
        } else if (contentName.contains("png")) {
            originalFileExtension = ".png";
        }
        // 다른 파일 명이면 아무 일 하지 않음
        else {
            throw new CustomException(ExceptionCode.IMAGE_INVALID);
        }
        return originalFileExtension;
    }
}
