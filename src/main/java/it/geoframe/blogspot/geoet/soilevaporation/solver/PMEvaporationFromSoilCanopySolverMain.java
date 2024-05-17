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
public class PMEvaporationFromSoilCanopySolverMain {
	
	@Description("Water stress factor for the Evaporation layer")
	@In
	@Unit("-")
	public double evaporationStressWater;
	
	//@In
	//public boolean useEvaporationWaterStress=false;
	
	//@In public double canopyHeight;
	
	//@In public String typeOfCanopy;
	
	
	double nullValue = -9999.0;
	
	
	
	@In 
	public boolean  doProcess4;
	
	@Out 
	public boolean  doProcess5;

	
	@Description("The Evaporation.")
	@Unit("mm h-1")
	@Out
	public double evaporation;
	
	/*@Description("The Transpiration.")
	@Unit("mm h-1")
	@Out
	public double transpiration;*/
	
	


	
	// METHODS FROM CLASSES		
	PenmanMonteithFAOModel soilevaporation 		= new PenmanMonteithFAOModel();
	WindProfile windAtSoil = new WindProfile();

	private Parameters parameters;	
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

		variables.windSoil = windAtSoil.computeWindProfile(input.windVelocity, 0.2);
        
        //soilevaporation.setNumber(input.airTemperatureC, input.atmosphericPressure, variables.incidentSolarRadiationSoil, input.relativeHumidity, input.soilFlux, variables.windSoil);
		//variables.fluxEvaporation = soilevaporation.doET(variables.windSoil, variables.incidentSolarRadiationSoil)* parameters.latentHeatEvaporation / 86400 * evaporationStressWater; // --> W/m2
		//variables.evaporation = variables.fluxEvaporation * (input.time / parameters.latentHeatEvaporation); // --> mm/time
		
		variables.evaporation = soilevaporation.doET(variables.windSoil, variables.incidentSolarRadiationSoil) * evaporationStressWater; // --> mm/time
		variables.fluxEvaporation = variables.evaporation * parameters.latentHeatEvaporation / input.time; // --> W/m2

		variables.evaporation = (variables.evaporation<0)?0:variables.evaporation;
		variables.fluxEvaporation = (variables.fluxEvaporation<0)?0:variables.fluxEvaporation;
		
		evaporation = variables.evaporation;
		
		//System.out.println("\nflux evaporation is  = "+ variables.fluxEvaporation);
		System.out.printf("\nflux evaporation = %.5f %n", variables.fluxEvaporation);
		
		if (input.airTemperature == nullValue) {
			System.out.printf("\nAir temperature is null");
			variables.evapoTranspiration = nullValue;}
			
		if (Double.isNaN(variables.evaporation)) {variables.evaporation = 0;}  
		
		System.out.print("\nEnd PMEvaporationFromSoilSolverMain");
	}
	
}
