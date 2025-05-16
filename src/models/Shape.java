package models;

import java.awt.*;

/**
 * Rozhraní pro všechny tvary v aplikaci
 */
public interface Shape {
    /**
     * Vykreslí tvar do grafického kontextu
     * @param g Grafický kontext
     */
    void draw(Graphics2D g);
    
    /**
     * Zkontroluje, zda bod [x,y] leží uvnitř nebo na hranici tvaru
     * @param x X-ová souřadnice bodu
     * @param y Y-ová souřadnice bodu
     * @return true pokud bod leží uvnitř nebo na hranici tvaru
     */
    boolean contains(int x, int y);
    
    /**
     * Posune tvar o zadaný vektor
     * @param deltaX Posun v ose X
     * @param deltaY Posun v ose Y
     */
    void move(int deltaX, int deltaY);
    
    /**
     * Změní velikost tvaru
     * @param x X-ová souřadnice bodu pro změnu velikosti
     * @param y Y-ová souřadnice bodu pro změnu velikosti
     */
    void resize(int x, int y);
    
    /**
     * Nastaví příznak výběru tvaru
     * @param selected True pokud je tvar vybrán
     */
    void setSelected(boolean selected);
    
    /**
     * Zjistí, zda je tvar vybrán
     * @return True pokud je tvar vybrán
     */
    boolean isSelected();
    
    /**
     * Nastaví barvu tvaru
     * @param color Nová barva
     */
    void setColor(Color color);
    
    /**
     * Zjistí aktuální barvu tvaru
     * @return Aktuální barva
     */
    Color getColor();
    
    /**
     * Nastaví, zda má být tvar vyplněn
     * @param filled True pokud má být tvar vyplněn
     */
    void setFilled(boolean filled);
    
    /**
     * Zjistí, zda je tvar vyplněn
     * @return True pokud je tvar vyplněn
     */
    boolean isFilled();
}
