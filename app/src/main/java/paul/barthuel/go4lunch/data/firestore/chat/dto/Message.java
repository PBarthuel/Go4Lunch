package paul.barthuel.go4lunch.data.firestore.chat.dto;

public class Message {

    private String text;
    private String sender;
    private String date;

    public Message() { }

    public Message(String text, String sender, String date) {

        this.text = text;
        this.sender = sender;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }
}
