package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

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
		parameters.put("startDate", "2013-12-15 00:00");
		parameters.put("endDate", "2013-12-16 00:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("alpha", 1.26);
		parameters.put("soilFluxParameterDay", 0.35);
		parameters.put("soilFluxParameterNight", 0.75);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put("airTemperature", resIn + "airT_1.csv");
		timeseries.put("netRadiation", resIn + "Net_1.csv");
		timeseries.put("atmosphericPressure", resIn + "Pres_1.csv");
		timeseries.put("soilFlux", resIn + "GHF_1.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
