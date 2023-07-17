package pointer.Pointer_Spring.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.report.repository.ReportRepository;
import pointer.Pointer_Spring.report.repository.UserReportRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final RoomRepository roomRepository;
    @Override
    @Transactional
    public ReportDto.UserReportResponse saveUserReport(ReportDto.UserReportRequest reportRequest) {
        User targetUser = userRepository.findById(reportRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        Long reportingUserId = reportRequest.getReportingUserId();
        UserReport userReport = UserReport.builder()
                .targetUser(targetUser)
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .reportCode(reportRequest.getReasonCode())
                .build();

        //중복확인
        if (!userReportRepository.existsByTargetUserUserIdAndReportingUserId(targetUser.getUserId(), reportingUserId)){
            userReportRepository.save(userReport);
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
    public ReportDto.ReportResponse saveReport(ReportDto.ReportRequest reportRequest) {
        User targetUser = userRepository.findById(reportRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        Long reportingUserId = reportRequest.getReportingUserId();

        Room reportRoom = roomRepository.findById(reportRequest.getRoomId()).orElseThrow(()->new CustomException(ExceptionCode.ROOM_NOT_FOUND));
        Report report = Report.builder()
                .targetUser(targetUser)
                .reportCode(reportRequest.getReasonCode())
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .room(reportRoom)
                .type(reportRequest.getType())
                .data(reportRequest.getData())
                .build();

        //중복확인
        if (!reportRepository.existsByTargetUserUserIdAndReportingUserId(targetUser.getUserId(), reportingUserId)){
            reportRepository.save(report);
        }

        ReportDto.ReportResponse reportResponse = ReportDto.ReportResponse.builder()
                .targetUserId(targetUser.getUserId())
                .reportingUserId(reportingUserId)
                .reason(reportRequest.getReason())
                .type(reportRequest.getType())
                .data(reportRequest.getData())
                .reasonCode(reportRequest.getReasonCode())
                .roomId(reportRoom.getRoomId())
                .build();
        return reportResponse;
    }

    //콘텐츠 삭제
    public void deleteContents(){

    }
    //영구적인 제한
    public void permanentRestriction(Long userReportId){
        
    }

    //일시적인 기능 제한
    public void temporalRestriction(){

    }

    // 관리자 모드
    @Override
    public List<UserReport> getUserReports(Long userId){//신고한 사람이 신고한 목록
        return userReportRepository.findAllByReportingUserId(userId);
    }
    @Override
    public UserReport getUserReport(Long userId, Long targetUserId){//일단 targetId로 받는데 targetUser의 고유 id등으로 논의 필요(프론트는 targetUserId알 수 있는지 확인)
        return userReportRepository.findByTargetUserUserIdAndReportingUserId(targetUserId, userId);
    }

    @Override
    public List<Report> getReports(Long userId){//신고한 사람이 신고한 목록
        return reportRepository.findAllByReportingUserId(userId);
    }
    @Override
    public Report getReport(Long userId, Long targetUserId){
        return reportRepository.findByTargetUserUserIdAndReportingUserId(targetUserId, userId);
    }
}
