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
![geo demo](https://user-images.githubusercontent.com/58245926/232056129-a6853668-8942-4d8f-95df-8a2f20e3d8c2.png)
