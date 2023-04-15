# java-geo-chart

Geo chart custom using java swing

### Sample code
``` java
// Create geo chart
GeoChart geoChart = new GeoChart();

// Init geo data with low resolution
geoChart.load(GeoData.Resolution.LOW);

// Set default zoom
geoChart.getGeoChart().zoom(2);

// Put data to geo chart (name, value, color)
geoChart.putData("Name 1", 5000, new Color(89, 152, 97));
geoChart.putData("Name 2", 3500, new Color(200, 149, 61));

// Put data with random color (name, value)
geoChart.putData("Name 3", 2500);
```
### Create GeoChart Data View
``` java
// Create
GeoChartDataView geoChartDataView = new GeoChartDataView();

// Set it to geochart
geoChart.setGeoChartDataView(geoChartDataView);
```
### Library Use
- gson-2.9.0.jar
- GeoJson : https://geojson-maps.ash.ms
### Screenshot
![2023-04-16_003635](https://user-images.githubusercontent.com/58245926/232244719-84cca654-ab4e-4e1c-855b-3a7611f803dc.png)
