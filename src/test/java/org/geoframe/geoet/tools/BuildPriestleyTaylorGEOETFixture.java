package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

/**
 * One-off generator for the {@code PriestleyTaylorGEOET.gpkg} fixture,
 * baking in the same CSVs, elevation/latitude/longitude (from the same
 * {@code dataET_point/1/} DEM/shapefile pair) and scalar literals {@code
 * TestPriestleyTaylorGEOET} currently hardcodes.
 */
public class BuildPriestleyTaylorGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/PriestleyTaylorGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2013-12-15 00:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2013-12-16 00:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 536.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 40.50554583408996);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 16.245253020414495);
		parameters.put(GeoetInputsHandler.PARAM_ALPHA, 1.26);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_DAY, 0.35);
		parameters.put(GeoetInputsHandler.PARAM_SOIL_FLUX_PARAMETER_NIGHT, 0.75);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put(GeoetInputsHandler.VAR_AIR_TEMPERATURE, resIn + "airT_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_NET_RADIATION, resIn + "Net_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, resIn + "Pres_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_FLUX, resIn + "GHF_1.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
