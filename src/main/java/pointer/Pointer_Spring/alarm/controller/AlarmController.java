package pointer.Pointer_Spring.alarm.controller;

import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.service.AlarmService;
import pointer.Pointer_Spring.common.response.BaseResponse;

@RestController
@RequestMapping("/alarm")
@CrossOrigin(origins = "http://localhost:3000")
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

    // 전체 알림 활성화 및 비활성화
    @PostMapping("/all/{userId}")
    public BaseResponse<Void> activeAllAlarm(@PathVariable Long userId, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeAllAlarm(userId, request);
        return new BaseResponse<>();
    }

    // 활동 알림 활성화 및 비활성화
    @PostMapping("/active/{userId}")
    public BaseResponse<Void> activeAlarm(@PathVariable Long userId, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeAlarm(userId, request);
        return new BaseResponse<>();
    }

    // 채팅 알림 활성화 및 비활성화
    @PostMapping("/chat/{userId}")
    public BaseResponse<Void> activeChatAlarm(@PathVariable Long userId, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeChatAlarm(userId, request);
        return new BaseResponse<>();
    }

    // 이벤트 알림 활성화 및 비활성화
    @PostMapping("/event/{userId}")
    public BaseResponse<Void> activeEventAlarm(@PathVariable Long userId, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeEventAlarm(userId, request);
        return new BaseResponse<>();
    }

    // 알림 활성화 여부 조회
    @GetMapping("/all/active/{userId}")
    public BaseResponse<AlarmDto.GetAlarmActiveResponse> getActiveAlarm(@PathVariable Long userId) {
        return new BaseResponse<>(alarmService.getActiveAlarm(userId));
    }

    // 알림 창 조회
    @GetMapping("/{userId}/{cursorId}")
    public BaseResponse<AlarmDto.GetAlarmResponses> getAlarm(@PathVariable Long userId, @PathVariable Long cursorId) {
        return new BaseResponse<>(alarmService.getAlarms(userId, cursorId));
    }

    // 이벤트 알림
    @PostMapping("/event")
    public BaseResponse<Void> eventAlarm(@RequestBody AlarmDto.EventAlarmRequest request) {
        alarmService.eventAlarm(request);
        return new BaseResponse<>();
    }
}
