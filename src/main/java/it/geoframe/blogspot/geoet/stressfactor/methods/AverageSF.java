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


@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

/**
 * Computation of the representaive stress factor as the average of stress factor from control volumes 
 * @author Concetta D'Amato
 */

public class AverageSF extends RepresentativeSF{
	

	/** General constructor used to pass the value of variables */
	public AverageSF (double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES, double totalDepth) {
		super(z,deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}

	
	public double [] computeRepresentativeStressFactor (double[]g, double etaRef,double zRef) {
		n = 0;
		G = 0;
		for (int i = 0; i <= NUM_CONTROL_VOLUMES-2; i++) {
			if (etaRef==0){
				G = 0;
				n = 1;} 
			else if (z[i] > zRef) {
				G = G + g[i];
				n = n + 1;}
			if (G <  1 * pow(10,-8)) {G = 0;} 	
		}
		G = G/n;
		
		if (G > 1) {G = 1;}	
		Gn[0] = G;
		Gn[1] = n;
		
		return Gn.clone();
	
	}
	
}