package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Representa el DefaultTableModel de la JTable que contiene la lista de solicitudes de amistad la vista  */
public class JTableInvitationList extends DefaultTableModel {
    /** Representa el tipo (Class) que contienen las columnas de la JTable*/
    final Class<?>[] columnClass = new Class[]{
            String.class, String.class, JButton.class, JButton.class};

    /**
     * Crea el DefaultTableModel
     */
    public JTableInvitationList() {
    }

    /**
     * Crea el DefaultTableModel
     * @param data - Valor de los datos de la JTable
     * @param columns - Valor de las columnas de la JTable
     */
    public JTableInvitationList(Object[][] data, Object[] columns) {
        super(data, columns);
    }

    /**
     * Función encargada de configurar si las celdas de la JTable son editables
     * @param row - Fila en la que esta situada la celda a configurar
     * @param column - Columna en la que esta situada la celda a configurar
     * @return - Devuelve la configuración en formato booleano (True: Es editable/ False: No es editable)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Función encargada de obtener el tipo (Class) de las columnas de la JTable
     * @param columnIndex - Indica la posición de la columna
     * @return - Devuelve el tipo de la columna
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

}