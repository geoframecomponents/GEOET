package org.geoframe.geoet.prospero;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.data.Leaf;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.solvers.ProsperoSolverWithExplicitLAI;
import org.geoframe.geoet.solvers.ProsperoStressFactorSolver;
import org.geoframe.geoet.solvers.RadiationSolver;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.junit.Test;

/**
 * Test ProsperoSolverWithExplicitLAI using single geopackages as input and output files.
 *
 * @author D'Amato Concetta
 * @author Michele Bottazzi
 * @author Andrea Antonello
 */
public class TestProsperoLAIGEOET_CavoneGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();
		Leaf leaf = new Leaf();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/ProsperoLAIGEOET_Cavone.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString("startDate");
		String endDate = inputs.getParameterString("endDate");
		int timeStepMinutes = inputs.getParameterInt("timeStepMinutes");

		String pathToOutputGpkg = getOutRes("ProsperoLAIGEOET_Cavone.gpkg");

		RadiationSolver radiation = new RadiationSolver();
		radiation.parameters = parameters;
		radiation.variables = variables;
		radiation.input = input;

		ProsperoStressFactorSolver prosperoStressFactor = new ProsperoStressFactorSolver();
		prosperoStressFactor.variables = variables;
		prosperoStressFactor.input = input;

		ProsperoSolverWithExplicitLAI prospero = new ProsperoSolverWithExplicitLAI();
		prospero.parameters = parameters;
		prospero.variables = variables;
		prospero.input = input;
		prospero.leafparameters = leaf;

		InputReader inputReader = new InputReader();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputReader.elevation = inputs.getParameterDouble("elevation");
		inputReader.latitude = inputs.getParameterDouble("latitude");
		inputReader.longitude = inputs.getParameterDouble("longitude");
		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;

		inputReader.canopyHeight = inputs.getParameterDouble("canopyHeight");
		String typeOfCanopy = inputs.getParameterString("typeOfCanopy");
		prospero.typeOfCanopy = typeOfCanopy;
		radiation.typeOfCanopy = typeOfCanopy;

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

				inputReader.inAirTemperature = one(STATION_ID, tempIt.value());
				inputReader.inWindVelocity = one(STATION_ID, windIt.value());
				inputReader.inRelativeHumidity = one(STATION_ID, humIt.value());
				inputReader.inShortWaveRadiationDirect = one(STATION_ID, swDirectIt.value());
				inputReader.inShortWaveRadiationDiffuse = one(STATION_ID, swDiffuseIt.value());
				inputReader.inLongWaveRadiation = one(STATION_ID, lwIt.value());
				inputReader.inSoilFlux = one(STATION_ID, soilFluxIt.value());
				inputReader.inAtmosphericPressure = one(STATION_ID, pressureIt.value());
				inputReader.inLeafAreaIndex = one(STATION_ID, laiIt.value());
				inputReader.inNetRadiation = one(STATION_ID, netradIt.value());
				inputReader.inSoilMoisture = one(STATION_ID, soilMoistureIt.value());

				inputReader.process();
				radiation.process();

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

		String goldenDir = "/golden/TestProsperoLAIGEOET_Cavone/";
		String lab = "actual_LAI_20_30928.csv";
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
