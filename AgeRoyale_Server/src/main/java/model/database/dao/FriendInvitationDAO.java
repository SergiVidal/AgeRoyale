package model.database.dao;


import model.database.DBConnector;
import model.entity.FriendInvitation;
import model.entity.Message;
import model.entity.UserFriendInvitation;
import model.enumeration.InvitationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Representa el Data Access Object que nos permite acceder a datos de invitaciones de amistad.
 */
public class FriendInvitationDAO {
    /**
     * Método que sirve para crear un amigo, osea para invitar otro usuario para ser amig@s.
     * @param userId Recibe como parámetro el id del usuario que está deseando agregar un amig@.
     * @param friendId Recibe como parámetro el id del usuario el cual será el amigo del que lo solicita en caso acepte.
     * @return Devuelve la invitación de amistad y con un mensaje que indica si se ha podido o no realizar.
     */
    public synchronized UserFriendInvitation createFriend(int userId, int friendId) {
        String query = "insert into friendinvitations (invitingUser, invitedUser, status, invitationDate) values (" + userId + ", " + friendId + ", 'Pending', now())";
        try {
            FriendInvitation aux = null;
            aux = getLastFriendInvitation(userId, friendId);
            if (aux == null || aux.getStatus().equals(InvitationStatus.Refuse) || aux.getStatus().equals(InvitationStatus.Accepted)) {
                if (DBConnector.getInstance().insertQuery(query)) {
                    aux = getLastFriendInvitation(userId, friendId);
                    //todo return last filtered by these 2 users
                    if (aux.getId() != null) {
                        return new UserFriendInvitation(aux, new Message(0, "Friend Invitation Successful!"));
                    } else {
                        aux = new FriendInvitation();
                        return new UserFriendInvitation(aux, new Message(2, "User not found on our system!!"));
                    }
                }
            } else {
                return new UserFriendInvitation(aux, new Message(1, "FriendInvitation has already been sent!!!!"));
            }
        } catch (SQLException ex) {
            System.out.println("Can't insert!!");
            ex.printStackTrace();
        }
        return new UserFriendInvitation(new FriendInvitation(), new Message(3, "Friend Invitation Error! (service unavaliable)"));
    }

