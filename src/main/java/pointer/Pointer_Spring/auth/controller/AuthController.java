package pointer.Pointer_Spring.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.auth.service.KakaoAuthService;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * KAKAO 소셜 로그인 기능
     */
    @GetMapping(value = "/kakao")
    public void kakaoAuthRequest(@RequestParam String code) {
        kakaoAuthService.login(code);
    }
}
