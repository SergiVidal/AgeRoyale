package model.entity;

import model.enumeration.CellType;

import java.io.Serializable;

/** Representa la clase Cell, esta contiene la información de una celda del tablero (Model) */
public class Cell implements Serializable {

    /**
     * Representa la tropa que ocupa una celda (en caso de que este ocupada)
     */
    private Troop troop;
    /**
     * Representa el tipo (enum) de celda, estos pueden ser: Tower, Ground, Range
     */
    private CellType cellType;

    /**
     * Crea una Cell del tablero
     */
    public Cell() {

    }

    /**
     * Crea una Cell del tablero
     * @param cellType - Representa el tipo de la celda
     */
    public Cell(CellType cellType) {
        this.cellType = cellType;
    }

    /**
     * Crea una Cell del tablero
     * @param troop    - Representa la tropa que ocupa la celda
     * @param cellType - Representa el tipo de la celda
     */
    public Cell(Troop troop, CellType cellType) {
        this.troop = troop;
        this.cellType = cellType;
    }

    public Troop getTroop() {
        return troop;
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "troop=" + troop +
                ", cellType=" + cellType +
                '}';
    }
}