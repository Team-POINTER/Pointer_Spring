package pointer.Pointer_Spring.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.report.enumeration.ReportType;
import pointer.Pointer_Spring.report.service.ReportService;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.service.AuthService;
import pointer.Pointer_Spring.user.service.UserService;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.dto.VoteDto;

import java.util.List;
import java.util.Optional;

@RestController("/api/v1")
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AuthService authService;

    //user가 실행하는 api
    @PostMapping("/user-report/create/")//done
    public BaseResponse<ReportDto.UserReportResponse> saveUserReport(@CurrentUser UserPrincipal userPrincipal, @RequestBody ReportDto.UserReportRequest userReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveUserReport(userPrincipal.getId(), userReportRequest));
    }
    @PostMapping("/report/create/")//done
    public BaseResponse<ReportDto.ReportResponse> saveUserReport(@CurrentUser UserPrincipal userPrincipal, @RequestBody ReportDto.ReportRequest ReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveReport(userPrincipal.getId(), ReportRequest));
    }

    //관리자 모드에서 실행할 api
    @PatchMapping("/report/delete/{reportId}")//done
    public BaseResponse<ExceptionCode> deleteContents(@PathVariable Long reportId){
        reportService.deleteContents(reportId);
        return new BaseResponse<>(ExceptionCode.REPORT_HANDLE_SUCCESS);
    }
    @PostMapping("/user-report/block/user/{userReportId}")//done
    public BaseResponse<ExceptionCode> permanentRestrictionByUserReport(@CurrentUser UserPrincipal userPrincipal , @PathVariable Long userReportId){
        reportService.permanentRestrictionByUserReport(userReportId);
        authService.resign(userPrincipal);//탈퇴 처리
        return new BaseResponse<>(ExceptionCode.REPORT_HANDLE_SUCCESS);
    }
    @PostMapping("/report/block/user/{reportId}")//done
    public BaseResponse<ExceptionCode> permanentRestrictionByOtherReport(@PathVariable Long reportId){
        reportService.permanentRestrictionByOtherReport(reportId);
        return new BaseResponse<>(ExceptionCode.REPORT_HANDLE_SUCCESS);
    }
    @PostMapping("/report/restrict/user/{reportId}")
    public BaseResponse<ExceptionCode> temporalRestriction(@PathVariable Long reportId){
        reportService.temporalRestriction(reportId);
        return new BaseResponse<>(ExceptionCode.REPORT_HANDLE_SUCCESS);
    }

    //추후 관리자 모드로 관리시 필요할 수도 있는 api
    @GetMapping("/user-reports/{userId}")//신고한 사람 -> done
    public BaseResponse<List<ReportDto.UserReportResponse>> getUserReports(@PathVariable Long userId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getUserReports(userId));
    }
    //신고 받은 사람도 필요할 듯
    @GetMapping("/user-report/{userId}/{targetId}") //-> done
    public BaseResponse<ReportDto.UserReportResponse> getUserReport(@PathVariable Long userId, @PathVariable Long targetId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS , reportService.getUserReport(userId, targetId));
    }
    @GetMapping("/user-reports/target/{targetId}")//-> done
    public BaseResponse<List<ReportDto.UserReportResponse>> getUserReportsByTarget(@PathVariable Long targetId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getUserReportsByTarget(targetId));
    }

    @GetMapping("/reports/{userId}")//신고한 사람 -> done
    public BaseResponse<List<ReportDto.ReportResponse>> getReports(@PathVariable Long userId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getReports(userId));
    }
    @GetMapping("/reports/target/{targetId}") // -> done
    public BaseResponse<List<ReportDto.ReportResponse>> getReportsByTarget(@PathVariable Long targetId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getReportsByTarget(targetId));
    }
    //update
    @GetMapping("/reports") //질문, 힌트, 차단된 유저 리스트 조회
    public BaseResponse<List<Object>> getReportListByReportType(@RequestParam(required = false) Long lastReportId, @RequestParam int size, @RequestParam ReportType reportType){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.fetchPagesCursorInReport(lastReportId, size, reportType));
    }
    @GetMapping("/user-reports")
    public BaseResponse<List<Object>> getUserReportList(@RequestParam(required = false)  Long lastReportId, @RequestParam int size){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.fetchPagesCursorInReport(lastReportId, size, ReportType.USER));
    }
    @GetMapping("/reports/restriction/user") //질문, 힌트, 차단된 유저 리스트 조회
    public BaseResponse<List<Object>> getRestrictedUsersReportListByReportType(@RequestParam(required = false)  Long lastReportId, @RequestParam int size, @RequestParam ReportType reportType){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.fetchPagesCursorInManagedReport(lastReportId, size, reportType));
    }
    @GetMapping("/reports/block/user")
    public BaseResponse<List<Object>> getBlockedUsersReportList(@RequestParam(required = false)  Long lastReportId, @RequestParam int size){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.fetchPagesCursorInManagedReport(lastReportId, size, ReportType.USER));
    }

    @GetMapping("/report/{reportId}")//reportId로 레포트get -> done
    public BaseResponse<ReportDto.ReportResponse> getReportByReportId(@PathVariable Long reportId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getReportByReportId(reportId));
    }
    @GetMapping("/user-report/{userReportId}")//userReportId로 유저레포트 get -> done
    public BaseResponse<ReportDto.UserReportResponse> getUserReportByUserReportId(@PathVariable Long userReportId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getUserReportByUserReportId(userReportId));
    }
    //차단 유저 1개 get
    @GetMapping("/block-user-report/{blockedUserId}")//blockesUserId 레포트get -> done
    public BaseResponse<ReportDto.BlockedUserResponse> getReportByBlockUsreId(@PathVariable Long blockedUserId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getBlockedUserReportByBlockedUserId(blockedUserId));
    }
    //제한 유저 1개 get
    @GetMapping("/restriction/report/{restrictedUserId}")//reportId로 레포트get -> done
    public BaseResponse<ReportDto.RestrictedUserResponse> getReportByRestrictUserId(@PathVariable Long restrictedUserId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getReportByRestrictedUserRestrictUserId(restrictedUserId));
    }
}
