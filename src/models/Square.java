package models;

import java.awt.*;

/**
 * Třída reprezentující čtverec
 */
public class Square implements Shape {
    private int x;          // X-ová souřadnice levého horního rohu
    private int y;          // Y-ová souřadnice levého horního rohu
    private int size;       // Velikost (délka strany) čtverce
    private Color color;    // Barva čtverce
    private int thickness;  // Tloušťka čáry
    private boolean selected;
    private boolean filled;

    /**
     * Konstruktor pro vytvoření čtverce
     * @param x X-ová souřadnice levého horního rohu
     * @param y Y-ová souřadnice levého horního rohu
     * @param size Velikost (délka strany) čtverce
     * @param color Barva čtverce
     * @param thickness Tloušťka čáry
     */
    public Square(int x, int y, int size, Color color, int thickness) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        this.thickness = thickness;
        this.selected = false;
        this.filled = false;
    }

    /**
     * Metoda pro vykreslení čtverce
     * @param g Grafický kontext
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        
        if (filled) {
            g.fillRect(x, y, size, size);
        } else {
            g.drawRect(x, y, size, size);
        }
        
        if (selected) {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g.drawRect(x - 2, y - 2, size + 4, size + 4);
            
            // Vykreslení úchytu pro změnu velikosti
            g.setColor(Color.BLACK);
            g.fillRect(x + size - 4, y + size - 4, 8, 8);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        // Vyplněný obdélník - zkontrolovat, jestli je bod uvnitř
        if (filled) {
            return px >= x && px <= x + size && py >= y && py <= y + size;
        } else {
            // U nevyplněného obdélníku zkontrolovat, jestli je bod blízko některé z hran
            int tolerance = thickness + 2;

            // Zkontrolovat, jestli je bod blízko horní nebo dolní hraně
            boolean nearHorizontalEdge = (py >= y - tolerance && py <= y + tolerance && px >= x && px <= x + size) ||
                                         (py >= y + size - tolerance && py <= y + size + tolerance && px >= x && px <= x + size);

            // Zkontrolovat, jestli je bod blízko levé nebo pravé hraně
            boolean nearVerticalEdge = (px >= x - tolerance && px <= x + tolerance && py >= y && py <= y + size) ||
                                       (px >= x + size - tolerance && px <= x + size + tolerance && py >= y && py <= y + size);
                                       
            return nearHorizontalEdge || nearVerticalEdge;
        }
    }

    @Override
    public void move(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    @Override
    public void resize(int newX, int newY) {
        // Výpočet maximální vzádlenost od původního bodu u x a y
        int deltaX = newX - x;
        int deltaY = newY - y;

        int newSize = Math.max(deltaX, deltaY);
        
        if (newSize > 0) {
            size = newSize;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
    
    @Override
    public boolean isFilled() {
        return filled;
    }
    
    //public void setThickness(int thickness) {this.thickness = thickness;}
    
    //public int getThickness() {return thickness;}
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
}
