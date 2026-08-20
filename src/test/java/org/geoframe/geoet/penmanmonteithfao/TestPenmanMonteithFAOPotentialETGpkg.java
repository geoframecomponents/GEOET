package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test FAO potential evapotranspiration, driven by a single input GeoPackage
 * ({@code PenmanMonteithFAOPotentialET.gpkg} - scalar parameters + driving
 * timeseries in one file) instead of the individual CSVs/DEM/shapefile
 * {@link TestPenmanMonteithFAOPotentialET} reads. Same solver wiring as the
 * original. The computed values go only into the output GeoPackage (via
 * {@link GeoetOutputsHandler}); this test then reads them straight back out
 * of that gpkg and compares them against the golden reference CSVs {@link
 * TestPenmanMonteithFAOPotentialET} already checks (see {@link
 * GeoetTestCase#assertGpkgColumnMatchesGolden}) - this way the assertion
 * actually exercises the gpkg's contents, not a parallel CSV written
 * alongside it purely for comparison purposes.
 */
public class TestPenmanMonteithFAOPotentialETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/input/gpkg/PenmanMonteithFAOPotentialET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("PenmanMonteithFAOPotentialET.gpkg");

		PenmanMonteithFAOSolver pmFAO = new PenmanMonteithFAOSolver();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;

		InputReader inputReader = new InputReader();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputReader.elevation = inputs.getParameterDouble("elevation");
		inputReader.latitude = inputs.getParameterDouble("latitude");
		inputReader.longitude = inputs.getParameterDouble("longitude");

		inputReader.canopyHeight = inputs.getParameterDouble("canopyHeight");
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;

		outputWriter.doPrintOutputPM = true;

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries("airTemperature", startDate, endDate, 1000);
				GeopackageTimeseriesIterator windIt = inputs.iterateTimeseries("windVelocity", startDate, endDate, 1000);
				GeopackageTimeseriesIterator humIt = inputs.iterateTimeseries("relativeHumidity", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries("netRadiation", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries("atmosphericPressure", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries("soilFlux", startDate, endDate, 1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.parameters = inputs.getParameters();

			while (tempIt.next()) {
				windIt.next();
				humIt.next();
				netradIt.next();
				pressureIt.next();
				soilFluxIt.next();

				inputReader.inAirTemperature = one(tempIt.value());
				inputReader.inWindVelocity = one(windIt.value());
				inputReader.inRelativeHumidity = one(humIt.value());
				inputReader.inNetRadiation = one(netradIt.value());
				inputReader.inAtmosphericPressure = one(pressureIt.value());
				inputReader.inSoilFlux = one(soilFluxIt.value());

				inputReader.process();
				pmFAO.process();
				outputWriter.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = outputWriter.outEvapoTranspirationPM.get(STATION_ID)[0];
				outputs.fluxEvapoTranspiration = outputWriter.outLatentHeatPM.get(STATION_ID)[0];
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/TestPenmanMonteithFAOPotentialET/ETpotentialFAO.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPO_TRANSPIRATION);
		assertGpkgColumnMatchesGolden("/golden/TestPenmanMonteithFAOPotentialET/FluxETpotentialFAO.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPO_TRANSPIRATION);
	}

	private static HashMap<Integer, double[]> one(double value) {
		HashMap<Integer, double[]> m = new HashMap<>();
		m.put(STATION_ID, new double[] { value });
		return m;
	}

}
