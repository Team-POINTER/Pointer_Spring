package pointer.Pointer_Spring.alarm.controller;

import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.alarm.service.AlarmService;
import pointer.Pointer_Spring.common.response.BaseResponse;

@RestController
@RequestMapping("/alarm")
//@CrossOrigin(origins = "http://localhost:3000")
public class AlarmController {

    private final AlarmService alarmService;


    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    // 콕 찌르기
    @PostMapping("/poke/{userId}/{questionId}")
    public BaseResponse<Void> poke(@PathVariable Long userId, @PathVariable Long questionId) {
        alarmService.poke(userId, questionId);
        return new BaseResponse<>();
    }
}
