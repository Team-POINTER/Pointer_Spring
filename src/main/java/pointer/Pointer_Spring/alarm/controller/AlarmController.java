package pointer.Pointer_Spring.alarm.controller;

import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.service.AlarmService;
import pointer.Pointer_Spring.alarm.service.KakaoPushNotiService;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alarm")
//@CrossOrigin(origins = "http://localhost:3000")
public class AlarmController {

    private final AlarmService alarmService;
    private final KakaoPushNotiService kakaoPushNotiService;


    public AlarmController(AlarmService alarmService, KakaoPushNotiService kakaoPushNotiService) {
        this.alarmService = alarmService;
        this.kakaoPushNotiService = kakaoPushNotiService;
    }

    // 콕 찌르기
    @PostMapping("/poke/{questionId}")
    public BaseResponse<Void> poke(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId) {
        alarmService.poke(userPrincipal, questionId);
        return new BaseResponse<>();
    }

    // 전체 알림 활성화 및 비활성화
    @PostMapping("/all")
    public BaseResponse<Void> activeAllAlarm(@CurrentUser UserPrincipal userPrincipal, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeAllAlarm(userPrincipal, request);
        return new BaseResponse<>();
    }

    // 활동 알림 활성화 및 비활성화
    @PostMapping("/active")
    public BaseResponse<Void> activeAlarm(
            @CurrentUser UserPrincipal userPrincipal, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeAlarm(userPrincipal, request);
        return new BaseResponse<>();
    }

    // 채팅 알림 활성화 및 비활성화
    @PostMapping("/chat")
    public BaseResponse<Void> activeChatAlarm(
            @CurrentUser UserPrincipal userPrincipal, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeChatAlarm(userPrincipal, request);
        return new BaseResponse<>();
    }

    // 이벤트 알림 활성화 및 비활성화
    @PostMapping("/event/active")
    public BaseResponse<Void> activeEventAlarm(@CurrentUser UserPrincipal userPrincipal, @RequestBody AlarmDto.AlarmActiveRequest request) {
        alarmService.activeEventAlarm(userPrincipal, request);
        return new BaseResponse<>();
    }

    // 알림 활성화 여부 조회
    @GetMapping("/all/active")
    public BaseResponse<AlarmDto.GetAlarmActiveResponse> getActiveAlarm(@CurrentUser UserPrincipal userPrincipal) {
        return new BaseResponse<>(alarmService.getActiveAlarm(userPrincipal));
    }

    // 알림 창 조회
    @GetMapping("/{cursorId}")
    public BaseResponse<AlarmDto.GetAlarmResponses> getAlarm(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long cursorId) {
        return new BaseResponse<>(alarmService.getAlarms(userPrincipal, cursorId));
    }

    // 이벤트 알림
    @PostMapping("/event")
    public BaseResponse<Void> eventAlarm(@RequestBody AlarmDto.EventAlarmRequest request) {
        alarmService.eventAlarm(request);
        return new BaseResponse<>();
    }

    // 카카오 토큰 등록
    @PostMapping("/kakao")
    public BaseResponse<Void> registKakaoToken(@CurrentUser UserPrincipal userPrincipal, @RequestBody AlarmDto.KakaoTokenRequest request) {
        kakaoPushNotiService.registKakaoToken(userPrincipal, request);
        return new BaseResponse<>();
    }

    // 카카오 토큰 삭제
    @PostMapping("/kakao/deregister")
    public BaseResponse<Void> deRegisterKakaoToken(Long userId,
                                                   @RequestBody AlarmDto.KakaoTokenDeRegisterRequest request) {
        kakaoPushNotiService.deRegisterKakaoToken(userId, request);
        return new BaseResponse<>();

    }

    @GetMapping("/friends/{cursorId}")
    public BaseResponse<List<AlarmDto.GetFriendAlarmResponse>> getFriendAlarm(@CurrentUser UserPrincipal userPrincipal,
                                                                              @PathVariable("cursorId") Long cursorId) {
        return new BaseResponse<>(alarmService.getFriendAlarm(userPrincipal, cursorId));
    }

    @GetMapping("/unread")
    public BaseResponse<AlarmDto.GetNewAlarmResponse> getNewAlarm(@CurrentUser UserPrincipal userPrincipal) {
        return new BaseResponse<>(alarmService.getNewAlarm(userPrincipal));
    }


}
