package view;

import controller.HomeController;
import model.entity.*;
import model.enumeration.MatchStatus;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/** Representa la HomeView */
public class HomeView extends JFrame {
    /* Constantes */
    private static final String FRAME_TITLE = "Age Royale";
    private static final String HOME_TAB = "Home";
    private static final String FRIEND_TAB = "Friend";
    private static final String GAME_TAB = "Game";
    /**/

    /** Representa el JTabbedPane de la vista, el cual contiene las pestañas Home, Friend, Match */
    private JTabbedPane jTabbedPane;

    /** Representa el número total de columnas que contienen las JTables*/
    private static final int MAX_COLUMNS = 4;

    /** Representa el tamaño de las celdas de las JTables*/
    private static final int CELL_SIZE = 24;

    /* Constantes */
    private static final String USERNAME = "Username:";
    private static final String EMAIL = "Email:";
    private static final String WINS = "Wins:";
    private static final String LOSES = "Loses:";
    private static final String RATE = "Win Rate:";
    private static final String CHANGE_PASSWORD = "Change Password";

    private static final String SEND_INVITATION = "Send Invitation";
    private static final String NAME = "Name";
    public static final String INVITE = "Invite";

    public static final String FRIENDS = "Friends";
    private static final String DATE = "Date";
    private static final String IS_CONNECTED = "Connected";
    public static final String DELETE = "Delete";

    public static final String INVITATIONS = "Invitations";
    public static final String ACCEPT = "Accept";
    public static final String DECLINE = "Decline";

    private static final String CREATE_MATCH = "Create Match";
    public static final String CREATE = "Create";
    private static final String MATCHES = "Matches";

    private static final String MATCH_NAME = "Match Name";
    private static final String HOST_NAME = "Host Name";
    public static final String JOIN_MATCH = "Join match";
    public static final String VIEW_MATCH = "View match";
    /**/

    /* Constantes para identificar el nombre de las imagenes */
    private static final String ADD_ICON = "/add.png";
    private static final String DELETE_ICON = "/delete.png";
    private static final String ACCEPT_ICON = "/accept.png";
    private static final String DECLINE_ICON = "/decline.png";
    private static final String VIEW_ICON = "/view.png";
    /**/

    /** Representa el JPanel principal del tab Home */
    private JPanel jpMain;
    /** Representa el JPanel principal del tab Friend */
    private JPanel jpFriend;
    /** Representa el JPanel principal del tab Game */
    private JPanel jpGame;

    /** Representa el JLabel del tab Home que muestra el Win Rate del usuario */
    private JLabel jlRateData;
    /** Representa el JLabel del tab Home que muestra las victorias del usuario */
    private JLabel jlWinsData;
    /** Representa el JLabel del tab Home que muestra las derrotas del usuario */
    private JLabel jlLosesData;

    /** Representa el JTextField del tab Friend que contiene el nombre del usuario a realizar la invitación de amistad */
    private JTextField jtfFriendUsername;
    /** Representa el JButton del tab Friend que permite enviar la solicitud de amistad */
    private JButton bInviteInvitation;

    /** Representa la JTable del tab Friend que contiene la lista de amigos del usuario */
    private JTable jTFriends;
    /** Representa el DefaultTableModel de la JTable que contiene la lista de amigos  */
    private JTableFriendList jTableFriendList;
    /** Representa los datos de la JTable que contiene la lista de amigos  */
    private Object[][] dataFriends;
    /** Representa los valores de las columnas de la JTable que contiene la lista de amigos  */
    private Object[] columnsFriends;
    /** Variable auxiliar que representa el número de amigos del usuario */
    private int numFriends;

    /** Representa la JTable del tab Friend que contiene la lista de solicitudes de amistad del usuario */
    private JTable jTInvitations;
    /** Representa el DefaultTableModel de la JTable que contiene la lista de solicitudes de amistad del usuario  */
    private JTableInvitationList jTableInvitationList;
    /** Representa los datos de la JTable que contiene la lista de solicitudes de amistad del usuario  */
    private Object[][] dataInvitations;
    /** Representa los valores de las columnas de la JTable que contiene la lista de solicitudes de amistad del usuario  */
    private Object[] columnsInvitations;
    /** Variable auxiliar que representa el número de solicitudes de amistad del usuario*/
    private int numInvitations;

