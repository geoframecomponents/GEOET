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
package it.geoframe.blogspot.geoet.stressfactor.solver;

import it.geoframe.blogspot.geoet.stressfactor.methods.*;
//import java.util.Arrays;
//import it.geoframe.blogspot.brokergeo.methods.*;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;

@Description("This class is used to connect the Richard model with the evapotranspiration model, calculating the stress factor representative of the domain and the stress factor for each control volumes.")
@Documentation("")
@Author(name = "Concetta D'Amato", contact = "concetta.damato@unitn.it")


public class JarvisWaterStressFactorSolverMain {
	
	@Description("Wilting point")
	@In 
	@Unit ("-")
	public double[] thetaWp;	

	@Description("Field capacity")
	@In 
	@Unit ("-")
	public double[] thetaFc;
	
	@Description("Water content")
	@In 
	@Unit ("-")
	public double[] theta;
	
	@Description("Depth of the root.")
	@In 
	@Unit("m")
	public double etaR;
	
	@Description("Depth of the Evaporation layer.")
	@In 
	@Unit("m")
	public double etaE;
	
	@Description("z coordinate read from grid NetCDF file.")
	@In
	@Unit("m")
	public double[] z;
	
	@Description("Depth of the root from the bottom.")
	@Unit("m")
	public double zR;
	
	@Description("Depth of the evaporation layer from the bottom")
	@Unit("m")
	public double zE;  
	
	@Description("Depth of the domain")
	@In
	@Unit("m")
	public double totalDepth;
	
	@Description("The stressed Evapotranspiration from Schymanski model.")
	@In
	@Unit("mm/s")
	public double StressedET;
	
	@Description("The stressed Transpiration from Prospero model.")
	@In
	@Unit("mm/s")
	public double transpiration;
	
	@Description("The stressed Evaporation from Prospero model.")
	@In
	@Unit("mm/s")
	public double evaporation;
	
	@Description("Vector containing the length of each control volume")
	@In
	@Unit("m")
	public double[] deltaZ;
	
	@Description("Number of control volume for domain discetrization")
	@In
	@Unit ("-")
	public int NUM_CONTROL_VOLUMES;
	
	@Description("ParameterID for the type of soil")
	@In
	@Unit ("-")
	public int[] ID;
	
	@Description("Stress Factor can be evaluated in different way"
			    + " Linear method (Jarvis) --> LinearStressFactor")
	@In
	public String stressFactorModel;
	
	@Description("Representative Stress Factor can be evaluated in different way"
			    + " Average method --> AverageMetod"
			    + " Weighted average method --> SizeWightedMetod")
	@In
	public String representativeStressFactorModel;
	
	@Description("The stress factor for each control volumes")
	@Out
	@Unit("-")
	public double[] g;
	
	@Description("The stress factor representative of the domain")
	@Out
	@Unit("-")
	public double G;
	
	@Description("Representative stress factor in the evaporation layer")
	@Out
	@Unit("-")
	public double GE;
	
	@Description("Vector of G and n, for transpiration and evaporation")
	@Out
	@Unit("-")
	public double[] GnT;
	
	@Description("Vector of G and n, for evaporation")
	@Out
	@Unit("-")
	public double[] GnE;
	
	@Description("The stressed Evapotranspiration for each control volumes")
	@Out
	@Unit("mm/s") 
	public double[] StressedETs;
	
	@Description("The stressed Transpiration for each control volumes")
	@Unit("mm/s") 
	public double[] transpirations;
	
	@Description("The stressed Evaporation for each control volumes within the Evaporation layer")
	@Unit("mm/s") 
	public double[] evaporations;
	
	@Description("It is needed to iterate on the date")
	int step;
	
	/////////////////////////////////////////////////////////////////////////////
	
	@Description("Object dealing with stress factor model for each control volumes")
	WaterStressFactor stressFactor;
	
	@Description("Object dealing with stress factor model representative of the domain")
	GeneralSF representativeSF;
	

	@Execute
	public void solve() {
		System.out.printf("\n\nStart JarvisWaterStressFactorSolverMain");	
		if(step==0){
		
			NUM_CONTROL_VOLUMES = z.length;
			totalDepth = z[NUM_CONTROL_VOLUMES -1];
			StressedETs = new double [NUM_CONTROL_VOLUMES -1];
		
			WaterStressFactorFactory stressFactorFactory= new WaterStressFactorFactory();
			stressFactor = stressFactorFactory.createStressFactor(stressFactorModel, thetaWp, thetaFc, ID, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);
		
			GeneralSFFactory representativeSFFactory= new GeneralSFFactory();
			representativeSF = representativeSFFactory.createRepresentativeStressFactor(representativeStressFactorModel, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);
			
			zR = totalDepth + etaR;
			zE = totalDepth + etaE;
		}	
		
						
		g = stressFactor.computeStressFactor(theta,zR,zE);
		GnT = representativeSF.computeRepresentativeStressFactor(g,etaR,zR);
		GnE = representativeSF.computeRepresentativeStressFactor(g,etaE,zE);
		G = GnT[0];
		GE = GnE[0];
		
		//System.out.println("theta = "+ Arrays.toString(theta));
		//System.out.println("\n\nG  = "+ G);
		System.out.printf("\nG = %.5f %n", GnT[0]);
		System.out.printf("GE = %.5f %n", GnE[0]);
						
		step++;
		System.out.printf("End JarvisWaterStressFactorSolverMain");	
	}
}
