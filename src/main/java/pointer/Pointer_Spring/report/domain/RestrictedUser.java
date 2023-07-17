package pointer.Pointer_Spring.report.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;

@Getter
@Entity(name = "RestrictedUser")
@RequiredArgsConstructor
public class RestrictedUser {
    private Long reportId;
    private Integer temporalNum;
//    public enum ReportType{
//        QUESTION, HINT, CHAT
//    }
    private Report.ReportType type;
}
