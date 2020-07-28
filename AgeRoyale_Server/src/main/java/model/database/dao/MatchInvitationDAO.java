package model.database.dao;


import model.database.DBConnector;
import model.entity.MatchInvitation;
import model.entity.Message;
import model.entity.User;
import model.entity.UserMatchInvitation;
import model.enumeration.InvitationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Representa el Data Access Object que nos permite acceder a datos de invitaciones de partidas.
 */
public class MatchInvitationDAO {
    /**
     * Representa el UserDAO.
     */
    private UserDAO userDAO;

    /**
     * Crea el MatchInvitationDAO
     */
    public MatchInvitationDAO() {
        this.userDAO = new UserDAO();
    }

    /**
     * Método que sirve para registrar una invitacion de partida.
     * @param matchId - ID de partida
     * @param hostId - ID de jugador owner
     * @param guestId - ID de jugador invitado
     * @return Devuelve la invitación a la partida
     * @throws SQLException SQL Exception - No se puede registrar la invitación de partida (puede que ya exista anteriormente).
     */
    public synchronized MatchInvitation registerMatchInvitation(int matchId, int hostId, int guestId) throws SQLException {
        User host = userDAO.getUserInfoById(hostId);
        User guest = userDAO.getUserInfoById(guestId);
        MatchInvitation matchInvitationAux = new MatchInvitation(0,matchId, false, host, guest);
        return registerMatchInvitation(matchInvitationAux);
    }

    /**
     * Método que sirve para registrar una invitación  de partida.
     * @param matchInvitation - MatchInvitation que tiene datos de la invitación de partida.
     * @return Devuelve la invitación de la partida con los datos actualizados de la DB.
     * @throws SQLException SQL Exception - No se puede registrar la invitación de partida (puede que ya exista anteriormente).
     */
    public synchronized MatchInvitation registerMatchInvitation(MatchInvitation matchInvitation) throws SQLException {
        String query = "Insert into matchesinvitations (matchId, isAccepted,idUserHost,idUserGuest,status) values (" + matchInvitation.getMatchId() + ", "+ matchInvitation.isAccepted() + ", " + matchInvitation.getHost().getId() + ", " + matchInvitation.getGuest().getId() + ", 'Pending'" + ");";
        if (DBConnector.getInstance().insertQuery(query)) {
            return this.getLastMatchInvitation();
        }
        return null;
    }

    /**
     * Método que sirve para recuperar la última invitación de partida.
     * @return Devuelve una instancia de MatchInvitation con la última invitación de partida.
     * @throws SQLException SQL Exception - No se puede recupear la última invitación de partida (si no hay invitaciones).
     */
    private MatchInvitation getLastMatchInvitation() throws SQLException {
        String query = "Select id, matchId, isAccepted, idUserHost, idUserGuest, status from matchesinvitations as mi order by mi.id DESC LIMIT 1;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        resultSet.next();
        return getResultSet(resultSet);
    }

