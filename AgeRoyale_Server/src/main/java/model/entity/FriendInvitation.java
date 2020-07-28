package model.entity;

import model.enumeration.InvitationStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;

/** Representa la clase FriendInvitation, esta contiene la información que hace referencia a una solicitud de amistad */
public class FriendInvitation implements Serializable {
    /** Representa el id de la solicitud */
    private Integer id;
    /** Representa el id del usuario que realiza la solicitud */
    private Integer invitingUser;
    /** Representa el id del usuario que recibe la solicitud */
    private Integer invitedUser;
    /** Representa el nombre del usuario que realiza la solicitud */
    private String userName;
    /** Representa el nombre del usuario que recibe la solicitud */
    private String friendName;
    /** Representa el usuario que recibe la solicitud */
    private User user;
    /** Representa el estado de la solicitud, este puede ser: Pending, Refuse, Accepted */
    private InvitationStatus status;
    /** Representa la fecha en la que se realiza la solicitud */
    private String invitationDate;

    /** Crea una FriendInvitation */
    public FriendInvitation() {
    }

    /**
     * Crea una FriendInvitation
     * @param id - Representa el id de la solicitud
     * @param invitingUser - Representa el id del usuario que realiza la solicitud
     * @param invitedUser - Representa el id del usuario que recibe la solicitud
     * @param status - Representa el estado de la solicitud
     * @param invitationDate . Representa la fecha en la que se realiza la solicitud
     */
    public FriendInvitation(Integer id, Integer invitingUser, Integer invitedUser, InvitationStatus status, String invitationDate) {
        this.id = id;
        this.invitingUser=invitingUser;
        this.invitedUser=invitedUser;
        this.status = status;
        this.invitationDate = invitationDate;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public String getInvitationDate() {
        return invitationDate;
    }

    public void setInvitationDate(String invitationDate) {
        this.invitationDate = invitationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(Integer invitingUser) {
        this.invitingUser = invitingUser;
    }

    public Integer getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(Integer invitedUser) {
        this.invitedUser = invitedUser;
    }

    @Override
    public String toString() {
        return "FriendInvitation{" +
                "id=" + id +
                ", invitingUser=" + invitingUser +
                ", invitedUser=" + invitedUser +
                ", userName='" + userName + '\'' +
                ", friendName='" + friendName + '\'' +
                ", user=" + user +
                ", status=" + status +
                ", invitationDate='" + invitationDate + '\'' +
                '}';
    }
}