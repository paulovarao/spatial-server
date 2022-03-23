package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.varaodev.spatialserver.geo.SimpleEarth;
import com.varaodev.spatialserver.resources.deserializers.MapPointDeserializer;

@JsonDeserialize(using = MapPointDeserializer.class)
public class MapPoint extends PointModel implements WktModel<Point> {
	/*
	 * Default unit: degree
	 */
	
	private static final long serialVersionUID = 1L;
	
	public static final Double DEGREES_TO_RADIANS = Math.PI/180;
	
	public MapPoint() {
		super();
	}

	public MapPoint(Coordinate c) {
		super(c);
	}

	public MapPoint(double x, double y) {
		super(x, y);
	}
	
	public MapPoint(String wkt) {
		Point point = geometry(wkt);
		Coordinate coordinate = point.getCoordinate();
		setX(coordinate.getX());
		setY(coordinate.getY());
	}
	
	@Override
	@JsonIgnore
	public double getZ() {
		return z;
	}
	
	public String latLonValue() {
		return getY() + "," + getX();
	}
	
	public SpacePoint toSpacePoint() {
		Double r = radius(); Double th = theta(); Double rh = rho();
		Double x = r * Math.cos(th) * Math.sin(rh);
		Double y = r * Math.sin(th) * Math.sin(rh);
		Double z = r * Math.cos(rh);
		return new SpacePoint(x,y,z);
	}
	
	// considering azimuth at 0 degree, width refers to axis x and length refers to axis y 
	public MapPoints pointRectangularBuffer(double widthKm, double lengthKm, 
			double azimuthDeg) {
		
		checkValueIsGreaterThanZero(widthKm, "width");
		checkValueIsGreaterThanZero(lengthKm, "length");
		
		// compute vertical points
		MapPoints points = circularBuffer(lengthKm/2, 2);
		
		// rotate points
		List<MapPoint> rotated = points.getPoints().stream()
				.map(p -> p.mapRotation(this, azimuthDeg, -1))
				.collect(Collectors.toList());
		
		// rectangular line buffer for rotatated points
		return rotated.get(0).lineRectangularBuffer(rotated.get(1), widthKm/2);
	}
	
	public MapPoints lineRectangularBuffer(MapPoint p, double distanceKm) {
		if (equals(p))
			throw new IllegalArgumentException("Can't calculate line parameters for points "
					+ this + " and " + p + ": they must be different.");
		
		checkValueIsGreaterThanZero(distanceKm, "distance");
		
		// calculates the inclination of the line between the two points
		double inclinAngle = inclinationAngle(p);
		
		List<MapPoint> points = new ArrayList<>();
		for (MapPoint mapPoint : List.of(this, p)) {
			
			// converts distance in km to Earth angle in rad
			double angleRad = mapPoint.convertLinearToAngularDistanceInRadians(distanceKm);
			points.addAll(mapPoint.orthogonalPoints(angleRad, inclinAngle));
		}
		// reorders it to create a polygon more easily
		List<MapPoint> result = new ArrayList<>(List.of(points.get(0), points.get(1), points.get(3), 
				points.get(2), points.get(0)));
		return new MapPoints(result);
	}
	
	public MapPoints circularBuffer(double distanceKm, int numAzimuths) {
		checkValueIsGreaterThanZero(distanceKm, "distance");
		checkValueIsGreaterThanZero(numAzimuths, "azimuths");
		
		double angleRad = convertLinearToAngularDistanceInRadians(distanceKm);
		
		List<Double> angles = IntStream.range(0, numAzimuths).boxed()
				.map(i -> angleRad).collect(Collectors.toList());
		List<MapPoint> result = toSpacePoint().azimuthRingBuffer(angles).stream()
				.map(SpacePoint::toMapPoint)
				.collect(Collectors.toList());
		return new MapPoints(result);
	}
	
	public MapPoints box(MapPoint otherPoint) {
		List<MapPoint> boxPoints = List.of(this, new MapPoint(getX(), otherPoint.getY()),
				otherPoint,	new MapPoint(otherPoint.getX(), getY()), this);
		return new MapPoints(boxPoints);
	}
	
