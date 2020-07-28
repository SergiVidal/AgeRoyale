package view;

import controller.GameController;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/** Representa la GameView */
public class GameView extends JFrame {
    /* Constantes */
    private static final String FRAME_TITLE = "Age Royale";
    public static final String WARRIOR = "Warrior";
    public static final String ARCHER = "Archer";
    public static final String CANNON = "Cannon";
    public static final String ARCHER_TOWER = "ArcherTower";
    /**/

    /** Representa el tablero (Vista) */
    private GridView gridView;
    /** Representa el JPanel principal */
    private JPanel jpMain;
    /** Representa el JPanel del Jugador Host */
    private PlayerInfoView jpHost;
    /** Representa el JPanel del Jugador Guest */
    private PlayerInfoView jpGuest;

    /**
     * Crea la GameView
     * @throws HeadlessException - Se lanza cuando el código es dependiente de los dispositivos de E/S y el entorno no lo soporta
     */
    public GameView() throws HeadlessException {
        configureView();
        initView();
    }

    /**
     * Función encargada de hacer visible la vista
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Función encargada de configurar los parametros básicos de la vista (Title, Size, Resizable...)
     */
    private void configureView() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(Utility.FAVICON)));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(10 * Utility.STANDARD_SIZE, 20 * Utility.STANDARD_SIZE);
        setLocationRelativeTo(null);
        setTitle(FRAME_TITLE);
        setResizable(false);
    }

    /**
     * Función encargada de inicializar la vista y su componente principal
     */
    private void initView() {
        jpMain = new JPanel(new BorderLayout());
    }

    /**
     * Función encargada de añadir el JPanel del Host/Guest al JPanel principal
     * @param infoView - JPanel (custom) del Host/Guest
     * @param layout - Valor del Layout al cual asignar a infoView (BorderLayout.NORTH/BorderLayout.SOUTH)
     * @param playerRol - Rol del jugador (Host/Guest), permite identificar que PlayerInfoView debe ir primero
     */
    public void addPlayerSection(PlayerInfoView infoView, String layout, String playerRol) {
        if (playerRol.equals("Host")) {
            jpHost = infoView;
            jpMain.add(jpHost, layout);
        } else {
            jpGuest = infoView;
            jpMain.add(jpGuest, layout);
        }
    }

    /**
     * Función encargada de inicializar el tablero
     * @param gridView - Representa el tablero (Vista)
     */
    public void initGrid(GridView gridView){
        setGridView(gridView);
        jpMain.add(gridView, BorderLayout.CENTER);
    }

    /**
     * Función encargada de añadir el JPanel principal al contenedor
     */
    public void addMainPanel() {
        getContentPane().add(jpMain);
    }

    /**
     * Función encargada de actualizar una casilla del tablero (Vista)
     * @param x - Indica la fila
     * @param y - Indica la columna
     * @param cellView - Casilla del tablero (Vista)
     */
    public void updateGrid(int x, int y, CellView cellView){
        gridView.getGrid()[x][y] = cellView;
    }

    /**
     * Función encargada de realizar el repaint y el revalidate del tablero
     */
    public void refreshGrid() {
        gridView.repaint();
        gridView.revalidate();
    }

    /**
     * Función encargada de bloquear los JButton de las tropas del enemigo y de esta forma el jugador no los puede accionar
     * @param playerInfoView - JPanel (custom) del Enemigo
     */
    private void disableEnemyButtons(PlayerInfoView playerInfoView){
        playerInfoView.getTroop1().setEnabled(false);
        playerInfoView.getTroop2().setEnabled(false);
        playerInfoView.getTroop3().setEnabled(false);
        playerInfoView.getTroop4().setEnabled(false);
    }

    /**
     * Función encargada de registrar los listener
     * @param playerInfoView - JPanel (custom) del Host/Guest
     * @param controller - Controlador de la GameView
     */
    private void setActionListener(PlayerInfoView playerInfoView, GameController controller){
        playerInfoView.getTroop1().setActionCommand(WARRIOR);
        playerInfoView.getTroop1().addActionListener(controller);

        playerInfoView.getTroop2().setActionCommand(ARCHER);
        playerInfoView.getTroop2().addActionListener(controller);

        playerInfoView.getTroop3().setActionCommand(CANNON);
        playerInfoView.getTroop3().addActionListener(controller);

        playerInfoView.getTroop4().setActionCommand(ARCHER_TOWER);
        playerInfoView.getTroop4().addActionListener(controller);
    }

    /**
     * Función encargada de registrar los componentes con el controlador de la vista y de llamar a la función que bloquea la UI del enemigo
     * @param controller - Controlador de la vista
     * @param option - Flag para indicar el rol del jugador y de esta forma poder bloquear la UI del enemigo, además de no registrar sus componentes
     */
    public void registerController(GameController controller, int option) {
        if(option == 1) {
            setActionListener(jpHost, controller);
            disableEnemyButtons(jpGuest);
        }else if(option == 2) {
            setActionListener(jpGuest, controller);
            disableEnemyButtons(jpHost);
        }else{
            disableEnemyButtons(jpHost);
            disableEnemyButtons(jpGuest);
        }

        if(option == 1 || option == 2) {
            for (int i = 0; i < gridView.getGrid().length; i++) {
                for (int j = 0; j < gridView.getGrid()[i].length; j++) {
                    gridView.getGrid()[i][j].addMouseListener(controller);
                }
            }
        }
    }

    public GridView getGridView() {
        return gridView;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }
}