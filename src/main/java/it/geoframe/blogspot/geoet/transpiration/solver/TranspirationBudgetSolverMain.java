package it.geoframe.blogspot.geoet.transpiration.solver;

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
//import java.util.Arrays;
import it.geoframe.blogspot.geoet.inout.*;
import it.geoframe.blogspot.geoet.radiation.methods.RadiationMethod;
import it.geoframe.blogspot.geoet.stressfactor.methods.*;
import it.geoframe.blogspot.geoet.transpiration.data.*;
import it.geoframe.blogspot.geoet.transpiration.methods.*;

@Description("Transpiration Budget solved according to D'Amato & Rigon (2023)")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Transpiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class TranspirationBudgetSolverMain {
	

	
	//@In public double canopyHeight;
	
	@In public String typeOfCanopy;
	
	
	double nullValue = -9999.0;

	
	@In
	public boolean  doProcess2;
	
	@Out
	public boolean  doProcess3;
	
	@Description("Stress factor for sun canopy")
	@In
	@Unit("-")
	public double stressSun;
	
	@Description("Stress factor for shade canopy")
	@In
	@Unit("-")
	public double stressShade;

	
	@Description("The Transpiration.")
	@Unit("mm h-1")
	@Out
	public double transpiration;
	
	
	// METHODS FROM CLASSES		
	TranspirationBudget plantstranspiration 			= new TranspirationBudget();
	ComputeQuantities computeQuantities = new ComputeQuantities();
	
	
	//private Leaf leafparameters;
	private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	
	
	@Execute
	public void process() throws Exception {
		System.out.print("\n\nStart TranspirationBudgetSolverMain");

		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
				


		computeQuantities.computeQuantities(input.windVelocity, variables.canopyHeight, input.airTemperature, input.relativeHumidity, input.atmosphericPressure, variables.date, input.latitude, input.longitude, input.time, input.leafAreaIndex, typeOfCanopy, input.shortWaveRadiationDirect, input.shortWaveRadiationDiffuse, input.netRadiation);
		
					
		////////////////////////////////////////////////////////// Transpiration //////////////////////////////////////////////////////////////////////
        
		variables.fluxTranspiration = plantstranspiration.computeTranspirationBudget(stressSun,  stressShade,  input.atmosphericPressure,  input.airTemperature,  input.time,  nullValue);
		variables.transpiration = variables.fluxTranspiration * (input.time / parameters.latentHeatEvaporation);
		transpiration=variables.transpiration;	
		System.out.println("\nflux transpiration is  = "+ variables.fluxTranspiration);

			
		if (Double.isNaN(variables.transpiration)) {variables.transpiration = 0;}  
		
		/*System.out.println("stressSun is  = "+ stressSun);
        System.out.println("stressShade is  = "+ stressShade);
        System.out.println("input.longWaveRadiation is  = "+ input.longWaveRadiation);
        System.out.println("input.airTemperature is  = "+ input.airTemperature);
        System.out.println("input.time, is  = "+ input.time);
        System.out.println("nullValue is  = "+ nullValue);*/
		
		System.out.print("\nEnd TranspirationBudgetSolverMain");
	}
	
}