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

import static java.lang.Math.pow;

import oms3.annotations.Author;
import oms3.annotations.License;

/**
 * Computation of the stress factor by the linear formulation of Jarvis 1976 
 * @author Concetta D'Amato
 */

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")


public class JarvisWaterVolumeStressFactor extends WaterStressFactor{

	/** General constructor used to pass the value of variables */
	public JarvisWaterVolumeStressFactor (double[] thetaWp, double[] thetaFc,int[] ID, double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES, double totalDepth) {
		super(thetaWp, thetaFc, ID, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}	

	public double[] computeStressFactor (double[] theta, double zR, double zE) {
			
	for (int i = 0; i <= NUM_CONTROL_VOLUMES-2; i++) {

		if(z[i] > zR || z[i] > zE) {
			stressFactor[i]=(theta[i]*deltaZ[i]-thetaWp[ID[i]]*deltaZ[i])/(thetaFc[ID[i]]*deltaZ[i]-thetaWp[ID[i]]*deltaZ[i]);
			stressFactor[i]=((stressFactor[i]>0)?stressFactor[i]:0);				
			stressFactor[i]=((stressFactor[i]<1)?stressFactor[i]:1);
			if (stressFactor[i] <  1 * pow(10,-8)) {
				stressFactor[i] = 0;}
			else {stressFactor[i]=stressFactor[i];}
		} 
		else {stressFactor[i]= 0;}
		
			
	}
	return stressFactor;

	}
}