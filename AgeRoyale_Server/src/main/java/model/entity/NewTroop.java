package model.entity;

import java.io.Serializable;
import java.util.List;

public class NewTroop implements Serializable {
    private Troop troop;
    private String username;
    private Message message;
    private String matchName;
    private List<Troop> troops;

    public NewTroop() {
    }

    public NewTroop(Troop troop, String username, Message message) {
        this.troop = troop;
        this.username = username;
        this.message = message;
    }

    public NewTroop(Troop troop, String username, Message message, String matchName, List<Troop> troops) {
        this.troop = troop;
        this.username = username;
        this.message = message;
        this.matchName = matchName;
        this.troops = troops;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public NewTroop(Troop troop) {
        this.troop = troop;
    }

    public Troop getTroop() {
        return troop;
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    @Override
    public String toString() {
        return "NewTroop{" +
                "troop=" + troop +
                ", username='" + username + '\'' +
                ", message=" + message +
                '}';
    }
}
