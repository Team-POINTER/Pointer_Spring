package pointer.Pointer_Spring.alarm.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.alarm.domain.Alarm;
import pointer.Pointer_Spring.alarm.domain.ChatAlarm;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.alarm.repository.ChatAlarmRepository;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.ImageRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
//    private final ActiveAlarmRepository activeAlarmRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;
    private final ImageRepository imageRepository;
    private final FriendRepository friendRepository;
    private final ChatAlarmRepository chatAlarmRepository;
    private final KakaoPushNotiService kakaoPushNotiService;

    private static final int PAGE_SIZE = 30;
    private static final int STATUS = 1;

    public AlarmServiceImpl(
            AlarmRepository alarmRepository,
            UserRepository userRepository,
            QuestionRepository questionRepository,
            RoomMemberRepository roomMemberRepository,
            VoteRepository voteRepository,
            ImageRepository imageRepository,
            FriendRepository friendRepository, ChatAlarmRepository chatAlarmRepository,
            KakaoPushNotiService kakaoPushNotiService) {
        this.alarmRepository = alarmRepository;
        //this.activeAlarmRepository = activeAlarmRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
        this.imageRepository = imageRepository;
        this.friendRepository = friendRepository;
        this.chatAlarmRepository = chatAlarmRepository;
        this.kakaoPushNotiService = kakaoPushNotiService;
    }

    @Override
    public void poke(UserPrincipal userPrincipal, Long questionId) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomAndStatus(question.getRoom(), STATUS);
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            if (!member.isActiveAlarmFlag()) continue;
            boolean vote = voteRepository.existsByMemberIdAndStatus(member.getUserId(), STATUS);
            if (!vote) {
                Alarm alarm = Alarm.builder()
                        .type(Alarm.AlarmType.POKE)
                        .sendUserId(user.getUserId())
                        .receiveUserId(member.getUserId())
                        .needId(question.getRoom().getRoomId())
                        .title(user.getName()+Alarm.AlarmType.POKE.getTitle())
                        .content(Alarm.AlarmType.POKE.getMessage())
                        .build();

                alarmRepository.save(alarm);

                AlarmDto.KakaoPushRequest kakaoPushRequest = AlarmDto.KakaoPushRequest.builder()
                        .forApns(AlarmDto.PushType.builder()
                                .message(alarm.getContent())
                                .apnsEnv(member.getApnsEnv())
                                .build())
                        .build();
                kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(member.getUserId())), kakaoPushRequest);

