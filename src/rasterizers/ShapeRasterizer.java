package rasterizers;

import models.Shape;
import rasters.Raster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Třída pro rasterizaci tvarů do rastru
 * Používá Java2D pro vykreslení tvaru do bufferu a pak překopíruje pixely do cílového rastru
 */
public class ShapeRasterizer {
    private Raster raster;                  // Cílový rastr pro vykreslení
    private Graphics2D graphics;             // Grafický kontext pro kreslení
    private BufferedImage bufferedImage;     // Pomocný buffer pro kreslení tvarů

    /**
     * Konstruktor rasterizéru tvarů
     * @param raster cílový rastr pro vykreslení
     */
    public ShapeRasterizer(Raster raster) {
        this.raster = raster;
        // Vytvoření bufferu pro vykreslování s podporou průhlednosti
        bufferedImage = new BufferedImage(raster.getWidth(), raster.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        // Zapnutí vyhlazování
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Rasterizuje jeden tvar do cílového rastru
     * @param shape tvar k rasterizaci
     */
    public void rasterizeShape(Shape shape) {
        // Vyčištění bufferu
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.setComposite(AlphaComposite.SrcOver);
        
        // Vykreslení tvaru do bufferu
        shape.draw(graphics);

        // Kopírování pixelů z bufferu do cílového rastru
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int pixel = bufferedImage.getRGB(x, y);
                if ((pixel & 0xFF000000) != 0) { // Kontrola, zda pixel není průhledný
                    raster.setPixel(x, y, pixel);
                }
            }
        }
    }

    /**
     * Rasterizuje pole tvarů do cílového rastru
     * @param shapes seznam tvarů k rasterizaci
     */
    public void rasterizeShapes(ArrayList<Shape> shapes) {
        for (Shape shape : shapes) {
            rasterizeShape(shape);
        }
    }
}
