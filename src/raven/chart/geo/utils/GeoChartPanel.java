package raven.chart.geo.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import raven.chart.geo.GeoChart;

public class GeoChartPanel extends JComponent {

    public boolean isHasData() {
        return shape != null;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getMin_zoom() {
        return min_zoom;
    }

    public void setMin_zoom(double min_zoom) {
        this.min_zoom = min_zoom;
        if (zoom < min_zoom) {
            double previousZoom = zoom;
            zoom = min_zoom;
            zoom(previousZoom, new Point(component.getWidth() / 2, component.getHeight() / 2));
        }
    }

    public double getMax_zoom() {
        return max_zoom;
    }

    public void setMax_zoom(double max_zoom) {
        this.max_zoom = max_zoom;
        if (zoom > max_zoom) {
            double previousZoom = zoom;
            zoom = max_zoom;
            zoom(previousZoom, new Point(component.getWidth() / 2, component.getHeight() / 2));
        }
    }

    private final GeoChart component;
    private HashMap<String, List<List<Coordinates>>> data;
    private HashMap<String, Shape> shape;
    private MaxAndMin maxAndMin;
    private double zoom = 2.5;
    private double min_zoom = 1;
    private double max_zoom = 300;
    private Shape shape_over;
    private Point mouse_location;
    private final PopupLabel popupLabel = new PopupLabel();

    public GeoChartPanel(GeoChart component) {
        this.component = component;
    }

    public void init(List<GeoData.Regions> geoRegions, GeoData.Resolution resolution) {
        data = GeoData.getInstance().getCountry(geoRegions, resolution);
        maxAndMin = getMaxAndMin(data);
        shape = new HashMap<>();
        initShape();
        repaint();
    }

    private void initShape() {
        shape.clear();
        data.forEach((t, u) -> {
            shape.put(t, toShap(u, 0));
        });
        setPreferredSize(maxAndMin.getTotalSize(zoom));
        revalidate();
    }

    public void initMouse() {
        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        MouseAdapter mouseEvent = new MouseAdapter() {
            private Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (shape != null) {
                    boolean over = false;
                    Dimension size = maxAndMin.getTotalSize(zoom);
                    double centerX = (getWidth() - size.getWidth()) / 2;
                    double centerY = (getHeight() - size.getHeight()) / 2;
                    for (Map.Entry<String, Shape> s : shape.entrySet()) {
                        if (s.getValue().contains(e.getPoint().getX() - centerX, e.getPoint().getY() - centerY)) {
                            over = true;
                            mouse_location = e.getPoint();
                            if (s.getValue() != shape_over) {
                                shape_over = s.getValue();
                                repaint();
                                break;
                            }
                        }
                    }
                    if (!over) {
                        if (shape_over != null) {
                            shape_over = null;
                            repaint();
                        }
                    } else {
                        if (popupLabel.isRenderAble()) {
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (shape != null) {
                    if (origin != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();
                        Rectangle view = viewPort.getViewRect();
                        view.x += deltaX;
                        view.y += deltaY;
                        GeoChartPanel.this.scrollRectToVisible(view);
                    }
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (shape != null) {
                    double previousZoom = zoom;
                    double zoomFactor = -0.1 * e.getPreciseWheelRotation() * zoom;
                    zoom = Math.abs(zoom + zoomFactor);
                    if (e.getWheelRotation() < 0) {
                        zoom += 0.5f;
                    } else {
                        zoom -= 0.5f;
                    }
                    if (zoom < min_zoom) {
                        zoom = min_zoom;
                    } else if (zoom > max_zoom) {
                        zoom = max_zoom;
                    }
                    zoom(previousZoom, e.getPoint());
                }
            }
        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);
        addMouseWheelListener(mouseEvent);
    }

    private void zoom(double previousZoom, Point point) {
        initShape();
        repaint();
        followMouseOrCenter(point, previousZoom);
    }

    public void zoom(double zoom) {
        double previousZoom = this.zoom;
        if (zoom < min_zoom) {
            this.zoom = min_zoom;
        } else if (zoom > max_zoom) {
            this.zoom = max_zoom;
        } else {
            this.zoom = zoom;
        }
        zoom(previousZoom, new Point(getWidth() / 2, getHeight() / 2));
    }

    private void followMouseOrCenter(Point2D point, double previousZoom) {
        Rectangle size = getBounds();
        Rectangle visibleRect = getVisibleRect();
        double scrollX = size.getCenterX();
        double scrollY = size.getCenterY();
        if (point != null) {
            scrollX = point.getX() / previousZoom * zoom - (point.getX() - visibleRect.getX());
            scrollY = point.getY() / previousZoom * zoom - (point.getY() - visibleRect.getY());
        }
        visibleRect.setRect(scrollX, scrollY, visibleRect.getWidth(), visibleRect.getHeight());
        scrollRectToVisible(visibleRect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (shape != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = maxAndMin.getTotalSize(zoom);
            int centerX = (int) (getWidth() - size.getWidth()) / 2;
            int centerY = (int) (getHeight() - size.getHeight()) / 2;
            g2.translate(centerX, centerY);
            popupLabel.start();
            shape.forEach((k, v) -> {
                drawCountry(g2, k, v);
            });
            popupLabel.clearAble();
            drawPopup(g2, centerX, centerY);
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private void drawCountry(Graphics2D g2, String country, Shape shap) {
        boolean hover = shap == shape_over;
        if (checkModel(country)) {
            g2.setColor(getColorOf(country));
            g2.fill(shap);
        } else {
            if (hover) {
                g2.setColor(component.getHoverMapColor());
            } else {
                g2.setColor(component.getMapColor());
            }
            g2.fill(shap);
        }
        if (hover) {
            getViewer(g2, country);
        }
    }

    private void drawPopup(Graphics2D g2, int centerX, int centerY) {
        if (mouse_location != null) {
            int x = mouse_location.x - centerX;
            int y = mouse_location.y - centerY;
            popupLabel.render(g2, x, y);
        }
    }

    private void getViewer(Graphics2D g2, String country) {
        if (popupLabel.isChanged(country)) {
            if (checkModel(country)) {
                double values = component.getModel().get(country).getValue();
                ModelViewer viewer = new ModelViewer(country, component.getFormat().format(values));
                popupLabel.update(g2, viewer, component);
            }
        } else {
            popupLabel.update();
        }
    }

    private boolean checkModel(String country) {
        return component.getModel().containsKey(country);
    }

    private Color getColorOf(String country) {
        return component.getModel().get(country).getColor();
    }

    private Shape toShap(List<List<Coordinates>> data, float add) {
        double size = zoom + add;
        double minHeight = maxAndMin.getMin_height() * -1;
        double minWidth = maxAndMin.getMin_width() * -1;
        double totalHeight = minHeight + maxAndMin.getMax_height();
        Path2D p2 = new Path2D.Double();
        for (List<Coordinates> d : data) {
            boolean move = true;
            for (Coordinates c : d) {
                double y = (c.getY() + minHeight);
                double x = (c.getX() + minWidth) * size;
                y = totalHeight - y;
                y *= size;
                if (move) {
                    p2.moveTo(x, y);
                    move = false;
                } else {
                    p2.lineTo(x, y);
                }
            }
        }
        return p2;
    }

    private MaxAndMin getMaxAndMin(HashMap<String, List<List<Coordinates>>> data) {
        double min_width = 0;
        double min_height = 0;
        double max_width = 0;
        double max_height = 0;
        boolean init = true;
        for (List<List<Coordinates>> list : data.values()) {
            for (List<Coordinates> d : list) {
                for (Coordinates c : d) {
                    if (init) {
                        min_width = c.getX();
                        min_height = c.getY();
                        max_width = c.getX();
                        max_height = c.getY();
                        init = false;
                    } else {
                        min_width = Math.min(min_width, c.getX());
                        min_height = Math.min(min_height, c.getY());
                        max_width = Math.max(max_width, c.getX());
                        max_height = Math.max(max_height, c.getY());
                    }
                }
            }
        }
        return new MaxAndMin(min_width, min_height, max_width, max_height);
    }
}
