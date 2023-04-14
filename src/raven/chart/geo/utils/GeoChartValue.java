package raven.chart.geo.utils;

import java.awt.Color;

public class GeoChartValue {

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public GeoChartValue(double value, Color color) {
        this.value = value;
        this.color = color;
    }

    public GeoChartValue() {
    }

    private double value;
    private Color color;
}
