package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{userId}/get/points")
    public Long getPoint(@PathVariable Long userId){
        return userService.getPoints(userId);
    }

    @PatchMapping ("/{userId}/update/name")
    public ResponseUser updateUserName(@PathVariable Long userId, @Valid @RequestBody UserDto.UpdateUserNmRequest updateUserNmRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        return userService.updateNm(userId, updateUserNmRequest.getName());
    }
    @PatchMapping("/{userId}/update/id")
    public ResponseUser updateUserId(@PathVariable Long userId, @Valid @RequestBody UserDto.UpdateIdRequest updateIdRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        return userService.updateId(userId, updateIdRequest.getId());
    }

    @GetMapping("/{userId}/info")
    public UserDto.UserInfo getUserInfo(@PathVariable Long userId){
        return userService.getUserInfo(userId);
    }

    //더미데이터 위한 api
    @PostMapping("/create")
    public void createUser(@RequestBody UserDto.CreateUserRequest createUserRequest){
        System.out.println(createUserRequest.getEmail());
        userService.createUser(createUserRequest);
    }
}
