package org.geoframe.geoet.evaporationfromsoil;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSoilEvaporationSolver;
import org.geoframe.geoet.solvers.PriestleyTaylorPenmanMonteithFAOStressFactorSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test PenmanMonteithFAOSoilEvaporationSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Andrea Antonello
 */
public class TestPMStressedEvaporationFromSoilGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		GeoetInputsHandler inputs = new GeoetInputsHandler(
				getRes("/Input/gpkg/PMStressedEvaporationFromSoilGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("PMStressedEvaporationFromSoilGEOET.gpkg");

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		PriestleyTaylorPenmanMonteithFAOStressFactorSolver pmWaterStressFactor = new PriestleyTaylorPenmanMonteithFAOStressFactorSolver();
		pmWaterStressFactor.variables = variables;
		pmWaterStressFactor.input = input;
		PenmanMonteithFAOSoilEvaporationSolver pmSoilevaporation = new PenmanMonteithFAOSoilEvaporationSolver();
		pmSoilevaporation.parameters = parameters;
		pmSoilevaporation.variables = variables;
		pmSoilevaporation.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputPreprocessor.elevation = inputs.getParameterDouble("elevation");
		inputPreprocessor.latitude = inputs.getParameterDouble("latitude");
		inputPreprocessor.longitude = inputs.getParameterDouble("longitude");
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		pmWaterStressFactor.defaultStress = inputs.getParameterDouble("defaultStress");
		pmWaterStressFactor.useWaterStress = inputs.getParameterInt("useWaterStress") != 0;
		pmWaterStressFactor.waterWiltingPoint = inputs.getParameterDouble("waterWiltingPoint");
		pmWaterStressFactor.waterFieldCapacity = inputs.getParameterDouble("waterFieldCapacity");
		pmWaterStressFactor.depth = inputs.getParameterDouble("depth");
		pmWaterStressFactor.depletionFraction = inputs.getParameterDouble("depletionFraction");

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

				inputPreprocessor.inAirTemperature = one(STATION_ID, tempIt.value());
				inputPreprocessor.inWindVelocity = one(STATION_ID, windIt.value());
				inputPreprocessor.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputPreprocessor.inNetRadiation = one(STATION_ID, netradIt.value());
				inputPreprocessor.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputPreprocessor.inSoilMoisture = one(STATION_ID, soilMoistureIt.value());
				inputPreprocessor.inSoilFlux = one(STATION_ID, soilFluxIt.value());

				inputPreprocessor.process();
				pmWaterStressFactor.solve();
				pmSoilevaporation.evaporationStressWater = pmWaterStressFactor.stressSun;
				pmSoilevaporation.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.evaporation = variables.evaporation;
				outputs.fluxEvaporation = variables.fluxEvaporation;
				outputs.write();
			}
		}

		assertGpkgColumnMatchesGolden("/golden/TestPMStressedEvaporationFromSoilGEOET/Evaporation.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_EVAPORATION);
		assertGpkgColumnMatchesGolden("/golden/TestPMStressedEvaporationFromSoilGEOET/FluxEvaporation.csv",
				pathToOutputGpkg, GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_EVAPORATION);
	}

}
