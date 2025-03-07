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
 * This class represents a vector or point in 2D space.
 * <p>
 * The operations {@link DynVector2#setMagnitude(double)}, {@link DynVector2#getNormalized()},
 * {@link DynVector2#project(DynVector2)}, and {@link DynVector2#normalize()} require the {@link DynVector2}
 * to be non-zero in length.
 * <p>
 * Some methods also return the vector to facilitate chaining.  For example:
 * <pre>
 * Vector a = new Vector();
 * a.zero().add(1, 2).multiply(2);
 * </pre>
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class DynVector2 implements Copyable<DynVector2> {
	/** A vector representing the x-axis; this vector should not be changed at runtime; used internally */
	static final DynVector2 X_AXIS = new DynVector2(1.0, 0.0);
	
	/** A vector representing the y-axis; this vector should not be changed at runtime; used internally */
	static final DynVector2 Y_AXIS = new DynVector2(0.0, 1.0);
	
	/** A vector representing the inverse x-axis; this vector should not be changed at runtime; used internally */
	static final DynVector2 INV_X_AXIS = new DynVector2(-1.0, 0.0);
	
	/** A vector representing the inverse y-axis; this vector should not be changed at runtime; used internally */
	static final DynVector2 INV_Y_AXIS = new DynVector2(0.0, -1.0);
	
	/** The magnitude of the x component of this {@link DynVector2} */
	public double x;
	
	/** The magnitude of the y component of this {@link DynVector2} */
	public double y;
	
	/** Default constructor. */
	public DynVector2() {}

	/**
	 * Copy constructor.
	 * @param vector the {@link DynVector2} to copy from
	 */
	public DynVector2(DynVector2 vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/**
	 * Optional constructor.
	 * @param x the x component
	 * @param y the y component
	 */
	public DynVector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a {@link DynVector2} from the first point to the second point.
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	public DynVector2(double x1, double y1, double x2, double y2) {
		this.x = x2 - x1;
		this.y = y2 - y1;
	}
	
	/**
	 * Creates a {@link DynVector2} from the first point to the second point.
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public DynVector2(DynVector2 p1, DynVector2 p2) {
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
	}

	/**
	 * Creates a unit length vector in the given direction.
	 * @param direction the direction in radians
	 * @since 3.0.1
	 */
	public DynVector2(double direction) {
		this.x = Math.cos(direction);
		this.y = Math.sin(direction);
	}
	
	/**
	 * Returns a new {@link DynVector2} given the magnitude and direction.
	 * @param magnitude the magnitude of the {@link DynVector2}
	 * @param direction the direction of the {@link DynVector2} in radians
	 * @return {@link DynVector2}
	 */
	public static DynVector2 create(double magnitude, double direction) {
		double x = magnitude * Math.cos(direction);
		double y = magnitude * Math.sin(direction);
		return new DynVector2(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	public DynVector2 copy() {
		return new DynVector2(this.x, this.y);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return double
	 */
	public double distance(double x, double y) {
		//return Math.hypot(this.x - x, this.y - y);
		double dx = this.x - x;
		double dy = this.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param point the point
	 * @return double
	 */
	public double distance(DynVector2 point) {
		//return Math.hypot(this.x - point.x, this.y - point.y);
		double dx = this.x - point.x;
		double dy = this.y - point.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return double
	 */
	public double distanceSquared(double x, double y) {
		//return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param point the point
	 * @return double
	 */
	public double distanceSquared(DynVector2 point) {
		//return (this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y);
		double dx = this.x - point.x;
		double dy = this.y - point.y;
		return dx * dx + dy * dy;
	}
	
	/**
	 * The triple product of {@link DynVector2}s is defined as:
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
	 * @param a the a {@link DynVector2} in the above equation
	 * @param b the b {@link DynVector2} in the above equation
	 * @param c the c {@link DynVector2} in the above equation
	 * @return {@link DynVector2}
	 */
	public static DynVector2 tripleProduct(DynVector2 a, DynVector2 b, DynVector2 c) {
		// expanded version of above formula
		DynVector2 r = new DynVector2();
		
		/*
		 * In the following we can substitute ac and bc in r.x and r.y
		 * and with some rearrangement get a much more efficient version
		 * 
		 * double ac = a.x * c.x + a.y * c.y;
		 * double bc = b.x * c.x + b.y * c.y;
		 * r.x = b.x * ac - a.x * bc;
		 * r.y = b.y * ac - a.y * bc;
		 */
		
		double dot = a.x * b.y - b.x * a.y;
		r.x = -c.y * dot;
		r.y = c.x * dot;
		
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
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof DynVector2) {
			DynVector2 vector = (DynVector2)obj;
			return this.x == vector.x && this.y == vector.y;
		}
		return false;
	}

	/**
	 * Returns true if the x and y components of this {@link DynVector2}
	 * are the same as the given {@link DynVector2}.
	 * @param vector the {@link DynVector2} to compare to
	 * @return boolean
	 */
	public boolean equals(DynVector2 vector) {
		if (vector == null) return false;
		if (this == vector) {
			return true;
		} else {
			return this.x == vector.x && this.y == vector.y;
		}
	}
	
	/**
	 * Returns true if the x and y components of this {@link DynVector2}
	 * are the same as the given x and y components.
	 * @param x the x coordinate of the {@link DynVector2} to compare to
	 * @param y the y coordinate of the {@link DynVector2} to compare to
	 * @return boolean
	 */
	public boolean equals(double x, double y) {
		return this.x == x && this.y == y;
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
		  .append(")");
		return sb.toString();
	}
	
	/**
	 * Sets this {@link DynVector2} to the given {@link DynVector2}.
	 * @param vector the {@link DynVector2} to set this {@link DynVector2} to
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 set(DynVector2 vector) {
		this.x = vector.x;
		this.y = vector.y;
		return this;
	}
	
	/**
	 * Sets this {@link DynVector2} to the given {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2} to set this {@link DynVector2} to
	 * @param y the y component of the {@link DynVector2} to set this {@link DynVector2} to
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Returns the x component of this {@link DynVector2}.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getXComponent() {
		return new DynVector2(this.x, 0.0);
	}
	
	/**
	 * Returns the y component of this {@link DynVector2}.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getYComponent() {
		return new DynVector2(0.0, this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link DynVector2}.
	 * @return double
	 */
	public double getMagnitude() {
		// the magnitude is just the pythagorean theorem
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link DynVector2} squared.
	 * @return double
	 */
	public double getMagnitudeSquared() {
		return this.x * this.x + this.y * this.y;
	}
	
	/**
	 * Sets the magnitude of the {@link DynVector2}.
	 * @param magnitude the magnitude
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 setMagnitude(double magnitude) {
		// check the given magnitude
		if (Math.abs(magnitude) <= Epsilon.E) {
			this.x = 0.0;
			this.y = 0.0;
			return this;
		}
		// is this vector a zero vector?
		if (this.isZero()) {
			return this;
		}
		// get the magnitude
		double mag = Math.sqrt(this.x * this.x + this.y * this.y);
		// normalize and multiply by the new magnitude
		mag = magnitude / mag;
		this.x *= mag;
		this.y *= mag;
		return this;
	}
	
	/**
	 * Returns the direction of this {@link DynVector2}
	 * as an angle in radians.
	 * @return double angle in radians [-&pi;, &pi;]
	 */
	public double getDirection() {
		return Math.atan2(this.y, this.x);
	}
	
	/**
	 * Sets the direction of this {@link DynVector2}.
	 * @param angle angle in radians
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 setDirection(double angle) {
		//double magnitude = Math.hypot(this.x, this.y);
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
        this.x = magnitude * Math.cos(angle);
        this.y = magnitude * Math.sin(angle);
        return this;
	}
	
	/**
	 * Adds the given {@link DynVector2} to this {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 add(DynVector2 vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}
	
	/**
	 * Adds the given {@link DynVector2} to this {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Adds this {@link DynVector2} and the given {@link DynVector2} returning
	 * a new {@link DynVector2} containing the result.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 sum(DynVector2 vector) {
		return new DynVector2(this.x + vector.x, this.y + vector.y);
	}
	
	/**
	 * Adds this {@link DynVector2} and the given {@link DynVector2} returning
	 * a new {@link DynVector2} containing the result.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 sum(double x, double y) {
		return new DynVector2(this.x + x, this.y + y);
	}
	
	/**
	 * Subtracts the given {@link DynVector2} from this {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 subtract(DynVector2 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link DynVector2} from this {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 subtract(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link DynVector2} from this {@link DynVector2} returning
	 * a new {@link DynVector2} containing the result.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 difference(DynVector2 vector) {
		return new DynVector2(this.x - vector.x, this.y - vector.y);
	}
	
	/**
	 * Subtracts the given {@link DynVector2} from this {@link DynVector2} returning
	 * a new {@link DynVector2} containing the result.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 difference(double x, double y) {
		return new DynVector2(this.x - x, this.y - y);
	}
	
	/**
	 * Creates a {@link DynVector2} from this {@link DynVector2} to the given {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 to(DynVector2 vector) {
		return new DynVector2(vector.x - this.x, vector.y - this.y);
	}
	
	/**
	 * Creates a {@link DynVector2} from this {@link DynVector2} to the given {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 to(double x, double y) {
		return new DynVector2(x - this.x, y - this.y);
	}
		
	/**
	 * Multiplies this {@link DynVector2} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 multiply(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	/**
	 * Divides this {@link DynVector2} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 divide(double scalar) {
		this.x /= scalar;
		this.y /= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link DynVector2} by the given scalar returning
	 * a new {@link DynVector2} containing the result.
	 * @param scalar the scalar
	 * @return {@link DynVector2}
	 */
	public DynVector2 product(double scalar) {
		return new DynVector2(this.x * scalar, this.y * scalar);
	}
	
	/**
	 * Divides this {@link DynVector2} by the given scalar returning
	 * a new {@link DynVector2} containing the result.
	 * @param scalar the scalar
	 * @return {@link DynVector2}
	 * @since 3.4.0
	 */
	public DynVector2 quotient(double scalar) {
		return new DynVector2(this.x / scalar, this.y / scalar);
	}
	
	/**
	 * Returns the dot product of the given {@link DynVector2}
	 * and this {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return double
	 */
	public double dot(DynVector2 vector) {
		return this.x * vector.x + this.y * vector.y;
	}
	
	/**
	 * Returns the dot product of the given {@link DynVector2}
	 * and this {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return double
	 */
	public double dot(double x, double y) {
		return this.x * x + this.y * y;
	}
	
	/**
	 * Returns the cross product of the this {@link DynVector2} and the given {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return double
	 */
	public double cross(DynVector2 vector) {
		return this.x * vector.y - this.y * vector.x;
	}
	
	/**
	 * Returns the cross product of the this {@link DynVector2} and the given {@link DynVector2}.
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return double
	 */
	public double cross(double x, double y) {
		return this.x * y - this.y * x;
	}
	
	/**
	 * Returns the cross product of this {@link DynVector2} and the z value of the right {@link DynVector2}.
	 * @param z the z component of the {@link DynVector2}
	 * @return {@link DynVector2}
	 */
	public DynVector2 cross(double z) {
		return new DynVector2(-this.y * z, this.x * z);
	}
	
	/**
	 * Returns true if the given {@link DynVector2} is orthogonal (perpendicular)
	 * to this {@link DynVector2}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param vector the {@link DynVector2}
	 * @return boolean
	 */
	public boolean isOrthogonal(DynVector2 vector) {
		return Math.abs(this.x * vector.x + this.y * vector.y) <= Epsilon.E;
	}
	
	/**
	 * Returns true if the given {@link DynVector2} is orthogonal (perpendicular)
	 * to this {@link DynVector2}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param x the x component of the {@link DynVector2}
	 * @param y the y component of the {@link DynVector2}
	 * @return boolean
	 */
	public boolean isOrthogonal(double x, double y) {
		return Math.abs(this.x * x + this.y * y) <= Epsilon.E;
	}
	
	/**
	 * Returns true if this {@link DynVector2} is the zero {@link DynVector2}.
	 * @return boolean
	 */
	public boolean isZero() {
		return Math.abs(this.x) <= Epsilon.E && Math.abs(this.y) <= Epsilon.E;
	}
	
	/** 
	 * Negates this {@link DynVector2}.
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 negate() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}
	
	/**
	 * Returns a {@link DynVector2} which is the negative of this {@link DynVector2}.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getNegative() {
		return new DynVector2(-this.x, -this.y);
	}
	
	/** 
	 * Sets the {@link DynVector2} to the zero {@link DynVector2}
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 zero() {
		this.x = 0.0;
		this.y = 0.0;
		return this;
	}
	
	/**
	 * Internal helper method that rotates about the origin by an angle &theta;.
	 * @param cos cos(&theta;)
	 * @param sin sin(&theta;)
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	DynVector2 rotate(double cos, double sin) {
		double x = this.x;
		double y = this.y;
		
		this.x = x * cos - y * sin;
		this.y = x * sin + y * cos;
		
		return this;
	}
	
	/**
	 * Rotates about the origin.
	 * @param theta the rotation angle in radians
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 rotate(double theta) {
		return this.rotate(Math.cos(theta), Math.sin(theta));
	}
	
	/**
	 * Rotates about the origin.
	 * @param rotation the {@link Rotation}
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 rotate(Rotation rotation) {
		return this.rotate(rotation.cost, rotation.sint);
	}
	
	/**
	 * Rotates about the origin by the inverse angle -&thetasym;.
	 * @param theta the rotation angle in radians
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(double theta) {
		return this.rotate(Math.cos(theta), -Math.sin(theta));
	}
	
	/**
	 * Rotates about the origin by the inverse angle -&thetasym;.
	 * @param rotation the {@link Rotation}
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(Rotation rotation) {
		return this.rotate(rotation.cost, -rotation.sint);
	}
	
	/**
	 * Internal helper method that rotates about the given coordinates by an angle &theta;.
	 * @param cos cos(&theta;)
	 * @param sin sin(&theta;)
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	DynVector2 rotate(double cos, double sin, double x, double y) {
		double tx = (this.x - x);
		double ty = (this.y - y);
		
		this.x = tx * cos - ty * sin + x;
		this.y = tx * sin + ty * cos + y;
		
		return this;
	}
	
	/**
	 * Rotates the {@link DynVector2} about the given coordinates.
	 * @param theta the rotation angle in radians
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 rotate(double theta, double x, double y) {
		return this.rotate(Math.cos(theta), Math.sin(theta), x, y);
	}
	
	/**
	 * Rotates the {@link DynVector2} about the given coordinates.
	 * @param rotation the {@link Rotation}
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 rotate(Rotation rotation, double x, double y) {
		return this.rotate(rotation.cost, rotation.sint, x, y);
	}
	
	/**
	 * Rotates about the given coordinates by the inverse angle -&thetasym;.
	 * @param theta the rotation angle in radians
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(double theta, double x, double y) {
		return this.rotate(Math.cos(theta), -Math.sin(theta), x, y);
	}
	
	/**
	 * Rotates about the given coordinates by the inverse angle -&thetasym;.
	 * @param rotation the {@link Rotation}
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(Rotation rotation, double x, double y) {
		return this.rotate(rotation.cost, -rotation.sint, x, y);
	}

	/**
	 * Rotates the {@link DynVector2} about the given point.
	 * @param theta the rotation angle in radians
	 * @param point the point to rotate about
	 * @return {@link DynVector2} this vector
	 */
	public DynVector2 rotate(double theta, DynVector2 point) {
		return this.rotate(theta, point.x, point.y);
	}
	
	/**
	 * Rotates the {@link DynVector2} about the given point.
	 * @param rotation the {@link Rotation}
	 * @param point the point to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 rotate(Rotation rotation, DynVector2 point) {
		return this.rotate(rotation, point.x, point.y);
	}
	
	/**
	 * Rotates the {@link DynVector2} about the given point by the inverse angle -&thetasym;.
	 * @param theta the rotation angle in radians
	 * @param point the point to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(double theta, DynVector2 point) {
		return this.inverseRotate(theta, point.x, point.y);
	}
	
	/**
	 * Rotates the {@link DynVector2} about the given point by the inverse angle -&thetasym;.
	 * @param rotation the {@link Rotation}
	 * @param point the point to rotate about
	 * @return {@link DynVector2} this vector
	 * @since 3.4.0
	 */
	public DynVector2 inverseRotate(Rotation rotation, DynVector2 point) {
		return this.inverseRotate(rotation, point.x, point.y);
	}
	
	/**
	 * Projects this {@link DynVector2} onto the given {@link DynVector2}.
	 * @param vector the {@link DynVector2}
	 * @return {@link DynVector2} the projected {@link DynVector2}
	 */
	public DynVector2 project(DynVector2 vector) {
		double dotProd = this.dot(vector);
		double denominator = vector.dot(vector);
		if (denominator <= Epsilon.E) return new DynVector2();
		denominator = dotProd / denominator;
		return new DynVector2(denominator * vector.x, denominator * vector.y);
	}

	/**
	 * Returns the right-handed normal of this vector.
	 * @return {@link DynVector2} the right hand orthogonal {@link DynVector2}
	 */
	public DynVector2 getRightHandOrthogonalVector() {
		return new DynVector2(-this.y, this.x);
	}
	
	/**
	 * Sets this vector to the right-handed normal of this vector.
	 * @return {@link DynVector2} this vector
	 * @see #getRightHandOrthogonalVector()
	 */
	public DynVector2 right() {
		double temp = this.x;
		this.x = -this.y;
		this.y = temp;
		return this;
	}
	
	/**
	 * Returns the left-handed normal of this vector.
	 * @return {@link DynVector2} the left hand orthogonal {@link DynVector2}
	 */
	public DynVector2 getLeftHandOrthogonalVector() {
		return new DynVector2(this.y, -this.x);
	}
	
	/**
	 * Sets this vector to the left-handed normal of this vector.
	 * @return {@link DynVector2} this vector
	 * @see #getLeftHandOrthogonalVector()
	 */
	public DynVector2 left() {
		double temp = this.x;
		this.x = this.y;
		this.y = -temp;
		return this;
	}

	/**
	 * Returns a unit {@link DynVector2} of this {@link DynVector2}.
	 * <p>
	 * This method requires the length of this {@link DynVector2} is not zero.
	 * @return {@link DynVector2}
	 */
	public DynVector2 getNormalized() {
		double magnitude = this.getMagnitude();
		if (magnitude <= Epsilon.E) return new DynVector2();
		magnitude = 1.0 / magnitude;
		return new DynVector2(this.x * magnitude, this.y * magnitude);
	}
	
	/**
	 * Converts this {@link DynVector2} into a unit {@link DynVector2} and returns
	 * the magnitude before normalization.
	 * <p>
	 * This method requires the length of this {@link DynVector2} is not zero.
	 * @return double
	 */
	public double normalize() {
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
		if (magnitude <= Epsilon.E) return 0;
		double m = 1.0 / magnitude;
		this.x *= m;
		this.y *= m;
		//return 1.0 / m;
		return magnitude;
	}

	/**
	 * Returns the smallest angle between the given {@link DynVector2}s.
	 * <p>
	 * Returns the angle in radians in the range -&pi; to &pi;.
	 * @param vector the {@link DynVector2}
	 * @return angle in radians [-&pi;, &pi;]
	 */
	public double getAngleBetween(DynVector2 vector) {
		double a = Math.atan2(vector.y, vector.x) - Math.atan2(this.y, this.x);
		if (a > Math.PI) return a - Geometry.TWO_PI;
		if (a < -Math.PI) return a + Geometry.TWO_PI;
		return a;
	}
	
	/**
	 * Returns the smallest angle between the given {@link DynVector2} and the given angle.
	 * <p>
	 * Returns the angle in radians in the range -&pi; to &pi;.
	 * @param otherAngle the angle. Must be in the range -&pi; to &pi;
	 * @return angle in radians [-&pi;, &pi;]
	 * @since 3.4.0
	 */
	public double getAngleBetween(double otherAngle) {
		double a = otherAngle - Math.atan2(this.y, this.x);
		if (a > Math.PI) return a - Geometry.TWO_PI;
		if (a < -Math.PI) return a + Geometry.TWO_PI;
		return a;
	}
}
