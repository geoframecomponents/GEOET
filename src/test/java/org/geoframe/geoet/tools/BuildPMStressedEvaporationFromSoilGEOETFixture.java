package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code PMStressedEvaporationFromSoilGEOET.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) and scalar literals
 * {@code TestPMStressedEvaporationFromSoilGEOET} currently hardcodes. That
 * test only sets a subset of {@code PriestleyTaylorPenmanMonteithFAOStressFactorSolver}'s
 * fields (defaultStress, useWaterStress, waterWiltingPoint,
 * waterFieldCapacity, depth, depletionFraction) - the rest (radiation/
 * temperature/VPD stress toggles, alpha, theta, VPD0, Tl, T0, Th,
 * cropCoefficient) are left at the solver's own class defaults, so this
 * fixture's {@code parameters} table only carries that same subset.
 */
public class BuildPMStressedEvaporationFromSoilGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/input/gpkg/PMStressedEvaporationFromSoilGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2013-12-15 00:00");
		parameters.put("endDate", "2013-12-15 02:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("defaultStress", 1.0);
		parameters.put("useWaterStress", 0);
		parameters.put("waterWiltingPoint", 0.16);
		parameters.put("waterFieldCapacity", 0.27);
		parameters.put("depth", 0.25);
		parameters.put("depletionFraction", 0.75);

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
