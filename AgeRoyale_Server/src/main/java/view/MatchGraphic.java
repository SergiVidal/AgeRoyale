package view;

import model.entity.MatchStat;
import model.entity.MatchStatItem;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la clase que nos permite crear y visualizar el gráfico.
 */
public class MatchGraphic extends JPanel {
    /**
     * Representa la lista con los valores que tendrá el eje de las Y para cada item en el gráfico.
     */
    private ArrayList<Integer> list;
    /**
     * Representa el espaciado interno / padding que se haré en el gráfico.
     */
    private final int PADDING = 30;
    /**
     * Representa la lista de valores del eje de las x.
     */
    private ArrayList<String> timeValueText;
    /**
     * Representa la lista de puntos del gráfico.
     */
    private List<Point> points;
    /**
     * Representa la distancia de origen en eje de las x.
     */
    public static final int X_ORIG_DISTANCE = 20;
    /**
     * Representa las variables que nos permiten especificar el valor de escalaje para el eje de las x y el eje de las y.
     */
    private double xScale, yScale;
    /**
     * Representa la distancia de origen en eje de las y.
     */
    private static final int Y_ORIG_DISTANCE = 20;

    /**
     * Representa la anchura y la altura. Además de las coordenadas x e y donde está el item.
     */
    private int horizontalWidth, verticalWidth, xCoord, yCoord;

    /**
     * Crea el MatchGraphic.
     */
    public MatchGraphic() {
        this.timeValueText = new ArrayList<>();
        this.points = new ArrayList<>();
        this.list = new ArrayList<>();
    }

    /**
     * Sirve para conseguir las medidas actuales del gráfico de líneas.
     */
    private void getActualLineSizeAndMeasures() {

        horizontalWidth = getWidth() - X_ORIG_DISTANCE;
        verticalWidth = getHeight();

        xCoord = X_ORIG_DISTANCE;
        yCoord = getHeight() - Y_ORIG_DISTANCE;
    }

    /**
     * Método que sirve para pintar el gráfico.
     * @param g - Representa un objeto de la class Graphics de AWT
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getActualLineSizeAndMeasures();
        addPointsToLine();
        Graphics2D graphics2D = (Graphics2D) g;
        int value = 0;

        graphics2D.setStroke(new BasicStroke(2f));
        graphics2D.setColor(Color.GREEN);
        if (list.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                graphics2D.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
        } else {
            graphics2D.setColor(Color.GREEN);
            graphics2D.drawLine(X_ORIG_DISTANCE, yCoord - ((int) ((value / getMaxItem()) * verticalWidth)) - 10, getWidth(), yCoord - ((int) ((value / getMaxItem()) * verticalWidth)) - 10);
        }

        graphics2D.setColor(Color.DARK_GRAY);

        prepareLineGraphic((Graphics2D) g, list);
    }

    /**
     * Método que sirve para recuperar el ítem máximo, osea el ítem que tenga más partidas.
     * @return Devuelve un double que indica el valor máximo.
     */
    private double getMaxItem() {
        double max = 0.0f;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }

    /**
     * Método que sirve para preparar el gráfico de líneas.
     * @param graphics2D  - Representa un objeto de la class Graphics2D de AWT
     * @param list -  Representa la lista con los valores de partidas realizadas para cada item de las x.
     */
    private void prepareLineGraphic(Graphics2D graphics2D, ArrayList<Integer> list) {
        graphics2D.drawLine(xCoord, yCoord, xCoord + horizontalWidth, yCoord);
        graphics2D.drawLine(xCoord, yCoord, xCoord, yCoord - verticalWidth);

        //posar el valor de dies
        for (int i = 0; i < list.size(); i++) {
            String xLabel = i + "";
            FontMetrics metrics = graphics2D.getFontMetrics();
            if (list.size() > 1) {
                int x0 = i * (getWidth() - 25 * 2 - 10) / (list.size() - 1) + 25 + 25;
                int labelWidth = metrics.stringWidth(xLabel);

                graphics2D.drawString(timeValueText.get(i), x0 - labelWidth / 2 - 25, yCoord + 15);
                graphics2D.drawString(list.get(i).toString(), ((getWidth() / list.size())) * i + 75, getHeight() / 2);
            } else {
                graphics2D.drawString(timeValueText.get(0), 24 - 25 / 2 + metrics.stringWidth(xLabel), yCoord + 15);
                graphics2D.drawString(list.get(i).toString(), 24 - 25 / 2 + getWidth() / 2, getHeight() / 2);

            }
        }

        AffineTransform affineTransform = new AffineTransform();
        //rotá en perpendicular a esquerra (vertical) (el text)
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        affineTransform.scale(1.0f, 1.0f);
        Font rotatedFont = getFont().deriveFont(affineTransform);
        graphics2D.setFont(rotatedFont);
        graphics2D.drawString("Matches Num",Y_ORIG_DISTANCE / 2,getHeight()/2);

    }

