package view;

import javax.swing.table.DefaultTableModel;

/**
 * Representa JTableTop10 que sirve para poder poner datos en la JTable.
 */
public class JTableTop10 extends DefaultTableModel {
    final Class<?>[] columnClass = new Class[]{
            String.class, String.class, String.class};

    /**
     * Crea la JTableTop10
     */
    public JTableTop10() {
    }

    /**
     *  Crea la JTableTop10
     * @param data - Representan los datos de la tabla.
     * @param columns - Representa la cabecera de la tabla.
     */
    public JTableTop10(Object[][] data, Object[] columns) {
        super(data, columns);
    }

    /**
     * Método para comprobar si una cela es editable.
     * @param row - Representa la posición por fila.
     * @param column - Representa la posición por columna.
     * @return Devuelve booleano que indica si es editable o no.Hacemos que no sea editable.
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Método para recuperar la clase de una columna.
     * @param columnIndex - Índice de columna.
     * @return Devuelve la clase del objeto o dato que haya en la columna.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

}