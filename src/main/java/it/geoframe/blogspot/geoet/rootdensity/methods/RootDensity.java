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

/**
 * The stressedETs abstract class.
 * @author Concetta D'Amato
 */

public abstract class RootDensity {
	
	//public double[] z;   			// z coordinate read from grid NetCDF file
	//public double zR;    			// Depth of the root from the bottom
	//public double zE;    			// Depth of the evaporation layer from the bottom
	//public double etaR;    			// Depth of the root 
	//public double etaE;    			// Depth of the Evaporation layer
	//public double[] deltaZ; 		// Vector containing the length of each control volume
	//public int NUM_CONTROL_VOLUMES; // Number of control volume for domain discetrization
	//public double totalDepth;		// Depth of the colum of soil
	//public double n; 				// nR counts the number of control volumes
	//public double G; 				// Representative stress factor
	//public double[] Gn;				// Vector containing G and n
	//public double[] fluxRefs;       // vector of flux divided in the control volumes
	
	//private ProblemQuantities variables;
	//private InputData input;
	
	/**
	 * General constructor used to pass the values of variables
	 */
	
	/*public SplittedETs (double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES,double totalDepth) {
		
		this.input = InputData.getInstance();
		this.variables = ProblemQuantities.getInstance();
		
		this.z  = input.z;
		this.deltaZ = input.deltaZ;
		this.NUM_CONTROL_VOLUMES = variables.NUM_CONTROL_VOLUMES;  
		this.totalDepth = totalDepth;
		n= 0;
		G = 0;
		fluxRefs = new double[NUM_CONTROL_VOLUMES-1];
		Gn = new double [2];
		}*/
	
	/*
	 * This method compute the evaporation and transpiration in each control volumes of the whole column of soil given 
	 * the total evaporation and transpiration from the Evapotranspiration Component
	 * @param Gn, @param fluxRef, @param zRef
	 *
	 * @return fluxRefs
	 */

	
	public abstract double [] computeRootDensity (double zRef);
	
	//public abstract double [] computeETs ();
}


