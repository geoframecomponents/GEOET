package org.geoframe.geoet.priestleytaylor;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PriestleyTaylorSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test PriestleyTaylorSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPriestleyTaylorGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PriestleyTaylorGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("PriestleyTaylorGEOET.gpkg");

		PriestleyTaylorSolver ptEt = new PriestleyTaylorSolver();
		ptEt.parameters = parameters;
		ptEt.variables = variables;
		ptEt.input = input;

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputPreprocessor.elevation = inputs.getParameterDouble("elevation");
		inputPreprocessor.latitude = inputs.getParameterDouble("latitude");
		inputPreprocessor.longitude = inputs.getParameterDouble("longitude");

		ptEt.alpha = inputs.getParameterDouble("alpha");
		ptEt.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		ptEt.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries("airTemperature", startDate, endDate, 1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries("netRadiation", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries("atmosphericPressure", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries("soilFlux", startDate, endDate, 1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.parameters = inputs.getParameters();

			while (tempIt.next()) {
				netradIt.next();
				pressureIt.next();
				soilFluxIt.next();

				inputPreprocessor.inAirTemperature = one(STATION_ID, tempIt.value());
				inputPreprocessor.inNetRadiation = one(STATION_ID, netradIt.value());
				inputPreprocessor.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputPreprocessor.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputPreprocessor.process();
				ptEt.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = variables.evapoTranspirationPT;
				outputs.fluxEvapoTranspiration = variables.fluxEvapoTranspirationPT;
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/TestPriestleyTaylorGEOET/etp_PrestleyTaylornew.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPO_TRANSPIRATION);
		assertGpkgColumnMatchesGolden("/golden/TestPriestleyTaylorGEOET/latentHeatPtnew.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPO_TRANSPIRATION);
	}

}
