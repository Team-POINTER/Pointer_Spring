package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.user.service.CloudinaryService;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final CloudinaryService cloudinaryService;


    @PostMapping("{userId}/upload/profile")
    public String uploadFile(@PathVariable Long userId,  @RequestParam("image") MultipartFile image) throws IOException {
        cloudinaryService.uploadProfileImage(userId, image);
        // 업로드된 파일의 공개 URL 반환
        return "fileUrl";
    }

}
