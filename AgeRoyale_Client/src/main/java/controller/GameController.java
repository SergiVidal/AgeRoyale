package controller;

import model.entity.*;
import model.enumeration.CellType;
import model.enumeration.TroopClass;
import model.network.GameCallback;
import model.network.ServerCommunication;
import view.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/** Representa el GameController */
public class GameController implements GameCallback, ActionListener, MouseListener {
    /** Representa la Vista */
    private GameView view;
    /** Representa el tablero (Vista) */
    private GridView gridView;
    /** Representa el thread de la comunicación con el servidor */
    private ServerCommunication sCommunication;
    /** Representa el usuario */
    private User user;
    /** Representa el juego */
    private Game game;
    /** Representa el tablero (Model) */
    private Board board;
    /** Representa el JPanel del Jugador Host */
    private PlayerInfoView hostView;
    /** Representa el JPanel del Jugador Guest */
    private PlayerInfoView guestView;
    /** Variable auxiliar que representa una fila */
    private int rows;
    /** Variable auxiliar que representa una columna */
    private int columns;
    /** Variable auxiliar que representa el nombre de una tropa */
    private String actualTroop;
    /** Variable auxiliar que representa una Lista de tropas */
    private List<Troop> troopList;
    /** Variable auxiliar (flag) que simboliza si el usuario ha creado o no una tropa */
    private boolean isTroopCreated = false;

    /**
     * Crea el GameController con los siguientes parametros de configuración
     * @param view - Vista a la cual esta vinculado
     * @param sCommunication - Objeto de la clase ServerCommunication que permite la comunicación vía sockets
     * @param user - Usuario actual
     * @param matchName - Nombre de la partida actual
     */
    public GameController(GameView view, ServerCommunication sCommunication, User user, String matchName) {
        this.view = view;
        this.sCommunication = sCommunication;
        this.user = user;
        sCommunication.registerGameCallback(this);
        sCommunication.registerCallback(null);
        sCommunication.getMatchInfo(user.getUsername(), matchName);
        handleClosing();
    }

    /**
     * Función encargada de gestionar el cierre de la aplicación por parte del cliente
     */
    private void handleClosing() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(view,
                        "Are you sure you want to exit?", "Exit?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    if (!game.getRol().equals("Viewer")) {
                        sCommunication.sendUserLogout(new UserLogin(user, null));
                    }
                    sCommunication.stopServerCommunication(ServerCommunication.CLIENT_DISCONNECTION_CODE);
                    view.dispose();
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Función encargada de inicializar la HomeView junto a su Controlador
     */
    public void goToHomeView() {
        HomeView homeView = new HomeView(user);
        HomeController homeController = new HomeController(homeView, sCommunication, user);

        homeView.showView();
        homeView.registerController(homeController);
        sCommunication.registerGameCallback(null);
        view.setVisible(false);
        view.dispose();
    }

