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
    public String uploadProfileImage(@PathVariable Long userId,  @RequestParam("image") MultipartFile image) throws IOException {
        return cloudinaryService.uploadProfileImage(userId, image);

    }

    @PostMapping("{userId}/upload/background")
    public String uploadBackgroundImage(@PathVariable Long userId,  @RequestParam("image") MultipartFile image) throws IOException {
        return cloudinaryService.uploadBackgroundImage(userId, image);
    }

}
