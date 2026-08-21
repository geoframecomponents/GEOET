package org.geoframe.geoet.penmanmonteithfao;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithStressFactor;
import org.geoframe.geoet.solvers.PriestleyTaylorPenmanMonteithFAOStressFactorSolver;
import org.hortonmachine.dbs.utils.DbTimeseriesIterator;
import org.junit.Test;

/**
 * Test PenmanMonteithFAOSolverWithStressFactor using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPenmanMonteithFAOTotalStressedGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/PenmanMonteithFAOTotalStressed.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString(GeoetInputsHandler.PARAM_START_DATE);
		String endDate = inputs.getParameterString(GeoetInputsHandler.PARAM_END_DATE);
		int timeStepMinutes = inputs.getParameterInt(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES);

		String pathToOutputGpkg = getOutRes("PenmanMonteithFAOTotalStressed.gpkg");

		PenmanMonteithFAOSolverWithStressFactor pmFAO = new PenmanMonteithFAOSolverWithStressFactor();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;
		PriestleyTaylorPenmanMonteithFAOStressFactorSolver pmStressfactor = new PriestleyTaylorPenmanMonteithFAOStressFactorSolver();
		pmStressfactor.variables = variables;
		pmStressfactor.input = input;

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputPreprocessor.elevation = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ELEVATION);
		inputPreprocessor.latitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LATITUDE);
		inputPreprocessor.longitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LONGITUDE);
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		pmStressfactor.defaultStress = inputs.getParameterDouble(GeoetInputsHandler.PARAM_DEFAULT_STRESS);
		pmStressfactor.useRadiationStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_RADIATION_STRESS) != 0;
		pmStressfactor.useTemperatureStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_TEMPERATURE_STRESS) != 0;
		pmStressfactor.useVDPStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_VDP_STRESS) != 0;
		pmStressfactor.useWaterStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_WATER_STRESS) != 0;
		pmStressfactor.alpha = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ALPHA);
		pmStressfactor.theta = inputs.getParameterDouble(GeoetInputsHandler.PARAM_THETA);
		pmStressfactor.VPD0 = inputs.getParameterDouble(GeoetInputsHandler.PARAM_VPD0);
		pmStressfactor.Tl = inputs.getParameterDouble(GeoetInputsHandler.PARAM_TL);
		pmStressfactor.T0 = inputs.getParameterDouble(GeoetInputsHandler.PARAM_T0);
		pmStressfactor.Th = inputs.getParameterDouble(GeoetInputsHandler.PARAM_TH);
		pmStressfactor.waterWiltingPoint = inputs.getParameterDouble(GeoetInputsHandler.PARAM_WATER_WILTING_POINT);
		pmStressfactor.waterFieldCapacity = inputs.getParameterDouble(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY);
		pmStressfactor.depth = inputs.getParameterDouble(GeoetInputsHandler.PARAM_DEPTH);
		pmStressfactor.depletionFraction = inputs.getParameterDouble(GeoetInputsHandler.PARAM_DEPLETION_FRACTION);
		pmStressfactor.cropCoefficient = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CROP_COEFFICIENT);

		inputPreprocessor.canopyHeight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CANOPY_HEIGHT);
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY);
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT);

		try (DbTimeseriesIterator tempIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_AIR_TEMPERATURE, startDate, endDate, 1000);
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
				pmStressfactor.solve();
				pmFAO.stressFactor = pmStressfactor.stressSun;
				pmFAO.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = variables.evapoTranspirationPM;
				outputs.fluxEvapoTranspiration = variables.fluxEvapoTranspirationPM;
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/TestPenmanMonteithFAOTotalStressed/ETPotentialFAOCavone.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPO_TRANSPIRATION);
		assertGpkgColumnMatchesGolden("/golden/TestPenmanMonteithFAOTotalStressed/FluxETPotentialFAOCavone.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPO_TRANSPIRATION);
	}



}
