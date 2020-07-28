package model.database.dao;

import model.database.DBConnector;
import model.entity.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Representa el Data Access Object que nos permite acceder a los datos del usuario.
 */
public class UserDAO {
    /* Constantes para identificar las acciones del usuario */
    private static final String GET_FRIEND_LIST = "Get friend list";
    private static final String DELETE_FRIEND = "Delete friend";
    /**/

    /**
     * Método que sirve para añadir un usuario.
     * @param u - Usuario a añadir.
     * @return Devuelve una instancia de UserLogin con el mensaje de si se ha podido añadir o no el usuario, además del usuario.
     */
    public synchronized UserLogin addUser(User u) {
        String query = "INSERT INTO users(username, password, email, isConnected) VALUES ('" + u.getUsername() + "', '" + u.getPassword() + "', '" + u.getEmail() + "', 1);";
        boolean isInserted = DBConnector.getInstance().insertQuery(query);
        if (isInserted) {
            return new UserLogin(findUserByName(u.getUsername()), new Message(0, "User Registered!"));
        }
        return new UserLogin(findUserByName(u.getUsername()), new Message(1, "This user already exists!"));
    }

    /**
     * Método que sirve para buscar un usuario por nombre.
     * @param username - Nombre del usuario por el cual se buscará todos los datos de dicho usuario.
     * @return Devuelve el usuario filtrado por nombre.
     */
    public User findUserByName(String username) {
        String query = "Select id, username, password, email, isConnected, isBanned, wins, loses from users as u where u.username='" + username + "'";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        User user = null;
        try {
            if (resultSet.next()) {
                user = (User) getResultSet(resultSet, "user");
                user.setPassword(null);
            }

        } catch (SQLException | NullPointerException e) {
            System.out.println("JDBC EXCEPTION! We can't connect to DB!");
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return user;
    }

    /**
     * Método que sirve para buscar un usuario por nombre o correo electrónico..
     * @param login - Nombre o Email del usuario por el cual se buscará todos los datos de dicho usuario.
     * @return Devuelve el usuario filtrado por login.
     */
    public User findUserByLogin(String login) {
        String query = "Select id, username, password, email, isConnected, isBanned, wins, loses from users as u where u.username='" + login + "' or u.email='"+login+"';";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        User user = null;
        try {
            if (resultSet.next()) {
                user = (User) getResultSet(resultSet, "user");
                user.setPassword(null);
            }

        } catch (SQLException | NullPointerException e) {
            System.out.println("JDBC EXCEPTION! We can't connect to DB!");
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return user;
    }

    /**
     * Método que permite hacer la cerrada de sessión del usuario.
     * @param login - Login del usuario (será el nombre o el mail).
     */
    public synchronized void logoutUser(String login) {
        String query = "update users set isConnected=0 where username='" + login + "';";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para hacer el baneamiento del usuario si ha salido en medio de una partida.
     * @param login - Login del usuario del cual se quiere banear.
     */
    public synchronized void banUser(String login) {
        String query = "update users set isConnected=0,isBanned=1,banDate=now() where username='" + login + "';";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para hacer el login del usuario
     * @param login - Nombre o mail de usuaroo
     * @param password - password del usuario
     * @return Devuelve una instancia de UserLogin con el usuario y el mensaje de si ha ido correctamente o no.
     */
    public synchronized UserLogin loginUser(String login, String password) {
        String query = "Select id, username, password, email, isConnected, isBanned, banDate, wins, loses from users as u where u.username='" + login + "'" + " or u.email='" + login + "'" + ";";
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                User user = (User) getResultSet(resultSet, "user");
                if (!(password.equals(user.getPassword()))) {
                    return new UserLogin(null, new Message(1, "Login Exists!Password not match with your user!"));
                }
                if (user.isBanned()) {
                    if (!isBannedFinished(user.getBanDate())) {
                        return new UserLogin(null, new Message(1, "User is banned!"));
                    } else {
                        query = "update users set isBanned=0, banDate=NULL where username='" + user.getUsername() + "';";
                        DBConnector.getInstance().updateQuery(query);
                    }
                }
                query = "update users set isConnected=1 where username='" + user.getUsername() + "';";
                DBConnector.getInstance().updateQuery(query);
                user.setConnected(true);
                return new UserLogin(user, new Message(0, "Login successful!"));
            } else {
                return new UserLogin(null, new Message(1, "Login doesn't exists on our system!"));

            }

        } catch (SQLException | NullPointerException e) {
            System.out.println("JDBC EXCEPTION! We can't connect to DB!");
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
            return new UserLogin(null, new Message(3, "This service is not available. We are going to fix!"));
        }

    }

    /**
     * Método que sirve para comprobar si el ban ya ha finalizado y el usuario se puede volver a conectar.
     * @param bannedTime - Tiempo del ban del usuario
     * @return Devuelve un boolean que indica en true si ya ha finalizado en ban y en false si aún no ha finalizado.
     */
    public  boolean isBannedFinished(LocalDateTime bannedTime) {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, bannedTime);
        long diff = Math.abs(duration.toHours());


        return diff >= 24;
    }

    /**
     * Método que sirve para crear una amistad entre 2 usuarios.
     * @param userId - Id de usuario que solicita amistad.
     * @param friendId - Id del usuario del cual se hace amigo.
     * @return Devuelve true o false si se ha podido realizar la relación de amistad o no.
     */
    public synchronized boolean createFriendShip(int userId, int friendId) {
        String query = "insert into friendships (user1,user2,friendshipDate) values (" + userId + " , " + friendId + " , now());";
        return DBConnector.getInstance().insertQuery(query);
    }

    /**
     * Método que sirve para recuperar la lista de amistades.
     * @param username - Nombre de usuario
     * @return Devuelve UserFriendship instance / object con si hay o no amistades y en caso que si con una lista de  usuarios con las que hay amistad.
     */
    public UserFriendship getFriendList(String username) {
        LinkedList<Friend> users = new LinkedList<>();
        int uid = findUserByLogin(username).getId();
        String query = "select users.*,friendships.* from users INNER join friendships on (users.id=friendships.user2 and friendships.user1=" + uid + ") or (users.id=friendships.user1 and friendships.user2=" + uid + ") where users.id <> 0;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        Friend friend = null;
        try {
            while (resultSet.next()) {
                friend = (Friend) getResultSet(resultSet, "friendship");
                friend.getUser().setPassword("");
                users.add(friend);
            }
            if (users.size() > 0) {
                return new UserFriendship(username, users, GET_FRIEND_LIST, new Message(0, "Friends Recovered Successfully"));
            } else {
                return new UserFriendship(username, users, GET_FRIEND_LIST, new Message(1, "You don't have any friend! We hope you get friends!"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return new UserFriendship(new Message(2, "Service unvaliable!!"));
    }

    /**
     * Método que sirve para eliminar a un amigo.
     * @param uid - ID de usuario
     * @param uid2 - Id de usuario de amigo
     * @return Devuelve una instancia de UserFriendship que indica si se ha podido o no, eliminar la amistad, más la información del usuario y el amgio.
     */
    public synchronized UserFriendship deleteFriendById(int uid, int uid2) {
        String query = "delete from friendships where (friendships.user1=" + uid + " and friendships.user2=" + uid2 + ") or (friendships.user1=" + uid2 + " and friendships.user2=" + uid + ");";
        UserFriendship userFriendship = getFriendList(getUserInfoById(uid).getUsername());
        if (DBConnector.getInstance().deleteQuery(query)) {
            userFriendship = getFriendList(getUserInfoById(uid).getUsername());
            userFriendship.setFriendName(getUserInfoById(uid2).getUsername());
            userFriendship.setMessage(new Message(0, "Remove has been done successfully!"));
        } else {
            userFriendship.setFriendName(getUserInfoById(uid2).getUsername());
            userFriendship.setMessage(new Message(1, "Remove hasn't been executed!"));
        }
        userFriendship.setAction(DELETE_FRIEND);
        return userFriendship;
    }

    /**
     * Método que sirve para recuperar la información de un usuario por su id.
     * @param id - ID de usuario del cual obtener información.
     * @return Devuelve el usuario con sus datos o vacío, en función de si encuentra o no su información.
     */
    public User getUserInfoById(int id) {
        String query = "Select id, username, password, email, isConnected, isBanned, wins, loses from users as u where u.id=" + id;
        User user = null;
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            if (resultSet.next()) {
                user = (User) getResultSet(resultSet, "user");
                user.setPassword(null);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
        }
        return user;
    }

    /**
     * Método que sirve para poder recuperar los datos de un usuario o de amistad (Friend) según el tipo especificado y el resultset con el registro de la DB.
     * @param resultSet - Resultset con los datos del registro en la DB.
     * @param type - Tipo que especifica si se ha recuperado un usuario o además, un amigo (contiene + datos).
     * @return
     */
    private Object getResultSet(ResultSet resultSet, String type) {
        try {
            int userId = resultSet.getInt("id");
            String login = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            boolean isConnected = resultSet.getInt("isConnected") == 1;
            boolean isBanned = resultSet.getInt("isBanned") == 1;
            String actualTime, dayParts[], hourParts[], parts[];
            LocalDateTime localDateTime = null;
            ZonedDateTime zonedDateTime = null;
            int wins = resultSet.getInt("wins");
            int loses = resultSet.getInt("loses");
            String date;
            if (type != null && type.equals("friendship")) {
                actualTime = resultSet.getString("friendshipDate");
                parts = actualTime.split(" ");
                dayParts = parts[0].split("-");
                hourParts = parts[1].split(":");
                localDateTime = LocalDateTime.of(Integer.parseInt(dayParts[0]), Integer.parseInt(dayParts[1]), Integer.parseInt(dayParts[2]), Integer.parseInt(hourParts[0]), Integer.parseInt(hourParts[1]));
                zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Madrid"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                date = zonedDateTime.format(formatter);
                return new Friend(new User(userId, login, password, email, wins, loses, isBanned, isConnected), date);
            }
            if (isBanned) {
                actualTime = resultSet.getString("banDate");
                parts = actualTime.split(" ");
                dayParts = parts[0].split("-");
                hourParts = parts[1].split(":");
                localDateTime = LocalDateTime.of(Integer.parseInt(dayParts[0]), Integer.parseInt(dayParts[1]), Integer.parseInt(dayParts[2]), Integer.parseInt(hourParts[0]), Integer.parseInt(hourParts[1]));
            }
            User user = new User(userId, login, password, email, wins, loses, isBanned, isConnected);
            user.setBanDate(localDateTime);
            return user;
        } catch (NullPointerException | SQLException e) {
            System.out.println("no s'ha pogut efectuar!");
        }
        return new User();
    }

    /**
     * Método que sirve para recuperar una amistad entre 2 usuarios.
     * @param id - ID de usuario
     * @param id2 - ID de usuario del cual se es amigo
     * @return Devuelve un boolean que indica si se es amigo o bien no lo es.
     */
    public boolean getFriendShip(int id, int id2) {
        String query = "SELECT * from users,friendships as fs where users.id=fs.user1 and ((fs.user1=" + id + " and fs.user2=" + id2 + ") or (fs.user1=" + id2 + " and fs.user2=" + id + "));";
        try {
            ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
            resultSet.next();
            Object obj = getResultSet(resultSet, "friendship");
            Friend friend;
            if (obj instanceof Friend) {
                friend = (Friend) obj;
                if (friend.getUser().getUsername() != null) {
                    return true;
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Amistat no existeix!");
        }
        return false;
    }

    /**
     * Método que nos permite recuperar los espectadores que hay en una partida.
     * @param matchId - ID de la partida / match
     * @return Recupera una lista de usuarios que sean espectadores de dicha partida.En el caso de que no haya, se devuelve vacía
     */
    public ArrayList<User> getSpectatorsByMatchId(int matchId) {
        ArrayList<User> viewers = new ArrayList<>();
        String query = "Select id, username, password, email, isConnected, isBanned, wins, loses from users as u,matches_spectators as m_s where m_s.id_user=u.id and m_s.id_match = " + matchId + ";";
        User userAux;
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        try {
            while (resultSet.next()) {
                userAux = (User) getResultSet(resultSet, "user");
                userAux.setPassword(null);
                viewers.add(userAux);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return viewers;
    }

    /**
     * Método que sirve para resetear el estado de conexión de los usuarios que estén conectados (al reiniciar server).
     */
    public void resetUserStatus() {
        String query = "update users set isConnected=0 where id<>0;";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para poder actualizar las estadísticas, especificando en dicho momento el jugador ganador y perdedor de la partida.
     * @param winnerName - Nombre del jugador ganador.
     * @param looserName - Nombre del jugador perdedor.
     */
    public synchronized void updateStatistics(String winnerName, String looserName) {
        String query = "update users set wins=wins+1 where username='" + winnerName + "';";
        DBConnector.getInstance().updateQuery(query);
        query = "update users set loses=loses+1 where username='" + looserName + "';";
        DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para recuperar las estadísticas de un usuario.
     * @param resultSet - ResultSet con el registro de la DB correspondiente a los datos del usuario para las estadísticas.
     * @return Devuelve la estadística del usuario.
     */
    public UserStat getUserStatResultSet(ResultSet resultSet){
        try {
            String username = resultSet.getString("users.username");
            int wins = resultSet.getInt("wins");
            int loses = resultSet.getInt("loses");
            int time = resultSet.getInt("time");

            return new UserStat(username, wins, loses, time);
        } catch (SQLException e){
            System.out.println("No hay usuarios");
        }
        return new UserStat();
    }

    /**
     * Método que sirve para recuperar las estadísticas de los top 10 jugadores.
     * @return Devuelve la estadística de usuarios / UserStatistic de los top 10 jugadores.
     */
    public UserStatistic getTop10Players() {
        String query = "SELECT users.username,wins,loses, sum(matchTime) as time FROM users left join players on players.userId=users.id left join matches on (players.id=matches.hostId or players.id=matches.guestId) group by users.id order by wins desc limit 10;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        UserStatistic userStatistic = new UserStatistic();
        try {
            while (resultSet.next()) {
                userStatistic.getUserStats().add(getUserStatResultSet(resultSet));
            }
            return userStatistic;
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnector.getInstance().disconnect();
            DBConnector.getInstance().resetValues();
        }
        return new UserStatistic();
    }

}