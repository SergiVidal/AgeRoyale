package model.entity;

import java.util.ArrayList;
import java.util.List;

public class UserStatistic {

    private List<UserStat> userStats;

    public UserStatistic() {
        userStats = new ArrayList<>();
    }

    public UserStatistic(List<UserStat> userStats) {
        this.userStats = userStats;
    }

    public List<UserStat> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStat> userStats) {
        this.userStats = userStats;
    }

    @Override
    public String toString() {
        return "UserStatistic{" +
                "userStats=" + userStats +
                '}';
    }
}
