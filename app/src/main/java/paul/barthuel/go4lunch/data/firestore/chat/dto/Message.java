package paul.barthuel.go4lunch.data.firestore.chat.dto;

import org.threeten.bp.LocalDateTime;

public class Message {

    private String text;
    private String senderId;
    private String senderName;
    private LocalDateTime date;

    public Message() {}

    public Message(String text, String senderId, String senderName, LocalDateTime date) {

        this.text = text;
        this.senderId = senderId;
        this.senderName = senderName;
        this.date = date;

    }

    public String getSenderName() {
        return senderName;
    }
    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
