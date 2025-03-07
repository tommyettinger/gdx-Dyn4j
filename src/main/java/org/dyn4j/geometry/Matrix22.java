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

import com.badlogic.gdx.utils.NumberUtils;
import org.dyn4j.Copyable;
import org.dyn4j.Epsilon;
import org.dyn4j.exception.ArgumentNullException;

/**
 * Represents a 2x2 Matrix.
 * <p>
 * Used to solve 2x2 systems of equations.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 */
public class Matrix22 implements Copyable<Matrix22> {
	/** The element at 0,0 */
	public double m00;
	
	/** The element at 0,1 */
	public double m01;
	
	/** The element at 1,0 */
	public double m10;
	
	/** The element at 1,1 */
	public double m11;
	
	/**
	 * Default constructor.
	 */
	public Matrix22() {}
	
	/**
	 * Full constructor.
	 * @param m00 the element at 0,0
	 * @param m01 the element at 0,1
	 * @param m10 the element at 1,0
	 * @param m11 the element at 1,1
	 */
	public Matrix22(double m00, double m01, double m10, double m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The given array should be in the same order as the 
	 * {@link #Matrix22(double, double, double, double)} constructor.
	 * @param values the values array
	 * @throws NullPointerException if values is null
	 * @throws IllegalArgumentException if the length of values is not 4
	 */
	public Matrix22(double[] values) {
		if (values == null) 
			throw new ArgumentNullException("values");
		
		if (values.length != 4) 
			throw new IndexOutOfBoundsException("The values array must have exactly 4 elements");
		
		this.m00 = values[0];
		this.m01 = values[1];
		this.m10 = values[2];
		this.m11 = values[3];
	}
	
	/**
	 * Copy constructor.
	 * @param matrix the {@link Matrix22} to copy
	 */
	public Matrix22(Matrix22 matrix) {
		this.m00 = matrix.m00; this.m01 = matrix.m01;
		this.m10 = matrix.m10; this.m11 = matrix.m11;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	public Matrix22 copy() {
		return new Matrix22(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = NumberUtils.doubleToLongBits(m00);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = NumberUtils.doubleToLongBits(m01);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = NumberUtils.doubleToLongBits(m10);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = NumberUtils.doubleToLongBits(m11);
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
		if (obj instanceof Matrix22) {
			Matrix22 other = (Matrix22) obj;
			if (other.m00 == this.m00
			 && other.m01 == this.m01
			 && other.m10 == this.m10
			 && other.m11 == this.m11) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.m00).append(" ").append(this.m01).append("][")
		.append(this.m10).append(" ").append(this.m11).append("]");
		return sb.toString();
	}
	
	/**
	 * Adds the given {@link Matrix22} to this {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this + m
	 * </pre>
	 * @param matrix the {@link Matrix22} to add
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 add(Matrix22 matrix) {
		this.m00 += matrix.m00;
		this.m01 += matrix.m01;
		this.m10 += matrix.m10;
		this.m11 += matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the sum of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this + m
	 * </pre>
	 * @param matrix the {@link Matrix22} to add
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 sum(Matrix22 matrix) {
		// make a copy of this matrix and perform the addition
		return this.copy().add(matrix);
	}
	
	/**
	 * Subtracts the given {@link Matrix22} from this {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this - m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 subtract(Matrix22 matrix) {
		this.m00 -= matrix.m00;
		this.m01 -= matrix.m01;
		this.m10 -= matrix.m10;
		this.m11 -= matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the difference of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this - m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 difference(Matrix22 matrix) {
		// make a copy of this matrix and perform the subtraction
		return this.copy().subtract(matrix);
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given matrix {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this * m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 multiply(Matrix22 matrix) {
		double m00 = this.m00;
		double m01 = this.m01;
		double m10 = this.m10;
		double m11 = this.m11;
		this.m00 = m00 * matrix.m00 + m01 * matrix.m10;
		this.m01 = m00 * matrix.m01 + m01 * matrix.m11;
		this.m10 = m10 * matrix.m00 + m11 * matrix.m10;
		this.m11 = m10 * matrix.m01 + m11 * matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the product of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this * m
	 * </pre>
	 * @param matrix the {@link Matrix22} to multiply
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 product(Matrix22 matrix) {
		// make a copy of this matrix and perform the multiplication
		return this.copy().multiply(matrix);
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given {@link DynVector2} and
	 * places the result in the given {@link DynVector2}.
	 * <pre>
	 * v = this * v
	 * </pre>
	 * @param vector the {@link DynVector2} to multiply
	 * @return {@link DynVector2} the vector result
	 */
	public DynVector2 multiply(DynVector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m01 * y;
		vector.y = this.m10 * x + this.m11 * y;
		return vector;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given {@link DynVector2} returning
	 * the result in a new {@link DynVector2}.
	 * <pre>
	 * r = this * v
	 * </pre>
	 * @param vector the {@link DynVector2} to multiply
	 * @return {@link DynVector2} the vector result
	 */
	public DynVector2 product(DynVector2 vector) {
		return this.multiply(vector.copy());
	}
	
	/**
	 * Multiplies the given {@link DynVector2} by this {@link Matrix22} and
	 * places the result in the given {@link DynVector2}.
	 * <p style="white-space: pre;"> v = v<sup>T</sup> * this</p>
	 * @param vector the {@link DynVector2} to multiply
	 * @return {@link DynVector2} the vector result
	 */
	public DynVector2 multiplyT(DynVector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m10 * y;
		vector.y = this.m01 * x + this.m11 * y;
		return vector;
	}
	
	/**
	 * Multiplies the given {@link DynVector2} by this {@link Matrix22} returning
	 * the result in a new {@link DynVector2}.
	 * <p style="white-space: pre;"> r = v<sup>T</sup> * this</p>
	 * @param vector the {@link DynVector2} to multiply
	 * @return {@link DynVector2} the vector result
	 */
	public DynVector2 productT(DynVector2 vector) {
		return this.multiplyT(vector.copy());
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given scalar and places
	 * the result in this {@link Matrix22}.
	 * <pre>
	 * this = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 multiply(double scalar) {
		this.m00 *= scalar;
		this.m01 *= scalar;
		this.m10 *= scalar;
		this.m11 *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given scalar returning a
	 * new {@link Matrix22} containing the result.
	 * <pre>
	 * r = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 product(double scalar) {
		// make a copy of this matrix and perform the scalar multiplication
		return this.copy().multiply(scalar);
	}
	
	/**
	 * Sets this {@link Matrix22} to an identity {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 identity() {
		this.m00 = 1; this.m01 = 0;
		this.m10 = 0; this.m11 = 1;
		return this;
	}
	
	/**
	 * Sets this {@link Matrix22} to the transpose of this {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 transpose() {
		double m = this.m01;
		this.m01 = this.m10;
		this.m10 = m;
		return this;
	}
	
	/**
	 * Returns the the transpose of this {@link Matrix22} in a new {@link Matrix22}.
	 * @return {@link Matrix22} a new matrix contianing the transpose
	 */
	public Matrix22 getTranspose() {
		return this.copy().transpose();
	}
	
	/**
	 * Returns the determinant of this {@link Matrix22}.
	 * @return double
	 */
	public double determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}
	
	/**
	 * Performs the inverse of this {@link Matrix22} and places the
	 * result in this {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 invert() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) > Epsilon.E) {
			det = 1.0 / det;
		} else {
			det = 0.0;
		}
		double a = this.m00;
		double b = this.m01;
		double c = this.m10;
		double d = this.m11;
		this.m00 =  det * d;
		this.m01 = -det * b;
		this.m10 = -det * c;
		this.m11 =  det * a;
		return this;
	}
	
	/**
	 * Performs the inverse of this {@link Matrix22} and places the
	 * result in the given {@link Matrix22}.
	 * @param dest the destination for the inverse
	 * @since 4.0.0
	 */
	public void invert(Matrix22 dest) {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) > Epsilon.E) {
			det = 1.0 / det;
		} else {
			det = 0.0;
		}
		dest.m00 =  det * this.m11;
		dest.m01 = -det * this.m01;
		dest.m10 = -det * this.m10;
		dest.m11 =  det * this.m00;
	}
	
	/**
	 * Returns a new {@link Matrix22} containing the inverse of this {@link Matrix22}.
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 getInverse() {
		// make a copy of this matrix and perform the inversion
		return this.copy().invert();
	}
	
	/**
	 * Solves the system of linear equations:
	 * <p style="white-space: pre;"> Ax = b
	 * Multiply by A<sup>-1</sup> on both sides
	 * x = A<sup>-1</sup>b</p>
	 * @param b the b {@link DynVector2}
	 * @return {@link DynVector2} the x vector
	 */
	public DynVector2 solve(DynVector2 b) {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) > Epsilon.E) {
			det = 1.0 / det;
		} else {
			det = 0.0;
		}
		DynVector2 r = new DynVector2();
		r.x = det * (this.m11 * b.x - this.m01 * b.y);
		r.y = det * (this.m00 * b.y - this.m10 * b.x);
		return r;
	}
	
	/**
	 * Returns the max-norm of this matrix.
	 * @return double
	 */
	public double normMax() {
		// just the max of the absolute values
		return Math.max(
				Math.abs(this.m00), Math.max(
						Math.abs(this.m01), Math.max(
								Math.abs(this.m10), Math.abs(this.m11))));
	}

	/**
	 * Returns the infinity-norm of this matrix.
	 * @return double
	 */
	public double normInfinity() {
		// the max of the sum of the absolute values of the rows
		double row1 = Math.abs(this.m00) + Math.abs(this.m01);
		double row2 = Math.abs(this.m10) + Math.abs(this.m11);
		return Math.max(row1, row2);		
	}

	/**
	 * Returns the 1-norm of this matrix.
	 * @return double
	 */
	public double norm1() {
		// the max of the sum of the absolute values of the columns
		double col1 = Math.abs(this.m00) + Math.abs(this.m10);
		double col2 = Math.abs(this.m01) + Math.abs(this.m11);
		return Math.max(col1, col2);
	}
	
	/**
	 * Returns the frobenius-norm of this matrix.
	 * @return double
	 */
	public double normFrobenius() {
		// the square root of the sum of all the elements squared
		return Math.sqrt(
			this.m00 * this.m00 +
			this.m10 * this.m10 +
			this.m01 * this.m01 +
			this.m11 * this.m11);
	}
}
