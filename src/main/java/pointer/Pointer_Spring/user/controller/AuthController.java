package pointer.Pointer_Spring.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.user.dto.TokenRequest;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.service.AuthServiceImpl;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    //  test
    @GetMapping("/auth/kakao")
    public Object kakaoLogin(@RequestParam String code) {
        // code와 username이 들어올 예정
        String accessToken = authServiceImpl.getKakaoAccessToken(code);
        return new ResponseEntity<>(authServiceImpl.kakaoCheck(accessToken), HttpStatus.OK);
    }

    // real
    @PostMapping("/auth/login") // kakao social login
    public ResponseEntity<Object> login(@RequestBody TokenRequest tokenRequest) {
        return new ResponseEntity<>(authServiceImpl.kakaoCheck(tokenRequest.getRefreshToken()), HttpStatus.OK);
    }

    @PostMapping("/auth/reissue") // token 재발급
    public ResponseEntity<Object> reissue(@RequestBody TokenRequest tokenRequest) {
        return new ResponseEntity<>(authServiceImpl.reissue(tokenRequest), HttpStatus.OK);
    }

    @PostMapping("/auth/userId")
    public ResponseEntity<Object> saveId(@RequestBody UserDto.UserInfo info) {
        return new ResponseEntity<>(authServiceImpl.saveId(info), HttpStatus.OK);
    }
}
