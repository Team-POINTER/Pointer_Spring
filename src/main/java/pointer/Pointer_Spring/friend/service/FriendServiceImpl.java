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

    @Override
    public FriendDto.FriendListResponse getUserList(FriendDto.FindFriendDto dto, HttpServletRequest request) {
        List<User> userList
                = userRepository.findByIdContainingOrNameContainingAndStatus(dto.getKeyword(), dto.getKeyword(), STATUS);

        List<FriendDto.FriendList> friendList = new ArrayList<>();
        for (User user : userList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendList.add(new FriendDto.FriendList(user).setFile(image.get().getImageUrl()));
            } else {
                friendList.add(new FriendDto.FriendList(user));
            }
        }
        return new FriendDto.FriendListResponse(friendList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getFriendList(FriendDto.FriendUserDto dto,
                                                          HttpServletRequest request) {
        User user = userRepository.findByIdAndStatus(dto.getId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );
        List<Friend> friendList =
                friendRepository.findByUserUserIdAndStatus(user.getUserId(), STATUS);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(friend.getId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend.getUser(), friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            }
            else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend.getUser(), friend.getRelationship()));
            }
        }
        return new FriendDto.FriendInfoListResponse(friendInfoList);
    }

    @Override
    public ResponseFriend requestFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        User user = userRepository.findByIdAndStatus(dto.getId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );
        User member = userRepository.findByIdAndStatus(dto.getMemberId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );

        Optional<Friend> findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(user.getUserId(), member.getUserId(), STATUS);
        Optional<Friend> findFriendMember = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(member.getUserId(), user.getUserId(), STATUS);
        if (findFriendUser.isEmpty() && findFriendMember.isEmpty()) { // 요청
            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.REQUEST)
                    .userFriendId(member.getUserId())
                    .build();
            friendRepository.save(friendUser);

            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_OK);
        }
        else if (findFriendMember.isPresent() && findFriendUser.isEmpty()) { // 수락
            if (!findFriendMember.get().getRelationship().equals(Friend.Relation.REFUSE)) {
                return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_NOT);
            }
            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.SUCCESS)
                    .userFriendId(member.getUserId())
                    .build();
            friendRepository.save(friendUser);
            findFriendMember.get().setRelationship(Friend.Relation.SUCCESS);

            return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend refuseFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        User user = userRepository.findByIdAndStatus(dto.getId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );
        User member = userRepository.findByIdAndStatus(dto.getMemberId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );

        Optional<Friend> findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(user.getUserId(), member.getUserId(), STATUS);
        Optional<Friend> findFriendMember = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(member.getUserId(), user.getUserId(), STATUS);
        if (findFriendUser.isPresent() && findFriendMember.isPresent()) { // 요청
            findFriendMember.get().setRelationship(Friend.Relation.REQUEST);
            findFriendUser.get().setRelationship(Friend.Relation.REFUSE);
            return new ResponseFriend(ExceptionCode.FRIEND_REFUSE_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REFUSE_OK);
    }

    @Override
    public ResponseFriend cancelFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        User user = userRepository.findByIdAndStatus(dto.getId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );
        User member = userRepository.findByIdAndStatus(dto.getMemberId(), STATUS).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
        );

        Optional<Friend> findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(user.getUserId(), member.getUserId(), STATUS);
        if (findFriendUser.isPresent() && findFriendUser.get().getRelationship().equals(Friend.Relation.REQUEST)) {
            friendRepository.delete(findFriendUser.get());
            return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

}
