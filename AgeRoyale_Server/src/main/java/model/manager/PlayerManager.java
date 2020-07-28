package model.manager;

import model.database.dao.PlayerDAO;
import model.database.dao.UserDAO;
import model.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el PlayerManager, que nos permite acceder a métodos del PlayerDAO y guarda una lista de jugadores.
 */
public class PlayerManager {
    /**
     * Representa el PlayerDAO.
     */
    private PlayerDAO playerDAO;
    /**
     * Representa la lista de jugadores.
     */
    private List<Player> players;

    /**
     * Crea el PlayerManager.
     */
    public PlayerManager() {
        this.playerDAO = new PlayerDAO();
        this.players = new ArrayList<>();
    }

    /**
     * Método que sirve para crear un jugador de acuerdo a la información proporcionada de usuario.
     * @param pl Recive como parámetro un jugador / Player que contendrá la información del usuario necesaria para hacer que sea jugador.
     * @return Devuelve un jugador / Player con la información del player para que dicho usuario pueda jugar una partida.
     */
    public Player createPlayer(Player pl) {
        Player player = playerDAO.insertPlayer(pl);
        if (player.getId() != null && !(player.getUsername().equals(""))) {
            addPlayer(pl);
            return player;
        }
        return null;
    }

    /**
     * Método que sirve para poder añadir al jugador / Player dentro de la lista de jugadores que contiene el manager.
     * @param player Recibe como parámetro el jugador a añadir a la lista de jugadores / players.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Método que sirve para poder obtener un jugador dado el id del usuario al que le pertenece. Se obtiene el último jugador creado para dicho usuario.
     * @param id Recibe como parámetro el id del usuario al que le pertenezca el jugador.
     * @return Devuelve el último jugador obtenido para dicho usuario.
     */
    public Player getPlayerById(int id) {
        return playerDAO.showPlayerInfoByUserId(id);
    }

    /**
     * Método que sirve para poder obtener un jugador dado el nombre del usuario al que le pertenece y el nombre de la partida que va a jugar o está jugando.
     * Se obtiene el último jugador creado para dicho usuario.
     * @param username Se pasa el nombre del jugador como parámetro para poderle identificar.
     * @param matchName Se pasa el nombre de la partida que se está jugando o se va a judar como parámetro para poder identificar al jugador por la partida.
     * @return Devuelve la informacion del jugador
     */
    public Player getPlayerByNameAndmatch(String username,String matchName) {
        return playerDAO.showPlayerInfoByNameAndMatchName(username,matchName);
    }

    /**
     * Método que sirve para llamar a DAO para aumentar el dinero de modo pasivo.
     * @param plHost -  Jugador Host
     * @param plGuest - Jugador Guest / invitado
     */
    public void incrementAvailableMoney(Player plHost, Player plGuest) {
        playerDAO.incrementPeriodicMoney(plHost.getId());
        playerDAO.incrementPeriodicMoney(plGuest.getId());
    }
}
