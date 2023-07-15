package pointer.Pointer_Spring.alarm.service;

import pointer.Pointer_Spring.alarm.dto.AlarmDto;

public interface AlarmService {
    void poke(Long userId, Long questionId);

    void activeAllAlarm(Long userId, AlarmDto.AlarmActiveRequest request);

    void activeAlarm(Long userId, AlarmDto.AlarmActiveRequest request);

    void activeChatAlarm(Long userId, AlarmDto.AlarmActiveRequest request);

    void activeEventAlarm(Long userId, AlarmDto.AlarmActiveRequest request);

    AlarmDto.GetAlarmActiveResponse getActiveAlarm(Long userId);

    AlarmDto.GetAlarmResponses getAlarms(Long userId, Long cursorId);

    void eventAlarm(AlarmDto.EventAlarmRequest request);
}
