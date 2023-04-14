package raven.chart.geo.utils;

public class ModelFontSize {

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAscent() {
        return ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public ModelFontSize(int width, int height, int ascent) {
        this.width = width;
        this.height = height;
        this.ascent = ascent;
    }

    public ModelFontSize() {
    }

    private int width;
    private int height;
    private int ascent;
}
