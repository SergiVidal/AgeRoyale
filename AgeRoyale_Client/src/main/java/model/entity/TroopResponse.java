package model.entity;

import java.io.Serializable;

public class TroopResponse implements Serializable {
    private Object object;
    private Message message;

    public TroopResponse() {
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TroopResponse{" +
                "object=" + object +
                ", message=" + message +
                '}';
    }
}