package org.geoframe.geoet.soilevaporation.solver;

import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.inout.InputTimeSeries;
import org.geoframe.geoet.penmanmonteithfao.PenmanMonteithFAOModel;
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

@Description("The Penman Monteith model for computing actual evaporation from soil considering the radiation incident the soil from the Prospero model")

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evaporation from soil")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class PMEvaporationFromSoilCanopySolverMain extends HMModel {

	@Description("Water stress factor for the Evaporation layer")
	@In
	@Unit("-")
	public double evaporationStressWater;

	// @In
	// public boolean useEvaporationWaterStress=false;

	// @In public double canopyHeight;

	// @In public String typeOfCanopy;
	double nullValue = -9999.0;

	@Description("The Evaporation.")
	@Unit("mm h-1")
	@Out
	public double evaporation;

	/*
	 * @Description("The Transpiration.")
	 * 
	 * @Unit("mm h-1")
	 * 
	 * @Out public double transpiration;
	 */

	public Parameters parameters;
	public ProblemQuantities variables;
	public InputTimeSeries input;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);

		input.airTemperatureC = input.airTemperature - 273.15;

		//////////// Evaporation from Soil //////////////////

		variables.windSoil = ProblemQuantities.computeWindProfile(input.windVelocity, 0.2);

		// soilevaporation.setNumber(input.airTemperatureC, input.atmosphericPressure,
		// variables.incidentSolarRadiationSoil, input.relativeHumidity, input.soilFlux,
		// variables.windSoil);
		// variables.fluxEvaporation = soilevaporation.doET(variables.windSoil,
		// variables.incidentSolarRadiationSoil)* parameters.latentHeatEvaporation /
		// 86400 * evaporationStressWater; // --> W/m2
		// variables.evaporation = variables.fluxEvaporation * (input.time /
		// parameters.latentHeatEvaporation); // --> mm/time

		variables.evaporation = PenmanMonteithFAOModel.doET(parameters, input, variables.windSoil,
				variables.incidentSolarRadiationSoil) * evaporationStressWater; // --> mm/time
		variables.fluxEvaporation = variables.evaporation * parameters.latentHeatEvaporation / input.time; // --> W/m2

		variables.evaporation = (variables.evaporation < 0) ? 0 : variables.evaporation;
		variables.fluxEvaporation = (variables.fluxEvaporation < 0) ? 0 : variables.fluxEvaporation;

		evaporation = variables.evaporation;

		// System.out.println("\nflux evaporation is = "+ variables.fluxEvaporation);
		// System.out.printf("\nflux evaporation = %.5f %n", variables.fluxEvaporation);

		if (input.airTemperature == nullValue) {
			// System.out.printf("\nAir temperature is null");
			variables.evapoTranspiration = nullValue;
		}

		if (Double.isNaN(variables.evaporation)) {
			variables.evaporation = 0;
		}

		// System.out.print("\nEnd PMEvaporationFromSoilSolverMain");
	}

}
