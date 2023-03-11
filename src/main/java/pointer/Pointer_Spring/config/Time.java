package pointer.Pointer_Spring.config;

import java.time.LocalDateTime;

public class Time {
    private static class TIME_MAX {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String calculateTime(LocalDateTime updateAt) {
        long curTime = System.currentTimeMillis();
        long updateTime = java.sql.Timestamp.valueOf(updateAt).getTime();
        long diffTime = (curTime - updateTime) / 1000;
        String time;
        if (diffTime < TIME_MAX.SEC) {
            time = diffTime + "초 전";
        } else if ((diffTime /= TIME_MAX.SEC) < TIME_MAX.MIN) {
            time = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAX.MIN) < TIME_MAX.HOUR) {
            time = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAX.HOUR) < TIME_MAX.DAY) {
            time = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAX.DAY) < TIME_MAX.MONTH) {
            time = (diffTime) + "달 전";
        } else {
            time = (diffTime) + "년 전";
        }
        return time;
    }
}
