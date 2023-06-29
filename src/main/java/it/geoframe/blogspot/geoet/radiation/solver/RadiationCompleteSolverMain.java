package it.geoframe.blogspot.geoet.radiation.solver;

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
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
//import java.util.Arrays;
import it.geoframe.blogspot.geoet.inout.*;
import it.geoframe.blogspot.geoet.radiation.methods.*;



@Description("This class compute the absorbed radiation from canopy")
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evapotranspiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class RadiationCompleteSolverMain {
	
	
	@In public String typeOfCanopy;
	
	
	double nullValue = -9999.0;

	@In 
	public boolean  doProcess1;
	
	@Out 
	public boolean  doProcess2;

	
	ComputeRadiationQuantitiesComplete computeRadiationQuantities = new ComputeRadiationQuantitiesComplete();
	

	private ProblemQuantities variables;
	private InputTimeSeries input;
	
	
	@Execute
	public void process() throws Exception {
		System.out.print("\n\nStart RadiationCompleteSolverMain");

		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		

		computeRadiationQuantities.computeRadiationQuantities(variables.date, input.latitude, input.longitude, input.time, input.leafAreaIndex, typeOfCanopy, input.shortWaveRadiationDirect, input.shortWaveRadiationDiffuse);
		
		
		System.out.print("\nEnd RadiationCompleteSolverMain");
	}
	
}
