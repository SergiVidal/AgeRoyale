package model.entity;

import model.enumeration.CellType;

import java.io.Serializable;
import java.util.Arrays;

/** Representa la clase Board, esta contiene la información del tablero (Model) */
public class Board implements Serializable {
    private Cell[][] cells;

    /**
     * Crea el Board (Model), según la fila y la columna inicializa las celdas de una forma u otra, contienen una Tower (Torre de un jugador), Ground (Campo de batalla) o Range (Celdas donde un jugador puede colocar tropas)
     * @param rows - Representa el número de filas que tendrá el tablero
     * @param cols - Representa el número de columnas que tendrá el tablero
     */
    public Board(int rows, int cols) {
        cells = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i <= 1 && j >= 4 && j <= 5) || (i >= 18 && i <= 19 && j >= 4 && j <= 5)) {
                    cells[i][j] = new Cell(CellType.Tower);
                } else if (i >= 5 && i <= 14) {
                    cells[i][j] = new Cell(CellType.Ground);
                } else {
                    cells[i][j] = new Cell(CellType.Range);
                }
            }
        }
    }


    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "Board{" +
                "cells=" + Arrays.toString(cells) +
                '}';
    }
}