package raven.chart.geo;

import raven.chart.geo.utils.GeoData;
import raven.chart.geo.utils.GeoChartPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import raven.chart.geo.utils.GeoChartValue;

public class GeoChart extends JComponent {

    private final GeoChartPanel geoChartPanel;
    private final JScrollPane scroll;
    private final Map<String, GeoChartValue> model = new HashMap<>();
    private final List<GeoData.Regions> geoRegions = new ArrayList<>();
    private GeoChartDataView geoChartDataView;
    private Color gradientColor = null;
    private Color mapColor = new Color(240, 240, 240);
    private Color hoverMapColor = new Color(230, 230, 230);
    private DecimalFormat format = new DecimalFormat("View : #,##0.##");

    public GeoChart() {
        scroll = new JScrollPane();
        geoChartPanel = new GeoChartPanel(this);
        scroll.setViewportView(geoChartPanel);
        scroll.getViewport().setOpaque(false);
        scroll.setViewportBorder(null);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(scroll);
        setPreferredSize(new Dimension(250, 200));
        setOpaque(true);
        setBackground(new Color(178, 225, 255));
        setForeground(new Color(80, 80, 80));
        init();
    }

    private void init() {
        geoChartPanel.initMouse();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (gradientColor != null) {
                g2.setPaint(getGradient());
            } else {
                g2.setColor(getBackground());
            }
            g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private RadialGradientPaint getGradient() {
        int width = getWidth();
        int height = getHeight();
        Point2D center = new Point2D.Double(width / 2, height / 2);
        float radius = (float) Math.max(width, height) / 2;
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {getBackground(), gradientColor};
        return new RadialGradientPaint(center, radius, dist, colors);
    }

    public void setRegions(GeoData.Regions... regions) {
        geoRegions.clear();
        for (GeoData.Regions r : regions) {
            geoRegions.add(r);
        }
    }

    public void putData(String country, double values, Color color) {
        model.put(country, new GeoChartValue(values, color));
        SwingUtilities.invokeLater(() -> {
            repaint();
            if (geoChartDataView != null) {
                geoChartDataView.updateData();
            }
        });
    }

    public void putData(String country, double values) {
        putData(country, values, randomColor());
    }

    public void clearData() {
        model.clear();
        repaint();
    }

    public void clearRegions() {
        geoRegions.clear();
    }

    public void load(GeoData.Resolution resolution) {
        geoChartPanel.init(geoRegions, resolution);
    }

    public void load() {
        geoChartPanel.init(geoRegions, GeoData.Resolution.MEDIUM);
    }

    public GeoChartPanel getGeoChart() {
        return geoChartPanel;
    }

    public Color getGradientColor() {
        return gradientColor;
    }

    public void setGradientColor(Color gradientColor) {
        this.gradientColor = gradientColor;
        repaint();
    }

    public Color getMapColor() {
        return mapColor;
    }

    public void setMapColor(Color mapColor) {
        this.mapColor = mapColor;
        repaint();
    }

    public Color getHoverMapColor() {
        return hoverMapColor;
    }

    public void setHoverMapColor(Color hoverMapColor) {
        this.hoverMapColor = hoverMapColor;
        repaint();
    }

    public Map<String, GeoChartValue> getModel() {
        return model;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = new DecimalFormat(format);
    }

    private Color randomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        return randomColor;
    }

    public GeoChartDataView getGeoChartDataView() {
        return geoChartDataView;
    }

    public void setGeoChartDataView(GeoChartDataView geoChartDataView) {
        if (this.geoChartDataView != null) {
            this.geoChartDataView.setModel(null);
        }
        this.geoChartDataView = geoChartDataView;
        geoChartDataView.setModel(model);
    }
}
