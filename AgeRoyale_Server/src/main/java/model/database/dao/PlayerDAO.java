package model.database.dao;

import model.database.DBConnector;
import model.entity.*;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Representa el Data Access Object que nos permite acceder a los datos de jugadores.
 */
public class PlayerDAO {

    /**
     * Método que sirve para registrar a un jugador.
     * @param pl - Jugador que se desea registrar.
     * @return Devuelve el jugador con su información recién insertada en la DB.
     */
    public synchronized Player insertPlayer(Player pl) {
        String query = "INSERT INTO players(userId,userName,vitalityPoints,availableMoney) VALUES (" + pl.getUserId() + ", '" + pl.getUsername() + "' ," + pl.getVitalityPoints() + ", " + pl.getAvailableMoney() + ");";
        DBConnector.getInstance().insertQuery(query);
        return showPlayerInfoByName(pl.getUsername());
    }

    /**
     * Método que sirve para recuperar la información de un jugador por id de jugador.
     * @param id - ID de jugador
     * @return Devuelve el jugador que se quería obtener.
     */
    public Player showPlayerInfoById(int id) {
        String query = "Select id, userId, userName, vitalityPoints, availableMoney from players as pl where pl.id=" + id + " ORDER BY id DESC LIMIT 1;";
        Player player = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                player = getResultSet(resultSet);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            player = new Player();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return player;
    }

    /**
     * Método que sirve para recuperar la información de un jugador por id de usuario.
     * @param id - ID de usuario
     * @return Devuelve el jugador que se quería obtener.
     */
    public Player showPlayerInfoByUserId(int id) {
        String query = "Select id, userId, userName, vitalityPoints, availableMoney from players as pl where pl.userId=" + id + " ORDER BY id DESC LIMIT 1;";
        Player player = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                player = getResultSet(resultSet);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            player = new Player();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return player;
    }

    /**
     * Método que sirve para recuperar la información de un jugador por nombre de usuario.
     * @param name - Nombre del usuario
     * @return Devuelve el jugador que se quería obtener.
     */
    public Player showPlayerInfoByName(String name) {
        String query = "Select id, userId, userName, vitalityPoints, availableMoney from players as pl where pl.userName='" + name + "' ORDER BY id DESC LIMIT 1;";
        Player player = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                player = getResultSet(resultSet);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            player = new Player();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return player;
    }

    /**
     * Método que sirve para recuperar la información de un jugador por nombre de usuario y nombre de partida.
     * @param name -  Nombre del usuario
     * @param matchName - nombre de la partida
     * @return Devuelve el jugador que se quería obtener.
     */
    public Player showPlayerInfoByNameAndMatchName(String name, String matchName) {
        String query = "Select pl.id, userId, userName, vitalityPoints, availableMoney from players as pl LEFT JOIN matches as m on (pl.id=m.hostId or pl.id=m.guestId) where pl.userName='" + name + "' and m.matchName='" + matchName + "' ORDER BY id DESC LIMIT 1;";
        Player player = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                player = getResultSet(resultSet);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            player = new Player();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return player;
    }

    /**
     * Método que sirve para recuperar la información de un jugador mediante resultset.
     * @param resultSet - Recibe el resultset del registro del jugador del cual se quiere obtener la información.
     * @return Devuelve una instancia de jugador con los datos de jugador cargados de la DB.
     * @throws SQLException - Error de SQL, puede porque no exista el usuario o haya habido algun error en la DB.
     */

    private Player getResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String userName = resultSet.getString("userName");
        int vitalityPoints = resultSet.getInt("vitalityPoints");
        int availableMoney = resultSet.getInt("availableMoney");
        return new Player(id.intValue(), userName, vitalityPoints, availableMoney);
    }

    /**
     * Método que sirve para reducir el dinero que tiene el jugador dado el jugador y un coste de tropa.
     * @param pl - Jugador
     * @param cost - Coste de la tropa que se va a decrementar (que quiere comprar el jugador)
     * @return Devuelve un boolean con true o false de si se ha podido o no reducir el dinero y por tanto comprado la tropa.
     */
    public  synchronized boolean reduceMoney(Player pl, Integer cost) {
        int minusCost = pl.getAvailableMoney() - cost;
        String query = "update players set availableMoney=" + minusCost + " where id=" + pl.getId() + ";";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para poder incrementar el dinero de un jugador cuando mata una tropa del enemigo.
     * @param id - Id del jugdor
     * @param attackedTroop - Tropa atacada
     * @return Devuelve un boolean con true o false de si se ha podido o no incrementar el dinero y por tanto sumado el coste de la tropa que ha matado
     */
    public synchronized boolean incrementMoneyByKillingTroop(Integer id, Troop attackedTroop) {
        String query = "update players set availableMoney=availableMoney+" + (attackedTroop.getCost() / 2) + " where id=" + id + ";";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para aumentar el dinero de un jugador pasivamente.
     * @param id - Id del jugador a aumentar el dinero.
     */
    public synchronized void incrementPeriodicMoney(Integer id) {
        String query = "update players set availableMoney=availableMoney+10 where id=" + id + ";";
       DBConnector.getInstance().updateQuery(query);
    }
}