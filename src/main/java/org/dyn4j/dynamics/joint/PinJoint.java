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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.DynVector2;

/**
 * Implementation of a pin joint.
 * <p>
 * A pin joint is a joint that pins a body to a specified world space point.
 * This joint will attempt to place the given anchor point at the target 
 * position.
 * <p>
 * NOTE: The anchor point does not have to be within the bounds of the body.
 * <p>
 * By default the target position will be the given world space anchor. Use 
 * the {@link #setTarget(DynVector2)} method to set a different target.
 * <p>
 * By default the pin joint is setup with a linear spring-damper with a 
 * maximum force. The defaults are a frequency of 8.0, damping ratio of 0.3
 * and a maximum force of 1000.0. 
 * <p>
 * You can disable the spring-damper using the 
 * {@link #setSpringEnabled(boolean)} method. This turns the joint into
 * an unary {@link MotorJoint}.  You can use the 
 * {@link #setCorrectionFactor(double)} method to set how quickly the
 * constraint violation should be resolved and you can set the maximum
 * force the correction should apply using the 
 * {@link #setMaximumCorrectionForce(double)}. Both of these settings are
 * only relevant when the spring-damper is disabled.
 * <p>
 * The {@link #getAnchor()} method returns the anchor point on body in world
 * space.  The {@link #getTarget()} returns the target point in world space.
 * <p>
 * Renamed from MouseJoint in 3.2.0. Can function without a spring-damper as
 * of 5.0.0.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Pin_Joint" target="_blank">Documentation</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class PinJoint<T extends PhysicsBody> extends AbstractSingleBodyJoint<T> implements LinearSpringJoint, SingleBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The world space target point */
	protected final DynVector2 target;
	
	/** The local anchor point for the body */
	protected final DynVector2 localAnchor;
	
	// spring-damper constraint
	
	/** True if the spring is enabled */
	protected boolean springEnabled;
	
	/** The current spring mode */
	protected int springMode;
	
	/** The oscillation frequency in hz */
	protected double springFrequency;

	/** The stiffness (k) of the spring */
	protected double springStiffness;
	
	/** True if the spring's damper is enabled */
	protected boolean springDamperEnabled;
	
	/** The damping ratio */
	protected double springDampingRatio;
	
	/** True if the spring maximum force is enabled */
	protected boolean springMaximumForceEnabled;
	
	/** The maximum force the spring can apply */
	protected double springMaximumForce;
	
	// "motor" constraint
	
	/** The correction factor in the range [0, 1] */
	protected double correctionFactor;
	
	/** The maximum force the constraint can apply */
	protected double correctionMaximumForce;
	
	// current state

	/** The world-space vector from the local center to the local anchor point */
	private DynVector2 r;
	
	/** The damping coefficient of the spring-damper */
	private double damping;

	/** The bias for adding work to the constraint (simulating a spring) */
	private DynVector2 bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	/** The constraint mass; K = J * Minv * Jtrans */
	private final Matrix22 K;

	/** The calculated linear error in the target distance */
	private DynVector2 linearError;
	
	// output
	
	/** The impulse applied to the body to satisfy the constraint */
	private DynVector2 impulse;
	
	/**
	 * Full constructor.
	 * @param body the body to attach the joint to
	 * @param anchor the anchor point on the body
	 * @throws NullPointerException if body or anchor is null
	 */
	public PinJoint(T body, DynVector2 anchor) {
		super(body);
		
		// check for a null anchor
		if (anchor == null) 
			throw new ArgumentNullException("anchor");
		
		this.target = anchor.copy();
		this.localAnchor = body.getLocalPoint(anchor);
		
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = true;
		this.springFrequency = 8.0;
		this.springStiffness = 0.0;
		this.springDamperEnabled = true;
		this.springDampingRatio = 0.3;
		this.springMaximumForceEnabled = true;
		this.springMaximumForce = 1000.0;
		
		// "motor" joint
		this.correctionFactor = 0.3;
		this.correctionMaximumForce = 1000.0;
		
		// initialize
		this.damping = 0.0;
		this.gamma = 0.0;
		this.bias = new DynVector2();
		this.K = new Matrix22();
		
		this.impulse = new DynVector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PinJoint[").append(super.toString())
		  .append("|Target=").append(this.target)
		  .append("|Anchor=").append(this.localAnchor)
		  .append("|Frequency=").append(this.springFrequency)
		  .append("|DampingRatio=").append(this.springDampingRatio)
		  .append("|MaximumForce=").append(this.springMaximumForce)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
		T body = this.body;
		Transform transform = body.getTransform();
		Mass mass = this.body.getMass();
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// compute the r vector
		this.r = transform.getTransformedR(body.getLocalCenter().to(this.localAnchor));
		
		// compute the K inverse matrix (point-to-point constraint)
		this.K.m00 = invM + this.r.y * this.r.y * invI;
		this.K.m01 = -invI * this.r.x * this.r.y; 
		this.K.m10 = this.K.m01;
		this.K.m11 = invM + this.r.x * this.r.x * invI;
		
		// recompute spring reduced mass (m), stiffness (k), and damping (d)
		// since frequency, dampingRatio, or the masses of the joined bodies
		// could change
		if (this.springEnabled) {
			this.updateSpringCoefficients();
			
			// get the delta time
			double dt = step.getDeltaTime();
			
			// compute the CIM
			this.gamma = getConstraintImpulseMixing(dt, this.springStiffness, this.damping);
			
			// compute the ERP
			double erp = getErrorReductionParameter(dt, this.springStiffness, this.damping);
			
			// compute the bias = ERP where ERP = hk / (hk + d)
			this.bias = body.getWorldCenter().add(this.r).difference(this.target);
			this.bias.multiply(erp);

			// apply the spring
			this.K.m00 += this.gamma;
			this.K.m11 += this.gamma;
		} else {
			// otherwise enforce a "motor" constraint
			DynVector2 bp = this.r.sum(this.body.getWorldCenter());
			// the linear error is the distance along the x/y 
			// from the local anchor to the target
			this.linearError = this.target.difference(bp);
		}
		
		// warm start
		if (settings.isWarmStartingEnabled()) {
			this.impulse.multiply(step.getDeltaTimeRatio());
			
			body.getLinearVelocity().add(this.impulse.product(invM));
			body.setAngularVelocity(body.getAngularVelocity() + invI * this.r.cross(this.impulse));
		} else {
			this.impulse.zero();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {
		T body = this.body;
		Mass mass = this.body.getMass();
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		double invdt = step.getInverseDeltaTime();
		double dt = step.getDeltaTime();
		
		// compute the velocity
		DynVector2 rv = this.r.cross(body.getAngularVelocity()).add(body.getLinearVelocity());
		
		if (this.springEnabled) {
			// soft point-to-point joint
			
			// compute Jv + b
			DynVector2 jvb = rv;
			jvb.add(this.bias);
			jvb.add(this.impulse.product(this.gamma));
			jvb.negate();
			DynVector2 J = this.K.solve(jvb);
			
			// clamp the maximum force
			if (this.springEnabled && this.springMaximumForceEnabled) {
				// clamp using the maximum force
				DynVector2 currentAccumulatedImpulse = this.impulse.copy();
				this.impulse.add(J);
				double maxImpulse = step.getDeltaTime() * this.springMaximumForce;
				if (this.impulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
					this.impulse.normalize();
					this.impulse.multiply(maxImpulse);
				}
				J = this.impulse.difference(currentAccumulatedImpulse);
			} else {
				this.impulse.add(J);
			}
			
			body.getLinearVelocity().add(J.product(invM));
			body.setAngularVelocity(body.getAngularVelocity() + invI * this.r.cross(J));
		} else {
			// motor joint
			
			// the "bias" for the motor constraint is the correction factor and linear error
			DynVector2 pivotV = rv.getNegative();
			pivotV.add(this.linearError.product(this.correctionFactor * invdt));
			DynVector2 stepImpulse = this.K.solve(pivotV);
			
			// clamp by the maxforce
			DynVector2 currentAccumulatedImpulse = this.impulse.copy();
			this.impulse.add(stepImpulse);
			double maxImpulse = this.correctionMaximumForce * dt;
			if (this.impulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
				this.impulse.normalize();
				this.impulse.multiply(maxImpulse);
			}
			stepImpulse = this.impulse.difference(currentAccumulatedImpulse);
			
			this.body.getLinearVelocity().add(stepImpulse.product(invM));
			this.body.setAngularVelocity(this.body.getAngularVelocity() + invI * this.r.cross(stepImpulse));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		// nothing to do here if spring is enabled
		return true;
	}

	/**
	 * Computes the spring coefficients from the current state of the joint.
	 * <p>
	 * This method is intended to set the springStiffness OR springFrequency and
	 * damping for use during constraint solving.
	 */
	protected void updateSpringCoefficients() {
		Mass mass = this.body.getMass();
		double m = mass.getMass();
		
		// check if the mass is zero
		if (m <= Epsilon.E) {
			// if the mass is zero, use the inertia
			// this will allow the pin joint to work with
			// all mass types other than INFINITE
			m = mass.getInertia();
		}
		
		double nf = 0.0;
		
		if (this.springMode == SPRING_MODE_FREQUENCY) {
			// compute the stiffness based on the frequency
			nf = getNaturalFrequency(this.springFrequency);
			this.springStiffness = getSpringStiffness(m, nf);
		} else if (this.springMode == SPRING_MODE_STIFFNESS) {
			// compute the frequency based on the stiffness
			nf = getNaturalFrequency(this.springStiffness, m);
			this.springFrequency = getFrequency(nf);
		}
		
		if (this.springDamperEnabled) {
			this.damping = getSpringDampingCoefficient(m, nf, this.springDampingRatio);
		} else {
			this.damping = 0.0;
		}
	}
	
	/**
	 * Returns the anchor point on the body in world space.
	 * @return Vector2
	 */
	public DynVector2 getAnchor() {
		return this.body.getWorldPoint(this.localAnchor);
	}

	/**
	 * Returns the target point in world coordinates.
	 * @param target the target point
	 * @throws NullPointerException if target is null
	 */
	public void setTarget(DynVector2 target) {
		// make sure the target is non null
		if (target == null) 
			throw new ArgumentNullException("target");
		
		// only wake the body if the target has changed
		if (!target.equals(this.target)) {
			// wake up the body
			this.body.setAtRest(false);
			// set the new target
			this.target.set(target);
		}
	}
	
	/**
	 * Returns the target point in world coordinates
	 * @return {@link DynVector2}
	 */
	public DynVector2 getTarget() {
		return this.target;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public DynVector2 getReactionForce(double invdt) {
		return this.impulse.product(invdt);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * Always returns zero.
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isCollisionAllowed()
	 */
	@Override
	public boolean isCollisionAllowed() {
		// never allow collisions since there is only one body attached
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(DynVector2 shift) {
		// the target point must be moved
		this.target.add(shift);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringDampingRatio()
	 */
	@Override
	public double getSpringDampingRatio() {
		return this.springDampingRatio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMode()
	 */
	@Override
	public int getSpringMode() {
		return this.springMode;
	}
	
	/**
	 * Returns the correction factor.
	 * @return double
	 * @since 5.0.0
	 */
	public double getCorrectionFactor() {
		return this.correctionFactor;
	}
	
	/**
	 * Sets the correction factor.
	 * <p>
	 * The correction factor controls the rate at which the bodies perform the
	 * desired actions.  The default is 0.3.
	 * <p>
	 * A value of zero means that the bodies do not perform any action.
	 * @param correctionFactor the correction factor in the range [0, 1]
	 * @since 5.0.0
	 */
	public void setCorrectionFactor(double correctionFactor) {
		if (correctionFactor < 0.0) 
			throw new ValueOutOfRangeException("correctionFactor", correctionFactor, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (correctionFactor > 1.0) 
			throw new ValueOutOfRangeException("correctionFactor", correctionFactor, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, 1.0);
		
		this.correctionFactor = correctionFactor;
	}
	
	/**
	 * Returns the maximum correction force this constraint will apply in newtons.
	 * @return double
	 * @since 5.0.0
	 */
	public double getMaximumCorrectionForce() {
		return this.correctionMaximumForce;
	}
	
	/**
	 * Sets the maximum correction force this constraint will apply in newtons.
	 * @param maximumForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxForce is less than zero
	 * @since 5.0.0
	 */
	public void setMaximumCorrectionForce(double maximumForce) {
		// make sure its greater than or equal to zero
		if (maximumForce < 0.0) 
			throw new ValueOutOfRangeException("maximumForce", maximumForce, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// set the max
		this.correctionMaximumForce = maximumForce;
	}

	/**
	 * Returns the correction force from the last step.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public double getCorrectionForce(double invdt) {
		if (!this.springEnabled) {
			return this.impulse.getMagnitude() * invdt;
		}
		return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDampingRatio(double)
	 */
	@Override
	public void setSpringDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio < 0.0) 
			throw new ValueOutOfRangeException("dampingRatio", dampingRatio, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (dampingRatio > 1.0) 
			throw new ValueOutOfRangeException("dampingRatio", dampingRatio, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, 1.0);
		
		// did it change?
		if (this.springDampingRatio != dampingRatio) {
			// set the damping ratio
			this.springDampingRatio = dampingRatio;
			// only wake if the damper would be applied
			if (this.springEnabled && this.springDamperEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringFrequency()
	 */
	@Override
	public double getSpringFrequency() {
		return this.springFrequency;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringStiffness()
	 */
	@Override
	public double getSpringStiffness() {
		return this.springStiffness;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringFrequency(double)
	 */
	@Override
	public void setSpringFrequency(double frequency) {
		// check for valid value
		if (frequency < 0)
			throw new ValueOutOfRangeException("frequency", frequency, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// set the spring mode
		this.springMode = SPRING_MODE_FREQUENCY;
		// check for change
		if (this.springFrequency != frequency) {
			// make the change
			this.springFrequency = frequency;
			// check if the spring is enabled
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringStiffness(double)
	 */
	@Override
	public void setSpringStiffness(double stiffness) {
		// check for valid value
		if (stiffness < 0)
			throw new ValueOutOfRangeException("stiffness", stiffness, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// set the spring mode
		this.springMode = SPRING_MODE_STIFFNESS;
		// only update if necessary
		if (this.springStiffness != stiffness) {
			this.springStiffness = stiffness;
			// wake up the bodies
			if (this.springEnabled) {
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMaximumForce()
	 */
	@Override
	public double getMaximumSpringForce() {
		return this.springMaximumForce;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForce(double)
	 */
	@Override
	public void setMaximumSpringForce(double maximum) {
		// check for valid value
		if (maximum < 0) 
			throw new ValueOutOfRangeException("maximum", maximum, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// check if changed
		if (this.springMaximumForce != maximum) {
			this.springMaximumForce = maximum;
			// wake up the bodies
			if (this.springEnabled && this.springMaximumForceEnabled) {
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringMaximumForceEnabled()
	 */
	@Override
	public boolean isMaximumSpringForceEnabled() {
		return this.springMaximumForceEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForceEnabled(boolean)
	 */
	@Override
	public void setMaximumSpringForceEnabled(boolean enabled) {
		if (this.springMaximumForceEnabled != enabled) {
			this.springMaximumForceEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringEnabled()
	 */
	public boolean isSpringEnabled() {
		return this.springEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringEnabled(boolean)
	 */
	@Override
	public void setSpringEnabled(boolean enabled) {
		if (this.springEnabled != enabled) {
			// update the flag
			this.springEnabled = enabled;
			// wake the bodies
			this.body.setAtRest(false);
			// clear the impulse from the last step
			this.impulse.zero();
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringDamperEnabled()
	 */
	public boolean isSpringDamperEnabled() {
		return this.springDamperEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDamperEnabled(boolean)
	 */
	@Override
	public void setSpringDamperEnabled(boolean enabled) {
		if (this.springDamperEnabled != enabled) {
			// update the flag
			this.springDamperEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringForce(double)
	 */
	@Override
	public double getSpringForce(double invdt) {
		if (this.springEnabled) {
			return this.impulse.getMagnitude() * invdt;
		}
		return 0.0;
	}
}
