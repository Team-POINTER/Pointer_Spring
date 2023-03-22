package pointer.Pointer_Spring.User.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.User.dto.MemberRequestDto;
import pointer.Pointer_Spring.User.dto.TokenDto;
import pointer.Pointer_Spring.User.dto.TokenRequestDto;
import pointer.Pointer_Spring.User.dto.KakaoRequestDto;
import pointer.Pointer_Spring.User.service.AuthService;

// 해당 mapping들만 header에 token 없이도 처리 가능함
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public Object signup(@RequestBody KakaoRequestDto kakaoRequestDto,  @RequestParam(defaultValue = "username") String username) {
        // code와 username이 들어올 예정
        return new ResponseEntity<>(authService.signup(kakaoRequestDto, username), HttpStatus.OK);
    }
}
