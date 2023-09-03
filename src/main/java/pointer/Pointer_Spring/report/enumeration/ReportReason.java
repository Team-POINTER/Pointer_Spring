package pointer.Pointer_Spring.report.enumeration;

public enum ReportReason {
    SPAM(0), JUST_HATE(1), CUSTOM(3);
    //, VIOLENCE(4), INSULT(5), SEXUAL_AVERSION(6);
    private final int value;

    ReportReason(int value) {
        this.value = value;
    }
}