    /**
     * Método que sirve para añadir los puntos a la línea.
     */
    private void addPointsToLine() {
        points.removeAll(points);
        getActualLineSizeAndMeasures();
        xScale = ((double) getWidth() - 2 * PADDING) / (list.size() - 1);
        yScale = ((double) getHeight() - 2 * PADDING) / ((int) getMaxItem() - 1);
        for (int i = 0; i < list.size(); i++) {
            points.add(new Point((int) (i * xScale + PADDING), (int) (((int) getMaxItem() - list.get(i)) * yScale + PADDING)));
        }
    }

    /**
     * Método que sirve para actualizar el gráfico con nuevos valores para el eje de las X y el eje de las Y y por lo tanto también para la línea.
     * @param matchStat - Representa el MatchStat
     * @param filter - Representa el filtro / modo de tiempo seleccionado.
     */
    public void updateGraphic(MatchStat matchStat, String filter) {
        list.removeAll(list);
        timeValueText.removeAll(timeValueText);

        for (MatchStatItem matchStatItem : matchStat.getMatchStatItems()) {
            list.add(matchStatItem.getMatchesCount());
            if (filter.equals("day")) {
                timeValueText.add(getDay(matchStatItem.getTimeId()) + "");
            } else if (filter.equals("month")) {
                timeValueText.add((matchStatItem.getTimeId()) + "");
                if (matchStatItem.getTimeId() + 6 < matchStatItem.getLastDay()) {
                    timeValueText.set(timeValueText.size() - 1, timeValueText.get(timeValueText.size() - 1) + ("-" + (matchStatItem.getTimeId() + 6)));
                } else {
                    timeValueText.set(timeValueText.size() - 1, timeValueText.get(timeValueText.size() - 1) + ("-" + (matchStatItem.getLastDay())));
                }
            } else {
                timeValueText.add(getMonth(matchStatItem.getTimeId()) + "");
            }
        }
    }

    /**
     * Método que sirve para recuperar el día dado un número de día de la semana.
     * @param weekday - Representa el día de la semana.
     * @return Devuelve un String con el día de la semana.
     */
    private String getDay(int weekday) {
        String weekDayName = "";
        switch (weekday) {
            case 1:
                weekDayName = "Monday";
                break;
            case 2:
                weekDayName = "Tuesday";
                break;
            case 3:
                weekDayName = "Wednesday";
                break;
            case 4:
                weekDayName = "Thursday";
                break;
            case 5:
                weekDayName = "Friday";
                break;
            case 6:
                weekDayName = "Saturday";
                break;
            case 7:
                weekDayName = "Sunday";
                break;
        }
        return weekDayName;
    }

    /**
     * Método que sirve para recuperar el mes dado un número de mes.
     * @param month - Representa el mes del año.
     * @return  Devuelve un String con el mes del año.
     */
    private String getMonth(int month) {
        String weekDayName = "";
        switch (month) {
            case 1:
                weekDayName = "January";
                break;
            case 2:
                weekDayName = "February";
                break;
            case 3:
                weekDayName = "March";
                break;
            case 4:
                weekDayName = "April";
                break;
            case 5:
                weekDayName = "May";
                break;
            case 6:
                weekDayName = "June";
                break;
            case 7:
                weekDayName = "July";
                break;
            case 8:
                weekDayName = "August";
                break;
            case 9:
                weekDayName = "September";
                break;
            case 10:
                weekDayName = "October";
                break;
            case 11:
                weekDayName = "November";
                break;
            case 12:
                weekDayName = "December";
                break;
        }
        return weekDayName;
    }
}