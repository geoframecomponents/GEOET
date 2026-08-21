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
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
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

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

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
		inputPreprocessor.elevation = inputs.getParameterDouble("elevation");
		inputPreprocessor.latitude = inputs.getParameterDouble("latitude");
		inputPreprocessor.longitude = inputs.getParameterDouble("longitude");
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		pmStressfactor.defaultStress = inputs.getParameterDouble("defaultStress");
		pmStressfactor.useRadiationStress = inputs.getParameterInt("useRadiationStress") != 0;
		pmStressfactor.useTemperatureStress = inputs.getParameterInt("useTemperatureStress") != 0;
		pmStressfactor.useVDPStress = inputs.getParameterInt("useVDPStress") != 0;
		pmStressfactor.useWaterStress = inputs.getParameterInt("useWaterStress") != 0;
		pmStressfactor.alpha = inputs.getParameterDouble("alpha");
		pmStressfactor.theta = inputs.getParameterDouble("theta");
		pmStressfactor.VPD0 = inputs.getParameterDouble("VPD0");
		pmStressfactor.Tl = inputs.getParameterDouble("Tl");
		pmStressfactor.T0 = inputs.getParameterDouble("T0");
		pmStressfactor.Th = inputs.getParameterDouble("Th");
		pmStressfactor.waterWiltingPoint = inputs.getParameterDouble("waterWiltingPoint");
		pmStressfactor.waterFieldCapacity = inputs.getParameterDouble("waterFieldCapacity");
		pmStressfactor.depth = inputs.getParameterDouble("depth");
		pmStressfactor.depletionFraction = inputs.getParameterDouble("depletionFraction");
		pmStressfactor.cropCoefficient = inputs.getParameterDouble("cropCoefficient");

		inputPreprocessor.canopyHeight = inputs.getParameterDouble("canopyHeight");
		pmFAO.soilFluxParameterDay = inputs.getParameterDouble("soilFluxParameterDay");
		pmFAO.soilFluxParameterNight = inputs.getParameterDouble("soilFluxParameterNight");

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
