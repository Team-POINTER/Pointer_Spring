package pointer.Pointer_Spring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.exception.ResourceNotFoundException;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService { // 로그인된 사용자 처리

    private final UserRepository userRepository;
    private static final Integer STATUS = 1;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        //System.out.println("CustomUserDetailsService.loadUserByUsername");

        User user = userRepository.findByEmailAndStatus(email, STATUS)
                .orElseThrow(() -> {throw new CustomException(ExceptionCode.USER_NOT_FOUND);
            });


        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        //System.out.println("CustomUserDetailsService.loadUserById");
        User user = userRepository.findByUserIdAndStatus(id, STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        return UserPrincipal.create(user);
    }
}