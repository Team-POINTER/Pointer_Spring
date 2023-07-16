package pointer.Pointer_Spring.user.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.user.dto.ImageDto;
import pointer.Pointer_Spring.user.response.ResponseImage;

import java.io.IOException;

public interface CloudinaryService {
    ResponseImage uploadProfileImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException;
    ResponseImage uploadBackgroundImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException;
    ImageDto.ImageUrlResponse getImages(Long userId);
    ResponseImage changeDefaultProfileImage(Long userId) throws IOException;
    ResponseImage changeDefaultBackgroundImage(Long userId) throws IOException;
}
