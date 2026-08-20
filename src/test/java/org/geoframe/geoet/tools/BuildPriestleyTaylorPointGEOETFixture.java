package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code PriestleyTaylorPointGEOET.gpkg} fixture,
 * baking in the same CSVs and scalar literals {@code
 * TestPriestleyTaylorPointGEOET} currently hardcodes. Unlike {@code
 * TestPriestleyTaylorGEOET}, no DEM/shapefile is read at all here, and
 * latitude/longitude are never set (left at {@code InputReader}'s own
 * {@code NaN} default) - only elevation is set, so this fixture's
 * {@code parameters} table carries no latitude/longitude columns either.
 */
public class BuildPriestleyTaylorPointGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/input/gpkg/PriestleyTaylorPointGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2013-12-15 00:00");
		parameters.put("endDate", "2013-12-16 00:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 579.0);
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
