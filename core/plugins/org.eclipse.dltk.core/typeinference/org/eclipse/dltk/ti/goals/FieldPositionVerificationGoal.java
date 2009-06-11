/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.ti.goals;

import org.eclipse.dltk.ti.IContext;

/**
 * Task of this goal is to verify given possible position as a real position,
 * where given field were read or changed.
 * 
 * As result, object of ItemReference or null should be returned.
 */
public class FieldPositionVerificationGoal extends AbstractGoal {

	private final PossiblePosition position;
	private final FieldReferencesGoal goal;

	public FieldPositionVerificationGoal(IContext context,
			FieldReferencesGoal goal, PossiblePosition postion) {
		super(context);
		this.goal = goal;
		this.position = postion;
	}

	public PossiblePosition getPosition() {
		return position;
	}

	public FieldReferencesGoal getGoal() {
		return goal;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldPositionVerificationGoal other = (FieldPositionVerificationGoal) obj;
		if (goal == null) {
			if (other.goal != null)
				return false;
		} else if (!goal.equals(other.goal))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

}
