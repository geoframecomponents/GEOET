package it.geoframe.blogspot.geoet.soilevaporation.solver;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;
import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.data.WindProfile;

//import java.util.Arrays;

import it.geoframe.blogspot.geoet.inout.*;
//import it.geoframe.blogspot.geoet.prospero.data.*;
import it.geoframe.blogspot.geoet.penmanmonteithfao.*;


@Description("The Penman Monteith model for computing actual evaporation from soil considering the radiation incident the soil from the Prospero model")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evaporation from soil")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class PMEvaporationFromSoilAfterCanopySolverMain {
	
	@Description("Water stress factor for the Evaporation layer")
	@In
	@Unit("-")
	public double evaporationStressWater;
	
	//@In
	//public boolean useEvaporationWaterStress=false;
	
	//@In public double canopyHeight;
	
	//@In public String typeOfCanopy;
	
	
	double nullValue = -9999.0;
	
	//@In 
	//public boolean  doProcess;
	
	@In 
	public boolean  doPro;
	
	@Out 
	public boolean  doProcessOut = false;
	
	
	
	/*@Description("Stress factor for sun canopy")
	@In
	@Unit("-")
	public double stressSun;
	
	@Description("Stress factor for shade canopy")
	@In
	@Unit("-")
	public double stressShade;*/
	
	@Description("The Evaporation.")
	@Unit("mm h-1")
	@Out
	public double evaporation;
	
	/*@Description("The Transpiration.")
	@Unit("mm h-1")
	@Out
	public double transpiration;*/
	
	


	
	// METHODS FROM CLASSES		
	//SensibleHeatMethods sensibleHeat 		= new SensibleHeatMethods();
	//LatentHeatMethods latentHeat 			= new LatentHeatMethods();
	//PressureMethods pressure 				= new PressureMethods(); 
	//RadiationMethod radiationMethods 		= new RadiationMethod();
	//SolarGeometry solarGeometry 			= new SolarGeometry();
	//EnvironmentalStress environmentalStress	= new EnvironmentalStress();
	PenmanMonteithFAOmodel soilevaporation 		= new PenmanMonteithFAOmodel();
	//Transpiration plantstranspiration 			= new Transpiration();
	//StoreResultsMethods results 			= new StoreResultsMethods();
	//ComputeQuantitiesProspero computeQuantitiesProspero = new ComputeQuantitiesProspero();
	
	
	//private HortonMessageHandler msg = HortonMessageHandler.getInstance();
	//private Leaf leafparameters;
	private Parameters parameters;
	//private ComputeQuantitiesProspero computeQuantitiesProspero;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	
	
	@Execute
	public void process() throws Exception {
		System.out.print("\n\nStart PMEvaporationFromSoilSolverMain");

		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		input.airTemperatureC = input.airTemperature - 273.15;
				
//////////// Evaporation from Soil //////////////////
        //if (useEvaporationWaterStress == false) {evaporationStressWater = 1;}
		WindProfile windAtSoil = new WindProfile();
		variables.windSoil = windAtSoil.computeWindProfile(input.windVelocity, 0.2);
        
        
        soilevaporation.setNumber(input.airTemperatureC, input.atmosphericPressure, variables.incidentSolarRadiationSoil, input.relativeHumidity, input.soilFlux, variables.windSoil);

		variables.fluxEvaporation = soilevaporation.doET()* parameters.latentHeatEvaporation / 86400 * evaporationStressWater; // --> W/m2
		
		variables.fluxEvaporation = (variables.fluxEvaporation<0)?0:variables.fluxEvaporation;
		
		variables.evaporation = variables.fluxEvaporation * (input.time / parameters.latentHeatEvaporation); // --> mm/time
		
		evaporation = variables.evaporation;
		
		System.out.println("\nevaporation is  = "+ variables.fluxEvaporation);
		
		
//////////// Transpiration /////////////////////////
		/*System.out.println("stressSun is  = "+ stressSun);
        System.out.println("stressShade is  = "+ stressShade);
        System.out.println("input.longWaveRadiation is  = "+ input.longWaveRadiation);
        System.out.println("input.airTemperatureis  = "+ input.airTemperature);
        System.out.println("input.time, is  = "+ input.time);
      
        System.out.println("nullValue is  = "+ nullValue);*/
        
		//variables.fluxTranspiration = plantstranspiration.computeTranspiration(stressSun,  stressShade,  input.longWaveRadiation,  input.airTemperature,  input.time,  nullValue);
		//variables.transpiration = variables.fluxTranspiration * (input.time / parameters.latentHeatEvaporation);
		//transpiration=variables.transpiration;	
		//System.out.println("\nflux transpiration is  = "+ variables.fluxTranspiration);

		
		//////////// EvapoTranspiration ////////////////////
			
		//variables.fluxEvapoTranspiration = (variables.fluxTranspiration+variables.fluxEvaporation); // --> W/m2
		//if (Double.isNaN(variables.fluxEvapoTranspiration)) {variables.fluxEvapoTranspiration = 0;}
		//variables.evapoTranspiration = variables.fluxEvapoTranspiration * (input.time / parameters.latentHeatEvaporation); // --> mm/time 
		
		if (input.airTemperature == nullValue) {
			System.out.printf("\nAir temperature is null");
			variables.evapoTranspiration = nullValue;}
			
		if (Double.isNaN(variables.evaporation)) {variables.evaporation = 0;}  
		
		System.out.print("\nEnd PMEvaporationFromSoilSolverMain");
	}
	
}
