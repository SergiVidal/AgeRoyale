package model.database.dao;

import model.database.DBConnector;
import model.entity.*;
import model.enumeration.TroopClass;
import model.enumeration.TroopType;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Representa el Data Access Object que nos permite acceder a datos
 */
public class TroopDAO {
    /* Constantes para identificar las acciones del usuario */
    private final String MATCH_TROOPS = "match troops";
    private final String TROOPS = "troops";
    /**/

    /**
     * Crea el TroopDAO.
     */
    public TroopDAO() {

    }

    /**
     * Método que sirve para añadir una tropa en una partida.
     * @param userGame - UserGame que contiene la inforamción de la partida.
     * @param matchId - Id de la patida
     * @param userId - Id de usuari
     * @return Devuelve el UserGame con la partida devuelta.
     */
    public synchronized UserGame insertTroopToMatch(UserGame userGame, int matchId, int userId) {
        if (userGame.getTroop() != null && matchId != 0 && userId != 0) {
            String query = "INSERT INTO matches_troops(id_match,id_troop,id_user,vitalityPoints,isDead,rowsLocation,colLocation) VALUES (" + matchId + ", " + userGame.getTroop().getId() + "," + userId + ", " + userGame.getTroop().getVitalityPoints() + ", 0" + ", -1, -1);";
            if (DBConnector.getInstance().insertQuery(query)) {
                userGame.setMessage(new Message(0, "Troop Inserted Successfully"));
            } else {
                userGame.setMessage(new Message(1, "Troop can't been inserted"));
            }
        } else {
            userGame.setMessage(new Message(1, "Troop can't be found!"));
        }

        return userGame;
    }

    /**
     * Método que sirve para recuperar todas las tropas que hay en la base de datos.
     * @return Devuelve una lista con las tropas (subclass ArrayList y con objetos de tipo Troop).
     */
    public ArrayList<Troop> readTroops() {
        ArrayList<Troop> troops = new ArrayList<>();
        String query = "Select id, name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost from troops as t;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            while (resultSet.next()) {

                troops.add(getResultSet(resultSet, TROOPS));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return troops;
    }

