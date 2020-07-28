package model.entity;

import model.enumeration.InvitationStatus;

import java.io.Serializable;

/** Representa la clase MatchInvitation, esta contiene la información que hace referencia a una solicitud (entre amigos) de jugar una partida privada */
public class MatchInvitation implements Serializable {
    /** Representa el id de la partida */
    private Integer matchId;
    /** Representa el id de la solicitud */
    private Integer id;
    /** Representa si la solicitud ha sido aceptada (true) o no (false) */
    private boolean isAccepted;
    /** Representa el usuario que realiza la solicitud */
    private User host;
    /** Representa el usuario que recibe la solicitud */
    private User guest;
    /** Representa el estado de la solicitud, este puede ser: Pending, Refuse, Accepted */
    private InvitationStatus status;

    /** Crea el MatchInvitation */
    public MatchInvitation() {

    }

    /**
     * Crea el MatchInvitation
     * @param id - Representa el id de la solicitud
     * @param isAccepted - Representa si la solicitud ha sido aceptada o no
     * @param host - Representa el usuario que realiza la solicitud
     * @param guest - Representa el usuario que recibe la solicitud
     */
    public MatchInvitation(Integer id, boolean isAccepted, User host, User guest) {
        this.id = id;
        this.isAccepted = isAccepted;
        this.host = host;
        this.guest = guest;
    }

    /**
     * Crea el MatchInvitation
     * @param id - Representa el id de la solicitud
     * @param matchId - Representa el id de la partida
     * @param isAccepted - Representa si la solicitud ha sido aceptada o no
     * @param host - Representa el usuario que realiza la solicitud
     * @param guest - Representa el usuario que recibe la solicitud
     */
    public MatchInvitation(Integer id, Integer matchId, boolean isAccepted, User host, User guest) {
        this.id = id;
        this.matchId = matchId;
        this.isAccepted = isAccepted;
        this.host = host;
        this.guest = guest;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }


    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MatchInvitation{" +
                "matchId=" + matchId +
                ", id=" + id +
                ", isAccepted=" + isAccepted +
                ", host=" + host +
                ", guest=" + guest +
                ", status=" + status +
                '}';
    }
}