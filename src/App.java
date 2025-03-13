import models.Line;
import models.LineCanvas;
import models.Point;
import rasterizers.LineCanvasRasterizer;
import rasterizers.LineRasterizerTrivial;
import rasterizers.Rasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;
    private Point point;
    private Point selectedPoint;
    private Line selectedLine;
    private static final int SELECTION_THRESHOLD = 10; // pixels
    private LineCanvasRasterizer rasterizer;
    private LineCanvas canvas;
    private boolean controlMode = false;
    private boolean shiftMode = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

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
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    point = new Point(e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // Right click - try to select nearest point
                    Point clickPoint = new Point(e.getX(), e.getY());
                    findNearestPoint(clickPoint);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Point point2 = new Point(e.getX(), e.getY());
                    if (shiftMode) {
                        point2 = makeLinestraight(point, point2);
                    }
                    Line line = new Line(point, point2, Color.red);

                    raster.clear();

                    if (controlMode) {
                        canvas.addDottedLine(line);
                    } else {
                        canvas.add(line);
                    }

                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedPoint = null;
                    selectedLine = null;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    // Left button drag
                    Point point2 = new Point(e.getX(), e.getY());
                    if (shiftMode) {
                        point2 = makeLinestraight(point, point2);
                    }
                    Line line = new Line(point, point2, Color.red);

                    raster.clear();

                    rasterizer.rasterizeCanvas(canvas);

                    if (controlMode) {
                        rasterizer.rasterizeDottedLine(line);
                    } else {
                        rasterizer.rasterizeLine(line);
                    }

                    panel.repaint();
                } else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
                    // Right button drag - move selected point
                    if (selectedPoint != null && selectedLine != null) {
                        updateSelectedPointPosition(e.getX(), e.getY());
                        raster.clear();
                        rasterizer.rasterizeCanvas(canvas);
                        panel.repaint();
                    }
                }
            }
        };

        keyAdapter = new KeyAdapter() {
            // TODO Klácesa C bude mazat vše uložené

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    controlMode = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    controlMode = false;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = false;
                }
            }
        };
    }

    private void findNearestPoint(Point clickPoint) {
        selectedPoint = null;
        selectedLine = null;
        double minDistance = SELECTION_THRESHOLD;

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
    }

    private double distance(Point p1, Point p2) {
        int dx = p1.getX() - p2.getX();
        int dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void updateSelectedPointPosition(int x, int y) {
        if (selectedPoint != null) {
            selectedPoint.setX(x);
            selectedPoint.setY(y);
        }
    }

    private Point makeLinestraight(Point start, Point end) {
        int dx = Math.abs(end.getX() - start.getX());
        int dy = Math.abs(end.getY() - start.getY());
        
        if (dx > dy) {
            // Make horizontal line
            return new Point(end.getX(), start.getY());
        } else {
            // Make vertical line
            return new Point(end.getX(), end.getY());
        }
    }
}