    /** Representa el JTextField del tab Match que contiene el nombre de la partida pública a crear */
    private JTextField jtfMatchName;
    /** Representa el JButton del tab Match que permite realizar la creación de la partida pública */
    private JButton bCreateMatch;

    /** Representa la JTable del tab Match que contiene la lista de partidas públicas */
    private JTable jTMatches;
    /** Representa el DefaultTableModel de la JTable que contiene la lista de partidas públicas */
    private JTableMatchList jTableMatchList;
    /** Representa los datos de la JTable que contiene la lista de partidas públicas  */
    private Object[][] dataMatches;
    /** Representa los valores de las columnas de la JTable que contiene la lista de partidas públicas   */
    private Object[] columnsMatches;
    /** Variable auxiliar que representa el número de partidas públicas*/
    private int numMatches;

    /**
     * Crea la HomeView
     * @param user - Usuario actual
     * @throws HeadlessException - Se lanza cuando el código es dependiente de los dispositivos de E/S y el entorno no lo soporta
     */
    public HomeView(User user) throws HeadlessException {
        configureView();
        initView(user);
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
        setSize(8 * Utility.STANDARD_SIZE, 10 * Utility.STANDARD_SIZE);
        setLocationRelativeTo(null);
        setTitle(FRAME_TITLE);
        setResizable(false);
    }

