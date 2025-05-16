package rasters;

import java.awt.Graphics;

/**
 * Rozhraní pro rastr (plátno) pro kreslení pixelů
 */
public interface Raster {

    /**
     * Vymaže plátno (vyplní nastavenou barvou)
     */
    void clear();

    /**
     * Nastaví barvu používanou pro mazání plátna
     *
     * @param clearColor barva (RGB formát)
     */
    void setClearColor(int clearColor);
    
    /**
     * Vykreslí rastr do zadaného grafického kontextu
     * 
     * @param graphics grafický kontext pro vykreslení
     */
    void repaint(Graphics graphics);

    /**
     * Získá barvu pixelu na zadaných souřadnicích
     *
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @return barva pixelu (RGB formát)
     */
    int getPixel(int x, int y);

    /**
     * Nastaví barvu pixelu na zadaných souřadnicích
     *
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @param color barva pixelu (RGB formát)
     */
    void setPixel(int x, int y, int color);

    /**
     * Vrací šířku rastru v pixelech
     *
     * @return šířka rastru
     */
    int getWidth();    /**
     * Vrací výšku rastru v pixelech
     *
     * @return výška rastru
     */
    int getHeight();
}
