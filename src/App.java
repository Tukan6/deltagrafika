import models.Line;
import models.LineCanvas;
import models.Tool;
import models.Circle;
import models.Rectangle;
import models.Square;
import models.Polygon;
import models.Shape;
import models.Point;
import rasterizers.LineCanvasRasterizer;
import rasterizers.LineRasterizerTrivial;
import rasterizers.Rasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Hlavní třída aplikace pro kreslení
 * Implementuje jednoduché kreslicí nástroje s podporou různých tvarů
 */
public class App {

    private final JPanel panel;                        // Hlavní kreslicí panel
    private final Raster raster;                       // Rastr pro vykreslování pixelů
    private MouseAdapter mouseAdapter;                 // Adaptér pro zpracování událostí myši
    private KeyAdapter keyAdapter;                     // Adaptér pro zpracování událostí klávesnice
    private Point point;                               // Aktuální bod pro kreslení
    private Point selectedPoint;                       // Vybraný bod
    private Line selectedLine;                         // Vybraná čára
    private Shape selectedShape;                       // Vybraný tvar
    private static final int SELECTION_THRESHOLD = 10; // Prahová hodnota pro výběr objektů (v pixelech)
    
    private LineCanvasRasterizer rasterizer;           // Rasterizátor pro vykreslení čar a tvarů
    private LineCanvas canvas;                         // Plátno obsahující všechny nakreslené objekty
    private boolean shiftMode = false;                 // Indikátor, zda je stisknuta klávesa Shift

    private Tool currentTool = Tool.LINE;              // Aktuálně vybraný nástroj (výchozí: čára)
    private boolean drawingPolygon = false;            // Indikátor, zda se kreslí mnohoúhelník
    private ArrayList<Point> polygonPoints;            // Body tvořící kreslený mnohoúhelník

    // Proměnné pro vytváření a manipulaci s tvary
    private Point startPoint;                          // Počáteční bod při kreslení
    private boolean dragging = false;                  // Indikátor tažení myši
    private boolean resizingShape = false;             // Indikátor změny velikosti tvaru
    private boolean movingShape = false;               // Indikátor přesouvání tvaru

    private Color currentColor = Color.RED;            // Aktuální barva (výchozí: červená)
    private int currentThickness = 1;                  // Aktuální tloušťka čáry (výchozí: 1px)
    private JPanel toolPanel;                          // Panel nástrojů
    private JButton colorButton;                       // Tlačítko pro výběr barvy
    private JPanel shapesPanel;                        // Panel pro výběr tvarů
    private JPanel toolsPanel;                         // Panel pro výběr nástrojů
    private JPanel colorPanel;                         // Panel pro výběr barev a tloušťky čar

