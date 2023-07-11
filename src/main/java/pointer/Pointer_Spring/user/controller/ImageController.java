package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.user.dto.ImageDto;
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

    @GetMapping("{userId}/get")
    public ImageDto.ImageUrlResponse getImage(@PathVariable Long userId) {
        return cloudinaryService.getImages(userId);
    }
    @PatchMapping("{userId}/change/default/profile")
    public String changeDefaultProfileImage(@PathVariable Long userId) throws IOException {
        return cloudinaryService.changeDefaultProfileImage(userId);
    }
    @PatchMapping("{userId}/change/default/background")
    public String changeDefaultBackgroundImage(@PathVariable Long userId) throws IOException {
        return cloudinaryService.changeDefaultBackgroundImage(userId);
    }
}
