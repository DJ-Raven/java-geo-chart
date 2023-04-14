package raven.chart.geo.json;

public class Properties {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Properties(String name, String continent) {
        this.name = name;
        this.continent = continent;
    }

    public Properties() {
    }

    private String name;
    private String continent;
}
