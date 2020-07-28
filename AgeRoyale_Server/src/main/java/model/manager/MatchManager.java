package model.manager;

import model.database.dao.MatchDAO;
import model.entity.*;
import model.enumeration.MatchStatus;
import model.network.DedicatedServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el MatchManager, que es el modelo que nos permite acceder a métodos de MatchDAO y guarda la lista de partidas.
 */
public class MatchManager {
    /**
     * Representa el MatchDAO.
     */
    private MatchDAO matchDAO;
    /**
     * Representa la lista de partidas.
     */
    private List<Match> matches;
    /* Constantes para identificar las acciones del usuario */
    public static final String JOIN_MATCH = "Join match";
    public static final String VIEW_MATCH = "View match";
    private static final String GET_GAME_INFO = "Get game info";
    public static final String HOST = "Host";
    public static final String GUEST = "Guest";
    public static final String VIEWER = "Viewer";
    /**/

    /**
     * Crea el MatchManager.
     */
    public MatchManager() {
        this.matchDAO = new MatchDAO();
        this.matches = new ArrayList<>();
    }

    /**
     * Método que sirve para añadir una partida que se va a jugar.
     * @param match Recibe como parámetro la partida a jugar.
     * @return Devuelve una instancia de UserMatch con la información de la partida, la acción para poder añadir partida, y el mensaje con status de registro.
     */
    public UserMatch addMatch(Match match) {
        UserMatch userMatch = matchDAO.registerMatch(match);
        if (userMatch != null) {
            return userMatch;
        } else
            return new UserMatch(new ArrayList<Match>(), new Message(1, "JDBC Exception!Service unavaliable!"));
    }

    /**
     * Método que sirve para unirse a una partida.
     * @param plId Recibe como parámetro el id del jugador (guest) que se quiere unir a la partida.
     * @param matchId Recibe como parámetro el id de la partida a la cual se quiere unir.
     * @return Devuelve una instancia de UserMatch con la información de la partida, el status a InProgress y los 2 jugadores (Host y Guest).
     */
    public UserMatch joinMatch(int plId, int matchId) {
        Match match = matchDAO.getMatchById(matchId);
        UserMatch userMatch = null;
        if (match != null && !(match.getMatchStatus().equals(MatchStatus.Finished))) {
            userMatch = matchDAO.joinMatch(plId, matchId);
            if (!match.isPublic()) {
                userMatch.setMatchName(match.getMatchName());
                userMatch.setUsername(match.getPlHost().getUsername());
                userMatch.setFriendName(match.getPlGuest().getUsername());
            }
            userMatch.setMessage(new Message(0, "Has been joined successfully!"));
            userMatch.setAction(JOIN_MATCH);
            return userMatch;

        } else {
            ArrayList<Match> matches = new ArrayList<>();
            matches.addAll(matchDAO.getPublicMatchesData().getMatches());
            matches.addAll(matchDAO.getMyMatchesData(plId).getMatches());
        }
        return new UserMatch(matches, new Message(1, "DB ERROR!"));
    }

    /**
     * Método que sirve para visualizar una partida como espectador.
     * @param idUser  Recibe como parámetro el id de usuario del espectador.
     * @param matchId Recibe como paráemtro el id de la partida de la cual se quiere ser espectador.
     * @return Devuelve una instancia de UserMatch con la información de los jugadores, la partida y el mensaje de si se ha podido añadir como espectador.
     */
    public UserMatch viewMatch(int idUser, int matchId) {
        Match match = matchDAO.getMatchById(matchId);

        UserMatch userMatch = null;
        if (match != null && !(match.getMatchStatus().equals(MatchStatus.Finished))) {
            userMatch = matchDAO.viewMatch(idUser, matchId);
            userMatch.setMessage(new Message(0, "Has been joined as viewer successfully!"));
            userMatch.setAction(VIEW_MATCH);
            return userMatch;

        } else {
            ArrayList<Match> matches = new ArrayList<>();
            matches.addAll(matchDAO.getPublicMatchesData().getMatches());
            matches.addAll(matchDAO.getMyMatchesData(idUser).getMatches());
        }
        return new UserMatch(matches, new Message(1, "DB ERROR!"));
    }

