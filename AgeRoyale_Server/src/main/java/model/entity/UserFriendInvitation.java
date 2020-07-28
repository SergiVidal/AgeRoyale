package model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Representa la clase UserFriendInvitation, esta contiene el listado de solicitudes de amistad de un usuario, permite la comunicación entre Cliente y Servidor */
public class UserFriendInvitation extends GenericUserFriendship implements Serializable {
    /** Representa el listado de solicitudes de amistad de un usuario */
    private List<FriendInvitation> friendInvitations;

    /**
     * Crea un UserFriendInvitation
     * @param friendInvitations - Representa el listado de solicitudes de amistad de un usuario
     * @param message - Representa la información de la comunicación entre el Cliente y el Servidor
     */
    public UserFriendInvitation(List<FriendInvitation> friendInvitations, Message message){
        super(message);
        this.friendInvitations=friendInvitations;
    }

    /**
     * Crea un UserFriendInvitation
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param friendName - Representa el nombre de usuario que recibe la solicitud
     * @param action - Representa la acción realizada
     */
    public UserFriendInvitation(String userName,String friendName, String action){
        super(userName, friendName, action);
    }

    /**
     * Crea un UserFriendInvitation
     * @param username - Representa el nombre de usuario que realiza la solicitud
     * @param action - Representa la acción realizada
     */
    public UserFriendInvitation(String username, String action){
        super(username,action);
    }

    /**
     *
     * @param friendInvitation - Representa una solicitud de amistad de un usuario
     * @param message - Representa la información de la comunicación entre el Cliente y el Servidor
     */
    public UserFriendInvitation(FriendInvitation friendInvitation, Message message) {
        super(message);
        this.friendInvitations = new ArrayList<FriendInvitation>();
        friendInvitations.add(friendInvitation);
    }

    public List<FriendInvitation> getFriendInvitations() {
        return friendInvitations;
    }

    public void setFriendInvitation(List<FriendInvitation> friendInvitation) {
        this.friendInvitations = friendInvitation;
    }

    @Override
    public String toString() {
        return "UserFriendInvitation{" +
                "friendInvitations=" + friendInvitations +
                '}';
    }
}