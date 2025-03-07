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
package org.dyn4j.geometry;

import com.badlogic.gdx.utils.NumberUtils;
import org.dyn4j.Copyable;
import org.dyn4j.Epsilon;

/**
 * This class represents a vector or point in 3D space.
 * <p>
 * Used to solve 3x3 systems of equations.
 * @see DynVector2
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class DynVector3 implements Copyable<DynVector3> {
	/** The magnitude of the x component of this {@link DynVector3} */
	public double x;
	
	/** The magnitude of the y component of this {@link DynVector3} */
	public double y;
	
	/** The magnitude of the z component of this {@link DynVector3} */
	public double z;
	
	/** Default constructor. */
	public DynVector3() {}

	/**
	 * Copy constructor.
	 * @param vector the {@link DynVector3} to copy from
	 */
	public DynVector3(DynVector3 vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}
	
	/**
	 * Optional constructor.
	 * @param x the x component
	 * @param y the y component
	 * @param z the z component
	 */
	public DynVector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a {@link DynVector3} from the first point to the second point.
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param z1 the z coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 * @param z2 the z coordinate of the second point
	 */
	public DynVector3(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x = x2 - x1;
		this.y = y2 - y1;
		this.z = z2 - z1;
	}
	
	/**
	 * Creates a {@link DynVector3} from the first point to the second point.
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public DynVector3(DynVector3 p1, DynVector3 p2) {
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
		this.z = p2.z - p1.z;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	public DynVector3 copy() {
		return new DynVector3(this.x, this.y, this.z);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @param z the z coordinate of the point
	 * @return double
	 */
	public double distance(double x, double y, double z) {
		double xd = this.x - x;
		double yd = this.y - y;
		double zd = this.z - z;
		return Math.sqrt(xd * xd + yd * yd + zd * zd);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param point the point
	 * @return double
	 */
	public double distance(DynVector3 point) {
		double xd = this.x - point.x;
		double yd = this.y - point.y;
		double zd = this.z - point.z;
		return Math.sqrt(xd * xd + yd * yd + zd * zd);
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @param z the z coordinate of the point
	 * @return double
	 */
	public double distanceSquared(double x, double y, double z) {
		double xd = this.x - x;
		double yd = this.y - y;
		double zd = this.z - z;
		return xd * xd + yd * yd + zd * zd;
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param point the point
	 * @return double
	 */
	public double distanceSquared(DynVector3 point) {
		double xd = this.x - point.x;
		double yd = this.y - point.y;
		double zd = this.z - point.z;
		return xd * xd + yd * yd + zd * zd;
	}
	
	/**
	 * The triple product of {@link DynVector3}s is defined as:
	 * <pre>
	 * a x (b x c)
	 * </pre>
	 * However, this method performs the following triple product:
	 * <pre>
	 * (a x b) x c
	 * </pre>
	 * this can be simplified to:
	 * <pre>
	 * -a * (b &middot; c) + b * (a &middot; c)
	 * </pre>
	 * or:
	 * <pre>
	 * b * (a &middot; c) - a * (b &middot; c)
	 * </pre>
	 * @param a the a {@link DynVector3} in the above equation
	 * @param b the b {@link DynVector3} in the above equation
	 * @param c the c {@link DynVector3} in the above equation
	 * @return {@link DynVector3}
	 */
	public static DynVector3 tripleProduct(DynVector3 a, DynVector3 b, DynVector3 c) {
		// expanded version of above formula
		DynVector3 r = new DynVector3();
		// perform a.dot(c)
		double ac = a.x * c.x + a.y * c.y + a.z * c.z;
		// perform b.dot(c)
		double bc = b.x * c.x + b.y * c.y + b.z * c.z;
		// perform b * a.dot(c) - a * b.dot(c)
		r.x = b.x * ac - a.x * bc;
		r.y = b.y * ac - a.y * bc;
		r.z = b.z * ac - a.z * bc;
		return r;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = NumberUtils.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = NumberUtils.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = NumberUtils.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof DynVector3) {
			DynVector3 other = (DynVector3) obj;
			if (this.x == other.x
			 && this.y == other.y
			 && this.z == other.z) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the x and y components of this {@link DynVector3}
	 * are the same as the given {@link DynVector3}.
	 * @param vector the {@link DynVector3} to compare to
	 * @return boolean
	 */
	public boolean equals(DynVector3 vector) {
		if (vector == null) return false;
		if (this == vector) {
			return true;
		} else {
			return this.x == vector.x && this.y == vector.y && this.z == vector.z;
		}
	}
	
	/**
	 * Returns true if the x, y and z components of this {@link DynVector3}
	 * are the same as the given x, y and z components.
	 * @param x the x coordinate of the {@link DynVector3} to compare to
	 * @param y the y coordinate of the {@link DynVector3} to compare to
	 * @param z the z coordinate of the {@link DynVector3} to compare to
	 * @return boolean
	 */
	public boolean equals(double x, double y, double z) {
		return this.x == x && this.y == y && this.z == z;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		  .append(this.x)
		  .append(", ")
		  .append(this.y)
		  .append(", ")
		  .append(this.z)
		  .append(")");
		return sb.toString();
	}
	
	/**
	 * Sets this {@link DynVector3} to the given {@link DynVector3}.
	 * @param vector the {@link DynVector3} to set this {@link DynVector3} to
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 set(DynVector3 vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
		return this;
	}
	
	/**
	 * Sets this {@link DynVector3} to the given {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3} to set this {@link DynVector3} to
	 * @param y the y component of the {@link DynVector3} to set this {@link DynVector3} to
	 * @param z the z component of the {@link DynVector3} to set this {@link DynVector3} to
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * Returns the x component of this {@link DynVector3}.
	 * @return {@link DynVector3}
	 */
	public DynVector3 getXComponent() {
		return new DynVector3(this.x, 0.0, 0.0);
	}
	
	/**
	 * Returns the y component of this {@link DynVector3}.
	 * @return {@link DynVector3}
	 */
	public DynVector3 getYComponent() {
		return new DynVector3(0.0, this.y, 0.0);
	}
	
	/**
	 * Returns the z component of this {@link DynVector3}.
	 * @return {@link DynVector3}
	 */
	public DynVector3 getZComponent() {
		return new DynVector3(0.0, 0.0, this.z);
	}
	
	/**
	 * Returns the magnitude of this {@link DynVector3}.
	 * @return double
	 */
	public double getMagnitude() {
		// the magnitude is just the pathagorean theorem
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	/**
	 * Returns the magnitude of this {@link DynVector3} squared.
	 * @return double
	 */
	public double getMagnitudeSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	/**
	 * Sets the magnitude of the {@link DynVector3}.
	 * @param magnitude  the magnitude
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 setMagnitude(double magnitude) {
		// check the given magnitude
		if (Math.abs(magnitude) <= Epsilon.E) {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
			return this;
		}
		// is this vector a zero vector?
		if (this.isZero()) {
			return this;
		}
		// get the magnitude
		double mag = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		// normalize and multiply by the new magnitude
		mag = magnitude / mag;
		this.x *= mag;
		this.y *= mag;
		this.z *= mag;
		return this;
	}
	
	/**
	 * Adds the given {@link DynVector3} to this {@link DynVector3}.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 add(DynVector3 vector) {
		this.x += vector.x;
		this.y += vector.y;
		this.z += vector.z;
		return this;
	}
	
	/**
	 * Adds the given {@link DynVector3} to this {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	/**
	 * Adds this {@link DynVector3} and the given {@link DynVector3} returning
	 * a new {@link DynVector3} containing the result.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 sum(DynVector3 vector) {
		return new DynVector3(this.x + vector.x, this.y + vector.y, this.z + vector.z);
	}
	
	/**
	 * Adds this {@link DynVector3} and the given {@link DynVector3} returning
	 * a new {@link DynVector3} containing the result.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 sum(double x, double y, double z) {
		return new DynVector3(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Subtracts the given {@link DynVector3} from this {@link DynVector3}.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 subtract(DynVector3 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		this.z -= vector.z;
		return this;
	}
	
	/**
	 * Subtracts the given {@link DynVector3} from this {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	/**
	 * Subtracts the given {@link DynVector3} from this {@link DynVector3} returning
	 * a new {@link DynVector3} containing the result.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 difference(DynVector3 vector) {
		return new DynVector3(this.x - vector.x, this.y - vector.y, this.z - vector.z);
	}
	
	/**
	 * Subtracts the given {@link DynVector3} from this {@link DynVector3} returning
	 * a new {@link DynVector3} containing the result.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 difference(double x, double y, double z) {
		return new DynVector3(this.x - x, this.y - y, this.z - z);
	}
	
	/**
	 * Creates a {@link DynVector3} from this {@link DynVector3} to the given {@link DynVector3}.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 to(DynVector3 vector) {
		return new DynVector3(vector.x - this.x, vector.y - this.y, vector.z - this.z);
	}
	
	/**
	 * Creates a {@link DynVector3} from this {@link DynVector3} to the given {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 to(double x, double y, double z) {
		return new DynVector3(x - this.x, y - this.y, z - this.z);
	}
		
	/**
	 * Multiplies this {@link DynVector3} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 multiply(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link DynVector3} by the given scalar returning
	 * a new {@link DynVector3} containing the result.
	 * @param scalar the scalar
	 * @return {@link DynVector3}
	 */
	public DynVector3 product(double scalar) {
		return new DynVector3(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	
	/**
	 * Returns the dot product of the given {@link DynVector3}
	 * and this {@link DynVector3}.
	 * @param vector the {@link DynVector3}
	 * @return double
	 */
	public double dot(DynVector3 vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z;
	}
	
	/**
	 * Returns the dot product of the given {@link DynVector3}
	 * and this {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return double
	 */
	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	/**
	 * Returns the cross product of the this {@link DynVector3} and the given {@link DynVector3}.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 cross(DynVector3 vector) {
		return new DynVector3(this.y * vector.z - this.z * vector.y,
				           this.z * vector.x - this.x * vector.z,
				           this.x * vector.y - this.y * vector.x);
	}
	
	/**
	 * Returns the cross product of the this {@link DynVector3} and the given {@link DynVector3}.
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return {@link DynVector3}
	 */
	public DynVector3 cross(double x, double y, double z) {
		return new DynVector3(this.y * z - this.z * y,
		                   this.z * x - this.x * z,
		                   this.x * y - this.y * x);
	}
	
	/**
	 * Returns true if the given {@link DynVector3} is orthogonal (perpendicular)
	 * to this {@link DynVector3}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param vector the {@link DynVector3}
	 * @return boolean
	 */
	public boolean isOrthogonal(DynVector3 vector) {
		return Math.abs(this.x * vector.x + this.y * vector.y + this.z * vector.z) <= Epsilon.E ? true : false;
	}
	
	/**
	 * Returns true if the given {@link DynVector3} is orthogonal (perpendicular)
	 * to this {@link DynVector3}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param x the x component of the {@link DynVector3}
	 * @param y the y component of the {@link DynVector3}
	 * @param z the z component of the {@link DynVector3}
	 * @return boolean
	 */
	public boolean isOrthogonal(double x, double y, double z) {
		return Math.abs(this.x * x + this.y * y + this.z * z) <= Epsilon.E;
	}
	
	/**
	 * Returns true if this {@link DynVector3} is the zero {@link DynVector3}.
	 * @return boolean
	 */
	public boolean isZero() {
		return Math.abs(this.x) <= Epsilon.E && Math.abs(this.y) <= Epsilon.E && Math.abs(this.z) <= Epsilon.E;
	}

	/** 
	 * Negates this {@link DynVector3}.
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}
	
	/**
	 * Returns a {@link DynVector3} which is the negative of this {@link DynVector3}.
	 * @return {@link DynVector3}
	 */
	public DynVector3 getNegative() {
		return new DynVector3(-this.x, -this.y, -this.z);
	}
	
	/** 
	 * Sets the {@link DynVector3} to the zero {@link DynVector3}
	 * @return {@link DynVector3} this vector
	 */
	public DynVector3 zero() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		return this;
	}
	
	/**
	 * Projects this {@link DynVector3} onto the given {@link DynVector3}.
	 * <p>
	 * This method requires the length of the given {@link DynVector3} is not zero.
	 * @param vector the {@link DynVector3}
	 * @return {@link DynVector3} the projected {@link DynVector3}
	 */
	public DynVector3 project(DynVector3 vector) {
		double dotProd = this.dot(vector);
		double denominator = vector.dot(vector);
		if (denominator <= Epsilon.E) return new DynVector3();
		denominator = dotProd / denominator;
		return new DynVector3(denominator * vector.x, denominator * vector.y, denominator * vector.z);
	}
	
	/**
	 * Returns a unit {@link DynVector3} of this {@link DynVector3}.
	 * <p>
	 * This method requires the length of this {@link DynVector3} is not zero.
	 * @return {@link DynVector3}
	 */
	public DynVector3 getNormalized() {
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (magnitude <= Epsilon.E) return new DynVector3();
		magnitude = 1.0 / magnitude;
		return new DynVector3(this.x * magnitude, this.y * magnitude, this.z * magnitude);
	}
	
	/**
	 * Converts this {@link DynVector3} into a unit {@link DynVector3} and returns
	 * the magnitude before normalization.
	 * <p>
	 * This method requires the length of this {@link DynVector3} is not zero.
	 * @return double
	 */
	public double normalize() {
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (magnitude <= Epsilon.E) return 0;
		double m = 1.0 / magnitude;
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return magnitude;
	}
}
