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

/** Representa el LoginController */
public class LoginController extends MouseAdapter implements ActionListener, NetworkCallback {
    /** Representa la Vista */
    private LoginView view;
    /** Representa el thread de la comunicación con el servidor */
    private ServerCommunication sCommunication;

    /**
     * Crea el LoginController con los siguientes parametros de configuración
     * @param view - Vista a la cual esta vinculado
     * @param sCommunication - Objeto de la clase ServerCommunication que permite la comunicación vía sockets
     */
    public LoginController(LoginView view, ServerCommunication sCommunication) {
        this.view = view;
        this.sCommunication = sCommunication;
        sCommunication.registerCallback(this);
    }

    @Override
    public void onRegisterUser(UserLogin userLogin) {

    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario inicia sesión
     * @param userLogin - Objeto UserLogin que contiene la información del usuario que acaba de iniciar sesión
     */
    @Override
    public void onLoginUser(UserLogin userLogin) {
        if (userLogin.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userLogin.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to next View...");
            goToHomeView(userLogin.getUser());
        } else {
            JOptionPane.showMessageDialog(null, userLogin.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
     * Función listener encargada de gestionar los clicks realizados por el usuario, cuando realiza un inició de sesión
     * @param e - ActionEvent correspondiente al click del usuario
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (LoginView.BTN_LOGIN.equals(e.getActionCommand())) {
            String login = view.getUsername();
            String password = view.getPassword();

            checkPassword(login, password);
        }
    }

    /**
     * Función encargada de comprobar que los datos introducidos en el formulario sean correctos antes de mandarlos al servidor
     * @param login - Nombre de usuario / Email utilizado para el inició de sesión
     * @param password - Contraseña utilizada para el inició de sesión
     */
    public void checkPassword(String login, String password) {
        if (login.length() == 0 || password.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debes de rellenar todos los campos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener un mínimo de 8 caracters, 1 mayuscula, 1 minuscula, 1 numero y no puede contener espacios",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            UserLogin userLogin = new UserLogin(new User(login, password), null);
            sCommunication.sendUserLogin(userLogin);
        }
    }

    /**
     * Función listener encargada de gestionar el click realizado por el usuario, cuando necesita registrarse en la aplicación (cambia de Vista: Login a Register)
     * @param e - MouseClicked correspondiente al click del usuario
     */
    @Override
    public void mouseClicked(MouseEvent e){
        goToRegisterView();
    }

    /**
     * Función encargada de inicializar la RegisterView junto a su Controlador
     */
    public void goToRegisterView() {
        RegisterView registerView = new RegisterView();
        RegisterController registerController = new RegisterController(registerView, sCommunication);

        registerView.showView();
        registerView.registerController(registerController);

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

