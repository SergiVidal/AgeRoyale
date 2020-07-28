package model.entity;

public class MatchStatItem {
    int timeId;
    int lastDay;
    int matchesCount;

    public MatchStatItem() {
    }

    public MatchStatItem(int timeId, int matchesCount) {
        this.timeId = timeId;
        this.matchesCount = matchesCount;
    }

    public MatchStatItem(int timeId, int lastDay, int matchesCount) {
        this.timeId = timeId;
        this.lastDay = lastDay;
        this.matchesCount = matchesCount;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getMatchesCount() {
        return matchesCount;
    }

    public void setMatchesCount(int matchesCount) {
        this.matchesCount = matchesCount;
    }

    public int getLastDay() {
        return lastDay;
    }

    public void setLastDay(int lastDay) {
        this.lastDay = lastDay;
    }

    @Override
    public String toString() {
        return "MatchStatItem{" +
                "timeId=" + timeId +
                ", matchesCount=" + matchesCount +
                '}';
    }
}
