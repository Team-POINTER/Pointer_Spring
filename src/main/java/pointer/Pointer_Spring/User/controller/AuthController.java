package pointer.Pointer_Spring.User.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.User.dto.TokenRequest;
import pointer.Pointer_Spring.User.service.AuthServiceImpl;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/auth/login") // kakao social login
    public ResponseEntity<Object> signup(@RequestBody TokenRequest tokenRequest) {
        return new ResponseEntity<>(authServiceImpl.kakaoCheck(tokenRequest.getToken()), HttpStatus.OK);
    }
}
