package model.network;

import model.entity.*;
import model.manager.TroopManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Server extends Thread implements NetworkCallback {
    private boolean isOn;
    private ConnectionConfig config;
    private ServerSocket sSocket;
    private LinkedList<DedicatedServer> dServers;
    private LinkedList<GameRoom> gameRooms;
    private NetworkCallback callback;

    public Server(ConnectionConfig config, NetworkCallback callback) {
        try {
            this.isOn = false;
            this.config = config;
            this.sSocket = new ServerSocket(config.getServerCommunicationPort());
            this.dServers = new LinkedList<>();
            this.gameRooms = new LinkedList<>();
            this.callback = callback;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para arrancar al servidor que estará a la escucha de nuevos clientes.
     */
    public void startServer() {
        isOn = true;
        this.start();
    }
    //Método que sirve para parar al servidor.
    public void stopServer() {
        isOn = false;
        this.interrupt();
    }

    /**
     * Métod o que sirve para mostrar cuantos DedicatedServer se tienen. (Se tiene un servidor dedicado para atender a 1 cliente (Tantos como clientes)).
     */
    public void showClients() {
        System.out.println("**** SERVER **** (" + dServers.size() + " clients / dedicated servers running)");
    }

    /**
     * Método principal del Server (Thread) y que se encarga de estar a la espera de nuevos clietnes.
     */
    public void run() {
        callback.onResetServer();
        while (isOn) {
            try {
                System.out.println("Esperando peticiones....");
                Socket sClient = sSocket.accept();

                DedicatedServer dServer = new DedicatedServer(sClient, dServers, this);
                dServers.add(dServer);

                dServer.registerCallback(callback);
                dServer.startDedicatedServer();
                showClients();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (DedicatedServer dServer : dServers) {
            dServer.stopDedicatedServer();
        }
    }

    /**
     * Método que sirve para añadir una partida a una sala.
     * @param game - Game con la información del juego que contiene la partida.
     * @param host - Jugador creador de la partida (host)
     * @param guest - Jugador invitado de la partida (guest)
     * @return Devuelve un booleano indicando si se ha podido añadir la partida a una sala o no.
     */
    public boolean addGameToGameRoom(Game game, Player host, Player guest) {
        int posHost = getDedicatedServerByUsername(host.getUsername());
        int posGuest = getDedicatedServerByUsername(guest.getUsername());
        int posRoom = -1;
        if (posHost != -1 && posGuest != -1 && game != null) {
            gameRooms.add(new GameRoom(dServers.get(posHost), dServers.get(posGuest), game));
            posRoom = getGameRoomByMatchName(game.getMatch().getMatchName());
            if (posRoom != -1) {
                gameRooms.get(posRoom).setCallback(callback);
                gameRooms.get(posRoom).startRoom(this);
                UserGame userGame = new UserGame();
                userGame.setGame(game);
                gameRooms.get(posRoom).setServer(this);
                if (game.getMatch().getPlHost().getTroops().size() == 0) {
                    this.refreshGame(userGame);
                } /*else{
                    this.updateTroops(userGame);*/

                //gameRooms.get(posRoom).onmatch();
            }
            return true;
        }
        return false;
    }

    /**
     * Método que sirve para parar una sala de una partida.
     * @param game - Game con la información del juego y que contiene la partida.
     */
    public synchronized void stopGameRoom(Game game) {
        int posRoom = -1;
        posRoom = getGameRoomByMatchName(game.getMatch().getMatchName());
        if (posRoom != -1) {
          //  gameRooms.get(posRoom).stopRoom();
            gameRooms.get(posRoom).interrupt();
            gameRooms.remove(posRoom);
        }
    }

    /**
     * Método que sirve para añadir un espectador a una partida dado el nombre de partida y el usuario del espectador.
     * @param matchName - Nombre de la partida
     * @param spectator - Usuario espectador
     * @return Devuelve un booleano (true o false) indicando si se ha podido o no añadir un espectador a una partida.
     */
    public boolean addAnSpectatorToGame(String matchName, User spectator) {
        int posSpectator = getDedicatedServerByUsername(spectator.getUsername());
        int posRoom = getGameRoomByMatchName(matchName);

        if (posSpectator != -1 && posRoom != -1 && matchName != null && matchName != "" && matchName != " ") {
            gameRooms.get(posRoom).addSpectator(dServers.get(posSpectator));
            return true;
        }
        return false;
    }

    /**
     * Método que sirve para recuperar la posición de servidor dedicado en la que se encuentra dicho usuario dado nombre de usaurio.
     * @param username - Nombre de usaurio
     * @return Devuelve un int que indica si se ha encontrado (num != -1) o -1 si no se encuentra dentro del atributo user dentro la lista de DedicatedServer.
     */
    private int getDedicatedServerByUsername(String username) {
        for (int i = 0; i < dServers.size(); i++) {
            if (dServers.get(i).getUser().getUsername().equals(username) && dServers.get(i).getUser().isConnected()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Método que sirve para encontrar una sala dentro de la lista de GameRoom dado un nombre de partida.
     * @param matchName - Nombre de la partida
     * @return Devuelve un int que indica si se ha encontrado (num != -1) o -1 si no se encuentra dentro del atributo matchName dentro de atributo Match dentro de Game dentro la lista de GameRoom.
     */
    private int getGameRoomByMatchName(String matchName) {
        for (int i = 0; i < dServers.size(); i++) {
            if (gameRooms.get(i).getGame().getMatch().getMatchName().equals(matchName)) {
                return i;
            }
        }
        return -1;
    }

    public LinkedList<GameRoom> getGameRooms() {
        return gameRooms;
    }

    public void setGameRooms(LinkedList<GameRoom> gameRooms) {
        this.gameRooms = gameRooms;
    }

    /**
     * Método que sirve para actualizar la sala con el UserGame actualizado.
     * @param userGame - UserGame con la información de la partida / juego actualizado.
     */
    public void refreshGame(UserGame userGame) {
        int posRoom = -1;
        try {
            posRoom = getGameRoomByMatchName(userGame.getGame().getMatch().getMatchName());
            if (posRoom != -1) {
                //gameRooms.get(posRoom).onmatch(userGame.getGame(),this);
                synchronized (this) {
                    gameRooms.get(posRoom).setGame(userGame.getGame());
                }
                userGame.setAction(DedicatedServer.REFRESH_GAME);
                //gameRooms.get(posRoom).setUserGame(userGame);
            }
        } catch (NullPointerException e) {
            System.out.println("Room no encontrada!");
        }
    }

    /**
     * Método que sirve para actualizar las tropas de la partida / juego.
     * @param userGame - UserGame con las tropas de la partida actualizadas (las de host o las de guest).
     */
    public void updateTroops(UserGame userGame) {
        int posRoom = -1;
        try {
            posRoom = getGameRoomByMatchName(userGame.getGame().getMatch().getMatchName());
            UserGame userGameGameRoom;
            if (posRoom != -1) {
                userGameGameRoom = gameRooms.get(posRoom).getUserGame();
                userGame.setAction(DedicatedServer.REFRESH_GAME);
                // System.exit(0);
                if (userGameGameRoom != null) {
                    if (userGame.getGame().getMatch().getPlHost().getTroops().size() >= 1 && userGame.getGame().getRol().equals("Host")) {
                        // userGame.getGame().getMatch().setPlHost(userGame.getGame().ge);
                        userGameGameRoom.getGame().getMatch().setPlHost(userGame.getGame().getMatch().getPlHost());
                        //todo  this.refreshGame(userGame);
                    } else if (userGame.getGame().getMatch().getPlGuest().getTroops().size() >= 1 && userGame.getGame().getRol().equals("Guest")) {
                        // userGame.getGame().getMatch().setPlHost(userGame.getGame().ge);
                        userGameGameRoom.getGame().getMatch().setPlGuest(userGame.getGame().getMatch().getPlGuest());
                        //todo  this.refreshGame(userGame);
                    }
                    synchronized (this) {
                        gameRooms.get(posRoom).setUserGame(userGameGameRoom);
                    }
                } else {
                    synchronized (this) {
                        gameRooms.get(posRoom).setUserGame(userGame);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Room no encontrada!");
        }
    }

    @Override
    public void onRegisterUser(User user, DedicatedServer dServer) {

    }

    @Override
    public void onLoginUser(UserLogin userLogin, DedicatedServer dedicatedServer) throws SQLException {

    }

    @Override
    public void onFriendInvitation(UserFriendInvitation object, DedicatedServer dedicatedServer) throws SQLException {

    }

    @Override
    public void onDeleteFriend(UserFriendship object, DedicatedServer dedicatedServer) throws SQLException {

    }

    @Override
    public void onJoinFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onGetFriends(UserFriendship object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onRefuseFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onGetFriendInvitations(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onAddMatch(UserMatch userMatch, DedicatedServer dServer) {

    }

    @Override
    public void onAcceptMatchInvitation(UserMatchInvitation matchInvitation, DedicatedServer dServer) {

    }

    @Override
    public void onCreateTroop(UserGame userGame, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onLocateTroop(UserGame troop, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onGetTroops(String matchName, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onGetPublicMatches(UserMatch userMatch, String type, DedicatedServer dedicatedServer) {

    }


    @Override
    public void onJoinMatch(UserMatch joinMatch, DedicatedServer dServer) {

    }

    @Override
    public void onViewMatch(UserMatch object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onCreateMatchInvitation(UserMatchInvitation userMatchInvitation, DedicatedServer dServer) {

    }

    @Override
    public void onGetUserInfo(User user, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onRefuseMatchInvitation(UserMatchInvitation object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onCancelMatch(UserMatchInvitation object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onFriendConnected(Friend friend, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onDisconnectClient(String userName, DedicatedServer dServer) {

    }

    @Override
    public void onResetServer() {

    }

    @Override
    public void onLogoutUser(User user, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onGetGameInfo(Game object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onCreateGameRoom(UserMatch joinMatch, User user, Player playerGuest, DedicatedServer dServer) {

    }

    @Override
    public void onMoveTroop(UserGame userGame) {

    }

    @Override
    public void getUserTroopsById(String matchName, String username, String guestName, Server server) {

    }

    @Override
    public void onUpdateStatistics(String username, String username1, DedicatedServer winnerClient, DedicatedServer looserClient) {

    }


    @Override
    public void onGetPlayersInfo(String hostName, String guestName, String matchName) {

    }

    @Override
    public void onCancelMatchInvitation(UserMatchInvitation object, DedicatedServer dedicatedServer) {

    }

    @Override
    public void onWin(String username, String matchName, int matchTime) {

    }

    @Override
    public void onIncrementPlayersMoney(Player plHost, Player plGuest) {

    }

    /**
     * Método que sirve para poder mantener actualizado el UserGame de una sala. (y poder actualizar las tropas) (para poder obtener las tropas).
     * @param troopsByUserId - Lista de tropas de usuario
     * @param player - Jugador que ha actualizado su lisa de tropas.
     * @param matchName - Nombre de la partida de la cual se obtiene la lista de tropas actualizada.
     */
    public void updateUserGame(ArrayList<Troop> troopsByUserId, Player player, String matchName) {
        int pos = -1;
        try {
            pos = getGameRoomByMatchName(matchName);
            if (pos != -1) {
                if (gameRooms.get(pos).getGame().getRol().equals("Host")) {
                    player.setTroops(troopsByUserId);
                    synchronized (this) {
                        gameRooms.get(pos).getGame().getMatch().setPlHost(player);
                        gameRooms.get(pos).getUserGame().getGame().getMatch().setPlHost(player);
                    }
                } else {
                    player.setTroops(troopsByUserId);
                    synchronized (this) {
                        gameRooms.get(pos).getGame().getMatch().setPlGuest(player);
                        gameRooms.get(pos).getUserGame().getGame().getMatch().setPlGuest(player);
                    }
                }
            }
        } catch (NullPointerException e) {
            //comment actually there aren't troops
        }

    }

    /**
     * Método que sirve para actualizar la información de los jugadores de una partida en su sala para dicha partida.
     * @param playerById - Id del jugador host.
     * @param playerById1 - Id del jugador guest.
     * @param matchName - Nombre de la partida
     */
    public void updatePlayerInfo(Player playerById, Player playerById1, String matchName) {
        int pos = -1;
        try {
            pos = getGameRoomByMatchName(matchName);
            if (pos != -1) {
                UserGame userGameAux = gameRooms.get(pos).getUserGame();
                userGameAux.getGame().getMatch().getPlHost().setVitalityPoints(playerById.getVitalityPoints());
                userGameAux.getGame().getMatch().getPlGuest().setVitalityPoints(playerById1.getVitalityPoints());
                synchronized (this) {
                    gameRooms.get(pos).setUserGame(userGameAux);
                }
            }
        } catch (NullPointerException e) {
            //comment actually there aren't troops
        }
    }
}