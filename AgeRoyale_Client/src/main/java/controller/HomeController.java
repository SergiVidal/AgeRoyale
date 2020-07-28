package controller;

import model.entity.*;
import model.network.NetworkCallback;
import model.network.ServerCommunication;
import view.GameView;
import view.HomeView;
import view.WaitingDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

/**
 * Representa el HomeController
 */
public class HomeController extends MouseAdapter implements ActionListener, NetworkCallback, ChangeListener {
    /**
     * Representa la Vista
     */
    private HomeView view;
    /**
     * Representa el thread de la comunicación con el servidor
     */
    private ServerCommunication sCommunication;
    /**
     * Representa el usuario
     */
    private User user;
    /**
     * Representa el JDialog cuando el usuario esta a la espera de que otro se una a la partida
     */
    private WaitingDialog waitingDialog;
    /**
     * Variable auxiliar que representa el nombre de un match
     */
    private String matchName;

    /**
     * Crea el HomeController con los siguientes parametros de configuración
     *
     * @param view           - Vista a la cual esta vinculado
     * @param sCommunication - Objeto de la clase ServerCommunication que permite la comunicación vía sockets
     * @param user           - Usuario actual
     */
    public HomeController(HomeView view, ServerCommunication sCommunication, User user) {
        this.view = view;
        this.sCommunication = sCommunication;
        this.user = user;
        sCommunication.registerCallback(this);
        setRateData();
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
                    sCommunication.stopServerCommunication(ServerCommunication.CLIENT_DISCONNECTION_CODE);
                    view.dispose();
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Función encargada de calcular el win rate del usuario actual
     */
    public void setRateData() {
        int total = user.getWins() + user.getLoses();
        int rate = 0;

        if (total != 0)
            rate = Math.round((float) user.getWins() * 100 / total);

        view.setJlRateData(rate + "%");
    }

    /**
     * Función encargada de notificar a la vista para que actualize la JTable que contiene la lista de amigos
     * @param userFriendship - Objeto UserFriendship que contiene la información de la lista de amigos
     */
    public void notifyViewFriends(UserFriendship userFriendship) {
        SwingUtilities.invokeLater(() -> {
            view.initTableFriends(userFriendship.getFriendList().size());
            for (Friend friend : userFriendship.getFriendList()) {
                view.setDataFriendList(friend);
            }
            view.refreshTableFriend();
        });
    }

    /**
     * Función encargada de notificar a la vista para que actualize la JTable que contiene la lista de invitaciones de amistad
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información de la lista de solicitudes de amistad
     */
    public void notifyViewInvitations(UserFriendInvitation userFriendInvitation) {
        SwingUtilities.invokeLater(() -> {
            view.initTableInvitations(userFriendInvitation.getFriendInvitations().size());
            for (FriendInvitation friendInvitation : userFriendInvitation.getFriendInvitations()) {
                view.setDataInvitationsList(friendInvitation);
            }
            view.refreshTableInvitation();
        });
    }

    /**
     * Función encargada de notificar a la vista para que actualize la JTable que contiene la lista de partidas
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    public void notifyViewMatches(UserMatch userMatch) {
        SwingUtilities.invokeLater(() -> {
            view.initTableMatches(userMatch.getMatches().size());
            for (Match match : userMatch.getMatches()) {
                view.setDataMatchesList(match);
            }
            view.refreshTableMatches();
        });
    }

    /**
     * Función encargada de inicializar y mostrar el JDialog cuando el usuario esta a la espera de que otro se una a la partida
     */
    private void showDialog() {
        view.setEnabled(false);
        waitingDialog = new WaitingDialog(view, "Information", false);
        waitingDialog.showDialog();
        waitingDialog.registerController(this);
        waitingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (matchName != null)
                    sCommunication.cancelMatch(user.getUsername());
                else
                    sCommunication.cancelMatchInvitation(user.getUsername());
            }
        });
    }

    /**
     * Función encargada de cerrar el JDialog de espera
     */
    private void closeDialog() {
        waitingDialog.dispose();
        waitingDialog.setVisible(false);
        view.setEnabled(true);
    }

    @Override
    public void onRegisterUser(UserLogin userLogin) {

    }

    @Override
    public void onLoginUser(UserLogin userLogin) {

    }

    /**
     * Función callback encargada de obtener todos los datos que hacen referencia a la lista de amigos
     *
     * @param userFriendship - Objeto UserFriendShip que contiene la información de los amigos del usuario
     */
    @Override
    public void onGetFriendList(UserFriendship userFriendship) {
        notifyViewFriends(userFriendship);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario es eliminado como amigo
     *
     * @param userFriendship - Objeto UserFriendShip que contiene la información actualizada de los amigos del usuario
     */
    @Override
    public void onDeleteFriend(UserFriendship userFriendship) {
        notifyViewFriends(userFriendship);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario añade a otro como amigo
     *
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información de las solicitudes de amistad del usuario
     */
    @Override
    public void onAddFriend(UserFriendInvitation userFriendInvitation) {
        if (userFriendInvitation.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userFriendInvitation.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        } else {
            JOptionPane.showMessageDialog(null, userFriendInvitation.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de obtener todos los datos que hacen referencia a la lista de solicitudes de amistad
     *
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información de las solicitudes de amistad del usuario
     */
    @Override
    public void onGetInvitationList(UserFriendInvitation userFriendInvitation) {
        notifyViewInvitations(userFriendInvitation);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario acepta una solicitud de amistad
     *
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información actualizada de las solicitudes de amistad del usuario
     */
    @Override
    public void onAcceptFriend(UserFriendInvitation userFriendInvitation) {
        notifyViewInvitations(userFriendInvitation);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario rechaza una solicitud de amistad
     *
     * @param userFriendInvitation - Objeto UserFriendInvitation que contiene la información actualizada de las solicitudes de amistad del usuario
     */
    @Override
    public void onDeclineFriend(UserFriendInvitation userFriendInvitation) {
        notifyViewInvitations(userFriendInvitation);
    }

    /**
     * Función callback encargada de obtener todos los datos que hacen referencia a la lista de partidas
     *
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    @Override
    public void onGetMatchList(UserMatch userMatch) {
        notifyViewMatches(userMatch);
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario crea una partida
     *
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    @Override
    public void onCreateMatch(UserMatch userMatch) {
        if (userMatch.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userMatch.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to WAITING View...");
            showDialog();
        } else {
            JOptionPane.showMessageDialog(null, userMatch.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de validar si la invitación a partida (privada) sigue activa
     *
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    @Override
    public void onValidateMatchInvitation(UserMatchInvitation userMatchInvitation) {
        if (userMatchInvitation.getMessage().getCode() == 0) {
            goToGameView(user, userMatchInvitation.getMatchName());
        } else {
            JOptionPane.showMessageDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * Función callback encargada de validar si la partida (pública) sigue activa
     *
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    @Override
    public void onValidateMatch(UserMatch userMatch) {
        if (userMatch.getMessage().getCode() == 0) {
            goToGameView(user, userMatch.getMatchName());
        } else {
            JOptionPane.showMessageDialog(null, userMatch.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
     * Función callback encargada de gestionar las invitaciones a partidas (privadas) entre amigos
     *
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    @Override
    public void onInviteFriendToMatch(UserMatchInvitation userMatchInvitation) {
        if (userMatchInvitation.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to WAITING View...");
            showDialog();
        } else if (userMatchInvitation.getMessage().getCode() == 1) {
            int result = JOptionPane.showConfirmDialog(null, "Do you want to join the game?", "Game Invitation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.out.println("Going to GAME View...");
                sCommunication.acceptMatchInvitation(userMatchInvitation);

            } else if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
                System.out.println("Stay here");
                sCommunication.declineMatchInvitation(userMatchInvitation);
            }
        } else if (userMatchInvitation.getMessage().getCode() == 2) {
            closeDialog();
            JOptionPane.showOptionDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Accepted! Going to GAME View...");
            goToGameView(user, userMatchInvitation.getMatchName());

        } else if (userMatchInvitation.getMessage().getCode() == 3) {
            closeDialog();
            JOptionPane.showOptionDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        } else {
            JOptionPane.showMessageDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario se une a una partida pública, como jugador
     *
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    @Override
    public void onJoinMatch(UserMatch userMatch) {
        if (userMatch.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userMatch.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to GAME View...");
            goToGameView(user, userMatch.getMatchName());
        } else {
            JOptionPane.showMessageDialog(null, userMatch.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario se une a una partida pública, como espectador
     *
     * @param userMatch - Objeto UserMatch que contiene la información de la lista de partidas públicas
     */
    @Override
    public void onViewMatch(UserMatch userMatch) {
        if (userMatch.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userMatch.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            System.out.println("Going to GAME View...");
            goToGameView(user, userMatch.getMatchName());
        } else {
            JOptionPane.showMessageDialog(null, userMatch.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función callback encargada de actualizar los datos del jugador y notificar a la vista
     *
     * @param user - Objeto User que contiene la información del usuario actual
     */
    @Override
    public void onGetUserInfo(User user) {
        this.user = user;
        view.setJlWinsData(user.getWins());
        view.setJlLosesData(user.getLoses());
        setRateData();
    }

    /**
     * Función callback encargada de gestionar la información recibida por el servidor cuando un usuario cancela la invitación a un amigo para jugar una partida privada
     *
     * @param userMatchInvitation - Objeto UserMatchInvitation que contiene la información de las invitaciones de los amigos a partidas privadas
     */
    @Override
    public void onCancelMatch(UserMatchInvitation userMatchInvitation) {
        closeDialog();
        if (userMatchInvitation.getMessage().getCode() == 0) {
            JOptionPane.showOptionDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        } else {
            JOptionPane.showMessageDialog(null, userMatchInvitation.getMessage().getMessageDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Función listener encargada de gestionar los clicks realizados por el usuario, cuando hace una petición de amistad, crea/cancela una partida privada y cancela la invitación a un amigo para jugar una partida privada
     *
     * @param e - ActionEvent correspondiente al click del usuario
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(HomeView.INVITE)) {
            String friendUsername = view.getJtfFriendUsername().getText();
            if (!friendUsername.equals("")) {
                sCommunication.addFriend(user.getUsername(), friendUsername);
            } else {
                JOptionPane.showMessageDialog(null, "Empty friend name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals(HomeView.CREATE)) {
            matchName = view.getJtfMatchName().getText();
            if (!matchName.equals("")) {
                sCommunication.createMatch(user.getUsername(), matchName);
            } else {
                JOptionPane.showMessageDialog(null, "Empty match name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals(WaitingDialog.CANCEL_MATCH)) {
            if (matchName != null)
                sCommunication.cancelMatch(user.getUsername());
            else
                sCommunication.cancelMatchInvitation(user.getUsername());
        }
    }

    /**
     * Función listener encargada de gestionar los cambios de pestañas (JTabbedPane) de la vista y solicitar la información correspondiente
     *
     * @param e - StateChanged correspondiente al cambio de pestaña del usuario
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        switch (view.getTabbedPane().getSelectedIndex()) {
            case 0:
                sCommunication.getUserInfo(user);
                break;
            case 1:
                matchName = null;
                sCommunication.getFriendList(user);
                sCommunication.getInvitationList(user);
                break;
            case 2:
                sCommunication.getMatchList(user);
                break;
        }
    }

    /**
     * Función listener encargada de gestionar los clicks realizados por el usuario a los JButtons de las tablas de Amigos, Invitaciones de amistad y de Partidas
     *
     * @param e - MouseClicked correspondiente al click del usuario a los JButtons de las tablas
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (((JTable) e.getSource()).getName().equals(HomeView.FRIENDS)) {
            int column = view.getJTFriends().getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / view.getJTFriends().getRowHeight();
            if (view.getDataFriends()[row][column] instanceof JButton) {
                JButton jButton = (JButton) view.getDataFriends()[row][column];
                String[] params = jButton.getActionCommand().split("-");

                String action = params[0];
                String friendName = params[1];

                if (action.equals(HomeView.INVITE)) {
                    sCommunication.inviteFriendToMatch(user.getUsername(), friendName);
                } else if (action.equals(HomeView.DELETE)) {
                    sCommunication.deleteFriend(user.getUsername(), friendName);
                }
            }
        } else if (((JTable) e.getSource()).getName().equals(HomeView.INVITATIONS)) {
            int column = view.getJTInvitations().getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / view.getJTInvitations().getRowHeight();
            if (view.getDataInvitations()[row][column] instanceof JButton) {
                JButton jButton = (JButton) view.getDataInvitations()[row][column];
                String[] params = jButton.getActionCommand().split("-");

                String action = params[0];
                String friendName = params[1];

                if (action.equals(HomeView.ACCEPT)) {
                    sCommunication.acceptFriend(user.getUsername(), friendName);
                } else if (action.equals(HomeView.DECLINE)) {
                    sCommunication.declineFriend(user.getUsername(), friendName);
                }
            }
        } else {
            int column = view.getJTMatches().getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / view.getJTMatches().getRowHeight();
            if (view.getDataMatches()[row][column] instanceof JButton) {
                JButton jButton = (JButton) view.getDataMatches()[row][column];
                String[] params = jButton.getActionCommand().split("-");

                String action = params[0];
                String matchName = params[1];

                if (action.equals(HomeView.JOIN_MATCH)) {
                    sCommunication.joinMatch(user.getUsername(), matchName);
                } else if (action.equals(HomeView.VIEW_MATCH)) {
                    sCommunication.viewMatch(user.getUsername(), matchName);
                }
            }
        }
    }

    /**
     * Función encargada de inicializar la GameView junto a su Controlador
     */
    private void goToGameView(User user, String matchName) {
        GameView gameView = new GameView();
        new GameController(gameView, sCommunication, user, matchName);

        gameView.showView();

        view.setVisible(false);
        view.dispose();
    }

}