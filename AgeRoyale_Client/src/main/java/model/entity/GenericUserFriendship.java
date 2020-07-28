package model.entity;

import java.io.Serializable;

/** Representa la clase abstracta/padre, que contiene información común para las distintas acciones vinculadas a las relaciones de amistad entre usuarios, permite la comunicación entre Cliente y Servidor */
public abstract class GenericUserFriendship implements Serializable {
    /** Representa el nombre de usuario que realiza la solicitud */
    private String userName;
    /** Representa el nombre de usuario que recibe la solicitud */
    private String friendName;
    /** Representa la acción realizada (ya sea crear/cancelar/rechazar una solicitud de amistad, eliminar de amigo a un usuario ...) */
    private String action;
    /** Representa la información recibida por el servidor */
    private Message message;

    /**
     * Crea un GenericUserFriendship
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param friendName - Representa el nombre de usuario que recibe la solicitud
     * @param action - Representa la acción realizada
     * @param message - Representa la información recibida por el servidor
     */
    public GenericUserFriendship(String userName, String friendName, String action, Message message) {
        this.userName = userName;
        this.friendName = friendName;
        this.action = action;
        this.message = message;
    }

    /**
     * Crea un GenericUserFriendship
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param message - Representa la información recibida por el servidor
     */
    public GenericUserFriendship(String userName, Message message) {
        this.userName = userName;
        this.message = message;
    }
    /**
     * Crea un GenericUserFriendship
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param action - Representa la acción realizada
     * @param message - Representa la información recibida por el servidor
     */
    public GenericUserFriendship(String userName,String action, Message message) {
        this.userName = userName;
        this.action=action;
        this.message = message;
    }

    /**
     * Crea un GenericUserFriendship
     * @param message - Representa la información recibida por el servidor
     */
    public GenericUserFriendship(Message message) {
        this.message = message;
    }

    /** Crea un GenericUserFriendship */
    public GenericUserFriendship() {

    }

    /**
     * Crea un GenericUserFriendship
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param action - Representa la acción realizada
     */
    public GenericUserFriendship (String userName,String action){
        this.userName=userName;
        this.action=action;
    }

    /**
     * Crea un GenericUserFriendship
     * @param userName - Representa el nombre de usuario que realiza la solicitud
     * @param friendName - Representa el nombre de usuario que recibe la solicitud
     * @param action - Representa la acción realizada
     */
    public GenericUserFriendship (String userName,String friendName, String action){
        this.userName=userName;
        this.friendName=friendName;
        this.action=action;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GenericUserFriendship{" +
                "userName='" + userName + '\'' +
                ", friendName='" + friendName + '\'' +
                ", action='" + action + '\'' +
                ", message=" + message +
                '}';
    }
}