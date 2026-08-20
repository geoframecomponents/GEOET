package org.geoframe.geoet.priestleytaylor;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.solvers.PriestleyTaylorSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test Priestley-Taylor evapotranspiration, driven by a single input
 * GeoPackage ({@code PriestleyTaylorPointGEOET.gpkg} - scalar parameters +
 * driving timeseries in one file) instead of the individual CSVs {@link
 * TestPriestleyTaylorPointGEOET} reads. Same solver wiring as the original -
 * no DEM/shapefile in either version, and latitude/longitude are left unset
 * (at {@code InputReader}'s own {@code NaN} default) here too, only
 * elevation is read from the gpkg. The computed values go only into the
 * output GeoPackage (via {@link GeoetOutputsHandler}); this test then reads
 * them straight back out of that gpkg and compares them against the golden
 * reference CSVs {@link TestPriestleyTaylorPointGEOET} already checks (see
 * {@link GeoetTestCase#assertGpkgColumnMatchesGolden}) - this way the
 * assertion actually exercises the gpkg's contents, not a parallel CSV
 * written alongside it purely for comparison purposes.
 */
public class TestPriestleyTaylorPointGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PriestleyTaylorPointGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("PriestleyTaylorPointGEOET.gpkg");

		PriestleyTaylorSolver ptEt = new PriestleyTaylorSolver();
		ptEt.parameters = parameters;
		ptEt.variables = variables;
		ptEt.input = input;

		InputReader inputReader = new InputReader();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		// no DEM/shapefile: elevation comes straight from the gpkg; latitude and
		// longitude are left unset (NaN default), since they are not used by the solver
		inputReader.elevation = inputs.getParameterDouble("elevation");

		ptEt.alpha = inputs.getParameterDouble("alpha");
		ptEt.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		ptEt.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;

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

				inputReader.inAirTemperature = one(STATION_ID, tempIt.value());
				inputReader.inNetRadiation = one(STATION_ID, netradIt.value());
				inputReader.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputReader.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputReader.process();
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
