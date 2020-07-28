package view;

import javax.swing.*;
import java.awt.*;

/** Representa el tablero (Vista) */
public class GridView extends JPanel {
    /** Representa el conjunto de celdas (Vista) */
    private CellView[][] grid;
    /** Representa el número de filas que tendrá el tablero */
    private int rows;
    /** Representa el número de columnas que tendrá el tablero */
    private int columns;

    /**
     * Crea la GridView
     * @param rows - Número de filas que tendrá el tablero
     * @param columns - Número de columnas que tendrá el tablero
     */
    public GridView(int rows, int columns) {
        this.grid = new CellView[rows][columns];
        this.rows = rows;
        this.columns = columns;

        setLayout(new GridLayout(rows,columns));
        setSize(400,800);
    }

//    public void addCell(int x, int y, String imagePath){
//        if(x >= 0 && x < rows && y >= 0 && y < columns) {
//            this.grid[x][y].setImagePath(imagePath);
//        }
//    }

    public CellView[][] getGrid() {
        return grid;
    }

    public void setGrid(CellView[][] grid) {
        this.grid = grid;
    }
}