package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
//import pointer.Pointer_Spring.User.dto.JoinRequestDto;
import pointer.Pointer_Spring.user.dto.KakaoRequestDto;
import pointer.Pointer_Spring.user.dto.TokenRequest;
import pointer.Pointer_Spring.user.response.ResponseKakaoUser;
import pointer.Pointer_Spring.user.service.AuthServiceImpl;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    //  test
    /*@GetMapping("/auth/kakao")
    public Object kakaoLogin(@RequestParam String code) {
        // code와 username이 들어올 예정
        System.out.println("AuthController.kakaoLogin - code = " + code);
        String accessToken = authServiceImpl.getKakaoAccessToken(code);
        ResponseKakaoUser responseKakaoUser = authServiceImpl.kakaoCheck(accessToken);
        System.out.println("responseKakaoUser.getMessage() = " + responseKakaoUser.getMessage());
        return new ResponseEntity<>(ExceptionCode.SIGNUP_CREATED_OK, HttpStatus.OK);
    }*/

    // real

    @PostMapping("/auth/login") // kakao social login
    public ResponseEntity<Object> login(@RequestBody TokenRequest tokenRequest) {
        System.out.println("AuthController.login");
        System.out.println("tokenRequest = " + tokenRequest);
        return new ResponseEntity<>(authServiceImpl.kakaoCheck(tokenRequest.getRefreshToken()), HttpStatus.OK);
    }

    @PostMapping("/auth/reissue") // token 재발급
    public ResponseEntity<Object> reissue(@RequestBody TokenRequest tokenRequest) {
        System.out.println("AuthController.reissue");
        System.out.println("tokenRequest = " + tokenRequest);

        return new ResponseEntity<>(authServiceImpl.reissue(tokenRequest), HttpStatus.OK);
    }
}
