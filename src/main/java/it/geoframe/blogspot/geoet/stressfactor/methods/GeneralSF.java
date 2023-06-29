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

import oms3.annotations.Author;
import oms3.annotations.License;

/**
 * The stress factor abstract class.
 * @author Concetta D'Amato
 */

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public abstract class GeneralSF {
	
	public double[] z;   			// z coordinate read from grid NetCDF file
	public double[] deltaZ; 		// Vector containing the length of each control volume
	public int NUM_CONTROL_VOLUMES; // Number of control volume for domain discetrization
	public double totalDepth;		// Depth of the colum of soil
	public double n; 				// n counts the number of control volumes
	public double G; 				// Representative stress factor
	public double[] Gn;				// Vector containing G and n
		
	
	/**
	 * General constructor used to pass the values of variables
	 */
	
	public GeneralSF (double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES,double totalDepth) {
		this.z  = z;
		this.deltaZ = deltaZ;
		this.NUM_CONTROL_VOLUMES = NUM_CONTROL_VOLUMES;  
		this.totalDepth = totalDepth;
		n= 0;
		G = 0;
		Gn = new double [2];
		}
	
	/*
	 * This method compute the representative stress factor given the values of stress factor for each control volumes
	 * @param g, @param etaRef, @param zRef
	 * 
	 * @return G
	 */
	
	public abstract double [] computeRepresentativeStressFactor (double[]g, double etaRef, double zRef);
	
	
}