package models;

import java.awt.*;

/**
 * Třída reprezentující obdélník
 */
public class Rectangle implements Shape {
    private int x;          // X-ová souřadnice levého horního rohu
    private int y;          // Y-ová souřadnice levého horního rohu
    private int width;      // Šířka obdélníku
    private int height;     // Výška obdélníku
    private Color color;    // Barva obdélníku
    private int thickness;  // Tloušťka čáry
    private boolean selected;
    private boolean filled;

    /**
     * Konstruktor pro vytvoření obdélníku
     * @param x X-ová souřadnice levého horního rohu
     * @param y Y-ová souřadnice levého horního rohu
     * @param width Šířka obdélníku
     * @param height Výška obdélníku
     * @param color Barva obdélníku
     * @param thickness Tloušťka čáry
     */
    public Rectangle(int x, int y, int width, int height, Color color, int thickness) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.thickness = thickness;
        this.selected = false;
        this.filled = false;
    }

    /**
     * Metoda pro vykreslení obdélníku
     * @param g Grafický kontext
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        
        if (filled) {
            g.fillRect(x, y, width, height);
        } else {
            g.drawRect(x, y, width, height);
        }
        
        if (selected) {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
            
            // Nakreslit úchyt pro změnu velikosti
            g.setColor(Color.BLACK);
            g.fillRect(x + width - 4, y + height - 4, 8, 8);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        // Vyplněný obdélník - zkontrolovat, jestli je bod uvnitř
        if (filled) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        } else {
            // U nevyplněného obdélníku zkontrolovat, jestli je bod blízko některé z hran
            int tolerance = thickness + 2;
            
            // Zkontrolovat, jestli je bod blízko horní nebo dolní hraně
            boolean nearHorizontalEdge = (py >= y - tolerance && py <= y + tolerance && px >= x && px <= x + width) ||
                                         (py >= y + height - tolerance && py <= y + height + tolerance && px >= x && px <= x + width);
                                         
            // Zkontrolovat, jestli je bod blízko levé nebo pravé hraně
            boolean nearVerticalEdge = (px >= x - tolerance && px <= x + tolerance && py >= y && py <= y + height) ||
                                       (px >= x + width - tolerance && px <= x + width + tolerance && py >= y && py <= y + height);
                                       
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
        int newWidth = newX - x;
        int newHeight = newY - y;
        
        if (newWidth > 0) {
            width = newWidth;
        }
        
        if (newHeight > 0) {
            height = newHeight;
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
    
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
    
    public int getThickness() {
        return thickness;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
