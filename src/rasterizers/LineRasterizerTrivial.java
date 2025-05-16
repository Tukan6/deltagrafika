package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

/**
 * Základní implementace rasterizéru čar
 * Používá triviální algoritmus pro kreslení čar
 */
public class LineRasterizerTrivial implements Rasterizer {

    private Raster raster;  // Cílový rastr pro vykreslení

    /**
     * Konstruktor rasterizéru čar
     * @param raster cílový rastr pro vykreslení
     */
    public LineRasterizerTrivial(Raster raster) {
        this.raster = raster;
    }

    /**
     * Rasterizuje jednu čáru do cílového rastru
     * @param line čára k rasterizaci
     */
    @Override
    public void rasterize(Line line) {
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();
        int thickness = line.getThickness();

        // Kontrola, jestli jsou body v plátně
        int width = raster.getWidth();
        int height = raster.getHeight();

        if (x1 == x2) { // Vertikální čára
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int y = y1; y <= y2; y++) {
                if (isInBounds(x1, y, width, height)) {
                    for (int i = 0; i < thickness; i++) {
                        int xOffset = (thickness % 2 == 0) ? i - (thickness / 2) : i - (thickness / 2);
                        if (isInBounds(x1 + xOffset, y, width, height)) {
                            raster.setPixel(x1 + xOffset, y, line.getColor().getRGB());
                        }
                    }
                }
            }
            return;
        }

        float k = (float) (y2 - y1) / (x2 - x1);
        float q = y1 - (k * x1);

        if (Math.abs(k) < 1) {
            if (x1 > x2) {
                int x = x1;
                x1 = x2;
                x2 = x;
                
                int y = y1;
                y1 = y2;
                y2 = y;
            }

            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);
                
                if (isInBounds(x, y, width, height)) {
                    for (int i = 0; i < thickness; i++) {
                        int yOffset = (thickness % 2 == 0) ? i - (thickness / 2) : i - (thickness / 2);
                        if (isInBounds(x, y + yOffset, width, height)) {
                            raster.setPixel(x, y + yOffset, line.getColor().getRGB());
                        }
                    }
                }
            }
        } else {
            if (y1 > y2) {
                int y = y1;
                y1 = y2;
                y2 = y;
                
                int x = x1;
                x1 = x2;
                x2 = x;
            }

            for (int y = y1; y <= y2; y++) {
                int x = Math.round((y - q) / k);
                
                if (isInBounds(x, y, width, height)) {
                    for (int i = 0; i < thickness; i++) {
                        int xOffset = (thickness % 2 == 0) ? i - (thickness / 2) : i - (thickness / 2);
                        if (isInBounds(x + xOffset, y, width, height)) {
                            raster.setPixel(x + xOffset, y, line.getColor().getRGB());
                        }
                    }
                }
            }
        }
    }

    private boolean isInBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Rasterizuje pole čar do cílového rastru
     * @param arrayList pole čar k rasterizaci
     */
    @Override
    public void rasterizeArray(ArrayList<Line> arrayList) {
        for (Line line : arrayList) {
            rasterize(line);
        }
    }
}
