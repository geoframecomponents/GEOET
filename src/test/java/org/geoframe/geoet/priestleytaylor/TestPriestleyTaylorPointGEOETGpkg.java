package org.geoframe.geoet.priestleytaylor;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PriestleyTaylorSolver;
import org.hortonmachine.dbs.utils.DbTimeseriesIterator;
import org.junit.Test;

/**
 * Test PriestleyTaylorSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPriestleyTaylorPointGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PriestleyTaylorPointGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString(GeoetInputsHandler.PARAM_START_DATE);
		String endDate = inputs.getParameterString(GeoetInputsHandler.PARAM_END_DATE);
		int timeStepMinutes = inputs.getParameterInt(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES);

		String pathToOutputGpkg = getOutRes("PriestleyTaylorPointGEOET.gpkg");

		PriestleyTaylorSolver ptEt = new PriestleyTaylorSolver();
		ptEt.parameters = parameters;
		ptEt.variables = variables;
		ptEt.input = input;

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		// no DEM/shapefile: elevation comes straight from the gpkg; latitude and
		// longitude are left unset (NaN default), since they are not used by the solver
		inputPreprocessor.elevation = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ELEVATION);

		ptEt.alpha = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ALPHA);
		ptEt.soilFluxParameterDay = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY);
		ptEt.soilFluxParameterNight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT);

		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		try (inputs;
				DbTimeseriesIterator tempIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_AIR_TEMPERATURE, startDate, endDate, 1000);
				DbTimeseriesIterator netradIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_NET_RADIATION, startDate, endDate,
						1000);
				DbTimeseriesIterator pressureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, startDate,
						endDate, 1000);
				DbTimeseriesIterator soilFluxIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_FLUX, startDate, endDate, 1000);
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

		assertGpkgColumnMatchesGolden("/golden/TestPriestleyTaylorPointGEOET/etp_PrestleyTaylornew.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPO_TRANSPIRATION);
		assertGpkgColumnMatchesGolden("/golden/TestPriestleyTaylorPointGEOET/latentHeatPtnew.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPO_TRANSPIRATION);
	}

}
