package com.techvengershurricane.model;

public class Message {
    private String content;
    private String timestamp;
    private String syncStatus;

    public Message() {
        /* TODO: connect offline messages to users. */
    }

    public Message(String content, String timestamp, String syncStatus) {
        this.content = content;
        this.timestamp = timestamp;
        this.syncStatus = syncStatus;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
