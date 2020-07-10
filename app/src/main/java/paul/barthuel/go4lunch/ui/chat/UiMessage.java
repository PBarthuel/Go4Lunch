package paul.barthuel.go4lunch.ui.chat;

public class UiMessage {

    private final String message;
    private final String date;
    private final String senderName;
    private final boolean isSender;

    public UiMessage(String message, String date, String senderName, boolean isSender) {
        this.message = message;
        this.date = date;
        this.senderName = senderName;
        this.isSender = isSender;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean isSender() {
        return isSender;
    }
}
