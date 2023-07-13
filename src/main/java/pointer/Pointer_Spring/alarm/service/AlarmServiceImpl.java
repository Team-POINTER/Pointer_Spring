package pointer.Pointer_Spring.alarm.service;

import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.alarm.domain.ActiveAlarm;
import pointer.Pointer_Spring.alarm.domain.Alarm;
import pointer.Pointer_Spring.alarm.repository.ActiveAlarmRepository;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final ActiveAlarmRepository activeAlarmRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;

    public AlarmServiceImpl(AlarmRepository alarmRepository, ActiveAlarmRepository activeAlarmRepository, UserRepository userRepository, QuestionRepository questionRepository, RoomMemberRepository roomMemberRepository, VoteRepository voteRepository) {
        this.alarmRepository = alarmRepository;
        this.activeAlarmRepository = activeAlarmRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
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
                        .type(Alarm.AlarmType.ACTIVE)
                        .responseUserId(member.getUserId())
                        .build();

                alarmRepository.save(alarm);

                ActiveAlarm activeAlarm = ActiveAlarm.builder()
                        .requestUserId(user.getUserId())
                        .responseUserId(member.getUserId())
                        .type(ActiveAlarm.ActiveAlarmType.POKE)
                        .content(ActiveAlarm.ActiveAlarmType.POKE.getMessage())
                        .build();

                activeAlarmRepository.save(activeAlarm);
            }
        }
    }
}
