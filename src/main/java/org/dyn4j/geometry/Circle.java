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

import org.dyn4j.DataContainer;
import org.dyn4j.exception.ValueOutOfRangeException;

/**
 * Implementation of a Circle {@link Convex} {@link Shape}.
 * <p>
 * A {@link Circle}'s radius must be greater than zero.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 */
public class Circle extends AbstractShape implements Convex, Shape, Transformable, DataContainer {
	/**
	 * Validated constructor.
	 * <p>
	 * Creates a new {@link Circle} centered on the origin with the given radius.
	 * @param valid always true or this constructor would not be called
	 * @param radius the radius
	 */
	private Circle(boolean valid, double radius) {
		super(radius);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a new {@link Circle} centered on the origin with the given radius.
	 * @param radius the radius
	 * @throws IllegalArgumentException if the given radius is less than or equal to zero
	 */
	public Circle(double radius) {
		this(validate(radius), radius);
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param radius the radius
	 * @return boolean true
	 * @throws IllegalArgumentException if the given radius is less than or equal to zero
	 */
	private static final boolean validate(double radius) {
		if (radius <= 0.0) 
			throw new ValueOutOfRangeException("radius", radius, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(DynVector2 center) {
		return this.radius + center.distance(this.center);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Circle[").append(super.toString())
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#contains(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform, boolean)
	 */
	@Override
	public boolean contains(DynVector2 point, Transform transform, boolean inclusive) {
		// transform the center
		DynVector2 v = transform.getTransformed(this.center);
		// get the transformed radius squared
		double radiusSquared = this.radius * this.radius;
		// create a vector from the center to the given point
		v.subtract(point);
		
		return inclusive ? v.getMagnitudeSquared() <= radiusSquared : v.getMagnitudeSquared() < radiusSquared;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(DynVector2 vector, Transform transform) {
		// if the transform is not null then transform the center
		DynVector2 center = transform.getTransformed(this.center);
		// project the center onto the given axis
		double c = center.dot(vector);
		// the interval is defined by the radius
		return new Interval(c - this.radius, c + this.radius);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * For a {@link Circle} this will always return a {@link PointFeature}.
	 */
	@Override
	public PointFeature getFarthestFeature(DynVector2 vector, Transform transform) {
		// obtain the farthest point along the given vector
		DynVector2 farthest = this.getFarthestPoint(vector, transform);
		// for a circle the farthest feature along a vector will always be a vertex
		return new PointFeature(farthest);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public DynVector2 getFarthestPoint(DynVector2 vector, Transform transform) {
		// make sure the axis is normalized
		DynVector2 nAxis = vector.getNormalized();
		// get the transformed center
		DynVector2 center = transform.getTransformed(this.center);
		// add the radius along the vector to the center to get the farthest point
		center.x += this.radius * nAxis.x;
		center.y += this.radius * nAxis.y;
		// return the new point
		return center;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Circular shapes are handled specifically in the SAT algorithm since
	 * they have an infinite number of axes. As a result this method returns
	 * null.
	 * @return null
	 */
	@Override
	public DynVector2[] getAxes(DynVector2[] foci, Transform transform) {
		// a circle has infinite separating axes and zero voronoi regions
		// therefore we return null
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFoci(org.dyn4j.geometry.Transform)
	 */
	@Override
	public DynVector2[] getFoci(Transform transform) {
		DynVector2[] foci = new DynVector2[1];
		// a circle only has one focus
		foci[0] = transform.getTransformed(this.center);
		return foci;
	}
	
	/**
	 * {@inheritDoc}
	 * <p style="white-space: pre;"> m = d * &pi; * r<sup>2</sup>
	 * I = m * r<sup>2</sup> / 2</p>
	 */
	@Override
	public Mass createMass(double density) {
		double r2 = this.radius * this.radius;
		// compute the mass
		double mass = density * Math.PI * r2;
		// compute the inertia tensor
		double inertia = mass * r2 * 0.5;
		// use the center supplied to the circle
		return new Mass(this.center, mass, inertia);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getArea()
	 */
	@Override
	public double getArea() {
		return Math.PI * this.radius * this.radius;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#computeAABB(org.dyn4j.geometry.Transform, org.dyn4j.geometry.AABB)
	 */
	@Override
	public void computeAABB(Transform transform, AABB aabb) {
		// if the transform is not null then transform the center
		DynVector2 center = transform.getTransformed(this.center);
		aabb.minX = center.x - this.radius;
		aabb.minY = center.y - this.radius;
		aabb.maxX = center.x + this.radius;
		aabb.maxY = center.y + this.radius;
	}
}
