package controller;

import model.entity.UserStat;
import model.entity.UserStatistic;
import model.manager.MatchManager;
import model.manager.UserManager;
import view.StatisticView;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Representa la clase StatisticController.
 */
public class StatisticController implements ChangeListener, ActionListener {
    /**
     * Representa la vista de estadísticas - StatisticView.
     */
    private StatisticView view;
    /**
     * Representa el UserManager.
     */
    private UserManager userManager;
    /**
     * Representa el MatchManager.
     */
    private MatchManager matchManager;

    /**
     * Crea el StatisticController.
     * @param view - StatisticView.
     * @param matchManager - MatchManager
     * @param userManager - UserManager
     */
    public StatisticController(StatisticView view, MatchManager matchManager, UserManager userManager) {
        this.view = view;
        this.userManager = userManager;
        this.matchManager = matchManager;
        getMatchEvolution("day");
    }

    /**
     * Método que sirve para notificar a la vista de los top 10 players.
     * @param userStatistic - UserStatistic
     */
    public void notifyViewTop10(UserStatistic userStatistic) {
        view.initTableTop10(userStatistic.getUserStats().size());
        for (UserStat userStat : userStatistic.getUserStats()) {
            view.setDataUserList(userStat);
        }
        view.refreshTableTop10();
    }

    /**
     * Método que sirve para calcular el ratio de partidas ganadas.
     * @param userStat - UserStat
     * @return Devuelve un int con el ratio de partidas ganadas.
     */
    public int calculateWinRate(UserStat userStat) {
        int total = userStat.getWins() + userStat.getLoses();

        if (total != 0)
            return Math.round((float) userStat.getWins() * 100 / total);

        return 0;
    }

    /**
     * Método que sirve para calcular el tiempo medio jugado.
     * @param userStat - UserStat
     * @return Devuelve un entero con la media de tiempo que se ha jugado.
     */
    public int calculateAvgTime(UserStat userStat) {
        int total = userStat.getWins() + userStat.getLoses();

        if (total != 0)
            return Math.round((float) userStat.getTime() / total);

        return 0;
    }

    /**
     * Método que sirve para recuperar los top 10 players.
     */
    public void getTop10() {
        UserStatistic userStatistic = userManager.getTop10();
        for (UserStat userStat : userStatistic.getUserStats()) {
            userStat.setWinRate(calculateWinRate(userStat));
            userStat.setAvgTime(calculateAvgTime(userStat));
        }
        notifyViewTop10(userStatistic);
    }

    /**
     * Método que sirve para recuperar las evoluciones de las partidas.
     * @param filter - Filtro para seleccionar que modo de tiempo se quiere para visualizar las partidas ganadas.
     */
    public void getMatchEvolution(String filter) {
        view.updateBarGraphic(matchManager.getMatchEvolutionDataByMonthOrYear(filter), filter);
        view.getMatchGraphic().repaint();
        view.getMatchGraphic().revalidate();
    }

    /**
     * Método que sirve para detectar cuando se ha cambiado de pestaña / tab.
     * @param e - Evento ChangeEvent.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        switch (view.getTabbedPane().getSelectedIndex()) {
            case 0:
                getMatchEvolution("day");
                break;
            case 1:
                getTop10();
                break;
        }
    }

    /**
     * Método que sirve para detectar el accionado de un botón en la vista y para acabar informando a la vista de los datos obtenidos del manager.
     * @param e - Evento ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(StatisticView.DAY)) {
            getMatchEvolution("day");
        } else if (e.getActionCommand().equals(StatisticView.MONTH)) {
            getMatchEvolution("month");
        } else {
            getMatchEvolution("year");
        }
    }
}
