/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.geometry.hull;

import java.util.Random;

import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.RobustGeometryTest;
import org.dyn4j.geometry.DynVector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link DivideAndConquer} algorithm.
 * @author William Bittle
 * @version 4.0.1
 * @since 3.4.0
 */
public class DivideAndConquerTest {
	/**
	 * Returns a random point cloud
	 * @param seed the random seed
	 * @return Vector2[]
	 */
	private DynVector2[] generate(long seed) {
		Random random = new Random(seed);
		// randomize the size from 4 to 100
		int size = (int) Math.floor(random.nextDouble() * 96.0 + 4.0);
		// create the cloud container
		DynVector2[] cloud = new DynVector2[size];
		// fill the cloud with a random distribution of points
		for (int i = 0; i < size; i++) {
			cloud[i] = new DynVector2(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0);
		}
		return cloud;
	}
	
	/**
	 * Test a point cloud with the {@link DivideAndConquer} hull algorithm.
	 * The result must be a valid {@link Polygon} and contain all the original
	 * points.
	 * 
	 * @param cloud The point cloud
	 */
	private void testCloud(DynVector2[] cloud) {
		DivideAndConquer dac = new DivideAndConquer();
		DynVector2[] hull = dac.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			DynVector2 p = cloud[i];
			if (!RobustGeometryTest.robustPolygonContains(poly.getVertices(), p)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Divide and Conquer class against the random
	 * point cloud.
	 */
	@Test
	public void dacRandom1() {
		DynVector2[] cloud = this.generate(0);
		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against the random
	 * point cloud.
	 */
	@Test
	public void dacRandom2() {
		DynVector2[] cloud = this.generate(5);
		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac22() {
		DynVector2[] cloud = new DynVector2[] {
				new DynVector2(-0.9726201659613243, -0.061797000490076304),
				new DynVector2(-0.49116488082909115, 0.7678687827639961),
				new DynVector2(-0.48739204883444187, -0.9956524492331893),
				new DynVector2(-0.4537370168301149, -0.6616125022480972),
				new DynVector2(-0.34908102882750347, -0.7747234303505055),
				new DynVector2(-0.33139487141988466, 0.03531605400219706),
				new DynVector2(-0.280521830078756, -0.8019885163611113),
				new DynVector2(-0.18129485409799484, -0.20967044425834414),
				new DynVector2(0.06567685427976788, 0.08518030687971767),
				new DynVector2(0.18792402577067557, -0.47162922175654987),
				new DynVector2(0.4819840632260479, 0.36769528256486916),
				new DynVector2(0.5215540748770324, 0.47445238228262654),
				new DynVector2(0.527334528883231, 0.4308651755136941),
				new DynVector2(0.6152877195482263, 0.7025685140058535),
				new DynVector2(0.7757776753703407, 0.3215448776809937),
				new DynVector2(0.7785392212464266, 0.7209603726626324),
				new DynVector2(0.8940177489295154, -0.1882690653739989)
		};
		
		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a failure case.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void dac2() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(23.854242711277, 1.0),
			new DynVector2(57.707453390935676, 1.0),
			new DynVector2(13.0, 1.0),
			new DynVector2(27.918475169266998, 1.0)
		};
		
		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac3() {
		DynVector2[] cloud = new DynVector2[] {
				new DynVector2(1.5, 1.0),
				new DynVector2(2.0, 1.0),
				new DynVector2(2.5, 1.0),
				new DynVector2(3.5, 1.0),
				new DynVector2(1.0, 1.0),
				new DynVector2(5.0, 0.0)
		};

		this.testCloud(cloud);
	}

	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac4() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(1.0, 1.0),
			new DynVector2(1.0, 1.0),
			new DynVector2(9135172.538699752, 1594.2921033236523),
			new DynVector2(1.0, 3436.444789677664),
			new DynVector2(53371.47726301303, 63.201463180191396),
			new DynVector2(1.0, 1.0),
			new DynVector2(0.09713620217398017, 286668.0866273699),
			new DynVector2(104.83526669412421, 579.583503857007)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac6() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(9.67305424383519, 114.09907896473986),
			new DynVector2(1.0, 1161.9752606517477),
			new DynVector2(1.0, 1.0),
			new DynVector2(1.0, 10.546088997659012),
			new DynVector2(22.088494561091807, 230.94365885699824),
			new DynVector2(2.8366426821689994, 1.0),
			new DynVector2(8.944224404040732, 6.315177488492587),
			new DynVector2(1.0, 59.98064323348245),
			new DynVector2(9.24861145190379, 8404.268678968832),
			new DynVector2(1.0, 0.03504029713737921),
			new DynVector2(1.0, 82.55330004652801)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac7() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(1.0, 1.5767304065549268E12),
			new DynVector2(1.870841152451107, 6.534140012505794E23),
			new DynVector2(1.9603053816739006E12, 9.074076290143341E10),
			new DynVector2(3260266.640396411, 7.796498271329308E7),
			new DynVector2(28.709287118505284, 82447.5698720256),
			new DynVector2(1.8463774403168068E15, 0.013098511687408589),
			new DynVector2(3740193.601814064, 4682.6340006396895),
			new DynVector2(3170758.3618271016, 2.131083797649407E7),
			new DynVector2(143.83527343008367, 3.2294659543003845E15),
			new DynVector2(1.0, 5.956908518731977E17),
			new DynVector2(2.0531014115467064E-7, 2306510.1010659263),
			new DynVector2(1.2474786776966758E20, 1.4802417824918536E11),
			new DynVector2(1.0, 15.084034859698757)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac5() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(69464.96179292782, 0.05006981639456297),
			new DynVector2(0.03735960666625501, 0.3783853688716485),
			new DynVector2(334.68865889609134, 3.955720227287777E-23),
			new DynVector2(5.758935896542613E22, 8.12199057379559E21)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac5Part2() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(334.68865889609134, 3.955720227287777E-23),
			new DynVector2(5.758935896542613E22, 8.12199057379559E21),
			new DynVector2(69464.96179292782, 0.05006981639456297),
			new DynVector2(0.03735960666625501, 0.3783853688716485)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac5Part3() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(334.68865889609134, 3.955720227287777E-23),
			new DynVector2(5.758935896542613E22, 8.12199057379559E21),
			new DynVector2(400.758935896542613E21, 8.12199057379559E20),
			new DynVector2(69464.96179292782, 0.05006981639456297),
			new DynVector2(0.03735960666625501, 0.3783853688716485)
		};

		this.testCloud(cloud);
	}

	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac8() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(24.00000000000005, 24.000000000000053),
			new DynVector2(54.85, 6),
			new DynVector2(24.000000000000068, 24.000000000000071),
			new DynVector2(54.850000000000357, 61.000000000000121),
			new DynVector2(24, 6),
			new DynVector2(6, 6)
		};

		this.testCloud(cloud);
	}

	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac9() {
		DynVector2[] cloud = new DynVector2[] {
				new DynVector2(23, 1.0),
				new DynVector2(57, 1.0),
				new DynVector2(13, 1.0),
				new DynVector2(27, 10.0)};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac10() {
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(-7.725662343635252, 3.239314248048395),
			new DynVector2(-7.725662343635252, 9.244107520658332),
			new DynVector2(-7.725662343635252, 5.6066430781506575),
			new DynVector2(-5.985432177897989, 1.0634285355681339),
			new DynVector2(2.7404621676247265, -4.946792659796997),
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac11() {
		// coincident vertices
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(-5.214810023866061, -5.581528163221621),
			new DynVector2(-3.2956195481849493, 6.700146933201903),
			new DynVector2(2.159226322162535, -2.2353877725618476),
			new DynVector2(4.84788802330902, -6.921113359457114),
			new DynVector2(4.84788802330902, -6.921113359457114)
		};

		this.testCloud(cloud);
	}
	
	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac12() {
		// coincident vertices
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(-0.33826889474805055, 8.329321811558497),
			new DynVector2(-3.5586156659982215, -3.467244912905423),
			new DynVector2(-3.5586156659982215, -4.566140779700733),
			new DynVector2(-3.5586156659982215, -3.05702346750299),
			new DynVector2(1.1178446483487536, -3.05702346750299),
		};

		this.testCloud(cloud);
	}

	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac13() {
		// coincident vertices
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(-0.9025337983824699, 4.56709308364953),
			new DynVector2(-5.5168621708920345, 0.34366552069341916),
			new DynVector2(-2.400927400987851, 3.19563523962121),
			new DynVector2(-9.419896312210547, 3.19563523962121)
		};

		this.testCloud(cloud);
	}

	/**
	 * Tests the Divide and Conquer class against a prior failure case.
	 */
	@Test
	public void dac14() {
		// coincident vertices
		DynVector2[] cloud = new DynVector2[] {
			new DynVector2(5.916275853509346, -4.228267720344762),
			new DynVector2(8.31483976082672, -0.3807196367883092),
			new DynVector2(3.9941738969349405, -0.491971233546733),
			new DynVector2(-5.952110964171484, -0.7480752942332325)
		};

		this.testCloud(cloud);
	}
}