//                ActiveAlarm activeAlarm = ActiveAlarm.builder()
//                        //.requestUserId(user.getUserId())
//                        //.responseUserId(member.getUserId())
//                        .build();
//
//                activeAlarmRepository.save(activeAlarm);
            }
        }
    }

    @Transactional
    @Override
    public void activeAllAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setActiveAlarmFlag(request.isActive());
        user.setChatAlarmFlag(request.isActive());
        user.setEventAlarmFlag(request.isActive());
        user.setAllAlarmFlag(request.isActive());

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activeAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setActiveAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activeChatAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setChatAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activeEventAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setEventAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Override
    public AlarmDto.GetAlarmActiveResponse getActiveAlarm(UserPrincipal userPrincipal) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        return AlarmDto.GetAlarmActiveResponse.builder()
                //.allAlarm(user.isAllAlarmFlag())
                .activeAlarm(user.isActiveAlarmFlag())
                .chatAlarm(user.isChatAlarmFlag())
                .eventAlarm(user.isEventAlarmFlag())
                .build();
    }

    @Transactional
    @Override
    public AlarmDto.GetAlarmResponses getAlarms(UserPrincipal userPrincipal, Long cursorId) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        if(cursorId == null || cursorId == 0) {
            cursorId = Long.MAX_VALUE;
        }

        // 안읽은 알람 있는지 체크
        boolean newAlarm = alarmRepository.existsByReceiveUserIdAndReadCheck(userPrincipal.getId(), false);
        List<ChatAlarm> newFriendAlarm = chatAlarmRepository.findAllBySendUserIdAndReadCheckAndStatus(userPrincipal.getId(), false, STATUS);

        // 30개씩 페이징
        PageRequest pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("id").descending());
        List<Alarm> alarms = alarmRepository.findAllByReceiveUserIdAndTypeNotAndIdLessThanOrderByIdDesc(userPrincipal.getId(), Alarm.AlarmType.FRIEND_REQUEST, cursorId, pageable);

        List<AlarmDto.GetAlarmResponse> alarmResponses = new ArrayList<>();
        for(Alarm alarm : alarms) {
            User requestUser = userRepository.findByUserId(alarm.getSendUserId()).orElse(null);
            Image profileImg = imageRepository.findByUserAndImageSort(requestUser, Image.ImageType.PROFILE).orElse(null);
            // 알림 읽음 표시
            alarm.setReadCheck(true);

            AlarmDto.GetAlarmResponse response = AlarmDto.GetAlarmResponse.builder()
                    .alarmId(alarm.getId())
                    .sendUserId(alarm.getSendUserId())
                    .sendUserName(requestUser!=null?requestUser.getName():null)
                    .sendUserProfile(profileImg!=null?profileImg.getImageUrl():null)
                    .content(alarm.getContent())
                    .type(alarm.getType().name())
                    .createdAt(alarm.getCreatedAt().toString())
                    .build();

            alarmResponses.add(response);
        }

        return AlarmDto.GetAlarmResponses.builder()
                .newAlarm(newAlarm)
                .newFriendAlarm(newFriendAlarm.size() > 0)
                .newFriendAlarmCnt(newFriendAlarm.size())
                .alarmList(alarmResponses)
                .build();
    }

    @Transactional
    @Override
    public void eventAlarm(AlarmDto.EventAlarmRequest request) {
        List<User> users = userRepository.findAllByEventAlarmFlag(true);

        for(User user : users) {
            Alarm alarm = Alarm.builder()
                    .type(Alarm.AlarmType.EVENT)
                    .receiveUserId(user.getUserId())
                    .content(request.getContent())
                    .build();

            alarmRepository.save(alarm);

            AlarmDto.KakaoPushRequest kakaoPushRequest = AlarmDto.KakaoPushRequest.builder()
                    .forApns(AlarmDto.PushType.builder()
                            .message(alarm.getContent())
                            .apnsEnv(user.getApnsEnv())
                            .build())
                    .build();
            kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(user.getUserId())), kakaoPushRequest);
        }
    }

    @Transactional
    @Override
    public List<AlarmDto.GetFriendAlarmResponse> getFriendAlarm(UserPrincipal userPrincipal, Long cursorId) {
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        if(cursorId == null || cursorId == 0) {
            cursorId = Long.MAX_VALUE;
        }

        PageRequest pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("id").descending());
        List<Alarm> alarms = alarmRepository.findAllByReceiveUserIdAndTypeAndIdLessThanOrderByIdDesc(
                user.getUserId(), Alarm.AlarmType.FRIEND_REQUEST, cursorId, pageable);
        List<AlarmDto.GetFriendAlarmResponse> responses = new ArrayList<>();
        for(Alarm alarm :alarms) {
            Friend friend = friendRepository.findByUserUserIdAndUserFriendIdAndStatus(
                    alarm.getSendUserId(), alarm.getReceiveUserId(), 1)
                    .orElseThrow(() -> {
                        throw new CustomException(ExceptionCode.FRIEND_INVALID);
                    });

            if(friend.getRelationship()== Friend.Relation.BLOCK
                    || friend.getRelationship() == Friend.Relation.REFUSE) {
                continue;
            }

            Optional<User> o = userRepository.findByUserId(alarm.getSendUserId());
            if(o.isEmpty()) {
                continue;
            }

            User sendUser = o.get();
            Image profileImg = imageRepository.findByUserAndImageSort(sendUser, Image.ImageType.PROFILE).orElse(null);
            AlarmDto.GetFriendAlarmResponse response = AlarmDto.GetFriendAlarmResponse.builder()
                    .alarmId(alarm.getId())
                    .userId(sendUser.getUserId())
                    .sendUserId(sendUser.getId())
                    .sendUserName(sendUser.getName())
                    .sendUserProfile(profileImg!=null?profileImg.getImageUrl():null)
                    .friendStatus(friend.getRelationship().name())
                    .type(alarm.getType().name())
                    .build();
            responses.add(response);

            alarm.setReadCheck(true);
        }

        return responses;
    }
}
