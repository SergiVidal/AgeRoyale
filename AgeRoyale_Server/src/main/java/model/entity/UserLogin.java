package model.entity;

import java.io.Serializable;

/** Representa la clase UserGame, esta contiene la información que hace referencia a un inicio de sesión o registro, permite la comunicación entre Cliente y Servidor */
public class UserLogin implements Serializable {
    /** Representa el usuario que realiza la acción */
    private User user;
    /** Representa la información recibida por el servidor */
    private Message message;
    /** Representa la acción realizada (Iniciar sesión/Registrarse) */
    private String action;

    /**
     * Crea un UserLogin
     * @param user - Representa el usuario que realiza la acción
     * @param message - Representa la información recibida por el servidor
     */
    public UserLogin(User user, Message message) {
        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "user=" + user +
                ", message=" + message +
                ", action='" + action + '\'' +
                '}';
    }
}
