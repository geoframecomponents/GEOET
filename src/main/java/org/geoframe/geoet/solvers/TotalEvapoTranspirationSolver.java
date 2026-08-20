package org.geoframe.geoet.solvers;

import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.solvers.*;
import org.geoframe.geoet.core.data.*;
import org.hortonmachine.gears.libs.modules.HMModel;

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

@Description("This class compute the actual evapotranspiration")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evapotranspiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class TotalEvapoTranspirationSolver extends HMModel {

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
	public boolean doProcess5;
	@Out
	public boolean doProcess6;

	public Parameters parameters;
	public ProblemQuantities variables;
	public InputTimeSeries input;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);
		
		if (Double.isNaN(transpiration)) {
			transpiration = 0;
		}
		if (Double.isNaN(evaporation)) {
			evaporation = 0;
		}

		evapoTranspiration = (transpiration + evaporation); // --> mm/time
		if (Double.isNaN(evapoTranspiration)) {
			evapoTranspiration = 0;
		}
		fluxEvapoTranspiration = evapoTranspiration / (input.time / parameters.latentHeatEvaporation); // --> W/m2
		if (Double.isNaN(fluxEvapoTranspiration)) {
			fluxEvapoTranspiration = 0;
		}

		if (input.airTemperature == nullValue) {
			// System.out.printf("\nAir temperature is null");
			evapoTranspiration = nullValue;
		}

		variables.evapoTranspiration = evapoTranspiration;
		variables.fluxEvapoTranspiration = fluxEvapoTranspiration;
	}

}
