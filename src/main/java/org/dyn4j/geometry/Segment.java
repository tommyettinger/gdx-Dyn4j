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
package org.dyn4j.geometry;

import java.util.Iterator;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.exception.ArgumentNullException;

/**
 * Implementation of a Line Segment {@link Convex} {@link Shape}.
 * <p>
 * This class represents a line segment that is infinitely thin.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 */
public class Segment extends AbstractShape implements Convex, Wound, Shape, Transformable, DataContainer {
	/** The segment vertices */
	final DynVector2[] vertices;
	
	/** The segment normals */
	final DynVector2[] normals;
	
	/** The segment length */
	double length;
	
	/**
	 * Validated constructor.
	 * <p>
	 * Creates a new segment using the given points.  The center will be the 
	 * average of the points.
	 * @param valid always true or this constructor would not be called
	 * @param point1 the first point
	 * @param point2 the second point
	 */
	private Segment(boolean valid, DynVector2[] vertices, DynVector2 segment, double length) {
		super(Geometry.getAverageCenter(vertices), length * 0.5);
		// assign the verices
		this.vertices = vertices;
		// create the normals
		this.normals = new DynVector2[2];
		this.normals[0] = segment.copy();
		this.normals[0].normalize();
		this.normals[1] = segment.right();
		this.normals[1].normalize();
		// compute the length
		this.length = length;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a new segment using the given points.  The center will be the 
	 * average of the points.
	 * <p>
	 * A segment's points cannot be null or the same point.
	 * @param point1 the first point
	 * @param point2 the second point
	 * @throws NullPointerException if point1 or point2 is null
	 * @throws IllegalArgumentException if point1 == point2
	 */
	public Segment(DynVector2 point1, DynVector2 point2) {
		this(validate(point1, point2),
			 new DynVector2[] {
				point1,
				point2
			 },
			 point1.to(point2),
			 point1.distance(point2));
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param point1 the first point
	 * @param point2 the second point
	 * @return boolean true
	 * @throws NullPointerException if point1 or point2 is null
	 * @throws IllegalArgumentException if point1 == point2
	 */
	private static final boolean validate(DynVector2 point1, DynVector2 point2) {
		// make sure either point is not null
		if (point1 == null) 
			throw new ArgumentNullException("point1");
		
		if (point2 == null) 
			throw new ArgumentNullException("point2");
		
		// make sure the two points are not coincident
		if (point1.equals(point2)) {
			throw new IllegalArgumentException("point1 and point2 cannot be the same point");
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Segment[").append(super.toString())
		.append("|Length=").append(this.length)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getVertices()
	 */
	@Override
	public DynVector2[] getVertices() {
		return this.vertices;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormals()
	 */
	@Override
	public DynVector2[] getNormals() {
		return this.normals;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getVertexIterator()
	 */
	@Override
	public Iterator<DynVector2> getVertexIterator() {
		return new WoundIterator(this.vertices);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormalIterator()
	 */
	@Override
	public Iterator<DynVector2> getNormalIterator() {
		return new WoundIterator(this.normals);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(DynVector2 center) {
		return Geometry.getRotationRadius(center, this.vertices);
	}
	
	/**
	 * Returns point1 in local coordinates.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getPoint1() {
		return this.vertices[0];
	}
	
	/**
	 * Returns point2 in local coordinates.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getPoint2() {
		return this.vertices[1];
	}
	
	/**
	 * Returns the length of the line {@link Segment}.
	 * @return double
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Determines where the point is relative to the given line.
	 * <p style="white-space: pre;"> Set L = linePoint2 - linePoint1
	 * Set P = point - linePoint1
	 * location = L.cross(P)</p>
	 * Returns 0 if the point lies on the line created from the line segment.<br>
	 * Assuming a right handed coordinate system:<br>
	 * Returns &lt; 0 if the point lies on the right side of the line<br>
	 * Returns &gt; 0 if the point lies on the left side of the line
	 * <p>
	 * Assumes all points are in world space.
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @throws NullPointerException if point, linePoint1, or linePoint2 is null
	 * @return double
	 */
	public static final double getLocation(DynVector2 point, DynVector2 linePoint1, DynVector2 linePoint2) {
		return (linePoint2.x - linePoint1.x) * (point.y - linePoint1.y) -
			  (point.x - linePoint1.x) * (linePoint2.y - linePoint1.y);
	}

	/**
	 * Returns the point on the given line closest to the given point.
	 * <p>
	 * Project the point onto the line:
	 * <p style="white-space: pre;"> V<sub>line</sub> = P<sub>1</sub> - P<sub>0</sub>
	 * V<sub>point</sub> = P<sub>0</sub> - P
	 * P<sub>closest</sub> = V<sub>point</sub>.project(V<sub>line</sub>)</p>
	 * Assumes all points are in world space.
	 * @see DynVector2#project(DynVector2)
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @throws NullPointerException if point, linePoint1, or linePoint2 is null
	 * @return {@link DynVector2}
	 */
	public static final DynVector2 getPointOnLineClosestToPoint(DynVector2 point, DynVector2 linePoint1, DynVector2 linePoint2) {
		// create a vector from the point to the first line point
		DynVector2 p1ToP = point.difference(linePoint1);
		// create a vector representing the line
	    DynVector2 line = linePoint2.difference(linePoint1);
	    // get the length squared of the line
	    double ab2 = line.dot(line);
	    // check ab2 for zero (linePoint1 == linePoint2)
	    if (ab2 <= Epsilon.E) return linePoint1.copy();
	    // get the projection of AP on AB
	    double ap_ab = p1ToP.dot(line);
	    // get the position from the first line point to the projection
	    double t = ap_ab / ab2;
	    // create the point on the line
	    return line.multiply(t).add(linePoint1);
	}

	/**
	 * Returns the point on the <b>line</b> that this {@link Segment} 
	 * defines closest to the given point.
	 * <p>
	 * This method works in this {@link Segment}'s local space.
	 * @param point the local space point
	 * @return {@link DynVector2}
	 * @throws NullPointerException if the given point is null
	 * @since 3.1.5
	 * @see #getPointOnLineClosestToPoint(DynVector2, DynVector2, DynVector2)
	 */
	public DynVector2 getPointOnLineClosestToPoint(DynVector2 point) {
		return Segment.getPointOnLineClosestToPoint(point, this.vertices[0], this.vertices[1]);
	}
	
	/**
	 * Returns the point on the given line segment closest to the given point.
	 * <p>
	 * If the point closest to the given point is on the line created by the
	 * given line segment, but is not on the line segment then either of the segments
	 * end points will be returned.
	 * <p>
	 * Assumes all points are in world space.
	 * @see Segment#getPointOnLineClosestToPoint(DynVector2, DynVector2, DynVector2)
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return {@link DynVector2}
	 * @throws NullPointerException if point, linePoint1, or linePoint2 is null
	 */
	public static final DynVector2 getPointOnSegmentClosestToPoint(DynVector2 point, DynVector2 linePoint1, DynVector2 linePoint2) {
		// create a vector from the point to the first line point
		DynVector2 p1ToP = point.difference(linePoint1);
		// create a vector representing the line
	    DynVector2 line = linePoint2.difference(linePoint1);
	    // get the length squared of the line
	    double ab2 = line.dot(line);
	    // get the projection of AP on AB
	    double ap_ab = p1ToP.dot(line);
	    // check ab2 for zero (linePoint1 == linePoint2)
	    if (ab2 <= Epsilon.E) return linePoint1.copy();
	    // get the position from the first line point to the projection
	    double t = ap_ab / ab2;
	    // make sure t is in between 0.0 and 1.0
	    t = Interval.clamp(t, 0.0, 1.0);
	    // create the point on the line
	    return line.multiply(t).add(linePoint1);
	}
	
	/**
	 * Returns the point on this {@link Segment} closest to the given point.
	 * <p>
	 * This method works in this {@link Segment}'s local space.
	 * @param point the local space point
	 * @return {@link DynVector2}
	 * @throws NullPointerException if the given point is null
	 * @since 3.1.5
	 * @see #getPointOnSegmentClosestToPoint(DynVector2, DynVector2, DynVector2)
	 */
	public DynVector2 getPointOnSegmentClosestToPoint(DynVector2 point) {
		return Segment.getPointOnSegmentClosestToPoint(point, this.vertices[0], this.vertices[1]);
	}
	
	/**
	 * Returns the intersection point of the two lines or null if they are parallel or coincident.
	 * <p>
	 * If we let:
	 * <p style="white-space: pre;"> A = A<sub>p2</sub> - A<sub>p1</sub>
	 * B = B<sub>p2</sub> - B<sub>p1</sub></p>
	 * we can create two parametric equations:
	 * <p style="white-space: pre;"> Q = A<sub>p1</sub> + t<sub>a</sub>A
	 * Q = B<sub>p1</sub> + t<sub>b</sub>B</p>
	 * Where Q is the intersection point:
	 * <p style="white-space: pre;"> A<sub>p1</sub> + t<sub>a</sub>A = B<sub>p1</sub> + t<sub>b</sub>B</p>
	 * We can solve for t<sub>b</sub> by applying the cross product with A on both sides:
	 * <p style="white-space: pre;"> (A<sub>p1</sub> + t<sub>a</sub>A) x A = (B<sub>p1</sub> + t<sub>b</sub>B) x A
	 * A<sub>p1</sub> x A = B<sub>p1</sub> x A + t<sub>b</sub>B x A
	 * (A<sub>p1</sub> - B<sub>p1</sub>) x A = t<sub>b</sub>B x A
	 * t<sub>b</sub> = ((A<sub>p1</sub> - B<sub>p1</sub>) x A) / (B x A)</p>
	 * If B x A == 0 then the lines are parallel.  If both the top and bottom are zero 
	 * then the lines are coincident.
	 * <p>
	 * If the lines are parallel or coincident, null is returned.
	 * @param ap1 the first point of the first line
	 * @param ap2 the second point of the first line
	 * @param bp1 the first point of the second line
	 * @param bp2 the second point of the second line
	 * @return Vector2 the intersection point; null if the lines are parallel or coincident
	 * @see #getSegmentIntersection(DynVector2, DynVector2, DynVector2, DynVector2)
	 * @throws NullPointerException if ap1, ap2, bp1 or bp2 is null
	 * @since 3.1.1
	 */
	public static final DynVector2 getLineIntersection(DynVector2 ap1, DynVector2 ap2, DynVector2 bp1, DynVector2 bp2) {
		DynVector2 A = ap1.to(ap2);
		DynVector2 B = bp1.to(bp2);
		
		// compute the bottom
		double BxA = B.cross(A);
		if (Math.abs(BxA) <= Epsilon.E) {
			// the lines are parallel and don't intersect
			return null;
		}
		
		// compute the top
		double ambxA = ap1.difference(bp1).cross(A);
		if (Math.abs(ambxA) <= Epsilon.E) {
			// the lines are coincident
			return null;
		}
		
		// compute tb
		double tb = ambxA / BxA;
		// compute the intersection point
		return B.product(tb).add(bp1);
	}
	
	/**
	 * Returns the line intersection of the given {@link Segment} and this {@link Segment}.
	 * <p>
	 * This method treats this segment and the given segment as defining <b>lines</b> rather than segments.
	 * <p>
	 * This method assumes that both this and the given segment are in the same space (either
	 * local or world space).
	 * <p>
	 * If the lines are parallel or coincident, null is returned.
	 * @param segment the other segment
	 * @return {@link DynVector2}
	 * @throws NullPointerException if the given segment is null
	 * @since 3.1.5
	 * @see #getLineIntersection(DynVector2, DynVector2, DynVector2, DynVector2)
	 */
	public DynVector2 getLineIntersection(Segment segment) {
		return Segment.getLineIntersection(this.vertices[0], this.vertices[1], segment.vertices[0], segment.vertices[1]);
	}
	
	/**
	 * Returns the intersection point of the two line segments or null if they are parallel, coincident
	 * or don't intersect.
	 * <p>
	 * If we let:
	 * <p style="white-space: pre;"> A = A<sub>p2</sub> - A<sub>p1</sub>
	 * B = B<sub>p2</sub> - B<sub>p1</sub></p>
	 * we can create two parametric equations:
	 * <p style="white-space: pre;"> Q = A<sub>p1</sub> + t<sub>a</sub>A
	 * Q = B<sub>p1</sub> + t<sub>b</sub>B</p>
	 * Where Q is the intersection point:
	 * <p style="white-space: pre;"> A<sub>p1</sub> + t<sub>a</sub>A = B<sub>p1</sub> + t<sub>b</sub>B</p>
	 * We can solve for t<sub>b</sub> by applying the cross product with A on both sides:
	 * <p style="white-space: pre;"> (A<sub>p1</sub> + t<sub>a</sub>A) x A = (B<sub>p1</sub> + t<sub>b</sub>B) x A
	 * A<sub>p1</sub> x A = B<sub>p1</sub> x A + t<sub>b</sub>B x A
	 * (A<sub>p1</sub> - B<sub>p1</sub>) x A = t<sub>b</sub>B x A
	 * t<sub>b</sub> = ((A<sub>p1</sub> - B<sub>p1</sub>) x A) / (B x A)</p>
	 * If B x A == 0 then the segments are parallel.  If the top == 0 then they don't intersect.  If both the
	 * top and bottom are zero then the segments are coincident.
	 * <p>
	 * If t<sub>b</sub> or t<sub>a</sub> less than zero or greater than 1 then the segments do not intersect.
	 * <p>
	 * If the segments do not intersect, are parallel, or are coincident, null is returned.
	 * @param ap1 the first point of the first line segment
	 * @param ap2 the second point of the first line segment
	 * @param bp1 the first point of the second line segment
	 * @param bp2 the second point of the second line segment
	 * @return Vector2 the intersection point; null if the line segments don't intersect, are parallel, or are coincident
	 * @see #getLineIntersection(DynVector2, DynVector2, DynVector2, DynVector2)
	 * @throws NullPointerException if ap1, ap2, bp1, or bp2 is null
	 * @since 3.1.1
	 */
	public static final DynVector2 getSegmentIntersection(DynVector2 ap1, DynVector2 ap2, DynVector2 bp1, DynVector2 bp2) {
		return Segment.getSegmentIntersection(ap1, ap2, bp1, bp2, true);
	}

	/**
	 * Returns the intersection point of the two line segments or null if they are parallel, coincident
	 * or don't intersect.
	 * <p>
	 * In the scenario where two segments intersect at an end point, the behavior is determined by the inclusive
	 * parameter.  When true, this method will return the intersection point - the end point.  When false, this 
	 * method will return null (indicating no intersection.
	 * @param ap1 the first point of the first line segment
	 * @param ap2 the second point of the first line segment
	 * @param bp1 the first point of the second line segment
	 * @param bp2 the second point of the second line segment
	 * @param inclusive see method documentation for more detail
	 * @return Vector2 the intersection point; null if the line segments don't intersect, are parallel, or are coincident
	 * @see #getSegmentIntersection(DynVector2, DynVector2, DynVector2, DynVector2)
	 * @throws NullPointerException if ap1, ap2, bp1, or bp2 is null
	 * @since 4.2.1
	 */
	public static final DynVector2 getSegmentIntersection(DynVector2 ap1, DynVector2 ap2, DynVector2 bp1, DynVector2 bp2, boolean inclusive) {
		DynVector2 A = ap1.to(ap2);
		DynVector2 B = bp1.to(bp2);
		
		// compute the bottom
		double BxA = B.cross(A);
		if (Math.abs(BxA) <= Epsilon.E) {
			// the line segments are parallel and don't intersect
			return null;
		}
		
		// compute the top
		double ambxA = ap1.difference(bp1).cross(A);
		if (Math.abs(ambxA) <= Epsilon.E) {
			// the line segments are coincident
			return null;
		}
		
		// compute tb
		double tb = ambxA / BxA;
		if (inclusive) {
			if (tb < 0.0 || tb > 1.0) return null;
		} else {
			if (tb <= 0.0 || tb >= 1.0) return null;
		}
		
		// compute the intersection point
		DynVector2 ip = B.product(tb).add(bp1);
		
		// since both are segments we need to verify that
		// ta is also valid.
		// compute ta
		double ta = ip.difference(ap1).dot(A) / A.dot(A);
		if (inclusive) {
			if (ta < 0.0 || ta > 1.0) return null;
		} else {
			if (ta <= 0.0 || ta >= 1.0) return null;
		}
		
		return ip;
	}

	/**
	 * Returns the intersection of the given {@link Segment} and this {@link Segment}.
	 * <p>
	 * This method assumes that both this and the given segment are in the same space (either
	 * local or world space).
	 * <p>
	 * If the segments do not intersect, are parallel, or are coincident, null is returned.
	 * @param segment the other segment
	 * @return {@link DynVector2}
	 * @throws NullPointerException if the given segment is null
	 * @since 3.1.5
	 * @see #getSegmentIntersection(DynVector2, DynVector2, DynVector2, DynVector2)
	 */
	public DynVector2 getSegmentIntersection(Segment segment) {
		return Segment.getSegmentIntersection(this.vertices[0], this.vertices[1], segment.vertices[0], segment.vertices[1], true);
	}
	
	/**
	 * Returns the farthest feature on the given segment.
	 * <p>
	 * This will always return the segment itself, but must return it with the correct winding
	 * and the correct maximum.
	 * @param v1 the first segment vertex
	 * @param v2 the second segment vertex
	 * @param vector the direction
	 * @param transform the local to world space {@link Transform} of this {@link Convex} {@link Shape}
	 * @return {@link EdgeFeature}
	 * @throws NullPointerException if v1, v2, vector, or transform is null
	 * @since 3.1.5
	 */
	public static final EdgeFeature getFarthestFeature(DynVector2 v1, DynVector2 v2, DynVector2 vector, Transform transform) {
		// the farthest feature for a line is always the line itself
		DynVector2 max = null;
		// get the vertices
		DynVector2 p1 = transform.getTransformed(v1);
		DynVector2 p2 = transform.getTransformed(v2);
		// project them onto the vector
		double dot1 = vector.dot(p1);
		double dot2 = vector.dot(p2);
		// find the greatest projection
		int index = 0;
		if (dot1 >= dot2) {
			max = p1;
			index = 0;
		} else {
			max = p2;
			index = 1;
		}
		// return the points of the segment in the
		// opposite direction as the other shape
		PointFeature vp1 = new PointFeature(p1, 0);
		PointFeature vp2 = new PointFeature(p2, 1);
		PointFeature vm = new PointFeature(max, index);
		// make sure the edge is the right winding
		if (p1.to(p2).right().dot(vector) > 0) {
			return new EdgeFeature(vp2, vp1, vm, p2.to(p1), 0);
		} else {
			return new EdgeFeature(vp1, vp2, vm, p1.to(p2), 0);
		}
	}
	
	/**
	 * Returns the farthest point on the given segment.
	 * @param v1 the first point of the segment
	 * @param v2 the second point of the segment
	 * @param vector the direction
	 * @param transform the local to world space {@link Transform} of this {@link Convex} {@link Shape}
	 * @return {@link DynVector2}
	 * @throws NullPointerException if v1, v2, vector, or transform is null
	 * @since 3.1.5
	 */
	public static final DynVector2 getFarthestPoint(DynVector2 v1, DynVector2 v2, DynVector2 vector, Transform transform) {
		// get the vertices and the center
		DynVector2 p1 = transform.getTransformed(v1);
		DynVector2 p2 = transform.getTransformed(v2);
		// project them onto the vector
		double dot1 = vector.dot(p1);
		double dot2 = vector.dot(p2);
		// find the greatest projection
		if (dot1 >= dot2) {
			return p1;
		} else {
			return p2;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(java.util.List, org.dyn4j.geometry.Transform)
	 */
	@Override
	public DynVector2[] getAxes(DynVector2[] foci, Transform transform) {
		// get the number of foci
		int size = foci != null ? foci.length : 0;
		// create an array to hold the axes
		DynVector2[] axes = new DynVector2[2 + size];
		int n = 0;
		// get the vertices
		DynVector2 p1 = transform.getTransformed(this.vertices[0]);
		DynVector2 p2 = transform.getTransformed(this.vertices[1]);
		// use both the edge and its normal
		axes[n++] = transform.getTransformedR(this.normals[1]);
		axes[n++] = transform.getTransformedR(this.normals[0]);
		DynVector2 axis;
		// add the voronoi region axes if point is supplied
		for (int i = 0; i < size; i++) {
			// get the focal point
			DynVector2 f = foci[i];
			// find the closest point
			if (p1.distanceSquared(f) < p2.distanceSquared(f)) {
				axis = p1.to(f);
			} else {
				axis = p2.to(f);
			}
			// normalize the axis
			axis.normalize();
			// add the axis to the array
			axes[n++] = axis;
		}
		// return all the axes
		return axes;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this shape.  Always returns null.
	 * @return null
	 */
	@Override
	public DynVector2[] getFoci(Transform transform) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Should almost always return false since this shape represents an infinitely
	 * thin line segment. Use the {@link #contains(DynVector2, Transform, double)}
	 * method instead for better, though technically inaccurate, results.
	 */
	@Override
	public boolean contains(DynVector2 point, Transform transform, boolean inclusive) {
		// if we don't consider the point on the segment
		// contains, then nothing can possibly be contained
		// in this segment
		if (!inclusive) {
			return false;
		}
		
		// put the point in local coordinates
		DynVector2 p = transform.getInverseTransformed(point);
		// create a reference to the end points
		DynVector2 p1 = this.vertices[0];
		DynVector2 p2 = this.vertices[1];
		// get the location of the given point relative to this segment
		double value = Segment.getLocation(p, p1, p2);
		// see if the point is on the line created by this line segment
		if (Math.abs(value) <= Epsilon.E) {
			double distSqrd = p1.distanceSquared(p2);
			if (p.distanceSquared(p1) <= distSqrd
			 && p.distanceSquared(p2) <= distSqrd) {
				// if the distance to the point from both points is less than or equal
				// to the length of the segment squared then we know its on the line segment
				return true;
			}
			// if the point is further away from either point than the length of the
			// segment then its not on the segment
			return false;
		}
		return false;
	}
	
	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * <p>
	 * If the radius is greater than zero then the point is tested to be
	 * within the shape expanded radially by the radius.
	 * @param point world space point
	 * @param transform {@link Transform} for this {@link Shape}
	 * @param radius the expansion radius; in the range [0, &infin;]
	 * @return boolean
	 */
	public boolean contains(DynVector2 point, Transform transform, double radius) {
		// if the radius is zero or less then perform the normal procedure
		if (radius <= 0) {
			return contains(point, transform);
		} else {
			// put the point in local coordinates
			DynVector2 p = transform.getInverseTransformed(point);
			// otherwise act like the segment is two circles and a rectangle
			if (this.vertices[0].distanceSquared(p) <= radius * radius) {
				return true;
			} else if (this.vertices[1].distanceSquared(p) <= radius * radius) {
				return true;
			} else {
				// see if the point is in the rectangle portion
				DynVector2 l = this.vertices[0].to(this.vertices[1]);
				DynVector2 p1 = this.vertices[0].to(p);
				DynVector2 p2 = this.vertices[1].to(p);
				if (l.dot(p1) > 0 && -l.dot(p2) > 0) {
					double dist = p1.project(l.getRightHandOrthogonalVector()).getMagnitudeSquared();
					if (dist <= radius * radius) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(DynVector2 vector, Transform transform) {
		double v = 0.0;
		// get the vertices
		DynVector2 p1 = transform.getTransformed(this.vertices[0]);
		DynVector2 p2 = transform.getTransformed(this.vertices[1]);
		// project the first
    	double min = vector.dot(p1);
    	double max = min;
    	// project the second
        v = vector.dot(p2);
        if (v < min) { 
            min = v;
        } else if (v > max) { 
            max = v;
        }

        return new Interval(min, max);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFurthestPoint(org.dyn4j.geometry.Vector, org.dyn4j.geometry.Transform)
	 */
	@Override
	public DynVector2 getFarthestPoint(DynVector2 vector, Transform transform) {
		return Segment.getFarthestPoint(this.vertices[0], this.vertices[1], vector, transform);
	}
	
	/**
	 * Returns the feature farthest in the direction of n.
	 * <p>
	 * For a {@link Segment} it's always the {@link Segment} itself.
	 * @param vector the direction
	 * @param transform the local to world space {@link Transform} of this {@link Convex} {@link Shape}
	 * @return {@link EdgeFeature}
	 */
	@Override
	public EdgeFeature getFarthestFeature(DynVector2 vector, Transform transform) {
		return Segment.getFarthestFeature(this.vertices[0], this.vertices[1], vector, transform);
	}
	

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		super.rotate(rotation, x, y);
		
		this.vertices[0].rotate(rotation, x, y);
		this.vertices[1].rotate(rotation, x, y);
		this.normals[0].rotate(rotation);
		this.normals[1].rotate(rotation);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		super.translate(x, y);
		this.vertices[0].add(x, y);
		this.vertices[1].add(x, y);
	}
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Segment} and the given density.
	 * <p>
	 * NOTE: This class represents an infinitely thin line segment and as such
	 * would technically have zero mass. However, to support physial simulation
	 * we assume a width of 1.0, therefore the mass is d * length.  This means
	 * that a segment would have the same mass as a rectangle of height = 1.0 with
	 * equal length to this segment.
	 * <p>
	 * Typically physical simulation for infinitely thin segments should limited to
	 * infinite mass "terrain" or static features of the simulation, in which case
	 * the area computation is irrelevant.
	 * <p style="white-space: pre;"> m = d * length
	 * I = l<sup>2</sup> * m / 12</p>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Segment}
	 */
	@Override
	public Mass createMass(double density) {
		double length = this.length;
		// compute the mass
		double mass = density * length;
		// compute the inertia tensor
		double inertia = length * length * mass / 12.0;
		// since we know that a line segment has only two points we can
		// feel safe using the averaging method for the centroid
		return new Mass(this.center, mass, inertia);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method will always return 0. This is a deviation from the {@link #createMass(double)}
	 * method where it assumes the dimension of width = 1.0 and uses the length as the "area".
	 */
	@Override
	public double getArea() {
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Be aware that this method could produce an infinitely thin
	 * AABB if this segment is aligned to either the x or y-axis.
	 */
	@Override
	public AABB createAABB(Transform transform) {
		AABB aabb = new AABB(0,0,0,0);
		this.computeAABB(transform, aabb);
		return aabb;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#computeAABB(org.dyn4j.geometry.Transform, org.dyn4j.geometry.AABB)
	 */
	@Override
	public void computeAABB(Transform transform, AABB aabb) {
    	// get the transformed points
		DynVector2 p0 = transform.getTransformed(this.vertices[0]);
		DynVector2 p1 = transform.getTransformed(this.vertices[1]);
		
		double maxX = p0.x;
		double minX = p1.x;
		if (maxX < minX) {
			double temp = maxX;
			maxX = minX;
			minX = temp;
		}
		
		double maxY = p0.y;
		double minY = p1.y;
		if (maxY < minY) {
			double temp = maxY;
			maxY = minY;
			minY = temp;
		}
		
		aabb.minX = minX;
		aabb.maxX = maxX;
		aabb.minY = minY;
		aabb.maxY = maxY;
	}
	
	/**
	 * Returns a normalized edge vector for this segment pointing
	 * from the first vertex to the second.
	 * @return Vector2
	 * @since 4.2.0
	 */
	public DynVector2 getEdgeVector() {
		DynVector2 edge = this.vertices[0].to(this.vertices[1]);
		edge.normalize();
		return edge;
	}
}
