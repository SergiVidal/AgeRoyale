package model.network;

import model.entity.*;
import model.enumeration.CellType;
import model.enumeration.MatchStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Representa el GameRoom que nos permite poder llevar a cabo una partida y su gestión.
 */
public class GameRoom extends Thread {
    /**
     * Representa el DedicatedServer del cliente host.
     */
    private DedicatedServer hostClientServer;
    /**
     * Representa el DedicatedServer del cliente guest o invitado.
     */
    private DedicatedServer guestClientServer;
    /**
     * Representa la lista de  DedicatedServer de los espectadores.
     */
    private List<DedicatedServer> spectatorsClientServer;
    /**
     * Representa el juego, contiene la partida, el usuario que lo llama, el rol y un mensaje. Datos relevantes a partida.
     */
    private Game game;
    /**
     * Representa el server que está a la escucha de nuevos espectadores y que actualiza la partida.
     */
    private Server server;
    /**
     * Representa el callback / interfaz que sirve para llamar a métodos implementados por server y SystemController.
     */
    private NetworkCallback callback;
    /**
     * Representa un booleano para saber si la sala osea la partida sigue activa o no.
     */
    private boolean isOn;
    /**
     * Representa el UserGame con datos de jugadores y del juego que por ende tiene datos de partida.
     */
    private UserGame userGame;

