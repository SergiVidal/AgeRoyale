package model.entity;

import java.io.Serializable;
import java.util.List;

/** Representa la clase UserMatch, esta contiene la información que hace referencia al listado de partidas, permite la comunicación entre Cliente y Servidor */
public class UserMatch implements Serializable {
    /** Representa el listado de partidas */
    private List<Match> matches;
    /** Representa el nombre del usuario host */
    private String username;
    /** Representa el nombre del usuario guest */
    private String friendName;
    /** Representa la acción realizada (Crear/Cancelar/Unirse a una partida) */
    private String action;
    /** Representa el nombre de la partida */
    private String matchName;
    /** Representa la información recibida por el servidor */
    private Message message;

    /**
     * Crea un UserMatch
     */
    public UserMatch() {
    }

    /**
     * Crea un UserMatch
     * @param matches - Representa el listado de partidas
     * @param message - Representa la información recibida por el servidor
     */
    public UserMatch(List<Match> matches, Message message) {
        this.matches = matches;
        this.message = message;
    }

    /**
     * Crea un UserMatch
     * @param username - Representa el nombre del usuario host
     */
    public UserMatch(String username) {
        this.username = username;
    }

    /**
     * Crea un UserMatch
     * @param username - Representa el nombre del usuario host
     * @param action - Representa la acción realizada
     */
    public UserMatch(String username, String action) {
        this.username = username;
        this.action = action;
    }

    /**
     * Crea un UserMatch
     * @param username - Representa el nombre del usuario host
     * @param matchName - Representa el nombre de la partida
     * @param action - Representa la acción realizada
     */
    public UserMatch(String username, String matchName, String action) {
        this.username = username;
        this.action = action;
        this.matchName = matchName;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserMatch{" +
                "matches=" + matches +
                ", username='" + username + '\'' +
                ", friendName='" + friendName + '\'' +
                ", action='" + action + '\'' +
                ", matchName='" + matchName + '\'' +
                ", message=" + message +
                '}';
    }
}