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
import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithFAOWaterStress;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test FAO Hourly evapotranspiration, driven by a single input GeoPackage
 * ({@code PenmanMonteithFAOWaterStressed.gpkg} - scalar parameters +
 * driving timeseries in one file) instead of the individual CSVs/DEM/
 * shapefile {@link TestPenmanMonteithFAOWaterStressed} reads. Same solver
 * wiring as the original. The computed values go only into the output
 * GeoPackage (via {@link GeoetOutputsHandler}); this test then reads them
 * straight back out of that gpkg and compares them against the golden
 * reference CSVs {@link TestPenmanMonteithFAOWaterStressed} already checks
 * (see {@link GeoetTestCase#assertGpkgColumnMatchesGolden}) - this way the
 * assertion actually exercises the gpkg's contents, not a parallel CSV
 * written alongside it purely for comparison purposes.
 *
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
public class TestPenmanMonteithFAOWaterStressedGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/input/gpkg/PenmanMonteithFAOWaterStressed.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("PenmanMonteithFAOWaterStressed.gpkg");

		PenmanMonteithFAOSolverWithFAOWaterStress pmFAO = new PenmanMonteithFAOSolverWithFAOWaterStress();
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

		pmFAO.cropCoefficient = inputs.getParameterDouble("cropCoefficient");
		// PmFAO.waterWiltingPoint = 0.05;
		// PmFAO.waterFieldCapacity = 0.27;
		pmFAO.rootsDepth = inputs.getParameterDouble("rootsDepth");
		// PmFAO.depletionFraction = 0.55;
		inputReader.canopyHeight = inputs.getParameterDouble("canopyHeight");
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;
		// PmFAO.defaultAtmosphericPressure = 101.3;
		// PmFAO.doHourly = true;

		outputWriter.doPrintOutputPM = true;

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries("airTemperature", startDate, endDate, 1000);
				GeopackageTimeseriesIterator windIt = inputs.iterateTimeseries("windVelocity", startDate, endDate, 1000);
				GeopackageTimeseriesIterator humIt = inputs.iterateTimeseries("relativeHumidity", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries("netRadiation", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries("atmosphericPressure", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries("soilMoisture", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries("soilFlux", startDate, endDate, 1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.parameters = inputs.getParameters();

			while (tempIt.next()) {
				windIt.next();
				humIt.next();
				netradIt.next();
				pressureIt.next();
				soilMoistureIt.next();
				soilFluxIt.next();

				inputReader.inAirTemperature = one(STATION_ID, tempIt.value());
				inputReader.inWindVelocity = one(STATION_ID, windIt.value());
				inputReader.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputReader.inNetRadiation = one(STATION_ID, netradIt.value());
				inputReader.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputReader.inSoilMoisture = one(STATION_ID, soilMoistureIt.value());
				inputReader.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputReader.process();
				pmFAO.process();
				outputWriter.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = outputWriter.outEvapoTranspirationPM.get(STATION_ID)[0];
				outputs.fluxEvapoTranspiration = outputWriter.outLatentHeatPM.get(STATION_ID)[0];
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/" + getClass().getSimpleName() + "/ETwaterStressedFAO.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPO_TRANSPIRATION);
		assertGpkgColumnMatchesGolden("/golden/" + getClass().getSimpleName() + "/FluxETwaterStressedFAO.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPO_TRANSPIRATION);
	}

}
