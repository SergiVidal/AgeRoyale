package view;

import controller.RegisterController;
import org.w3c.dom.css.RGBColor;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/** Representa la RegisterView */
public class RegisterView extends JFrame {

    /* Constantes */
    private static final String FRAME_TITLE = "Register";
    private static final String USERNAME = "Username";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    private static final String CONFIRM_PASSWORD = "Confirm Password";
    public static final String BTN_REGISTER = "Register";
    public static final String SIGN_IN = "Already registered? Sign in";
    /**/

    /** Representa el JTextField que contiene el nombre del usuario para registrase */
    JTextField jtfUsername;
    /** Representa el JTextField que contiene el email para registrase */
    JTextField jtfEmail;
    /** Representa el JTextField que contiene la contraseña para registrase */
    JPasswordField jtfPassword;
    /** Representa el JTextField que contiene la confirmación de contraseña para registrase */
    JPasswordField jtfConfirmPassword;
    /** Representa el JButton que permite registrarse */
    JButton bRegister;
    /** Representa el JLabel que permite cambiar de vista (Register a View) */
    JLabel jlLogin;

    /**
     * Crea la RegisterView
     * @throws HeadlessException - Se lanza cuando el código es dependiente de los dispositivos de E/S y el entorno no lo soporta
     */
    public RegisterView() throws HeadlessException {
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
        setSize(8 * Utility.STANDARD_SIZE, 10 * Utility.STANDARD_SIZE);
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

        JLabel jlUsername = new JLabel(USERNAME);
        jlUsername.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

        jtfUsername = new JTextField(50);

        JLabel jlEmail = new JLabel(EMAIL);
        jlEmail.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

        jtfEmail = new JTextField(50);

        JLabel jlPassword = new JLabel(PASSWORD);
        jlPassword.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

        jtfPassword = new JPasswordField(50);

        JLabel jlConfirmPassword = new JLabel(CONFIRM_PASSWORD);
        jlConfirmPassword.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.PRIMARY_FONT_SIZE));

        jtfConfirmPassword = new JPasswordField(50);

        bRegister = new JButton(BTN_REGISTER);
        bRegister.setFont(new Font(Utility.FONT_NAME, Font.BOLD, Utility.SECONDARY_FONT_SIZE));
        bRegister.setMaximumSize(new Dimension(400, 40));

        jlLogin = new JLabel(SIGN_IN);
        jlLogin.setFont(new Font(Utility.FONT_NAME, Font.ITALIC, Utility.SECONDARY_FONT_SIZE));
        jlLogin.setForeground(Color.BLUE);

        jpMain.add(iconLabel);

        jpMain.add(jlUsername);
        jpMain.add(jtfUsername);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(jlEmail);
        jpMain.add(jtfEmail);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(jlPassword);
        jpMain.add(jtfPassword);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(jlConfirmPassword);
        jpMain.add(jtfConfirmPassword);
        jpMain.add(Box.createVerticalStrut(20));

        jpMain.add(bRegister);
        jpMain.add(Box.createVerticalStrut(10));

        jpMain.add(jlLogin);
        getContentPane().add(jpMain);

    }

    /**
     * Función encargada de registrar los componentes con el controlador de la vista
     * @param controller - Controlador de la vista
     */
    public void registerController(RegisterController controller) {
        bRegister.setActionCommand(BTN_REGISTER);
        bRegister.addActionListener(controller);

        jlLogin.addMouseListener(controller);
    }

    public String getUsername() {
        return jtfUsername.getText();
    }

    public String getEmail() {
        return jtfEmail.getText();
    }

    public String getPassword(){
        return jtfPassword.getText();
    }

    public String getConfirmPassword(){
        return jtfConfirmPassword.getText();
    }
}