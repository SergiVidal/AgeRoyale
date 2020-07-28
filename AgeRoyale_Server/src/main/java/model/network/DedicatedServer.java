package model.network;

import model.entity.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Representa el DedicatedServer, que es la clase que se instanciará cada vez que haya 1 nuevo cliente (hay 1 DedicatedServer para cada cliente (1 DS - 1 C)).
 */
public class DedicatedServer extends Thread {
    /**
     * Representa el ObjectOutputStream que es el que nos permite enviarle datos al cliente. (escritura / write)
     */
    private ObjectOutputStream objectOut;
    /**
     * Representa el User, el usuario del cliente que se ha conectado.
     */
    private User user;
    /**
     * Representa el ObjectInputStream que es el que nos permite recibir datos al cliente. (lectura / read)
     */
    private ObjectInputStream objectIn;
    /**
     * Representa el boolean que nos permite decidir hasta cuando se debe estar recibiendo y enviando datos al cleinte.
     */
    private boolean isOn;
    /**
     * Representa el NetworkCallback, que nos permite llamar a métodos de otras clases a través de esta clase que es de networking.
     */
    private NetworkCallback callback;
    /**
     * Representa el id del cliente que se le ha asignado.
     */
    private int clientId;
    /**
     * Representa el Socket, que es la combinación de IP y Puerto con la que se ha establecido conexión con el cliente, además de otra información que nos permite interactuar con él.
     * Como tipo de socket,de IP ... y como podemos leer y recibir de él.
     */
    private Socket sClient;
    /**
     * Representa la lista de DedicatedServer, cada 1 servirá para atender peticiones de 1 cliente distinto.
     */
    private LinkedList<DedicatedServer> clients;
    /**
     * Representa el Server, que es quien nos ha instanciado y quien está a la espera de nuevos clientes.
     */
    private Server server;
    /* Constantes para identificar las acciones del usuario */
    private static final String LOGIN_USER = "Login user";
    private static final String REGISTER_USER = "Register user";
    private static final String LOGOUT_USER = "Logout user";
    private static final String GET_FRIEND_LIST = "Get friend list";
    private static final String DELETE_FRIEND = "Delete friend";
    private static final String ADD_FRIEND = "Add friend";
    private static final String GET_INVITATION_LIST = "Get invitation list";
    private static final String ACCEPT_FRIEND = "Accept friend";
    private static final String DECLINE_FRIEND = "Decline friend";
    public static final String GET_MATCH_LIST = "Get match list";
    private static final String CREATE_MATCH = "Create match";
    public static final String JOIN_MATCH = "Join match";
    public static final String VIEW_MATCH = "View match";
    private static final String INVITE_FRIEND_MATCH = "Invite friend match";
    private static final String ACCEPT_FRIEND_MATCH = "Accept friend match";
    private static final String DECLINE_FRIEND_MATCH = "Decline friend match";
    public static final String CANCEL_MATCH = "Cancel match";
    private static final String GET_GAME_INFO = "Get game info";
    private static final String VALIDATE_FRIEND_INVITATION = "Validate friend invitation";
    private static final String VALIDATE_MATCH = "Validate match";
    public static final String VALIDATE = "validate";
    public static final String JOIN = "join";
    public static final String VIEW = "view";
    public static final String CREATE_TROOP = "Create troop";
    public static final String ADD_TROOP_TO_MATCH = "Add troop to match";
    public static final String LOCATE_TROOP = "Locate troop";
    public static final String MOVE_TROOP = "Move troop";
    public static final String ATTACK_TROOP = "Attack troop";
    public static final String GET_TROOPS_LIST = "Get troops list";
    public static final String GET_USER_TROOPS_LIST = "Get user troops list";
    public static final String REFRESH_GAME = "Refresh game";
    public static final String GAME_FINISHED = "Game finished";
    public static final String CANCEL_MATCH_INVITATION = "Cancel match invitation";
    /**/

    /**
     * Crea el DedicatedServer.
     * @param sClient - Socket del cliente.
     * @param clients - Lista de DedicatedServer.
     * @param server - Servidor.
     */
    public DedicatedServer(Socket sClient, LinkedList<DedicatedServer> clients, Server server) {
        this.isOn = false;
        this.sClient = sClient;
        this.clientId = clients.size();
        this.clients = clients;
        this.server = server;
    }

