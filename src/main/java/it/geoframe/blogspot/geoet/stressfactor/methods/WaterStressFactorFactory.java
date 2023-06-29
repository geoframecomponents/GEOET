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

//import computeStressFactor.LinearStressFactor;
/**
 * A simple design factory for creating a StressFactor objects.
 * @author Concetta D'Amato
 */

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class WaterStressFactorFactory {

	/**
	 * Creates a new stress factor object.
	 * @param type name of the Stress Factor model
	 * @param theta_Wp dimensionless wilting point
	 * @param theta_Fc dimensionless field capacity
	 * @param z water content
	 * @param zR depth of the root
	 * @param dx vector containing the length of each control volume
	 * @param NUM_CONTROL_VOLUMES number of control volume for domain discetrization
	 * @return stressFactor g
	 */
	
	public WaterStressFactor createStressFactor (String type, double[] thetaWp, double[] thetaFc, int[] ID, double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES, double totalDepth) {

		WaterStressFactor stressFactor = null;
		if(type.equalsIgnoreCase("LinearStressFactor") || type.equalsIgnoreCase("LinearStressFactor")){
			stressFactor = new JarvisWaterStressFactor(thetaWp, thetaFc, ID, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}
		
		if(type.equalsIgnoreCase("LinearVolumeStressFactor") || type.equalsIgnoreCase("LinearVolumeStressFactor")){
			stressFactor = new JarvisWaterVolumeStressFactor(thetaWp, thetaFc, ID, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}
		
		return stressFactor;
	}

		
}
