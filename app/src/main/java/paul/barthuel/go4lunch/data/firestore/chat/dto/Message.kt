package paul.barthuel.go4lunch.data.firestore.chat.dto

class Message {
    var text: String = ""
        private set
    var senderId: String = ""
        private set
    var senderName: String = ""
        private set

    // return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
    var epoch: Long = 0
        private set

    constructor() {}
    constructor(text: String, senderId: String, senderName: String, epoch: Long) {
        this.text = text
        this.senderId = senderId
        this.senderName = senderName
        this.epoch = epoch
        //this.epoch = epoch.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}