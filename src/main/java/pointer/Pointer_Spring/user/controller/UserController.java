package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.response.ResponseUser;
import pointer.Pointer_Spring.user.service.CloudinaryService;
import pointer.Pointer_Spring.user.service.UserService;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/get/points")
    public ResponseUser getPoint(@CurrentUser UserPrincipal userPrincipal){
        return userService.getPoints(userPrincipal.getId());
    }

    @PatchMapping ("/update/info")
    public ResponseUser updateUserInfo(@CurrentUser UserPrincipal userPrincipal,
                                       @RequestPart(value = "profile-image", required = false) MultipartFile profileImage,
                                       @RequestPart(value = "background-image", required = false) MultipartFile backgroundImage,
                                       @Valid @RequestPart("request") UserDto.UpdateUserInfoRequest updateUserInfoRequest, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        Long userId = userPrincipal.getId();
        if(profileImage != null){
            cloudinaryService.uploadProfileImage(userId, profileImage);
        } else if(updateUserInfoRequest.isProfileImageDefaultChange()){
            cloudinaryService.changeDefaultProfileImage(userId);
        }

        if(backgroundImage != null){
            cloudinaryService.uploadBackgroundImage(userId, backgroundImage);
        }else if(updateUserInfoRequest.isBackgroundImageDefaultChange()){
            cloudinaryService.changeDefaultBackgroundImage(userId);
        }

        return userService.updateNm(userId, updateUserInfoRequest.getName());
    }
//    @PatchMapping("/update/id")
//    public ResponseUser updateUserId(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody UserDto.UpdateIdRequest updateIdRequest, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            throw new CustomException(ExceptionCode.INVALID_FORM);
//        }
//        return userService.updateId(userPrincipal.getId(), updateIdRequest.getId());
//    }

    @GetMapping(value = {"{targetUserId}/info", "/info"})
    public ResponseUser getUserInfo(@CurrentUser UserPrincipal userPrincipal, @PathVariable(required = false) Long targetUserId){
        return userService.getUserInfo(userPrincipal, targetUserId);
    }
}
