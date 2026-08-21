package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

/**
 * One-off generator for the {@code PenmanMonteithFAOTotalStressed.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) and scalar literals
 * {@code TestPenmanMonteithFAOTotalStressed} currently hardcodes - including
 * the {@code PriestleyTaylorPenmanMonteithFAOStressFactorSolver} parameters
 * that feed the stress factor into the FAO solver. 
 */
public class BuildPenmanMonteithFAOTotalStressedFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/PenmanMonteithFAOTotalStressed.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2013-12-15 00:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2015-12-16 00:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 536.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 40.50554583408996);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 16.245253020414495);
		parameters.put(GeoetInputsHandler.PARAM_CANOPY_HEIGHT, 1.30);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY, 0.35);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT, 0.75);
		// PriestleyTaylorPenmanMonteithFAOStressFactorSolver parameters
		parameters.put(GeoetInputsHandler.PARAM_DEFAULT_STRESS, 1.0);
		parameters.put(GeoetInputsHandler.PARAM_USE_RADIATION_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_TEMPERATURE_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_VDP_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_USE_WATER_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_ALPHA, 0.005);
		parameters.put(GeoetInputsHandler.PARAM_THETA, 0.85);
		parameters.put(GeoetInputsHandler.PARAM_VPD0, 5.0);
		parameters.put(GeoetInputsHandler.PARAM_TL, -5.0);
		parameters.put(GeoetInputsHandler.PARAM_T0, 15.0);
		parameters.put(GeoetInputsHandler.PARAM_TH, 35.0);
		parameters.put(GeoetInputsHandler.PARAM_WATER_WILTING_POINT, 0.10);
		parameters.put(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY, 0.25);
		parameters.put(GeoetInputsHandler.PARAM_DEPTH, 1.30);
		parameters.put(GeoetInputsHandler.PARAM_DEPLETION_FRACTION, 0.70);
		parameters.put(GeoetInputsHandler.PARAM_CROP_COEFFICIENT, 0.95);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put(GeoetInputsHandler.VAR_AIR_TEMPERATURE, resIn + "airT_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_WIND_VELOCITY, resIn + "Wind_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, resIn + "RH_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_NET_RADIATION, resIn + "Net_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, resIn + "Pres_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_MOISTURE, resIn + "SoilMoisture18.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_FLUX, resIn + "GHF_1.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
