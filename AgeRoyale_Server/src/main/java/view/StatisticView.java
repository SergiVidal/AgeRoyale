package view;

import controller.StatisticController;
import model.entity.MatchStat;
import model.entity.UserStat;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/**
 * Representa la vista que contendrá tanto Top 10 players como MatchGraphic.
 */
public class StatisticView extends JFrame{
    /**
     * Representa el título del Frame.
     */
    private static final String FRAME_TITLE = "Age Royale";
    /**
     * Representa el máximo de columnas de la tabla para Top 10 players.
     */
    private static final int MAX_COLUMNS = 3;
    /**
     * Representa el nombre para el Tab de Matches Evolution.
     */
    private static final String MATCHES_EVOLUTION = "Matches Evolution";
    /**
     * Representa el atributo de la clase del gráfico.
     */
    private MatchGraphic matchGraphic;
    /**
     * Representa el panel para matches evolution.
     */
    private JPanel jMatchPanel;
    /**
     * Representa el panel donde habrán los 3 botones (Day,Month,Year) en horizontal.
     */
    private JPanel jButtonsPanel;
    /**
     * Representa el botón para escoger filtrar por año.
     */
    private JButton jYearButton;
    /**
     * Representa el botón para escoger ver resultados de partidas por mes.
     */
    private JButton jMonthButton;
    /**
     * Representa el botón para escoger ver resultados de partidas por día.
     */
    private JButton jDayButton;
    /**
     * Representa el texto del botón (JButton) por día.
     */
    public static final String DAY = "Day";
    /**
     * Representa el texto del botón (JButton) por mes.
     */
    public static final String MONTH = "Month";
    /**
     * Representa el texto del botón (JButton) por año.
     */
    public static final String YEAR = "Year";
    /**
     * Representa el título de la pestaña por top 10 players.
     */
    private static final String TOP_10 = "Top 10 players";
    /**
     * Representa el panel de pestañas.
     */
    private JTabbedPane jTabbedPane;
    /**
     * Representa el título de la columna de nombre de usuario.
     */
    private static final String USERNAME = "Username";
    /**
     * Representa el título de la columna de partidas ganadas.
     */
    private static final String WIN_RATE = "Wins (Win Rate)";
    /**
     * Representa el título de la columna de tiempo medio de partida.
     */
    private static final String AVG_TIME = "Average Time";
    /**
     * Representa el panel para Top 10 players.
     */
    private JPanel jpTop10;
    /**
     * Representa la tabla de Top 10 players.
     */
    private JTable jTTop10;
    /**
     * Representa el objeto que hereda de DefaultTableModel y que sirve para poder poner datos a la tabla y especificar el tipo de datos de cada columna.
     */
    private JTableTop10 jTableTop10;
    /**
     * Representa los datos de cada fila y por cada fila de cada columna.
     */
    private Object[][] dataPlayers;
    /**
     * Representa los nombres de cada columna.
     */
    private Object[] columnsPlayers;
    /**
     * Representa el número de jugadores a visualizar en la estadística (Top 10 players).
     */
    private int numPlayers;

    /**
     * Crea el StatisticView.
     * @throws HeadlessException - Error de Headless
     */
    public StatisticView() throws HeadlessException {
        configureView();
        initView();
    }

    /**
     * Método para visualizar la vista.
     */
    public void showView() {
        setVisible(true);
    }

    /**
     * Método que sirve para configurar la vista.
     */
    private void configureView() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(Utility.FAVICON)));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(14 * Utility.STANDARD_SIZE, 10 * Utility.STANDARD_SIZE);
        setLocationRelativeTo(null);
        setTitle(FRAME_TITLE);
