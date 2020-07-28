package controller;

import model.entity.*;
import model.enumeration.InvitationStatus;
import model.enumeration.MatchStatus;
import model.manager.*;
import model.network.ConnectionConfig;
import model.network.DedicatedServer;
import model.network.NetworkCallback;
import model.network.Server;
import utils.Utility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la clase que nos hará de controlador principal y de la que se encargará de una vez se ha recibido la petición, tratarla y interactuar con lógica y datos.
 * Trata lo que recibe por networking y lo envía a modelo ya sea manager y luego este puede que a DB mediante DAO.
 */
public class SystemController implements NetworkCallback {
    /**Configuración de la conexión (Server & DB).*/
    private ConnectionConfig config;
    /**Este atributo de case Server (objeto server) es el que nos va a permitir estar a la escucha de nuevas peticiones por parte de clientes.*/
    private Server server;
    /**Este atributo es el que nos permite poder hacer solicitudes al UserDAO queries relacionadas con los usuarios.*/
    private UserManager userManager;
    /**Este atributo es el que nos va a permitir hacer solicitudes y queries a través del MatchDAO de las partidas.*/
    private MatchManager matchManager;
    /**Este atributo es el que nos va a permitir hacer solicitudes y queries a través del MatchInvitationDAO de la invitación de las partidas.*/
    private MatchInvitationManager matchInvitationManager;
    /**Este atributo es el que nos permite poder hacer solicitudes al PlayerDAO queries relacionadas de los jugadores.*/
    private PlayerManager playerManager;
    /**Este atributo es el que nos permite poder hacer solicitudes al TroopDAO queries relacionadas de las tropas.*/
    private TroopManager troopManager;
    public static final String JOIN_MATCH = "Join match";
    private static final String INVITE_FRIEND_MATCH = "Invite friend match";
    private static final String GAME_FINISHED = "Game finished";
    private static final String VALIDATE_FRIEND_INVITATION = "Validate friend invitation";

    /**
     * Crea el SystemController
     * @param userManager - Representa el UserManager
     * @param matchManager - Representa el MatchManager
     * @param matchInvitationManager - Representa el MatchInvitationManager
     * @param playerManager - Representa el PlayerManager
     * @param troopManager - Representa el TroopManager
     */
    public SystemController(UserManager userManager, MatchManager matchManager, MatchInvitationManager matchInvitationManager, PlayerManager playerManager, TroopManager troopManager) {
        config = Utility.getServerConfig();
        if (config != null) {
            server = new Server(config, this);
            server.startServer();
            this.userManager = userManager;
            this.matchManager = matchManager;
            this.matchInvitationManager = matchInvitationManager;
            this.playerManager = playerManager;
            this.troopManager = troopManager;
        }
    }

    /**
     * Método que sirve para registrar un usuario en el sistema (y por ende en la base de datos / DB).
     * @param user - Usuario a registrar en la DB
     * @param dServer - Servidor del cliente para dicho usuario y al cuál se le pasará un UserRegister con la información devuelta.
     */
    @Override
    public void onRegisterUser(User user, DedicatedServer dServer) {
        dServer.onRegisterUser(userManager.registerUser(user.getUsername(), user.getEmail(), user.getPassword()));
    }

