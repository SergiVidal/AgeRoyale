package model.database.dao;

import model.database.DBConnector;
import model.entity.*;
import model.enumeration.MatchStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el Data Access Object que nos permite acceder a datos relacionados con partidas.
 */
public class MatchDAO {
    /**
     * Representa el PlayerDAO.
     */
    private PlayerDAO playerDAO;

    /* Constantes para identificar las acciones del usuario */
    private static final String GET_MATCH_LIST = "Get match list";
    private static final String CREATE_MATCH = "Create match";
    public static final String JOIN_MATCH = "Join match";
    public static final String VIEW_MATCH = "View match";
    private static final String CANCEL_MATCH = "Cancel match";
    /**/

    /**
     * Crea el MatchDAO.
     */
    public MatchDAO() {
        this.playerDAO = new PlayerDAO();
    }

    /**
     * Método que sirve para poder registrar una partida en la DB.
     * @param match Recibe la partida a registrar en la DB.
     * @return Devuelve una isntancia de UserMatch que indica si se ha podido insertar o no la partida en la DB, además de la lista de partidas públicas que hay actualmente.
     */
    public synchronized UserMatch registerMatch(Match match) {
        UserMatch userMatch = new UserMatch();
        int isPublicNum = -1;
        if (match.isPublic()) {
            isPublicNum = 1;
        } else {
            isPublicNum = 0;
        }
        String query = "";
        if (match.isPublic()) {
            query = "insert into matches (matchName, hostId, guestId, matchDate, isPublic, status) values ('" + match.getMatchName() + "', " + match.getPlHost().getId() + ", " + match.getPlHost().getId() + ", now(), " + isPublicNum + ", '" + match.getMatchStatus() + "');";
        } else {
            query = "insert into matches (matchName, hostId, guestId, matchDate, isPublic, status) values (concat('" + match.getMatchName() + "',now()), " + match.getPlHost().getId() + ", " + match.getPlGuest().getId() + ", now(), " + isPublicNum + ", '" + match.getMatchStatus() + "');";
        }
        if (DBConnector.getInstance().insertQuery(query)) {
            userMatch.setMessage(new Message(0, "Match Created successfully!"));
        } else {
            userMatch.setMessage(new Message(1, "Match can't be created!"));
        }
        userMatch.setMatches(getPublicMatchesData().getMatches());
        userMatch.setAction(CREATE_MATCH);
        userMatch.getMatches().addAll(getMyMatchesData(match.getPlHost().getId()).getMatches());
        return userMatch;
    }

    /**
     * Método que sirve para unirse a una partida mediante el id de jugador y el id de la partida a la que uno se quiere unir como guest.
     * @param plId  Recibe el id del jugador que será si va bien el jugador guest de la partida.
     * @param matchId Recibe el id de la partida a la cual se quiere unir como jugador invitado / guest.
     * @return Devuelve una instancia de UserMatch que indica si se ha podido añadir o no a la partida, además de la lista de partidas públicas.
     */
    public synchronized UserMatch joinMatch(int plId, int matchId) {
        String query = null;
        UserMatch userMatch = new UserMatch();
        query = "update matches set guestId=" + plId + ", status='InProgress' where id=" + matchId + ";";
        if (DBConnector.getInstance().updateQuery(query)) {
            userMatch.setMessage(new Message(0, "Match has beeen joined as guest successfully!"));
        } else {
            userMatch.setMessage(new Message(1, "Match can't be joined as guest!"));
        }
        Match match = getLastMatchByUserId(plId);
        userMatch.setFriendName(playerDAO.showPlayerInfoById(match.getPlHost().getId()).getUsername());
        List<Match> matchList = new ArrayList<Match>();
        matchList.addAll(getPublicMatchesData().getMatches());
        userMatch.setAction(JOIN_MATCH);
// matchList.addAll(getMyMatchesData(plId).getMatches());
        userMatch.setMatches(matchList);
        return userMatch;
    }

