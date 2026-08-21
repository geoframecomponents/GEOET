package org.geoframe.geoet.prospero;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.config.Leaf;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.ProsperoSolver;
import org.geoframe.geoet.solvers.ProsperoStressFactorSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test ProsperoSolver using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Michele Bottazzi
 * @author Andrea Antonello
 */
public class TestProsperoPointGEOETGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();
		Leaf leaf = new Leaf();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/ProsperoPointGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("ProsperoPointGEOET.gpkg");

		ProsperoStressFactorSolver prosperoStressFactor = new ProsperoStressFactorSolver();
		prosperoStressFactor.variables = variables;
		prosperoStressFactor.input = input;
		ProsperoSolver prospero = new ProsperoSolver();
		prospero.parameters = parameters;
		prospero.variables = variables;
		prospero.input = input;
		prospero.leafparameters = leaf;

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		// no DEM/shapefile: elevation/latitude/longitude are plain literals here too
		inputPreprocessor.elevation = inputs.getParameterDouble("elevation");
		inputPreprocessor.latitude = inputs.getParameterDouble("latitude");
		inputPreprocessor.longitude = inputs.getParameterDouble("longitude");
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		inputPreprocessor.canopyHeight = inputs.getParameterDouble("canopyHeight");
		prospero.typeOfCanopy = inputs.getParameterString("typeOfCanopy");

		prosperoStressFactor.defaultStress = inputs.getParameterDouble("defaultStress");
		prosperoStressFactor.useRadiationStress = inputs.getParameterInt("useRadiationStress") != 0;
		prosperoStressFactor.useTemperatureStress = inputs.getParameterInt("useTemperatureStress") != 0;
		prosperoStressFactor.useVDPStress = inputs.getParameterInt("useVDPStress") != 0;
		prosperoStressFactor.useWaterStress = inputs.getParameterInt("useWaterStress") != 0;
		prosperoStressFactor.alpha = inputs.getParameterDouble("alpha");
		prosperoStressFactor.theta = inputs.getParameterDouble("theta");
		prosperoStressFactor.VPD0 = inputs.getParameterDouble("VPD0");
		prosperoStressFactor.Tl = inputs.getParameterDouble("Tl");
		prosperoStressFactor.T0 = inputs.getParameterDouble("T0");
		prosperoStressFactor.Th = inputs.getParameterDouble("Th");
		prosperoStressFactor.waterWiltingPoint = inputs.getParameterDouble("waterWiltingPoint");
		prosperoStressFactor.waterFieldCapacity = inputs.getParameterDouble("waterFieldCapacity");
		prosperoStressFactor.rootsDepth = inputs.getParameterDouble("rootsDepth");
		prosperoStressFactor.depletionFraction = inputs.getParameterDouble("depletionFraction");
		prosperoStressFactor.cropCoefficient = inputs.getParameterDouble("cropCoefficient");

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries("airTemperature", startDate, endDate, 1000);
				GeopackageTimeseriesIterator windIt = inputs.iterateTimeseries("windVelocity", startDate, endDate, 1000);
				GeopackageTimeseriesIterator humIt = inputs.iterateTimeseries("relativeHumidity", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator swDirectIt = inputs.iterateTimeseries("shortWaveRadiationDirect", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator swDiffuseIt = inputs.iterateTimeseries("shortWaveRadiationDiffuse", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator lwIt = inputs.iterateTimeseries("longWaveRadiation", startDate, endDate, 1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries("netRadiation", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries("soilFlux", startDate, endDate, 1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries("atmosphericPressure", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator laiIt = inputs.iterateTimeseries("leafAreaIndex", startDate, endDate, 1000);
				GeopackageTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries("soilMoisture", startDate, endDate,
						1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.parameters = inputs.getParameters();

			while (tempIt.next()) {
				windIt.next();
				humIt.next();
				swDirectIt.next();
				swDiffuseIt.next();
				lwIt.next();
				netradIt.next();
				soilFluxIt.next();
				pressureIt.next();
				laiIt.next();
				soilMoistureIt.next();

				inputPreprocessor.inAirTemperature = one(STATION_ID, tempIt.value());
				inputPreprocessor.inWindVelocity = one(STATION_ID, windIt.value());
				inputPreprocessor.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputPreprocessor.inShortWaveRadiationDirect = one(STATION_ID, swDirectIt.value());
				inputPreprocessor.inShortWaveRadiationDiffuse = one(STATION_ID, swDiffuseIt.value());
				inputPreprocessor.inLongWaveRadiation = one(STATION_ID, lwIt.value());
				inputPreprocessor.inSoilFlux = one(STATION_ID, soilFluxIt.value());
				inputPreprocessor.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputPreprocessor.inLeafAreaIndex = one(STATION_ID, laiIt.value());
				inputPreprocessor.inNetRadiation = one(STATION_ID, netradIt.value());
				inputPreprocessor.inSoilMoisture = one(STATION_ID, soilMoistureIt.value());

				inputPreprocessor.process();

				prosperoStressFactor.solve();
				prospero.stressSun = prosperoStressFactor.stressSun;
				prospero.stressShade = prosperoStressFactor.stressShade;
				prospero.process();

				outputs.timestamp = tempIt.timestamp();
				outputs.latentHeatSun = variables.latentHeatFluxSun;
				outputs.latentHeatShade = variables.latentHeatFluxShade;
				outputs.transpiration = variables.transpiration;
				outputs.fluxTranspiration = variables.fluxTranspiration;
				outputs.leafTemperatureSun = variables.leafTemperatureSun;
				outputs.leafTemperatureShade = variables.leafTemperatureShade;
				outputs.radiationSun = variables.shortwaveCanopySun;
				outputs.radiationShade = variables.shortwaveCanopyShade;
				outputs.sensibleHeatSun = variables.sensibleHeatFluxSun;
				outputs.sensibleHeatShade = variables.sensibleHeatFluxShade;
				outputs.radiationSoil = variables.incidentSolarRadiationSoil;
				outputs.canopy = variables.areaCanopySun;
				outputs.vpd = variables.vapourPressureDeficit;
				outputs.write();
			}
		}

		String goldenDir = "/golden/TestProsperoPointGEOET/";
		String lab = "test.csv";
		assertGpkgColumnMatchesGolden(goldenDir + "LatentHeatSun" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_LATENT_HEAT_SUN);
		assertGpkgColumnMatchesGolden(goldenDir + "LatentHeatShadow" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_LATENT_HEAT_SHADE);
		assertGpkgColumnMatchesGolden(goldenDir + "Transpiration" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_TRANSPIRATION);
		assertGpkgColumnMatchesGolden(goldenDir + "FluxTranspiration" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_FLUX_TRANSPIRATION);
		assertGpkgColumnMatchesGolden(goldenDir + "LeafTemperatureSun" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_LEAF_TEMPERATURE_SUN);
		assertGpkgColumnMatchesGolden(goldenDir + "LeafTemperatureSh" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_LEAF_TEMPERATURE_SHADE);
		assertGpkgColumnMatchesGolden(goldenDir + "RadSun" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_RADIATION_SUN);
		assertGpkgColumnMatchesGolden(goldenDir + "RadShadow" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_RADIATION_SHADE);
		assertGpkgColumnMatchesGolden(goldenDir + "sensibleSun" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_SENSIBLE_HEAT_SUN);
		assertGpkgColumnMatchesGolden(goldenDir + "sensibleShadow" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_SENSIBLE_HEAT_SHADE);
		assertGpkgColumnMatchesGolden(goldenDir + "RadiationSoil" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_RADIATION_SOIL);
		assertGpkgColumnMatchesGolden(goldenDir + "Canopy" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP,
				GeoetOutputsHandler.COL_CANOPY);
		assertGpkgColumnMatchesGolden(goldenDir + "VPD" + lab, pathToOutputGpkg,
				GeoetOutputsHandler.TABLE_OUTPUT_RESULTS, GeoetOutputsHandler.COL_TIMESTAMP, GeoetOutputsHandler.COL_VPD);
	}

}
