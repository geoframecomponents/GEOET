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
package it.geoframe.blogspot.geoet.rootdensity.methods;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

/**
 * 
 * @author Concetta D'Amato
 */


public class ExponentialGrowthMethod extends RootDensity{

	private ProblemQuantities variables;
	private InputTimeSeries input;
	
	
	public double [] computeRootDensity (double zRef) {
		
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		if(variables.step==0){
			variables.rootDensity = input.rootDensityIC;
		}
		
		else {

		for (int i = 0; i <= variables.NUM_CONTROL_VOLUMES-2; i++) {
			if (input.z[i] >= zRef) {
				variables.rootDensity[i]= variables.rootDensity[i] + input.growthRateRoot * (1-exp(-input.z[i]));
				if (variables.rootDensity[i] > 1) {
					variables.rootDensity[i]= 1;}	
			}
			else{
				variables.rootDensity[i] = 0;}}
		}
		
	return variables.rootDensity.clone();
	}
}
 