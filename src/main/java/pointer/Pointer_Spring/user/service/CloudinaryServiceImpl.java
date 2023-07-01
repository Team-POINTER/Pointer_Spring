package pointer.Pointer_Spring.user.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.Image.ImageType;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService{//기존 이미지 db에서 삭제?
//    @Value("${profile.img.path}")
//    private String PROFILE_IMG_PATH;
//    @Value("${background.img.path}")
//    private String BACKGROUND_IMG_PATH;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;


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
    public void uploadProfileImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException {//이미지 업로드(수정)
         if(!userRepository.existsById(userId)){
             throw new CustomException(ExceptionCode.USER_NOT_FOUND);
         }
        String fileNm = uploadImageInCloudinary(userId, multipartFile);
        uploadImage(userId, fileNm, ImageType.PROFILE);
    }

    public void uploadBackgroundImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException {

    }
    public String getImage(Long userId){
        //imageRepository.

        return "string";
    }
    public void changeDefaultImage(Long userId){

    }


    private String uploadImageInCloudinary(Long userId, @NonNull MultipartFile multipartFile) throws IOException{
        if (multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.USER_IMAGE_UPDATE_INVALID);
        }

        String extension = checkExtension(multipartFile);
        String fileNm = userId.toString() + "_" + UUID.randomUUID().toString() + extension;

        Map<String, Object> params = new HashMap<>();
        params.put("folder", "profile-photos"); // 프로필 사진을 저장할 폴더
        params.put("public_id", fileNm); // 파일의 고유 ID

        cloudinary.uploader().upload(multipartFile.getBytes(), params); //public_id는 매개변수 이름, 뒤의 값이 저장될 이미지 이름

        return fileNm;
    }
    private void uploadImage(Long userId, String fileNm, ImageType imageType) {
        String imageUrl = cloudinary.url().generate(fileNm);

        Image image = new Image(imageUrl, imageType, userRepository.findById(userId).get());
        imageRepository.save(image);
    }
    private void deleteImage(){

    }
    private String checkExtension(MultipartFile multipartFile){

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
