package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

/**
 * One-off generator for the {@code ProsperoPointGEOET.gpkg} fixture,
 * baking in the same CSVs and scalar literals {@code TestProsperoPointGEOET}
 * currently hardcodes. Unlike {@code TestProsperoGEOET_Cavone}, no
 * DEM/shapefile is read at all here - elevation/latitude/longitude are
 * explicit literals in the original test, so they're plain fixture
 * parameters here too (same values, not derived from any raster/shapefile).
 */
public class BuildProsperoPointGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/ProsperoPointGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2013-12-15 11:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2013-12-16 11:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 579.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 37.97);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 13.57);
		parameters.put(GeoetInputsHandler.PARAM_CANOPY_HEIGHT, 30.0);
		parameters.put(GeoetInputsHandler.PARAM_TYPE_OF_CANOPY, "multilayer");
		parameters.put(GeoetInputsHandler.PARAM_DEFAULT_STRESS, 1.0);
		parameters.put(GeoetInputsHandler.PARAM_USE_RADIATION_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_TEMPERATURE_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_VDP_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_WATER_STRESS, 1);
		parameters.put(GeoetInputsHandler.PARAM_ALPHA, 0.005);
		parameters.put(GeoetInputsHandler.PARAM_THETA, 0.9);
		parameters.put(GeoetInputsHandler.PARAM_VPD0, 5.0);
		parameters.put(GeoetInputsHandler.PARAM_TL, -5.0);
		parameters.put(GeoetInputsHandler.PARAM_T0, 20.0);
		parameters.put(GeoetInputsHandler.PARAM_TH, 45.0);
		parameters.put(GeoetInputsHandler.PARAM_WATER_WILTING_POINT, 0.08);
		parameters.put(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY, 0.27);
		parameters.put(GeoetInputsHandler.PARAM_ROOTS_DEPTH, 1.0);
		parameters.put(GeoetInputsHandler.PARAM_DEPLETION_FRACTION, 0.55);
		parameters.put(GeoetInputsHandler.PARAM_CROP_COEFFICIENT, 0.85);

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
		timeseries.put(GeoetInputsHandler.VAR_LEAF_AREA_INDEX, resIn + "LAI_10.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_MOISTURE, resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
