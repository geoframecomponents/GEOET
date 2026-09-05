package org.geoframe.geoet.evaporationfromsoil;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.ETCurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ETProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSoilEvaporationSolver;
import org.hortonmachine.dbs.utils.DbTimeseriesIterator;
import org.junit.Test;

/**
 * Test PenmanMonteithFAOSoilEvaporationSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPMEvaporationFromSoilGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ETProblemQuantities variables = new ETProblemQuantities();
		ETCurrentStepInput input = new ETCurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PMEvaporationFromSoilGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString(GeoetInputsHandler.PARAM_START_DATE);
		String endDate = inputs.getParameterString(GeoetInputsHandler.PARAM_END_DATE);
		int timeStepMinutes = inputs.getParameterInt(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES);

		String pathToOutputGpkg = getOutRes("PMEvaporationFromSoilGEOET.gpkg");

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		PenmanMonteithFAOSoilEvaporationSolver pmSoilevaporation = new PenmanMonteithFAOSoilEvaporationSolver();
		pmSoilevaporation.parameters = parameters;
		pmSoilevaporation.variables = variables;
		pmSoilevaporation.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputPreprocessor.elevation = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ELEVATION);
		inputPreprocessor.latitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LATITUDE);
		inputPreprocessor.longitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LONGITUDE);
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		try (inputs;
				DbTimeseriesIterator tempIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_AIR_TEMPERATURE, startDate, endDate, 1000);
				DbTimeseriesIterator windIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_WIND_VELOCITY, startDate, endDate, 1000);
				DbTimeseriesIterator humIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, startDate, endDate,
						1000);
				DbTimeseriesIterator netradIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_NET_RADIATION, startDate, endDate,
						1000);
				DbTimeseriesIterator pressureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, startDate,
						endDate, 1000);
				DbTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_MOISTURE, startDate, endDate,
						1000);
				DbTimeseriesIterator soilFluxIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_FLUX, startDate, endDate, 1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.parameters = inputs.getParameters();

			while (tempIt.next()) {
				windIt.next();
				humIt.next();
				netradIt.next();
				pressureIt.next();
				soilMoistureIt.next();
				soilFluxIt.next();

				inputPreprocessor.inAirTemperature = one(STATION_ID, tempIt.value());
				inputPreprocessor.inWindVelocity = one(STATION_ID, windIt.value());
				inputPreprocessor.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputPreprocessor.inNetRadiation = one(STATION_ID, netradIt.value());
				inputPreprocessor.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputPreprocessor.inSoilMoisture = one(STATION_ID, soilMoistureIt.value());
				inputPreprocessor.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputPreprocessor.process();
				pmSoilevaporation.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evaporation = variables.evaporation;
				outputs.fluxEvaporation = variables.fluxEvaporation;
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/TestPMEvaporationFromSoilGEOET/Evaporation.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPORATION);
		assertGpkgColumnMatchesGolden("/golden/TestPMEvaporationFromSoilGEOET/FluxEvaporation.csv", pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPORATION);
	}

}