    /**
     * Método que sirve para recuperar las tropas de un usuario en una partida (filtrado por id de match e id de usuario)-
     * @param match_id - ID de la partida / match
     * @param userId - ID del usuario al que le pertenezcan las tropas
     * @return Devuelve una lista con las tropas (subclass ArrayList y con objetos de tipo Troop) de dicho usuario.
     */
    public synchronized ArrayList<Troop> readTroopsByUserId(int match_id, int userId) {
        ArrayList<Troop> troops = new ArrayList<>();
        String query = "Select id, name, troopType, class, m_t.vitalityPoints, speed, damage, troopRange, icon, cost, m_t.rowsLocation, m_t.colLocation, m_t.matchTroopId from troops as t LEFT JOIN matches_troops as m_t on t.id=m_t.id_troop where id_user=" + userId + " and id_match=" + match_id + ";";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            while (resultSet.next()) {

                troops.add(getResultSet(resultSet, MATCH_TROOPS));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return troops;
    }

    /**
     * Método que sirve para recuperar una tropa dado el nombre de la tropa.
     * @param name - nombre de la tropa
     * @return Devuelve la tropa (filtrada por nombre).
     */
    public Troop getTroopInfoByName(String name) {
        String query = "Select id, name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost from troops as t where t.name='" + name + "';";
        Troop troop = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                troop = getResultSet(resultSet, TROOPS);
                if (troop == null) {
                    troop = null;
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return troop;
    }

    /**
     * Método que sirve para atacar una tropa dado la tropa atacante y la tropa atacada y el id de la partida / match donde se ataca.
     * @param troop - Tropa Atacante
     * @param attackedTroop - Tropa atacada
     * @param matchId - ID del match en el cual la tropa atacante ataca la tropa atacada.
     * @return - Devuelve el userGame con la tropa actualizada
     */
    public synchronized UserGame attackTroop(Troop troop, Troop attackedTroop, int matchId) {
        UserGame troopResponse = new UserGame();
        int remainingLife = (attackedTroop.getVitalityPoints() - troop.getDamage());
        if (remainingLife < 0) {
            remainingLife = 0;
        }
        StringBuilder str = new StringBuilder("update matches_troops set vitalityPoints=" + remainingLife);
        if (remainingLife == 0) {
            str.append(", isDead=1");
        }
        str.append(" where matchTroopId=" + attackedTroop.getMatchTroopId() + ";");
        if (DBConnector.getInstance().updateQuery(str.toString())) {
            troopResponse.setMessage(new Message(0, "Troop has been attacked Successfully!"));
        } else
            troopResponse.setMessage(new Message(1, "Troop can't be attacked Successfully!"));
        return troopResponse;
    }

    /**
     * Método que sirve para mover una tropa dentro del tablero (modelo) y que quede reflejado en la DB.
     * @param troopRequest - Instancia de UserGame con la solicitud para mover la tropa.
     * @param matchId - ID del match
     * @param userId - ID de usuario
     * @param option - Opción a realizar (según el tipo de movimiento por si es host o guest y en que parte del tablero esté).
     * @return Devuelve un userGame con la tropa ya movida y con la información de las tropas actualizada.
     */
    public synchronized UserGame moveTroop(UserGame troopRequest, int matchId, int userId, int option) {
        String query = null;
        if (option == 0) {
            query = "update matches_troops set rowsLocation=" + (troopRequest.getTroop().getRowLocation() + 1) + ", colLocation=" + (troopRequest.getTroop().getColLocation()) + " where id_match=" + matchId + " and id_troop=" + troopRequest.getTroop().getId() + " and id_user=" + userId + " and matchTroopId=" + troopRequest.getTroop().getMatchTroopId() + ";";
        } else if (option == 1) {
            query = "update matches_troops set colLocation=" + (troopRequest.getTroop().getColLocation() + 1) + " where id_match=" + matchId + " and id_troop=" + troopRequest.getTroop().getId() + " and id_user=" + userId + " and matchTroopId=" + troopRequest.getTroop().getMatchTroopId() + ";";
        } else if (option == 2) {
            query = "update matches_troops set colLocation=" + (troopRequest.getTroop().getColLocation() - 1) + " where id_match=" + matchId + " and id_troop=" + troopRequest.getTroop().getId() + " and id_user=" + userId + " and matchTroopId=" + troopRequest.getTroop().getMatchTroopId() + ";";
        } else {
            query = "update matches_troops set rowsLocation=" + (troopRequest.getTroop().getRowLocation() - 1) + ", colLocation=" + (troopRequest.getTroop().getColLocation()) + " where id_match=" + matchId + " and id_troop=" + troopRequest.getTroop().getId() + " and id_user=" + userId + " and matchTroopId=" + troopRequest.getTroop().getMatchTroopId() + ";";
        }
        if (DBConnector.getInstance().updateQuery(query)) {
            troopRequest.setMessage(new Message(0, "Troop moved successfully!"));
        } else {
            troopRequest.setMessage(new Message(1, "Troop can't be moved!"));
        }
        troopRequest.setTroop(getTroopInfoByMatchTroopId(troopRequest.getTroop().getMatchTroopId()));
        troopRequest.getGame().getMatch().setTroops(getMatchTroops(matchId));
        return troopRequest;
    }

    /**
     * Método que sirve para recuperar una tropa dado su id de tropa en partida (matchtroop).
     * @param matchTroopId - ID de la tropa en la partida.
     * @return Devuelve la tropa con todos sus datos.
     */
    public Troop getTroopInfoByMatchTroopId(Integer matchTroopId) {
        String query = "Select id, name, troopType, class, m_t.vitalityPoints, speed, damage, troopRange, icon, cost, m_t.rowsLocation, m_t.colLocation, m_t.matchTroopId from troops as t LEFT JOIN matches_troops as m_t on t.id=m_t.id_troop where matchTroopId=" + matchTroopId + ";";
        Troop troop = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                troop = getResultSet(resultSet, MATCH_TROOPS);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();

        }
        return troop;
    }

    /**
     * Método que sirve para recuperar las tropas de una partida.
     * @param matchId - ID de la partida
     * @return Devuelve una lista con todas las tropas de dicha partida.
     */
    //todo return matchTroops
    public synchronized ArrayList<Troop> getMatchTroops(int matchId) {
        String query = "select troops.id, troops.name, troops.troopType, troops.class, troops.speed, troops.damage, troops.troopRange, troops.icon, troops.cost, matches_troops.vitalityPoints from troops, matches_troops where troops.id=matches_troops.id_troop and matches_troops.id_match=" + matchId + ";";
        ArrayList<Troop> troops = new ArrayList<>();
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            while (resultSet.next()) {

                troops.add(getResultSet(resultSet, MATCH_TROOPS));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return troops;
    }

    /**
     * Método que sirve para pasar del registro obtenido de la tabla en la DB a un objeto de class (instance) de Troop.
     * @param resultSet - Resultset con la info del registro de aquel instante obtenido de la DB.
     * @param type - Tipo sirve para saber en que tabla o tablas se han solicitado los datos.
     * @return Devuelve un objeto / instancia de Troop con todos sus datos.
     * @throws SQLException - SQL Exception
     */
    private Troop getResultSet(ResultSet resultSet, String type) {
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String typeS = (resultSet.getString("troopType"));
            TroopType troopType = null;
            if (typeS.equals("Ofensive")) {
                troopType = TroopType.Ofensive;
            }
            if (typeS.equals("Defensive")) {
                troopType = TroopType.Defensive;
            }
            String classN = resultSet.getString("class");
            TroopClass troopClass = null;
            if (classN.equals("Warrior")) {
                troopClass = TroopClass.Warrior;
            }
            if (classN.equals("Archer")) {
                troopClass = TroopClass.Archer;
            }
            if (classN.equals("Cannon")) {
                troopClass = TroopClass.Cannon;
            }
            if (classN.equals("ArcherTower")) {
                troopClass = TroopClass.ArcherTower;
            }
            int vitalityPoints = resultSet.getInt("vitalityPoints");
            int speed = resultSet.getInt("speed");
            int damage = resultSet.getInt("damage");
            int range = resultSet.getInt("troopRange");
            String icon = resultSet.getString("icon");
            Integer cost = resultSet.getInt("cost");
            Troop troop = new Troop(id, name, troopType, troopClass, vitalityPoints, speed, damage, range, icon, cost);
            if (type.equals(MATCH_TROOPS)) {
                int matchTroopId = resultSet.getInt("matchTroopId");
                int rows = resultSet.getInt("rowsLocation");
                int cols = resultSet.getInt("colLocation");
                troop.setRowLocation(rows);
                troop.setColLocation(cols);
                troop.setMatchTroopId(matchTroopId);
            }
            return troop;
        } catch (SQLException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Método que sirve para localizar una tropa en la DB y posteriormente dentro del tablero.
     * @param troop - Tropa a ubicar en el tablero.
     * @param matchId - ID de la partida en la que se va a ubicar.
     * @param userId - ID del usuario que la va a ubicar.
     * @return Devuelve un boolean que indica si se ha podido ubicar la tropa o no.
     */
    public synchronized boolean locateTroop(Troop troop, int matchId, int userId) {
        String query = "update matches_troops set rowsLocation=" + troop.getRowLocation() + ", colLocation=" + troop.getColLocation() + ", isLocated=1 where id_troop=" + troop.getId() + " and id_match=" + matchId + " and id_user=" + userId + " and isLocated=0;";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para atacar a una torre (jugador contrario)
     * @param troop - Tropa que está atacando a la torre
     * @param player - Torre / Jugador atacado
     * @return Devuelve al jugador una vez ha sido atacado.
     */
    public synchronized Player attackTower(Troop troop, Player player) {
        int minus;
        minus = player.getVitalityPoints() - troop.getDamage();
        if (minus < 0) {
            minus = 0;
        }
        String query = "update players set vitalityPoints=" + minus + " where id=" + player.getId() + ";";
        if (DBConnector.getInstance().updateQuery(query)) {
            player.setVitalityPoints(minus);
        }
        return player;
    }
}