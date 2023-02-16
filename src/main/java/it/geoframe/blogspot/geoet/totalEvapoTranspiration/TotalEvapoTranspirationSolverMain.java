package it.geoframe.blogspot.geoet.totalEvapoTranspiration;

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
//import it.geoframe.blogspot.geoet.prospero.data.*;


@Description("This class compute the actual evapotranspiration")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evapotranspiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class TotalEvapoTranspirationSolverMain {

	
	
	double nullValue = -9999.0;
	
	@Description("The Evaporation.")
	@Unit("mm h-1")
	@In
	public double evaporation;
	
	@Description("The Transpiration.")
	@Unit("mm h-1")
	@In
	public double transpiration;
	
	@Description("The Transpiration.")
	@Unit("mm h-1")
	@Out
	public double evapoTranspiration;
	
	@Description("The flux of Transpiration.")
	@Unit("W m-2")
	@Out
	public double fluxEvapoTranspiration;
	
	@In
	public boolean  doProcess5;
	@Out
	public boolean  doProcess6;

	private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	
	
	@Execute
	public void process() throws Exception {
		System.out.print("\n\nStart TotalEvapoTranspirationSolverMain");

		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();

		
		if (Double.isNaN(transpiration)) {transpiration = 0;}
		if (Double.isNaN(evaporation)) {evaporation = 0;}
			
		evapoTranspiration = (transpiration+evaporation); // --> mm/time 
		if (Double.isNaN(evapoTranspiration)) {evapoTranspiration = 0;}
		fluxEvapoTranspiration = evapoTranspiration / (input.time / parameters.latentHeatEvaporation); // --> W/m2
		if (Double.isNaN(fluxEvapoTranspiration)) {fluxEvapoTranspiration = 0;} 
		
		
		if (input.airTemperature == nullValue) {
			System.out.printf("\nAir temperature is null");
			evapoTranspiration = nullValue;}
	  
	 
		variables.evapoTranspiration = evapoTranspiration;
		variables.fluxEvapoTranspiration = fluxEvapoTranspiration ; 
		System.out.print("\nEnd TotalEvapoTranspirationSolverMain");
	}
	
}
