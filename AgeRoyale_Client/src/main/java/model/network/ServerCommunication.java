package model.network;

import model.entity.*;

import java.io.*;
import java.net.Socket;

/** Representa el ServerCommunication */
public class ServerCommunication extends Thread {
    /** Representa si la comunicación con el servidor esta activa (True) o no (False) */
    private boolean isOn;
    /** Representa el socket de la comunicación del servidor */
    private Socket socketToServer;
    /** Permite enviar datos (objectos) al servidor */
    private ObjectOutputStream objectOut;
    /** Permite recibir datos (objectos) del servidor */
    private ObjectInputStream objectIn;
    /** Representa el NetworkCallback que permite comunicarse con los controladores (Excepto el GameController) */
    private NetworkCallback callback;
    /** Representa el GameCallback que permite comunicarse con el GameController */
    private GameCallback gameCallback;

    /* Constantes para identificar las acciones del usuario */
    private static final String LOGIN_USER = "Login user";
    private static final String REGISTER_USER = "Register user";
    private static final String LOGOUT_USER = "Logout user";

    private static final String GET_FRIEND_LIST = "Get friend list";
    private static final String ADD_FRIEND = "Add friend";
    private static final String DELETE_FRIEND = "Delete friend";
    private static final String INVITE_FRIEND_MATCH = "Invite friend match";
    private static final String ACCEPT_FRIEND_MATCH = "Accept friend match";
    private static final String DECLINE_FRIEND_MATCH = "Decline friend match";
    private static final String VALIDATE_FRIEND_INVITATION = "Validate friend invitation";

    private static final String GET_INVITATION_LIST = "Get invitation list";
    private static final String ACCEPT_FRIEND = "Accept friend";
    private static final String DECLINE_FRIEND = "Decline friend";

    private static final String GET_MATCH_LIST = "Get match list";
    private static final String CREATE_MATCH = "Create match";
    private static final String JOIN_MATCH = "Join match";
    private static final String VIEW_MATCH = "View match";
    private static final String VALIDATE_MATCH = "Validate match";

    private static final String GET_GAME_INFO = "Get game info";
    private static final String GAME_FINISHED = "Game finished";

    private static final String CANCEL_MATCH = "Cancel match";
    private static final String CANCEL_MATCH_INVITATION = "Cancel match invitation";

    private static final String CREATE_TROOP ="Create troop";
    private static final String LOCATE_TROOP ="Locate troop";
    private static final String REFRESH_GAME ="Refresh game";
    /**/

    /** Representa un identificador en caso de que el servidor haya caido */
    public static final int SERVER_DISCONNECTION_CODE = 0;

    /** Representa un identificador en caso de que un cliente se haya desconectado */
    public static final int CLIENT_DISCONNECTION_CODE = 1;

