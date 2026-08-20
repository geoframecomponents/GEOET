package org.geoframe.geoet.solvers;

import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.radiation.ComputeRadiationQuantitiesComplete;
import org.hortonmachine.gears.libs.modules.HMModel;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;

@Description("This class compute the absorbed radiation from canopy")
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evapotranspiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class RadiationSolverComplete extends HMModel {

	@In
	public String typeOfCanopy;

	public Parameters parameters;
	public ProblemQuantities variables;
	public InputTimeSeries input;

	@Execute
	public void process() throws Exception {
		checkNull(parameters, variables, input);
		// System.out.print("\n\nStart RadiationSolverComplete");

		ComputeRadiationQuantitiesComplete.computeRadiationQuantities(parameters, variables, input, variables.date,
				input.latitude, input.longitude, input.time, input.leafAreaIndex, typeOfCanopy,
				input.shortWaveRadiationDirect, input.shortWaveRadiationDiffuse);

		// System.out.print("\nEnd RadiationSolverComplete");
	}

}
