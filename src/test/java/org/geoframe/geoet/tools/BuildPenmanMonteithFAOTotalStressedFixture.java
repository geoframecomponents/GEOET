package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code PenmanMonteithFAOTotalStressed.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) and scalar literals
 * {@code TestPenmanMonteithFAOTotalStressed} currently hardcodes - including
 * the {@code PriestleyTaylorPenmanMonteithFAOStressFactorSolver} parameters
 * that feed the stress factor into the FAO solver. Not a JUnit test (no
 * {@code @Test} method) - run it directly:
 *
 * <pre>
 * java -cp target/test-classes:target/classes:$(cat /tmp/cp.txt) \
 *     org.geoframe.geoet.tools.BuildPenmanMonteithFAOTotalStressedFixture
 * </pre>
 */
public class BuildPenmanMonteithFAOTotalStressedFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/input/gpkg/PenmanMonteithFAOTotalStressed.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2013-12-15 00:00");
		parameters.put("endDate", "2015-12-16 00:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("canopyHeight", 1.30);
		parameters.put("soilFluxParameterDay", 0.35);
		parameters.put("soilFluxParameterNight", 0.75);
		// PriestleyTaylorPenmanMonteithFAOStressFactorSolver parameters
		parameters.put("defaultStress", 1.0);
		parameters.put("useRadiationStress", 0);
		parameters.put("useTemperatureStress", 0);
		parameters.put("useVDPStress", 0);
		parameters.put("useWaterStress", 0);
		parameters.put("alpha", 0.005);
		parameters.put("theta", 0.85);
		parameters.put("VPD0", 5.0);
		parameters.put("Tl", -5.0);
		parameters.put("T0", 15.0);
		parameters.put("Th", 35.0);
		parameters.put("waterWiltingPoint", 0.10);
		parameters.put("waterFieldCapacity", 0.25);
		parameters.put("depth", 1.30);
		parameters.put("depletionFraction", 0.70);
		parameters.put("cropCoefficient", 0.95);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put("airTemperature", resIn + "airT_1.csv");
		timeseries.put("windVelocity", resIn + "Wind_1.csv");
		timeseries.put("relativeHumidity", resIn + "RH_1.csv");
		timeseries.put("netRadiation", resIn + "Net_1.csv");
		timeseries.put("atmosphericPressure", resIn + "Pres_1.csv");
		timeseries.put("soilMoisture", resIn + "SoilMoisture18.csv");
		timeseries.put("soilFlux", resIn + "GHF_1.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