	public MapPoint mapRotation(MapPoint centroid, double angleDeg, int rotationSense) {
		if (distance(centroid) > 90)
			throw new IllegalArgumentException("Invalid centroid:"
					+ " distance between point and centroid must not exceed 90 degrees.");
		
		double angleRad = angleDeg * DEGREES_TO_RADIANS;
		
		// positive rotation sense correspond to counterclockwise rotation
		SpacePoint axis = new SpacePoint(0.0,0.0,rotationSense);
		
		// translate original point considering centroid is at (0, 0) 
		MapPoint p = offset(-centroid.x, -centroid.y);
		SpacePoint sp = new SpacePoint(p.x, p.y, 0.0);
		SpacePoint rp = sp.rotation(axis, angleRad);
		return new MapPoint(rp.x, rp.y).offset(centroid.x, centroid.y).geoNormalized();
	}
	
	public double distanceKm(MapPoint point) {
		if (equals(point)) return 0.0;
		
		// Lambert's formula for long lines method
		
		double a = SimpleEarth.EQUATOR_AXIS_KM, b = SimpleEarth.POLAR_AXIS_KM;
		
		double f = (a - b)/a;
		
		MapPoint p1rad = toRadian(), p2rad = point.toRadian();
		
		double beta1 = Math.atan( (1 - f)*Math.tan(p1rad.y) );
		double beta2 = Math.atan( (1 - f)*Math.tan(p2rad.y) );
		
		double p = (beta1 + beta2)/2;
		double q = (beta1 - beta2)/2;
		
		double dx = Math.cos(p2rad.y)*Math.cos(p2rad.x) - Math.cos(p1rad.y)*Math.cos(p1rad.x);
		double dy = Math.cos(p2rad.y)*Math.sin(p2rad.x) - Math.cos(p1rad.y)*Math.sin(p1rad.x);
		double dz = Math.sin(p2rad.y) - Math.sin(p1rad.y);
		
		double c = Math.sqrt(dx*dx + dy*dy + dz*dz);
		double sigma = 2*Math.asin(c/2);
		
		double x = (sigma - Math.sin(sigma))*Math.pow(Math.sin(p), 2)*Math.pow(Math.cos(q), 2)
				/Math.pow(Math.cos(sigma/2), 2);
		
		double y = (sigma + Math.sin(sigma))*Math.pow(Math.cos(p), 2)*Math.pow(Math.sin(q), 2)
				/Math.pow(Math.sin(sigma/2), 2);
		
		return a * (sigma - (x + y)*f/2);
	}

	public MapPoint invertLongitude() {
		return new MapPoint(-getX(), getY());
	}
	
	public MapPoint limitPoint(MapPoint p) {
		if (!crossedMapLonLimit(p))
			throw new IllegalArgumentException("Can't calculate limit point:"
					+ " points did not cross the limit 180/-180.");
		
		// calculates point at 180 degree meridian linearly
		Double lon = getX() < 0 ? -180.0 : 180.0;
		
		// calculate line equation y = a.x + b parameters a and b
		Double[] line = lineParameters(p);
		
		// calculates latitude using line equation
		Double lat = line[0]*lon + line[1];
		return new MapPoint(lon, lat);
	}
	
	public boolean crossedMapLonLimit(MapPoint p) {
		// checks if longitudes of two points are close to the 180 meridian in opposite sides
		double lonDif = Math.abs( getX() - p.getX() );
		return lonDif > 180*5/3;
	}
	
	public MapPoint geoNormalized() {
		if (Math.abs(getY()) > 90)
			throw new IllegalArgumentException("Invalid latitude value: " + getY()
			 + ". Its maximum absolute value must be 90 degrees.");
		
		double halfTurns = Math.ceil(Math.abs(x/180));
		double xOffset = Math.floor(halfTurns/2)*360;
		xOffset = x < 0 ? -xOffset : xOffset;
		return offset(-xOffset, 0);
	}
	
	public MapPoint closestPoint(List<MapPoint> points) {
		List<Double> distances = points.stream().map(p -> distance(p))
				.collect(Collectors.toList());
		return points.get(distances.indexOf(Collections.min(distances)));
	}
	
