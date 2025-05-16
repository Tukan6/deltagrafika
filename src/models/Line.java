package models;

import java.awt.*;

/**
 * Třída reprezentující čáru mezi dvěma body
 */
public class Line {

    private Point point1;   // První bod čáry
    private Point point2;   // Druhý bod čáry
    private Color color;    // Barva čáry
    private int thickness;  // Tloušťka čáry

    /**
     * Konstruktor pro vytvoření čáry s výchozí tloušťkou 1
     * @param point1 První bod čáry
     * @param point2 Druhý bod čáry
     * @param color Barva čáry
     */
    public Line(Point point1, Point point2, Color color) {
        this.point1 = point1;
        this.point2 = point2;
        this.color = color;
        this.thickness = 1; // Výchozí tloušťka
    }

    /**
     * Konstruktor pro vytvoření čáry se zadanou tloušťkou
     * @param point1 První bod čáry
     * @param point2 Druhý bod čáry
     * @param color Barva čáry
     * @param thickness Tloušťka čáry
     */
    public Line(Point point1, Point point2, Color color, int thickness) {
        this.point1 = point1;
        this.point2 = point2;
        this.color = color;
        this.thickness = thickness;
    }

    /**
     * Získání barvy čáry
     * @return Barva čáry
     */
    public Color getColor() {
        return color;
    }

    /**
     * Nastavení barvy čáry
     * @param color Nová barva čáry
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Získání prvního bodu čáry
     * @return První bod čáry
     */
    public Point getPoint1() {
        return point1;
    }

    /**
     * Získání druhého bodu čáry
     * @return Druhý bod čáry
     */
    public Point getPoint2() {
        return point2;
    }

    /**
     * Získání tloušťky čáry
     * @return Tloušťka čáry
     */
    public int getThickness() {
        return thickness;
    }
}
