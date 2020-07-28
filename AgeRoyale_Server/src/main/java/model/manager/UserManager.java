package model.manager;

import model.database.dao.FriendInvitationDAO;
import model.database.dao.UserDAO;
import model.entity.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Representa el UserManager, que nos permite acceder a métodos del UserDAO y se guarda una lista de usuarios.
 */
public class UserManager {
    /**
     * Representa el UserDAO.
     */
    private UserDAO userDAO;
    /**
     * Representa el FriendInvitationDAO.
     */
    private FriendInvitationDAO friendInvitationDAO;
    /**
     * Representa la lista de usuarios del sistema y que estén conectados.
     */
    private List<User> users;

    /* Constantes para identificar las acciones del usuario */
    private static final String GET_FRIEND_LIST = "Get friend list";
    private static final String DELETE_FRIEND = "Delete friend";
    private static final String ADD_FRIEND = "Add friend";
    private static final String GET_INVITATION_LIST = "Get invitation list";
    private static final String ACCEPT_FRIEND = "Accept friend";
    private static final String DECLINE_FRIEND = "Decline friend";
    /**/

    /**
     * Crea el UserManager.
     */
    public UserManager() {
        this.userDAO = new UserDAO();
        this.users = new LinkedList<>();
        this.friendInvitationDAO = new FriendInvitationDAO();
    }

    /**
     * Método que sirve para registrar a un usuario dado su nombre de usuario, el correo y una contraseña.
     * @param username - Nombre de usuario
     * @param email - Email de usuario
     * @param password - Contraseña de usuario
     * @return Devuelve una instancia de UserLogin con la información del usuario ya registrado y lo añade dentro de la lista de usuarios.
     */
    public UserLogin registerUser(String username, String email, String password) {
        UserLogin userRegister = userDAO.addUser(new User(username, email, password));
        if (userRegister.getMessage().getCode() == 0) {
            addUser(userRegister.getUser());
        }
        return userRegister;
    }

    /**
     * Método que sirve para que un usuario pueda hacer login.
     * @param login - Login del usuario (nombre o email)
     * @param password - Contraseña del usuario
     * @return Devuelve una instancia de UserLogin con la información del usuario logueado.
     */
    public UserLogin loginUser(String login, String password) {
        return userDAO.loginUser(login, password);
    }

    /**
     * Método que sirve para añadir un usuario en la lista de usuarios.
     * @param user - Contiene la informacion del usuario
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Método que sirve para añadir un amig@.
     * @param invitingUsername - Id del usuario que quiere añadir un amig@.
     * @param invitedUsername - Id del usuario que es añadi@ como amig@.
     * @return Devuelve una instancia de UserFriendInvitation con la información de la solicitud de amistad.
     * @throws SQLException - SQL Exception
     */
    public UserFriendInvitation addFriend(String invitingUsername, String invitedUsername) throws SQLException {
        User invitingUser = userDAO.findUserByLogin(invitingUsername);
        User invitedUser = userDAO.findUserByLogin(invitedUsername);
        UserFriendInvitation userFriendInvitation = null;
        if (invitingUser != null && invitedUser != null) {
            if (!friendInvitationDAO.checkFriendInvitation(invitedUser.getId(), invitingUser.getId())) {
                if (!userDAO.getFriendShip(invitingUser.getId(), invitedUser.getId())) {
                    userFriendInvitation = friendInvitationDAO.createFriend(invitingUser.getId(), invitedUser.getId());
                    userFriendInvitation.getFriendInvitations().get(0).setUserName(invitingUser.getUsername());
                    userFriendInvitation.getFriendInvitations().get(0).setFriendName(invitedUser.getUsername());
                    userFriendInvitation.getFriendInvitations().get(0).setUser(userDAO.findUserByName(invitedUser.getUsername()));
                    userFriendInvitation.setUserName(invitingUser.getUsername());
                    userFriendInvitation.setFriendName(invitedUser.getUsername());
                } else {
                    userFriendInvitation = new UserFriendInvitation(new FriendInvitation(), new Message(3, "Friendship has already been done with this user!"));
                    userFriendInvitation.setUserName(invitingUsername);
                    userFriendInvitation.setFriendName(invitedUsername);
                }
            } else {
                userFriendInvitation = new UserFriendInvitation(new FriendInvitation(), new Message(2, "User has invited you before!"));
                userFriendInvitation.setUserName(invitingUsername);
                userFriendInvitation.setFriendName(invitedUsername);
            }
        } else {
            userFriendInvitation = new UserFriendInvitation(new FriendInvitation(), new Message(1, "User not exists on the system!"));
            userFriendInvitation.setUserName(invitingUsername);
        }
        userFriendInvitation.setAction(ADD_FRIEND);

        return userFriendInvitation;
    }

