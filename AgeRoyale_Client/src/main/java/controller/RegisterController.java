package controller;

import model.entity.*;
import model.network.NetworkCallback;
import model.network.ServerCommunication;
import view.HomeView;
import view.LoginView;
import view.RegisterView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Representa el RegisterController */
public class RegisterController extends MouseAdapter implements ActionListener, NetworkCallback {
    /** Representa la Vista */
    private RegisterView view;
    /** Representa el thread de la comunicación con el servidor */
    private ServerCommunication sCommunication;

    /**
     * Crea el RegisterController con los siguientes parametros de configuración
     * @param view - Vista a la cual esta vinculado
     * @param sCommunication - Objeto de la clase ServerCommunication que permite la comunicación vía sockets
     */
    public RegisterController(RegisterView view, ServerCommunication sCommunication) {
        this.view = view;
        this.sCommunication = sCommunication;
        sCommunication.registerCallback(this);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario se registra en la aplicación
     * @param userLogin - Objeto UserLogin que contiene la información del usuario que acaba de registrarse
     */
    @Override
    public void onRegisterUser(UserLogin userLogin) {
        if (userLogin.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userLogin.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to next View...");
            goToHomeView(userLogin.getUser());
        } else {
            JOptionPane.showMessageDialog(null, userLogin.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onLoginUser(UserLogin userLogin) {

    }

    @Override
    public void onGetFriendList(UserFriendship userFriendship) {

    }

    @Override
    public void onDeleteFriend(UserFriendship userFriendship) {

    }

    @Override
    public void onAddFriend(UserFriendInvitation userFriendInvitation) {

    }

    @Override
    public void onGetInvitationList(UserFriendInvitation userFriendInvitation) {

    }

    @Override
    public void onAcceptFriend(UserFriendInvitation userFriendInvitation) {

    }

    @Override
    public void onDeclineFriend(UserFriendInvitation userFriendInvitation) {

    }

    @Override
    public void onGetMatchList(UserMatch userMatch) {

    }

    @Override
    public void onCreateMatch(UserMatch userMatch) {

    }

    @Override
    public void onInviteFriendToMatch(UserMatchInvitation userMatchInvitation) {

    }

    @Override
    public void onJoinMatch(UserMatch userMatch) {

    }

    @Override
    public void onViewMatch(UserMatch userMatch) {

    }

    @Override
    public void onGetUserInfo(User user) {

    }

    @Override
    public void onCancelMatch(UserMatchInvitation userMatchInvitation) {

    }

    @Override
    public void onValidateMatchInvitation(UserMatchInvitation userMatchInvitation) {

    }

    @Override
    public void onValidateMatch(UserMatch userMatch) {

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
     * Función listener encargada de gestionar los clicks realizados por el usuario, cuando quiere registrarse en la aplicación
     * @param e - ActionEvent correspondiente al click del usuario
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (RegisterView.BTN_REGISTER.equals(e.getActionCommand())) {
            String username = view.getUsername();
            String email = view.getEmail();
            String password = view.getPassword();
            String confirmPassword = view.getConfirmPassword();

            checkPassword(username, email, password, confirmPassword);
        }
    }

    /**
     * Función encargada de comprobar que los datos introducidos en el formulario sean correctos antes de mandarlos al servidor
     * @param username - Nombre de usuario utilizado para registrarse
     * @param email - Email utilizado para registrarse
     * @param password - Contraseña utilizada para registrarse
     * @param confirmPassword - Confirmación de contraseña utilizada para registrarse
     */
    public void checkPassword(String username, String email, String password, String confirmPassword) {
        if (username.length() == 0 || email.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes de rellenar todos los campos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(null, "Email incorrecto",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener un mínimo de 8 caracters, 1 mayuscula, 1 minuscula, 1 numero y no puede contener espacios",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Las passwords no coinciden",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            sCommunication.sendUserRegister(new UserLogin(new User(username, email, password), null));
        }
    }

    /**
     * Función listener encargada de gestionar el click realizado por el usuario, cuando necesita iniciar sesión en la aplicación (cambia de Vista: Register a View)
     * @param e - MouseClicked correspondiente al click del usuario
     */
    @Override
    public void mouseClicked(MouseEvent e){
        goToLoginView();
    }

    /**
     * Función encargada de inicializar la LoginView junto a su Controlador
     */
    public void goToLoginView() {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView, sCommunication);

        loginView.showView();
        loginView.registerController(loginController);

        view.setVisible(false);
        view.dispose();
    }

    /**
     * Función encargada de inicializar la HomeView junto a su Controlador
     * @param user - Representa el usuario actual
     */
    public void goToHomeView(User user) {
        HomeView homeView = new HomeView(user);
        HomeController homeController = new HomeController(homeView, sCommunication, user);

        homeView.showView();
        homeView.registerController(homeController);

        view.setVisible(false);
        view.dispose();
    }
}
