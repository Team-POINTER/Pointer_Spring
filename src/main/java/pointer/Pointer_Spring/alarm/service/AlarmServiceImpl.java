package pointer.Pointer_Spring.alarm.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.alarm.domain.Alarm;
import pointer.Pointer_Spring.alarm.domain.ChatAlarm;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.alarm.repository.ChatAlarmRepository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
//    private final ActiveAlarmRepository activeAlarmRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;
    private final ChatAlarmRepository chatAlarmRepository;

    private static final int PAGE_SIZE = 30;

    public AlarmServiceImpl(AlarmRepository alarmRepository, UserRepository userRepository, QuestionRepository questionRepository, RoomMemberRepository roomMemberRepository, VoteRepository voteRepository, ChatAlarmRepository chatAlarmRepository) {
        this.alarmRepository = alarmRepository;
        //this.activeAlarmRepository = activeAlarmRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
        this.chatAlarmRepository = chatAlarmRepository;
    }

    @Override
    public void poke(Long userId, Long questionId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(question.getRoom());
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            if (!member.isActiveAlarmFlag()) continue;
            boolean vote = voteRepository.existsByMemberId(member.getUserId());
            if (!vote) {
                Alarm alarm = Alarm.builder()
                        .type(Alarm.AlarmType.POKE)
                        .sendUserId(user.getUserId())
                        .receiveUserId(member.getUserId())
                        .content(Alarm.AlarmType.POKE.getMessage())
                        .build();

                alarmRepository.save(alarm);

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
    public void activeAllAlarm(Long userId, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userId)
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
    public void activeAlarm(Long userId, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setActiveAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activeChatAlarm(Long userId, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setChatAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void activeEventAlarm(Long userId, AlarmDto.AlarmActiveRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        user.setEventAlarmFlag(request.isActive());
        userRepository.save(user);
    }

    @Override
    public AlarmDto.GetAlarmActiveResponse getActiveAlarm(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        return AlarmDto.GetAlarmActiveResponse.builder()
                .allAlarm(user.isAllAlarmFlag())
                .activeAlarm(user.isActiveAlarmFlag())
                .chatAlarm(user.isChatAlarmFlag())
                .eventAlarm(user.isEventAlarmFlag())
                .build();
    }

    @Transactional
    @Override
    public AlarmDto.GetAlarmResponses getAlarms(Long userId, Long cursorId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        if(cursorId == null || cursorId == 0) {
            cursorId = Long.MAX_VALUE;
        }

        // 안읽은 알람 있는지 체크
        boolean newAlarm = alarmRepository.existsByReceiveUserIdAndReadCheck(userId, false);
        List<ChatAlarm> newFriendAlarm = chatAlarmRepository.findAllBySendUserIdAndReadCheck(userId, false);

        // 30개씩 페이징
        PageRequest pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("id").descending());
        List<Alarm> alarms = alarmRepository.findAllByReceiveUserIdAndIdLessThanOrderByIdDesc(userId, cursorId, pageable);

        List<AlarmDto.GetAlarmResponse> alarmResponses = new ArrayList<>();
        for(Alarm alarm : alarms) {
            User requestUser = userRepository.findByUserId(alarm.getSendUserId()).orElse(null);

            // 알림 읽음 표시
            alarm.setReadCheck(true);

            AlarmDto.GetAlarmResponse response = AlarmDto.GetAlarmResponse.builder()
                    .alarmId(alarm.getId())
                    .sendUserId(alarm.getSendUserId())
                    .sendUserName(requestUser!=null?requestUser.getName():null)
                    //.requestUserProfile(requestUser.getProfile())
                    .content(alarm.getContent())
                    .type(alarm.getType().name())
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
        }
    }
}
