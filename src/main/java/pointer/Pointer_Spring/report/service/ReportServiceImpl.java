package pointer.Pointer_Spring.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.report.domain.BlockedUser;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.report.repository.BlockedUserRepository;
import pointer.Pointer_Spring.report.repository.ReportRepository;
import pointer.Pointer_Spring.report.repository.RestrictedUserRepository;
import pointer.Pointer_Spring.report.repository.UserReportRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.domain.VoteHistory;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final String REPORT_QUESTION = "해당 질문은 삭제되었습니다.";
    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final RoomRepository roomRepository;
    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final RestrictedUserRepository restrictedUserRepository;

    private final Integer STATUS = 1;

    @Override
    @Transactional
    public ReportDto.UserReportResponse saveUserReport(Long reportingUserId, ReportDto.UserReportRequest reportRequest) {
        User targetUser = userRepository.findById(reportRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        UserReport userReport = UserReport.builder()
                .targetUser(targetUser)
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .reportCode(reportRequest.getReasonCode())
                .build();

        //중복확인
        if (!userReportRepository.existsByTargetUserUserIdAndReportingUserIdAndStatus(targetUser.getUserId(), reportingUserId, STATUS)) {
            userReportRepository.save(userReport);
        }else{
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        return ReportDto.UserReportResponse.builder()
                .targetUserId(targetUser.getUserId())
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .reasonCode(reportRequest.getReasonCode())
                .build();
    }
    @Override
    @Transactional
    public ReportDto.ReportResponse saveReport(Long reportingUserId,ReportDto.ReportRequest reportRequest) {
        User targetUser = userRepository.findById(reportRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        Long dataId = reportRequest.getDataId();

        String data = reportRequest.getType() == Report.ReportType.HINT?
                voteRepository.findById(dataId).orElseThrow(()->new CustomException(ExceptionCode.HINT_NOT_FOUND)).getHint()
                : questionRepository.findById(dataId).orElseThrow(()->new CustomException(ExceptionCode.QUESTION_NOT_FOUND)).getQuestion();

        Room reportRoom = roomRepository.findById(reportRequest.getRoomId()).orElseThrow(()->new CustomException(ExceptionCode.ROOM_NOT_FOUND));

        //중복확인
        if(reportRequest.getType()== Report.ReportType.HINT &&
                reportRepository.existsByReportingUserIdAndTargetUserUserIdAndRoomRoomIdAndAndTypeAndDataId(reportingUserId, targetUser.getUserId(), reportRoom.getRoomId(), Report.ReportType.HINT, dataId)){
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        } else if ((reportRequest.getType()== Report.ReportType.QUESTION &&
                reportRepository.existsByReportingUserIdAndTargetUserUserIdAndRoomRoomIdAndAndTypeAndDataId(reportingUserId, targetUser.getUserId(), reportRoom.getRoomId(), Report.ReportType.QUESTION, dataId))){
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        Report report = Report.builder()
                .targetUser(targetUser)
                .reportCode(reportRequest.getReasonCode())
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .room(reportRoom)
                .type(reportRequest.getType())
                .dataId(dataId)
                .build();
        reportRepository.save(report);

        ReportDto.ReportResponse reportResponse = ReportDto.ReportResponse.builder()
                .targetUserId(targetUser.getUserId())
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .type(reportRequest.getType())
                .data(data)
                .reasonCode(reportRequest.getReasonCode())
                .roomId(reportRoom.getRoomId())
                .build();
        return reportResponse;
    }

    /**
     * 관리자 모드로 관리
     */

    //콘텐츠 삭제
    @Override
    @Transactional
    public void deleteContents(Long reportId){
        Report report = reportRepository.findById(reportId).orElseThrow(
                ()->new CustomException(ExceptionCode.REPORT_NOT_FOUND)
        );
        Long dataId = report.getDataId();
        if(report.getType() == Report.ReportType.QUESTION){
            Question question = questionRepository.findById(dataId).orElseThrow(()->new CustomException(ExceptionCode.QUESTION_NOT_FOUND));
            question.modify(REPORT_QUESTION);

        } else if (report.getType() == Report.ReportType.HINT) {
            VoteHistory voteHistory =  voteRepository.findById(dataId).orElseThrow(()-> new CustomException(ExceptionCode.HINT_NOT_FOUND));
            voteHistory.updateHint(null);
        }

    }
    //영구적인 제한
    @Override
    @Transactional
    public void permanentRestrictionByUserReport(Long userReportId){
        UserReport userReport = userReportRepository.findById(userReportId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND)
        );
        User user = userRepository.findById(userReport.getTargetUser().getUserId()).get();
        if(checkDuplicatedPermanentRestriction(user.getEmail())){
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }
        blockedUserRepository.save(new BlockedUser(user.getEmail(), user.getId()));

    }
    @Override
    @Transactional
    public void permanentRestrictionByOtherReport(Long reportId){
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND)
        );
        User user = userRepository.findById(report.getTargetUser().getUserId()).get();
        if(checkDuplicatedPermanentRestriction(user.getEmail())){
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }
        blockedUserRepository.save(new BlockedUser(user.getEmail(), user.getId()));
        user.setStatus(0);
    }
    private boolean checkDuplicatedPermanentRestriction(String email){
        return blockedUserRepository.existsByEmail(email);
    }

    //일시적인 기능 제한
    @Override
    @Transactional
    public void temporalRestriction(Long reportId){
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND)
        );
        Long targetUserId = report.getTargetUser().getUserId();
        Report.ReportType reportType = report.getType();
        Long roomId = report.getRoom().getRoomId();
        if(checkDuplicatedTemporalRestriction(targetUserId, roomId, reportType)){
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        restrictedUserRepository.save(new RestrictedUser(report, targetUserId, reportType, roomId));
        if(reportType== Report.ReportType.QUESTION) {
            report.getTargetUser().updateIsQuestionRestricted(true);
        }else if(reportType== Report.ReportType.HINT){
            report.getTargetUser().updateIsHintRestricted(true);
        }
    }

    private boolean checkDuplicatedTemporalRestriction(Long targetUserId, Long roomId, Report.ReportType reportType){
        return restrictedUserRepository.existsByTargetUserIdAndRoomIdAndReportTypeAndStatus(targetUserId, roomId, reportType, STATUS);
    }

    // 관리자 조회 모드
    @Override
    public List<ReportDto.UserReportResponse> getUserReports(Long userId){//신고한 사람이 신고한 목록
        return userReportRepository.findAllByReportingUserIdAndStatus(userId, STATUS).stream()
                .map(ReportDto.UserReportResponse::new).collect(Collectors.toList());
    }
    @Override
    public ReportDto.UserReportResponse  getUserReport(Long userId, Long targetUserId){//일단 targetId로 받는데 targetUser의 고유 id등으로 논의 필요(프론트는 targetUserId알 수 있는지 확인)
        UserReport userReport = userReportRepository.findByTargetUserUserIdAndReportingUserIdAndStatus(targetUserId, userId, STATUS);
        return ReportDto.UserReportResponse.builder()
                .reportingUserId(userReport.getReportingUserId())
                .reasonCode(userReport.getReportCode())
                .targetUserId(userReport.getTargetUser().getUserId())
                .reason(userReport.getReason())
                .build();

    }
    @Override
    public List<ReportDto.UserReportResponse> getUserReportsByTarget(Long targetUserId){//일단 targetId로 받는데 targetUser의 고유 id등으로 논의 필요(프론트는 targetUserId알 수 있는지 확인)
        return userReportRepository.findAllByTargetUserUserIdAndStatus(targetUserId, STATUS).stream()
                .map(ReportDto.UserReportResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<ReportDto.ReportResponse> getReports(Long userId){//신고한 사람이 신고한 목록

        return reportRepository.findAllByReportingUserId(userId).stream()
                .map(report -> {
                    String data = report.getType() == Report.ReportType.HINT
                            ? voteRepository.findById(report.getDataId()).get().getHint()
                            : questionRepository.findById(report.getDataId()).get().getQuestion();
                    return new ReportDto.ReportResponse(report, data);
                }).collect(Collectors.toList());
    }
//    @Override
//    public Report getReport(Long userId, Long targetUserId){
//        return reportRepository.findByTargetUserUserIdAndReportingUserId(targetUserId, userId);
//    }
    @Override
    public List<ReportDto.ReportResponse> getReportsByTarget(Long targetUserId){//신고한 사람이 신고한 목록
        return reportRepository.findAllByTargetUserUserId(targetUserId).stream().map(report -> {
            String data = report.getType() == Report.ReportType.HINT
                    ? voteRepository.findById(report.getDataId()).get().getHint()
                    : questionRepository.findById(report.getDataId()).get().getQuestion();
            return new ReportDto.ReportResponse(report, data);
        }).collect(Collectors.toList());
    }

    @Override
    public ReportDto.UserReportResponse getUserReportByUserReportId(Long userReportId){
        UserReport userReport = userReportRepository.findById(userReportId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND)
        );
        return new ReportDto.UserReportResponse(userReport);
    }

    @Override
    public ReportDto.ReportResponse getReportByReportId(Long reportId){
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND));
        String data = report.getType() == Report.ReportType.HINT
                ? voteRepository.findById(report.getDataId()).get().getHint()
                : questionRepository.findById(report.getDataId()).get().getQuestion();
        return new ReportDto.ReportResponse(report, data);
    }

    @Override
    public ReportDto.BlockedUserResponse getBlockedUserReportByBlockedUserId(Long blockedUserId){
        BlockedUser blockedUser =  blockedUserRepository.findById(blockedUserId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND));
        return new ReportDto.BlockedUserResponse(blockedUser);
    }
    public ReportDto.RestrictedUserResponse getReportByRestrictedUserRestrictUserId(Long restrictedUserId){
        RestrictedUser restrictedUser = restrictedUserRepository.findById(restrictedUserId).orElseThrow(
                () -> new CustomException(ExceptionCode.REPORT_NOT_FOUND));
        return new ReportDto.RestrictedUserResponse(restrictedUser);
    }


}
