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
package it.geoframe.blogspot.geoet.stressfactor.methods;
/**
 * The stress factor abstract class.
 * @author Concetta D'Amato
 */

public abstract class WaterStressFactor {
	
	public double[] thetaWp;  		// wilting point
	public double[] thetaFc;  		// field capacity
	public double[] z;   			// z coordinate read from grid NetCDF file
	public double[] deltaZ;  		// Vector containing the length of each control volume
	public double etaR;    			// Depth of the root
	public double etaE;    			// Depth of the Evaporation layer
	public int NUM_CONTROL_VOLUMES; // Number of control volume for domain discetrization
	public double totalDepth;
	public double zR;    			// Depth of the root from the bottom 
	public double zE;    			// Depth of the evaporation layer from the bottom
	public int[] ID; 
	
	public double[] stressFactor; 	// stress factor
	/**
	 * General constructor used to pass the values of variables
	 */
	
	public WaterStressFactor (double[] thetaWp, double[] thetaFc, int[] ID, double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES, double totalDepth) {
	
		this.thetaWp = thetaWp;
		this.thetaFc = thetaFc;
		this.z  = z;
		this.deltaZ = deltaZ;
		this.NUM_CONTROL_VOLUMES = NUM_CONTROL_VOLUMES;  
		this.totalDepth = totalDepth;
		
		this.ID = ID;
		
		stressFactor = new double[NUM_CONTROL_VOLUMES-1];
	}
	
	/**
	 * This method compute the stress factor given the theta values for each control volumes
	 * @param theta
	 * @return stress factor
	 */
	
	public abstract double[] computeStressFactor (double[]theta, double zR, double zE);		
}