    /**
     * Método que sirve para recuperar la lista de amigos dado un nombre de usuario.
     * @param userName - Nombre del usuario.
     * @return Devuelve una instancia de UserFriendship / objeto de esta clase UserFriendship y que contiene si se han podido o no recuperar la lista de amistades y en caso que si, también la lista de amistades.
     */
    public UserFriendship getFriendList(String userName) {
        if (userName != null) {
            return userDAO.getFriendList(userName);
        }
        return null;
    }

    /**
     * Métod que sirve para eliminar a un amig@.
     * @param invitingUser - Id del usuario que quiere eliminar al amig@.
     * @param invitedUser - Id del usuario que serà eliminad@.
     * @return Devuelve una instancia de UserFriendship / objeto de esta clase UserFriendship y que contiene si se ha podido o no eliminar al amig@ y de nuevo la lista de amistades actuales suyas cargadas de DB.
     */
    public UserFriendship removeFriend(String invitingUser, String invitedUser){
        return userDAO.deleteFriendById(userDAO.findUserByName(invitingUser).getId(), userDAO.findUserByName(invitedUser).getId());
    }

    /**
     * Método que sirve para acceptar una solicitud de amistad.
     * @param userName - Nombre del usuario que ha hecho la invitación.
     * @param friendName - Nombre del usuario que accepta la invitación.
     * @return Devuelve una instancia de UserFriendInvitation / objeto de UserFriendInvitation donde se recuperaran la lista de invitaciones de amistad y se verá que se ha acceptado la invitación.Además si es que si, se crea la amistad.
     */
    public UserFriendInvitation acceptFriendInvitation(String userName, String friendName) {
        UserFriendInvitation userFriendInvitation = friendInvitationDAO.acceptFriendInvitation(userDAO.findUserByName(friendName).getId(), userDAO.findUserByName(userName).getId());
        for (FriendInvitation friendInvitation : userFriendInvitation.getFriendInvitations()) {
            friendInvitation.setUserName(friendName);
            friendInvitation.setFriendName(userName);
            friendInvitation.setUser(userDAO.getUserInfoById(friendInvitation.getInvitingUser()));
        }
        userFriendInvitation.setAction(ACCEPT_FRIEND);
        if (userFriendInvitation.getMessage().getCode() == 0) {
            userDAO.createFriendShip(userDAO.findUserByName(userName).getId(), userDAO.findUserByName(friendName).getId());
        }
        return userFriendInvitation;
    }

    /**
     * Método que sirve para rechazar la solicitud de amistad.
     * @param userName - Nombre del usuario que ha hecho la invitación de amistad.
     * @param friendName - Nombre del usuario que rechaza la invitación de amistad.
     * @return Devuelve una instancia de UserFriendInvitation / objeto de UserFriendInvitation con la invitación de amistad rechazada, ya que recupera de nuevo la lista de amistades.Además pone la acción para notificar al usuario que creó la solicitud de amistad, que ha sido rechazada.
     */
    public UserFriendInvitation refuseFriendInvitation(String userName, String friendName) {
        UserFriendInvitation userFriendInvitation = friendInvitationDAO.refuseFriendInvitation(userDAO.findUserByName(friendName).getId(), userDAO.findUserByName(userName).getId());
        for (FriendInvitation friendInvitation : userFriendInvitation.getFriendInvitations()) {
            friendInvitation.setUserName(friendName);
            friendInvitation.setFriendName(userName);
            friendInvitation.setUser(userDAO.getUserInfoById(friendInvitation.getInvitingUser()));
        }
        userFriendInvitation.setAction(DECLINE_FRIEND);
        return userFriendInvitation;
    }