    /**
     * Método que sirve para hacer el login del usuario.
     * @param userLogin - UserLogin con la información del usuario login (nombre de usuario o correo) y la contraseña.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onLoginUser(UserLogin userLogin, DedicatedServer dServer) {
        UserLogin userLogged = userManager.loginUser(userLogin.getUser().getUsername(), userLogin.getUser().getPassword());
        dServer.onLoginUser(userLogged);
        if (userLogged.getMessage().getCode() == 0) {
            dServer.onNotifyLogin(userManager.getFriendList(userLogin.getUser().getUsername()));
        }
    }

    /**
     * Método que sirve para hacer la desconexión del usuario en el sistema.
     * @param user - Usuario que quiere desloguearse.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onLogoutUser(User user, DedicatedServer dServer) {
        Game game = new Game();
        Match match = matchManager.getLastMatchByUserId(playerManager.getPlayerById(userManager.getUserByName(user.getUsername()).getId()).getId());
        game.setMatch(match);
        game.setAction(GAME_FINISHED);
        List<User> viewers = new ArrayList<>();
        viewers.addAll(userManager.getSpectatorsByMatchId(match.getId()));
        User winner, looser;
        if (match.getPlHost().getUsername().equals(user.getUsername())) {
            winner = userManager.getUserByName(match.getPlGuest().getUsername());
            looser = userManager.getUserByName(match.getPlHost().getUsername());
            game.setRol(MatchManager.GUEST);
        } else {
            winner = userManager.getUserByName(match.getPlHost().getUsername());
            looser = userManager.getUserByName(match.getPlGuest().getUsername());
            game.setRol(MatchManager.HOST);
        }
        game.setMessage(new Message(0, "The winner is " + winner.getUsername() + "!"));
        Player playerWinner = playerManager.getPlayerById(winner.getId());
        match.setWinner(playerWinner);
        game.setUsername(winner.getUsername());
        matchManager.updateWinner(playerWinner, match.getId(),match.getMatchTime());
        userManager.updateStatistics(winner.getUsername(), looser.getUsername());
        winner = userManager.getUserByName(winner.getUsername());
        userManager.banUser(user.getUsername());
        //user = userManager.getUserByName(looser.getUsername());
        dServer.onFinishGame(game, winner, viewers, user);
        UserMatch userMatch = new UserMatch();
        userMatch.setMatches(matchManager.getPublicMatches().getMatches());
        dServer.onGetPublicMatchesAllClients(userMatch);
        dServer.disconnectServer();
    }

    /**
     * Método que sirve para especificar que una partida ha finalizado y que un jugador ha ganado la partida.
     * @param username - Nombre de usuario que ha ganado la partida.
     * @param matchName - Nombre de la partida de la cual se quiere especificar que ha finalizado y especificar el jugador que la ha ganado.
     * @param matchTime - Tiempo de duración de la partida.
     */
    @Override
    public void onWin(String username, String matchName, int matchTime) {
        matchManager.updateWinner(playerManager.getPlayerByNameAndmatch(username,matchName),matchManager.getMatchByName(matchName).getId(),matchTime);
    }

    /**
     * Método que sirve para aumentar el dinero de ambos jugadores de la partida de modo pasivo.
     * @param plHost - Jugador Host
     * @param plGuest - Jugador Guest / Invitado
     */
    @Override
    public void onIncrementPlayersMoney(Player plHost, Player plGuest) {
        playerManager.incrementAvailableMoney(plHost,plGuest);
    }

