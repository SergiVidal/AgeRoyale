package model.network;

import model.entity.*;

/** Representa la interficie NetworkCallback, esta permite la comunicación entre la clase ServerCommunication y todos los controladores (excepto el GameController), una vez se recibe información del servidor */
public interface NetworkCallback {
    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario se registra en la aplicación
     * @param userLogin - Objeto UserLogin que contiene la información del usuario que acaba de registrarse
     */
    void onRegisterUser(UserLogin userLogin);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario inicia sesión
     * @param userLogin - Objeto UserLogin que contiene la información del usuario que acaba de iniciar sesión
     */
    void onLoginUser(UserLogin userLogin);

    /**
     * Función encargada de obtener todos los datos que hacen referencia a la lista de amigos
     * @param userFriendship - Objeto UserFriendShip que contiene la información de los amigos del usuario
     */
    void onGetFriendList(UserFriendship userFriendship);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario es eliminado como amigo
     * @param userFriendship - Objeto UserFriendShip que contiene la información actualizada de los amigos del usuario
     */
    void onDeleteFriend(UserFriendship userFriendship);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario añade a otro como amigo
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información de las solicitudes de amistad del usuario
     */
    void onAddFriend(UserFriendInvitation userFriendInvitation);

    /**
     * Función encargada de obtener todos los datos que hacen referencia a la lista de solicitudes de amistad
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información de las solicitudes de amistad del usuario
     */
    void onGetInvitationList(UserFriendInvitation userFriendInvitation);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario acepta una solicitud de amistad
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información actualizada de las solicitudes de amistad del usuario
     */
    void onAcceptFriend(UserFriendInvitation userFriendInvitation);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario rechaza una solicitud de amistad
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información actualizada de las solicitudes de amistad del usuario
     */
    void onDeclineFriend(UserFriendInvitation userFriendInvitation);

    /**
     * Función encargada de obtener todos los datos que hacen referencia a la lista de partidas
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    void onGetMatchList(UserMatch userMatch);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario crea una partida
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    void onCreateMatch(UserMatch userMatch);

    /**
     * Función encargada de gestionar las invitaciones a partidas (privadas) entre amigos
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    void onInviteFriendToMatch(UserMatchInvitation userMatchInvitation);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario se une a una partida pública, como jugador
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    void onJoinMatch(UserMatch userMatch);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario se une a una partida pública, como espectador
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    void onViewMatch(UserMatch userMatch);

    /**
     * Función encargada de actualizar los datos del jugador y notificar a la vista
     * @param user - Objeto User que contiene la información del usuario actual
     */
    void onGetUserInfo(User user);

    /**
     * Función encargada de gestionar la información recibida por el servidor cuando un usuario cancela la invitación a un amigo para jugar una partida privada
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    void onCancelMatch(UserMatchInvitation userMatchInvitation);

    /**
     * Función encargada de validar si la invitación a partida (privada) sigue activa
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    void onValidateMatchInvitation(UserMatchInvitation userMatchInvitation);

    /**
     * Función encargada de validar si la partida (pública) sigue activa
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    void onValidateMatch(UserMatch userMatch);

    /**
     * Función encargada de notificar que el servidor ha caido y de cerrar el proceso del cliente
     */
    void stoppedServer();
}
