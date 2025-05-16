package models;

import java.awt.*;
import java.util.ArrayList;

/**
 * Třída reprezentující mnohoúhelník
 */
public class Polygon implements Shape {
    private ArrayList<Point> points; // Seznam bodů tvořících mnohoúhelník
    private Color color;            // Barva mnohoúhelníku
    private int thickness;          // Tloušťka čáry
    private boolean selected;       // Příznak výběru
    private boolean filled;         // Příznak výplně
    
    // Pro účely přesunu a výběru
    private int centerX;  // X-ová souřadnice středu
    private int centerY;  // Y-ová souřadnice středu

    /**
     * Konstruktor pro vytvoření mnohoúhelníku
     * @param points Seznam bodů tvořících mnohoúhelník
     * @param color Barva mnohoúhelníku
     * @param thickness Tloušťka čáry
     */
    public Polygon(ArrayList<Point> points, Color color, int thickness) {
        this.points = new ArrayList<>(points);
        this.color = color;
        this.thickness = thickness;
        this.selected = false;
        this.filled = false;
        calculateCenter();
    }
    
    /**
     * Výpočet středu mnohoúhelníku
     */
    private void calculateCenter() {
        int sumX = 0;
        int sumY = 0;
        
        for (Point p : points) {
            sumX += p.getX();
            sumY += p.getY();
        }
        
        centerX = sumX / points.size();
        centerY = sumY / points.size();
    }

    /**
     * Vykreslení mnohoúhelníku
     * @param g Grafický kontext
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        
        int[] xPoints = new int[points.size()];
        int[] yPoints = new int[points.size()];
        
        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = points.get(i).getX();
            yPoints[i] = points.get(i).getY();
        }
        
        if (filled) {
            g.fillPolygon(xPoints, yPoints, points.size());
        } else {
            g.drawPolygon(xPoints, yPoints, points.size());
        }
        
        if (selected) {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g.drawPolygon(xPoints, yPoints, points.size());

            g.setColor(Color.BLACK);
            for (Point p : points) {
                g.fillRect(p.getX() - 3, p.getY() - 3, 6, 6);
            }
        }
    }

    /**
     * Zda bod patří do mnohoúhelníku
     * @param x X-ová souřadnice bodu
     * @param y Y-ová souřadnice bodu
     * @return Pravda, pokud bod patří do mnohoúhelníku
     */
    @Override
    public boolean contains(int x, int y) {
        if (filled) {
            java.awt.Polygon poly = createJavaPolygon();
            return poly.contains(x, y);
        } else {

            int tolerance = thickness + 2;
            
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size());
                

                double dist = distanceToLineSegment(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x, y);
                if (dist <= tolerance) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private java.awt.Polygon createJavaPolygon() {
        int[] xPoints = new int[points.size()];
        int[] yPoints = new int[points.size()];
        
        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = points.get(i).getX();
            yPoints[i] = points.get(i).getY();
        }
        
        return new java.awt.Polygon(xPoints, yPoints, points.size());
    }
    

    private double distanceToLineSegment(int x1, int y1, int x2, int y2, int px, int py) {
        float A = px - x1;
        float B = py - y1;
        float C = x2 - x1;
        float D = y2 - y1;

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = -1;
        
        if (len_sq != 0) {
            param = dot / len_sq;
        }

        float xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        float dx = px - xx;
        float dy = py - yy;
        
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Přesunutí mnohoúhelníku
     * @param deltaX Změna v X-ové souřadnici
     * @param deltaY Změna v Y-ové souřadnici
     */
    @Override
    public void move(int deltaX, int deltaY) {
        for (Point p : points) {
            p.setX(p.getX() + deltaX);
            p.setY(p.getY() + deltaY);
        }
        centerX += deltaX;
        centerY += deltaY;
    }

    /**
     * Změna velikosti mnohoúhelníku
     * @param x Nová X-ová souřadnice
     * @param y Nová Y-ová souřadnice
     */
    @Override
    public void resize(int x, int y) {

        double originalDistance = Math.sqrt(Math.pow(points.get(0).getX() - centerX, 2) + 
                                          Math.pow(points.get(0).getY() - centerY, 2));
        double newDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        
        if (originalDistance <= 0 || newDistance <= 0) return;
        
        double scaleFactor = newDistance / originalDistance;
        

        for (Point p : points) {
            int dx = p.getX() - centerX;
            int dy = p.getY() - centerY;
            
            int newX = centerX + (int)(dx * scaleFactor);
            int newY = centerY + (int)(dy * scaleFactor);
            
            p.setX(newX);
            p.setY(newY);
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
    
    public ArrayList<Point> getPoints() {
        return points;
    }
    
    public void addPoint(Point p) {
        points.add(p);
        calculateCenter();
    }
}
