package org.geoframe.geoet.penmanmonteithfao;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test PenmanMonteithFAOSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPenmanMonteithFAOPotentialETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PenmanMonteithFAOPotentialET.gpkg"));
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

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputReader.elevation = inputs.getParameterDouble("elevation");
		inputReader.latitude = inputs.getParameterDouble("latitude");
		inputReader.longitude = inputs.getParameterDouble("longitude");

		inputReader.canopyHeight = inputs.getParameterDouble("canopyHeight");
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;

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

				inputReader.inAirTemperature = one(STATION_ID, tempIt.value());
				inputReader.inWindVelocity = one(STATION_ID, windIt.value());
				inputReader.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputReader.inNetRadiation = one(STATION_ID, netradIt.value());
				inputReader.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputReader.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputReader.process();
				pmFAO.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = variables.evapoTranspirationPM;
				outputs.fluxEvapoTranspiration = variables.fluxEvapoTranspirationPM;
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


}
