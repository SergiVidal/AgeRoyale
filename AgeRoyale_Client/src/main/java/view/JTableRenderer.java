package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Permite mostrar (renderizar) un JButton (entre otros componentes) correctamente en una JTable, de no utilizarlo se muestra como String (Ej: java.swing.JButton) */
public class JTableRenderer extends DefaultTableCellRenderer {

    /**
     * Función encargada de renderizas un componente
     * @param table - JTable a la que pertenece el componente
     * @param value - El valor (objeto) que asignar a una celda
     * @param isSelected - Indica si la celda es seleccionable (True: Seleccionable / False: No seleccionable)
     * @param hasFocus - Indica si la celda contiene el focus (True: Es el focus / False: No es el focus)
     * @param row - Indica la fila en la que se situa la celda a renderizar
     * @param column - Indica la columna en la que se situa la celda a renderizar
     * @return - Devuelve la celda (componente) renderizada
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return (Component) value;
    }
}

