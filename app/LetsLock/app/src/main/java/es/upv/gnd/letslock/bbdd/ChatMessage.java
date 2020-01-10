package es.upv.gnd.letslock;

import android.widget.ImageView;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String nameUser;
    private Long messageTime;
    private ImageView messageFoto;

    public ChatMessage(String messageText, String messageUser, String nameUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.nameUser = nameUser;

        messageTime = new Date().getTime();
    }

    public ChatMessage() {}

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public ImageView getMessageFoto() {
        return messageFoto;
    }

    public void setMessageFoto(ImageView messageFoto) {
        this.messageFoto = messageFoto;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
