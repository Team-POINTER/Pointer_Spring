package pointer.Pointer_Spring.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final Integer STATUS = 1;
    private final Image.ImageType PROFILE_TYPE = Image.ImageType.PROFILE;

    // 친구 요청, 취소만 알림 -> 친구 성공시, 알림 갱신

    @Override
    public FriendDto.FriendListResponse getUserList(FriendDto.FindFriendDto dto, HttpServletRequest request) {
        List<User> userList = userRepository
                .findByIdContainingOrNameContainingAndStatus(dto.getKeyword(), dto.getKeyword(), STATUS);
        List<Friend> blockFriendList = friendRepository
                .findByUserUserIdAndRelationshipAndStatus(dto.getUserId(), Friend.Relation.BLOCK, STATUS);

        List<FriendDto.FriendList> friendList = new ArrayList<>();
        for (User user : userList) {
            if (blockFriendList.stream().filter(f -> user.getUserId().equals(f.getUserFriendId())).findAny().isEmpty()) { // 차단
                Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
                if (image.isPresent()) {
                    friendList.add(new FriendDto.FriendList(user).setFile(image.get().getImageUrl()));
                } else {
                    friendList.add(new FriendDto.FriendList(user));
                }
            }
        }
        return new FriendDto.FriendListResponse(friendList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getBlockFriendList(FriendDto.FriendUserDto dto, HttpServletRequest request) {
        List<Friend> friendList = friendRepository
                .findByUserUserIdAndRelationshipAndStatus(dto.getUserId(), Friend.Relation.BLOCK, STATUS);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new RuntimeException("유저를 조회할 수 없습니다.");
                    }
            );
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(user, Friend.Relation.BLOCK)
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(user, Friend.Relation.BLOCK));
            }
        }
        return new FriendDto.FriendInfoListResponse(friendInfoList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getFriendList(FriendDto.FriendUserDto dto, HttpServletRequest request) {
        List<Friend> friendList = friendRepository
                .findByUserUserIdAndNotRelationshipAndStatus(dto.getUserId(), Friend.Relation.BLOCK, STATUS);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(friend.getId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend.getUser(), friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend.getUser(), friend.getRelationship()));
            }
        }
        return new FriendDto.FriendInfoListResponse(friendInfoList);
    }

    @Override
    public ResponseFriend requestFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);
        /*Optional<Friend> findFriendMember
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), dto.getUserId(), STATUS);*/

        if (findFriendUser.isEmpty()) { // 요청
            User user = userRepository.findByUserIdAndStatus(dto.getUserId(), STATUS).orElseThrow(
                    () -> {
                        throw new RuntimeException("유저를 조회할 수 없습니다.");
                    }
            );

            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.REQUEST)
                    .userFriendId(dto.getMemberId())
                    .build();
                friendRepository.save(friendUser);

            // 차단이 안된 경우, 친구 요청 알림 전송

            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_OK);
        }
        /*else if (findFriendMember.isPresent()) { // 서로 요청시, 친구 수락
            Friend friendUser = Friend.builder()
                    .user(findFriendUser.get().getUser())
                    .relationship(Friend.Relation.SUCCESS)
                    .userFriendId(dto.getMemberId())
                    .build();
            friendRepository.save(friendUser);
            findFriendMember.get().setRelationship(Friend.Relation.SUCCESS);

            return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_OK);
        }*/
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend acceptFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);
        Optional<Friend> findFriendMember
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), dto.getUserId(), STATUS);

        if (findFriendMember.isPresent() && findFriendUser.isEmpty()) { // 수락
            // 차단 : 알림을 받지 못해서 자동으로 요청 불가
            User user = userRepository.findByUserIdAndStatus(dto.getUserId(), STATUS).orElseThrow(
                    () -> {
                        throw new RuntimeException("유저를 조회할 수 없습니다.");
                    }
            );

            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.SUCCESS)
                    .userFriendId(dto.getMemberId())
                    .build();
            friendRepository.save(friendUser);
            findFriendMember.get().setRelationship(Friend.Relation.SUCCESS);

            return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend refuseFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);
        /*if (findFriendUser.isPresent()) { // 관계 존재
            if (findFriendUser.get().getRelationship().equals(Friend.Relation.REQUEST)) {
                // 보낸 친구 요청 알림 삭제
            }
        }*/
        return new ResponseFriend(ExceptionCode.FRIEND_REFUSE_OK);
    }

    @Override
    public ResponseFriend blockFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);
        Optional<Friend> findFriendMember = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), dto.getUserId(), STATUS);

        if (findFriendUser.isPresent()) { // 관계 존재
            /*if (findFriendUser.get().getRelationship().equals(Friend.Relation.REQUEST)) {
                // 보낸 친구 요청 알림 삭제
            }*/
            findFriendUser.get().setRelationship(Friend.Relation.BLOCK);
        } else {
            User user = userRepository.findByUserIdAndStatus(dto.getUserId(), STATUS).orElseThrow(
                    () -> {
                        throw new RuntimeException("유저를 조회할 수 없습니다.");
                    }
            );

            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.BLOCK)
                    .userFriendId(dto.getMemberId())
                    .build();
            friendRepository.save(friendUser);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_BLOCK_OK);
    }

    @Override
    public ResponseFriend cancelRequest(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);

        if (findFriendUser.isPresent() && findFriendUser.get().getRelationship().equals(Friend.Relation.REQUEST)) {
            friendRepository.delete(findFriendUser.get());
            // 친구 요청 알림 삭제
            return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend cancelFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(dto.getUserId(), dto.getMemberId(), STATUS);
        Optional<Friend> findFriendMember = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), dto.getUserId(), STATUS);

        if (findFriendUser.isPresent() && findFriendMember.isPresent()) {
            friendRepository.delete(findFriendUser.get());
            friendRepository.delete(findFriendMember.get());
            return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }
}