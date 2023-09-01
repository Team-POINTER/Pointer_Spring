package pointer.Pointer_Spring.question.service;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.alarm.domain.Alarm;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.alarm.service.KakaoPushNotiService;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;
import pointer.Pointer_Spring.report.enumeration.ReportType;
import pointer.Pointer_Spring.report.repository.RestrictedUserRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;
    private final AlarmRepository alarmRepository;
    private final KakaoPushNotiService kakaoPushNotiService;
    private final RestrictedUserRepository restrictedUserRepository;

    private final Integer STATUS = 1;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            RoomMemberRepository roomMemberRepository,
            VoteRepository voteRepository,
            AlarmRepository alarmRepository, KakaoPushNotiService kakaoPushNotiService,
            RestrictedUserRepository restrictedUserRepository) {

        this.questionRepository = questionRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
        this.alarmRepository = alarmRepository;
        this.kakaoPushNotiService = kakaoPushNotiService;
        this.restrictedUserRepository = restrictedUserRepository;
    }


    @Override
    @Transactional
    public QuestionDto.CreateResponse createQuestion(UserPrincipal userPrincipal, QuestionDto.CreateRequest request) {

        // 방 조회
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
                });

        // 유저 조회
        User user = userRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        //신고 당한 유저인지
        if(user.isQuestionRestricted()){ //질문 생성마다 새로운 투표를 해야함 -> 따라서 신고 처리 확인 로직 존재
            throw new CustomException(ExceptionCode.REPORTED_USER);//
        }

        Question question;
        try {
            synchronized (room) {
                // 질문 생성 가능한지 확인
                if (!validQuestionTime(room.getRoomId())) {
                    throw new CustomException(ExceptionCode.QUESTION_CREATED_FAILED);
                }

                question = Question.builder()
                        .room(room)
                        .creatorId(user.getUserId())
                        .question(request.getContent())
                        .build();

                questionRepository.save(question);
                question.getRoom().setUpdatedAt(question.getUpdatedAt());
                room.addQuestion(question);
            }
        }catch(CannotAcquireLockException e){
            throw new CustomException(ExceptionCode.QUESTION_GET_LOCK_FAIL);
        }

        // 알림
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomAndStatus(room, STATUS);
        for(RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            if(!member.isActiveAlarmFlag()) continue;

            Alarm alarm = Alarm.builder()
                    .sendUserId(user.getUserId())
                    .receiveUserId(member.getUserId())
                    .needId(question.getRoom().getRoomId())
                    .type(Alarm.AlarmType.QUESTION)
                    .title(room.getName()+ Alarm.AlarmType.QUESTION.getTitle())
                    .content(Alarm.AlarmType.QUESTION.getMessage())
                    .build();

            alarmRepository.save(alarm);

            AlarmDto.KakaoPushRequest kakaoPushRequest = AlarmDto.KakaoPushRequest.builder()
                    .forApns(AlarmDto.PushType.builder()
                            .message(alarm.getContent())
                            .apnsEnv(member.getApnsEnv())
                            .build())
                    .build();
            kakaoPushNotiService.sendKakaoPush(List.of(String.valueOf(member.getUserId())), kakaoPushRequest);
        }

        handlingReportRoomMembers(roomMemberRepository.findAllByRoomAndUserIsQuestionRestrictedAndStatusEquals(room, true, STATUS),
                roomMemberRepository.findAllByRoomAndUserIsHintRestrictedAndStatusEquals(room, true, STATUS));

        return QuestionDto.CreateResponse.builder()
                .questionId(question.getId())
                .content(question.getQuestion())
                .build();
    }
    private void handlingReportRoomMembers(List<RoomMember> questionRestrictedRoomMembers, List<RoomMember> hintRestrictedRoomMembers){
        for(RoomMember roomMember : questionRestrictedRoomMembers){
            RestrictedUser restrictedUser = restrictedUserRepository.findByReportTargetUserUserIdAndReportRoomRoomIdAndReportTypeAndStatus(roomMember.getUser().getUserId(), roomMember.getRoom().getRoomId(), ReportType.QUESTION, STATUS);

            restrictedUser.updateTemporalNum(restrictedUser.getTemporalNum() - 1);
            if (restrictedUser.getTemporalNum() == 0) {
                roomMember.getUser().updateIsQuestionRestricted(false);
                restrictedUser.setStatus(0);
            }
        }
        for(RoomMember roomMember : hintRestrictedRoomMembers){//힌트는 현재 질문에서 투표를 했든 안했든 다음 부터 적용되는데 만약 지금 질문에 투표를 안했다면 지금 질문을 제외하고도 3번 더 투표흫 못 함
            RestrictedUser restrictedUser =  restrictedUserRepository.findByReportTargetUserUserIdAndReportRoomRoomIdAndReportTypeAndStatus(roomMember.getUser().getUserId(), roomMember.getRoom().getRoomId(), ReportType.HINT, STATUS);
            if(restrictedUser.getTemporalNum() == 0){
                roomMember.getUser().updateIsHintRestricted(false);
                restrictedUser.setStatus(0);
            }

            restrictedUser.updateTemporalNum(restrictedUser.getTemporalNum() - 1);
        }
    }

    @Override
    public boolean validQuestionTime(Long roomId) {
        Question prevQuestion = questionRepository.findTopByRoomRoomIdAndStatusOrderByIdDesc(roomId, STATUS).orElse(null);

        if(prevQuestion != null) {
            LocalDateTime now = LocalDateTime.now();

            if(now.isBefore(prevQuestion.getCreatedAt().plusDays(1))) {
                int roomMemberCount = roomMemberRepository.countByRoomAndStatus(prevQuestion.getRoom(), STATUS);
                int voteCount = voteRepository.countDistinctUserByQuestionAndStatus(prevQuestion.getId(), STATUS);

                if(roomMemberCount == voteCount) {
                    prevQuestion.setCreatedAt(now);
                    questionRepository.save(prevQuestion);
                    return true;
                }
                return false;
            }
            return true;
        }
        throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
    }

    @Override
    public QuestionDto.GetCurrentResponse getCurrentQuestion(UserPrincipal userPrincipal, Long roomId) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
        });

        Question currentQuestion = questionRepository.findTopByCreatedAtAfterAndRoomRoomIdAndStatus(now, roomId, STATUS).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.CURRENT_QUESTION_NOT_FOUND);
        });

        boolean isVoted = voteRepository.existsByMemberIdAndQuestionIdAndStatus(userPrincipal.getId(), currentQuestion.getId(), STATUS);

        List<QuestionDto.GetMemberResponse> roomMembers = roomMemberRepository.findAllByRoomAndStatus(room, STATUS).stream()
                .map((m) -> QuestionDto.GetMemberResponse.builder()
                        .userId(m.getUser().getUserId())
                        .nickname(m.getUser().getName())
                        .build())
                .toList();


        return QuestionDto.GetCurrentResponse.builder()
                .roomName(room.getName())
                .questionId(currentQuestion.getId())
                .content(currentQuestion.getQuestion())
                .isVoted(isVoted)
                .members(roomMembers)
                .build();
    }

    @Override
    public List<QuestionDto.GetResponse> getQuestions(UserPrincipal userPrincipal, Long roomId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
        });

        List<Question> questions = questionRepository.findAllByRoomAndStatusOrderByCreatedAtDesc(room, STATUS);
        List<QuestionDto.GetResponse> response = new ArrayList<>();
        for(Question question : questions) {
            //int roomMemberCount = roomMemberRepository.countByRoom(room);
            //int voteCount = voteRepository.countDistinctUsersByQuestion(question);
            int allVoteCount = voteRepository.countByQuestionIdAndStatus(question.getId(), STATUS);
            int voteCount = voteRepository.countByCandidateIdAndQuestionIdAndStatus(user.getUserId(), question.getId(), STATUS);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

            response.add(QuestionDto.GetResponse.builder()
                    .questionId(question.getId())
                    .question(question.getQuestion())
                    .votedMemberCnt(voteCount)
                    .allVoteCnt(allVoteCount)
                    .createdAt(question.getCreatedAt().format(formatter))
                    .build());
        }

        return response;
    }

    @Override
    @Transactional
    public void modifyQuestion(UserPrincipal userPrincipal, Long questionId, QuestionDto.ModifyRequest request) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        boolean checkRoomMember = roomMemberRepository
                .existsByUserUserIdAndRoomRoomIdAndStatus(userPrincipal.getId(), question.getRoom().getRoomId(), STATUS);

        if(!checkRoomMember)
            throw new CustomException(ExceptionCode.QUESTION_DELETE_NOT_AUTHENTICATED);

        question.modify(request.getContent());
        question.getRoom().setUpdatedAt(question.getUpdatedAt());
    }

    @Override
    @Transactional
    public void deleteQuestion(UserPrincipal userPrincipal, Long questionId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        boolean checkRoomMember = roomMemberRepository
                .existsByUserUserIdAndRoomRoomIdAndStatus(user.getUserId(), question.getRoom().getRoomId(), STATUS);

        if(!checkRoomMember)
            throw new CustomException(ExceptionCode.QUESTION_DELETE_NOT_AUTHENTICATED);

        questionRepository.delete(question);
    }
}
