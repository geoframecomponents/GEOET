package org.geoframe.geoet.solvers;

import org.geoframe.geoet.core.transpiration.ComputeQuantities;
import org.geoframe.geoet.core.data.Leaf;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.solvers.*;
import org.geoframe.geoet.core.data.*;
import org.geoframe.geoet.core.radiation.RadiationMethod;
import org.geoframe.geoet.core.stressfactor.*;
import org.geoframe.geoet.core.transpiration.*;
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

@Description("Transpiration Budget solved according to D'Amato & Rigon (2023)")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Transpiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class TranspirationBudgetSolver extends HMModel {

	// @In public double canopyHeight;

	@In
	public String typeOfCanopy;

	double nullValue = -9999.0;

	@In
	public boolean doProcess2;

	@Out
	public boolean doProcess3;

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

	public Parameters parameters;
	public ProblemQuantities variables;
	public InputTimeSeries input;
	public Leaf leafparameters;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);

		ComputeQuantities computeQuantities = new ComputeQuantities(leafparameters, parameters, variables);

		computeQuantities.computeQuantities(input.windVelocity, variables.canopyHeight, input.airTemperature,
				input.relativeHumidity, input.atmosphericPressure, variables.date, input.latitude, input.longitude,
				input.time, input.leafAreaIndex, typeOfCanopy, input.shortWaveRadiationDirect,
				input.shortWaveRadiationDiffuse, input.netRadiation);

		////////////////////////////////////////////////////////// Transpiration
		////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////

		variables.fluxTranspiration = TranspirationBudget.computeTranspirationBudget(variables, leafparameters,
				parameters, stressSun, stressShade, input.atmosphericPressure, input.airTemperature, input.time,
				nullValue);
		variables.transpiration = variables.fluxTranspiration * (input.time / parameters.latentHeatEvaporation);
		transpiration = variables.transpiration;
		System.out.println("\nflux transpiration is  = " + variables.fluxTranspiration);

		if (Double.isNaN(variables.transpiration)) {
			variables.transpiration = 0;
		}

	}

}
