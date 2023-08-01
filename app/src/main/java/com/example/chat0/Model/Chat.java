package com.example.chat0.Model;

public class Chat {

    private String id;
    private String sender;
    private String receiver;
    private String message;
    private String decryptedMessage;
    private boolean isseen;


    public Chat(String sender, String receiver, String message, boolean isseen, String id, String decryptedMessage) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.decryptedMessage = decryptedMessage;
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getDecryptedMessage() {
        return decryptedMessage;
    }

    public void setDecryptedMessage(String decryptedMessage) {
        this.decryptedMessage = decryptedMessage;
    }
}

    // Other getters and setters if you have more fields in the Chat class...

