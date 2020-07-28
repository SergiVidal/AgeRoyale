package model.entity;

import java.io.Serializable;

/** Representa la clase Game, esta contiene la información que hace referencia a la creación de una partida */
public class Game implements Serializable {
    /** Representa la partida */
    private Match match;
    /** Representa la información recibida por el servidor */
    private Message message;
    /** Representa la acción realizada (ya sea crear/cancelar una partida, unirse como jugador o espectador...) */
    private String action;
    /** Representa el rol (Host, Guest, Viewer) del jugador en la partida */
    private String rol;
    /** Representa el nombre de usuario del jugador */
    private String username;

    /** Crea el Game */
    public Game() {
    }

    /**
     * Crea el Game
     * @param match - Representa la partida
     * @param message - Representa la información recibida por el servidor
     * @param action - Representa la acción realizada
     * @param rol - Representa el rol del jugador en la partida
     * @param username - Representa el nombre de usuario del jugador
     */
    public Game(Match match, Message message, String action, String rol, String username) {
        this.match = match;
        this.message = message;
        this.action = action;
        this.rol = rol;
        this.username = username;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Game{" +
                "match=" + match +
                ", message=" + message +
                ", action='" + action + '\'' +
                ", rol='" + rol + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
