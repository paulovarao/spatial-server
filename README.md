# (Geo) Spatial Data processing web microsservice

## TO DO

- zoom to result after update
- remove centroid style of satellite FOR
- limit satellite orbit altitude
- test 90 deg inclination orbit
- load custom TLE file
- Open topo data / open elevation
- display errors for satellite

- filter polygon access
- select satellite using name
- limit elements in input arrays
- all available endpoints

## Languages

* Java
* JavaScript
* HTML
* CSS

## Frameworks & Libraries

### Java

* Spring Boot
* JTS

### Javascript

* Satellite (shashwatak)
* OpenLayers

## APIs

* CelesTrak
* Open Topo data
* Open Elevation

## Services

### Geometries

#### 1) Points

* Distance from Earth center (ellipsoid radius)
* Elevation
* Rotation around another point
* Distance between points
* Linear interpolation
* Circular buffer
* Rectangular buffer

#### 2) Lines

* Buffer

#### 3) Polygons

* Area
* Simplification of non simple polygons
* Union of polygons
* Intersection between polygons
* Difference between polygons
* Mosaic

### Satellite

* Field of View
* Access to polygons

## Instructions

1) Edit **application.properties** file located in **src/main/resources** if you need to change the port (default 8080)

## Web page

* http://localhost:8080/map

## Endpoints

All available endpoints are listed on address  
http://localhost:8080/

### Example


## References