    /**
     * Crea un ServerCommunication
     * @param config - Contiene la información necesaria para conectarse via sockets con el servidor
     */
    public ServerCommunication(ConnectionConfig config) {
        try {
            this.isOn = false;
            this.socketToServer = new Socket(config.getServerCommunicationIP(), config.getServerCommunicationPort());
            this.objectOut = new ObjectOutputStream(socketToServer.getOutputStream());
            this.objectIn = new ObjectInputStream(socketToServer.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite empezar la comunicación con el servidor
     */
    public void startServerCommunication() {
        isOn = true;
        this.start();
    }

    /**
     * Permite realizar la desconexión con el servidor
     * @param option - Identifica si la conexión ha sido por parte del cliente o del servidor
     */
    public void stopServerCommunication(int option) {
        if (option == SERVER_DISCONNECTION_CODE) {
            if (callback != null) {
                callback.stoppedServer();
            }
            if (gameCallback != null) {
                gameCallback.stoppedServer();
            }
        }
        this.isOn = false;
        this.interrupt();
    }

    /**
     * Motor de la aplicación, permite continuamente recibir datos del servidor siempre y cuando haya una comunicación establecida, informando a los controladores y según los datos recibidos se realizarán distintas acciones mediante la ejecución de metodos callback.
     */
    public void run() {
        try {
            while (isOn) {
                Object object = objectIn.readObject();

                if (callback != null) {
                    if (object instanceof UserLogin) {
                        if (((UserLogin) object).getAction().equals(LOGIN_USER)) {
                            callback.onLoginUser((UserLogin) object);
                        } else if (((UserLogin) object).getAction().equals(REGISTER_USER)) {
                            callback.onRegisterUser((UserLogin) object);
                        }
                    } else if (object instanceof User) {
                        callback.onGetUserInfo((User) object);
                    } else if (object instanceof UserFriendship) {
                        if (((UserFriendship) object).getAction().equals(GET_FRIEND_LIST)) {
                            callback.onGetFriendList((UserFriendship) object);
                        } else if (((UserFriendship) object).getAction().equals(DELETE_FRIEND)) {
                            callback.onDeleteFriend((UserFriendship) object);
                        }
                    } else if (object instanceof UserFriendInvitation) {
                        if (((UserFriendInvitation) object).getAction().equals(ADD_FRIEND)) {
                            callback.onAddFriend((UserFriendInvitation) object);
                        } else if (((UserFriendInvitation) object).getAction().equals(GET_INVITATION_LIST)) {
                            callback.onGetInvitationList((UserFriendInvitation) object);
                        } else if (((UserFriendInvitation) object).getAction().equals(ACCEPT_FRIEND)) {
                            callback.onAcceptFriend((UserFriendInvitation) object);
                        } else if (((UserFriendInvitation) object).getAction().equals(DECLINE_FRIEND)) {
                            callback.onDeclineFriend((UserFriendInvitation) object);
                        }
                    } else if (object instanceof UserMatch) {
                        if (((UserMatch) object).getAction().equals(GET_MATCH_LIST)) {
                            callback.onGetMatchList((UserMatch) object);
                        } else if (((UserMatch) object).getAction().equals(CREATE_MATCH)) {
                            callback.onCreateMatch((UserMatch) object);
                        } else if (((UserMatch) object).getAction().equals(JOIN_MATCH)) {
                            callback.onJoinMatch((UserMatch) object);
                        } else if (((UserMatch) object).getAction().equals(VIEW_MATCH)) {
                            callback.onViewMatch((UserMatch) object);
                        } else if (((UserMatch) object).getAction().equals(VALIDATE_MATCH)) {
                            callback.onValidateMatch((UserMatch) object);
                        }
                    } else if (object instanceof UserMatchInvitation) {
                        if (((UserMatchInvitation) object).getAction().equals(INVITE_FRIEND_MATCH)) {
                            callback.onInviteFriendToMatch((UserMatchInvitation) object);
                        } else if (((UserMatchInvitation) object).getAction().equals(ACCEPT_FRIEND_MATCH)) {
                            callback.onInviteFriendToMatch((UserMatchInvitation) object);
                        } else if (((UserMatchInvitation) object).getAction().equals(DECLINE_FRIEND_MATCH)) {
                            callback.onInviteFriendToMatch((UserMatchInvitation) object);
                        } else if (((UserMatchInvitation) object).getAction().equals(CANCEL_MATCH)) {
                            callback.onCancelMatch((UserMatchInvitation) object);
                        } else if (((UserMatchInvitation) object).getAction().equals(VALIDATE_FRIEND_INVITATION)) {
                            callback.onValidateMatchInvitation((UserMatchInvitation) object);
                        }
                    }
                }
                if (gameCallback != null) {
                    if (object instanceof Game) {
                        if (((Game) object).getAction().equals(GET_GAME_INFO)) {
                            gameCallback.getGameInfo((Game) object);
                        } else if (((Game) object).getAction().equals(GAME_FINISHED)) {
                            gameCallback.onFinishGame((Game) object);
                        }
                    }else if (object instanceof User){
                        gameCallback.onUpdateStatistics((User) object);
                    }else if(object instanceof UserGame){
                        if (((UserGame) object).getAction().equals(CREATE_TROOP)) {
                            gameCallback.onCreateTroop((UserGame) object);
                        }else if(((UserGame) object).getAction().equals(LOCATE_TROOP)){
                            gameCallback.onLocateTroop((UserGame) object);
                        }else if(((UserGame) object).getAction().equals(REFRESH_GAME)) {
                            gameCallback.onRefreshGame((UserGame) object);
                        }
                    }

                }
                objectOut.reset();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
        stopServerCommunication(SERVER_DISCONNECTION_CODE);
    }

    /**
     * Permite enviar los datos necesarios al servidor para registrar un usuario
     * @param userLogin - Contiene la información que hace referencia al registro de un usuario
     */
    public void sendUserRegister(UserLogin userLogin) {
        try {
            userLogin.setAction(REGISTER_USER);
            objectOut.writeObject(userLogin);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para iniciar sesión un usuario
     * @param userLogin - Contiene la información que hace referencia al inicio de sesión de un usuario
     */
    public void sendUserLogin(UserLogin userLogin) {
        try {
            userLogin.setAction(LOGIN_USER);
            objectOut.writeObject(userLogin);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para cerrar la sesión de un usuario
     * @param userLogin - Contiene la información que hace referencia al cierre de sesión de un usuario
     */
    public void sendUserLogout(UserLogin userLogin) {
        try {
            userLogin.setAction(LOGOUT_USER);
            objectOut.writeObject(userLogin);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }


    /**
     * Permite enviar los datos necesarios al servidor para obtener los datos de un usuario
     * @param user - Objeto User que contiene la información del usuario actual
     */
    public void getUserInfo(User user) {
        try {
            objectOut.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para obtener el listado de amigos de un usuario
     * @param user - Objeto User que contiene la información del usuario actual
     */
    public void getFriendList(User user) {
        try {
            objectOut.writeObject(new UserFriendship(user.getUsername(), GET_FRIEND_LIST));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para obtener el listado de solicitudes de amistades de un usuario
     * @param user - Objeto User que contiene la información del usuario actual
     */
    public void getInvitationList(User user) {
        try {
            objectOut.writeObject(new UserFriendInvitation(user.getUsername(), GET_INVITATION_LIST));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para añadir a un usuario como amigo
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que es añadido como amigo
     */
    public void addFriend(String username, String friendName) {
        try {
            objectOut.writeObject(new UserFriendInvitation(username, friendName, ADD_FRIEND));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para eliminar a un usuario como amigo
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que es eliminado como amigo
     */
    public void deleteFriend(String username, String friendName) {
        try {
            objectOut.writeObject(new UserFriendship(username, friendName, DELETE_FRIEND));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para aceptar a un usuario como amigo
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que es aceptado como amigo
     */
    public void acceptFriend(String username, String friendName) {
        try {
            objectOut.writeObject(new UserFriendInvitation(username, friendName, ACCEPT_FRIEND));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para rechazar a un usuario como amigo
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que es rechazar como amigo
     */
    public void declineFriend(String username, String friendName) {
        try {
            objectOut.writeObject(new UserFriendInvitation(username, friendName, DECLINE_FRIEND));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para obtener el listado de partidas públicas
     * @param user - Objeto User que contiene la información del usuario actual
     */
    public void getMatchList(User user) {
        try {
            objectOut.writeObject(new UserMatch(user.getUsername(), GET_MATCH_LIST));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para crear una partida pública
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param matchName - Representa el nombre de la partida a crear
     */
    public void createMatch(String username, String matchName) {
        try {
            objectOut.writeObject(new UserMatch(username, matchName, CREATE_MATCH));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para invitar a un amigo a jugar una partida
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param friendName - Representa el nombre del usuario que es invitado a jugar
     */
    public void inviteFriendToMatch(String username, String friendName) {
        try {
            objectOut.writeObject(new UserMatchInvitation(username, friendName, username + friendName, INVITE_FRIEND_MATCH));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para aceptar la invitación de un amigo para jugar una partida
     * @param userMatchInvitation - Contiene la información que hace referencia a la invitacion
     */
    public void acceptMatchInvitation(UserMatchInvitation userMatchInvitation) {
        try {
            userMatchInvitation.setAction(ACCEPT_FRIEND_MATCH);
            objectOut.writeObject(userMatchInvitation);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para rechazar la invitación de un amigo para jugar una partida
     * @param userMatchInvitation - Contiene la información que hace referencia a la invitacion
     */
    public void declineMatchInvitation(UserMatchInvitation userMatchInvitation) {
        try {
            userMatchInvitation.setAction(DECLINE_FRIEND_MATCH);
            objectOut.writeObject(userMatchInvitation);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para unise como jugador a una partida pública
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param matchName - Representa el nombre de la partida a unirse
     */
    public void joinMatch(String username, String matchName) {
        try {
            objectOut.writeObject(new UserMatch(username, matchName, JOIN_MATCH));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para unise como espectador a una partida pública
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param matchName - Representa el nombre de la partida a unirse
     */
    public void viewMatch(String username, String matchName) {
        try {
            objectOut.writeObject(new UserMatch(username, matchName, VIEW_MATCH));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para cancelar una partida pública
     * @param username - Representa el nombre del usuario que realiza la acción
     */
    public void cancelMatch(String username) {
        try {
            objectOut.writeObject(new UserMatchInvitation(username, CANCEL_MATCH));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para cancelar una invitación a un amigos para jugar una partida
     * @param username - Representa el nombre del usuario que realiza la acción
     */
    public void cancelMatchInvitation(String username) {
        try {
            objectOut.writeObject(new UserMatchInvitation(username, CANCEL_MATCH_INVITATION));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Función encargada de registrar el controlador que recibira la información sobre t@do lo que no esté relacionado con una partida en curso
     * @param callback - Representa el NetWorkCallback (Cualquier controlador excepto el GameController)
     */
    public void registerCallback(NetworkCallback callback) {
        this.callback = callback;
    }

    /**
     * Permite enviar los datos necesarios al servidor para obtener la información de una partida
     * @param username - Representa el nombre del usuario que realiza la acción
     * @param matchName - Representa el nombre de la partida
     */
    public void getMatchInfo(String username, String matchName) {
        try {
            Match match = new Match();
            match.setMatchName(matchName);
            objectOut.writeObject(new Game(match, null, GET_GAME_INFO, null, username));
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Función encargada de registrar el controlador que recibira la información de la partida en curso
     * @param gameCallback - Representa el GameCallback (GameContoller)
     */
    public void registerGameCallback(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
    }

    /**
     * Permite enviar los datos necesarios al servidor para crear una tropa por parte de un player host/guest
     * @param userGame - Contiene la información que hace referencia a una partida en curso
     */
    public void createTroop(UserGame userGame) {
        try {
            userGame.setAction(CREATE_TROOP);
            objectOut.writeObject(userGame);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }

    /**
     * Permite enviar los datos necesarios al servidor para colocar una tropa en el tablero por parte de un player host/guest
     * @param userGame - Contiene la información que hace referencia a una partida en curso
     */
    public void locateTroop(UserGame userGame) {
        try {
            userGame.setAction(LOCATE_TROOP);
            objectOut.writeObject(userGame);
        } catch (IOException e) {
            e.printStackTrace();
            stopServerCommunication(SERVER_DISCONNECTION_CODE);
            System.out.println("** ESTA EL SERVIDOR EN EXECUCIO? **");
        }
    }
}