package rasters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Implementace rastru pomocí BufferedImage z Java2D
 * Slouží jako plátno pro kreslení pixelů
 */
public class RasterBufferedImage implements Raster {

    private final BufferedImage img;  // Obrázek pro ukládání pixelů
    private int color;                // Barva pro mazání plátna

    /**
     * Získání podkladového BufferedImage
     * @return podkladový obrázek
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * Konstruktor vytvoří nový rastr dané velikosti
     * @param width šířka rastru v pixelech
     * @param height výška rastru v pixelech
     */
    public RasterBufferedImage(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Vykreslí rastr do zadaného grafického kontextu
     * @param graphics grafický kontext pro vykreslení
     */
    public void repaint(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    /**
     * Získání grafického kontextu pro kreslení do rastru
     * @return grafický kontext
     */
    public Graphics getGraphics(){
        return img.getGraphics();
    }

    /**
     * Získá barvu pixelu na zadaných souřadnicích
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @return barva pixelu (RGB formát)
     */
    @Override
    public int getPixel(int x, int y) {
        return img.getRGB(x, y);
    }

    /**
     * Nastaví barvu pixelu na zadaných souřadnicích
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @param color barva pixelu (RGB formát)
     */
    @Override
    public void setPixel(int x, int y, int color) {
        img.setRGB(x, y, color);
    }

    /**
     * Vymaže rastr (vyplní nastavenou barvou)
     */
    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(color));
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Nastaví barvu používanou pro mazání plátna
     * @param color barva (RGB formát)
     */
    @Override
    public void setClearColor(int color) {
        this.color = color;
    }

    /**
     * Vrací šířku rastru v pixelech
     * @return šířka rastru
     */
    @Override
    public int getWidth() {
        return img.getWidth();
    }

    /**
     * Vrací výšku rastru v pixelech
     * @return výška rastru
     */
    @Override
    public int getHeight() {
        return img.getHeight();
    }

}
