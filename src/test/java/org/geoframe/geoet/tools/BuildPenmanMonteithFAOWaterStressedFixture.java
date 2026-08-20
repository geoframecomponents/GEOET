package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code PenmanMonteithFAOWaterStressed.gpkg} pilot
 * fixture, baking in the same CSVs and scalar literals
 * {@code TestPenmanMonteithFAOWaterStressed} currently hardcodes. Not a
 * JUnit test (no {@code @Test} method) - run it directly:
 *
 * <pre>
 * java -cp target/test-classes:target/classes:$(cat /tmp/cp.txt) \
 *     org.geoframe.geoet.tools.BuildPenmanMonteithFAOWaterStressedFixture
 * </pre>
 */
public class BuildPenmanMonteithFAOWaterStressedFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/input/gpkg/PenmanMonteithFAOWaterStressed.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2014-01-01 00:00");
		parameters.put("endDate", "2014-01-02 00:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("cropCoefficient", 0.75);
		parameters.put("rootsDepth", 0.75);
		parameters.put("canopyHeight", 0.12);
		parameters.put("soilFluxParameterDay", 0.35);
		parameters.put("soilFluxParameterNight", 0.75);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put("airTemperature", resIn + "airT_1.csv");
		timeseries.put("windVelocity", resIn + "Wind_1.csv");
		timeseries.put("relativeHumidity", resIn + "RH_1.csv");
		timeseries.put("netRadiation", resIn + "Net_1.csv");
		timeseries.put("atmosphericPressure", resIn + "Pres_1.csv");
		timeseries.put("soilFlux", resIn + "nan.csv");
		timeseries.put("soilMoisture", resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
