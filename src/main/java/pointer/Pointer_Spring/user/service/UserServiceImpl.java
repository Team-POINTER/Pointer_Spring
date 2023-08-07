package pointer.Pointer_Spring.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.user.response.ResponseUser;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final FriendRepository friendRepository;

    private final Integer STATUS = 1;

    @Override
    public ResponseUser getPoints(Long userId){
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );
        return new ResponseUser(ExceptionCode.USER_GET_OK, user.getPoint());
    }

    @Override
    @Transactional
    public ResponseUser updateNm(Long userId, String userNm){
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );
        user.changeName(userNm);
        return new ResponseUser(ExceptionCode.USER_UPDATE_OK);
    }

    @Override
    @Transactional
    public ResponseUser updateId(Long userId, String newId){
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );
        // 형식 검사
        if (!isValidId(newId)) {
            throw new CustomException(ExceptionCode.INVALID_FORM);
        }
        // 중복 체크
        if (userRepository.existsById(newId)) {
            throw new CustomException(ExceptionCode.USER_DUPLICATED_ID);
        }

        user.changeId(newId);
        return new ResponseUser(ExceptionCode.USER_UPDATE_OK);
    }
    private boolean isValidId(String id) {
        // 영문, 숫자, 특수문자 . 과 _ 만 사용 가능
        final String pattern = "^[a-zA-Z0-9._]+$";
        // 30자 이내
        if (id.length() > 30) {
            return false;
        }
        // 띄어쓰기 허용하지 않음
        if (id.contains(" ")) {
            return false;
        }
        // 형식 검사
        return id.matches(pattern);
    }

    public ResponseUser getUserInfo(UserPrincipal userPrincipal, Long targetUserId) {
        if(targetUserId == null){
            User user = userRepository.findByUserId(userPrincipal.getId()).orElseThrow(
                    ()-> new CustomException(ExceptionCode.USER_NOT_FOUND)
            );
            return new ResponseUser(ExceptionCode.USER_GET_OK , new UserDto.UserInfo(user ,cloudinaryService.getImages(user.getUserId())));
        }
        Optional<User> userOptional = userRepository.findByUserId(targetUserId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Friend friend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), targetUserId, STATUS)
                    .orElse(null);
            if(friend != null) {
                return new ResponseUser(ExceptionCode.USER_GET_OK , new UserDto.UserInfo(user, friend.getRelationship() ,cloudinaryService.getImages(targetUserId)));
            }
            return new ResponseUser(ExceptionCode.USER_GET_OK , new UserDto.UserInfo(user ,cloudinaryService.getImages(targetUserId)));
        } else {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }
//        return userRepository.findByUserId(userId)
//                .map(user -> new UserDto.UserInfo(user,cloudinaryService.getImages(userId)))
//                .orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

}
