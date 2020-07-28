package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** Representa una celda del tablero (Vista) */
public class CellView extends JPanel {
    /** Representa la imagen que tendrá la celda */
    private String imagePath;
    /** Representa el número de filas que tendrá el tablero */
    private int row;
    /** Representa el número de columnas que tendrá el tablero */
    private int column;

//    public CellView() {
//    }

    /**
     * Crea una CellView
     * @param row - Valor de la fila del tablero
     * @param column - Valor de la columna del tablero
     */
    public CellView(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Función encargada de pintar/repintar el componente
     * @param g - Objeto de la clase Graphics utilizado para pintar la celda
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagePath != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
                g.drawImage(bufferedImage, 0, 0, 48, 48, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
