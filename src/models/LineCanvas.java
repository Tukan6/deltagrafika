package models;

import java.util.ArrayList;

/**
 * Třída představující plátno pro kreslení čar a tvarů
 * Uchovává všechny nakreslené objekty
 */
public class LineCanvas {

    private ArrayList<Line> lines;        // Seznam normálních čar
    private ArrayList<Line> dottedLines;  // Seznam přerušovaných čar
    private ArrayList<Shape> shapes;      // Seznam tvarů

    /**
     * Konstruktor pro vytvoření plátna
     * @param lines seznam normálních čar
     * @param dottedLines seznam přerušovaných čar
     */
    public LineCanvas(
            ArrayList<Line> lines,
            ArrayList<Line> dottedLines
    ) {
        this.lines = lines;
        this.dottedLines = dottedLines;
        this.shapes = new ArrayList<>();
    }

    /**
     * Získání seznamu normálních čar
     * @return seznam čar
     */
    public ArrayList<Line> getLines() {
        return lines;
    }

    /**
     * Získání seznamu přerušovaných čar
     * @return seznam přerušovaných čar
     */
    public ArrayList<Line> getDottedLines() {
        return dottedLines;
    }
    
    /**
     * Získání seznamu tvarů
     * @return seznam tvarů
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * Přidání normální čáry do plátna
     * @param line čára k přidání
     */
    public void add(Line line) {
        lines.add(line);
    }

    /**
     * Přidání přerušované čáry do plátna
     * @param line čára k přidání
     */
    public void addDottedLine(Line line) {
        dottedLines.add(line);
    }
    
    /**
     * Přidání tvaru do plátna
     * @param shape tvar k přidání
     */
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    
    /**
     * Najde tvar na zadaných souřadnicích
     * Kontroluje od nejvrchnějšího (naposledy přidaného) tvaru
     * @param x X-ová souřadnice
     * @param y Y-ová souřadnice
     * @return tvar na zadaných souřadnicích nebo null, pokud žádný tvar není nalezen
     */
    public Shape getShapeAt(int x, int y) {
        // Procházíme od konce (naposledy přidaného tvaru)
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }
    
    /**
     * Odstraní tvar z plátna
     * @param shape tvar k odstranění
     */
    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    /**
     * Vyčistí plátno - odstraní všechny čáry a tvary
     */
    public void clear() {
        lines.clear();
        dottedLines.clear();
        shapes.clear();
    }
}
