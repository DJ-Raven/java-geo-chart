package raven.chart.geo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import raven.chart.geo.utils.GeoChartValue;
import raven.chart.geo.utils.StringCalculate;
import raven.ui.utils.ShadowRenderer;

public class GeoChartDataView extends JComponent {

    public DecimalFormat getFormat() {
        return format;
    }

    public void setFormat(DecimalFormat format) {
        this.format = format;
    }

    public int getGapX() {
        return gapX;
    }

    public void setGapX(int gapX) {
        this.gapX = gapX;
    }

    public int getGapY() {
        return gapY;
    }

    public void setGapY(int gapY) {
        this.gapY = gapY;
    }

    void setModel(Map<String, GeoChartValue> model) {
        this.model = model;
        repaint();
    }
    private BufferedImage buffImage;
    private Map<String, GeoChartValue> model = new HashMap<>();
    private DecimalFormat format = new DecimalFormat("#,##.##");
    private int itemHeight = 18;
    private int gapX = 8;
    private int gapY = 5;
    private int minWidth = 0;

    public GeoChartDataView() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setForeground(new Color(80, 80, 80));
        setBackground(new Color(255, 255, 255));
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                createImage();
            }
        });
        setLayout(new LayoutManager() {
            @Override
            public void addLayoutComponent(String name, Component comp) {

            }

            @Override
            public void removeLayoutComponent(Component comp) {

            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return getLayoutSize();
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return getLayoutSize();
            }

            @Override
            public void layoutContainer(Container parent) {

            }
        });
        setOpaque(true);
    }

    private Dimension getLayoutSize() {
        Insets insets = getInsets();
        int width = insets.left + insets.right + minWidth;
        int height = insets.top + insets.bottom;
        height += (model.size() * itemHeight) + ((model.size() - 1) * gapY);
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (isOpaque()) {
            g2.setColor(getBackground());
            g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        }
        if (buffImage != null) {
            g2.drawImage(buffImage, 0, 0, null);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private void createImage() {
        int width = getWidth();
        int height = getHeight();
        if (!model.isEmpty() && width > 0 && height > 0) {
            buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Insets insets = getInsets();
            int x = insets.left;
            int y = insets.top;
            int w = getWidth() - (insets.left + insets.right);
            int h = getHeight() - (insets.top + insets.bottom);
            toListView(model).forEach(new Consumer<DataView>() {
                private int index;

                @Override
                public void accept(DataView t) {
                    drawItem(g2, t.getKey(), t.getValues(), getRectangle(index++));
                }

                private Rectangle getRectangle(int index) {
                    return new Rectangle(x, y + (index * itemHeight + getGapY() * index), w, itemHeight);
                }
            });
            g2.dispose();
        } else {
            buffImage = null;
        }
    }

    private void drawItem(Graphics2D g2, String key, GeoChartValue v, Rectangle rec) {
        int keyWidth = StringCalculate.calculateMap(model, new StringCalculate<String, GeoChartValue>(g2) {
            @Override
            protected String calculate(String t, GeoChartValue u) {
                return t;
            }
        });
        int valueWidth = StringCalculate.calculateMap(model, new StringCalculate<String, GeoChartValue>(g2) {
            @Override
            protected String calculate(String t, GeoChartValue u) {
                return getPercentage(u.getValue());
            }
        });
        keyWidth += rec.x;
        valueWidth -= rec.x;
        minWidth = keyWidth + valueWidth + gapX * 2 + 50;
        int width = (int) ((rec.width - (keyWidth + valueWidth + gapX * 2)) * getPercentageForPaint(v.getValue()));
        if (width > 0) {
            int x = keyWidth + gapX;
            int y = (int) (rec.getY() + 4);
            Shape shape = new RoundRectangle2D.Double(x, y, width, rec.getHeight() - 8, rec.getHeight() - 8, rec.getHeight() - 8);
            g2.drawImage(new ShadowRenderer(5, 0.2f, Color.GRAY).createShadow(shape), x - 3, y - 3, null);
            g2.setPaint(new GradientPaint(rec.x, rec.y, v.getColor().brighter(), rec.x, rec.y + rec.height, v.getColor()));
            g2.fill(shape);
        }
        drawKey(g2, key, rec, keyWidth);
        drawValue(g2, getPercentage(v.getValue()), rec, valueWidth);
    }

    private void drawKey(Graphics2D g2, String key, Rectangle rec, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(key, g2);
        int y = (int) ((rec.height - r2.getHeight()) / 2) + fm.getAscent();
        g2.setColor(getForeground());
        int x = (int) (maxWidth - r2.getWidth());
        g2.drawString(key, x, rec.y + y);
    }

    private void drawValue(Graphics2D g2, String values, Rectangle rec, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(values, g2);
        int y = (int) ((rec.height - r2.getHeight()) / 2) + fm.getAscent();
        g2.setColor(getForeground());
        int x = (int) (rec.getX() + rec.getWidth() - r2.getWidth());
        g2.drawString(values, x, rec.y + y);
    }

    private double[] getMinAndMaxValue() {
        double min = 0;
        double max = 0;
        for (GeoChartValue v : model.values()) {
            min = Math.min(min, v.getValue());
            max = Math.max(max, v.getValue());
        }
        return new double[]{min, max};
    }

    private String getPercentage(double value) {
        double total = getTotalValue();
        return format.format(value * 100 / total) + "%";
    }

    private float getPercentageForPaint(double value) {
        double minAndMax[] = getMinAndMaxValue();
        return (float) (value / minAndMax[1]);
    }

    private double getTotalValue() {
        double total = 0;
        for (GeoChartValue v : model.values()) {
            total += v.getValue();
        }
        return total;
    }

    private List<DataView> toListView(Map<String, GeoChartValue> map) {
        List<DataView> list = new ArrayList<>();
        map.forEach((t, u) -> {
            list.add(new DataView(t, u));
        });
        list.sort(new Comparator<DataView>() {
            @Override
            public int compare(DataView o1, DataView o2) {
                return (int) (o2.getValues().getValue() - o1.getValues().getValue());
            }
        });
        return list;
    }

    public void updateData() {
        repaint();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        createImage();
    }

    private class DataView {

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public GeoChartValue getValues() {
            return values;
        }

        public void setValues(GeoChartValue values) {
            this.values = values;
        }

        public DataView(String key, GeoChartValue values) {
            this.key = key;
            this.values = values;
        }

        public DataView() {
        }

        private String key;
        private GeoChartValue values;
    }
}
