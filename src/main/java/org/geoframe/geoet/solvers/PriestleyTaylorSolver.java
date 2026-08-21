/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
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
package org.geoframe.geoet.solvers;

import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.hortonmachine.gears.libs.modules.HMModel;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
//import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;

import org.geoframe.geoet.core.priestleytaylor.PriestleyTaylorModel;
@Description("Calculate evapotraspiration based on the Priestley Taylor model")
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptetp")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")

public class PriestleyTaylorSolver extends HMModel {

	@Description("The alpha parameter.")
	@In
	@Unit("-")
	public double alpha;

	@Description("The coefficient for the soil heat flux during daylight")
	@In
	public double soilFluxParameterDay;

	@Description("The coefficient for the soil heat flux during nighttime")
	@In
	public double soilFluxParameterNight;

	@Description("The soilflux default value in case of missing data.")
	@In
	@Unit("W m-2")
	public double defaultSoilFlux = 0.0;

	double nullValue = -9999.0;

	int step;

	public Parameters parameters;
	public ProblemQuantities variables;
	public CurrentStepInput input;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);
		// //System.out.printf("\n\nStart PriestleyTaylorETSolverMain");

		input.airTemperatureC = input.airTemperature - 273.15;
		parameters.alpha = alpha;

		variables.hourOfDay = variables.date.getHourOfDay();
		variables.isLigth = false;
		if (variables.hourOfDay > 6 && variables.hourOfDay < 18) {
			variables.isLigth = true;
		}

		if (variables.isLigth == true) {
			variables.soilFluxparameter = soilFluxParameterDay;
		} else {
			variables.soilFluxparameter = soilFluxParameterNight;
		}

		if (input.soilFlux == defaultSoilFlux) {
			input.soilFlux = variables.soilFluxparameter * input.netRadiation;
		}

		variables.fluxEvapoTranspirationPT = (input.netRadiation < 0) ? 0
				: PriestleyTaylorModel.computeEvapotranspirationFlux(parameters.alpha, input.atmosphericPressure,
						input.airTemperatureC, input.soilFlux, input.netRadiation);
		variables.fluxEvapoTranspirationPT = (variables.fluxEvapoTranspirationPT < 0) ? 0
				: variables.fluxEvapoTranspirationPT;

		variables.evapoTranspirationPT = variables.fluxEvapoTranspirationPT
				* (input.time / parameters.latentHeatEvaporation);
		variables.evapoTranspirationPT = (variables.evapoTranspirationPT < 0) ? 0 : variables.evapoTranspirationPT;

		// System.out.println("\ndata = "+variables.date+ " ID ="+ID);

		// System.out.println("\nevapotranspiration = "+variables.evapoTranspirationPT);
		// System.out.println("\nairTpriestleyTaylor = "+input.airTemperature+ " ID
		// ="+ID);
		// System.out.println("\natmosphericPressure = "+input.atmosphericPressure);
		// System.out.println("\nnetRadiation = "+input.netRadiation);
		// System.out.println("\nsoilFlux = "+input.soilFlux);
		// System.out.println("\nsoilFluxparameter = "+variables.soilFluxparameter);

		// //System.out.printf("\n\nEnd PriestleyTaylorETSolverMain");

	}
}
