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
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import it.geoframe.blogspot.geoet.stressfactor.methods.EnvironmentalStress;
import it.geoframe.blogspot.geoet.stressfactor.methods.FaoWaterStress;

@Description("This class is used to compute stress factors for Priestley Taylor and Penman Monteith evapotranspiration model")
@Documentation("")
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")


public class PTPMStressFactorSolverMain {
	
	@Description("Wilting point")
	@In 
	@Unit ("-")
	public double waterWiltingPoint;	

	@Description("Field capacity")
	@In 
	@Unit ("-")
	public double waterFieldCapacity;
	
	@Description("Water content")
	@In 
	@Unit ("-")
	public double soilMoisture;
	
	@Description("Depth of the root.")
	@In 
	@Unit("m")
	public double depth;
	
	@Description("The crop coefficient.")
	@Unit("[-]")
	@In
	public double cropCoefficient = 1;
	
	@In	public double alpha;
	@In public double theta;
	@In public double VPD0;

	@In	public double T0;
	@In public double Tl;
	@In public double Th;
	
	@In public double depletionFraction;
	
	@In
	public boolean useRadiationStress = false;
	@In
	public boolean useTemperatureStress = false;
	@In
	public boolean useVDPStress = false;
	@In
	public boolean useWaterStress = false;
	
	@In
	public double defaultStress = 1;
	
	//@In
	//public boolean  doProcess;
	
	@In
	public boolean  doProcess2;
	
	@Out
	public boolean  doProcess3;
	
	@Description("It is needed to iterate on the date")
	int step;
	
	@Description("Stress factor for sun canopy")
	@Out
	@Unit("-")
	public double stressSun;
	
	//@Description("Stress factor for shade canopy")
	//@Out
	//@Unit("-")
	//public double stressShade;
	
	/////////////////////////////////////////////////////////////////////////////

	EnvironmentalStress environmentalStress	= new EnvironmentalStress();
	FaoWaterStress faoWaterStress = new FaoWaterStress();
	
	private ProblemQuantities variables;
	private InputTimeSeries input;

	@Execute
	public void solve() {
		
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
	
	
		variables.stressRadiationSun = 1;
        if (useRadiationStress == true) {
        	variables.stressRadiationSun = environmentalStress.computeRadiationStress(input.netRadiation*2.1, alpha, theta);
        	}
        
       /* variables.stressRadiationShade = 1;
        if (useRadiationStress == true) {
        	variables.stressRadiationShade = environmentalStress.computeRadiationStress(input.netRadiation*2.1, alpha, theta);
        	}*/
        
        
        variables.stressTemperature = 1;
        if (useTemperatureStress == true) {
        	variables.stressTemperature = environmentalStress.computeTemperatureStress(input.airTemperature, Tl, Th, T0);
        	}

        variables.stressVPD = 1;
        if (useVDPStress == true) {
        	variables.stressVPD = environmentalStress.computeVapourPressureStress(input.airTemperature, VPD0);
        	}
          
        variables.stressWater = 1;
        if (useWaterStress == true) {
        	variables.stressWater = faoWaterStress.computeFAOWaterStress(input.soilMoisture, waterFieldCapacity, waterWiltingPoint, depth, depletionFraction) * cropCoefficient;
        	}
        	            
        stressSun = defaultStress * variables.stressRadiationSun * variables.stressTemperature * variables.stressWater * variables.stressVPD;
    
        //stressShade = defaultStress * variables.stressRadiationShade * variables.stressTemperature * variables.stressWater * variables.stressVPD;
        
		//System.out.printf("\n\nStressFactorBroker Finished, G = %.5f %n", GnT[0]);
		//System.out.printf("\nGE = %.5f %n", GnE[0]);
		
       // System.out.printf("\n\nstressSun= %.5f %n", stressSun);
        
       // System.out.printf("\ntheta= %.5f %n", input.soilMoisture);
        
        
        
		step++;
	}
}
