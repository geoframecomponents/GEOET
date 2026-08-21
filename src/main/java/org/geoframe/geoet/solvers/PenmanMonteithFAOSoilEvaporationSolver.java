package org.geoframe.geoet.solvers;

import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.penmanmonteithfao.PenmanMonteithFAOModel;
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

@Description("The Penman Monteith model for computing actual evaporation from soil considering the net radiation incoming")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evaporation from soil")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class PenmanMonteithFAOSoilEvaporationSolver extends HMModel {

	@Description("Water stress factor for the Evaporation layer")
	@In
	@Unit("-")
	public double evaporationStressWater = 1;

	double nullValue = -9999.0;

	// @In
	// public boolean doProcess;

	// @Out
	// public boolean doProcessOut = false;

	@In
	public boolean doProcess4;

	@Out
	public boolean doProcess5;

	@Description("The Evaporation.")
	@Unit("mm h-1")
	@Out
	public double evaporation;

	public Parameters parameters;
	public ProblemQuantities variables;
	public CurrentStepInput input;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);

		input.airTemperatureC = input.airTemperature - 273.15;

//////////// Evaporation from Soil //////////////////

		variables.windSoil = ProblemQuantities.computeWindProfile(input.windVelocity, 0.2);

		variables.evaporation = PenmanMonteithFAOModel.computeEvapotranspirationDepth(parameters.Cp, parameters.Cd,
				input.atmosphericPressure, input.airTemperatureC, input.relativeHumidity, input.soilFlux, input.time,
				variables.windSoil, input.netRadiation) * evaporationStressWater; // --> mm/time
		variables.fluxEvaporation = variables.evaporation * parameters.latentHeatEvaporation / input.time; // --> W/m2

		variables.evaporation = (variables.evaporation < 0) ? 0 : variables.evaporation;
		variables.fluxEvaporation = (variables.fluxEvaporation < 0) ? 0 : variables.fluxEvaporation;

		evaporation = variables.evaporation;

		if (input.airTemperature == nullValue) {
			// System.out.printf("\nAir temperature is null");
			variables.evapoTranspiration = nullValue;
		}

		if (Double.isNaN(variables.evaporation)) {
			variables.evaporation = 0;
		}

		// System.out.print("End PenmanMonteithFAOSoilEvaporationSolver");
	}

}