    /**
     * Hlavní metoda aplikace
     * @param args argumenty příkazové řádky (nevyužité)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    /**
     * Vymazání plátna zadanou barvou
     * @param color barva pozadí (RGB formát)
     */
    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }    /**
     * Vykreslení obsahu rastru na obrazovku
     * @param graphics grafický kontext pro vykreslení
     */
    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }    /**
     * Spuštění aplikace - inicializace plátna a překreslení
     */
    public void start() {
        clear(0xaaaaaa);
        
        // Ujistíme se, že plátno je připraveno k vykreslení
        if (rasterizer != null && canvas != null) {
            rasterizer.rasterizeCanvas(canvas);
        }
        
        panel.repaint();
    }

    /**
     * Konstruktor aplikace - nastavení okna a inicializace komponent
     * @param width šířka kreslicího plátna
     * @param height výška kreslicího plátna
     */
    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Vytvoření rastru pro kreslení
        raster = new RasterBufferedImage(width, height);
        
        // Vytvoření panelu s vlastním vykreslováním
        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));


        createToolPanel();
        frame.add(toolPanel, BorderLayout.NORTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        rasterizer = new LineCanvasRasterizer(
                raster
        );
        canvas = new LineCanvas(new ArrayList<>(), new ArrayList<>());

        createAdapters();
        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        panel.requestFocus();
        panel.requestFocusInWindow();
    }    /**
     * Vytvoření hlavního panelu nástrojů
     * Obsahuje podpanely pro tvary, nástroje a barvy
     */
    private void createToolPanel() {
        toolPanel = new JPanel();
        toolPanel.setLayout(new BorderLayout());

        // Vytvoření panelu pro tvary
        createShapesPanel();

        // Vytvoření panelu pro kreslící nástroje
        createToolsPanel();

        // Vytvoření panelu pro barvy a tloušťku čáry
        createColorPanel();

        // Rozložení hlavního panelu nástrojů
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topToolbar.add(shapesPanel);
        topToolbar.add(toolsPanel);

        toolPanel.add(topToolbar, BorderLayout.NORTH);
        toolPanel.add(colorPanel, BorderLayout.SOUTH);
    }    /**
     * Vytvoření panelu pro výběr tvarů
     * Obsahuje tlačítka pro různé typy čar a tvarů
     */
    private void createShapesPanel() {
        shapesPanel = new JPanel();
        shapesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        shapesPanel.setBorder(BorderFactory.createTitledBorder("Tvary"));

        // Nástroje pro čáry
        JButton lineButton = new JButton("Čára");
        lineButton.addActionListener(e -> setCurrentTool(Tool.LINE));
        shapesPanel.add(lineButton);

        JButton dottedLineButton = new JButton("Přerušovaná čára");
        dottedLineButton.addActionListener(e -> setCurrentTool(Tool.DOTTED_LINE));
        shapesPanel.add(dottedLineButton);

        // Nástroje pro tvary
        JButton circleButton = new JButton("Kruh");
        circleButton.addActionListener(e -> setCurrentTool(Tool.CIRCLE));
        shapesPanel.add(circleButton);

        JButton squareButton = new JButton("Čtverec");
        squareButton.addActionListener(e -> setCurrentTool(Tool.SQUARE));
        shapesPanel.add(squareButton);

        JButton rectangleButton = new JButton("Obdélník");
        rectangleButton.addActionListener(e -> setCurrentTool(Tool.RECTANGLE));
        shapesPanel.add(rectangleButton);

        JButton polygonButton = new JButton("Mnohoúhelník");
        polygonButton.addActionListener(e -> setCurrentTool(Tool.POLYGON));
        shapesPanel.add(polygonButton);
    }    /**
     * Vytvoření panelu pro nástroje úprav
     * Obsahuje tlačítka pro výběr, mazání, výplň a vyčištění plátna
     */
    private void createToolsPanel() {
        toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Nástroje"));

        JButton selectButton = new JButton("Výběr");
        selectButton.addActionListener(e -> setCurrentTool(Tool.SELECT));
        toolsPanel.add(selectButton);

        JButton eraserButton = new JButton("Guma");
        eraserButton.addActionListener(e -> setCurrentTool(Tool.ERASER));
        toolsPanel.add(eraserButton);

        JButton fillButton = new JButton("Výplň");
        fillButton.addActionListener(e -> setCurrentTool(Tool.FILL));
        toolsPanel.add(fillButton);

        JButton clearButton = new JButton("Vyčistit plátno");
        clearButton.addActionListener(e -> {
            canvas.clear();
            raster.clear();
            panel.repaint();
        });
        toolsPanel.add(clearButton);
    }    /**
     * Vytvoření panelu pro barvy a tloušťku čáry
     * Obsahuje tlačítka pro nastavení tloušťky čáry a výběr barev
     */
    private void createColorPanel() {
        colorPanel = new JPanel();
        colorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Panel pro tloušťku čáry
        JPanel thicknessPanel = new JPanel();
        thicknessPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        thicknessPanel.setBorder(BorderFactory.createTitledBorder("Tloušťka čáry"));

        // Tlačítka pro volbu tloušťky
        for (int i = 1; i <= 5; i++) {
            final int thickness = i;
            JButton thicknessButton = new JButton(Integer.toString(i));
            thicknessButton.addActionListener(e -> setThickness(thickness));
            thicknessPanel.add(thicknessButton);
        }

        // Panel pro výběr barvy
        JPanel colorChoicePanel = new JPanel();
        colorChoicePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        colorChoicePanel.setBorder(BorderFactory.createTitledBorder("Barva"));

        // Tlačítko pro otevření dialogu výběru barvy
        colorButton = new JButton("Vybrat barvu");
        colorButton.setBackground(currentColor);
        colorButton.setForeground(getContrastColor(currentColor));
        colorButton.addActionListener(e -> chooseColor());
        colorChoicePanel.add(colorButton);        // Přednastavené barvy
        Color[] presetColors = { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
                               Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA };

        // Vytvoření tlačítek pro přednastavené barvy
        for (Color color : presetColors) {
            JButton presetButton = new JButton();
            presetButton.setBackground(color);
            presetButton.setPreferredSize(new Dimension(24, 24));
            presetButton.addActionListener(e -> {
                currentColor = color;
                colorButton.setBackground(color);
                colorButton.setForeground(getContrastColor(color));
                updateSelectedElementColor();
            });
            colorChoicePanel.add(presetButton);
        }

        // Přidání obou panelů do panelu barev
        colorPanel.add(thicknessPanel);
        colorPanel.add(colorChoicePanel);
    }    /**
     * Nastavení aktuálního nástroje a související inicializace
     * @param tool nástroj, který má být nastaven jako aktivní
     */
    private void setCurrentTool(Tool tool) {
        currentTool = tool;

        // Resetování stavu kreslení mnohoúhelníku při změně nástroje
        if (tool != Tool.POLYGON && drawingPolygon) {
            drawingPolygon = false;
            polygonPoints = null;
        }

        // Inicializace pole bodů mnohoúhelníku při výběru nástroje pro mnohoúhelník
        if (tool == Tool.POLYGON && !drawingPolygon) {
            polygonPoints = new ArrayList<>();
        }

        // Zrušení výběru při změně nástroje (kromě nástroje výběr)
        if (tool != Tool.SELECT) {
            deselectAll();
        }

        // Speciální kurzor pro gumu
        if (tool == Tool.ERASER) {
            panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            panel.setCursor(Cursor.getDefaultCursor());
        }
    }    /**
     * Zrušení výběru všech objektů
     */
    private void deselectAll() {
        selectedPoint = null;
        selectedLine = null;

        if (selectedShape != null) {
            selectedShape.setSelected(false);
            selectedShape = null;
        }

        // Zrušení výběru všech tvarů
        for (Shape shape : canvas.getShapes()) {
            shape.setSelected(false);
        }
    }    /**
     * Aktualizace barvy vybraného objektu
     * Nastaví aktuální barvu pro vybraný tvar nebo čáru
     */
    private void updateSelectedElementColor() {
        if (selectedShape != null) {
            selectedShape.setColor(currentColor);
            raster.clear();
            rasterizer.rasterizeCanvas(canvas);
            panel.repaint();
        } else if (selectedLine != null) {
            selectedLine.setColor(currentColor);
            raster.clear();
            rasterizer.rasterizeCanvas(canvas);
            panel.repaint();
        }
    }    /**
     * Nastavení tloušťky čáry
     * @param thickness nová tloušťka čáry v pixelech
     */
    private void setThickness(int thickness) {
        currentThickness = thickness;
    }

    /**
     * Otevře dialog pro výběr barvy a nastaví vybranou barvu
     * jako aktuální barvu kreslení
     */
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(null, "Vyberte barvu", currentColor);
        if (newColor != null) {
            currentColor = newColor;
            colorButton.setBackground(currentColor);
            colorButton.setForeground(getContrastColor(currentColor));
        }
    }
    /**
     * Vypočítá kontrastní barvu k zadané barvě
     * Pro tmavé barvy vrátí bílou, pro světlé černou
     * 
     * @param color vstupní barva
     * @return kontrastní barva (černá nebo bílá)
     */    private Color getContrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
    
    /**
     * Vytvoření adaptérů pro zpracování událostí myši a klávesnice
     * Implementuje celou logiku kreslení a manipulace s objekty
     */
    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX();
                    int y = e.getY();

                    switch (currentTool) {
                        case LINE:
                        case DOTTED_LINE:
                            point = new Point(x, y);
                            startPoint = point;
                            dragging = true;
                            break;
                        case CIRCLE:
                        case SQUARE:
                        case RECTANGLE:
                            startPoint = new Point(x, y);
                            dragging = true;
                            break;                        case POLYGON:
                            if (!drawingPolygon) {
                                polygonPoints = new ArrayList<>();
                                drawingPolygon = true;
                            }                            // Pokud klikneme blízko prvního bodu a máme alespoň 3 body, uzavřeme mnohoúhelník
                            if (polygonPoints.size() > 2 &&
                                distance(polygonPoints.get(0), new Point(x, y)) < SELECTION_THRESHOLD) {

                                // Uzavření mnohoúhelníku přidáním kopie prvního bodu jako posledního bodu
                                // To zajistí čisté uzavření tvaru
                                polygonPoints.add(new Point(polygonPoints.get(0).getX(), polygonPoints.get(0).getY()));

                                ArrayList<Point> finalPoints = new ArrayList<>(polygonPoints);
                                Polygon polygon = new Polygon(finalPoints, currentColor, currentThickness);
                                canvas.addShape(polygon);

                                drawingPolygon = false;
                                polygonPoints = new ArrayList<>();

                                raster.clear();
                                rasterizer.rasterizeCanvas(canvas);
                                panel.repaint();                            } else {
                                // Přidání nového bodu
                                polygonPoints.add(new Point(x, y));

                                // Vykreslení rozpracovaného mnohoúhelníku
                                raster.clear();
                                rasterizer.rasterizeCanvas(canvas);

                                // Vykreslení dočasných čar spojujících body
                                if (polygonPoints.size() > 1) {
                                    for (int i = 0; i < polygonPoints.size() - 1; i++) {
                                        Line line = new Line(polygonPoints.get(i), polygonPoints.get(i + 1), currentColor, currentThickness);
                                        rasterizer.rasterizeLine(line);
                                    }

                                    // Vykreslení náhledu čáry od posledního bodu k prvnímu bodu (pokud máme více než 2 body)
                                    if (polygonPoints.size() > 2) {
                                        Line closingLine = new Line(
                                                polygonPoints.get(polygonPoints.size() - 1),
                                                polygonPoints.get(0),
                                                currentColor,
                                                currentThickness);
                                        rasterizer.rasterizeDottedLine(closingLine);
                                    }
                                }

                                panel.repaint();
                            }
                            break;
                        case SELECT:
                            // First, check if we clicked on a shape
                            selectedShape = null;
                            for (Shape shape : canvas.getShapes()) {
                                if (shape.contains(x, y)) {
                                    // Deselect all others first
                                    deselectAll();
                                    // Select this one
                                    shape.setSelected(true);
                                    selectedShape = shape;

                                    // Check if clicked on the resize handle
                                    if (shape.isSelected()) {
                                        resizingShape = isPointOnResizeHandle(shape, x, y);
                                        if (!resizingShape) {
                                            movingShape = true;
                                        }
                                    }

                                    raster.clear();
                                    rasterizer.rasterizeCanvas(canvas);
                                    panel.repaint();
                                    break;
                                }
                            }

                            // If no shape was clicked, check for lines/points
                            if (selectedShape == null) {
                                Point clickPoint = new Point(x, y);
                                findNearestPoint(clickPoint);
                            }
                            break;
                        case ERASER:
                            // Implementation of eraser
                            eraseAtPoint(x, y);
                            break;                        case FILL:
                            // Implementation of fill tool
                            fillAtPoint(x, y);
                            break;
                    }                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // Pravé tlačítko myši dokončí mnohoúhelník v režimu kreslení mnohoúhelníku
                    if (currentTool == Tool.POLYGON && drawingPolygon && polygonPoints.size() > 2) {
                        // Dokončení mnohoúhelníku jeho uzavřením
                        // Přidání bodu, který je kopií prvního bodu, pro zajištění čistého uzavření tvaru
                        polygonPoints.add(new Point(polygonPoints.get(0).getX(), polygonPoints.get(0).getY()));

                        ArrayList<Point> finalPoints = new ArrayList<>(polygonPoints);
                        Polygon polygon = new Polygon(finalPoints, currentColor, currentThickness);
                        canvas.addShape(polygon);

                        drawingPolygon = false;
                        polygonPoints = new ArrayList<>();

                        raster.clear();
                        rasterizer.rasterizeCanvas(canvas);
                        panel.repaint();
                    }
                }
            }            /**
             * Zpracování události uvolnění tlačítka myši
             * Dokončuje kreslení objektů a přidává je na plátno
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX();
                    int y = e.getY();
                    
                    // Podle aktuálního nástroje dokončíme příslušnou operaci
                    switch (currentTool) {
                        case LINE:
                            Point point2 = new Point(x, y);
                            if (shiftMode) {
                                point2 = makeLinestraight(startPoint, point2);
                            }
                            Line line = new Line(startPoint, point2, currentColor, currentThickness);
                            canvas.add(line);
                            break;
                        case DOTTED_LINE:
                            Point dotPoint2 = new Point(x, y);
                            if (shiftMode) {
                                dotPoint2 = makeLinestraight(startPoint, dotPoint2);
                            }
                            Line dotLine = new Line(startPoint, dotPoint2, currentColor, currentThickness);
                            canvas.addDottedLine(dotLine);
                            break;
                        case CIRCLE:
                            if (dragging) {
                                int centerX = startPoint.getX();
                                int centerY = startPoint.getY();
                                int radius = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                                if (radius > 0) {
                                    Circle circle = new Circle(centerX, centerY, radius, currentColor, currentThickness);
                                    canvas.addShape(circle);
                                }
                            }
                            break;
                        case SQUARE:
                            if (dragging) {
                                int size = Math.max(Math.abs(x - startPoint.getX()), Math.abs(y - startPoint.getY()));

                                if (size > 0) {
                                    Square square = new Square(
                                            startPoint.getX(),
                                            startPoint.getY(),
                                            size,
                                            currentColor,
                                            currentThickness);
                                    canvas.addShape(square);
                                }
                            }
                            break;
                        case RECTANGLE:
                            if (dragging) {
                                int width = Math.abs(x - startPoint.getX());
                                int height = Math.abs(y - startPoint.getY());

                                if (width > 0 && height > 0) {
                                    Rectangle rectangle = new Rectangle(
                                            Math.min(startPoint.getX(), x),
                                            Math.min(startPoint.getY(), y),
                                            width,
                                            height,
                                            currentColor,
                                            currentThickness);
                                    canvas.addShape(rectangle);
                                }
                            }
                            break;
                        case POLYGON:
                            // Polygon is handled in mousePressed event
                            break;
                        case SELECT:
                            if (movingShape || resizingShape) {
                                movingShape = false;
                                resizingShape = false;
                            }
                            break;
                        case ERASER:
                            // Eraser is handled during dragging
                            break;
                        case FILL:
                            // Fill is handled in mousePressed
                            break;
                    }

                    dragging = false;

                    // Redraw everything
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedPoint = null;
                    selectedLine = null;
                }
            }            /**
             * Zpracování události tažení myši
             * Zobrazuje náhled kresleného objektu během tažení
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    // Vyčištění rastru a překreslení existujících objektů
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                      switch (currentTool) {
                        case LINE:
                            Point point2 = new Point(x, y);
                            if (shiftMode) {
                                point2 = makeLinestraight(startPoint, point2);
                            }
                            Line line = new Line(startPoint, point2, currentColor, currentThickness);
                            rasterizer.rasterizeLine(line);
                            break;
                        case DOTTED_LINE:
                            Point dotPoint2 = new Point(x, y);
                            if (shiftMode) {
                                dotPoint2 = makeLinestraight(startPoint, dotPoint2);
                            }
                            Line dotLine = new Line(startPoint, dotPoint2, currentColor, currentThickness);
                            rasterizer.rasterizeDottedLine(dotLine);
                            break;
                        case CIRCLE:
                            if (dragging) {
                                int centerX = startPoint.getX();
                                int centerY = startPoint.getY();
                                int radius = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                                if (radius > 0) {
                                    Circle circle = new Circle(centerX, centerY, radius, currentColor, currentThickness);
                                    rasterizer.rasterizeShape(circle);
                                }
                            }
                            break;
                        case SQUARE:
                            if (dragging) {
                                int size = Math.max(Math.abs(x - startPoint.getX()), Math.abs(y - startPoint.getY()));

                                if (size > 0) {
                                    Square square = new Square(
                                            startPoint.getX(),
                                            startPoint.getY(),
                                            size,
                                            currentColor,
                                            currentThickness);
                                    rasterizer.rasterizeShape(square);
                                }
                            }
                            break;
                        case RECTANGLE:
                            if (dragging) {
                                int width = Math.abs(x - startPoint.getX());
                                int height = Math.abs(y - startPoint.getY());

                                if (width > 0 && height > 0) {
                                    Rectangle rectangle = new Rectangle(
                                            Math.min(startPoint.getX(), x),
                                            Math.min(startPoint.getY(), y),
                                            width,
                                            height,
                                            currentColor,
                                            currentThickness);
                                    rasterizer.rasterizeShape(rectangle);
                                }
                            }
                            break;                        case POLYGON:
                            // Vykreslení rozpracovaného mnohoúhelníku
                            if (drawingPolygon && polygonPoints.size() > 0) {
                                // Vykreslení čar mezi existujícími body
                                for (int i = 0; i < polygonPoints.size() - 1; i++) {
                                    Line polyLine = new Line(polygonPoints.get(i), polygonPoints.get(i + 1), currentColor, currentThickness);
                                    rasterizer.rasterizeLine(polyLine);
                                }

                                // Vykreslení čáry od posledního bodu k aktuální pozici myši
                                Line currentLine = new Line(
                                        polygonPoints.get(polygonPoints.size() - 1),
                                        new Point(x, y),
                                        currentColor,
                                        currentThickness);
                                rasterizer.rasterizeLine(currentLine);

                                // Vykreslení náhledu čáry od aktuální pozice myši k prvnímu bodu
                                if (polygonPoints.size() > 2) {
                                    Line closingLine = new Line(
                                            new Point(x, y),
                                            polygonPoints.get(0),
                                            currentColor,
                                            currentThickness);
                                    rasterizer.rasterizeDottedLine(closingLine);

                                    // Zvýraznění prvního bodu, když jsme dostatečně blízko k uzavření mnohoúhelníku
                                    if (distance(polygonPoints.get(0), new Point(x, y)) < SELECTION_THRESHOLD) {
                                        // Vykreslení malého zvýrazňujícího kruhu kolem prvního bodu
                                        Circle highlightCircle = new Circle(
                                                polygonPoints.get(0).getX(),
                                                polygonPoints.get(0).getY(),
                                                SELECTION_THRESHOLD,
                                                Color.BLUE,
                                                1);
                                        rasterizer.rasterizeShape(highlightCircle);
                                    }
                                }
                            }
                            break;
                        case SELECT:
                            if (selectedShape != null) {
                                if (resizingShape) {
                                    selectedShape.resize(x, y);
                                } else if (movingShape) {
                                    int deltaX = x - startPoint.getX();
                                    int deltaY = y - startPoint.getY();
                                    selectedShape.move(deltaX, deltaY);
                                    startPoint = new Point(x, y);
                                }
                                rasterizer.rasterizeCanvas(canvas);
                            } else if (selectedPoint != null && selectedLine != null) {
                                updateSelectedPointPosition(x, y);
                                rasterizer.rasterizeCanvas(canvas);
                            }
                            break;
                        case ERASER:
                            eraseAtPoint(x, y);
                            break;
                        case FILL:
                            // Fill operation is done on mouse press, not during drag
                            break;
                    }
                    panel.repaint();
                }
            }            /**
             * Zpracování události pohybu myši
             * Používá se hlavně pro náhled při kreslení mnohoúhelníku
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                // Zpracováváme pouze pokud kreslíme mnohoúhelník
                if (currentTool == Tool.POLYGON && drawingPolygon && polygonPoints.size() > 0) {
                    int x = e.getX();
                    int y = e.getY();

                    // Překreslení všeho včetně rozpracovaného mnohoúhelníku
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);                    // Vykreslení čar mezi existujícími body
                    for (int i = 0; i < polygonPoints.size() - 1; i++) {
                        Line line = new Line(polygonPoints.get(i), polygonPoints.get(i + 1), currentColor, currentThickness);
                        rasterizer.rasterizeLine(line);
                    }

                    // Vykreslení čáry od posledního bodu k aktuální pozici myši
                    Line line = new Line(polygonPoints.get(polygonPoints.size() - 1), new Point(x, y), currentColor, currentThickness);
                    rasterizer.rasterizeLine(line);

                    // Vykreslení čáry od aktuální pozice myši k prvnímu bodu pro zobrazení úplného mnohoúhelníku
                    if (polygonPoints.size() > 2) {
                        boolean closeToStart = distance(polygonPoints.get(0), new Point(x, y)) < SELECTION_THRESHOLD;

                        Line closeLine = new Line(new Point(x, y), polygonPoints.get(0),
                                                 currentColor,
                                                 currentThickness);

                        // Použití přerušované čáry pro uzavírací linii jako indikátor náhledu
                        rasterizer.rasterizeDottedLine(closeLine);

                        // Zvýraznění prvního bodu, když jsme dostatečně blízko k uzavření mnohoúhelníku
                        if (closeToStart) {
                            // Vykreslení malého zvýrazňujícího kruhu kolem prvního bodu
                            Circle highlightCircle = new Circle(
                                    polygonPoints.get(0).getX(),
                                    polygonPoints.get(0).getY(),
                                    SELECTION_THRESHOLD,
                                    Color.BLUE,
                                    1);
                            rasterizer.rasterizeShape(highlightCircle);
                        }
                    }

                    panel.repaint();
                }
            }
        };        /**
         * Adaptér pro zpracování událostí klávesnice
         */
        keyAdapter = new KeyAdapter() {
            /**
             * Zpracování události stisknutí klávesy
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    // Aktivace režimu Shift pro zarovnávání čar
                    shiftMode = true;
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    // Klávesa C vymaže celé plátno
                    canvas.clear();
                    raster.clear();
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Klávesa ESC ukončí kreslení mnohoúhelníku nebo zruší výběr vybraných tvarů
                    if (drawingPolygon) {
                        drawingPolygon = false;
                        polygonPoints = null;
                    } else {
                        deselectAll();
                        raster.clear();
                        rasterizer.rasterizeCanvas(canvas);
                        panel.repaint();
                    }
                }
            }            /**
             * Zpracování události uvolnění klávesy
             */
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    // Deaktivace režimu Shift pro zarovnávání čar
                    shiftMode = false;
                }
            }
        };
    }    /**
     * Najde nejbližší bod k bodu kliknutí
     * Používá se pro výběr bodů čar pro úpravy
     * 
     * @param clickPoint bod, kde bylo kliknuto myší
     */
    private void findNearestPoint(Point clickPoint) {
        selectedPoint = null;
        selectedLine = null;
        double minDistance = SELECTION_THRESHOLD;

        // Procházení všech čar a hledání nejbližšího bodu
        for (Line line : canvas.getLines()) {
            double dist1 = distance(clickPoint, line.getPoint1());
            double dist2 = distance(clickPoint, line.getPoint2());

            if (dist1 < minDistance) {
                minDistance = dist1;
                selectedPoint = line.getPoint1();
                selectedLine = line;
            }
            if (dist2 < minDistance) {
                minDistance = dist2;
                selectedPoint = line.getPoint2();
                selectedLine = line;
            }
        }

        for (Line line : canvas.getDottedLines()) {
            double dist1 = distance(clickPoint, line.getPoint1());
            double dist2 = distance(clickPoint, line.getPoint2());

            if (dist1 < minDistance) {
                minDistance = dist1;
                selectedPoint = line.getPoint1();
                selectedLine = line;
            }
            if (dist2 < minDistance) {
                minDistance = dist2;
                selectedPoint = line.getPoint2();
                selectedLine = line;
            }
        }
    }    /**
     * Vypočítá euklidovskou vzdálenost mezi dvěma body
     * 
     * @param p1 první bod
     * @param p2 druhý bod
     * @return vzdálenost mezi body v pixelech
     */
    private double distance(Point p1, Point p2) {
        int dx = p1.getX() - p2.getX();
        int dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Aktualizuje pozici vybraného bodu
     * Používá se pro přesun bodu čáry při tažení myší
     * 
     * @param x nová x-ová souřadnice
     * @param y nová y-ová souřadnice
     */
    private void updateSelectedPointPosition(int x, int y) {
        if (selectedPoint != null) {
            selectedPoint.setX(x);
            selectedPoint.setY(y);
        }
    }    /**
     * Vytvoří rovnou čáru z počátečního a koncového bodu
     * Používá se při stisknuté klávese Shift pro zarovnání čáry horizontálně nebo vertikálně
     * 
     * @param start počáteční bod čáry
     * @param end koncový bod čáry
     * @return nový koncový bod pro rovnou čáru (horizontální nebo vertikální)
     */
    private Point makeLinestraight(Point start, Point end) {
        int dx = Math.abs(end.getX() - start.getX());
        int dy = Math.abs(end.getY() - start.getY());

        // Pokud je horizontální vzdálenost větší než vertikální, čára bude horizontální
        if (dx > dy) {
            return new Point(end.getX(), start.getY());
        } else {
            // Jinak bude čára vertikální
            return new Point(start.getX(), end.getY());
        }
    }

    // Helper methods for shape manipulation
    private boolean isPointOnResizeHandle(Shape shape, int x, int y) {
        // For circles, check if point is near the right resize handle
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            int handleX = circle.getCenterX() + circle.getRadius();
            int handleY = circle.getCenterY();
            return Math.abs(x - handleX) <= 5 && Math.abs(y - handleY) <= 5;
        }
        // For squares and rectangles, check if point is near the bottom-right corner
        else if (shape instanceof Square) {
            Square square = (Square) shape;
            int handleX = square.getX() + square.getSize() - 4;
            int handleY = square.getY() + square.getSize() - 4;
            return Math.abs(x - handleX) <= 5 && Math.abs(y - handleY) <= 5;
        }
        else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            int handleX = rect.getX() + rect.getWidth() - 4;
            int handleY = rect.getY() + rect.getHeight() - 4;
            return Math.abs(x - handleX) <= 5 && Math.abs(y - handleY) <= 5;
        }
        return false;
    }    /**
     * Vymaže prvky na zadaných souřadnicích
     * Používá se pro nástroj guma
     * 
     * @param x x-ová souřadnice mazání
     * @param y y-ová souřadnice mazání
     */
    private void eraseAtPoint(int x, int y) {
        // Kontrola tvarů, které mají být vymazány
        ArrayList<Shape> shapesToRemove = new ArrayList<>();
        for (Shape shape : canvas.getShapes()) {
            if (shape.contains(x, y)) {
                shapesToRemove.add(shape);
            }
        }

        // Odstranění tvarů zasažených gumou
        for (Shape shape : shapesToRemove) {
            canvas.getShapes().remove(shape);
        }        // Kontrola čar, které mají být vymazány
        ArrayList<Line> linesToRemove = new ArrayList<>();
        int eraserRadius = currentThickness * 5; // Velikost gumy závisí na aktuální tloušťce

        // Kontrola normálních čar
        for (Line line : canvas.getLines()) {
            if (isLineHitByEraser(line, x, y, eraserRadius)) {
                linesToRemove.add(line);
            }
        }

        // Kontrola přerušovaných čar
        for (Line line : canvas.getDottedLines()) {
            if (isLineHitByEraser(line, x, y, eraserRadius)) {
                linesToRemove.add(line);
            }
        }

        // Odstranění čar zasažených gumou
        canvas.getLines().removeAll(linesToRemove);
        canvas.getDottedLines().removeAll(linesToRemove);

        // Překreslení všeho
        raster.clear();
        rasterizer.rasterizeCanvas(canvas);
        panel.repaint();
    }

    private boolean isLineHitByEraser(Line line, int x, int y, int eraserRadius) {
        // Calculate distance from point to line segment
        return distanceToLineSegment(
                line.getPoint1().getX(), line.getPoint1().getY(),
                line.getPoint2().getX(), line.getPoint2().getY(),
                x, y) <= eraserRadius;
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
    }    /**
     * Vyplní tvar na zadaných souřadnicích aktuální barvou
     * Používá se pro nástroj výplň
     * 
     * @param x x-ová souřadnice pro výplň
     * @param y y-ová souřadnice pro výplň
     */
    private void fillAtPoint(int x, int y) {
        // Kontrola, zda jsme klikli do tvaru
        for (Shape shape : canvas.getShapes()) {
            if (shape.contains(x, y)) {
                // Nastavení vlastností výplně
                shape.setFilled(true);
                shape.setColor(currentColor);
                
                // Překreslení plátna
                raster.clear();
                rasterizer.rasterizeCanvas(canvas);
                panel.repaint();
                break;
            }
        }
    }
}
