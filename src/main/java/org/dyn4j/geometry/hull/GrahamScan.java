/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.NullElementException;
import org.dyn4j.geometry.RobustGeometry;
import org.dyn4j.geometry.DynVector2;

/**
 * Implementation of the Graham Scan convex hull algorithm.
 * <p>
 * This algorithm handles coincident and colinear points by ignoring them during processing. This ensures
 * the produced hull will not have coincident or colinear vertices.
 * <p>
 * This algorithm is O(n log n) where n is the number of input points.
 * @author William Bittle
 * @version 5.0.0
 * @since 2.2.0
 */
public class GrahamScan extends AbstractHullGenerator implements HullGenerator {
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.hull.HullGenerator#generate(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public DynVector2[] generate(DynVector2... points) {
		// check for null points array
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// get the size
		int size = points.length;
		// check the size
		if (size <= 2) return points;
		
		// find the point of minimum y (choose the point of minimum x if there is a tie)
		DynVector2 minY = points[0];
		for (int i = 1; i < size; i++) {
			DynVector2 p = points[i];
			
			// make sure the point is not null
			if (p == null) 
				throw new NullElementException("points", i);
			
			if (p.y < minY.y) {
				minY = p;
			} else if (p.y == minY.y) {
				if (p.x > minY.x) {
					minY = p;
				}
			}
		}
		
		// create the comparator for the array
		ReferencePointComparator pc = new ReferencePointComparator(minY);
		// sort the array by angle
		Arrays.sort(points, pc);
		
		// build the hull
		List<DynVector2> stack = new ArrayList<DynVector2>();
		
		// push
		stack.add(points[0]);
		stack.add(points[1]);
		
		int i = 2;
		while (i < size) {
			int sSize = stack.size();
			// if the stack size is one then just
			// push the current point onto the stack
			// thereby making a line segment
			
			if (sSize == 1) {
				// push
				stack.add(points[i]);
				i++;
				continue;
			}
			
			// otherwise get the top two items off the stack
			DynVector2 p1 = stack.get(sSize - 2);
			DynVector2 p2 = stack.get(sSize - 1);
			// get the current point
			DynVector2 p3 = points[i];
			
			// test if the current point is to the left of the line
			// created by the top two items in the stack (the last edge
			// on the current convex hull)
			
			// Use the robust side of line test because otherwise this algorithm
			// can produce incorrect results in edge cases
			// The order of parameters here must match the one in ReferenceComparator
			// in order to obtain correct results and winding
			double location = RobustGeometry.getLocation(p3, p2, p1);
			
			if (location < 0.0) {
				// if its to the left, then push the new point on
				// the stack since it maintains convexity
				stack.add(p3);
				i++;
			} else {
				// otherwise the pop the previous point off the stack
				// since this indicates that if we added the current
				// point to the stack we would make a concave section
				// pop
				stack.remove(sSize - 1);
			}
		}
		
		// finally copy all the stack items into the array to return
		DynVector2[] hull = new DynVector2[stack.size()];
		stack.toArray(hull);
		
		// return the array
		return hull;
	}
}
