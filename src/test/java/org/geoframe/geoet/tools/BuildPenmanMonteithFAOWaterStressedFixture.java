package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

/**
 * One-off generator for the {@code PenmanMonteithFAOWaterStressed.gpkg} pilot
 * fixture, baking in the same CSVs and scalar literals
 * {@code TestPenmanMonteithFAOWaterStressed} currently hardcodes. 
 */
public class BuildPenmanMonteithFAOWaterStressedFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/PenmanMonteithFAOWaterStressed.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2014-01-01 00:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2014-01-02 00:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 536.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 40.50554583408996);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 16.245253020414495);
		parameters.put(GeoetInputsHandler.PARAM_CROP_COEFFICIENT, 0.75);
		parameters.put(GeoetInputsHandler.PARAM_ROOTS_DEPTH, 0.75);
		parameters.put(GeoetInputsHandler.PARAM_CANOPY_HEIGHT, 0.12);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY, 0.35);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT, 0.75);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put(GeoetInputsHandler.VAR_AIR_TEMPERATURE, resIn + "airT_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_WIND_VELOCITY, resIn + "Wind_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, resIn + "RH_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_NET_RADIATION, resIn + "Net_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, resIn + "Pres_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_FLUX, resIn + "nan.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_MOISTURE, resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
