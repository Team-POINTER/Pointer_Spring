package pointer.Pointer_Spring.user.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadProfileImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException;
    String uploadBackgroundImage(Long userId, @NonNull MultipartFile multipartFile) throws IOException;
}
