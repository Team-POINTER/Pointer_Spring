package pointer.Pointer_Spring.report.enumeration;

public enum ReportReason {
    SPAM(0), JUST_HATE(1), VIOLENCE(2), INSULT(3), SEXUAL_AVERSION(4), CUSTOM(5);
    private final int value;

    ReportReason(int value) {
        this.value = value;
    }
}
