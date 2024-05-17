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
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.License;
import oms3.annotations.Out;
import oms3.annotations.Unit;

import java.util.ArrayList;
import java.util.Arrays;

import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import it.geoframe.blogspot.geoet.stressfactor.methods.EnvironmentalStress;
import it.geoframe.blogspot.geoet.stressfactor.methods.RepresentativeSFFactory;
//import it.geoframe.blogspot.geoet.stressfactor.methods.FaoWaterStress;
import it.geoframe.blogspot.geoet.stressfactor.methods.RepresentativeSF;
import it.geoframe.blogspot.geoet.stressfactor.methods.WaterStressFactor;
import it.geoframe.blogspot.geoet.stressfactor.methods.WaterStressFactorFactory;

@Description("This class is used to compute Jarvis water stress factor for the evaporation")
@Documentation("")
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")


public class JarvisEvaporationStressFactorSolverMain {
	
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
	
	
	@Description("Representative stress factor in the evaporation layer")
	@Out
	@Unit("-")
	public double evaporationStressWater=1;
	
	
	@Description("Vector of G and n, for evaporation")
	@Out
	@Unit("-")
	public double[] GnE;


	
	/////////////////////////////////////////////////////////////////////////////
	
	@Description("Object dealing with stress factor model for each control volumes")
	WaterStressFactor stressFactor;
	
	@Description("Object dealing with stress factor model representative of the domain")
	RepresentativeSF representativeSF;
	

	

	@In
	public boolean useWaterStress = true;
	
	@In
	public double defaultStress=1;
	
	@Description("It is needed to iterate on the date")
	int step;
	
	
	
	@In
	public boolean  doProcess2;
	
	@Out
	public boolean  doProcess3;
	
	@Description("ArrayList of variable to be stored in the buffer writer")
	@Out
	public ArrayList<double[]> outputToBuffer;
	
	/////////////////////////////////////////////////////////////////////////////

	EnvironmentalStress environmentalStress	= new EnvironmentalStress();
	//FaoWaterStress faoWaterStress = new FaoWaterStress();
	
	private ProblemQuantities variables;
	//private InputTimeSeries input;

	@Execute
	public void solve() {
		System.out.printf("\n\nStart JarvisStressFactorSolverMain");	
		if(step==0){
		
			NUM_CONTROL_VOLUMES = z.length;
			totalDepth = z[NUM_CONTROL_VOLUMES -1];
			//StressedETs = new double [NUM_CONTROL_VOLUMES -1];
		
			WaterStressFactorFactory stressFactorFactory= new WaterStressFactorFactory();
			stressFactor = stressFactorFactory.createStressFactor(stressFactorModel, thetaWp, thetaFc, ID, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);
		
			RepresentativeSFFactory representativeSFFactory= new RepresentativeSFFactory();
			representativeSF = representativeSFFactory.createRepresentativeStressFactor(representativeStressFactorModel, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth);
			
			zR = totalDepth + etaR;
			zE = totalDepth + etaE;
			
			outputToBuffer = new ArrayList<double[]>();
		}
		variables = ProblemQuantities.getInstance();
		//input = InputTimeSeries.getInstance();
	
		outputToBuffer.clear();
		

        g = stressFactor.computeStressFactor(theta,zR,zE);
       
		
		GnE = representativeSF.computeRepresentativeStressFactor(g,etaE,zE);
        
		if (useWaterStress == true) {
    		variables.evaporationStressWater = GnE[0];
    		
    		System.out.printf("GE = %.5f %n", GnE[0]);
 
        	}
        
        evaporationStressWater = variables.evaporationStressWater;
        
		//System.out.printf("\n\nStressFactorBroker Finished, G = %.5f %n", GnT[0]);
		//System.out.printf("\nGE = %.5f %n", GnE[0]);
        /*System.out.println("CELJstressSun is  = "+ stressSun);
        System.out.println("CELJstressShade is  = "+ stressShade);
        System.out.println("CELJsstressRadiationSun is  = "+ variables.stressRadiationSun);
        System.out.println("CELJstressTemperature is  = "+ variables.stressTemperature);
        System.out.println("CELJstressWater is  = "+ stressWater);
        System.out.println("CELJstressVPD is  = "+ variables.stressVPD);
        System.out.println("CELJdefault is  = "+ defaultStress);*/
        /* System.out.println("CELJvariables.shortwaveCanopySun is  = "+ variables.shortwaveCanopySun);
        System.out.println("CELJvariables.shortwaveCanopyShade is  = "+ variables.shortwaveCanopyShade);
        System.out.println("CELJalpha is  = "+ alpha);
        System.out.println("CELJtheta is  = "+ theta);
        System.out.println("CELJinput.airTemperature is  = "+ input.airTemperature);
        System.out.println("CELJTl is  = "+ Tl);
        System.out.println("CELJTh is  = "+ Th);
        System.out.println("CELJT0 is  = "+ T0);*/
						
        outputToBuffer.add(g);

        outputToBuffer.add(new double[] {variables.stressWater});
        outputToBuffer.add(new double[] {variables.evaporationStressWater});
        outputToBuffer.add(new double[] {0});
        outputToBuffer.add(new double[] {0});
        
		step++;
		System.out.printf("End JarvisStressFactorSolverMain");	
	}
}
