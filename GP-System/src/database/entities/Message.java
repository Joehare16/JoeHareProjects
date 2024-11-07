package database.entities;

import java.sql.Date;
import java.sql.ResultSet;

public class Message {
    private Integer message_id;
    private Integer sender_id;
    private Integer receiver_id;

    public String message;
    public Date timestamp;

    public Message(Integer message_id, Integer sender_id, Integer receiver_id, String message,
            Date timestamp) {
        this.message_id = message_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static Message fromResultSet(ResultSet result) {
        try {
            return new Message(
                    result.getInt("message_id"),
                    result.getInt("sender_id"),
                    result.getInt("receiver_id"),
                    result.getString("message"),
                    result.getDate("timestamp"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
