package raven.chart.geo.utils;

public class ModelViewer {

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public ModelViewer(String country, String values) {
        this.country = country;
        this.values = values;
    }

    public ModelViewer() {
    }

    private String country;
    private String values;
}
