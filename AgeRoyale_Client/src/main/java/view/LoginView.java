package view;

import controller.LoginController;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/** Representa la LoginView */
public class LoginView extends JFrame {

    /* Constantes */
    private static final String FRAME_TITLE = "Login";
    private static final String LOGIN = "Username/Email";
    private static final String PASSWORD = "Password";
    public static final String BTN_LOGIN = "Login";
    public static final String SIGN_UP = "Not registered yet? Sign up";
    /**/

    /** Representa el JTextField que contiene el nombre del usuario/email utilizado para iniciar sesión */
    private JTextField jtfLogin;
    /** Representa el JPasswordField que contiene la contraseña utilizada para iniciar sesión */
    private JPasswordField jtfPassword;
    /** Representa el JButton que permite iniciar sesión */
    private JButton bLogin;
    /** Representa el JLabel que permite cambiar de vista (Login a Register) */
    private JLabel jlRegister;

    /**
     * Crea la LoginView
     * @throws HeadlessException - Se lanza cuando el código es dependiente de los dispositivos de E/S y el entorno no lo soporta
     */
    public LoginView() throws HeadlessException {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(8 * Utility.STANDARD_SIZE, 7 * Utility.STANDARD_SIZE);
        setLocationRelativeTo(null);
        setTitle(FRAME_TITLE);
        setResizable(false);
    }

    /**
     * Función encargada de inicializar la vista y sus componentes principales
     */
    private void initView() {
        JPanel jpMain = new JPanel();
        jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.PAGE_AXIS));

        Image imageIcon = new ImageIcon(getClass().getResource(Utility.APP_LOGO)).getImage().getScaledInstance(360, 120, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(imageIcon);
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(icon);

        JLabel jlLogin = new JLabel(LOGIN);
        jlLogin.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));
        jtfLogin = new JTextField(50);

        JLabel jlPassword = new JLabel(PASSWORD);
        jlPassword.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));
        jtfPassword = new JPasswordField(50);

        bLogin = new JButton(BTN_LOGIN);
        bLogin.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.SECONDARY_FONT_SIZE));
        bLogin.setMaximumSize(new Dimension(400, 40));

        jlRegister = new JLabel(SIGN_UP);
        jlRegister.setFont(new Font(Utility.FONT_NAME, Font.ITALIC, Utility.SECONDARY_FONT_SIZE));
        jlRegister.setForeground(Color.BLUE);

        jpMain.add(iconLabel);

        jpMain.add(jlLogin);
        jpMain.add(jtfLogin);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(jlPassword);
        jpMain.add(jtfPassword);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(bLogin);
        jpMain.add(Box.createVerticalStrut(10));

        jpMain.add(jlRegister);

        getContentPane().add(jpMain);
    }

    /**
     * Función encargada de registrar los componentes con el controlador de la vista
     * @param controller - Controlador de la vista
     */
    public void registerController(LoginController controller) {
        bLogin.setActionCommand(BTN_LOGIN);
        bLogin.addActionListener(controller);

        jlRegister.addMouseListener(controller);
    }

    public String getUsername() {
        return jtfLogin.getText();
    }

    public String getPassword() {
        return jtfPassword.getText();
    }
}
