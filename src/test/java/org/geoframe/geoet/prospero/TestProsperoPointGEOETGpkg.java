package org.geoframe.geoet.prospero;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.state.ETCurrentStepInput;
import org.geoframe.geoet.core.config.Leaf;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ETProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.solvers.ProsperoSolver;
import org.geoframe.geoet.solvers.ProsperoStressFactorSolver;
import org.hortonmachine.dbs.utils.DbTimeseriesIterator;
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
		ETProblemQuantities variables = new ETProblemQuantities();
		ETCurrentStepInput input = new ETCurrentStepInput();
		Leaf leaf = new Leaf();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/Input/gpkg/ProsperoPointGEOET.gpkg"));
		inputs.read();

		String startDate = inputs.getParameterString(GeoetInputsHandler.PARAM_START_DATE);
		String endDate = inputs.getParameterString(GeoetInputsHandler.PARAM_END_DATE);
		int timeStepMinutes = inputs.getParameterInt(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES);

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
		inputPreprocessor.elevation = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ELEVATION);
		inputPreprocessor.latitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LATITUDE);
		inputPreprocessor.longitude = inputs.getParameterDouble(GeoetInputsHandler.PARAM_LONGITUDE);
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		inputPreprocessor.canopyHeight = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CANOPY_HEIGHT);
		prospero.typeOfCanopy = inputs.getParameterString(GeoetInputsHandler.PARAM_TYPE_OF_CANOPY);

		prosperoStressFactor.defaultStress = inputs.getParameterDouble(GeoetInputsHandler.PARAM_DEFAULT_STRESS);
		prosperoStressFactor.useRadiationStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_RADIATION_STRESS) != 0;
		prosperoStressFactor.useTemperatureStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_TEMPERATURE_STRESS) != 0;
		prosperoStressFactor.useVDPStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_VDP_STRESS) != 0;
		prosperoStressFactor.useWaterStress = inputs.getParameterInt(GeoetInputsHandler.PARAM_USE_WATER_STRESS) != 0;
		prosperoStressFactor.alpha = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ALPHA);
		prosperoStressFactor.theta = inputs.getParameterDouble(GeoetInputsHandler.PARAM_THETA);
		prosperoStressFactor.VPD0 = inputs.getParameterDouble(GeoetInputsHandler.PARAM_VPD0);
		prosperoStressFactor.Tl = inputs.getParameterDouble(GeoetInputsHandler.PARAM_TL);
		prosperoStressFactor.T0 = inputs.getParameterDouble(GeoetInputsHandler.PARAM_T0);
		prosperoStressFactor.Th = inputs.getParameterDouble(GeoetInputsHandler.PARAM_TH);
		prosperoStressFactor.waterWiltingPoint = inputs.getParameterDouble(GeoetInputsHandler.PARAM_WATER_WILTING_POINT);
		prosperoStressFactor.waterFieldCapacity = inputs.getParameterDouble(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY);
		prosperoStressFactor.rootsDepth = inputs.getParameterDouble(GeoetInputsHandler.PARAM_ROOTS_DEPTH);
		prosperoStressFactor.depletionFraction = inputs.getParameterDouble(GeoetInputsHandler.PARAM_DEPLETION_FRACTION);
		prosperoStressFactor.cropCoefficient = inputs.getParameterDouble(GeoetInputsHandler.PARAM_CROP_COEFFICIENT);

		try (inputs;
				DbTimeseriesIterator tempIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_AIR_TEMPERATURE, startDate, endDate, 1000);
				DbTimeseriesIterator windIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_WIND_VELOCITY, startDate, endDate, 1000);
				DbTimeseriesIterator humIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, startDate, endDate,
						1000);
				DbTimeseriesIterator swDirectIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SHORT_WAVE_RADIATION_DIRECT, startDate,
						endDate, 1000);
				DbTimeseriesIterator swDiffuseIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SHORT_WAVE_RADIATION_DIFFUSE, startDate,
						endDate, 1000);
				DbTimeseriesIterator lwIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_LONG_WAVE_RADIATION, startDate, endDate, 1000);
				DbTimeseriesIterator netradIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_NET_RADIATION, startDate, endDate,
						1000);
				DbTimeseriesIterator soilFluxIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_FLUX, startDate, endDate, 1000);
				DbTimeseriesIterator pressureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, startDate,
						endDate, 1000);
				DbTimeseriesIterator laiIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_LEAF_AREA_INDEX, startDate, endDate, 1000);
				DbTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries(GeoetInputsHandler.VAR_SOIL_MOISTURE, startDate, endDate,
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
