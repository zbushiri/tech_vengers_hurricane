package model;
import java.time.LocalDateTime;
/**
 * A message a user sends, which may be waiting to sync when offline.
 * @author Team Tech-vengers
 */
public class Message {
    private String content;
    private LocalDateTime timestamp;
    private SyncStatus syncStatus;

    /**
     * Creates a message.
     * 
     * @param content the text of the message
     * @param timestamp when it was written
     * @param syncStatus whether it has been sent yet
     */
    public Message(String content, LocalDateTime timestamp, SyncStatus syncStatus) {
        setContent(content);
        setTimestamp(timestamp);
        setSyncStatus(syncStatus);
    }

    /**
     * Returns the content.
     * @return the text of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the timestamp.
     * @return when the message was written
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the sync status.
     * @return whether the message has been sent
     */
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    /**
     * Sets the content.
     * @param content the new text of the message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the timestamp.
     * @param timestamp when the message was written
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the sync status.
     * @param syncStatus whether the message has been sent
     */
    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
}
