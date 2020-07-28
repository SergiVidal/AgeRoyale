package model.entity;

import java.util.ArrayList;
import java.util.List;

public class MatchStat {
    private List<MatchStatItem> matchStatItems;
    private Message message;

    public MatchStat() {
        this.matchStatItems=new ArrayList<>();
    }

    public MatchStat(List<MatchStatItem> matchStatItems, Message message) {
        this.matchStatItems = matchStatItems;
        this.message = message;
    }

    public List<MatchStatItem> getMatchStatItems() {
        return matchStatItems;
    }

    public void setMatchStatItems(List<MatchStatItem> matchStatItems) {
        this.matchStatItems = matchStatItems;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MatchStat{" +
                "matchStatItems=" + matchStatItems +
                ", message=" + message +
                '}';
    }
}
