package pointer.Pointer_Spring.User.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.auth.dto.KakaoDto;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveKakaoUser(KakaoDto findKakaoDto) {
        Optional<User> findUser = userRepository.findByEmailAndTypeAndStatusEquals(findKakaoDto.getEmail(), User.Type.KAKAO, 1);
        if (findUser.isEmpty()) {
            User kakaoUser = User.builder()
                    .id(findKakaoDto.getId())
                    .email(findKakaoDto.getEmail())
                    .nickname(findKakaoDto.getNickname())
                    .type(User.Type.KAKAO)
                    .build();

            userRepository.save(kakaoUser);
        }
//        else {
//            중복되는 이메일이자 가입한 회원
//        }
    }

}
