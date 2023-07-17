package pointer.Pointer_Spring.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.report.service.ReportService;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.dto.VoteDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //user가 실행하는 api
    @PostMapping("/user-report/create/")
    public BaseResponse<ReportDto.UserReportResponse> saveUserReport(@RequestBody ReportDto.UserReportRequest userReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveUserReport(userReportRequest));
    }
    @PostMapping("/report/create/")
    public BaseResponse<ReportDto.ReportResponse> saveUserReport(@RequestBody ReportDto.ReportRequest ReportRequest){
        return new BaseResponse<>(ExceptionCode.REPORT_CREATE_SUCCESS ,reportService.saveReport(ReportRequest));
    }

    //관리자 모드에서 실행할 api


    //추후 관리자 모드로 관리시 필요할 수도 있는 api
    @GetMapping("/user-reports/{userId}")//신고한 사람
    public void getUserReports(@PathVariable Long userId){
        reportService.getUserReports(userId);
    }
    @GetMapping("/user-report/{userId}/{targetId}")
    public void getUserReport(@PathVariable Long userId, @PathVariable Long targetId){
        reportService.getUserReport(userId, targetId);
    }
    @GetMapping("/reports/{userId}")//신고한 사람
    public void getReports(@PathVariable Long userId){
        reportService.getReports(userId);
    }
    @GetMapping("/report/{userId}/{targetId}")
    public void getReport(@PathVariable Long userId, @PathVariable Long targetId){
        reportService.getReport(userId, targetId);
    }
}
