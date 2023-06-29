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
package it.geoframe.blogspot.geoet.rootdensity.solver;

import it.geoframe.blogspot.geoet.rootdensity.methods.*;

import java.util.ArrayList;
import java.util.Arrays;

import it.geoframe.blogspot.geoet.data.*;
import it.geoframe.blogspot.geoet.inout.*;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
//import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;

@Description("This class compute the root density in each control volume")
@Documentation("")
@Author(name = "Concetta D'Amato", contact = "concetta.damato@unitn.it")


public class RootDensitySolverMain {
	
	
	@Description("It is needed to iterate on the date")
	int step;
	
	@Description("Root Density can be evaluated in different way"
		    + " The same of the IC--> CostantMethod"
		    + " Costant growth with the depth--> CostantGrowthMetod"
		    + " Linear growth according to a costant--> LinearGrowthMetod"
		    + " Exponential growth with the depth--> ExponentialGrowthMetod")
	@In
	public String rootDensityModel = "CostantMethod";
	
	@In
	public boolean  doProcess7;
	@Out
	public boolean  doProcess8;
	
	@Description("Vector of root density")
	@Out
	@Unit("-")
	public double [] defRootDensity;
	
	
	/////////////////////////////////////////////////////////////////////////////
	
	
	@Description("Object dealing with rootdensity in each control volume of the domain")
	RootDensity rootDensity;
	
	private ProblemQuantities variables;
	private InputTimeSeries input;

	@Execute
	public void solve() {
		System.out.print("\n\nStart RootDensitySolverMain");
		
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		if(step==0){
			variables.NUM_CONTROL_VOLUMES = input.z.length;
			variables.totalDepth = input.z[variables.NUM_CONTROL_VOLUMES -1];
			
			variables.rootDensity = new double [variables.NUM_CONTROL_VOLUMES -1];
		
			
			RootDensityFactory rootDensityFactory= new RootDensityFactory();
			rootDensity = rootDensityFactory.createRootDensity(rootDensityModel);
			
			
			
		}	
		
		variables.zR = variables.totalDepth + variables.rootDepth;

		variables.rootDensity = rootDensity.computeRootDensity(variables.zR);
		
		defRootDensity = variables.rootDensity;
		
		System.out.print("\nEnd RootDensitySolverMain");

		
		//System.out.println("z = "+Arrays.toString(z));
		//System.out.println("\n\nStressedET  = "+ StressedET);
		
		System.out.println("root density  = "+Arrays.toString(variables.rootDensity));
		//System.out.println("\ng  = "+Arrays.toString(input.g));
		//System.out.println("\n\nsumRootWaterStress  = "+ variables.sumRootWaterStress);
		//System.out.println("\n\ntranspirations  = "+ Arrays.toString(variables.transpirations));
		
		
		
		step++;
		variables.step=step;
		
	}
}
