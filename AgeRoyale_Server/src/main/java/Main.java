import controller.StatisticController;
import controller.SystemController;
import model.database.DBConnector;
import model.manager.*;
import model.network.Server;
import utils.Utility;
import view.StatisticView;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserManager userManager=new UserManager();
            MatchManager matchManager = new MatchManager();
            MatchInvitationManager matchInvitationManager = new MatchInvitationManager();
            PlayerManager playerManager = new PlayerManager();
            TroopManager troopManager = new TroopManager();
            DBConnector.getInstance();
            SystemController systemController = new SystemController(userManager,matchManager,matchInvitationManager,playerManager,troopManager);
            StatisticView statisticView = new StatisticView();
            StatisticController statisticController = new StatisticController(statisticView, matchManager,userManager);
            statisticView.registerController(statisticController);
            statisticView.showView();
        });
    }
}