    /**
     * Método que sirve para recuperar la información de una partida.
     * @param game - Juego que contiene la partida.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onGetGameInfo(Game game, DedicatedServer dServer) {
        game = matchManager.getGameInfo(game.getMatch().getMatchName(), game.getUsername());
        game.getMatch().setTroops(troopManager.getDBTroops());
        dServer.onGetGameInfo(game);
    }

    /**
     * Método que sirve para crar una solicitud de amistad.
     * @param friendInvitation - Solicitud de amistad
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     * @throws SQLException - SQL Exception
     */
    @Override
    public void onFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dServer) throws SQLException {
        if (!(friendInvitation.getUserName().equals(friendInvitation.getFriendName()))) {
            UserFriendInvitation userInviting = userManager.addFriend(friendInvitation.getUserName(), friendInvitation.getFriendName());
            UserFriendInvitation userInvited = null;
            if (userInviting.getMessage().getCode() == 0) {
                userInvited = userManager.getFriendInvitations(userInviting.getFriendName());
                userInvited.setFriendName(userInviting.getUserName());
            }
            dServer.onFriendInvitationCreated(userInviting, userInvited);
        } else {
            friendInvitation.setMessage(new Message(4, "Can't invite to yourself!"));
            dServer.onFriendInvitationFailure(friendInvitation);
        }
    }

    /**
     * Método que sirve para eliminar a un amigo
     * @param userFriendShip - UserFriendship con la datos de ambos usuarios y la relación de amistad.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     * @throws SQLException - SQL Exception
     */
    @Override
    public void onDeleteFriend(UserFriendship userFriendShip, DedicatedServer dServer) throws SQLException {
        dServer.onDeleteFriend(userManager.removeFriend(userFriendShip.getUserName(), userFriendShip.getFriendName()), userManager.getFriendList(userFriendShip.getFriendName()));
    }

    /**
     * Método que sirve para acceptar una solicitud de amistad.
     * @param friendInvitation - Solicitud de amistad
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onJoinFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dServer) {
        dServer.onUpdatingInvitation(userManager.acceptFriendInvitation(friendInvitation.getUserName(), friendInvitation.getFriendName()));
        dServer.onGetFriends(userManager.getFriendList(friendInvitation.getUserName()), userManager.getFriendList(friendInvitation.getFriendName()));
    }

    /**
     * Método que sirve para recuperar los amigos que tu tienes.
     * @param userFriendShip - UserFriendship instance / object con la información del usuario (nombre de usuario) para solicitar recuperar los amigos que tiene.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onGetFriends(UserFriendship userFriendShip, DedicatedServer dServer) {
        dServer.onGetFriends(userManager.getFriendList(userFriendShip.getUserName()), null);
    }

    /**
     * Método que sirve para rechazar una solicitud de amistad.
     * @param friendInvitation - Solicitud de amistad
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onRefuseFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dServer) {
        dServer.onUpdatingInvitation(userManager.refuseFriendInvitation(friendInvitation.getUserName(), friendInvitation.getFriendName()));
        dServer.onGetFriends(userManager.getFriendList(friendInvitation.getUserName()), userManager.getFriendList(friendInvitation.getFriendName()));
    }

    /**
     * Método que sirve para recuperar las invitaciones de amistad.
     * @param friendInvitation - UserFriendInvitation Instance / object con el nombre del usuario que quiere recuperar que solicitudes de amistad tiene.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onGetFriendInvitations(UserFriendInvitation friendInvitation, DedicatedServer dServer) {
        dServer.onGetFriendsInvitations(userManager.getFriendInvitations(friendInvitation.getUserName()));
    }

    /**
     * Método que sirve para poder añadir una partida.
     * @param userMatch - UserMatch con el nombre de la partida a registrar y el nombre del usuario que la va a crear.
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onAddMatch(UserMatch userMatch, DedicatedServer dServer) {
        Player player = new Player(userManager.getUserByName(userMatch.getUsername()).getId(), userMatch.getUsername(), 500, 500);
        player.setUserId(userManager.getUserByName(userMatch.getUsername()).getId());
        player = playerManager.createPlayer(player);
        dServer.onAddMatch(matchManager.addMatch(new Match(userMatch.getMatchName(), MatchStatus.Pending, true, player)));
        dServer.onNotifyNewMatch(matchManager.getPublicMatches());
    }

    /**
     * Método que sirve para poder unirse a una partida.
     * @param joinMatch UserMatch con el nombre de la partida a la que unirse y el nombre del usuario que va a unirse. (As Guest)
     * @param dServer - Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onJoinMatch(UserMatch joinMatch, DedicatedServer dServer) {
        Match match = matchManager.getMatchByName(joinMatch.getMatchName());
        User user = userManager.getUserByName(match.getPlHost().getUsername());
        if (user.isConnected()) {
            Player playerGuest = new Player(joinMatch.getUsername(), 500, 500);
            playerGuest.setUserId(userManager.getUserByName(joinMatch.getUsername()).getId());
            playerGuest = playerManager.createPlayer(playerGuest);
            joinMatch = matchManager.joinMatch(playerGuest.getId(), matchManager.getMatchByName(joinMatch.getMatchName()).getId());
            joinMatch.setUsername(joinMatch.getUsername());
            joinMatch.setMatchName(match.getMatchName());
            joinMatch.setUsername(user.getUsername());
            dServer.onJoinMatch(joinMatch);
            dServer.onValidateMatch(joinMatch, match.isPublic(), user, playerGuest);
            //en system controller
        } else {
            joinMatch.setUsername(user.getUsername());
            matchManager.finishGame(user.getId());
            joinMatch.setMessage(new Message(1, "The host user has been disconnected!"));
            dServer.onValidateMatch(joinMatch, match.isPublic(), user, null);
        }
    }

    /**
     * Método el cual va a permitir que un usuario se apunte a visualizar una partida como espectador.
     * @param viewMatch - UserMatch con el nombre de la partida a la que unirse como espectador y el nombre del usuario que va a unirse.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onViewMatch(UserMatch viewMatch, DedicatedServer dServer) {
        Match match = matchManager.getMatchByName(viewMatch.getMatchName());
        User user = userManager.getUserByName(match.getPlHost().getUsername()), guest = userManager.getUserByName(match.getPlGuest().getUsername());
        User viewer = null;
        if (user.isConnected() && guest.isConnected()) {
            viewer = userManager.getUserByName(viewMatch.getUsername());
            viewMatch = matchManager.viewMatch(userManager.getUserByName(viewMatch.getUsername()).getId(), matchManager.getMatchByName(viewMatch.getMatchName()).getId());
            viewMatch.setMatchName(match.getMatchName());
            dServer.onViewMatch(viewMatch);
            dServer.onValidateMatch(viewMatch, match.isPublic(), null, null);
            server.addAnSpectatorToGame(match.getMatchName(), viewer);
        } else {
            matchManager.finishGame(user.getId());
            viewMatch.setMessage(new Message(1, "The host user has been disconnected!"));
            dServer.onValidateMatch(viewMatch, match.isPublic(), null, null);
        }
    }

    /**
     * Método que sirve para recuperar las partidas públicas.
     * @param userMatch - UserMatch Instance con la información del usuario para poder recuperar las partidas públicas.
     * @param type - Tipo que nos va permitir saber si el usuario que quier recuperar las partidas ya creadas (Guest or Viewer) o si es el creador de la partida.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onGetPublicMatches(UserMatch userMatch, String type, DedicatedServer dServer) {
        User user = userManager.getUserByName(userMatch.getUsername());
        UserMatch findPublicMatches = new UserMatch();
        if (user != null) {
            findPublicMatches = matchManager.getPublicMatches();
        } else {
            findPublicMatches.setMessage(new Message(1, "SQL Exception!"));
        }
        if (!(type.equals(DedicatedServer.JOIN) || type.equals(DedicatedServer.VIEW))) {
            dServer.onGetPublicMatches(findPublicMatches);
        } else
            dServer.onNotifyNewMatch(findPublicMatches);
    }
    /**
     * Método que sirve para poder crear una invitación a una partida privada.
     * @param userMatchInvitation - Recibe una instancia de UserMatchInvitation con la información de la invitación de partida a crear.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    //todo not create match until match has been created. here only create matchinvitation.
    @Override
    public void onCreateMatchInvitation(UserMatchInvitation userMatchInvitation, DedicatedServer dServer) {
        UserMatchInvitation userMatchInvitationGuest = new UserMatchInvitation();
        UserMatchInvitation userMatchInvitationHost = new UserMatchInvitation();
        userMatchInvitationHost.setUsername(userMatchInvitation.getUsername());
        userMatchInvitationGuest.setUsername(userMatchInvitation.getFriendName());
        MatchInvitation matchInvitation = null;
        userMatchInvitationGuest.setMatchInvitations(new ArrayList<>());
        User user = userManager.getUserByName(userMatchInvitation.getFriendName());
        if (user.isConnected()) {
            try {
                Match match = new Match(userMatchInvitation.getUsername() + "-" + userMatchInvitation.getFriendName(), MatchStatus.Pending, false, playerManager.getPlayerById(userManager.getUserByName(userMatchInvitation.getUsername()).getId()));

                Player playerHost = new Player(userMatchInvitation.getUsername(), 500, 500);
                playerHost.setUserId(userManager.getUserByName(userMatchInvitation.getUsername()).getId());
                playerHost = playerManager.createPlayer(playerHost);
                match.setPlHost(playerHost);


                Player playerGuest = new Player(userMatchInvitation.getFriendName(), 500, 500);
                playerGuest.setUserId(userManager.getUserByName(userMatchInvitation.getFriendName()).getId());
                playerGuest = playerManager.createPlayer(playerGuest);
                match.setPlGuest(playerGuest);

                UserMatch userMatch = matchManager.addMatch(match);

                match = matchManager.getLastMatchByInvitation(match.getPlHost().getId(), match.getPlGuest().getId());

                if (userMatch != null && match != null) {
                    matchInvitation = matchInvitationManager.createMatchInvitation(match.getId(), userManager.getUserByName(userMatchInvitation.getUsername()).getId(), userManager.getUserByName(userMatchInvitation.getFriendName()).getId());
                    if (matchInvitation != null) {
                        userMatchInvitationGuest.setLastFriendInvitation(matchInvitation);
                        userMatchInvitationHost.setMatchInvitations(matchInvitationManager.getHostCreatedMatchInvitation(userManager.getUserByName(userMatchInvitation.getUsername()).getId()).getMatchInvitations());
                        userMatchInvitationHost.setMessage(new Message(0, "MatchInvitation has been sent successfully!"));
                        userMatchInvitationGuest.setMessage(new Message(1, "You have been invited!"));
                        userMatchInvitationGuest.setUsername(userMatchInvitation.getUsername());
                        userMatchInvitationGuest.setFriendName(userMatchInvitation.getFriendName());
                        userMatchInvitationGuest.setMatchName(match.getMatchName());
                        userMatchInvitationHost.setAction(INVITE_FRIEND_MATCH);
                        userMatchInvitationGuest.setAction(INVITE_FRIEND_MATCH);

                    } else {
                        userMatchInvitationGuest.setMessage(new Message(1, "It has been an error!"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            userMatchInvitationHost.setMessage(new Message(4, "The friend is not connected!"));
            userMatchInvitationHost.setAction(INVITE_FRIEND_MATCH);
        }
        dServer.onCreateMatchInvitation(userMatchInvitationHost, userMatchInvitationGuest);
    }

    /**
     * Método que sirve para recuperar la información de un usuario.
     * @param user - User del cual se quiere obtener su información (se pasa su nombre de usuario).
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onGetUserInfo(User user, DedicatedServer dServer) {
        dServer.onGetUserInfo(userManager.getUserByName(user.getUsername()));
    }

    /**
     * Método que sirve para rechazar una solicitud de invitación de  amistad.
     * @param userMatchInvitation - Invitación de amistad.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onRefuseMatchInvitation(UserMatchInvitation userMatchInvitation, DedicatedServer dServer) {
        boolean isSuccess = matchInvitationManager.refuseMatchInvitation(matchManager.getMatchByName(userMatchInvitation.getMatchName()).getId(), userManager.getUserByName(userMatchInvitation.getFriendName()).getId());
        if (isSuccess) {
            matchManager.cancelMatch(userManager.getUserByName(userMatchInvitation.getUsername()).getId());
            userMatchInvitation.setMessage(new Message(3, "The friend has refused the matchinvitation!"));
            dServer.onRefuseMatchInvitation(userMatchInvitation);
        }
    }

    /**
     * Método que sirve para cancelar una partida.
     * @param object - Recibe como parámtro una solicitud de invitación a partida por la cual se va a cancelar la partida.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onCancelMatch(UserMatchInvitation object, DedicatedServer dServer) {
        User user = userManager.getUserByName(object.getUsername());
        Player player = playerManager.getPlayerById(user.getId());
        dServer.onCancelMatch(matchManager.cancelMatch(player.getId()));
        dServer.onNotifyNewMatch(matchManager.getPublicMatches());
    }

    /**
     * Métod que sirve para recuperar la lista de amigos una vez se conecta.
     * @param friend - Friend que indica el nombre del usuario del cual obtener sus amigos.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onFriendConnected(Friend friend, DedicatedServer dServer) {
        dServer.onFriendConnected(userManager.getFriendList(friend.getUser().getUsername()));
    }

    /**
     * Método que sirve para desconectar a un cliente.
     * @param userName - Nombre del usuario que se va a desconectar / logout.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onDisconnectClient(String userName, DedicatedServer dServer) {
        userManager.logoutUser(userName);
        dServer.onNotifyLogout(userManager.getFriendList(userName), userManager.getUserByName(userName));
    }

    /**
     * Método que sirve para resetear el servidor.
     */
    @Override
    public void onResetServer() {
        userManager.resetUserStatus();
    }

    /**
     * Método que sirve para acceptar una solicitud de partida.
     * @param userMatchInvitation - Solicitud de partida.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onAcceptMatchInvitation(UserMatchInvitation userMatchInvitation, DedicatedServer dServer) {
        UserMatch acceptedMatch;
        MatchInvitation matchInvitation = matchInvitationManager.getLastMatchInvitationByFriendsId(userManager.getUserByName(userMatchInvitation.getUsername()).getId(), userManager.getUserByName(userMatchInvitation.getFriendName()).getId());
        if (matchInvitation.getStatus().equals(InvitationStatus.Pending)) {
            boolean isSuccess = matchInvitationManager.acceptMatchInvitation(matchManager.getMatchByName(userMatchInvitation.getMatchName()).getId(), userManager.getUserByName(userMatchInvitation.getFriendName()).getId());
            if (isSuccess) {
                userMatchInvitation.setMessage(new Message(2, "The friend has accepted the matchinvitation!"));
                acceptedMatch = matchManager.joinMatch(playerManager.getPlayerById(userManager.getUserByName(userMatchInvitation.getFriendName()).getId()).getId(), matchManager.getMatchByName(userMatchInvitation.getMatchName()).getId());
                dServer.onAcceptMatchInvitation(userMatchInvitation);
                userMatchInvitation.setMessage(new Message(0, "The friend has accepted the matchinvitation!"));
                dServer.onValidateFriendInvitation(userMatchInvitation);
                onCreateGameRoom(acceptedMatch,userManager.getUserByName(acceptedMatch.getUsername()),playerManager.getPlayerByNameAndmatch(acceptedMatch.getFriendName(),acceptedMatch.getMatchName()),dServer);
            }
        } else {
            userMatchInvitation.setMessage(new Message(1, "The invitation has expired!"));
            dServer.onValidateFriendInvitation(userMatchInvitation);
        }
    }

    /**
     * Método que sirve para crear una tropa.
     * @param userGame - Instancia de UserGame que lleva la información de los usuarios y de la partida y de la tropa a crear en la partida.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onCreateTroop(UserGame userGame, DedicatedServer dServer) {
        //insertar tropa sense localització
        if (userGame.getGame().getRol().equals("Host")) {
            //Player player, Troop troop, Game game
            userGame = troopManager.createTroop(userGame.getGame().getMatch().getPlHost(), troopManager.getTroopInfoByName(userGame.getTroop().getName()), userGame.getGame()); //player, Troop (name), game

            dServer.onCreateTroop(userGame);
            server.updateUserGame((ArrayList<Troop>) userGame.getGame().getMatch().getPlGuest().getTroops(),userGame.getGame().getMatch().getPlGuest(),userGame.getGame().getMatch().getMatchName());
            // server.onNotifyGameInfo(userGame.getGame(),userGame.getPlayerHost().getUsername());
        } else {
            userGame = troopManager.createTroop(userGame.getGame().getMatch().getPlGuest(), troopManager.getTroopInfoByName(userGame.getTroop().getName()), userGame.getGame()); //player, Troop (name), game
            dServer.onCreateTroop(userGame);
            server.updateUserGame((ArrayList<Troop>) userGame.getGame().getMatch().getPlGuest().getTroops(),userGame.getGame().getMatch().getPlGuest(),userGame.getGame().getMatch().getMatchName());
            // server.onNotifyGameInfo(userGame.getGame(),userGame.getPlayerHost().getUsername());
        }

    }

    /**
     * Método el cual servirá para localizar a una tropa en la partida.
     * @param userGame - Instancia de UserGame que lleva la información de los usuarios y de la partida y de la tropa a ubicar en la partida.
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    /*todo
    boolean=> amb locateTroop() em passarà same as createTroop but diff action and give me row & col dins de troop
    passar grid visual como la matriz de modificaciones! (passaré board (Cell[][]) i una de boolean[][]
    *  */
    @Override
    public void onLocateTroop(UserGame userGame, DedicatedServer dServer) {
        if (userGame.getGame().getRol().equals("Host")) {
            userGame = troopManager.locateTroop(userGame.getPlayerHost(), userGame.getTroop(), userGame.getGame()); //player, Troop (name), game
            userGame.getGame().getMatch().getPlHost().setAvailableMoney(playerManager.getPlayerByNameAndmatch(userGame.getPlayerHost().getUsername(),userGame.getGame().getMatch().getMatchName()).getAvailableMoney());
        } else {
            userGame = troopManager.locateTroop(userGame.getPlayerGuest(), userGame.getTroop(), userGame.getGame()); //player, Troop (name), game
            userGame.getGame().getMatch().getPlGuest().setAvailableMoney(playerManager.getPlayerByNameAndmatch(userGame.getPlayerGuest().getUsername(),userGame.getGame().getMatch().getMatchName()).getAvailableMoney());
        }
        Troop troop = troopManager.getTroopInfoByName(userGame.getTroop().getName());
        troop.setRowLocation(userGame.getTroop().getRowLocation());
        troop.setColLocation(userGame.getTroop().getColLocation());
        userGame.setTroop(troop);
        dServer.onLocateTroop(userGame);
        userGame.setAction(DedicatedServer.REFRESH_GAME);
        server.updateTroops(userGame);
    }


    @Override
    public void onGetTroops(String matchName, DedicatedServer dServer) {
        //  dServer.onGetMatchTroopsAllClients(troopManager.getTroopsInfoByMatchName(matchName));
    }


    /**
     * Método que sirve para mover una tropa en la partida.
     * @param userGame - Instancia de UserGame con la tropa a mover, la partida y el usuario que la mueve.
     */
    @Override
    public void onMoveTroop(UserGame userGame) {
        troopManager.moveTroop(userGame);
    }

    /**
     *  Método que sirve para recuperar las tropas de un usuario host y las del guest.
     * @param matchName - Nombre de partida
     * @param username - Nombre de usuario
     * @param guestName - Nombre de usuario guest
     * @param server -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void getUserTroopsById(String matchName, String username, String guestName, Server server) {
        //todo to review
        //todo maybe its on the select IN TROOPDAO OR MATCHDAO METHOD to get data
        server.updateUserGame(troopManager.getTroopsByUserId(matchManager.getMatchByName(matchName).getId(), userManager.getUserByName(username).getId()), playerManager.getPlayerById(userManager.getUserByName(username).getId()), matchName);
        server.updateUserGame(troopManager.getTroopsByUserId(matchManager.getMatchByName(matchName).getId(), userManager.getUserByName(guestName).getId()), playerManager.getPlayerById(userManager.getUserByName(guestName).getId()), matchName);
    }

    /**
     * Método que sirve para actualizar las estadísticas de la partida.
     * @param winnerName - nombre del jugador ganador.
     * @param looserName - nombre del jugador perdedor.
     * @param winnerClient -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado. (del jugador ganador)
     * @param looserClient - -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado. (del jugador perdedor)
     */
    @Override
    public void onUpdateStatistics(String winnerName, String looserName, DedicatedServer winnerClient, DedicatedServer looserClient) {
        userManager.updateStatistics(winnerName, looserName);
        winnerClient.onGetUserInfo(userManager.getUserByName(winnerName));
        looserClient.onGetUserInfo(userManager.getUserByName(looserName));
    }


    /**
     * Método que sirve para recuperar la información de los jugadores de una partida.
     * @param hostName - Nombre Jugador principal
     * @param guestName - Nombre Jugador invitado
     * @param matchName - nombre de partida
     */
    @Override
    public void onGetPlayersInfo(String hostName, String guestName, String matchName) {
        server.updatePlayerInfo(playerManager.getPlayerByNameAndmatch(hostName, matchName), playerManager.getPlayerByNameAndmatch(guestName, matchName), matchName);
    }

    /**
     * Método que sirve para cancelar una invitación de partida
     * @param object - Invitación de la partida a cancelar
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onCancelMatchInvitation(UserMatchInvitation object, DedicatedServer dServer) {
        User user = userManager.getUserByName(object.getUsername());
        Player player = playerManager.getPlayerById(user.getId());
        matchInvitationManager.cancelMatchInvitation(matchInvitationManager.getLastMatchInvitationByUserId(user.getId()).getMatchId(), user.getId());
        dServer.onCancelMatch(matchManager.cancelMatch(player.getId()));
        dServer.onNotifyNewMatch(matchManager.getPublicMatches());
    }

    /**
     * Método qeu sirve para crear una room donde jugarán y se llevará a cabo la partida.
     * @param joinMatch - UserMatch con info de la partida y guadores
     * @param user - Usuario Host
     * @param playerGuest - Jugador guest
     * @param dServer -  Servidor dedicado para atender peticiones del cliente  del usuario que hay conectado.
     */
    @Override
    public void onCreateGameRoom(UserMatch joinMatch, User user, Player playerGuest, DedicatedServer dServer) {
        // dServer.onCreateGameRoom(new Game(matchManager.getMatchByName(joinMatch.getMatchName()),joinMatch.getMessage(),"JOIN_ROOM","Guest",joinMatch.getUsername()),playerManager.getPlayerById(user.getId()),playerGuest);
        server.addGameToGameRoom(new Game(matchManager.getMatchByName(joinMatch.getMatchName()), joinMatch.getMessage(), "JOIN_ROOM", "Guest", joinMatch.getUsername()), playerManager.getPlayerById(user.getId()), playerGuest);
    }

}