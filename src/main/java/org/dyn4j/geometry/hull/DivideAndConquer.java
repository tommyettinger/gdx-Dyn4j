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

import java.util.Arrays;

import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.NullElementException;
import org.dyn4j.geometry.DynVector2;

/**
 * Implementation of the Divide and Conquer convex hull algorithm.
 * <p>
 * This algorithm handles coincident and colinear points by ignoring them during processing. This ensures
 * the produced hull will not have coincident or colinear vertices.
 * <p>
 * This algorithm is O(n log n) where n is the number of input points.
 * @author William Bittle
 * @version 5.0.0
 * @since 2.2.0
 */
public class DivideAndConquer extends AbstractHullGenerator implements HullGenerator {
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.hull.HullGenerator#generate(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public DynVector2[] generate(DynVector2... points) {
		// check for a null array of points
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// get the size
		int size = points.length;
		// check the size
		if (size <= 2) return points;
		
		try {
			// sort the points by the x coordinate, then the y coordinate
			Arrays.sort(points, new MinXYPointComparator());
		} catch (NullPointerException e) {
			// this will be hit if any of the points are null
			throw new NullElementException("points");
		}
		
		// No need to pre-process the input and remove coincident points
		// Those are gracefully handled and removed in the main algorithm 
		
		// perform the divide and conquer algorithm on the point cloud
		LinkedVertexHull hull = this.divide(points, 0, size);
		
		// return the array
		return hull.toArray();
	}
	
	/**
	 * Recursive method to subdivide and merge the points.
	 * @param points the array of points
	 * @param first the first index inclusive
	 * @param last the last index exclusive
	 * @return {@link LinkedVertexHull} the convex hull created
	 */
	final LinkedVertexHull divide(DynVector2[] points, int first, int last) {
		// compute the size of the hull we need to create
		int size = last - first;
		if (size == 1) {
			// if we only have one point create a hull containing the one point
			return new LinkedVertexHull(points[first]);
		} else {
			// otherwise find the middle index
			int mid = (first + last) / 2;
			// create the left convex hull
			LinkedVertexHull left = divide(points, first, mid);
			// create the right convex hull
			LinkedVertexHull right = divide(points, mid, last);
			// merge the two convex hulls
			return LinkedVertexHull.merge(left, right);
		}
	}
}
