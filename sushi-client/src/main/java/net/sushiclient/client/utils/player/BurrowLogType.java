package net.sushiclient.client.utils.player;

public enum BurrowLogType {
    NONE(false, false),
    ERROR(false, true),
    SUCCESS(true, false),
    ALL(true, true),
    ;

    private final boolean showSuccess;
    private final boolean showError;
    BurrowLogType(boolean showSuccess, boolean showError) {
        this.showSuccess = showSuccess;
        this.showError = showError;
    }

    public boolean getShowSuccess() {
        return this.showSuccess;
    }

    public boolean getShowError() {
        return this.showError;
    }
}
