package paul.barthuel.go4lunch.data.firestore.chat.dto;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

public class Message {

    private String text;
    private String senderId;
    private String senderName;
    private Long epoch;

    public Message() {}

    public Message(String text, String senderId, String senderName, Long epoch) {

        this.text = text;
        this.senderId = senderId;
        this.senderName = senderName;
        this.epoch = epoch;
        //this.epoch = epoch.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

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

    public Long getEpoch() {
       // return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
        return epoch;
    }
}
