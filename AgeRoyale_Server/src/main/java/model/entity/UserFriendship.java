package model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Representa la clase UserFriendship, esta contiene el listado de amigos de un usuario, permite la comunicación entre Cliente y Servidor */
public class UserFriendship extends GenericUserFriendship implements Serializable {
    /** Representa el listado de amigos de un usuario */
    private List<Friend> friendList;

    /**
     * Crea un UserFriendship
     * @param username - Representa el nombre de usuario
     * @param friendList - Representa el listado de amigos de un usuario
     * @param action - Representa la acción realizada
     * @param message - Representa la información de la comunicación entre el Cliente y el Servidor
     */
    public UserFriendship(String username, List<Friend> friendList, String action, Message message) {
        super(username, action,message);
        this.friendList = friendList;
    }

    /**
     * Crea un UserFriendship
     * @param userId - Representa el id del usuario
     * @param friendId - Representa el id del amigo
     * @param action - Representa la acción realizada
     * @param message - Representa la información de la comunicación entre el Cliente y el Servidor
     */
    public UserFriendship(int userId, int friendId, String action, Message message) {
        super(null, null, "action", null);
        this.friendList = new ArrayList<>();
    }

    /**
     * Crea un UserFriendship
     * @param message - Representa la información de la comunicación entre el Cliente y el Servidor
     */
    public UserFriendship(Message message) {
        super(message);
    }

    /**
     * Crea un UserFriendship
     * @param userName - Representa el nombre de usuario
     * @param action - Representa la acción realizada
     */
    public UserFriendship(String userName,String action){
        super(userName,action);
    }

    /**
     * Crea un UserFriendship
     * @param userName - Representa el nombre de usuario
     * @param friendName - Representa el nombre de un amigo
     * @param action - Representa la acción realizada
     */
    public UserFriendship(String userName,String friendName, String action){
        super(userName, friendName, action);
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @Override
    public String toString() {
        return "UserFriendship{" + super.toString()+
                "friendList=" + friendList +
                '}';
    }
}