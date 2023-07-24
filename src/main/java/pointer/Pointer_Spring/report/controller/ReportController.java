package pointer.Pointer_Spring.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.report.service.ReportService;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.dto.VoteDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //user가 실행하는 api
    @PostMapping("/user-report/create/")//done
    public BaseResponse<ReportDto.UserReportResponse> saveUserReport(@RequestBody ReportDto.UserReportRequest userReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveUserReport(userReportRequest));
    }
    @PostMapping("/report/create/")//done
    public BaseResponse<ReportDto.ReportResponse> saveUserReport(@RequestBody ReportDto.ReportRequest ReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveReport(ReportRequest));
    }

    //관리자 모드에서 실행할 api
    @PatchMapping("/report/delete/{reportId}")//done
    public BaseResponse<ExceptionCode> deleteContents(@PathVariable Long reportId){
        reportService.deleteContents(reportId);
        return new BaseResponse<>(ExceptionCode.REPORT_HANDLE_SUCCESS);
    }
    @PostMapping("/user-report/block/user/{userReportId}")//done
    public BaseResponse<ExceptionCode> permanentRestrictionByUserReport(@PathVariable Long userReportId){
        reportService.permanentRestrictionByUserReport(userReportId);
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
//    @GetMapping("/report/{userId}/{targetId}")
//    public void getReport(@PathVariable Long userId, @PathVariable Long targetId){
//        reportService.getReport(userId, targetId);
//    }
    @GetMapping("/reports/target/{targetId}") // -> done
    public BaseResponse<List<ReportDto.ReportResponse>> getReportsByTarget(@PathVariable Long targetId){
        return new BaseResponse<>(ExceptionCode.REPORT_GET_SUCCESS, reportService.getReportsByTarget(targetId));
    }
}