    /**
     * Método que sirve para obtener todas las partidas que sean públicas.
     * @return Devuelve una instancia de UserMatch con la lista de partidas disponibles (Pending y públicas).
     */
    public UserMatch getPublicMatches() {
        return matchDAO.getPublicMatchesData();
    }

    /**
     * Método que sirve para obtener una partida por nombre de partida.
     * @param matchName Recibe el nombre de la partida de la cual se quiere obtener sus datos.
     * @return Devuelve la partida obtenida.
     */
    public Match getMatchByName(String matchName) {
        return matchDAO.getMatchByName(matchName);
    }

    /**
     * Método que sirve para obtener la partida dado la última invitación a una partida dado el id del jugador Host y el id del jugador Guest.
     * @param hostId Recibe el id del jugador Host
     * @param guestId Recibe el id del jugador Guest.
     * @return Devuelve el match obtenido.
     */
    public Match getLastMatchByInvitation(Integer hostId, Integer guestId) {
        return matchDAO.getLastInvitations(hostId, guestId);
    }

    /**
     * Método que sirve para obtener la última partida dado el id de jugador.
     * @param id Recibe como parámetro el id del jugador del cual se quiere obtener la última partida disponbile.
     * @return Devuelve la partida obtenida.
     */
    public Match getLastMatchByUserId(int id) {
        return matchDAO.getLastMatchByUserId(id);
    }

    /**
     * Método que sirve para obtener la última invitación de la partida cancelada (sirve para cancelar una partida).
     * @param id Recibe el id de la partida a cancelar.
     * @return Devuelve una instancia de UserMatchInvitation con la información de la partida cancelada y si es de invitacion con inof de invitacion).
     */
    public UserMatchInvitation cancelMatch(int id) {
        UserMatchInvitation userMatchInvitation = matchDAO.cancelMatch(id);
        userMatchInvitation.setAction(DedicatedServer.CANCEL_MATCH);
        return userMatchInvitation;
    }

    /**
     * Método que sirve para obtener la información de juego dado un nombre de partida y el nombre del jugador.
     * @param matchName Recibe como parámetro el nombre de la partida de la que obtener información del juego.
     * @param username Recibe como parámetro el nombre del jugador para poder obtener información del juego.
     * @return Devuelve un Game con la informacion actualizada
     */
    public Game getGameInfo(String matchName, String username) {
        Game game = new Game();
        game.setUsername(username);
        Match match = getMatchByName(matchName);
        if (match != null) {
            if (match.getPlHost().getUsername().equals(username)) {
                game.setRol(HOST);
            } else if (match.getPlGuest().getUsername().equals(username)) {
                game.setRol(GUEST);
            } else {
                game.setRol(VIEWER);
            }
            game.setMatch(match);
            game.setMessage(new Message(0, "Game get it successfully!"));
        } else {
            game.setMessage(new Message(1, "Game can't be getted"));
        }
        game.setAction(GET_GAME_INFO);
        return game;
    }

    /**
     * Método que sirve para actualizar la información de una partida,marcarla como finalizada y poner el ganador.
     * @param playerWinner Recibe el jugador que es el ganador de la partida.
     * @param matchId Recibe el id del match del cual se quiere actualizar la información.
     * @param matchTime Recibe el tiempo que ha durado la partida.
     */
    public void updateWinner(Player playerWinner, Integer matchId,Integer matchTime) {
        matchDAO.updateWinner(playerWinner.getId(), matchId,matchTime);
    }

    /**
     * Método que sirve para modificar el status de la partida y ponerlo en Finished.
     * @param hostId Recibe como parámetro el id del jugador host para finalizar su última partida.
     */
    public void finishGame(int hostId) {
        matchDAO.finishMatch(hostId);
    }

    /**
     * Método que sirve para recuperar las evoluciones de las partidas dado un filtro de por dia, mes o año.
     * @param filter - Representa el filtro.
     * @return Devuelve las estadisticas de evoluciones Match.
     */
    public MatchStat getMatchEvolutionDataByMonthOrYear(String filter){
        return matchDAO.getMatchEvolutionDataByMonthOrYear(filter);
    }
    @Override
    public String toString() {
        return "MatchManager{" +
                "matchDAO=" + matchDAO +
                ", matches=" + matches +
                '}';
    }
}