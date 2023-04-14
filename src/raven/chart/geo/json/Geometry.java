package raven.chart.geo.json;

public class Geometry {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Object[][][] coordinates) {
        this.coordinates = coordinates;
    }

    public Geometry(String type, Object[][][] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public Geometry() {
    }

    private String type;
    private Object[][][] coordinates;
}