    /**
     * Función encargada de inicializar la vista del tablero
     */
    public void initGrid() {
        CellView cellView;
        this.rows = board.getCells().length;
        this.columns = board.getCells()[0].length;
        this.gridView = new GridView(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cellView = new CellView(i, j);
                cellView.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.DARK_GRAY, Color.DARK_GRAY));
                gridView.getGrid()[i][j] = cellView;
                gridView.add(gridView.getGrid()[i][j]);
            }
        }
        paintGrid(rows, columns);

        view.initGrid(gridView);
        view.refreshGrid();
    }

    /**
     * Función encargada de pintar una casilla del tablero especifica
     * @param x - Indica la fila
     * @param y - Indica la columna
     */
    private void paintGrid(int x, int y) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                paintGridPosition(i, j);
            }
        }
    }

    /**
     * Función encargada de añadir una imagen a una casilla del tablero especifica
     * @param x - Indica la fila
     * @param y - Indica la columna
     * @param imagePath - Indica el nombre (path) de de la imagen correspondiente
     */
    public void fillGrid(int x, int y, String imagePath) {
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            gridView.getGrid()[x][y].setImagePath(imagePath);
        }
    }

    /**
     * Función encargada de añadir una imagen de una tropa a un JButton especifico
     * @param jButton - JButton al cual modificar la imagen
     * @param imgPath - Indica el nombre (path) de de la imagen correspondiente
     */
    public void setImageTroop(JButton jButton, String imgPath) {
        Image imageIcon = new ImageIcon(getClass().getResource(imgPath)).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(imageIcon);
        jButton.setIcon(icon);
    }

    /**
     * Función encargada de inicializar los valores del JPanel del Host/Guest
     * @param infoView - JPanel (custom) al cual modificar su contenido
     * @param player - Objeto Player que contiene los valores del jugador
     * @param troopList - Lista de Objetos Troop que contiene los valores de las tropas.
     */
    public void initPlayerInfoView(PlayerInfoView infoView, Player player, List<Troop> troopList) {
        infoView.setPlayerName(player.getUsername());
        infoView.setVitalityPoints("VP: " + player.getVitalityPoints());
        infoView.setMoney("Money: " + player.getAvailableMoney());
        infoView.setTime("Time: 0sec");

        setImageTroop(infoView.getTroop1(), troopList.get(0).getIcon());
        setImageTroop(infoView.getTroop2(), troopList.get(1).getIcon());
        setImageTroop(infoView.getTroop3(), troopList.get(2).getIcon());
        setImageTroop(infoView.getTroop4(), troopList.get(3).getIcon());

        infoView.setTroop1(String.valueOf(TroopClass.Warrior));
        infoView.setTroop2(String.valueOf(TroopClass.Archer));
        infoView.setTroop3(String.valueOf(TroopClass.Cannon));
        infoView.setTroop4(String.valueOf(TroopClass.ArcherTower));

        infoView.setCostTroop1(String.valueOf(troopList.get(0).getCost()));
        infoView.setCostTroop2(String.valueOf(troopList.get(1).getCost()));
        infoView.setCostTroop3(String.valueOf(troopList.get(2).getCost()));
        infoView.setCostTroop4(String.valueOf(troopList.get(3).getCost()));
    }

    /**
     * Función callback encargada de obtener todos los datos que hacen referencia al juego actual
     * @param game - Objeto Game que contiene todos los valores del juego
     */
    @Override
    public void getGameInfo(Game game) {
        if (game.getMessage().getCode() == 0) {
            this.game = game;
            this.board = game.getMatch().getBoard();
            this.troopList = game.getMatch().getTroops();

            hostView = new PlayerInfoView();
            guestView = new PlayerInfoView();

            initPlayerInfoView(hostView, game.getMatch().getPlHost(), troopList);
            initPlayerInfoView(guestView, game.getMatch().getPlGuest(), troopList);

            SwingUtilities.invokeLater(() -> {
                if (game.getRol().equals("Host") || game.getRol().equals("Viewer")) {
                    view.addPlayerSection(guestView, BorderLayout.NORTH, "Guest");
                    initGrid();
                    view.addPlayerSection(hostView, BorderLayout.SOUTH, "Host");
                    if (game.getRol().equals("Host")) {
                        view.registerController(this, 1);
                    } else {
                        view.registerController(this, 3);

                    }
                } else if (game.getRol().equals("Guest")) {
                    view.addPlayerSection(hostView, BorderLayout.NORTH, "Host");
                    initGrid();
                    view.addPlayerSection(guestView, BorderLayout.SOUTH, "Guest");
                    view.registerController(this, 2);
                }

                view.addMainPanel();

                view.getContentPane().revalidate();
            });

        } else {
            JOptionPane.showMessageDialog(null, game.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de notificar que el juego actual ha terminado y de mandar al jugador a la Home View
     * @param game - Objeto Game que contiene todos los valores del juego
     */
    @Override
    public void onFinishGame(Game game) {
        if (game.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, game.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            goToHomeView();
        } else {
            JOptionPane.showMessageDialog(null, game.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de obtener las nuevas estadisticas del jugador una vez ha terminado la partida, con una nueva victoria o derrota
     * @param user - Objeto User que contiene los valores del usuario
     */
    @Override
    public void onUpdateStatistics(User user) {
        this.user = user;
    }

    /**
     * Función callback encargada de obtener los datos de la tropa creada y de actualizar la vista
     * @param userGame - Objeto UserGame que gestiona la información recibida por el servidor
     */
    @Override
    public void onCreateTroop(UserGame userGame) {
        if (userGame.getMessage().getCode() == 0) {
            System.out.println("Se ha creado correctamente la tropa, colocala en una casilla valida!");

            SwingUtilities.invokeLater(() -> {
                if (game.getRol().equals("Host")) {
                    blockCreateButtons(false, 1);
                } else {
                    blockCreateButtons(false, 2);
                }
            });

            actualTroop = userGame.getTroop().getName();
            isTroopCreated = true;
        } else {
            System.out.println("Error: " + userGame.getMessage().getMessageDescription());
        }
    }

    /**
     * Función callback encargada de obtener los datos de la tropa colocada
     * @param userGame - Objeto UserGame que gestiona la información recibida por el servidor
     */
    @Override
    public void onLocateTroop(UserGame userGame) {
        if (userGame.getMessage().getCode() == 0) {
            System.out.println("Se ha colocado correctamente la tropa!");
            board = userGame.getGame().getMatch().getBoard();
            int x = userGame.getTroop().getRowLocation();
            int y = userGame.getTroop().getColLocation();

            SwingUtilities.invokeLater(() -> {
                hostView.getMoney().setText("Money: " + userGame.getGame().getMatch().getPlHost().getAvailableMoney());
                guestView.getMoney().setText("Money: " + userGame.getGame().getMatch().getPlGuest().getAvailableMoney());

                fillGrid(x, y, userGame.getTroop().getIcon());
                view.updateGrid(x, y, gridView.getGrid()[x][y]);

                view.refreshGrid();
            });

            isTroopCreated = false;
        } else {
            System.out.println("Error: " + userGame.getMessage().getMessageDescription());
        }
    }

    /**
     * Función listener encargada de gestionar los clicks realizados en los JButtons de las tropas (Create Troop)
     * @param e - ActionEvent correspondiente al click del usuario
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Troop troop = new Troop();

        if (e.getActionCommand().equals(GameView.WARRIOR)) {
            troop.setName(GameView.WARRIOR);
        } else if (e.getActionCommand().equals(GameView.ARCHER)) {
            troop.setName(GameView.ARCHER);
        } else if (e.getActionCommand().equals(GameView.CANNON)) {
            troop.setName(GameView.CANNON);
        } else if (e.getActionCommand().equals(GameView.ARCHER_TOWER)) {
            troop.setName(GameView.ARCHER_TOWER);
        }

        UserGame userGame = new UserGame(troop, game);
        if (game.getRol().equals("Host")) {
            userGame.setPlayerHost(game.getMatch().getPlHost());
        } else if (game.getRol().equals("Guest")) {
            userGame.setPlayerGuest(game.getMatch().getPlGuest());
        }
        sCommunication.createTroop(userGame);
    }

    /**
     * Función encargada de bloquear o desbloquear los JButtons de creación de una tropa
     * @param action - Bloquear los JButtons (True), Desbloquear (False)
     * @param option - Flag para indicar el usuario al cual bloquear/desbloquear los JButtons (1 - Host / 2 - Guest)
     */
    public void blockCreateButtons(boolean action, int option) {
        if (option == 1) {
            hostView.getTroop1().setEnabled(action);
            hostView.getTroop2().setEnabled(action);
            hostView.getTroop3().setEnabled(action);
            hostView.getTroop4().setEnabled(action);
        } else if (option == 2) {
            guestView.getTroop1().setEnabled(action);
            guestView.getTroop2().setEnabled(action);
            guestView.getTroop3().setEnabled(action);
            guestView.getTroop4().setEnabled(action);
        }
    }

    /**
     * Función listener encargada de gestionar los clicks realizados en el tablero por el usuario (Locate Troop)
     * @param e - ActionEvent correspondiente al click del usuario
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isTroopCreated) {
            CellView cell = (CellView) e.getSource();
            int x = cell.getRow();
            int y = cell.getColumn();

            if (x >= 15) {
                if ((x == 18 || x == 19) && (y == 4 || y == 5)) {
                    System.out.println("No puedes colocar la tropa donde esta tu torre!");
                } else {
                    Troop troop = new Troop();
                    troop.setName(actualTroop);
                    if (game.getRol().equals("Guest")) {
                        troop.setRowLocation(19 - x);
                    } else {
                        troop.setRowLocation(x);
                    }
                    troop.setColLocation(y);

                    UserGame userGame = new UserGame(troop, game);
                    if (game.getRol().equals("Host")) {
                        userGame.setPlayerHost(game.getMatch().getPlHost());
                    } else if (game.getRol().equals("Guest")) {
                        userGame.setPlayerGuest(game.getMatch().getPlGuest());
                    }
                    sCommunication.locateTroop(userGame);
                }
            } else {
                System.out.println("No puedes colocar la tropa fuera de la zona permitida de tu lado del tablero");
            }
        } else {
            System.out.println("Antes de colocar la tropa debes de comprar la deseada!");
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    /**
     * Función callback encargada de notificar a la vista cuando el servidor ha caido y de cerrar el proceso del cliente
     */
    @Override
    public void stoppedServer() {
        JOptionPane.showOptionDialog(null, "El servidor no responde, se va a cerrar AgeRoyale!", "Aviso", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        view.dispose();
        System.exit(0);
    }

    /**
     * Función encargada de pintar la casilla del tablero correspondiente según su Tipo (Tower, Ground, Range, Troop)
     * @param i - Indica la fila
     * @param j - Indica la columna
     */
    public void paintGridPosition(int i, int j) {
        if (board.getCells()[i][j].getCellType().equals(CellType.Tower)) {
            if (game.getRol().equals("Guest")) {
                fillGrid(19 - i, j, "/tower.png");
            } else {
                fillGrid(i, j, "/tower.png");

            }
        } else if (board.getCells()[i][j].getCellType().equals(CellType.Ground)) {
            if (game.getRol().equals("Guest")) {

                fillGrid(19 - i, j, "/ground.png");
            } else {
                fillGrid(i, j, "/ground.png");

            }
        } else if (board.getCells()[i][j].getCellType().equals(CellType.Range)) {
            if (game.getRol().equals("Guest")) {
                fillGrid(19 - i, j, "/range.png");

            } else {
                fillGrid(i, j, "/range.png");

            }
        } else {
            if (game.getRol().equals("Guest")) {
                fillGrid(19 - i, j, board.getCells()[i][j].getTroop().getIcon());

            } else {
                fillGrid(i, j, board.getCells()[i][j].getTroop().getIcon());

            }
        }
    }

    /**
     * Función encargada de bloquear los JButtons (Create Troop) dependiendo del dinero actual del jugador
     * @param player - Objeto de la clase Player que contiene la información del jugador
     * @param playerInfoView - JPanel correspondiente al jugador (Host/Guest) al cual se va a realizar la modificación
     */
    public void blockTroopDependsMoney(Player player, PlayerInfoView playerInfoView) {
        if (!isTroopCreated) {
            if (player.getAvailableMoney() >= 400) {
                playerInfoView.getTroop1().setEnabled(true);
                playerInfoView.getTroop2().setEnabled(true);
                playerInfoView.getTroop3().setEnabled(true);
                playerInfoView.getTroop4().setEnabled(true);
            } else if (player.getAvailableMoney() < 400 && player.getAvailableMoney() >= 300) {
                playerInfoView.getTroop1().setEnabled(true);
                playerInfoView.getTroop2().setEnabled(true);
                playerInfoView.getTroop3().setEnabled(true);
                playerInfoView.getTroop4().setEnabled(false);
            } else if (player.getAvailableMoney() < 300 && player.getAvailableMoney() >= 200) {
                playerInfoView.getTroop1().setEnabled(true);
                playerInfoView.getTroop2().setEnabled(true);
                playerInfoView.getTroop3().setEnabled(false);
                playerInfoView.getTroop4().setEnabled(false);
            } else if (player.getAvailableMoney() < 200 && player.getAvailableMoney() >= 100) {
                playerInfoView.getTroop1().setEnabled(true);
                playerInfoView.getTroop2().setEnabled(false);
                playerInfoView.getTroop3().setEnabled(false);
                playerInfoView.getTroop4().setEnabled(false);
            } else if (player.getAvailableMoney() < 100) {
                playerInfoView.getTroop1().setEnabled(false);
                playerInfoView.getTroop2().setEnabled(false);
                playerInfoView.getTroop3().setEnabled(false);
                playerInfoView.getTroop4().setEnabled(false);
            }
        }
    }

    /**
     * Función callback encargada de obtener continuamente la información actual del juego
     * @param userGame - Objeto UserGame que gestiona la información recibida por el servidor
     */
    @Override
    public void onRefreshGame(UserGame userGame) {
        board = userGame.getGame().getMatch().getBoard();
        if (game != null) {
            if (game.getRol().equals("Guest")) {
                for (int i = board.getCells().length - 1; i >= 0; i--) {
                    for (int j = board.getCells()[i].length - 1; j >= 0; j--) {
                        paintGridPosition(i, j);
                        view.updateGrid(19 - i, j, gridView.getGrid()[19 - i][j]);
                    }
                }
                blockTroopDependsMoney(userGame.getGame().getMatch().getPlGuest(), guestView);
            } else {
                for (int i = 0; i < board.getCells().length; i++) {
                    for (int j = 0; j < board.getCells()[i].length; j++) {
                        paintGridPosition(i, j);
                        view.updateGrid(i, j, gridView.getGrid()[i][j]);
                    }
                }
                if (game.getRol().equals("Host"))
                    blockTroopDependsMoney(userGame.getGame().getMatch().getPlHost(), hostView);
            }
            view.refreshGrid();

            hostView.getVitalityPoints().setText("VP: " + userGame.getGame().getMatch().getPlHost().getVitalityPoints());
            guestView.getVitalityPoints().setText("VP: " + userGame.getGame().getMatch().getPlGuest().getVitalityPoints());

            hostView.getMoney().setText("Money: " + userGame.getGame().getMatch().getPlHost().getAvailableMoney());
            guestView.getMoney().setText("Money: " + userGame.getGame().getMatch().getPlGuest().getAvailableMoney());

            hostView.getTime().setText("Time: " + userGame.getGame().getMatch().getMatchTime() + "sec");
            guestView.getTime().setText("Time: " + userGame.getGame().getMatch().getMatchTime() + "sec");
        }
    }
}