package model.entity;

import java.io.Serializable;

/** Representa la clase Message, esta contiene la información de la comunicación entre el Cliente y el Servidor */
public class Message implements Serializable {
    /** Representa el código de la comunicación que indica si las acciones han sido realizadas con exito o no */
    private Integer code;
    /** Representa el mensaje de la comunicación que describe/especifica las respuestas del servidor */
    private String messageDescription;

    /**
     * Crea un Message
     * @param code - Representa el código de la comunicación
     * @param messageDescription - Representa el mensaje de la comunicación
     */
    public Message(Integer code, String messageDescription) {
        this.code = code;
        this.messageDescription = messageDescription;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", messageDescription='" + messageDescription + '\'' +
                '}';
    }
}
