package rasterizers;

import models.Line;
import models.LineCanvas;
import models.Shape;
import rasters.Raster;

/**
 * Třída pro rasterizaci celého plátna s čarami a tvary
 * Deleguje rasterizaci konkrétním rasterizérům podle typu objektu
 */
public class LineCanvasRasterizer {

    private Raster raster;                     // Cílový rastr pro kreslení
    private Rasterizer lineRasterizer;         // Rasterizér pro normální čáry
    private Rasterizer dottedLineRasterizer;   // Rasterizér pro přerušované čáry
    private ShapeRasterizer shapeRasterizer;   // Rasterizér pro tvary

    /**
     * Konstruktor rasterizéru plátna
     * @param raster cílový rastr pro kreslení
     */
    public LineCanvasRasterizer(Raster raster) {
        this.raster = raster;

        lineRasterizer = new LineRasterizerTrivial(raster);
        dottedLineRasterizer = new DottedLineRasterizerTrivial(raster);
        shapeRasterizer = new ShapeRasterizer(raster);
    }

    /**
     * Rasterizuje celé plátno se všemi objekty
     * @param canvas plátno k rasterizaci
     */
    public void rasterizeCanvas(LineCanvas canvas) {
        lineRasterizer.rasterizeArray(canvas.getLines());
        dottedLineRasterizer.rasterizeArray(canvas.getDottedLines());
        shapeRasterizer.rasterizeShapes(canvas.getShapes());
    }

    /**
     * Rasterizuje jednu normální čáru
     * @param line čára k rasterizaci
     */
    public void rasterizeLine(Line line) {
        lineRasterizer.rasterize(line);
    }

    /**
     * Rasterizuje jednu přerušovanou čáru
     * @param line čára k rasterizaci
     */
    public void rasterizeDottedLine(Line line) {
        dottedLineRasterizer.rasterize(line);
    }
    
    /**
     * Rasterizuje jeden tvar
     * @param shape tvar k rasterizaci
     */
    public void rasterizeShape(Shape shape) {
        shapeRasterizer.rasterizeShape(shape);
    }
}
