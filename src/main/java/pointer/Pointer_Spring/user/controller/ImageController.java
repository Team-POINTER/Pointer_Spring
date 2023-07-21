package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.user.response.ResponseImage;
import pointer.Pointer_Spring.user.service.CloudinaryService;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class ImageController {
    private final CloudinaryService cloudinaryService;


    @PostMapping("{userId}/upload/profile")
    public ResponseImage uploadProfileImage(@PathVariable Long userId, @RequestParam("image") MultipartFile image) throws IOException {
        return cloudinaryService.uploadProfileImage(userId, image);

    }

    @PostMapping("{userId}/upload/background")
    public ResponseImage uploadBackgroundImage(@PathVariable Long userId,  @RequestParam("image") MultipartFile image) throws IOException {
        return cloudinaryService.uploadBackgroundImage(userId, image);
    }

    @GetMapping("{userId}/get")
    public ResponseImage getImage(@PathVariable Long userId) {
        return new ResponseImage(ExceptionCode.IMAGE_GET_OK, cloudinaryService.getImages(userId));
    }
    @PatchMapping("{userId}/change/default/profile")
    public ResponseImage changeDefaultProfileImage(@PathVariable Long userId) throws IOException {
        return cloudinaryService.changeDefaultProfileImage(userId);
    }
    @PatchMapping("{userId}/change/default/background")
    public ResponseImage changeDefaultBackgroundImage(@PathVariable Long userId) throws IOException {
        return cloudinaryService.changeDefaultBackgroundImage(userId);
    }
}
