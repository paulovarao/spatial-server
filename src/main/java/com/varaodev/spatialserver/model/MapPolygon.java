package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.varaodev.spatialserver.serializers.ObjectSerializer;

public class MapPolygon implements WktModel<Polygon>, ObjectSerializer, Triangularizable {

	private MapPoints shell;
	private List<MapPoints> holes;

	public MapPolygon(MapPoints shell, List<MapPoints> holes) {
		validateMapPointsSize(shell, holes);
		this.shell = shell;
		this.holes = holes;
	}

	public MapPolygon(String wkt) {
		Polygon polygon = geometry(wkt);
		
		LinearRing shellRing = polygon.getExteriorRing();
		MapPoints shell = fromLinearRing(shellRing);
		
		List<MapPoints> holes = IntStream.range(0, polygon.getNumInteriorRing()).boxed()
				.map(i -> fromLinearRing(polygon.getInteriorRingN(i)))
				.collect(Collectors.toList());
		
		validateMapPointsSize(shell, holes);
		this.shell = shell;
		this.holes = holes; 
	}

	@Override
	public MapPoints getShell() {
		return shell;
	}

	@Override
	public List<MapPoints> getHoles() {
		return holes;
	}
	
	public MapPolygon simple() {
		if (wktGeometry().isSimple()) {
			return this;
		} else {
			MapPoints simpShell = new MapPoints(getShell().simplified());
			List<MapPoints> simpHoles = getHoles().stream().map(h -> new MapPoints(h.simplified()))
					.collect(Collectors.toList());
			return new MapPolygon(simpShell, simpHoles);				
		}
	}

	@Override
	public Polygon wktGeometry() {
		LinearRing sh = shell.getLinearRing();
		if (holes == null || holes.isEmpty()) return factory().createPolygon(sh);
		LinearRing[] hs = holes.stream().map(MapPoints::getLinearRing).toArray(LinearRing[]::new);
		return factory().createPolygon(sh, hs);
	}

	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_POLYGON;
	}
	
	@Override
	public String toString() {
		return wktGeometry().toString();
	}
	
	private void validateMapPointsSize(MapPoints shell, List<MapPoints> holes) {
		if(shell == null)
			throw new NullPointerException("Failed to instantiate MapPolygon:"
					+ " shell MapPoints can't be null.");
		
		List<MapPoints> list = new ArrayList<>(List.of(shell));
		if (holes != null) list.addAll(holes);
		for (MapPoints mp : list) {
			if (mp == null || mp.getPoints() == null)
				throw new IllegalArgumentException("Failed to instantiate MapPolygon:"
						+ " a null element was found.");
			
			if (mp.getPoints().size() < 3)
				throw new IllegalArgumentException("Failed to instantiate MapPolygon:"
						+ " invalid MapPoints size (less than 3) was found.");
		}
	}
	
	private MapPoints fromLinearRing(LinearRing ring) {
		List<MapPoint> points = Arrays.asList(ring.getCoordinates()).stream()
				.map(c -> new MapPoint(c)).collect(Collectors.toList());
		return new MapPoints(points);
	}
	
}
