package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.response.ResponseUser;
import pointer.Pointer_Spring.user.service.UserService;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get/points")
    public ResponseUser getPoint(@CurrentUser UserPrincipal userPrincipal){
        return userService.getPoints(userPrincipal.getId());
    }

    @PatchMapping ("/update/name")
    public ResponseUser updateUserName(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody UserDto.UpdateUserNmRequest updateUserNmRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        return userService.updateNm(userPrincipal.getId(), updateUserNmRequest.getName());
    }
    @PatchMapping("/update/id")
    public ResponseUser updateUserId(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody UserDto.UpdateIdRequest updateIdRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        return userService.updateId(userPrincipal.getId(), updateIdRequest.getId());
    }

    @GetMapping("/info")
    public ResponseUser getUserInfo(@CurrentUser UserPrincipal userPrincipal){
        return userService.getUserInfo(userPrincipal.getId());
    }
}