    /**
     * Crea el GameRoom.
     * @param hostClientServer - Representa el DedicatedServer hostClientServer.
     * @param guestClientServer -  Representa el DedicatedServer guest.
     * @param game -  Representa el juego con datos de la partida.
     */
    public GameRoom(DedicatedServer hostClientServer, DedicatedServer guestClientServer, Game game) {
        this.hostClientServer = hostClientServer;
        this.guestClientServer = guestClientServer;
        this.game = game;
        this.spectatorsClientServer = new LinkedList<>();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Método que sirve para añadir un espectador a la sala de la partida.
     * @param dServer
     */
    public void addSpectator(DedicatedServer dServer) {
        if (dServer != null && dServer.getUser() != null && dServer.getUser().isConnected()) {
            if (!isAlreadyAnSpectator(dServer)) {
                spectatorsClientServer.add(dServer);
            }
        }
    }

    /**
     * Método que sirve para comprobar si un DedicatedServer ya es espectador.
     * @param dServer - DedicatedServer del cual se quiere comprobar si ya es espectador.
     * @return Devuelve un booleano que indica true o false, que significa que ya es espectador o no lo es respectivamente.
     */
    private boolean isAlreadyAnSpectator(DedicatedServer dServer) {
        for (DedicatedServer dedicatedServer : spectatorsClientServer) {
            if (dedicatedServer.equals(dServer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que sirve para notificar de cambios y avances en la partida al usuario host, al guest y a los espectadores.
     * @param userGame - UserGame con la partida, los usuarios y las tropas.
     */
    public void onNotifyRoomUsers(UserGame userGame) {
        //  if (username.equals(hostClientServer.getUser().getUsername())){
        hostClientServer.onRefreshGame(userGame);
        guestClientServer.onRefreshGame(userGame);
        // }/* else {
            /*hostClientServer.onGetGameInfo(game);
        }*/
        for (DedicatedServer dedicatedServer : spectatorsClientServer) {
            dedicatedServer.onRefreshGame(userGame);
        }
    }

    /**
     * Método que sirve para iniciar la sala donde se jugadrá la partida.
     * @param server - Server instance que crea la sala y con el que nos podremos comunicar, para que nos haga actualizaciones algunas en la sala.
     */
    public void startRoom(Server server) {
        this.isOn = true;
        this.start();
        this.server = server;
    }

    /**
     * Método principal de la class GameRoom donde se lleva  el flow y  la lógica del juego e irá haciendo solicitudes y respuestas y llamadas a métodos.
     * También se encarga de esperar por parte de jugadores y llama al method para notificar al host, al guest y al espectador.También se encarga de llamar method para finalizar la partida.
     */
    public void run() {
        while (isOn) {
            //recupero troops user
            callback.getUserTroopsById(game.getMatch().getMatchName(), game.getMatch().getPlHost().getUsername(), game.getMatch().getPlGuest().getUsername(), server);
            //callback.getUserTroopsById(game.getMatch().getMatchName(), game.getMatch().getPlGuest().getUsername(), server);
            //per cada tropa de host
            if (userGame != null && userGame.getGame() != null && userGame.getGame().getMatch() != null && ((userGame.getGame().getMatch().getPlHost() != null && userGame.getGame().getMatch().getPlHost().getTroops().size() != 0) || (userGame.getGame().getMatch().getPlGuest() != null && userGame.getGame().getMatch().getPlGuest().getTroops().size() != 0))) {
                if (userGame.getGame().getMatch().getPlHost().getTroops() != null && userGame.getGame().getMatch().getPlHost().getTroops().size() != 0) {
                    for (Troop troop : userGame.getGame().getMatch().getPlHost().getTroops()) {
                        userGame.getGame().setRol("Host");
                        if (troop.getSpeed() != 0) {
                            userGame.setTroop(troop);
                            if (userGame.getTroop().getRowLocation() > 0 && userGame.getTroop().getVitalityPoints() > 0 && (userGame.getPlayerHost() == null || userGame.getPlayerHost().getVitalityPoints() > 0)) {
                                //faig un move de la troop
                                callback.onMoveTroop(userGame);
                                // System.exit(0);
                            }
                            if (troop.getVitalityPoints() == 0){
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setTroop(null);
                                if ((troop.getRowLocation() <= 1 && troop.getColLocation() >= 4 && troop.getColLocation() <= 5) || (troop.getRowLocation() >= 18 && troop.getRowLocation() <= 19 && troop.getColLocation() >= 4 && troop.getColLocation() <= 5)) {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Tower);
                                } else if (troop.getRowLocation() >= 5 && troop.getRowLocation() <= 14) {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Ground);
                                } else {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Range);
                                }
                            }
                            callback.onGetPlayersInfo(userGame.getGame().getMatch().getPlHost().getUsername(), userGame.getGame().getMatch().getPlGuest().getUsername(), userGame.getGame().getMatch().getMatchName());
                            if (userGame.getGame().getMatch().getPlHost().getVitalityPoints() == 0 || userGame.getGame().getMatch().getPlGuest().getVitalityPoints() == 0) {
                                isOn = false;
                                userGame.getGame().getMatch().setMatchStatus(MatchStatus.Finished);
                                onNotifyWin(userGame.getGame());
                            }
                        } else {
                            if (troop.getRowLocation() >= 0 && troop.getColLocation() >= 0) {
                                userGame.setTroop(troop);
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setCellType(CellType.Troop);
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setTroop(troop);
                            }
                        }
                    }
                }
                if (userGame.getGame().getMatch().getPlGuest().getTroops() != null && userGame.getGame().getMatch().getPlGuest().getTroops().size() != 0) {
                    for (Troop troop : userGame.getGame().getMatch().getPlGuest().getTroops()) {
                        userGame.getGame().setRol("Guest");
                        if (troop.getSpeed() != 0) {
                            userGame.setTroop(troop);
                            if (userGame.getTroop().getRowLocation() > 0 && userGame.getTroop().getVitalityPoints() > 0 && userGame.getGame().getMatch().getPlGuest().getVitalityPoints() > 0) {
                                //faig un move de la troop
                                if (userGame.getTroop().getRowLocation() <= 18) {
                                    callback.onMoveTroop(userGame);
                                }
                            }
                            if (troop.getVitalityPoints() == 0){
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setTroop(null);
                                if ((troop.getRowLocation() <= 1 && troop.getColLocation() >= 4 && troop.getColLocation() <= 5) || (troop.getRowLocation() >= 18 && troop.getRowLocation() <= 19 && troop.getColLocation() >= 4 && troop.getColLocation() <= 5)) {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Tower);
                                } else if (troop.getRowLocation() >= 5 && troop.getRowLocation() <= 14) {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Ground);
                                } else {
                                    userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(CellType.Range);
                                }
                            }
                            callback.onGetPlayersInfo(userGame.getGame().getMatch().getPlHost().getUsername(), userGame.getGame().getMatch().getPlGuest().getUsername(), userGame.getGame().getMatch().getMatchName());
                            if (userGame.getGame().getMatch().getPlHost().getVitalityPoints() == 0 || userGame.getGame().getMatch().getPlGuest().getVitalityPoints() == 0) {
                                isOn = false;
                                userGame.getGame().getMatch().setMatchStatus(MatchStatus.Finished);
                                onNotifyWin(game);
                            }
                        } else {
                            if (troop.getRowLocation() >=0 && troop.getColLocation()>=0) {
                                userGame.setTroop(troop);
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setCellType(CellType.Troop);
                                userGame.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()].setTroop(troop);
                            }
                        }
                    }
                }
                userGame.getGame().setAction(DedicatedServer.REFRESH_GAME);
                userGame.setAction(DedicatedServer.REFRESH_GAME);
                userGame.getGame().getMatch().setMatchTime(userGame.getGame().getMatch().getMatchTime()+1);
                userGame.getGame().getMatch().getPlHost().setAvailableMoney(userGame.getGame().getMatch().getPlHost().getAvailableMoney()+10);
                userGame.getGame().getMatch().getPlGuest().setAvailableMoney(userGame.getGame().getMatch().getPlGuest().getAvailableMoney()+10);
                callback.onIncrementPlayersMoney(userGame.getGame().getMatch().getPlHost(),userGame.getGame().getMatch().getPlGuest());
                onNotifyRoomUsers(userGame);
                if (isOn) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        server.stopGameRoom(game);
    }


    public UserGame getUserGame() {
        return userGame;
    }

    public void setUserGame(UserGame userGame) {
        this.userGame = userGame;
    }

    public void setCallback(NetworkCallback callback) {
        this.callback = callback;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Método que sirve para notificar al host,guest y viewers de que el juego ha finalizado. También para indicar el ganador de la partida.
     * @param game - Game con la información del juego actualizado / partida actualizada.
     */
    public void onNotifyWin(Game game) {
        isOn = false;
        if (userGame.getGame().getMatch().getPlHost().getVitalityPoints() == 0) {
            game.getMatch().setWinner(game.getMatch().getPlGuest());
            //userManager.updateStatistics(winner.getUsername(),looser.getUsername());
            callback.onUpdateStatistics(game.getMatch().getWinner().getUsername(), game.getMatch().getPlHost().getUsername(), guestClientServer, hostClientServer);
        } else {
            game.getMatch().setWinner(game.getMatch().getPlHost());
            callback.onUpdateStatistics(game.getMatch().getWinner().getUsername(), game.getMatch().getPlGuest().getUsername(), hostClientServer, guestClientServer);
        }
        callback.onWin(game.getMatch().getWinner().getUsername(),game.getMatch().getMatchName(),game.getMatch().getMatchTime());
        game.setAction(DedicatedServer.GAME_FINISHED);
        game.setMessage(new Message(0,game.getMatch().getWinner().getUsername()+" has won the game"));
        try {
            hostClientServer.getObjectOut().reset();
            hostClientServer.getObjectOut().writeObject(game);
            hostClientServer.getObjectOut().reset();
            hostClientServer.getObjectOut().writeObject(game.getMatch().getPlHost());
            hostClientServer.getObjectOut().reset();
            hostClientServer.getObjectOut().flush();
            guestClientServer.getObjectOut().reset();
            guestClientServer.getObjectOut().writeObject(game);
            guestClientServer.getObjectOut().reset();
            guestClientServer.getObjectOut().flush();
            for (DedicatedServer dServer : spectatorsClientServer) {
                dServer.getObjectOut().reset();
                dServer.getObjectOut().writeObject(game);
                dServer.getObjectOut().reset();
                dServer.getObjectOut().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "hostClientServer=" + hostClientServer +
                ", guestClientServer=" + guestClientServer +
                ", spectatorsClientServer=" + spectatorsClientServer +
                ", game=" + game +
                '}';
    }
}
