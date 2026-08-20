/*
* GNU GPL v3 License
 *
 * Copyright 2019 Concetta D'Amato
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geoframe.geoet.core.rootdensity;

import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.core.data.InputTimeSeries;

/**
 * The stressedETs abstract class.
 * 
 * @author Concetta D'Amato
 */

public abstract class RootDensity {

	protected ProblemQuantities variables;
	protected InputTimeSeries input;

	public RootDensity(ProblemQuantities variables, InputTimeSeries input) {
		this.variables = variables;
		this.input = input;
	}

	public abstract double[] computeRootDensity(double zRef);
}