    /**
     * Método que sirve para arrancar un servidor dedicado.
     */
    public void startDedicatedServer() {
        isOn = true;
        this.start();
    }

    /**
     * Método que sirve para parar un servidor dedicado / instancia de DedicatedServer.
     */
    public void stopDedicatedServer() {
        this.isOn = false;
        this.interrupt();
    }

    /**
     * Método principal del servidor dedicado (Thread) / instancia de DedicatedServer donde irá atendiendo continuamente solicitudes por parte del cliente y con el que se comunicará.
     */
    public void run() {
        try {
            objectOut = new ObjectOutputStream(sClient.getOutputStream());
            objectIn = new ObjectInputStream(sClient.getInputStream());

            while (isOn) {
                Object object = objectIn.readObject();

                if (object instanceof User) {
                    if (callback != null) {
                        callback.onGetUserInfo(user, this);
                    }
                }

                if (object instanceof UserLogin) {
                    if (callback != null) {
                        if (((UserLogin) object).getAction().equals(LOGIN_USER)) {
                            if (getPosByUserByName(((UserLogin) object).getUser().getUsername()) == -1) {
                                this.user = new User();
                                clients.get(clientId).setUser(((UserLogin) object).getUser());
                                callback.onLoginUser((UserLogin) object, this);
                            } else {
                                ((UserLogin) object).setMessage(new Message(1, "You are already connected!"));
                                onLoginUser((UserLogin) object);
                            }
                        } else if (((UserLogin) object).getAction().equals(REGISTER_USER)) {
                            callback.onRegisterUser(((UserLogin) object).getUser(), this);
                        } else if (((UserLogin) object).getAction().equals(LOGOUT_USER)) {
                            this.isOn = false;
                            callback.onLogoutUser(((UserLogin) object).getUser(), this);
// callback.onGetPublicMatches(new UserMatch(this.getUser().getUsername()),JOIN,this);
                        }
                    }
                }
                if (object instanceof UserFriendInvitation) {
                    if (callback != null) {
                        if (((UserFriendInvitation) object).getAction().equals(ADD_FRIEND)) {
                            callback.onFriendInvitation((UserFriendInvitation) object, this);
                        } else if (((UserFriendInvitation) object).getAction().equals(ACCEPT_FRIEND)) {
                            callback.onJoinFriendInvitation(((UserFriendInvitation) object), this);
                        } else if (((UserFriendInvitation) object).getAction().equals(DECLINE_FRIEND)) {
                            callback.onRefuseFriendInvitation(((UserFriendInvitation) object), this);
                        } else if (((UserFriendInvitation) object).getAction().equals(GET_INVITATION_LIST))
                            callback.onGetFriendInvitations(((UserFriendInvitation) object), this);

                    }
                }
                if (object instanceof UserFriendship) {
                    if (callback != null) {
                        if (((UserFriendship) object).getAction().equals(DELETE_FRIEND)) {
                            callback.onDeleteFriend((UserFriendship) object, this);
                        } else if (((UserFriendship) object).getAction().equals(GET_FRIEND_LIST)) {
                            callback.onGetFriends((UserFriendship) object, this);
                        }
                    }
                }
                if (object instanceof UserMatch) {
                    if (callback != null) {
                        if (((UserMatch) object).getAction().equals(GET_MATCH_LIST)) {
                            callback.onGetPublicMatches((UserMatch) object, "GetMatchList", this);
                        } else if (((UserMatch) object).getAction().equals(CREATE_MATCH)) {
                            callback.onAddMatch((UserMatch) object, this);
                        } else if (((UserMatch) object).getAction().equals(JOIN_MATCH)) {
                            callback.onJoinMatch((UserMatch) object, this);
                        } else if (((UserMatch) object).getAction().equals(VIEW_MATCH)) {
                            callback.onViewMatch((UserMatch) object, this);
                        }
                    }

                }
                if (object instanceof UserMatchInvitation) {
                    if (callback != null) {
                        if (((UserMatchInvitation) object).getAction().equals(INVITE_FRIEND_MATCH)) {
                            callback.onCreateMatchInvitation((UserMatchInvitation) object, this);
                        } else if (((UserMatchInvitation) object).getAction().equals(ACCEPT_FRIEND_MATCH)) {
                            callback.onAcceptMatchInvitation((UserMatchInvitation) object, this);
                        } else if (((UserMatchInvitation) object).getAction().equals(DECLINE_FRIEND_MATCH)) {
                            callback.onRefuseMatchInvitation((UserMatchInvitation) object, this);
                        } else if (((UserMatchInvitation) object).getAction().equals(CANCEL_MATCH)) {
                            callback.onCancelMatch((UserMatchInvitation) object, this);
                        } else if (((UserMatchInvitation) object).getAction().equals(CANCEL_MATCH_INVITATION)) {
                            callback.onCancelMatchInvitation((UserMatchInvitation) object, this);
                        }
                    }
                }
                if (object instanceof Game) {
                    if (callback != null) {
                        if (((Game) object).getAction().equals(GET_GAME_INFO)) {
                            callback.onGetGameInfo((Game) object, this);
                        }
                    }
                }
                if (object instanceof UserGame) {
                    if (callback != null) {
                        if (((UserGame) object).getAction().equals(CREATE_TROOP)) {
                            callback.onCreateTroop(((UserGame) object), this);
                        } else if (((UserGame) object).getAction().equals(LOCATE_TROOP)) {
                            callback.onLocateTroop(((UserGame) object), this);
                        } else if (((UserGame) object).getAction().equals(GET_TROOPS_LIST)) {
                            callback.onGetTroops(((UserGame) object).getGame().getMatch().getMatchName(), this);
                        }
                    }
                }
            }
            objectOut.reset();
        } catch (IOException | ClassNotFoundException e) {
//todo to check if it's not removed
            if (isOn == true) {
                if (user != null) {
                    callback.onDisconnectClient(user.getUsername(), this);
                    callback.onGetPublicMatches(new UserMatch(this.getUser().getUsername()), JOIN, this);
                }
                clients.remove(this);
            }
            stopDedicatedServer();
            user = null;
            server.showClients();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para registrar el NetworkCallback como callback que usará dicha clase para comunicarse con el SystemController.
     * @param callback - Representa el NetworkCallback vinculado
     */
    public void registerCallback(NetworkCallback callback) {
        this.callback = callback;
    }

    /**
     * Método que sirve para informar al cliente del registro del usuario.
     * @param userRegister - Instancia de UserLogin con el registro del usuario.
     */
    public void onRegisterUser(UserLogin userRegister) {
        try {
            if (userRegister.getMessage().getCode() == 0) {
                this.user = new User();
                user.setId(userRegister.getUser().getId());
                user.setUsername(userRegister.getUser().getUsername());
                user.setConnected(true);
                clients.get(clientId).setUser(user);
            }
            userRegister.setAction(REGISTER_USER);
            objectOut.writeObject(userRegister);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar al cliente del login del usuario.
     * @param userLogin - Instancia de UserLogin con el login del usuario.
     */
    public void onLoginUser(UserLogin userLogin) {
        try {
            user = userLogin.getUser();
            userLogin.setAction(LOGIN_USER);
            objectOut.writeObject(userLogin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la creación de la solicitud de amistad.
     * @param friendshipInviting - Solciitud de amistad para el usuario que invita
     * @param friendshipInvited - Solciitud de amistad para el usuario que es invitado
     */
    public void onFriendInvitationCreated(UserFriendInvitation friendshipInviting, UserFriendInvitation friendshipInvited) {
        int pos = -1;
        try {
            objectOut.writeObject(friendshipInviting);
            if (friendshipInviting.getFriendInvitations() != null && friendshipInvited != null) {
                friendshipInviting.setMessage(new Message(5, ""));
                pos = getPosByUserByName(friendshipInvited.getUserName());
                if (pos != -1)
                    clients.get(pos).objectOut.writeObject(friendshipInvited);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Método que sirve para informar de la eliminación de un amig@.
     * @param friendshipDeleting - UserFriendship para el usuario que ha querido eliminar al otro.
     * @param friendshipDeleted - UserFriendship para el usuario que ha sido eliminado.
     */
    public void onDeleteFriend(UserFriendship friendshipDeleting, UserFriendship friendshipDeleted) {
        int pos = -1;
        try {
            objectOut.writeObject(friendshipDeleting);
            pos = getPosByUserByName(friendshipDeleted.getUserName());
            if (pos != -1)
                clients.get(pos).objectOut.writeObject(friendshipDeleted);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la acceptación o rechazo (modificación) de la solicitud de amistad.
     * @param acceptFriendInvitation - Solicitud de amistad modificada.
     */
    public void onUpdatingInvitation(UserFriendInvitation acceptFriendInvitation) {
        try {
            objectOut.writeObject(acceptFriendInvitation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para recuperar a los amig@s.
     * @param friendList - UserFriendShip para el usuario que la solicita.
     * @param friendFriendList - UserFriendShip para el usuario que es amig@.
     */
    public void onGetFriends(UserFriendship friendList, UserFriendship friendFriendList) {
        int pos = -1;
        try {
            objectOut.writeObject(friendList);
            if (friendFriendList != null) {
                pos = getPosByUserByName(friendFriendList.getUserName());
                if (pos != -1)
                    clients.get(pos).objectOut.writeObject(friendFriendList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar (private / privado) que sirve para encontrar el DedicatedServer (la posición dentro de la lista) que sirve para atender al usuario deseado.
     * @param userName - Nombre del usuario del cual se quiere obtener la posición de su DedicatedServer para poder interactuar con dicho cliente a través del servidor dedicado que le atiende.
     * @return Devuelve un entero / int que indica la posición donde está dicho DedicatedServer dentro de la lista de DedicatedServer.
     */
    private int getPosByUserByName(String userName) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).user != null) {
                if (clients.get(i).user.getUsername().equals(userName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Método que sirve para informar de que se recuperan la lista de solicitud de amistades.
     * @param friendInvitations - UserFriendInvitation que contiene un Message y la lista de solicitudes de amistad.
     */
    public void onGetFriendsInvitations(UserFriendInvitation friendInvitations) {
        try {
            objectOut.writeObject(friendInvitations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de que no se ha podido crear la solicitud de amistad.
     * @param friendInvitation - UserFriendInvitation con la información básica de solicitud y un Message que indica si ha ido o no bien.
     */
    public void onFriendInvitationFailure(UserFriendInvitation friendInvitation) {
        try {
            objectOut.writeObject(friendInvitation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la lista de partidas públicas.
     * @param userMatch - UserMatch que contiene la lista de partidas públicas si hay y un mensaje que indica si se han podido recuperar o no.
     */
    public void onGetPublicMatches(UserMatch userMatch) {
        try {
            userMatch.setAction(GET_MATCH_LIST);
            objectOut.writeObject(userMatch);
            //objectOut.reset();
            objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la lista de partidas públicas a todos los clientes.
     * @param userMatch - UserMatch que contiene la lista de partidas públicas si hay y un mensaje que indica si se han podido recuperar o no.
     */
    public void onGetPublicMatchesAllClients(UserMatch userMatch) {
        userMatch.setAction(GET_MATCH_LIST);
        for (DedicatedServer dedicatedServer : clients) {
            try {
                if (dedicatedServer.getUser() != null && dedicatedServer.getUser().isConnected()) {
                    dedicatedServer.objectOut.writeObject(userMatch);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que sirve para informar de la adición de una partida.
     * @param addMatch - UserMatch que contiene un Message y en caso que si, la partida añadida.
     */
    public void onAddMatch(UserMatch addMatch) {
        try {
            objectOut.writeObject(addMatch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la unión de una partida.
     * @param joinMatch -  UserMatch que contiene un Message y en caso que si, la partida a la que se ha unido.
     */
    public void onJoinMatch(UserMatch joinMatch) {
        int pos = -1;
        try {
            objectOut.writeObject(joinMatch);
            //objectOut.reset();
            objectOut.flush();
            pos = getPosByUserByName(joinMatch.getFriendName());
            if (pos != -1)
                clients.get(pos).objectOut.writeObject(joinMatch);
            clients.get(pos).objectOut.flush();

            joinMatch.setAction(GET_MATCH_LIST);
            callback.onGetPublicMatches(joinMatch, JOIN, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para enviar al viewer el Match al cual se ha unido como espectador.
     * @param viewMatch -  UserMatch que contiene un Message y en caso que si, la partida a la que se ha unido como espectador.
     */
    public void onViewMatch(UserMatch viewMatch) {
        try {
            objectOut.writeObject(viewMatch);
            callback.onGetPublicMatches(viewMatch, VIEW, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la creación de las solicitudes de invitación a partida.
     * @param userMatchInvitationHost - UserMatchInvitation para el usuario Host
     * @param userMatchInvitationGuest - - UserMatchInvitation para el usuario Guest / Invitado.
     */
    public void onCreateMatchInvitation(UserMatchInvitation userMatchInvitationHost, UserMatchInvitation userMatchInvitationGuest) {
        int pos = -1;
        try {
            objectOut.writeObject(userMatchInvitationHost);
            pos = getPosByUserByName(userMatchInvitationGuest.getFriendName());
            if (pos != -1)
                clients.get(pos).objectOut.writeObject(userMatchInvitationGuest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la obtención de los datos de un usuario.
     * @param user - User que tiene todos los datos del usuario que previamente se había solicitado.
     */
    public void onGetUserInfo(User user) {
        try {
            objectOut.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar del rechazo de una invitación de partida.
     * @param refuseMatchInvitation - UserMatchInvitation que contiene el rechazo de la partida.
     */
    public void onRefuseMatchInvitation(UserMatchInvitation refuseMatchInvitation) {
        int pos = -1;
        try {
            if (refuseMatchInvitation != null) {
                pos = getPosByUserByName(refuseMatchInvitation.getUsername());
                if (pos != -1)
                    clients.get(pos).objectOut.writeObject(refuseMatchInvitation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar  de la acceptación de una invitación de partida.
     * @param userMatchInvitation - UserMatchInvitation que contiene la acceptación de la partida.
     */
    public void onAcceptMatchInvitation(UserMatchInvitation userMatchInvitation) {
        int pos = -1;
        try {
            if (userMatchInvitation != null) {
                pos = getPosByUserByName(userMatchInvitation.getUsername());
                if (pos != -1)
                    clients.get(pos).objectOut.writeObject(userMatchInvitation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para validar la solicitud de partida e informar al creador y al usuario que la accepta o rechaza.
     * @param userMatchInvitation - UserMatchInvitation con la información de la partida acceptada o rechazada. Y contiene lista de partidas públicas.
     */
    public void onValidateFriendInvitation(UserMatchInvitation userMatchInvitation) {
        int pos = -1;
        try {
            if (userMatchInvitation != null) {
                pos = getPosByUserByName(userMatchInvitation.getFriendName());
                userMatchInvitation.setAction(VALIDATE_FRIEND_INVITATION);
                if (pos != -1)
                    clients.get(pos).objectOut.writeObject(userMatchInvitation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de validar a una partida.
     * @param userMatch - UserMatch que se valida.
     * @param isPublic - boolean que indica si es pública o no.
     * @param user - User que ha creado la partia.
     * @param player - Jugador que es el invitado.
     */
    public void onValidateMatch(UserMatch userMatch, boolean isPublic, User user, Player player) {
        int pos = -1;
        try {
            if (userMatch != null) {
                pos = getPosByUserByName(userMatch.getUsername());
                userMatch.setAction(VALIDATE_MATCH);
                if (pos != -1) {
                    clients.get(pos).objectOut.writeObject(userMatch);
                    clients.get(pos).objectOut.flush();
                   /* if (isPublic)
                        callback.onGetPublicMatches(userMatch, VALIDATE, this);*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user != null) {
            callback.onCreateGameRoom(userMatch, user, player, this);
        }
    }

    /**
     * Método que sirve para informar de la cancelación de una partida.
     * @param cancelMatch - Match cancelado.
     */
    public void onCancelMatch(UserMatchInvitation cancelMatch) {
        try {
            objectOut.writeObject(cancelMatch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la lista de amisades actualizada al hacer login un usuario para indicar que está conectado.
     * @param friendList - UserFriendship con la lista de amistades.
     */
    public void onNotifyLogin(UserFriendship friendList) {
        for (Friend friend : friendList.getFriendList()) {
            if (friend.getUser().isConnected()) {
                callback.onFriendConnected(friend, this);
            }
        }
    }

    /**
     * Método que sirve para informar de la cerrada de sessión de un usuario.
     * @param friendList - UserFriendship con la lista de amistades.
     * @param user - User con la información del usuario
     */
    public void onNotifyLogout(UserFriendship friendList, User user) {
        this.user = user;
        for (Friend friend : friendList.getFriendList()) {
            if (friend.getUser().isConnected()) {
                callback.onFriendConnected(friend, this);
            }
        }
    }

    /**
     * Método que sirve para indicar que un usuario ha sido connectado.
     * @param friendList - UserFriendship con la información de la lista de usaurios.
     */
    public void onFriendConnected(UserFriendship friendList) {
        int pos = -1;
        try {
            pos = getPosByUserByName(friendList.getUserName());
            if (pos != -1) {
                friendList.setAction(GET_FRIEND_LIST);
                clients.get(pos).objectOut.writeObject(friendList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de la creación de una nueva partida. (Informa a todos los DedicatedServer que estén conectados, que pertenecen a clientes).
     * @param matches - UserMatch que contiene todas las partidas públicas.
     */
    public void onNotifyNewMatch(UserMatch matches) {
        matches.setAction(GET_MATCH_LIST);
        for (DedicatedServer dServer : clients) {
            if (dServer.getUser().isConnected()) {
                try {
                    dServer.objectOut.writeObject(matches);
                    //dServer.objectOut.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Método que sirve para informar de la finalización de una partida a todos los usuarios.
     * @param game - Game con la partida actualizada
     * @param viewers - ArrayList de User (lista de usuarios) que son espectadores de la partida.
     * @param user - Usuario que informa de la finalización de la partida.
     */
    public void onNotifyGameFinish(Game game, ArrayList<User> viewers, User user) {
        for (DedicatedServer dServer : clients) {
            this.user = user;
            if (dServer.getUser() != null) {
                if (dServer.getUser().isConnected() && !(dServer.getUser().getUsername().equals(game.getUsername())) && !(user.getUsername().equals(dServer.getUser().getUsername()))) {
                    for (int i = 0; i < viewers.size(); i++) {
                        if (viewers.get(i).getUsername().equals(dServer.getUser().getUsername())) {
                            try {
                                dServer.objectOut.writeObject(game);
                                // dServer.objectOut.reset();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Método que sirve para desconectar un servidor dediado.
     */
    public void disconnectServer() {
        clients.remove(this);
    }

    /**
     * Método que sirve para informar del juego / de la partida.
     * @param gameInfo - Game con la información del juego para dicha partida.
     */
    public void onGetGameInfo(Game gameInfo) {
        try {
            objectOut.writeObject(gameInfo);
            //objectOut.reset();
            objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de que la partida ha sido actualizada / refrescada.
     * @param userGame - UserGame con la información de la partida.
     */
    public void onRefreshGame(UserGame userGame) {
        try {
            objectOut.reset();
            objectOut.writeObject(userGame);
            //objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para indicar que una partida ha finalizado.
     * @param game - Game con información de partida
     * @param winner - Usuario ganador
     * @param viewers - Espectadores
     * @param user - Usuario que informa de la finalización de partida.
     */
    public void onFinishGame(Game game, User winner, List<User> viewers, User user) {
        int posWinner = -1;
        try {
            posWinner = getPosByUserByName(winner.getUsername());
            setUser(user);
            if (posWinner != -1) {
                game.setMessage(new Message(0,winner.getUsername()+"has won the game"));
                clients.get(posWinner).objectOut.writeObject(winner);
                clients.get(posWinner).objectOut.writeObject(game);
            }
            onNotifyGameFinish(game, (ArrayList<User>) viewers, user);
//callback.onGetPublicMatches(new UserMatch(user.getUsername()),JOIN,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que sirve para informar de que una trop ha sido ubicada.
     * @param userGame - UserGame con la información del juego / partida actualizada y con la tropa ya ubicada en el tablero.
     */
    public void onLocateTroop(UserGame userGame) {
        try {
            objectOut.writeObject(userGame);
            objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Método que sirve para informar de la creación de una tropa.
     * @param userGame - UserGame que contiene la información de la partida y los jugadores y la tropa comprada / creada.
     */
    public void onCreateTroop(UserGame userGame) {
        try {
            objectOut.writeObject(userGame);
            objectOut.flush();
            objectOut.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectOutputStream getObjectOut() {
        return objectOut;
    }
}