    /**
     * Método que sirve para obtener la lista de invitaciones de amistad por el nombre de usuario (Guest).
     * @param invitedUser - Nombre del usuario invitado
     * @return Devuelve una instancia de UserFriendInvitation / objeto de class UserFriendInvitation con un Message que indica si hay invitaciones o no, o si no se han podido recuperar.
     * En caso que si, añade la lista de solicitudes de amistad que le han hecho a dicho usaurio.
     */
    public UserFriendInvitation getFriendInvitations(String invitedUser) {
        UserFriendInvitation userFriendInvitation = friendInvitationDAO.findFriendInvitationsByUserId(userDAO.findUserByLogin(invitedUser).getId());
        userFriendInvitation.setUserName(invitedUser);
        userFriendInvitation.setAction(GET_INVITATION_LIST);
        if (userFriendInvitation.getFriendInvitations().size() != 0) {
            for (FriendInvitation friendInvitation : userFriendInvitation.getFriendInvitations()) {
                User user = userDAO.getUserInfoById(friendInvitation.getInvitedUser());
                friendInvitation.setUserName(userDAO.getUserInfoById(friendInvitation.getInvitingUser()).getUsername());
                friendInvitation.setFriendName(userDAO.getUserInfoById(friendInvitation.getInvitedUser()).getUsername());
                if (user.getUsername().equals(invitedUser)) {
                    friendInvitation.setUser(userDAO.getUserInfoById(friendInvitation.getInvitingUser()));
                }
            }
        }
        return userFriendInvitation;
    }

    /**
     * Método que sirve para recuperar los espectadores de una partida por id de la partida.
     * @param matchId - Id de la partida
     * @return Devuelve una lista de usuarios que están viendo la partida como espectadores.
     */
    public ArrayList<User> getSpectatorsByMatchId(int matchId) {
        return userDAO.getSpectatorsByMatchId(matchId);
    }

    /**
     * Método que sirve para recuperar un usuario dado su nombre de usuario.
     * @param username - Nombre de usaurio a aconseguir.
     * @return Devuelve una instancia de User / objeto de class User con los datos del usuario solicitado.
     */
    public User getUserByName(String username) {
        return userDAO.findUserByName(username);
    }

    /**
     * Método que sirve para que un usuario pueda hacer desconexión.
     * @param userName - Nombre de usuario.
     */
    public void logoutUser(String userName) {
        userDAO.logoutUser(userName);
    }

    /**
     * Método que sirve para poder banear a un usuario.
     * @param userName - Nombre de usuario..
     */
    public void banUser(String userName) {
        userDAO.banUser(userName);
    }

    /**
     * Método que sirve para resetear el status de conexión de un usuario.
     */
    public void resetUserStatus() {
        userDAO.resetUserStatus();
    }

    /**
     * Método que sirve para poder actualizar las estadísticas de una partida.
     * @param winnerName - Nombre del jugador ganador de la partida.
     * @param looserName - Nombre del jugador perdedor de la partida.
     */
    public void updateStatistics(String winnerName, String looserName) {
        userDAO.updateStatistics(winnerName, looserName);
    }

    /**
     * Método para recuperar los top 10 players.
     * @return Devuelve la estadística de los top 10 jugadores - UserStatistic.
     */
    public UserStatistic getTop10(){
        return userDAO.getTop10Players();
    }
}
