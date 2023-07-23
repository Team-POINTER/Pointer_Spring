package pointer.Pointer_Spring.alarm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class AlarmDto {

    @Getter
    @NoArgsConstructor
    public static class AlarmActiveRequest {
        private boolean active;

        @Builder
        public AlarmActiveRequest(boolean active) {
            this.active = active;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetAlarmActiveResponse {
        private boolean allAlarm;
        private boolean activeAlarm;
        private boolean chatAlarm;
        private boolean eventAlarm;

        @Builder
        public GetAlarmActiveResponse(boolean allAlarm, boolean activeAlarm, boolean chatAlarm, boolean eventAlarm) {
            this.allAlarm = allAlarm;
            this.activeAlarm = activeAlarm;
            this.chatAlarm = chatAlarm;
            this.eventAlarm = eventAlarm;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetAlarmResponses {
        private boolean newAlarm;
        private boolean newFriendAlarm;
        private int newFriendAlarmCnt;
        private List<GetAlarmResponse> alarmList;


        @Builder
        public GetAlarmResponses(boolean newAlarm, boolean newFriendAlarm, int newFriendAlarmCnt, List<GetAlarmResponse> alarmList) {
            this.newAlarm = newAlarm;
            this.newFriendAlarm = newFriendAlarm;
            this.newFriendAlarmCnt = newFriendAlarmCnt;
            this.alarmList = alarmList;
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetAlarmResponse {
        private Long alarmId;
        private Long sendUserId;
        private String sendUserName;
        private String sendUserProfile;
        private String content;
        private String type;

        @Builder
        public GetAlarmResponse(Long alarmId, Long sendUserId, String sendUserName, String sendUserProfile, String content, String type) {
            this.alarmId = alarmId;
            this.sendUserId = sendUserId;
            this.sendUserName = sendUserName;
            this.sendUserProfile = sendUserProfile;
            this.content = content;
            this.type = type;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EventAlarmRequest {
        private String content;

        @Builder
        public EventAlarmRequest(String content) {
            this.content = content;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoTokenRequest {
        //Map<String, Object> requestMap;
        //private Long uuid;
        private String deviceId;
        private String pushType;
        private String pushToken;
        //private String apnsEnv;

        @Builder
//        public KakaoTokenRequest(Map<String, Object> requestMap) {
//            this.requestMap = requestMap;
//        }
        public KakaoTokenRequest(String deviceId, String pushType, String pushToken) {
            //this.uuid = uuid;
            this.deviceId = deviceId;
            this.pushType = pushType;
            this.pushToken = pushToken;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoPushRequest {
        private PushType forApns;

        @Builder
        public KakaoPushRequest(PushType forApns) {
            this.forApns = forApns;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PushMessage {
        private PushType forApns;

        @Builder
        public PushMessage(PushType forApns) {
            this.forApns = forApns;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PushType {
        private String message;
        private String apnsEnv;

        @Builder
        public PushType(String message, String apnsEnv) {
            this.message = message;
            this.apnsEnv = apnsEnv;
        }
    }
}
