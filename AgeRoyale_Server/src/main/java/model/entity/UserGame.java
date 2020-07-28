package model.entity;

import java.io.Serializable;
import java.util.Arrays;

/** Representa la clase UserGame, esta contiene la información que hace referencia a una partida, permite la comunicación entre Cliente y Servidor */
public class UserGame implements Serializable {
    /** Representa el Player Host (Creador) de la partida */
    private Player playerHost;
    /** Representa el Player Guest de la partida */
    private Player playerGuest;
    /** Variable auxiliar que representa el nombre de una tropa de la partida, facilida la gestión de la partida */
    private String troopName;
    /** Representa la acción realizada (crear/colocar/mover/atacar una tropa/actualizar la partida) */
    private String action;
    /** Representa la información recibida por el servidor */
    private Message message;
    /** Variable auxiliar que representa una tropa de la partida, facilida la gestión de la partida */
    private Troop troop;
    /** Representa el juego */
    private Game game;
    /** Representa las modificaciones que se van realizando periodicamente en el tablero */
    private boolean [][] modifications;

    /**
     * Crea un UserGame
     */
    public UserGame() {
    }

    /**
     * Crea un UserGame
     * @param troop - Representa una tropa de la partida
     * @param game - Representa el juego
     */
    public UserGame(Troop troop, Game game) {
        this.troop = troop;
        this.game = game;
    }

    public Player getPlayerHost() {
        return playerHost;
    }

    public void setPlayerHost(Player playerHost) {
        this.playerHost = playerHost;
    }

    public Player getPlayerGuest() {
        return playerGuest;
    }

    public void setPlayerGuest(Player playerGuest) {
        this.playerGuest = playerGuest;
    }

    public String getTroopName() {
        return troopName;
    }

    public void setTroopName(String troopName) {
        this.troopName = troopName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Troop getTroop() {
        return troop;
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean[][] getModifications() {
        return modifications;
    }

    public void setModifications(boolean[][] modifications) {
        this.modifications = modifications;
    }

    @Override
    public String toString() {
        return "UserGame{" +
                "playerHost=" + playerHost +
                ", playerGuest=" + playerGuest +
                ", troopName='" + troopName + '\'' +
                ", action='" + action + '\'' +
                ", message=" + message +
                ", troop=" + troop +
                ", game=" + game +
                ", modifications=" + Arrays.toString(modifications) +
                '}';
    }
}