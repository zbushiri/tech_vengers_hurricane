package model;
import java.time.LocalDateTime;
public class Message {
    private String content;
    private LocalDateTime timestamp;
    private SyncStatus syncStatus;

    public Message(String content, LocalDateTime timestamp, SyncStatus syncStatus) {
        setContent(content);
        setTimestamp(timestamp);
        setSyncStatus(syncStatus);
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
}
