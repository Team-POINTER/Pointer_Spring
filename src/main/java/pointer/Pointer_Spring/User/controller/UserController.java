package pointer.Pointer_Spring.User.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    // header에 JWT Bearer 필요함
    @GetMapping("/{email}")
    public Object findUserInfoByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>(ExceptionCode.USER_NOT_FOUND, HttpStatus.OK);
        }
        return new ResponseEntity<>(ExceptionCode.USER_GET_OK, HttpStatus.OK);
    }
}