    /**
     * Función encargada de crear el Tab que corresponde a la vista Home
     * @param user - Usuario actual
     */
    private void createHomeTab(User user) {
        jpMain = new JPanel();
        jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.PAGE_AXIS));

        JPanel jpIcon = new JPanel();
        Image imageIcon = new ImageIcon(getClass().getResource(Utility.APP_LOGO)).getImage().getScaledInstance(360, 120, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(imageIcon);
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(icon);

        jpIcon.add(iconLabel);
        jpMain.add(jpIcon);

        if (user != null) {
            /* Username Panel */
            JPanel jpUsername = new JPanel();
            jpUsername.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel jlUsername = new JLabel(USERNAME);
            jlUsername.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

            JLabel jlUsernameData = new JLabel(user.getUsername());
            jlUsernameData.setFont(new Font(Utility.FONT_NAME, Font.PLAIN, Utility.PRIMARY_FONT_SIZE));

            jpUsername.add(jlUsername);
            jpUsername.add(jlUsernameData);

            /* Email Panel */
            JPanel jpEmail = new JPanel();
            jpEmail.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel jlEmail = new JLabel(EMAIL);
            jlEmail.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

            JLabel jlEmailData = new JLabel(user.getEmail());
            jlEmailData.setFont(new Font(Utility.FONT_NAME, Font.PLAIN, Utility.PRIMARY_FONT_SIZE));

            jpEmail.add(jlEmail);
            jpEmail.add(jlEmailData);

            /* Wins Panel */
            JPanel jpWins = new JPanel();
            jpWins.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel jlWins = new JLabel(WINS);
            jlWins.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

            jlWinsData = new JLabel(String.valueOf(user.getWins()));
            jlWinsData.setFont(new Font(Utility.FONT_NAME, Font.PLAIN, Utility.PRIMARY_FONT_SIZE));

            jpWins.add(jlWins);
            jpWins.add(jlWinsData);

            /* Loses Panel */
            JPanel jpLoses = new JPanel();
            jpLoses.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel jlLoses = new JLabel(LOSES);
            jlLoses.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

            jlLosesData = new JLabel(String.valueOf(user.getLoses()));
            jlLosesData.setFont(new Font(Utility.FONT_NAME, Font.PLAIN, Utility.PRIMARY_FONT_SIZE));

            jpLoses.add(jlLoses);
            jpLoses.add(jlLosesData);

            /* Rate Panel */
            JPanel jpRate = new JPanel();
            jpRate.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel jlRate = new JLabel(RATE);
            jlRate.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

            jlRateData = new JLabel();
            jlRateData.setFont(new Font(Utility.FONT_NAME, Font.PLAIN, Utility.PRIMARY_FONT_SIZE));

            jpRate.add(jlRate);
            jpRate.add(jlRateData);

            jpMain.add(jpUsername);
            jpMain.add(Box.createVerticalStrut(10));

            jpMain.add(jpEmail);
            jpMain.add(Box.createVerticalStrut(10));

            jpMain.add(jpWins);
            jpMain.add(Box.createVerticalStrut(10));

            jpMain.add(jpLoses);
            jpMain.add(Box.createVerticalStrut(10));

            jpMain.add(jpRate);
            jpMain.add(Box.createVerticalStrut(10));
        }
    }

    /**
     * Función encargada de crear el Tab que corresponde a la vista Friend
     */
    private void createFriendTab() {
        /* Friend Panel */
        jpFriend = new JPanel();
        jpFriend.setLayout(new BoxLayout(jpFriend, BoxLayout.PAGE_AXIS));

//-------------------------------------------------------------------------------------//

        JLabel jlSendTitle = new JLabel(SEND_INVITATION);

        JPanel jpFriendInvitation = new JPanel();
        jpFriendInvitation.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel jlName = new JLabel(NAME);
        jtfFriendUsername = new JTextField(20);
        jtfFriendUsername.setMaximumSize(new Dimension(20, 20));
        bInviteInvitation = new JButton(INVITE);

        jpFriendInvitation.add(jlName);
        jpFriendInvitation.add(jtfFriendUsername);
        jpFriendInvitation.add(bInviteInvitation);

//-------------------------------------------------------------------------------------//

        /* Friend List Panel */
        JLabel jlFriends = new JLabel(FRIENDS);

        columnsFriends = new Object[]{NAME, DATE, IS_CONNECTED, INVITE, DELETE};

        jTFriends = new JTable();
        jTableFriendList = new JTableFriendList();
        jTFriends.setModel(jTableFriendList);
        jTFriends.setDefaultRenderer(JButton.class, new JTableRenderer());
        jTFriends.setCellSelectionEnabled(false);
        jTFriends.setRowHeight(50);

        JScrollPane jspFriendList = new JScrollPane(jTFriends);

        //-------------------------------------------------------------------------------------//

        /* Invitation List Panel */
        JLabel jlInvitation = new JLabel(INVITATIONS);

        columnsInvitations = new Object[]{NAME, DATE, ACCEPT, DECLINE};

        jTInvitations = new JTable();
        jTableInvitationList = new JTableInvitationList();
        jTInvitations.setModel(jTableInvitationList);
        jTInvitations.setDefaultRenderer(JButton.class, new JTableRenderer());
        jTInvitations.setCellSelectionEnabled(false);
        jTInvitations.setRowHeight(50);

        JScrollPane jspInvitationList = new JScrollPane(jTInvitations);

        //-------------------------------------------------------------------------------------//

        /* Adding components to Friends Panel */
        jpFriend.add(jlSendTitle);
        jpFriend.add(jpFriendInvitation);

        jpFriend.add(jlFriends);
        jpFriend.add(jspFriendList);

        jpFriend.add(jlInvitation);
        jpFriend.add(jspInvitationList);

    }

    /**
     * Función encargada de crear el Tab que corresponde a la vista Game
     */
    private void createGameTab() {
        /* Game Panel */
        jpGame = new JPanel();
        jpGame.setLayout(new BoxLayout(jpGame, BoxLayout.PAGE_AXIS));

        /* Create Match Panel */
        JLabel jlCreateMatch = new JLabel(CREATE_MATCH);

        JPanel jpCreateMatch = new JPanel();
        jpCreateMatch.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel jlMatchName = new JLabel(NAME);
        jtfMatchName = new JTextField(20);
        jtfMatchName.setMaximumSize(new Dimension(20, 20));
        bCreateMatch = new JButton(CREATE);

        /* Horizontal Panel */
        jpCreateMatch.add(jlMatchName);
        jpCreateMatch.add(jtfMatchName);
        jpCreateMatch.add(bCreateMatch);

//-------------------------------------------------------------------------------------//

        JLabel jlMatches = new JLabel(MATCHES);

        columnsMatches = new Object[]{MATCH_NAME, HOST_NAME, DATE, JOIN_MATCH};

        jTMatches = new JTable();
        jTableMatchList = new JTableMatchList();
        jTMatches.setModel(jTableMatchList);
        jTMatches.setDefaultRenderer(JButton.class, new JTableRenderer());
        jTMatches.setCellSelectionEnabled(false);
        jTMatches.setRowHeight(50);

        JScrollPane jspFriendList = new JScrollPane(jTMatches);


        /* Adding components to Game Panel */
        jpGame.add(jlCreateMatch);
        jpGame.add(jpCreateMatch);

        jpGame.add(jlMatches);
        jpGame.add(jspFriendList);
    }

    /**
     * Función encargada de crear el JTabbedPane que contendrá los Tabs anteriores
     */
    private void createTabbedPane() {
        /* Tabbed Pane */
        jTabbedPane = new JTabbedPane();
        jTabbedPane.add(HOME_TAB, jpMain);
        jTabbedPane.add(FRIEND_TAB, jpFriend);
        jTabbedPane.add(GAME_TAB, jpGame);

        getContentPane().add(jTabbedPane);
    }

    /**
     * Función encargada de inicializar la vista y sus componentes principales
     * @param user - Representa el usuario actual junto a su informacion
     */
    public void initView(User user) {
        createHomeTab(user);
        createFriendTab();
        createGameTab();
        createTabbedPane();
    }

    /**
     * Función encargada de registrar los componentes con el controlador de la vista
     * @param controller - Controlador de la vista
     */
    public void registerController(HomeController controller) {
        jTabbedPane.addChangeListener(controller);

        jTFriends.setName(FRIENDS);
        jTFriends.addMouseListener(controller);

        jTInvitations.setName(INVITATIONS);
        jTInvitations.addMouseListener(controller);

        bInviteInvitation.setActionCommand(INVITE);
        bInviteInvitation.addActionListener(controller);

        bCreateMatch.setActionCommand(CREATE);
        bCreateMatch.addActionListener(controller);

        jTMatches.setName(MATCHES);
        jTMatches.addMouseListener(controller);
    }

    /**
     * Función encargada de inicializar la JTable que pertenece a la lista de amigos
     * @param numFriends - Número de amigos del usuario
     */
    public void initTableFriends(int numFriends) {
        dataFriends = new Object[numFriends][MAX_COLUMNS + 1];
    }

    /**
     * Función encargada de inicializar la JTable que pertenece a la lista de solicitudes de amistad
     * @param numInvitations - Número de solicitudes de amistad del usuario
     */
    public void initTableInvitations(int numInvitations) {
        dataInvitations = new Object[numInvitations][MAX_COLUMNS + 1];
    }

    /**
     * Función encargada de inicializar la JTable que pertenece a la lista de partidas públicas
     * @param numMatches - Número de partidas públicas
     */
    public void initTableMatches(int numMatches) {
        dataMatches = new Object[numMatches][MAX_COLUMNS];
    }

    /**
     * Función encargada de llenar con datos la JTable que pertenece a la lista de amigos
     * @param friend - Objeto de la clase Friend que contiene la información de un amigo
     */
    public void setDataFriendList(Friend friend) {
        int pos = 0;
        dataFriends[numFriends][pos] = friend.getUser().getUsername();
        dataFriends[numFriends][++pos] = friend.getFriendshipDate();
        if (friend.getUser().isConnected()) {
            dataFriends[numFriends][++pos] = "Yes";
        } else {
            dataFriends[numFriends][++pos] = "No";
        }
        createButton(dataFriends, numFriends, INVITE, ADD_ICON, friend.getUser().getUsername(), ++pos);
        createButton(dataFriends, numFriends, DELETE, DELETE_ICON, friend.getUser().getUsername(), ++pos);
        numFriends++;
    }

    /**
     * Función encargada de llenar con datos la JTable que pertenece a la lista de solicitudes de amistad
     * @param friendInvitation - Objeto de la clase FriendInvitation que contiene la información de una solicitud de amistad
     */
    public void setDataInvitationsList(FriendInvitation friendInvitation) {
        int pos = 0;
        dataInvitations[numInvitations][pos] = friendInvitation.getUser().getUsername();
        dataInvitations[numInvitations][++pos] = friendInvitation.getInvitationDate();
        createButton(dataInvitations, numInvitations, ACCEPT, ACCEPT_ICON, friendInvitation.getUser().getUsername(), ++pos);
        createButton(dataInvitations, numInvitations, DECLINE, DECLINE_ICON, friendInvitation.getUser().getUsername(), ++pos);
        numInvitations++;
    }

    /**
     * Función encargada de llenar con datos la JTable que pertenece a la lista de partidas públicas
     * @param match - Objeto de la clase Match que contiene la información de una partida
     */
    public void setDataMatchesList(Match match) {
        int pos = 0;
        dataMatches[numMatches][pos] = match.getMatchName();
        dataMatches[numMatches][++pos] = match.getPlHost().getUsername();
        dataMatches[numMatches][++pos] = match.getMatchDate();
        if (match.getMatchStatus().equals(MatchStatus.Pending)) {
            createButton(dataMatches, numMatches, JOIN_MATCH, ADD_ICON, match.getMatchName(), ++pos);
        } else {
            createButton(dataMatches, numMatches, VIEW_MATCH, VIEW_ICON, match.getMatchName(), ++pos);
        }
        numMatches++;
    }

    /**
     * Función encargada de resetear los datos de la JTable que pertenece a la lista de amigos del usuario
     */
    public void refreshTableFriend() {
        jTableFriendList = new JTableFriendList(dataFriends, columnsFriends);
        jTFriends.setModel(jTableFriendList);
        jTFriends.revalidate();
        numFriends = 0;
    }

    /**
     * Función encargada de resetear los datos de la JTable que pertenece a la lista de solicitudes de amistad
     */
    public void refreshTableInvitation() {
        jTableInvitationList = new JTableInvitationList(dataInvitations, columnsInvitations);
        jTInvitations.setModel(jTableInvitationList);
        jTInvitations.revalidate();
        numInvitations = 0;
    }

    /**
     * Función encargada de resetear los datos de la JTable que pertenece a la lista de partdias públicas
     */
    public void refreshTableMatches() {
        jTableMatchList = new JTableMatchList(dataMatches, columnsMatches);
        jTMatches.setModel(jTableMatchList);
        jTMatches.revalidate();
        numMatches = 0;
    }

    /**
     * Función encargada de crear un JButton dentro de las JTables anteriores
     * @param data - Representa los datos de la JTable
     * @param cont - Representa la posición en la fila de la JTable
     * @param action - Representa el valor del ActionCommand cuando el JButton sea clickado
     * @param img - Representa la Imagen (path) que contendrá el JButton
     * @param friendName - Representa el nombre del amigo, del usuario que realiza la solicitud de amistad o el nombre del usuario que crea la partida pública, permite diferenciar a que JButton de todas las filas de las JTables ha sido clickado.
     * @param pos - Representa la posición en la columna de la JTable
     */
    private void createButton(Object[][] data, int cont, String action, String img, String friendName, int pos) {
        data[cont][pos] = new JButton();
        JButton aux = (JButton) data[cont][pos];
        aux.setActionCommand(action + "-" + friendName);
        aux.setBackground(Color.white);
        aux.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(img)).getImage().getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH)));
    }

    /**
     * Función encargada de modificar el contenido de la JLabel que corresponde al porcentaje de victorias respecto a las derrotas (Home Tab)
     * @param data - Valor del JLabel
     */
    public void setJlRateData(String data) {
        jlRateData.setText(data);
    }

    /**
     * Función encargada de modificar el contenido de la JLabel que corresponde al número de victorias del usuario (Home Tab)
     * @param data - Valor del JLabel
     */
    public void setJlWinsData(int data) {
        jlWinsData.setText(String.valueOf(data));
    }

    /**
     * Función encargada de modificar el contenido de la JLabel que corresponde al número de derrotas del usuario (Home Tab)
     * @param data - Valor del JLabel
     */
    public void setJlLosesData(int data) {
        jlLosesData.setText(String.valueOf(data));
    }

    public JTabbedPane getTabbedPane() {
        return jTabbedPane;
    }

    public JTable getJTFriends() {
        return jTFriends;
    }

    public JTable getJTInvitations() {
        return jTInvitations;
    }

    public JTable getJTMatches() {
        return jTMatches;
    }

    public Object[][] getDataFriends() {
        return dataFriends;
    }

    public Object[][] getDataInvitations() {
        return dataInvitations;
    }

    public Object[][] getDataMatches() {
        return dataMatches;
    }

    public JTextField getJtfFriendUsername() {
        return jtfFriendUsername;
    }

    public JTextField getJtfMatchName() {
        return jtfMatchName;
    }
}