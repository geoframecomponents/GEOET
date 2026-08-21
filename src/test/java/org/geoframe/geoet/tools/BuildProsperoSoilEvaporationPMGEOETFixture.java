package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

/**
 * One-off generator for the {@code ProsperoSoilEvaporationPMGEOET.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) and scalar literals
 * {@code TestProspero_SoilEvaporationPM_GEOET} currently hardcodes across
 * its four solvers: {@code ProsperoStressFactorSolverWithEvaporation}
 * (stress + evaporation stress), {@code ProsperoSolver} (transpiration),
 * {@code PenmanMonteithFAOSoilEvaporationSolverWithCanopy} (evaporation),
 * and {@code TotalEvapoTranspirationSolver} (combines the two into total
 * evapotranspiration). Note this test uses {@code LAI_1.csv}, not the
 * {@code LAI_10.csv} the Cavone Prospero tests use.
 */
public class BuildProsperoSoilEvaporationPMGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/ProsperoSoilEvaporationPMGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2013-12-15 11:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2013-12-15 12:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 536.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 40.50554583408996);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 16.245253020414495);
		parameters.put(GeoetInputsHandler.PARAM_CANOPY_HEIGHT, 1.26);
		parameters.put(GeoetInputsHandler.PARAM_TYPE_OF_CANOPY, "multilayer");
		parameters.put(GeoetInputsHandler.PARAM_DEFAULT_STRESS, 1.0);
		parameters.put(GeoetInputsHandler.PARAM_USE_RADIATION_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_TEMPERATURE_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_VDP_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_WATER_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_ALPHA, 0.005);
		parameters.put(GeoetInputsHandler.PARAM_THETA, 0.9);
		parameters.put(GeoetInputsHandler.PARAM_VPD0, 5.0);
		parameters.put(GeoetInputsHandler.PARAM_TL, -5.0);
		parameters.put(GeoetInputsHandler.PARAM_T0, 20.0);
		parameters.put(GeoetInputsHandler.PARAM_TH, 45.0);
		parameters.put(GeoetInputsHandler.PARAM_WATER_WILTING_POINT, 0.15);
		parameters.put(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY, 0.27);
		parameters.put(GeoetInputsHandler.PARAM_ROOTS_DEPTH, 0.75);
		parameters.put(GeoetInputsHandler.PARAM_DEPLETION_FRACTION, 0.55);
		parameters.put(GeoetInputsHandler.PARAM_CROP_COEFFICIENT, 0.75);
		parameters.put(GeoetInputsHandler.PARAM_EVAPORATION_DEPTH, 0.5);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put(GeoetInputsHandler.VAR_AIR_TEMPERATURE, resIn + "airT_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_WIND_VELOCITY, resIn + "Wind_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, resIn + "RH_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SHORT_WAVE_RADIATION_DIRECT, resIn + "ShortwaveDirect_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SHORT_WAVE_RADIATION_DIFFUSE, resIn + "ShortwaveDiffuse_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_LONG_WAVE_RADIATION, resIn + "LongDownwelling_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_NET_RADIATION, resIn + "Net_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_FLUX, resIn + "GHF_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, resIn + "Pres_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_LEAF_AREA_INDEX, resIn + "LAI_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_MOISTURE, resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