    /**
     * Método que sirve para unirse a la partida como espectador / viewer.
     * @param idUser Recibe el id del jugador del cual quiere unirse a la partida como espectador.
     * @param matchId Recibe el id de la partida a la cual el jugador quiere unirse como espectador.
     * @return Devuelve una instancia de UserMatch que contiene si se ha podido añadir como espectador o no e indica la lista de partidas públicas.
     */
    public synchronized UserMatch viewMatch(int idUser, int matchId) {
        String query = null;
        UserMatch userMatch = new UserMatch();
        query = "insert into matches_spectators (id_match, id_user) values (" + matchId + ", " + idUser + ");";
        if (DBConnector.getInstance().insertQuery(query)) {
            userMatch.setMessage(new Message(0, "Match has beeen joined as viewer successfully!"));
        } else {
            userMatch.setMessage(new Message(1, "Match can't be joined as viewer!"));
        }
        List<Match> matchList = new ArrayList<Match>();
        matchList.addAll(getPublicMatchesData().getMatches());
        userMatch.setAction(VIEW_MATCH);
// matchList.addAll(getMyMatchesData(plId).getMatches());
        userMatch.setMatches(matchList);
        return userMatch;
    }


    /**
     * Método que sirve para recuperar la lista de partidas públicas.
     * @return Devuelve una instancia de UserMatch que devuelve si se ha podido recuperar o no la lista de partidas públicas, además, de que si ha ido bien, devuelve la lista de partidas públicas.
     */
    public UserMatch getPublicMatchesData() {
        UserMatch userMatch = new UserMatch();
        ArrayList<Match> matches = new ArrayList<>();
        StringBuilder str = new StringBuilder("select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.isPublic=1 and m.status<>'Finished' and m.status<>'Cancelled' AND m.id<>0 ORDER BY matchDate DESC");
        String query = str.toString();
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    matches.add(getResultSet(resultSet));
                }
                userMatch.setMatches(matches);
                if (matches.size() == 0) {
                    userMatch.setMessage(new Message(1, "Any Match Available!"));
                } else {
                    userMatch.setMessage(new Message(0, "Match list getted successfully!"));
                }
            } catch (SQLException e) {
                userMatch.setMessage(new Message(1, "JDBC Exception!"));
                e.printStackTrace();
                DBConnector.getInstance().disconnect();
                DBConnector.getInstance().resetValues();
            }
        } else {
            userMatch.setMatches(new ArrayList<>());
            userMatch.setMessage(new Message(1, "No matches available!!!"));
        }
        userMatch.setAction(GET_MATCH_LIST);
        return userMatch;
    }

    /**
     * Método que sirve para recuperar las partidas que ha jugado siendo host o guest.
     * @param plId Recibe como parámetro el id del jugador del cual se quieren obtener sus partidas.
     * @return Devuelve una instancia de UserMatch que indica si se han podido recuperar o no, y cuando es afirmativo, además la lista de partidas del jugador.
     */
    public UserMatch getMyMatchesData(int plId) {
        UserMatch userMatch = new UserMatch();
        ArrayList<Match> matches = new ArrayList<>();
        String query = "select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.hostId=" + plId + " or m.guestId=" + plId + " and m.id<>0;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    matches.add(getResultSet(resultSet));
                }
                userMatch.setMatches(matches);
                if (matches.size() == 0) {
                    userMatch.setMessage(new Message(1, "Any Match Available!"));
                } else {
                    userMatch.setMessage(new Message(0, "Match list getted successfully!"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DBConnector.getInstance().disconnect();
                DBConnector.getInstance().resetValues();
            }
        } else {
            userMatch.setMatches(new ArrayList<>());
            userMatch.setMessage(new Message(1, "Any Match Available!"));
        }
        return userMatch;
    }

    /**
     * Método que sirve para recuperar una partida por su nombre de partida.
     * @param matchName Recibe como parámetro el nombre de la partida de la cual se quiere obtener su información.
     * @return Devuelve una instancia / objeto de Match con la partida recuperada de la DB (con sus datos).
     */
    public Match getMatchByName(String matchName) {
        Match match = new Match();

        String query = "select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.matchName='" + matchName + "';";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            if (resultSet.next()) {
                match = getResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return match;
    }

    /**
     * Método que sirve para recuperar una partida por su id de partida.
     * @param id Recibe como parámetro el id de la partida de la cual se quiere obtener su información.
     * @return Devuelve una instancia / objeto de Match con la partida recuperada de la DB (con sus datos).
     */
    public Match getMatchById(Integer id) {
        Match match = new Match();

        String query = "select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.id=" + id + ";";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            if (resultSet.next()) {
                match = getResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return match;
    }

    /**
     * Método que sirve para poder recuperar un objeto Match / instancia de Match con los datos que se han obtenido del registro en concreto de la db mediante resultset.
     * @param resultSet Recibe el resultset con la información recuperada para aquel registro de la tabla matches de la DB.
     * @return Devuelve una instancia de Match con los datos de la partida.
     */
    private Match getResultSet(ResultSet resultSet) {
        try {
            Integer id = resultSet.getInt("id");
            String date = "";
            String matchName = resultSet.getString("matchName");
            int hostId = resultSet.getInt("hostId");
            int guestId = resultSet.getInt("guestId");
            String actualTime = resultSet.getString("matchDate");
            String parts[] = actualTime.split(" ");
            String dayParts[] = parts[0].split("-");
            String hourParts[] = parts[1].split(":");
            LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(dayParts[0]), Integer.parseInt(dayParts[1]), Integer.parseInt(dayParts[2]), Integer.parseInt(hourParts[0]), Integer.parseInt(hourParts[1]));
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Madrid"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            date = zonedDateTime.format(formatter);
            Player playerHost = null;
            if (hostId != 0) {
                playerHost = playerDAO.showPlayerInfoById(hostId);
            }
            Player playerGuest = null;
            if (guestId != 0) {
                playerGuest = playerDAO.showPlayerInfoById(guestId);
            }
            Player winner = null;
            boolean isPublic = resultSet.getInt("isPublic") == 1;
            String statusText = resultSet.getString("status");
            MatchStatus matchStatus = null;
            if (statusText.equals("InProgress")) {
                matchStatus = MatchStatus.InProgress;
            } else if (statusText.equals("Finished")) {
                matchStatus = MatchStatus.Finished;
            } else {
                matchStatus = MatchStatus.Pending;
            }
            int winnerId = resultSet.getInt("winner");
            if (winnerId != 0) {
                winner = playerDAO.showPlayerInfoById(winnerId);
            }
            int matchTime = resultSet.getInt("matchTime");
// public Match(Integer id, String matchName, LocalDate matchDate, boolean isPublic, MatchStatus matchStatus, ZonedDateTime zonedDateTime, Player plHost, Player plGuest) {
            return new Match(id, matchName, date, isPublic, matchStatus, matchTime, playerHost, playerGuest, winner, 20, 10);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método que sirve para recuperar la última invitación a una partida privada entre 2 usuarios.
     * @param id Recibe el id del jugador host del cual se quiere obtener la última invitación.
     * @param id1 Recibe el id del jugador guest del cual se quiere obtener la última invitación.
     * @return Devuelve la última invitación de partida privada entre 2 jugadores.
     */
    public Match getLastInvitations(Integer id, Integer id1) {
        Match match = new Match();
        String query = "select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.hostId=" + id + " and m.guestId=" + id1 + " ORDER by m.id DESC LIMIT 1;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            if (resultSet.next()) {
                match = getResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return match;
    }

    /**
     * Método que sirve para recuperar la última partida por id de usuario.
     * @param userId Recibe como parámetro el id de usuario del cual se quiere obtener la última partida.
     * @return Devuelve la última partida de dicho usuario.
     */
    public Match getLastMatchByUserId(int userId) {
        Match match = null;
        String query = "select id, matchName, hostId, guestId, matchDate, isPublic, status, winner, matchTime from matches as m where m.hostId=" + userId + " or m.guestId=" + userId + " ORDER by m.id DESC LIMIT 1;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            if (resultSet.next()) {
                match = getResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return match;
    }

    /**
     * Método que sirve para cancelar una partida.
     * @param id Recibe el id de la partida que se quiere cancelar.
     * @return Devuelve una instancia de UserMatchInvitation que indica si se ha podido o no cancelar una partida.
     */
    public synchronized UserMatchInvitation cancelMatch(int id) {
        UserMatchInvitation userMatchInvitation = new UserMatchInvitation();
        userMatchInvitation.setAction(CANCEL_MATCH);
        Match match = getLastMatchByUserId(id);
        if (match != null) {
            String query = "update matches set status='Cancelled' where id=" + match.getId() + ";";
            if (DBConnector.getInstance().updateQuery(query)) {
                userMatchInvitation.setMessage(new Message(0, "Match has been cancelled successfully!"));
            } else {
                userMatchInvitation.setMessage(new Message(1, "Match hasn't been cancelled successfully!"));
            }
        } else {
            userMatchInvitation.setMessage(new Message(2, "It has occur a server error!"));
        }
        return userMatchInvitation;
    }

    /**
     * Método que sirve para actualizar el jugador ganador de una partida.
     * @param winnerId Recibe el id de jugador de una partia.
     * @param matchId Recibe como parámetro el id de la partida de la cual se quiere añadir el ganador.
     * @param matchTime Recibe el tiempo que ha durado la partida.
     */
    public synchronized void updateWinner(Integer winnerId, Integer matchId,Integer matchTime) {
        String query = "update matches set winner=" + winnerId + ", status='Finished', matchTime="+matchTime+" where id=" + matchId + ";";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para finalizar un match por id de usuario.
     * @param uid Recibe como parámetro el id de usuario.
     */
    public  synchronized void finishMatch(int uid) {
        String query = "update matches set status='Finished' where hostId in (select players.id from players,users where users.id=players.userId and users.id=" + uid + ");";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método para recuperar el MatchStatItem dado los datos que vienen del registro en la DB con ResultSet.
     * @param resultSet - Representa el ResultSet con los datos de la estadística de la partida.
     * @param filter - Filtro de modo de estadística por tiempo.
     * @return Devuelve el MatchStatItem.
     */
    public MatchStatItem getStatResultSet(ResultSet resultSet,String filter){
        MatchStatItem matchStatItem = new MatchStatItem();
        int timeId;
        int matchesCount;
        int lastDay;
        try {
            if (filter.equals("year")) {
                timeId = resultSet.getInt("yearMonth");
                matchesCount = resultSet.getInt("matchNum");
            } else if (filter.equals("month")) {
                timeId = resultSet.getInt("dayd");
                lastDay = resultSet.getInt("last_month_day");
                matchesCount = resultSet.getInt("matchNum");
                matchStatItem.setLastDay(lastDay);
            } else {
                timeId = resultSet.getInt("day");
                matchesCount = resultSet.getInt("matchNum");
            }
            matchStatItem.setTimeId(timeId);
            matchStatItem.setMatchesCount(matchesCount);
        } catch (SQLException e){
            return new MatchStatItem();
        }
        return matchStatItem;
    }

    /**
     * Método que sirve para la obtención de las evoluciones de las partidas por filtro (day,month,year).
     * @param filter - Representa el filtro por el cual se obtiene la evolución de las partidas.
     * @return Devuelve las estadísticas de partidas.
     */
    public MatchStat getMatchEvolutionDataByMonthOrYear(String filter){
        MatchStat matchStat = new MatchStat();
        String query = "";
        if (filter.equals("year")){
            query = "SELECT month(matchDate) as yearMonth, count(id) as matchNum FROM matches group by month(matchDate) ORDER BY month(matchDate);";
        } else if (filter.equals("month")) {
            query = "select count(id) as matchNum, DAY(DATE_ADD(matchDate, INTERVAL(1-DAYOFWEEK(matchDate)) DAY)) as dayd,week(((DATE(matchDate)))) as day, DAY(last_day(matchDate)) as last_month_day from matches where month(matchDate)=5 group by week(((DATE(matchDate)))) ,last_month_day,DAY(DATE_ADD(matchDate, INTERVAL(1-DAYOFWEEK(matchDate)) DAY))  ORDER BY day;";
            //select count(id) as d,(SUBDATE(DATE(matchDate), WEEKDAY(matchDate)))  from matches where month(matchDate)=5 group by (SUBDATE(DATE(matchDate), WEEKDAY(matchDate)))
            // query = "SELECT year(matchDate) as year, count(id) as matchNum FROM  matches group by year(matchDate);";
        } else {
            //SELECT distinct(weekday(matchDate)) as dayMatch, week(matchDate) as matchWeek, count(id) as matchNum, DAY(DATE(matchDate)), week(now()) as actual FROM matches where  week(matchDate)=(week(now())-1) group by id
            query = "SELECT weekday(matchDate) as day, week(matchDate,1) as matchWeek, count(id) as matchNum, week(now(),1) as actual FROM matches group by weekday(matchDate), matchWeek having matchWeek = actual-1 ORDER BY weekday(matchDate);";
        }
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    matchStat.getMatchStatItems().add(getStatResultSet(resultSet,filter));
                }
                if (matchStat.getMatchStatItems().size() == 0) {
                    matchStat.setMessage(new Message(1, "Any Match Available!"));
                } else {
                    matchStat.setMessage(new Message(0, "MatchStat list getted successfully!"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DBConnector.getInstance().disconnect();
                DBConnector.getInstance().resetValues();
            }
        } else {
            matchStat.setMatchStatItems(new ArrayList<>());
            matchStat.setMessage(new Message(1, "Any Match Available!"));
        }
        return matchStat;
    }
}