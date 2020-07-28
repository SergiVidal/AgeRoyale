package model.manager;

import model.database.dao.MatchInvitationDAO;
import model.entity.MatchInvitation;
import model.entity.UserMatchInvitation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la clase que nos permite llamar a métodos del DAO y poder guardar invitaciones de partidas
 */
public class MatchInvitationManager {
    /**
     * Representa el MatchInvitationDAO.
     */
    private MatchInvitationDAO matchInvitationDAO;
    /**
     * Representa la lista de invitaciones de partidas.
     */
    private List<MatchInvitation> matchInvitations;
    /* Constantes para identificar las acciones del usuario */
    public static final String ACCEPT_MATCH_INVITATION = "Accepted";
    public static final String REFUSE_MATCH_INVITATION = "Refuse";
    /**/

    /**
     * Crea el MatchInvitationManager.
     */
    public MatchInvitationManager() {
        this.matchInvitationDAO = new MatchInvitationDAO();
        this.matchInvitations = new ArrayList<>();
    }

    /**
     * Método que sirve para poder crear invitaciones de partidas.
     * @param matchId - Id de la partida
     * @param hostId - Id del jugador host
     * @param guestId - Id del jugador guest
     * @return Devuelve la invitación de la partida que hace el jugador host al jugador guest con los datos obtenidos de db.
     * @throws SQLException - SQL Exception
     */
    public MatchInvitation createMatchInvitation(int matchId, int hostId, int guestId) throws SQLException {
        MatchInvitation matchInvitation = matchInvitationDAO.registerMatchInvitation(matchId, hostId, guestId);
        if (matchInvitation.getHost().getId() != 0 && !(matchInvitation.getHost().getUsername().equals(""))) {
            addMatchInvitation(matchInvitation);
            return matchInvitation;
        }
        return null;
    }

    /**
     * Método que sirve para añadir una invitación de partida dentro de la lista de invitaciones.
     * @param matchInvitation Representa la MatchInvitation realizada
     */
    private void addMatchInvitation(MatchInvitation matchInvitation) {
        matchInvitations.add(matchInvitation);
    }

    /**
     * Método que sirve para acceptar una invitación a una partida dado el id de la partida / match y el id del usuario que la accepta (guest).
     * @param matchInvitationId - Id de la invitación de la partida
     * @param userId - Id del usuario
     * @return Devuelve un booleano (true o false) que indica si se ha podido acceptar o no correctamente respectivamente.
     */
    public boolean acceptMatchInvitation(int matchInvitationId, int userId) {
        return matchInvitationDAO.responseAcceptance(matchInvitationId, userId, ACCEPT_MATCH_INVITATION);
    }

    /**
     * Método que sirve para rechazar una invitación a una partida dado el id de la partida / match y el id del usuario que la rechaza (guest).
     * @param matchInvitationId - Id de la invitación de la partida
     * @param userId - Id del usuario
     * @return Devuelve un booleano (true o false) que indica si se ha podido rechazar o no correctamente respectivamente.
     */
    public boolean refuseMatchInvitation(int matchInvitationId, int userId) {
        return matchInvitationDAO.responseAcceptance(matchInvitationId, userId, REFUSE_MATCH_INVITATION);
    }

    /**
     * Método que sirve para recuperar las invitaciones a partidas creadas por el usuario que lo solicita.
     * @param hostId - Recibe el id del usuario creador de las invitaciones a partidas.
     * @return Devuelve una instancia de UserMatchInvitation / un objeto de dicha clase, que devuelve si hay partidas creadas por él o no y en caso que si, las partidas creadas por el usuario Host.
     */
    public UserMatchInvitation getHostCreatedMatchInvitation(int hostId) {
        return matchInvitationDAO.readHostCreatedMatchInvitations(hostId);
    }

    /**
     * Método que sirve para cancelar una invitación a una partida. (La cancela el usuario Host).
     * @param matchinvitationId - Id de la invitación a partida
     * @param userId - Id del usuario que crea la partida y que la quiere cancelar.
     */
    public void cancelMatchInvitation(int matchinvitationId, int userId) {
        matchInvitationDAO.cancelMathInvitation(matchinvitationId, userId);
    }

    /**
     * Método que sirve para recuperar la última invitación a partida hecha por el usuario host.
     * @param hostId - Id del usuario host
     * @return En caso de que haya, recuperará una instancia de MatchInvitation / objeto con la información de la última MatchInvitation hecha por el usuario host.
     */
    public MatchInvitation getLastMatchInvitationByUserId(int hostId) {
        return matchInvitationDAO.getLastMatchInvitationByUserId(hostId);
    }

    /**
     * Método que sirve para recuperar la última invitación a partida hecha entre 2 amig@s.
     * @param userId - Id del usuario Host
     * @param friendId - Id del usuario Guest
     * @return En caso de que haya, recuperará una instancia de MatchInvitation / objeto con la información de la última MatchInvitation hecha por el usuario host.
     */
    public MatchInvitation getLastMatchInvitationByFriendsId(int userId, int friendId) {
        return matchInvitationDAO.getLastMatchInvitationByFriendsId(userId, friendId);
    }
}
