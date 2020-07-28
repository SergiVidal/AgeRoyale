package model.entity;


import model.enumeration.MatchStatus;

import java.io.Serializable;
import java.util.List;

/** Representa la clase Match que contiene toda la información relacionada con una partida */
public class Match implements Serializable {
    /** Representa el id de la partida */
    private Integer id;
    /** Representa el nombre de la partida */
    private String matchName;
    /** Representa la fecha de la partida */
    private String matchDate;
    /** Representa si la partida es pública (true) o privada (false) */
    private boolean isPublic;
    /** Representa el estado de la partida, este puede ser: InProgress, Finished, Pending, Cancelled */
    private MatchStatus matchStatus;
    /** Representa el tiempo transcurrido de la partida */
    private int matchTime;
    /** Representa el Player Host (Creador) de la partida */
    private Player plHost;
    /** Representa el Player Guest de la partida */
    private Player plGuest;
    /** Representa el Player ganador de la partida */
    private Player winner;
    /** Representa el tablero (model) de la partida */
    private Board board;
    /** Variable auxiliar que representa una fila del tablero, necesaria para la gestión de la partida (Ej: mover una tropa) */
    private Integer rows;
    /** Variable auxiliar que representa una columna del tablero, necesaria para la gestión de la partida (Ej: mover una tropa)  */
    private Integer cols;
    /** Representa una lista de tropas existentes (no por cada jugador, sino las que hemos creado en la aplicación - Warrior, Archer, Cannon, ArcherTower) */
    private List<Troop> troops;

    /** Crea un Match */
    public Match() {
    }

    /**
     * Crea un Match
     * @param matchName - Representa el nombre de la partida
     * @param matchStatus - Representa el estado de la partida,
     * @param isPublic - Representa si la partida es pública o privada
     * @param plHost - Representa el Player Host (Creador) de la partida
     */
    public Match(String matchName, MatchStatus matchStatus, boolean isPublic, Player plHost) {
        this.matchName = matchName;
        this.matchStatus = matchStatus;
        this.isPublic = isPublic;
        this.plHost = plHost;
        this.board = new Board(20, 10);
    }

    /**
     * Crea un Match
     * @param id - Representa el id de la partida
     * @param matchName - Representa el nombre de la partida
     * @param matchDate- Representa la fecha de la partida
     * @param isPublic - Representa si la partida es pública o privada
     * @param matchStatus - Representa el estado de la partida,
     * @param matchTime- Representa el tiempo transcurrido de la partida
     * @param plHost - Representa el Player Host (Creador) de la partida
     * @param plGuest - Representa el Player Guest de la partida
     * @param winner - Representa el Player ganador de la partida
     * @param rows - Representa una fila del tablero, necesaria para la gestión de la partida (Ej: mover una tropa)
     * @param cols - Representa una columna del tablero, necesaria para la gestión de la partida (Ej: mover una tropa)
     */
    public Match(Integer id, String matchName, String matchDate, boolean isPublic, MatchStatus matchStatus, int matchTime, Player plHost, Player plGuest, Player winner, Integer rows, Integer cols) {
        this.id = id;
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.isPublic = isPublic;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
        this.plHost = plHost;
        this.plGuest = plGuest;
        this.winner = winner;
        this.rows = rows;
        this.cols = cols;
        this.board = new Board(rows, cols);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(int matchTime) {
        this.matchTime = matchTime;
    }

    public Player getPlHost() {
        return plHost;
    }

    public void setPlHost(Player plHost) {
        this.plHost = plHost;
    }

    public Player getPlGuest() {
        return plGuest;
    }

    public void setPlGuest(Player plGuest) {
        this.plGuest = plGuest;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getCols() {
        return cols;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", matchName='" + matchName + '\'' +
                ", matchDate='" + matchDate + '\'' +
                ", isPublic=" + isPublic +
                ", matchStatus=" + matchStatus +
                ", matchTime=" + matchTime +
                ", plHost=" + plHost +
                ", plGuest=" + plGuest +
                ", winner=" + winner +
                ", board=" + board +
                ", rows=" + rows +
                ", cols=" + cols +
                ", troops=" + troops +
                '}';
    }
}