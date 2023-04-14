package raven.chart.geo.utils;

import java.awt.Dimension;

public class MaxAndMin {

    public double getMin_width() {
        return min_width;
    }

    public void setMin_width(double min_width) {
        this.min_width = min_width;
    }

    public double getMin_height() {
        return min_height;
    }

    public void setMin_height(double min_height) {
        this.min_height = min_height;
    }

    public double getMax_width() {
        return max_width;
    }

    public void setMax_width(double max_width) {
        this.max_width = max_width;
    }

    public double getMax_height() {
        return max_height;
    }

    public void setMax_height(double max_height) {
        this.max_height = max_height;
    }

    public MaxAndMin(double min_width, double min_height, double max_width, double max_height) {
        this.min_width = min_width;
        this.min_height = min_height;
        this.max_width = max_width;
        this.max_height = max_height;
    }

    public MaxAndMin() {
    }

    private double min_width;
    private double min_height;
    private double max_width;
    private double max_height;

    public Dimension getTotalSize(double zoom) {
        double width = (min_width * -1 + max_width) * zoom;
        double height = (min_height * -1 + max_height) * zoom;
        return new Dimension((int) width, (int) height);
    }

    @Override
    public String toString() {
        return "min w: " + min_width + "/ min h: " + min_height + "/ max w:" + max_width + "/ max h:" + max_height;
    }
}
