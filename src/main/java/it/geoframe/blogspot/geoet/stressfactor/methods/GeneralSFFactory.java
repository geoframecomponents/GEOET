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
 * A simple design factory for creating a StressFactor object
 * @author Concetta D'Amato
 */
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class GeneralSFFactory {
	/**
	 * Creates a new stress factor object.
	 * @param type name of the Stress Factor model
	 * @param z 
	 * @param zR depth of the root
	 * @param dx vector containing the length of each control volume
	 * @param NUM_CONTROL_VOLUMES number of control volume for domain discetrization
	 * @return stressFactor G
	 */

	public GeneralSF createRepresentativeStressFactor (String type, double[] z, double[] deltaZ, int NUM_CONTROL_VOLUMES, double totalDepth) 
	{
		GeneralSF generalSF = null;
		if(type.equalsIgnoreCase("AverageMethod") || type.equalsIgnoreCase("AverageMethod")){
			generalSF = new AverageSF(z,deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}
		else if(type.equalsIgnoreCase("SizeWeightedMethod") || type.equalsIgnoreCase("SizeWeightedMethod")){
			generalSF = new SizeWeightedSF(z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);}
		return generalSF;
	}	
}





