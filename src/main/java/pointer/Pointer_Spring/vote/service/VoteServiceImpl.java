package pointer.Pointer_Spring.vote.service;

import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;
import pointer.Pointer_Spring.report.repository.RestrictedUserRepository;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.domain.VoteHistory;
import pointer.Pointer_Spring.vote.dto.VoteDto;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RestrictedUserRepository restrictedUserRepository;

    public VoteServiceImpl(VoteRepository voteRepository, QuestionRepository questionRepository, UserRepository userRepository, RoomRepository roomRepository, RoomMemberRepository roomMemberRepository, RestrictedUserRepository restrictedUserRepository) {
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.restrictedUserRepository = restrictedUserRepository;
    }

    @Transactional
    @Override
    public List<VoteDto.CreateResponse> createVote(VoteDto.CreateRequest dto) {
        // 질문 validation
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
                });

        // 유저 validation check
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        //신고 당한 유저인지
        if(user.isHintRestricted()){
            RestrictedUser restrictedUser =  restrictedUserRepository.findByReportTargetUserUserIdAndReportRoomRoomIdAndReportType(user.getUserId(), question.getRoom().getRoomId(), Report.ReportType.HINT);
            Long questionId = question.getId();

            if(restrictedUser.getCurrentQuestionId() != questionId) {
                restrictedUser.updateTemporalNum(restrictedUser.getTemporalNum() - 1);
                restrictedUser.updateCurrentQuestionId(questionId);
            }//지금 신고된 상황에서 질문의 다음 질문부터 패널티 적용됨

            if(restrictedUser.getTemporalNum() == 0){
                user.updateIsHintRestricted(false);
            }
            throw new CustomException(ExceptionCode.REPORTED_USER);//한 턴 동안만 막아야해
        }//생성이 안되니까 신고 당한 유저가 있을 시 룸 멤버 수보다 투표 가능 인원 수 가 더 적게 나옴

        // 투표 생성
        List<VoteDto.CreateResponse> response = new ArrayList<>();
        for(Long userId : dto.getVotedUserIds()) {
            // 유저 validation check
            User voteUser = userRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                    });

            VoteHistory vote = VoteHistory.builder()
                    .questionId(dto.getQuestionId())
                    .memberId(dto.getUserId())
                    .candidateId(userId)
                    .candidateName(voteUser.getName())
                    .hint(dto.getHint())
                    .build();

            voteRepository.save(vote);
            response.add(VoteDto.CreateResponse.builder()
                    .id(vote.getVoteHistoryId())
                    .userId(user.getUserId())
                    .questionId(question.getId())
                    .votedUserId(voteUser.getUserId())
                    .hint(vote.getHint())
                    .build());
        }

        return response;
    }

    @Override
    public VoteDto.GetResponse getQuestionVoteCnt(Long userId, Long questionId) {
        // 유저 validation check
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        // 해당 유저 정보 조회
        int allVoteCnt = voteRepository.countByQuestionId(question.getId());
        int targetVotedCnt = voteRepository.countByQuestionIdAndCandidateId(question.getId(), user.getUserId());
        VoteDto.GetMemberResponse targetUser = VoteDto.GetMemberResponse.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .allVoteCnt(allVoteCnt)
                .votedMemberCnt(targetVotedCnt)
                .build();

        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(question.getRoom());

        // 룸 유저들 정보 조회
        List<VoteDto.GetMemberResponse> memberResponses = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            int votedCnt = voteRepository.countByQuestionIdAndCandidateId(question.getId(), member.getUserId());
            memberResponses.add(VoteDto.GetMemberResponse.builder()
                    .userId(member.getUserId())
                    .userName(member.getName())
                    .votedMemberCnt(votedCnt)
                    .allVoteCnt(allVoteCnt)
                    .build());
        }

        // 투표안한 유저 개수
        int votedUserCnt = voteRepository.countDistinctUserByQuestion(question.getId());
        int notVotedCnt = roomMembers.size() - votedUserCnt;

        System.out.println(roomMembers.size());
        System.out.println(votedUserCnt);

        return VoteDto.GetResponse.builder()
                .roomName(question.getRoom().getName())
                .members(memberResponses)
                .targetUser(targetUser)
                .notNotedMemberCnt(notVotedCnt)
                .build();

    }


    @Override
    public List<VoteDto.GetNotVotedMember> getNotVotedMember(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });
        // 쿼리문 변경 예정
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(question.getRoom());
        List<VoteDto.GetNotVotedMember> notVotedMembers = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            boolean vote = voteRepository.existsByMemberId(member.getUserId());
            if (!vote) {
                notVotedMembers.add(VoteDto.GetNotVotedMember.builder()
                        .userId(member.getUserId())
                        .userName(member.getName())
                        .build());
            }
        }

        return notVotedMembers;
    }

    @Override
    public VoteDto.GetHintResponse getHintResponse(Long userId, Long questionId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

        List<VoteHistory> voteHistories = voteRepository.findAllByQuestionIdAndCandidateId(question.getId(), user.getUserId());//해당 user를 투표한 인원
        int allVoteCnt = voteRepository.countByQuestionId(question.getId());//해당 질문에 대해 투표한 사람의 수
        List<String> hints = new ArrayList<>();
        for(VoteHistory vote : voteHistories) {
            hints.add(vote.getHint());
        }

        return VoteDto.GetHintResponse.builder()
                .targetVotedCnt(voteHistories.size())
                .allVoteCnt(allVoteCnt)
                .hint(hints)
                .createdAt(question.getCreatedAt().format(formatter))
                .build();
    }
}
