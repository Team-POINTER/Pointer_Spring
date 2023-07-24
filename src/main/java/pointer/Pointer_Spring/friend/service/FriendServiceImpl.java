package pointer.Pointer_Spring.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.alarm.domain.Alarm;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.alarm.service.KakaoPushNotiService;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final AlarmRepository alarmRepository;
    private final KakaoPushNotiService kakaoPushNotiService;
    private final Integer STATUS = 1;
    private final Integer PAGE_COUNT = 30;
    private final Image.ImageType PROFILE_TYPE = Image.ImageType.PROFILE;

    // 친구 요청, 취소만 알림 -> 친구 성공시, 알림 갱신

    // 검색
    private List<User> fetchPagesOffsetUser(FriendDto.FindFriendDto findFriendDto){
        PageRequest pageRequest = PageRequest.of(findFriendDto.getLastPage(), PAGE_COUNT, Sort.by("userId"));
        return userRepository.findAllByIdContainingOrNameContainingAndStatusOrderByUserIdDesc
                (findFriendDto.getKeyword(), findFriendDto.getKeyword(), STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetUserFriend(UserPrincipal userPrincipal, FriendDto.FindFriendDto findFriendDto){
        PageRequest pageRequest = PageRequest.of(findFriendDto.getLastPage(), PAGE_COUNT, Sort.by("friendName"));
        return friendRepository.findAllByUserUserIdAndFriendUserIdContainingOrFriendNameContainingAndRelationshipNotAndStatusOrderByUserIdDesc
                (userPrincipal.getId(), findFriendDto.getKeyword(), findFriendDto.getKeyword(), Friend.Relation.BLOCK, STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetUserBlockFriend(UserPrincipal userPrincipal, FriendDto.FindFriendDto findFriendDto){
        PageRequest pageRequest = PageRequest.of(findFriendDto.getLastPage(), PAGE_COUNT, Sort.by("friendName"));
        return friendRepository.findAllByUserUserIdAndFriendUserIdContainingOrFriendNameContainingAndRelationshipAndStatusOrderByUserIdDesc
                (userPrincipal.getId(), findFriendDto.getKeyword(), findFriendDto.getKeyword(), Friend.Relation.BLOCK, STATUS, pageRequest);
    }

    // 조회
    private List<Friend> fetchPagesOffsetFriend(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto){
        PageRequest pageRequest = PageRequest.of(dto.getLastPage(), PAGE_COUNT, Sort.by("friendName"));
        return friendRepository.findAllByUserUserIdAndRelationshipNotAndStatus
                (userPrincipal.getId(), Friend.Relation.BLOCK, STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetBlock(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto){
        PageRequest pageRequest = PageRequest.of(dto.getLastPage(), PAGE_COUNT, Sort.by("friendName"));
        return friendRepository.findAllByUserUserIdAndRelationshipAndStatus
                (userPrincipal.getId(), Friend.Relation.BLOCK, STATUS, pageRequest);
    }

    @Override
    public UserDto.UserListResponse getUserList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto, HttpServletRequest request) {
        List<User> userList = fetchPagesOffsetUser(dto);
        List<UserDto.UserList> friendList = new ArrayList<>();
        for (User user : userList) {
            if (!user.getUserId().equals(userPrincipal.getId())) { // 차단
                Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
                if (image.isPresent()) {
                    friendList.add(new UserDto.UserList(user).setFile(image.get().getImageUrl()));
                } else {
                    friendList.add(new UserDto.UserList(user));
                }
            }
        }
        Long total = userRepository.countByIdContainingOrNameContainingAndStatus(dto.getKeyword(), dto.getKeyword(), STATUS);
        return new UserDto.UserListResponse(total, friendList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto, HttpServletRequest request) {
        List<Friend> friendList = fetchPagesOffsetFriend(userPrincipal, dto);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(friend.getId(), PROFILE_TYPE, STATUS);
            User friendUser = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );
            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship()));
            }
        }

        Long total = friendRepository.countByUserUserIdAndRelationshipNotAndStatus(userPrincipal.getId(), Friend.Relation.BLOCK, STATUS);
        return new FriendDto.FriendInfoListResponse(total, friendInfoList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getUserFriendList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto, HttpServletRequest request) {
        List<Friend> userFriendList = fetchPagesOffsetUserFriend(userPrincipal, dto);
        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : userFriendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(friend.getId(), PROFILE_TYPE, STATUS);
            User friendUser = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );

            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship()));
            }
        }

        Long total = friendRepository.countByUserUserIdAndRelationshipNotAndStatus(userPrincipal.getId(), Friend.Relation.BLOCK, STATUS);
        return new FriendDto.FriendInfoListResponse(total, friendInfoList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getUserBlockFriendList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto, HttpServletRequest request) {
        List<Friend> userFriendList = fetchPagesOffsetUserBlockFriend(userPrincipal, dto);
        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : userFriendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(friend.getId(), PROFILE_TYPE, STATUS);
            User friendUser = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );

            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, friend.getRelationship()));
            }
        }

        Long total = friendRepository.countByUserUserIdAndRelationshipAndStatus(userPrincipal.getId(), Friend.Relation.BLOCK, STATUS);
        return new FriendDto.FriendInfoListResponse(total, friendInfoList);
    }

    @Override
    public FriendDto.FriendInfoListResponse getBlockFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto, HttpServletRequest request) {
        List<Friend> friendList = fetchPagesOffsetBlock(userPrincipal, dto);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                    }
            );

            User friendUser = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );

            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);

            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, Friend.Relation.BLOCK)
                        .setFile(image.get().getImageUrl()));throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(friend, friendUser, Friend.Relation.BLOCK));
            }
        }

        Long total = friendRepository.countByUserUserIdAndRelationshipAndStatus(userPrincipal.getId(), Friend.Relation.BLOCK, STATUS);
        return new FriendDto.FriendInfoListResponse(total, friendInfoList);
    }

    /*private List<Friend> fetchPages(FriendDto.FindFriendDto findFriendDto)  { // findFriendDto에 lastName의 userId 추가 필요
        PageRequest pageRequest = PageRequest.of(0, findFriendDto.getSize());
        if (findFriendDto.getLastId() == null) { // 처음 조회
            return friendRepository.findAllByUserUserIdAndRelationshipNotAndStatusOrderByNameDesc
                    (findFriendDto.getUserId(), Friend.Relation.BLOCK, STATUS, findFriendDto.getName(), pageRequest);
        }
        else { // 불러오기
            return friendRepository.findAllByUserFriendIdLessThanAndUserUserIdAndRelationshipNotAndStatusOrderByNameDesc
                    (findFriendDto.getUserId(), Friend.Relation.BLOCK, STATUS, findFriendDto.getName(), pageRequest);
        }
    }*/

    @Override
    public ResponseFriend requestFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS);
        Optional<Friend> findFriendMember
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), userPrincipal.getId(), STATUS);

        if (findFriendUser.isEmpty()) { // 요청 삭제 -> 요청
            User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                    }
            );

            User friend = userRepository.findByUserIdAndStatus(dto.getMemberId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );

            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.REQUEST)
                    .friend(friend)
                    .build();
            friendRepository.save(friendUser);

            System.out.println(friendUser.getFriendName());
            System.out.println(friendUser.getUser().getName());

            // 차단 아닌 경우에만 친구 조회 범위에 포함
            if (findFriendMember.isEmpty()) { // ! findFriendMember.get().getRelationship().equals(Friend.Relation.BLOCK) 의미
                Friend userFriend = Friend.builder()
                        .user(friend)
                        .relationship(Friend.Relation.REQUESTED)
                        .friend(user)
                        .build();
                friendRepository.save(userFriend);

                // 차단이 안된 경우, 친구 요청 알림 전송
                Alarm alarm = Alarm.builder()
                        .sendUserId(user.getUserId())
                        .receiveUserId(friend.getUserId())
                        .type(Alarm.AlarmType.FRIEND_REQUEST)
                        .content(friendUser.getUser().getName()+Alarm.AlarmType.FRIEND_REQUEST.getMessage())
                        .build();
                alarmRepository.save(alarm);

//                ActiveAlarm activeAlarm = ActiveAlarm.builder()
//                        //.requestUserId(user.getUserId())
//                        //.responseUserId(friend.getUserId())
//                        .build();
//                activeAlarmRepository.save(activeAlarm);

                //kakaoPushRequestMap.put("custom_field", Map.of("room_id", room.getRoomId()));
                AlarmDto.KakaoPushRequest kakaoPushRequest = AlarmDto.KakaoPushRequest.builder()
                        .forApns(AlarmDto.PushType.builder()
                                .message(alarm.getContent())
                                .apnsEnv("sandbox")
                                .build())
                        .build();
                kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(friend.getUserId())), kakaoPushRequest);

            }

            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_OK);
        }

        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend acceptFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Friend findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        Friend findFriendMember = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), userPrincipal.getId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                });

        if (findFriendUser.getRelationship().equals(Friend.Relation.REQUESTED)) { // 수락
            // 차단 : 알림을 받지 못해서 자동으로 요청 불가
            findFriendUser.setRelationship(Friend.Relation.SUCCESS);
            findFriendMember.setRelationship(Friend.Relation.SUCCESS);

            Alarm alarm = Alarm.builder()
                    .sendUserId(dto.getMemberId())
                    .receiveUserId(findFriendUser.getUser().getUserId())
                    .type(Alarm.AlarmType.FRIEND_ACCEPT)
                    .content(findFriendUser.getUser().getName()
                            +Alarm.AlarmType.FRIEND_ACCEPT.getMessage())
                    .build();
            alarmRepository.save(alarm);

//            ActiveAlarm activeAlarm = ActiveAlarm.builder()
//                    //.requestUserId(dto.getUserId())
//                    //.responseUserId(dto.getMemberId())
//                    .build();
//            activeAlarmRepository.save(activeAlarm);

            AlarmDto.KakaoPushRequest kakaoPushRequest = AlarmDto.KakaoPushRequest.builder()
                    .forApns(AlarmDto.PushType.builder()
                            .message(alarm.getContent())
                            .apnsEnv("sandbox")
                            .build())
                    .build();
            kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(findFriendUser.getUser().getUserId())), kakaoPushRequest);

            return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend refuseFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Friend findFriend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        if (findFriend.getRelationship().equals(Friend.Relation.REQUEST)) { // 관계 존재
            findFriend.setRelationship(Friend.Relation.REFUSE);
            friendRepository.save(findFriend);

            Alarm alarm = alarmRepository.findBySendUserIdAndReceiveUserIdAndType(dto.getMemberId(), dto.getMemberId(), Alarm.AlarmType.FRIEND_REQUEST)
                    .orElseThrow(() -> {
                        throw new CustomException(ExceptionCode.ACTIVE_ALARM_NOT_FOUND);
                    });

            alarmRepository.delete(alarm);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REFUSE_OK);
    }

    @Override
    public ResponseFriend blockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Optional<Friend> findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS);

        if (findFriendUser.isPresent()) { // 관계 존재 : request, requested, success
            // 내 알림에서 친구 관련 알림 모두 숨김
            findFriendUser.get().setRelationship(Friend.Relation.BLOCK);
        } else {
            User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                    }
            );

            User friend = userRepository.findByUserIdAndStatus(dto.getMemberId(), STATUS).orElseThrow(
                    () -> {
                        throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                    }
            );

            Friend friendUser = Friend.builder()
                    .user(user)
                    .relationship(Friend.Relation.BLOCK)
                    .friend(friend)
                    .build();
            friendRepository.save(friendUser);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_BLOCK_OK);
    }

    @Override
    public ResponseFriend cancelRequest(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Friend findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        Friend findFriendMember = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), userPrincipal.getId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                });

        if (findFriendUser.getRelationship().equals(Friend.Relation.REQUEST)) {
            friendRepository.delete(findFriendUser);
            if (!findFriendMember.getRelationship().equals(Friend.Relation.BLOCK)) {
                friendRepository.delete(findFriendMember);
            }
            // 상대 알림에서 친구 요청 삭제
            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_CANCEL_NOT);
    }

    @Override
    public ResponseFriend cancelFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request) {
        Friend findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndRelationshipNotAndStatus(userPrincipal.getId(), dto.getMemberId(), Friend.Relation.BLOCK, STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        Optional<Friend> findFriendMember = friendRepository
                .findByUserUserIdAndUserFriendIdAndRelationshipNotAndStatus(dto.getMemberId(), userPrincipal.getId(), Friend.Relation.BLOCK, STATUS);

        if (findFriendUser.getRelationship().equals(Friend.Relation.SUCCESS)) {
            friendRepository.delete(findFriendUser);
            // 차단 아닌 친구 상태
            findFriendMember.ifPresent(friendRepository::delete);
            return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_NOT);
    }
}