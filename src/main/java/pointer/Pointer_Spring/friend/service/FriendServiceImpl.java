package pointer.Pointer_Spring.friend.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.asm.internal.Relationship;
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
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final ImageRepository imageRepository;
    private final AlarmRepository alarmRepository;
    private final KakaoPushNotiService kakaoPushNotiService;
    private final Integer STATUS = 1;
    private final Integer PAGE_COUNT = 30;
    private final Image.ImageType PROFILE_TYPE = Image.ImageType.PROFILE;

    // 친구 요청, 취소만 알림 -> 친구 성공시, 알림 갱신

    // 검색
    private List<User> fetchPagesOffsetUser(UserPrincipal userPrincipal, String keyword, int lastPage){
        PageRequest pageRequest = PageRequest.of(lastPage, PAGE_COUNT, Sort.by("name"));

        return userRepository.findUsersWithKeywordAndStatusNotFriendOfUser(userPrincipal.getId(), keyword, STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetUserFriend(User user, String keyword, int lastPage){
        PageRequest pageRequest = PageRequest.of(lastPage, PAGE_COUNT, Sort.by("user.name"));
        return friendRepository.findUsersAndFriends(user.getUserId(), keyword, STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetTargetFriend(User user, String keyword, int lastPage, Long meId){
        PageRequest pageRequest = PageRequest.of(lastPage, PAGE_COUNT, Sort.by("user.name"));
        return friendRepository.findTargetAndFriends(user.getUserId(), keyword, meId, STATUS, pageRequest);
    }

    private List<Friend> fetchPagesOffsetUserBlockFriend(UserPrincipal userPrincipal,  String keyword, int lastPage){
        PageRequest pageRequest = PageRequest.of(lastPage, PAGE_COUNT, Sort.by(Sort.Direction.DESC,"updatedAt"));
        return friendRepository.findUsersAndBlockFriends(userPrincipal.getId(), keyword, STATUS, pageRequest);
    }


    /*@Override
    public UserDto.UserListResponse getUserList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto) {
        List<User> userList = fetchPagesOffsetUser(userPrincipal, dto.getKeyword(), dto.getLastPage()); // 본인 제외
        List<UserDto.UserList> friendList = new ArrayList<>();
        for (User user : userList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendList.add(new UserDto.UserList(user).setFile(image.get().getImageUrl()));
            } else {
                friendList.add(new UserDto.UserList(user));
            }
        }

        Long total = userRepository.countUsersWithKeywordAndStatusNotFriendOfUser(userPrincipal.getId(), dto.getKeyword(), STATUS);
        return new UserDto.UserListResponse(ExceptionCode.USER_SEARCH_OK, total, friendList, dto.getLastPage());
    }*/

    @Override
    public UserDto.UserInfoListResponse getUserInfoList(UserPrincipal userPrincipal, String keyword, int lastPage) {
        List<User> userList = fetchPagesOffsetUser(userPrincipal, keyword, lastPage); // 본인 제외
        List<UserDto.UserInfoList2> friendList = new ArrayList<>();
        for (User user : userList) {

            Optional<Friend> friend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), user.getUserId(), STATUS);
            Friend.Relation relationship;
            if (friend.isPresent()) {
                relationship = friend.get().getRelationship();
            } else {
                relationship = Friend.Relation.NONE;
            }

            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
            if (image.isPresent()) {
                friendList.add(new UserDto.UserInfoList2(user, relationship).setFile(image.get().getImageUrl()));
            } else {
                friendList.add(new UserDto.UserInfoList2(user, relationship));
            }
        }

        Long total = userRepository.countUsersWithKeywordAndStatusNotFriendOfUser(userPrincipal.getId(), keyword, STATUS);
        return new UserDto.UserInfoListResponse(ExceptionCode.USER_SEARCH_OK, total, friendList, lastPage);

    }

    @Override
    public FriendDto.FriendInfoListResponse getUserFriendList(UserPrincipal userPrincipal, Long targetId, String keyword, int lastPage) {

        User findUser = userRepository.findByUserIdAndStatus(targetId, STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        Long total;

        if (userPrincipal.getId().equals(targetId)) {
            List<Friend> objects = fetchPagesOffsetUserFriend(findUser, keyword, lastPage);

            for (Friend friend : objects) {
                User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).get();
                Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);

                if (image.isPresent()) {
                    friendInfoList.add(new FriendDto.FriendInfoList(user, friend.getRelationship())
                            .setFile(image.get().getImageUrl()));
                } else {
                    friendInfoList.add(new FriendDto.FriendInfoList(user, friend.getRelationship()));
                }
            }
            total = friendRepository.countUsersByFriendCriteria(targetId, keyword, STATUS);

        } else {
            List<Friend> objects = fetchPagesOffsetTargetFriend(findUser, keyword, lastPage, userPrincipal.getId());

            for (Friend friend : objects) {
                User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).get();
                Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);

                // 나 - 친구의 친구 관계
                Friend.Relation relationship;
                Optional<Friend> optionalFriend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), user.getUserId(), STATUS);

                if (optionalFriend.isEmpty()) {
                    relationship = Friend.Relation.NONE;
                } else {
                    relationship = optionalFriend.get().getRelationship();
                }

                if (image.isPresent()) {
                    friendInfoList.add(new FriendDto.FriendInfoList(user, relationship).setFile(image.get().getImageUrl()));
                } else {
                    friendInfoList.add(new FriendDto.FriendInfoList(user, relationship));
                }
            }
            total = friendRepository.countTargetByFriendCriteria(targetId, keyword, userPrincipal.getId(), STATUS);
        }

        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        return new FriendDto.FriendInfoListResponse(ExceptionCode.FRIEND_SEARCH_OK, user.getName(), total, friendInfoList, lastPage);
    }

    @Override
    public FriendDto.FriendInfoListResponse getUserBlockFriendList(UserPrincipal userPrincipal, String keyword, int lastPage) {
        List<Friend> objects = fetchPagesOffsetUserBlockFriend(userPrincipal, keyword, lastPage);
        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : objects) {
            User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).get();
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);

            if (image.isPresent()) {
                friendInfoList.add(new FriendDto.FriendInfoList(user, friend.getRelationship())
                        .setFile(image.get().getImageUrl()));
            } else {
                friendInfoList.add(new FriendDto.FriendInfoList(user, friend.getRelationship()));
            }
        }

        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        Long total = friendRepository.countUsersByBlockFriendCriteria(userPrincipal.getId(), keyword, STATUS);
        return new FriendDto.FriendInfoListResponse(ExceptionCode.FRIEND_BLOCK_SEARCH_OK, user.getName(), total, friendInfoList, lastPage);
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
    public ResponseFriend requestFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
        Optional<Friend> findFriendUser
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS);
        Optional<Friend> findFriendMember
                = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), userPrincipal.getId(), STATUS);

        if (userPrincipal.getId().equals(dto.getMemberId())) {
            return new ResponseFriend(ExceptionCode.FRIEND_INVALID);
        }

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

            //System.out.println(friendUser.getFriendName());
            //System.out.println(friendUser.getUser().getName());

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
                        .title(friendUser.getUser().getName()+Alarm.AlarmType.FRIEND_REQUEST.getTitle())
                        .content(Alarm.AlarmType.FRIEND_REQUEST.getMessage())
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
                                .apnsEnv(friend.getApnsEnv())
                                .build())
                        .build();
                kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(friend.getUserId())), kakaoPushRequest);
            }

            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_OK);
        }

        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_NOT);
    }

    @Override
    public ResponseFriend acceptFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
        Friend findFriendUser = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        Friend findFriendMember = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(dto.getMemberId(), userPrincipal.getId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                });

        if (userPrincipal.getId().equals(dto.getMemberId())) {
            return new ResponseFriend(ExceptionCode.FRIEND_INVALID);
        }

        if (findFriendUser.getRelationship().equals(Friend.Relation.REQUESTED)) { // 수락
            // 차단 : 알림을 받지 못해서 자동으로 요청 불가
            findFriendUser.setRelationship(Friend.Relation.SUCCESS);
            findFriendMember.setRelationship(Friend.Relation.SUCCESS);

            Alarm alarm = Alarm.builder()
                    .sendUserId(findFriendUser.getUser().getUserId())
                    .receiveUserId(findFriendMember.getUser().getUserId())
                    .type(Alarm.AlarmType.FRIEND_ACCEPT)
                    .title(findFriendUser.getUser().getName()
                            +Alarm.AlarmType.FRIEND_ACCEPT.getTitle())
                    .content(Alarm.AlarmType.FRIEND_ACCEPT.getMessage())
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
                            .apnsEnv(findFriendMember.getUser().getApnsEnv())
                            .build())
                    .build();
            kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(findFriendMember.getUser().getUserId())), kakaoPushRequest);
            return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_ACCEPT_NOT);
    }

    @Override
    public ResponseFriend refuseFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
        Friend findFriend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        if (userPrincipal.getId().equals(dto.getMemberId())) {
            return new ResponseFriend(ExceptionCode.FRIEND_INVALID);
        }

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
    public ResponseFriend blockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
        Optional<Friend> findFriendUser = friendRepository
                .findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS);

        if (userPrincipal.getId().equals(dto.getMemberId())) {
            return new ResponseFriend(ExceptionCode.FRIEND_INVALID);
        }

        if (findFriendUser.isPresent()) { // 관계 존재 : request, requested, success
            Friend.Relation now = findFriendUser.get().getRelationship();
            if (now.equals(Friend.Relation.BLOCK)) {
                return new ResponseFriend(ExceptionCode.FRIEND_BLOCK_CANCEL_NOT);
            } else if (now.equals(Friend.Relation.SUCCESS)) {
                friendRepository.deleteByUserFriendIdAndUserUserId(findFriendUser.get().getId(), findFriendUser.get().getUserFriendId()); // 친구 관계 삭제
            }
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
    public ResponseFriend cancelBlockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {

        if (userPrincipal.getId().equals(dto.getMemberId())) {
            return new ResponseFriend(ExceptionCode.FRIEND_INVALID);
        }

        Friend friend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(userPrincipal.getId(), dto.getMemberId(), STATUS)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_FRIEND_NOT_FOUND);
                });

        friendRepository.delete(friend);
        return new ResponseFriend(ExceptionCode.FRIEND_BLOCK_CANCEL_OK);
    }

    @Override
    public ResponseFriend cancelRequest(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
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
            Optional<Alarm> o = alarmRepository.findTopBySendUserIdAndReceiveUserIdAndTypeOrderByIdDesc(
                    userPrincipal.getId(), dto.getMemberId(), Alarm.AlarmType.FRIEND_REQUEST);

            o.ifPresent(alarmRepository::delete);

            return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_CANCEL_OK);
        }
        return new ResponseFriend(ExceptionCode.FRIEND_REQUEST_CANCEL_NOT);
    }

    @Transactional
    @Override
    public ResponseFriend cancelFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto) {
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

            Optional<Alarm> o = alarmRepository.findTopBySendUserIdAndReceiveUserIdAndTypeOrderByIdDesc(
                    userPrincipal.getId(), dto.getMemberId(), Alarm.AlarmType.FRIEND_REQUEST);

            o.ifPresent(alarmRepository::delete);

            return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_OK);
        }

        return new ResponseFriend(ExceptionCode.FRIEND_CANCEL_NOT);
    }

    @Override
    public FriendDto.RoomFriendListResponse getRoomFriendList(Long roomId, UserPrincipal userPrincipal, String keyword, int lastPage) {
        User foundUser = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        RoomMember roomMember = roomMemberRepository.findByRoom_RoomIdAndUser_UserIdAndStatus(roomId, foundUser.getUserId(), 1).orElseThrow(() -> new CustomException(ExceptionCode.ROOMMEMBER_NOT_EXIST));

        // roomMember 인지
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomAndStatus(roomMember.getRoom(), STATUS);
        List<Friend> friends = fetchPagesOffsetUserFriend(foundUser, keyword, lastPage);

        List<FriendDto.FriendRoomInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friends) {
            User user = userRepository.findByUserIdAndStatus(friend.getUserFriendId(), STATUS).get();
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);

            if (roomMembers.stream().anyMatch(m-> Objects.equals(m.getUser().getUserId(), friend.getUserFriendId()))) {
                FriendDto.FriendRoomInfoList.Reason reason = FriendDto.FriendRoomInfoList.Reason.ALREADY;
                
                if (image.isPresent()) {
                    friendInfoList.add(new FriendDto.FriendRoomInfoList(user,0, reason).setFile(image.get().getImageUrl()));
                } else {
                    friendInfoList.add(new FriendDto.FriendRoomInfoList(user,0, reason));
                }
            } else {
                FriendDto.FriendRoomInfoList.Reason reason = user.getRoomLimit() < 30 ?
                        null : FriendDto.FriendRoomInfoList.Reason.OVERLIMIT;
                int status = reason == null ? 1 : 0;

                if (image.isPresent()) {
                    friendInfoList.add(new FriendDto.FriendRoomInfoList(user,status, reason).setFile(image.get().getImageUrl()));
                } else {
                    friendInfoList.add(new FriendDto.FriendRoomInfoList(user,status, reason));
                }
            }
        }

        Long total = friendRepository.countUsersByFriendCriteria(userPrincipal.getId(), keyword, STATUS);
        return new FriendDto.RoomFriendListResponse(ExceptionCode.ROOM_FRIEND_OK, friendInfoList, total, lastPage);
    }

    /*@Override
    public FriendDto.FriendInfoListResponse getFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto) {
        List<Friend> friendList = fetchPagesOffsetFriend(userPrincipal, dto);

        List<FriendDto.FriendInfoList> friendInfoList = new ArrayList<>();
        for (Friend friend : friendList) {
            Optional<Image> image = imageRepository.findByUserUserIdAndImageSortAndStatus(user.getUserId(), PROFILE_TYPE, STATUS);
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
        return new FriendDto.FriendInfoListResponse(ExceptionCode.FRIEND_LIST_SEARCH_OK, total, friendInfoList);
    }*/

    /*@Override
    public FriendDto.FriendInfoListResponse getBlockFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto) {
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
        return new FriendDto.FriendInfoListResponse(ExceptionCode.FRIEND_LIST_FIND_OK, total, friendInfoList);
    }*/

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
}