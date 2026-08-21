package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code ProsperoLAIGEOET_Cavone.gpkg} fixture,
 * baking in the same CSVs, elevation/latitude/longitude (from the same
 * {@code dataET_point/1/} DEM/shapefile pair) and scalar literals {@code
 * TestProsperoLAIGEOET_Cavone} currently hardcodes across its three solvers
 * ({@code RadiationSolver}, {@code ProsperoStressFactorSolver}, {@code
 * ProsperoSolverWithExplicitLAI}) - {@code typeOfCanopy} is shared by both
 * the radiation solver and Prospero itself, always the same literal in the
 * original, so it's a single fixture parameter here too.
 */
public class BuildProsperoLAIGEOETCavoneFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/ProsperoLAIGEOET_Cavone.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2014-01-01 09:00");
		parameters.put("endDate", "2014-01-01 14:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("canopyHeight", 30.0);
		parameters.put("typeOfCanopy", "multilayer");
		parameters.put("defaultStress", 1.0);
		parameters.put("useRadiationStress", 1);
		parameters.put("useTemperatureStress", 1);
		parameters.put("useVDPStress", 1);
		parameters.put("useWaterStress", 1);
		parameters.put("alpha", 0.005);
		parameters.put("theta", 0.9);
		parameters.put("VPD0", 5.0);
		parameters.put("Tl", -5.0);
		parameters.put("T0", 20.0);
		parameters.put("Th", 45.0);
		parameters.put("waterWiltingPoint", 0.05);
		parameters.put("waterFieldCapacity", 0.30);
		parameters.put("rootsDepth", 1.80);
		parameters.put("depletionFraction", 0.7);
		parameters.put("cropCoefficient", 0.95);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put("airTemperature", resIn + "airT_1.csv");
		timeseries.put("windVelocity", resIn + "Wind_1.csv");
		timeseries.put("relativeHumidity", resIn + "RH_1.csv");
		timeseries.put("shortWaveRadiationDirect", resIn + "ShortwaveDirect_1.csv");
		timeseries.put("shortWaveRadiationDiffuse", resIn + "ShortwaveDiffuse_1.csv");
		timeseries.put("longWaveRadiation", resIn + "LongDownwelling_1.csv");
		timeseries.put("netRadiation", resIn + "Net_1.csv");
		timeseries.put("soilFlux", resIn + "GHF_1.csv");
		timeseries.put("atmosphericPressure", resIn + "Pres_1.csv");
		timeseries.put("leafAreaIndex", resIn + "LAI_10.csv");
		timeseries.put("soilMoisture", resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
