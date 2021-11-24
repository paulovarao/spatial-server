package com.varaodev.spatialserver.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.SpacePoint;

@SpringBootTest
public class GenericTest {
	
	SpacePoint x = new SpacePoint(1, 0, 0);
	SpacePoint y = new SpacePoint(0, 1, 0);
	SpacePoint z = new SpacePoint(0, 0, 1);
	
	MapPoint p1 = new MapPoint(0, 0);
	MapPoint p2 = new MapPoint(2, 2);
	
	@Test
	void generic() {
		
	}

}
