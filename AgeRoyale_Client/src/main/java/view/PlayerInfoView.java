package view;

import javax.swing.*;
import java.awt.*;

/** Representa el JPanel (custom) PlayerInfoView de un jugador Host/Guest en la partida  */
public class PlayerInfoView extends JPanel {

    /** Representa el JLabel que contiene el nombre del jugador */
    private JLabel playerName;
    /** Representa el JLabel que contiene los puntos de salud del jugador */
    private JLabel vitalityPoints;
    /** Representa el JLabel que contiene el dinero disponible del jugador */
    private JLabel money;
    /** Representa el JLabel que contiene el tiempo transcurrido de la partida */
    private JLabel time;

    /** Representa el JButton que permite crear la tropa número 1 (Warrior) */
    private JButton troop1;
    /** Representa el JButton que permite crear la tropa número 2 (Archer) */
    private JButton troop2;
    /** Representa el JButton que permite crear la tropa número 3 (Cannon) */
    private JButton troop3;
    /** Representa el JButton que permite crear la tropa número 4 (Archer Tower) */
    private JButton troop4;

    /** Representa el JLabel que contiene el coste de la tropa número 1 (Warrior) */
    private JLabel costTroop1;
    /** Representa el JLabel que contiene el coste de la tropa número 2 (Archer) */
    private JLabel costTroop2;
    /** Representa el JLabel que contiene el coste de la tropa número 3 (Cannon) */
    private JLabel costTroop3;
    /** Representa el JLabel que contiene el coste de la tropa número 4 (Archer Tower) */
    private JLabel costTroop4;

    /**
     * Crea el JPanel (custom) PlayerInfoView
     */
    public PlayerInfoView() {
        initView();
    }

    /**
     * Función encargada de inicializar el JPanel y sus componentes principales
     */
    private void initView() {
        this.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        playerName = new JLabel();
        vitalityPoints = new JLabel();
        money = new JLabel();
        time = new JLabel();
        
        troop1 = new JButton();
        troop2 = new JButton();
        troop3 = new JButton();
        troop4 = new JButton();

        costTroop1 = new JLabel();
        costTroop2 = new JLabel();
        costTroop3 = new JLabel();
        costTroop4 = new JLabel();

        //First row
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(playerName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(vitalityPoints, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        this.add(money, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        this.add(time, gbc);

        //Second row
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(troop1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(troop2, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        this.add(troop3, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        this.add(troop4, gbc);

        //Third row
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(costTroop1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        this.add(costTroop2, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        this.add(costTroop3, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        this.add(costTroop4, gbc);
    }

    public JLabel getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName.setText(playerName);
    }

    public JLabel getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public JLabel getVitalityPoints() {
        return vitalityPoints;
    }

    public void setVitalityPoints(String vitalityPoints) {
        this.vitalityPoints.setText(vitalityPoints);
    }

    public JLabel getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money.setText(money);
    }

    public JButton getTroop1() {
        return troop1;
    }

    public void setTroop1(String troop1) {
        this.troop1.setText(troop1);
    }

    public JLabel getCostTroop1() {
        return costTroop1;
    }

    public void setCostTroop1(String costTroop1) {
        this.costTroop1.setText(costTroop1);
    }

    public JButton getTroop2() {
        return troop2;
    }

    public void setTroop2(String troop2) {
        this.troop2.setText(troop2);
    }

    public JLabel getCostTroop2() {
        return costTroop2;
    }

    public void setCostTroop2(String costTroop2) {
        this.costTroop2.setText(costTroop2);
    }

    public JButton getTroop3() {
        return troop3;
    }

    public void setTroop3(String troop3) {
        this.troop3.setText(troop3);
    }

    public JLabel getCostTroop3() {
        return costTroop3;
    }

    public void setCostTroop3(String costTroop3) {
        this.costTroop3.setText(costTroop3);
    }

    public JButton getTroop4() {
        return troop4;
    }

    public void setTroop4(String troop4) {
        this.troop4.setText(troop4);
    }

    public JLabel getCostTroop4() {
        return costTroop4;
    }

    public void setCostTroop4(String costTroop4) {
        this.costTroop4.setText(costTroop4);
    }
}