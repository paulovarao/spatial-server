package com.varaodev.spatialserver.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.varaodev.spatialserver.geo.SimpleEarth;

@SpringBootTest
public class MapPointTests {
	
	List<MapPoint> samples = List.of(
			new MapPoint(0,0),
			new MapPoint(90,45),
			new MapPoint(180,90),
			new MapPoint(-180,-90),
			new MapPoint(-90,-45)
			);
	
	@Test
	void toSpacePoint() {
		SpacePoint mpx = new MapPoint(0, 0).toSpacePoint().normalizedVector().rounded(3);
		SpacePoint mpy = new MapPoint(90, 0).toSpacePoint().normalizedVector().rounded(3);
		SpacePoint mpz = new MapPoint(0, 90).toSpacePoint().normalizedVector().rounded(3);
		assertThat(mpx).isEqualTo(new SpacePoint(1, 0, 0));
		assertThat(mpy).isEqualTo(new SpacePoint(0, 1, 0));
		assertThat(mpz).isEqualTo(new SpacePoint(0, 0, 1));
	}
	
	@Test
	void pointRectangularBuffer() {
		MapPoint mp1 = new MapPoint(0, 0);
		
		double distance = 100;
		
		MapPoints points = mp1.pointRectangularBuffer(distance, distance, 45);
		List<MapPoint> list = points.getPoints();
		
		double d1 = Rounded.round(list.get(1).distanceKm(list.get(0)), 0);
		double d2 = Rounded.round(list.get(1).distanceKm(list.get(2)), 0);
		
		assertThat(d1).isEqualTo(distance);
		assertThat(d2).isEqualTo(distance);
		
		// Test exceptions
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.pointRectangularBuffer(0, distance, 0);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.pointRectangularBuffer(distance, 0, 0);
		});
	}
	
	@Test
	void lineRectangularBuffer() {
		MapPoint mp1 = new MapPoint(0, 0);
		MapPoint mp2 = new MapPoint(0, 4);
		double distance = 100;
		MapPoints points = mp1.lineRectangularBuffer(mp2, distance);
		List<MapPoint> list = points.getPoints();
		double d1 = Rounded.round(list.get(1).distanceKm(list.get(0)), 0);
		double d2 = Rounded.round(list.get(1).distanceKm(list.get(2)), 0);
		double d3 = Rounded.round(mp1.distanceKm(mp2), 0);
		
		assertThat(d1).isEqualTo(2*distance);
		assertThat(d2).isEqualTo(d3);
		
		// Test exceptions
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.lineRectangularBuffer(mp1, distance);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.lineRectangularBuffer(mp2, 0);
		});
	}
	
	@Test
	void circularBuffer() {
		double distance = 100;
		MapPoint mp1 = new MapPoint(0, 0);
		MapPoints points = mp1.circularBuffer(distance, 4);
		List<MapPoint> list = points.getPoints();

		// Test distances
		list.stream().map(mp -> Rounded.round(mp1.distanceKm(mp), 0))
			.forEach(d -> assertThat(d).isEqualTo(distance));
		
		// Test azimuths
		IntStream.range(0, list.size()).boxed().forEach(i -> {
			MapPoint mp = (MapPoint) list.get(i).rounded(3);
			double ref = i % 2 == 0 ? mp.getX() : mp.getY();
			assertThat(ref).isEqualTo(0);
		});
		
		// Test exceptions
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.circularBuffer(0, 2);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.circularBuffer(10, -2);
		});
	}
	
	@Test
	void mapRotation() {
		MapPoint mp1 = new MapPoint(0, 0);
		MapPoint mp2 = new MapPoint(90, 0);
		
		assertThat(mp1.mapRotation(mp2, 90, -1)).isEqualTo(new MapPoint(90, 90));
		
		assertThrows(IllegalArgumentException.class, () -> {
			mp1.mapRotation(new MapPoint(100,0), 90, 1);
		});
	}
	
	@Test
	void distanceKm() {
		MapPoint mp1 = new MapPoint(0, 0);
		MapPoint mp2 = new MapPoint(1, 0);
		
		double calcDist = Rounded.round(mp1.distanceKm(mp2), 4);
		double theoryDist = Rounded.round(2 * Math.PI * SimpleEarth.EQUATOR_AXIS_KM / 360, 4);
		
		assertThat(calcDist).isEqualTo(theoryDist);
		assertThat(mp1.distanceKm(mp1)).isEqualTo(0);
	}
	
	@Test
	void invertLongitude() {
		assertThat(new MapPoint(180, 10).invertLongitude()).isEqualTo(new MapPoint(-180, 10));
	}
	
	@Test
	void limitPoint() {
		MapPoint p1 = new MapPoint(-175, 40);
		MapPoint p2 = new MapPoint(177, 39);
		
		MapPoint p3 = new MapPoint(-168, 40);
		
		assertThat(p1.limitPoint(p2)).isEqualTo(new MapPoint(-180, 39.375));
		assertThrows(IllegalArgumentException.class, () -> {
			p1.limitPoint(p3);
		});
	}
	
	@Test
	void crossedMapLonLimit() {
		MapPoint p1 = new MapPoint(-175, 40);
		MapPoint p2 = new MapPoint(177, 39);
		MapPoint p3 = new MapPoint(-168, 40);
		MapPoint p4 = new MapPoint(-68, 40);
		MapPoint p5 = new MapPoint(122, 10);
		
		assertThat(p1.crossedMapLonLimit(p2)).isEqualTo(true);
		assertThat(p1.crossedMapLonLimit(p3)).isEqualTo(false);
		assertThat(p1.crossedMapLonLimit(p4)).isEqualTo(false);
		assertThat(p1.crossedMapLonLimit(p5)).isEqualTo(false);
	}
	
	@Test
	void geoNormalized() {
		samples.stream().forEach(p -> assertThat(p.geoNormalized()).isEqualTo(p));
		assertThat(new MapPoint(-270, -45).geoNormalized()).isEqualTo(new MapPoint(90, -45));
		
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPoint(-270, -145).geoNormalized();
		});
	}
	
	@Test
	void closestPoint() {
		MapPoint closest = new MapPoint(1, 2).closestPoint(samples);
		assertThat(closest).isEqualTo(samples.get(0));
	}
	
	@Test
	void interpolation() {
		List<MapPoint> list = new MapPoint(0, 0).interpolation(new MapPoint(2, 2), 3);
		assertThat(list.get(1)).isEqualTo(new MapPoint(1, 1));
		
		assertThrows(IllegalArgumentException.class, () -> {
			samples.get(0).interpolation(samples.get(0), 10);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			samples.get(0).interpolation(samples.get(2), 1);
		});
	}
	
	@Test
	void offset() {
		assertThat(samples.get(0).offset(10, 20)).isEqualTo(new MapPoint(10, 20));
	}
	
	@Test
	void toDegree() {
		assertThat(new MapPoint(Math.PI/2, Math.PI/4).toDegree()).isEqualTo(samples.get(1));
	}
	
	@Test
	void toRadian() {
		assertThat(samples.get(1).toRadian()).isEqualTo(new MapPoint(Math.PI/2, Math.PI/4));
	}

}