    /**
     * Método que sirve para aceptar una invitación de amistad.
     * @param userId Recibe como parámetro el userId que sirve para indicar el usuario que hizo en un principio la petición de amistad.
     * @param friendId  Recibe como parámetro el id del usuario que está aceptando la solicitud de amistad.
     * @return  Devuelve una instancia de UserFriendInvitation que devuelve la invitación de amistad aceptada o con error, indicando en dicho caso el error que ha habido.
     */
    public synchronized UserFriendInvitation acceptFriendInvitation(int userId, int friendId) {
        UserFriendInvitation userFriendInvitation;
        boolean isSuccess = updateInvitation(userId, friendId, "Accepted");
        try {
            if (isSuccess) {
                 userFriendInvitation = new UserFriendInvitation(getLastFriendInvitation(userId, friendId), new Message(0, "Accepting FriendShip with " + userId + " successfully!!"));
                userFriendInvitation.setFriendInvitation(findFriendInvitationsByUserId(friendId).getFriendInvitations());
                userFriendInvitation.setMessage(new Message(0, "FriendInvitation Accepted Succesfully!!!"));
            }
            else {
                 userFriendInvitation = new UserFriendInvitation(getLastFriendInvitation(userId, friendId), new Message(1, "Error Accepting FriendInvitation!!"));
                userFriendInvitation.setFriendInvitation(findFriendInvitationsByUserId(friendId).getFriendInvitations());
                userFriendInvitation.setMessage(new Message(1, "Can't accept friend invitation!!!"));
            }
            return userFriendInvitation;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserFriendInvitation(new FriendInvitation(), new Message(2, "Service Unavaliable!!"));
    }

    /**
     * Método que sirve para rechazar una invitación de amistad.
     * @param userId Recibe como parámetro el userId que sirve para indicar el usuario que hizo en un principio la petición de amistad.
     * @param friendId  Recibe como parámetro el id del usuario que está rechazando la solicitud de amistad.
     * @return Devuelve una instancia de UserFriendInvitation que devuelve la invitación de amistad rechazada o con error, indicando en dicho caso el error que ha habido.
     */
    public synchronized UserFriendInvitation refuseFriendInvitation(int userId, int friendId) {
        boolean isSuccess = updateInvitation(userId, friendId, "Refuse");
        UserFriendInvitation userFriendInvitation;
        try {
            if (isSuccess) {
                userFriendInvitation = new UserFriendInvitation(getLastFriendInvitation(userId, friendId), new Message(0, "Accepting FriendShip with " + userId + " successfully!!"));
                userFriendInvitation.setFriendInvitation(findFriendInvitationsByUserId(friendId).getFriendInvitations());
                userFriendInvitation.setMessage(new Message(0, "FriendInvitation Refused Succesfully!!!"));
            }
            else {
                userFriendInvitation = new UserFriendInvitation(getLastFriendInvitation(userId, friendId), new Message(1, "Error Accepting FriendInvitation!!"));
                userFriendInvitation.setFriendInvitation(findFriendInvitationsByUserId(friendId).getFriendInvitations());
                userFriendInvitation.setMessage(new Message(1, "Can't refuse friend invitation!!!"));
            }
            return userFriendInvitation;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserFriendInvitation(new FriendInvitation(), new Message(1, "Service Unavaliable!!"));
    }

    /**
     * Método que sirve para actualizar una invitación de amistad, y que sirve como método reusable para aceptar o rechazar la invitación de amistad.
     * @param userId Recibe como parámetro el userId que sirve para indicar el usuario que hizo en un principio la petición de amistad.
     * @param friendId Recibe como parámetro el id del usuario que está actualizando la solicitud de amistad.
     * @param status Recibe como parámetro si se quiere o no acceptar la solicitud de amistad ('Accepted' o 'Refuse').
     * @return Devuelve un booleano que indica si se ha podido o no actualizar la invitación.
     */
    private synchronized boolean updateInvitation(int userId, int friendId, String status) {
        String query = "update friendinvitations as fi set status='" + status + "'" + " where fi.invitingUser=" + userId + " and fi.invitedUser=" + friendId + ";";
        return DBConnector.getInstance().updateQuery(query);
    }

    /**
     * Método que sirve para recuperar una solicitud de amistad entre 2 usuarios.
     * @param userID Recibe como parámetro el userId que sirve para indicar el usuario que hizo en un principio la petición de amistad.
     * @param friendID Recibe como parámetro el id del usuario al que se le envió la solicitud de amistad.
     * @return Devuelve la solicitud de amistad entre estos 2 usuarios.
     * @throws SQLException SQL error en el caso de que no haya una invitación de amistad entre estos 2 usuarios o se pierda la conexión con el servidor.
     */
    public FriendInvitation getLastFriendInvitation(int userID, int friendID) throws SQLException {
        String query = "Select id, invitingUser, invitedUser, status, invitationDate from friendinvitations as fi where fi.invitingUser=" + userID + " and fi.invitedUser=" + friendID + " order by fi.id DESC LIMIT 1;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        if (resultSet.next()) {
            return getResultSet(resultSet);
        }
        return null;
    }

    /**
     * Método que sirve para comprobar si una solicitud de amistad sigue estando pendiente o no.
     * @param userID Recibe como parámetro el userId que sirve para indicar el usuario que hizo en un principio la petición de amistad.
     * @param friendID Recibe como parámetro el id del usuario al que se le envió la solicitud de amistad.
     * @return Devuelve un boolean (true o false) indicando respectivamente si aún está pendiende o false si ha ha sido acceptada o  rechazada.
     * @throws SQLException En caso de que no exista una petición de amistad entre dichos 2 usuarios o haya un dato que no exista o error en DB.
     */
    public boolean checkFriendInvitation(int userID, int friendID) throws SQLException {
        String query = "Select id, invitingUser, invitedUser, status, invitationDate from friendinvitations as fi where fi.invitingUser=" + userID + " and fi.invitedUser=" + friendID + " and fi.status='Pending' order by fi.id DESC LIMIT 1;";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        return resultSet.next();
    }

    /**
     * Método que sirve para encontrar las invitaciones de amistad que le han hecho otros usuarios a un usuario en concreto.
     * @param uid Recibe el id de usuario del cual se quiere recuperar la lista de invitaciones de amistad.
     * @return Devuelve una instancia de UserFriendInvitation que indica si se han podido recuperar o no las invitaciones, y en caso afirmativo contiene la lista de invitaciones de amistad.
     */
    public UserFriendInvitation findFriendInvitationsByUserId(int uid) {
        ArrayList<FriendInvitation> friendInvitations = new ArrayList<>();
        String query = "select id, invitingUser, invitedUser, status, invitationDate from friendinvitations as fi where fi.invitedUser=" + uid + " and fi.status='Pending';";
        ResultSet resultSet = DBConnector.getInstance().selectQuery(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    friendInvitations.add(getResultSet(resultSet));
                }
                if (friendInvitations.size() != 0) {
                    return new UserFriendInvitation(friendInvitations, new Message(0, "Your friend invitations has been recovered successfully! "));
                } else
                    return new UserFriendInvitation(new ArrayList<FriendInvitation>(), new Message(1, "Nowadays you don't have friend invitations"));
            } catch (SQLException | NullPointerException e) {
                //e.printStackTrace();
                DBConnector.getInstance().disconnect();
                DBConnector.getInstance().resetValues();
            }
        }
        return new UserFriendInvitation(new ArrayList<FriendInvitation>(), new Message(2, "Service is not avaliable yet! Try Again"));
    }

    /**
     * Método que sirve para poder recuperar los datos de un registro en la tabla friendinvitations de la DB y lo guarda en un nuevo objeto instanciado de FriendInvitation.
     * @param resultSet Recibe el resultado obtenido para aquél registro por la DB.
     * @return Devuelve una instancia / objeto de FriendInvitation con loss datos que ha recuperado de aquella invitación de amistad mediante el resulset.
     */
    private FriendInvitation getResultSet(ResultSet resultSet) {
        try {
            Integer id = resultSet.getInt("id");
            Integer invitingUser = resultSet.getInt("invitingUser");
            Integer invitedUser = resultSet.getInt("invitedUser");
            String statusText = resultSet.getString("status");
            InvitationStatus invitationStatus;
            String date;
            if (statusText.equals("Pending")) {
                invitationStatus = InvitationStatus.Pending;
            } else if (statusText.equals("Refuse")) {
                invitationStatus = InvitationStatus.Refuse;
            } else {
                invitationStatus = InvitationStatus.Accepted;
            }
            String actualTime = resultSet.getString("invitationDate");
            String parts[] = actualTime.split(" ");
            String dayParts[] = parts[0].split("-");
            String hourParts[] = parts[1].split(":");
            LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(dayParts[0]), Integer.parseInt(dayParts[1]), Integer.parseInt(dayParts[2]), Integer.parseInt(hourParts[0]), Integer.parseInt(hourParts[1]));
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Madrid"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            date = zonedDateTime.format(formatter);
            //todo modify in UserManager
            return new FriendInvitation(id, invitingUser, invitedUser, invitationStatus, date);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return new FriendInvitation();
        }
    }
}