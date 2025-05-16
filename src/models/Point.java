package models;

/**
 * Třída reprezentující bod v 2D prostoru
 */
public class Point {

    private int x;  // X-ová souřadnice bodu
    private int y;  // Y-ová souřadnice bodu

    /**
     * Konstruktor pro vytvoření bodu
     * @param x X-ová souřadnice bodu
     * @param y Y-ová souřadnice bodu
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Získání X-ové souřadnice bodu
     * @return X-ová souřadnice
     */
    public int getX() {
        return x;
    }

    /**
     * Získání Y-ové souřadnice bodu
     * @return Y-ová souřadnice
     */
    public int getY() {
        return y;
    }

    /**
     * Nastavení X-ové souřadnice bodu
     * @param x Nová X-ová souřadnice
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Nastavení Y-ové souřadnice bodu
     * @param y Nová Y-ová souřadnice
     */
    public void setY(int y) {
        this.y = y;
    }

}
