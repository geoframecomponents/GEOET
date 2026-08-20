package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code PMEvaporationFromSoilGEOET.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) {@code
 * TestPMEvaporationFromSoilGEOET} currently hardcodes. That test sets no
 * scalar solver parameters at all (the solver runs off its own defaults),
 * so this fixture's {@code parameters} table only carries the run window
 * and the station's elevation/lat/lon.
 */
public class BuildPMEvaporationFromSoilGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/input/gpkg/PMEvaporationFromSoilGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2014-01-01 09:00");
		parameters.put("endDate", "2014-01-01 11:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put("airTemperature", resIn + "airT_1.csv");
		timeseries.put("windVelocity", resIn + "Wind_1.csv");
		timeseries.put("relativeHumidity", resIn + "RH_1.csv");
		timeseries.put("netRadiation", resIn + "Net_1.csv");
		timeseries.put("atmosphericPressure", resIn + "Pres_1.csv");
		timeseries.put("soilFlux", resIn + "GHF_1.csv");
		timeseries.put("soilMoisture", resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
