package view;

import controller.HomeController;
import utils.Utility;

import javax.swing.*;
import java.awt.*;

/** Representa el JDialog cuando el usuario esta a la espera de que otro se una a la partida */
public class WaitingDialog extends JDialog{

    /* Constantes */
    public static final String CANCEL_MATCH = "Cancel match";
    public static final String LABEL_INFO = "Waiting for guest player...";
    /**/

    /** Representa el JButton que permite cancelar la partida, ya sea pública o privada (por invitación) */
    private JButton bCancel;

    /**
     * Crea el WaitingDialog
     * @param owner - Identifica la vista (padre/propietaria del JDialog)
     * @param title - Representa el título que tendrá el JDialog
     * @param modal - Indica si el JDialog es Modal o no (True: No permite realizar ninguna acción que no sea en este JDialog / False: Permite realizar acciones fuera del JDialog)
     */
    public WaitingDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(4 * Utility.STANDARD_SIZE, 2 * Utility.STANDARD_SIZE);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initDialog();

    }

    /**
     * Función encargada de inicializar el WaitingDialog y sus componentes principales
     */
    private void initDialog() {
        JPanel jpMain = new JPanel(new BorderLayout());

        JLabel jLabel = new JLabel(LABEL_INFO);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bCancel = new JButton(CANCEL_MATCH);

        jpMain.add(jLabel, JLabel.CENTER);
        jpMain.add(bCancel, BorderLayout.SOUTH);

        this.add(jpMain);
    }

    /**
     * Función encargada de hacer visible el WaitingDialog
     */
    public void showDialog() {
        setVisible(true);
    }

    /**
     * Función encargada de registrar los componentes con el controlador del WaitingDialog
     * @param controller - Controlador del WaitingDialog
     */
    public void registerController(HomeController controller) {
        bCancel.setActionCommand(CANCEL_MATCH);
        bCancel.addActionListener(controller);
    }
}
