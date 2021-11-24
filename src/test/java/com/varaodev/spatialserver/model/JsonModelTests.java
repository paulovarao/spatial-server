package com.varaodev.spatialserver.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JsonModelTests {
	
	@Test
	void ignoreUnknown() throws Exception {
		MapPoint point = new MapPoint(3, 4);
		SpacePoint sp = new SpacePoint(0, 1, 2);
		System.out.println(point);
		System.out.println(sp);
		MapPoint mp = JsonModel.getModel(sp.toString(), MapPoint.class);
		assertThat(mp.toJson()).isEqualTo(new MapPoint(0, 1).toJson());
	}

}
