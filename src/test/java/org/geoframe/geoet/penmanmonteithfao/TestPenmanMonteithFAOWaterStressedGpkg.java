package org.geoframe.geoet.penmanmonteithfao;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithFAOWaterStress;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test PenmanMonteithFAOSolverWithFAOWaterStress using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPenmanMonteithFAOWaterStressedGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PenmanMonteithFAOWaterStressed.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString(GeoetInputsHandler.PARAM_START_DATE);
		String endDate = inputs.getParameterString(GeoetInputsHandler.PARAM_END_DATE);
		int timeStepMinutes = inputs.getParameterInt(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES);

		String pathToOutputGpkg = getOutRes("PenmanMonteithFAOWaterStressed.gpkg");

		PenmanMonteithFAOSolverWithFAOWaterStress pmFAO = new PenmanMonteithFAOSolverWithFAOWaterStress();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputPreprocessor.elevation = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ELEVATION);
		inputPreprocessor.latitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LATITUDE);
		inputPreprocessor.longitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LONGITUDE);

		pmFAO.cropCoefficient = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CROP_COEFFICIENT);
		// PmFAO.waterWiltingPoint = 0.05;
		// PmFAO.waterFieldCapacity = 0.27;
		pmFAO.rootsDepth = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ROOTS_DEPTH);
		// PmFAO.depletionFraction = 0.55;
		inputPreprocessor.canopyHeight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CANOPY_HEIGHT);
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY);
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT);

		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;
		// PmFAO.defaultAtmosphericPressure = 101.3;
		// PmFAO.doHourly = true;

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_AIR_TEMPERATURE, startDate, endDate, 1000);
				GeopackageTimeseriesIterator windIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_WIND_VELOCITY, startDate, endDate, 1000);
				GeopackageTimeseriesIterator humIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, startDate, endDate,
						1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_NET_RADIATION, startDate, endDate,
						1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_MOISTURE, startDate, endDate,
						1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_FLUX, startDate, endDate, 1000);
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
				pmFAO.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = variables.evapoTranspirationPM;
				outputs.fluxEvapoTranspiration = variables.fluxEvapoTranspirationPM;
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
