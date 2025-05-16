package models;

import java.awt.*;

/**
 * Třída reprezentující kruh
 */
public class Circle implements Shape {
    private int centerX;   // X-ová souřadnice středu kruhu
    private int centerY;   // Y-ová souřadnice středu kruhu
    private int radius;    // Poloměr kruhu
    private Color color;   // Barva kruhu
    private int thickness; // Tloušťka čáry
    private boolean selected;
    private boolean filled;

    /**
     * Konstruktor pro vytvoření kruhu
     * @param centerX X-ová souřadnice středu
     * @param centerY Y-ová souřadnice středu
     * @param radius Poloměr kruhu
     * @param color Barva kruhu
     * @param thickness Tloušťka čáry
     */
    public Circle(int centerX, int centerY, int radius, Color color, int thickness) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.selected = false;
        this.filled = false;
    }

    /**
     * Metoda pro vykreslení kruhu
     * @param g Grafický kontext
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        
        if (filled) {
            g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        } else {
            g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        if (selected) {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g.drawOval(centerX - radius - 2, centerY - radius - 2, (radius * 2) + 4, (radius * 2) + 4);
            
            // Vykreslení úchytu pro změnu velikosti
            g.setColor(Color.BLACK);
            g.fillRect(centerX + radius - 4, centerY - 4, 8, 8);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        // Výpočet vzdálenosti od středu k bodu (x,y)
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        // Pokud je kruh vyplněný, zkontroluju jestli je bod uvnitř
        if (filled) {
            return distance <= radius;
        } else {
            // Pro nevyplněný kruh, zkontroluju jestli je bod blízko obvodu
            return Math.abs(distance - radius) <= thickness + 2;
        }
    }

    @Override
    public void move(int deltaX, int deltaY) {
        centerX += deltaX;
        centerY += deltaY;
    }

    @Override
    public void resize(int x, int y) {
        // Vypočítám nový rádius podle vzdálenosti od středu k bodu (x,y)
        int newRadius = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        if (newRadius > 0) {
            this.radius = newRadius;
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
    
    public int getCenterX() {
        return centerX;
    }
    
    public int getCenterY() {
        return centerY;
    }
    
    public int getRadius() {
        return radius;
    }
}
