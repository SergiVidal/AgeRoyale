package model.manager;

import model.database.dao.MatchDAO;
import model.database.dao.PlayerDAO;
import model.database.dao.TroopDAO;
import model.database.dao.UserDAO;
import model.entity.*;
import model.enumeration.CellType;
import model.network.DedicatedServer;

import java.util.ArrayList;

/**
 * Representa el TroopManager que nos pemite acceder a métodos del TroopDAO.
 */
public class TroopManager {
    /**
     * Representa el TroopDAO.
     */
    private TroopDAO troopDAO;
    /**
     * Representa el PlayerDAO.
     */
    private PlayerDAO playerDAO;
    /**
     * Representa el UserDAO.
     */
    private UserDAO userDAO;
    /**
     * Representa el MatchDAO.
     */
    private MatchDAO matchDAO;

    /**
     * Crea el TroopManager.
     */
    public TroopManager() {
        this.troopDAO = new TroopDAO();
        this.matchDAO = new MatchDAO();
        this.playerDAO = new PlayerDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Método que sirve para crear una tropa para un jugador y una partida en concreto. (Solo se añadirá si el jugador tiene suficiente dinero como para comprarla).
     * @param player Se pasa como parámetro de entrada el jugador par que se pueda identificar y añadirle la tropa que se va a crear a él.
     * @param troop  Se pasa la tropa que se va a crear con la información de dicha tropa.
     * @param game   Se pasa el game que contiene la información del juego y de la partida.
     * @return Se devuelve un objeto / instancia de UserGame que contiene entre otros, el jugador, el juego y la partida.
     */
    public UserGame createTroop(Player player, Troop troop, Game game) {
        UserGame userGame = new UserGame();
        if (player != null && troop != null && game != null) {
            player = playerDAO.showPlayerInfoByNameAndMatchName(player.getUsername(), game.getMatch().getMatchName());
            if ((player.getAvailableMoney() - troop.getCost()) >= 0) {
                if (playerDAO.reduceMoney(player, troop.getCost())) {
                    player = playerDAO.showPlayerInfoByNameAndMatchName(player.getUsername(), game.getMatch().getMatchName());
                    userGame.setTroop(troop);
                }
                troopDAO.insertTroopToMatch(userGame, game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId());
            } else {
                userGame.setMessage(new Message(1, "You don't have enough money to buy the troop!"));
            }
            if (game.getRol().equals("Host")) {
                userGame.setPlayerHost(player);
                game.getMatch().setPlHost(player);
                game.getMatch().getPlHost().setTroops(troopDAO.readTroopsByUserId(game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
            } else {
                userGame.setPlayerGuest(player);
                game.getMatch().setPlGuest(player);
                game.getMatch().getPlGuest().setTroops(troopDAO.readTroopsByUserId(game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
            }
        } else {
            userGame.setMessage(new Message(1, "DB Error!"));
        }

        userGame.setGame(game);
        userGame.setAction(DedicatedServer.CREATE_TROOP);
        return userGame;
    }

    /**
     * Método que sirve para ubicar y posicionar una tropa dentro del tablero / Board (modelo) y para registrar el cambio en la DB.
     * @param player Se recibe como parámetro el jugador / player que quiere ubicar la tropa.
     * @param troop  Se recibe como parámetro la tropa a ubicar, que está ya tendrá el valor apropiado en filas (rows) y columnas (cols).
     * @param game   Se recibe el juego / game con la información de la partida y del juego y que entre otros tiene en el objeto match, el board al cual poner la tropa.
     * @return Devuelve la tropa ubicada.
     */
    public UserGame locateTroop(Player player, Troop troop, Game game) {
        UserGame userGame = new UserGame();
        if (player != null && troop != null && game != null) {
            Troop auxTroop = troopDAO.getTroopInfoByName(troop.getName());
            auxTroop.setRowLocation(troop.getRowLocation());
            auxTroop.setColLocation(troop.getColLocation());
            if (troopDAO.locateTroop(auxTroop, game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId())) {
                game.getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(troop, CellType.Troop);

                if (game.getRol().equals("Host")) {
                    game.getMatch().getPlHost().setTroops(troopDAO.readTroopsByUserId(game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
                    userGame.setPlayerHost(game.getMatch().getPlHost());
                } else {
                    game.getMatch().getPlGuest().setTroops(troopDAO.readTroopsByUserId(game.getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
                    //    userGame.getGame().getMatch().setPlGuest(game.getMatch().getPlGuest());
                    userGame.setPlayerGuest(game.getMatch().getPlGuest());
                }
                userGame.setMessage(new Message(0, "Troop located successfully!"));
            } else {
                userGame.setMessage(new Message(1, "Troop can't be located!"));
            }
        } else {
            userGame.setMessage(new Message(1, "DB Error!"));
        }
        userGame.setTroop(troop);
        userGame.setGame(game);
        userGame.setAction(DedicatedServer.LOCATE_TROOP);
        return userGame;
    }

    /**
     * Método que sirve para poder mover una tropa des de una coordenada en tablero (x,y) a otra coordenada (x,y). También se le puede llamar posición.
     * @param gameUserTroop Se recibe como parámetro una instancia de UserGame para dicha partida, que contiene los jugadores Host y Guest y la partida.Contiene la tropa a mover. Además de contener la acción que le permite identificar cual acción hacer.
     * @return Devuelve el UserGame con la tropa ya movida en el tablero.
     */
    public synchronized UserGame moveTroop(UserGame gameUserTroop) {
        Player player = null;
        boolean isSameUser = false;
        int rows, cols;
        Integer[] positions = null;
        if ((gameUserTroop.getTroop()) != null) {
            Troop troop = gameUserTroop.getTroop();
            rows = troop.getRowLocation();
            cols = troop.getColLocation();

            if (gameUserTroop.getGame().getRol().equals("Host")) {
                player = gameUserTroop.getGame().getMatch().getPlHost();
            }
            if (gameUserTroop.getGame().getRol().equals("Guest")) {
                player = gameUserTroop.getGame().getMatch().getPlGuest();
            }

            positions = checkIfThereIsATroopAtPosition(troop, gameUserTroop.getGame().getMatch().getBoard(), troop.getRowLocation(), troop.getColLocation(), positions);
            if ((positions[0] != -1 && positions[1] != -1)) {
                if (!checkIfOtherTroopIsFromSameUser(gameUserTroop.getGame().getMatch().getBoard().getCells()[positions[0]][positions[1]].getTroop(), player)) {
                    if (positions[0] != -1 && positions[1] != -1) {
                        gameUserTroop = attackTroop(gameUserTroop, positions);
                    }
                } else
                    isSameUser = true;
            }
            if ((positions[0] == -1 || positions[1] == -1) || isSameUser) {
                //comprovo si la ubicacio de rows i col és compatible i si s'ha pogut moure la troop
                if (gameUserTroop.getGame().getRol().equals("Host")) {
                    int idMatch = matchDAO.getMatchByName(gameUserTroop.getGame().getMatch().getMatchName()).getId();
                    int userId = userDAO.findUserByName(player.getUsername()).getId();
                    if (troop.getRowLocation() >= 1 && troop.getSpeed() > 0) {
                        //significa per moure a la dreta
                        if (troop.getColLocation() < 3 && troop.getRowLocation() == 1) {
                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 1);
                            //significa moure a l'esquerra
                        } else if (troop.getColLocation() > 6 && troop.getRowLocation() == 1) {
                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 2);
                        } else if ((troop.getColLocation() == 3 || troop.getColLocation() == 6) && troop.getRowLocation() == 1) {
                            //attackTower(gameUserTroop);
                            Player player2;
                            if (gameUserTroop.getGame().getRol().equals("Host")) {
                                player2 = playerDAO.showPlayerInfoByName(gameUserTroop.getGame().getMatch().getPlGuest().getUsername());
                            } else {
                                player2 = playerDAO.showPlayerInfoByName(gameUserTroop.getGame().getMatch().getPlHost().getUsername());
                            }
                            troopDAO.attackTower(gameUserTroop.getTroop(), player2);
                        } else if (troop.getRowLocation() > 1) {
                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 3);
                        }
                    }
                }
                if (gameUserTroop.getGame().getRol().equals("Guest")) {

                    int idMatch = matchDAO.getMatchByName(gameUserTroop.getGame().getMatch().getMatchName()).getId();
                    int userId = userDAO.findUserByName(player.getUsername()).getId();
                    if (troop.getRowLocation() <= 18 && troop.getSpeed() > 0) {
                        if (troop.getColLocation() < 3 && troop.getRowLocation() == 18) {
                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 1);
                        } else if (troop.getColLocation() > 6 && troop.getRowLocation() == 18) {
                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 2);
                        } else if ((troop.getColLocation() == 3 || troop.getColLocation() == 6) && troop.getRowLocation() == 18) {
                            Player player2;
                            if (gameUserTroop.getGame().getRol().equals("Host")) {
                                player2 = playerDAO.showPlayerInfoByName(gameUserTroop.getGame().getMatch().getPlGuest().getUsername());
                            } else {
                                player2 = playerDAO.showPlayerInfoByName(gameUserTroop.getGame().getMatch().getPlHost().getUsername());
                            }
                            troopDAO.attackTower(gameUserTroop.getTroop(), player2);

                        } else if (troop.getRowLocation() < 18) {

                            troopDAO.moveTroop(gameUserTroop, idMatch, userId, 0);
                        }
                    }
                }
            }

            //resetejo la cela on estava abans la troop amb el tipus de Cell apropiat
            if ((rows <= 1 && cols >= 4 && cols <= 5) || (rows >= 18 && rows <= 19 && cols >= 4 && cols <= 5)) {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[rows][cols] = new Cell(CellType.Tower);
            } else if (rows >= 5 && rows <= 14) {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[rows][cols] = new Cell(CellType.Ground);
            } else {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[rows][cols] = new Cell(CellType.Range);
            }

            troop = troopDAO.getTroopInfoByMatchTroopId(gameUserTroop.getTroop().getMatchTroopId());//recupero la troop moguda
            gameUserTroop.getGame().getMatch().getBoard().getCells()[troop.getRowLocation()][troop.getColLocation()] = new Cell(troop, CellType.Troop);//coloco la troop en la seva nova posicio
            gameUserTroop.setTroop(troopDAO.getTroopInfoByMatchTroopId(gameUserTroop.getTroop().getMatchTroopId())); //actualitzo la troop dins de UserGame

            if (gameUserTroop.getGame().getRol().equals("Host")) {
                gameUserTroop.getGame().getMatch().getPlHost().setTroops(troopDAO.readTroopsByUserId(gameUserTroop.getGame().getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
            } else {
                gameUserTroop.getGame().getMatch().getPlGuest().setTroops(troopDAO.readTroopsByUserId(gameUserTroop.getGame().getMatch().getId(), userDAO.findUserByName(player.getUsername()).getId()));
            }
            gameUserTroop.setMessage(new Message(0, "Troop Moved!"));
        } else {
            gameUserTroop.setMessage(new Message(1, "Troop can't be Moved!"));
        }
        return gameUserTroop;
    }

    /**
     * Método que sirve para comprobar si otra troopa con la que se encuentra es del mismo jugador.
     * @param troop Se recibe la tropa de la cual se quiere saber si es del mismo jugador.
     * @param pl    Se recibe como parámetro el jugador para el cuál se comprobará si la tropa es suya o no.
     * @return Se devuelve un boolean (true or false) que nos indicará si la tropa es del jugador (true) o no lo es (false).
     */
    public boolean checkIfOtherTroopIsFromSameUser(Troop troop, Player pl) {
        for (Troop auxTroop : pl.getTroops()) {
            if (auxTroop.getMatchTroopId().intValue() == troop.getMatchTroopId().intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que sirve para poder atacar a otra tropa dado una tropa atacante y una tropa atacada.
     * @param gameUserTroop Se recibe como parámetro una instancia de UserGame, que sirve para poder recibir la partida, la tropa a atacar y la tropa atacante.
     * @param coords        Se recibe como parámetro un array de Integer / entero (objeto) que contendrá las posiciones (x,y) o coordenada donde está la tropa a atacar.
     * @return Devuelve una instancia de UserGame que va a contener la tropa ya atacada y posicionada en el tablero.
     */
    private UserGame attackTroop(UserGame gameUserTroop, Integer coords[]) {
        int matchId = gameUserTroop.getGame().getMatch().getId();
        Integer hostId = userDAO.findUserByName(gameUserTroop.getGame().getMatch().getPlHost().getUsername()).getId();
        Integer guestId = userDAO.findUserByName(gameUserTroop.getGame().getMatch().getPlGuest().getUsername()).getId();
        Game game = gameUserTroop.getGame();
        Player plHost = gameUserTroop.getGame().getMatch().getPlHost();
        Player plGuest = gameUserTroop.getGame().getMatch().getPlGuest();

        if (coords != null && coords[0] != -1 && coords[1] != -1) {
            while (gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]].getTroop().getVitalityPoints() > 0) {
                Troop troop = gameUserTroop.getTroop();
                gameUserTroop = troopDAO.attackTroop(troop, gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]].getTroop(), gameUserTroop.getGame().getMatch().getId());
                troop = troopDAO.getTroopInfoByMatchTroopId(troop.getMatchTroopId());

                gameUserTroop.setTroop(troop);
                gameUserTroop.setPlayerHost(plHost);
                gameUserTroop.setPlayerGuest(plGuest);
                gameUserTroop.setGame(game);
                gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]].setTroop(troop);
                gameUserTroop.getGame().getMatch().getPlHost().setTroops(troopDAO.readTroopsByUserId(matchId, hostId));
                gameUserTroop.getGame().getMatch().getPlGuest().setTroops(troopDAO.readTroopsByUserId(matchId, guestId));

                if(troop.getVitalityPoints() == 0){
                    if(checkIfOtherTroopIsFromSameUser(troop, plHost)){
//guest
                        playerDAO.incrementMoneyByKillingTroop(plGuest.getId(),troop);
                    }else{
//host
                    playerDAO.incrementMoneyByKillingTroop(plHost.getId(),troop);
                    }
                }
            }
            gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]].setTroop(null);
            if ((coords[0] <= 1 && coords[1] >= 4 && coords[1] <= 5) || (coords[0] >= 18 && coords[0] <= 19 && coords[1] >= 4 && coords[1] <= 5)) {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]] = new Cell(CellType.Tower);
            } else if (coords[0] >= 5 && coords[0] <= 14) {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]] = new Cell(CellType.Ground);
            } else {
                gameUserTroop.getGame().getMatch().getBoard().getCells()[coords[0]][coords[1]] = new Cell(CellType.Range);
            }
        }
        return gameUserTroop;
    }

    /**
     * Método que sirve para comprobar si una tropa está en una dirección y distancia (posicion x,y) o no.Se escogerá la dirección para la cual haya menos distancia. (cuerpo a cuerpo o rango).
     * @param troop     Se recibe como parámetro la tropa de la cual se va a mirar si está a distancia de otras, para poder atacar a otra tropa.
     * @param board     Se recibe como parámetro el tablero (modelo) con la información de las casillas (si hay una tropa se verá reflejado en dicho tablero / Board).
     * @param row       Se recibe como parámetro la fila donde está la tropa que quiere atacar.
     * @param col       Se recibe como parámetro la columna donde está la tropa que quiere atacar.
     * @param positions Se pasa un array de Integer / entero (positions) para contener la posicion donde está la tropa-
     * @return Devuelve un array de Integer / entero (objeto) con la posición donde está la tropa más cercana a atacar si es que hay. Sino lo devuelve así: {-1,-1}.
     */
    private Integer[] checkIfThereIsATroopAtPosition(Troop troop, Board board, int row, int col, Integer positions[]) {
        int rows = row, cols = col, i = 0, actual = 0, min = troop.getRange(), coords[] = new int[2], actualRow = 0, actualColumn = 0;
        boolean isFound = false;
        //diagonal esquerra
        while (i < troop.getRange() && rows > 0 && cols > 0 && !isFound) {
            rows--;
            cols--;
            if (board.getCells()[rows][cols].getTroop() != null) {
                isFound = true;
            }
            actual++;
            i++;
        }
        if (actual > 0 && actual <= min && isFound) {
            actualRow = rows;
            actualColumn = cols;
            positions = new Integer[]{rows, cols};
            min = i;
            coords = new int[]{rows, cols};

        }

        rows = row;
        cols = col;
        actual = 0;
        isFound = false;
        i = 0;
        //diagonal dreta
        while (i < troop.getRange() && rows > 0 && cols < board.getCells()[0].length - 1 && !isFound) {
            rows--;
            cols++;
            if (board.getCells()[rows][cols].getTroop() != null) {
                isFound = true;
            }
            actual++;
            i++;
        }
        if (actual > 0 && actual <= min && isFound) {
            actualRow = rows;
            actualColumn = cols;
            positions = new Integer[]{rows, cols};
            min = i;
            coords = new int[]{actualRow, actualColumn};
        }
        //diagonal abaix dreta
        while (i < troop.getRange() && rows > 0 && cols < board.getCells()[0].length - 1 && !isFound) {
            rows++;
            cols++;
            if (board.getCells()[rows][cols].getTroop() != null) {
                isFound = true;
            }
            actual++;
            i++;
        }
        if (actual > 0 && actual <= min && isFound) {
            actualRow = rows;
            actualColumn = cols;
            positions = new Integer[]{rows, cols};
            min = i;
            coords = new int[]{actualRow, actualColumn};
        }
        //diagonal abaix esquerra
        while (i < troop.getRange() && rows > 0 && cols < board.getCells()[0].length - 1 && !isFound) {
            rows++;
            cols--;
            if (board.getCells()[rows][cols].getTroop() != null) {
                isFound = true;
            }
            actual++;
            i++;
        }
        if (actual > 0 && actual <= min && isFound) {
            actualRow = rows;
            actualColumn = cols;
            positions = new Integer[]{rows, cols};
            min = i;
            coords = new int[]{actualRow, actualColumn};
        }
        //adalt
        if (board.getCells()[row - 1][col].getTroop() != null) {
            if (min >= 1) {
                coords[0] = row - 1;
                coords[1] = col;
                rows = coords[0];
                cols = coords[1];
                positions = new Integer[]{rows, cols};
            }
        }

        if (positions != null) {
            return new Integer[]{positions[0], positions[1]};
        }
        return new Integer[]{-1, -1};
    }

    /**
     * Método que sirve para poder obtener una tropa dado un id de partida y un id de jugador.
     * @param matchId Recibe como parámetro el id de la partida.
     * @param userId  Recibe como parámetro el id del jugador.
     * @return Devuelve una lista de tropas.
     */
    public ArrayList<Troop> getTroopsByUserId(int matchId, int userId) {
        return troopDAO.readTroopsByUserId(matchId, userId);
    }

    /**
     * Método que sirve para poder obtener una tropa dado un nombre de tropa.
     * @param name Recibe como parámetro la tropa a buscar a la base de datos y obtener.
     * @return Devuelve la tropa obtenida des de la DB.
     */
    public Troop getTroopInfoByName(String name) {
        return troopDAO.getTroopInfoByName(name);
    }

    /**
     * Método que sirve para poder obtener el conjunto de tropas disponibles para una partida.
     * @return Devuelve una lista de tropas disponibles para partida.
     */
    public ArrayList<Troop> getDBTroops() {
        return troopDAO.readTroops();
    }
}