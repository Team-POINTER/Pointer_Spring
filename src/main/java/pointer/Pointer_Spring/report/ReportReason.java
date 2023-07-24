package pointer.Pointer_Spring.report;

public enum ReportReason {
    SPAM(1), INSULT(2), SEXUAL_AVERSION(3), VIOLENCE(4), CUSTOM(5);
    private final int value;

    ReportReason(int value) {
        this.value = value;
    }
}
