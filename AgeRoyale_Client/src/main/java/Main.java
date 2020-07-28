import controller.LoginController;
import controller.RegisterController;
import model.network.ConnectionConfig;
import model.network.ServerCommunication;
import utils.Utility;
import view.LoginView;
import view.RegisterView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectionConfig config = Utility.getServerConfig();
            ServerCommunication sCommunication;
            if(config != null) {

                sCommunication = new ServerCommunication(config);

                sCommunication.startServerCommunication();

                LoginView loginView = new LoginView();
                LoginController loginController = new LoginController(loginView, sCommunication);
                sCommunication.registerCallback(loginController);

                loginView.registerController(loginController);
                loginView.showView();
            }
        });
    }
}