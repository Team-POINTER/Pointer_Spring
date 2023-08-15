package pointer.Pointer_Spring.alarm.service;

import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.security.UserPrincipal;

import java.util.List;

public interface AlarmService {
    void poke(UserPrincipal userPrincipal, Long questionId);

    void activeAllAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request);

    void activeAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request);

    void activeChatAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request);

    void activeEventAlarm(UserPrincipal userPrincipal, AlarmDto.AlarmActiveRequest request);

    AlarmDto.GetAlarmActiveResponse getActiveAlarm(UserPrincipal userPrincipal);

    AlarmDto.GetAlarmResponses getAlarms(UserPrincipal userPrincipal, Long cursorId);

    void eventAlarm(AlarmDto.EventAlarmRequest request);

    List<AlarmDto.GetFriendAlarmResponse> getFriendAlarm(UserPrincipal userPrincipal, Long cursorId);
}
