package model.entity;

import java.io.Serializable;

/** Representa la clase Friend, esta contiene la información de un amigo de un usuario */
public class Friend implements Serializable {
    /** Representa el usuario */
    private User user;
    /** Representa la fecha desde que son amigos */
    private String friendshipDate;

    /**
     * Crea un Friend
     * @param user - Representa el usuario
     * @param friendshipDate - Representa la fecha desde que son amigos
     */
    public Friend(User user, String friendshipDate) {
        this.user = user;
        this.friendshipDate = friendshipDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(String friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "user=" + user +
                ", friendshipDate=" + friendshipDate +
                '}';
    }
}