	public List<MapPoint> interpolation(MapPoint p, int numPoints) {
		if (numPoints < 2) 
			throw new IllegalArgumentException("Invalid number of points for interpolation: "
					+ numPoints + ". Must be at least 2.");
		
		int parts = numPoints - 1;
		double dx = (p.x - getX())/parts;
		double dy = (p.y - getY())/parts;
		boolean invertedAxis = dx == 0;
		Double[] line = lineParameters(p);
		List<MapPoint> list = new ArrayList<>(List.of(this));
		for (int i = 1; i < parts; i++) {
			double x = invertedAxis ? getX() : getX() + i*dx;
			double y = invertedAxis ? getY() + i*dy : line[0]*x + line[1];
			list.add( new MapPoint(x,y) );
		}
		list.add(p);
		return list;
	}
	
	public double inclinationAngle(MapPoint point) {
		double dX = getX() - point.x;
		double dY = getY() - point.y;
		
		// calculates the inclination of the line between the two points
		return dX == 0 ? Math.PI/2 : Math.atan(dY/dX);
	}
	
	public List<MapPoint> orthogonalPoints(double distanceAngleRad, double inclinationAngleRad) {
		SpacePoint sp = toSpacePoint();
		LinkedHashMap<Double, Double> azimuthMap = new LinkedHashMap<>();
		
		// calculates the orthogonal azimuths
		for (int i = 0; i < 2; i++) azimuthMap.put(i*Math.PI - inclinationAngleRad, distanceAngleRad);
		
		return sp.buffer(azimuthMap).stream().map(SpacePoint::toMapPoint).collect(Collectors.toList());
	}
	
	public MapPoint offset(double xo, double yo) {
		return new MapPoint(getX() + xo, getY() + yo);
	}
	
	public MapPoint toDegree() {
		return new MapPoint(getX()/DEGREES_TO_RADIANS, getY()/DEGREES_TO_RADIANS);
	}
	
	public MapPoint toRadian() {
		return new MapPoint(getX()*DEGREES_TO_RADIANS, getY()*DEGREES_TO_RADIANS);
	}

	@Override
	public Point wktGeometry() {
		return factory().createPoint(this);
	}

	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_POINT;
	}
	
	// Angular distance to linear distance in km
	public double convertLinearToAngularDistanceInDegrees(double linearDistance) {
		double factor = radius() * Math.PI / 180;
		return linearDistance / factor;
	}
	
	public Double radius() {
		MapPoint p = toRadian();
		
		// uses ellipse equation
		Double a = SimpleEarth.EQUATOR_AXIS_KM; Double b = SimpleEarth.POLAR_AXIS_KM;
		Double asin = a*a*Math.pow(Math.sin(p.getY()), 2);
		Double bcos = b*b*Math.pow(Math.cos(p.getY()), 2);
		return a * b / Math.sqrt( asin + bcos );
	}
	
	// private methods
	private double convertLinearToAngularDistanceInRadians(double linearDistance) {
		return convertLinearToAngularDistanceInDegrees(linearDistance) * DEGREES_TO_RADIANS;
	}
	
	private Double theta() {
		MapPoint p = toRadian();
		return p.getX();
	}
	
	private Double rho() {
		MapPoint p = toRadian();
		return Math.PI/2 - p.getY();
	}
	
	private Double[] lineParameters(MapPoint p) {
		if (this.equals(p))
			throw new IllegalArgumentException("Can't calculate line parameters for "
					+ "two identical points");
		
		// calculates latitude variation
		Double dY = p.y - getY();
		
		// factor is used to normalize longitude variation (case crossed lon limit)
		Double factor = getX() >= 0 ? 360.0 : -360.0;
		
		// calculates longitude variation
		Double dX = p.x - getX();
		dX = Math.abs(dX) > 180 ? dX + factor : dX;
		
		// calculates tangent of line inclination 
		Double a = dY/dX;
		
		// calculates line constant value
		Double b = getY() - a*getX();
		return new Double[]{a, b};
	}
	
	private void checkValueIsGreaterThanZero(double value, String valueName) {
		if (value <= 0)
			throw new IllegalArgumentException("Invalid " + valueName + ":"
					+ " must be greater than zero.");
	}

}
