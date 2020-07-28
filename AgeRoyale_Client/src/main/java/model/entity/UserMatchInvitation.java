package model.entity;

import java.io.Serializable;
import java.util.List;

/** Representa la clase UserMatch, esta contiene la información que hace referencia al listado de invitaciones a partidas, permite la comunicación entre Cliente y Servidor */
public class UserMatchInvitation implements Serializable {
    /** Representa el listado de invitaciones a partidas */
    private List<MatchInvitation> matchInvitations;
    /** Representa una invitación a partidas */
    private MatchInvitation lastFriendInvitation;
    /** Representa la información recibida por el servidor */
    private Message message;
    /** Representa el nombre del usuario que realiza la acción */
    private String username;
    /** Representa el nombre del usuario que recibe la acción */
    private String friendName;
    /** Representa el nombre de la partida */
    private String matchName;
    /** Representa la acción realizada (Invitar/Cancelar/Unirse/Rechazar una invitación a partida) */
    private String action;

    /**
     * Crea un UserMatchInvitation
     */
    public UserMatchInvitation() {
    }

    /**
     * Crea un UserMatchInvitation
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que recibe la acción
     * @param matchName - Representa el nombre de la partida
     * @param action - Representa la acción realizada
     */
    public UserMatchInvitation(String username, String friendName, String matchName, String action) {
        this.username = username;
        this.friendName = friendName;
        this.matchName = matchName;
        this.action = action;
    }

    /**
     * Crea un UserMatchInvitation
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param action - Representa la acción realizada
     */
    public UserMatchInvitation(String username, String action) {
        this.username = username;
        this.action = action;
    }

    public List<MatchInvitation> getMatchInvitations() {
        return matchInvitations;
    }

    public void setMatchInvitations(List<MatchInvitation> matchInvitations) {
        this.matchInvitations = matchInvitations;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MatchInvitation getLastFriendInvitation() {
        return lastFriendInvitation;
    }

    public void setLastFriendInvitation(MatchInvitation lastFriendInvitation) {
        this.lastFriendInvitation = lastFriendInvitation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchInvitation) {
        this.matchName = matchInvitation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "UserMatchInvitation{" +
                "matchInvitations=" + matchInvitations +
                ", lastFriendInvitation=" + lastFriendInvitation +
                ", message=" + message +
                ", username='" + username + '\'' +
                ", friendName='" + friendName + '\'' +
                ", matchName='" + matchName + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}