    /**
     * Método que sirve para obtener la última invitación de partida dado un usuario creador.
     * @param hostId - ID del usuario creador de la partida.
     * @return Devuelve la última invitación de partida para dicho usuario.
     */
    public MatchInvitation getLastMatchInvitationByUserId(int hostId) {
        try {
            String query = "Select id, matchId, isAccepted, idUserHost, idUserGuest, status from matchesinvitations as mi where mi.idUserHost=" + hostId + " order by mi.id DESC LIMIT 1;";
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            resultSet.next();
            return getResultSet(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método que srive para encontrar la última invitación de partida entre 2 jugadores (host y guest).
     * @param hostId - ID del usuario creador de la partida
     * @param guestId - ID del usuario invitado a la partida.
     * @return Devuelve la invitación a la partida.
     */
    public MatchInvitation getLastMatchInvitationByFriendsId(int hostId,int guestId) {
        try {
            String query = "Select id, matchId, isAccepted, idUserHost, idUserGuest, status from matchesinvitations as mi where mi.idUserHost=" + hostId + " and mi.idUserGuest="+guestId+" order by mi.id DESC LIMIT 1;";
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            resultSet.next();
            return getResultSet(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método que sirve para que el usuario host pueda recuperar las invitaciones de partidas que ha realizado.
     * @param uId - ID del usuario host.
     * @return Devuelve una instancia de UserMatchInvitation con las invitaciones de partidas que ha realizado.
     */
    public UserMatchInvitation readHostCreatedMatchInvitations(int uId) {
        UserMatchInvitation userMatchInvitation = new UserMatchInvitation();
        ArrayList<MatchInvitation> matchInvitations = new ArrayList<>();
        String query = "Select id, matchId, isAccepted, idUserHost, idUserGuest, status from matchesinvitations as mi where mi.idUserHost = " + uId + ";";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            while (resultSet.next()) {

                matchInvitations.add(getResultSet(resultSet));
            }
            if (matchInvitations.size()!=0){
                userMatchInvitation.setMatchInvitations(matchInvitations);
                userMatchInvitation.setMessage(new Message(0,"Match Invitations read successful!"));
            }
            else{
                userMatchInvitation.setMessage(new Message(1,"You don't have invitations!"));
            }
        } catch (SQLException e) {
            userMatchInvitation.setMessage(new Message(1,"JDBC EXCEPTION! We can't connect to DB!"));
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return userMatchInvitation;
    }

    /**
     * Método que sirve para responder a una invitación de partida, acceptándola o rechazándola.
     * @param matchInvitationId - ID de la invitación de partida.
     * @param userId - ID del usuario que accepta o rechaza la invitación de partida.
     * @param status - Status ('Accepted' o 'Refused').
     * @return Devuelve un boolean que indica si ha ido bien o no.
     */
    public synchronized boolean responseAcceptance(int matchInvitationId, int userId,String status) {
        String query = "update matchesinvitations set isAccepted=1, status='"+status+"', idUserGuest="+userId+" where matchId=" + matchInvitationId + ";";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para cancelar una invitación de partida.
     * @param matchInvitationId - ID de invitación de partida
     * @param userId - ID de usuario que cancela la partida que ha creado.
     * @return Devuelve un boolean que indica si ha ido bien o no. (si se ha podido cancelar)
     */
    public synchronized boolean cancelMathInvitation(int matchInvitationId, int userId) {
        String query = "update matchesinvitations set status='Refuse', idUserHost="+userId+" where matchId=" + matchInvitationId + ";";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para devolver una instancia de MatchInvitation con los datos obtenidos del registro de DB mediante resultset.
     * @param resultSet - Resultset de la invitación de la partida
     * @return Devuelve un objeto de MatchInvitation con los datos de invitación de la partida.
     * @throws SQLException SQL Exception - Alguna parte del registro que se ha pasado o el registro entero no tiene valor (null).
     */
    private MatchInvitation getResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        boolean isAccepted = resultSet.getInt("isAccepted") == 1;
        int idUserHost = resultSet.getInt("idUserHost");
        int matchId = resultSet.getInt("matchId");
        int idUserGuest = resultSet.getInt("idUserGuest");
        String matchStatusText = resultSet.getString("status");
        InvitationStatus matchInvitationStatus = null;
        if (matchStatusText.equals("Pending")) {
            matchInvitationStatus = InvitationStatus.Pending;
        } else if (matchStatusText.equals("Refuse")) {
            matchInvitationStatus = InvitationStatus.Refuse;
        } else {
            matchInvitationStatus = InvitationStatus.Accepted;
        }
        User userHost = userDAO.getUserInfoById(idUserHost);
        User userGuest = userDAO.getUserInfoById(idUserGuest);
        MatchInvitation matchInvitation = new MatchInvitation(id, isAccepted, userHost, userGuest);
        matchInvitation.setMatchId(matchId);
        matchInvitation.setStatus(matchInvitationStatus);
        return matchInvitation;
    }
}