//        setResizable(false);
    }

    /**
     * Método que sirve para crear el panel de pestañas y añadir las pestañas de Matches Evolution y Top 10 players.
     */
    private void createTabbedPane() {
        /* Tabbed Pane */
        jTabbedPane = new JTabbedPane();
        jTabbedPane.add(MATCHES_EVOLUTION, jMatchPanel);
        jTabbedPane.add(TOP_10, jpTop10);

        getContentPane().add(jTabbedPane);
    }

    /**
     * Método que sirve para inicializar la vista y crear las varias partes de la vista.
     */
    public void initView() {
        createMatchesEvolution();
        createTop10Tab();
        createTabbedPane();
    }

    /**
     * Método que sirve para crear la pestaña del panel para Top 10 players.
     */
    private void createTop10Tab() {
        jpTop10 = new JPanel();
        jpTop10.setLayout(new BoxLayout(jpTop10, BoxLayout.PAGE_AXIS));

        JLabel jlTop10 = new JLabel(TOP_10);

        columnsPlayers = new Object[]{USERNAME, WIN_RATE, AVG_TIME};

        jTTop10 = new JTable();
        jTableTop10 = new JTableTop10();
        jTTop10.setModel(jTableTop10);
        jTTop10.setCellSelectionEnabled(false);
        jTTop10.setRowHeight(50);

        JScrollPane jspFriendList = new JScrollPane(jTTop10);

        jpTop10.add(jlTop10);
        jpTop10.add(jspFriendList);
    }

    /**
     * Método que sirve para inicializar el panel de Top 10 players
     * @param numPlayers - Representa el número de jugadores.
     */
    public void initTableTop10(int numPlayers) {
        dataPlayers = new Object[numPlayers][MAX_COLUMNS];
    }

    /**
     * Método que sirve para añadir los datos de los usuarios en la tabla.
     * @param userStat - Representa las estadísticas de cada usuario.
     */
    public void setDataUserList(UserStat userStat) {
        int pos = 0;
        dataPlayers[numPlayers][pos] = userStat.getUsername();
        dataPlayers[numPlayers][++pos] = userStat.getWins() + "(" + userStat.getWinRate() + "%)";
        dataPlayers[numPlayers][++pos] = userStat.getAvgTime() + "sec";
        numPlayers++;
    }

    /**
     * Método para refrescar la tabla de Top 10 players.
     */
    public void refreshTableTop10() {
        jTableTop10 = new JTableTop10(dataPlayers, columnsPlayers);
        jTTop10.setModel(jTableTop10);
        jTTop10.revalidate();
        numPlayers = 0;
    }

    /**
     * Método que sirve para crear las partes visuales de la vista de Matches Evolution.
     */
    public void createMatchesEvolution(){
        jMatchPanel = new JPanel(new BorderLayout());

        createButtonsPanel();
        matchGraphic = new MatchGraphic();

        jMatchPanel.add(jButtonsPanel, BorderLayout.NORTH);
        jMatchPanel.add(matchGraphic, BorderLayout.CENTER);
    }

    /**
     * Método que sirve para crear el panel de botones del tab Matches Evolution.
     */
    public void createButtonsPanel() {
        jButtonsPanel = new JPanel();
        jDayButton = new JButton(DAY);
        jMonthButton = new JButton(MONTH);
        jYearButton = new JButton(YEAR);
        jButtonsPanel.add(jDayButton);
        jButtonsPanel.add(jMonthButton);
        jButtonsPanel.add(jYearButton);
    }

    /**
     * Método que sirve para registrar el controlador de las estadísticas.
     * @param controller - Representa StatisticcONTROLLER
     */
    public void registerController(StatisticController controller) {
        jTabbedPane.addChangeListener(controller);
        jDayButton.addActionListener(controller);
        jDayButton.setActionCommand(DAY);
        jMonthButton.addActionListener(controller);
        jMonthButton.setActionCommand(MONTH);
        jYearButton.addActionListener(controller);
        jYearButton.setActionCommand(YEAR);
    }

    /**
     * Método que actualizar la vista Matches Evolution.
     * @param matchStat - Representa las estadisticas de la evolucion de partidas
     * @param filter - Permite indicar si mostrar los datos por dia, mes o año
     */
    public void updateBarGraphic(MatchStat matchStat,String filter) {
        matchGraphic.updateGraphic(matchStat,filter);
    }

    public JTabbedPane getTabbedPane() {
        return jTabbedPane;
    }

    public MatchGraphic getMatchGraphic() {
        return matchGraphic;
    }
}
