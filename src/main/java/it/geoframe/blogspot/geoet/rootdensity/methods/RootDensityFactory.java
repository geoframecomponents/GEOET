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


/**
 * A simple design factory for creating a StressedETs objects
 * @author Concetta D'Amato
 */

public class RootDensityFactory {
	/**
	 * Creates a new StressedETs object.
	 * @param type name of the Evaporation or Transpiration splitting model
	 * @param z 
	 * @param zR depth of the root
	 * @param dx vector containing the length of each control volume
	 * @param NUM_CONTROL_VOLUMES number of control volume for domain discetrization
	 * @return stressFactor G
	 */
	
	private ProblemQuantities variables;
	private InputTimeSeries input;

	public RootDensity createRootDensity (String type) 
	{
		this.input = InputTimeSeries.getInstance();
		this.variables = ProblemQuantities.getInstance();
		
		RootDensity rootDensity = null;
		if(type.equalsIgnoreCase("CostantGrowthMethod") || type.equalsIgnoreCase("CostantGrowthMethod")){
			rootDensity = new CostantGrowthMethod();}
		
		else if(type.equalsIgnoreCase("CostantMethod") || type.equalsIgnoreCase("CostantMethod")){
			rootDensity = new CostantMethod();}
		
		else if(type.equalsIgnoreCase("LinearGrowthMethod") || type.equalsIgnoreCase("LinearGrowthMethod")){
			rootDensity = new LinearGrowthMethod();}
		
		else if(type.equalsIgnoreCase("ExponentialGrowthMethod") || type.equalsIgnoreCase("ExponentialGrowthMethod")){
			rootDensity = new ExponentialGrowthMethod();}
		
		return rootDensity;
	}	
}





