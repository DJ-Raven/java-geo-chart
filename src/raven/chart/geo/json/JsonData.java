package raven.chart.geo.json;

public class JsonData {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Features[] getFeatures() {
        return features;
    }

    public void setFeatures(Features[] features) {
        this.features = features;
    }

    private String type;
    private Features[] features;
}
