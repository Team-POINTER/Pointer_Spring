package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.response.ResponseImage;
import pointer.Pointer_Spring.user.service.CloudinaryService;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/image")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class ImageController {
    private final CloudinaryService cloudinaryService;

    @GetMapping("/get")
    public ResponseImage getImage(@CurrentUser UserPrincipal userPrincipal) {
        return new ResponseImage(ExceptionCode.IMAGE_GET_OK, cloudinaryService.getImages(userPrincipal.getId()));
    }

    //    @PostMapping("upload/profile")
//    public ResponseImage uploadProfileImage(@CurrentUser UserPrincipal userPrincipal, @RequestParam("image") MultipartFile image) throws IOException {
//        return cloudinaryService.uploadProfileImage(userPrincipal.getId(), image);
//
//    }
//
//    @PostMapping("upload/background")
//    public ResponseImage uploadBackgroundImage(@CurrentUser UserPrincipal userPrincipal,  @RequestParam("image") MultipartFile image) throws IOException {
//        return cloudinaryService.uploadBackgroundImage(userPrincipal.getId(), image);
//    }
//    @PatchMapping("change/default/profile")
//    public ResponseImage changeDefaultProfileImage(@CurrentUser UserPrincipal userPrincipal) throws IOException {
//        return cloudinaryService.changeDefaultProfileImage(userPrincipal.getId());
//    }
//    @PatchMapping("change/default/background")
//    public ResponseImage changeDefaultBackgroundImage(@CurrentUser UserPrincipal userPrincipal) throws IOException {
//        return cloudinaryService.changeDefaultBackgroundImage(userPrincipal.getId());
//    }
}
