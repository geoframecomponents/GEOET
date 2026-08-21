package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code ProsperoGEOET_Cavone.gpkg} fixture,
 * baking in the same CSVs, elevation/latitude/longitude (from the same
 * {@code dataET_point/1/} DEM/shapefile pair) and scalar literals {@code
 * TestProsperoGEOET_Cavone} currently hardcodes - both the {@code
 * ProsperoSolver} itself ({@code canopyHeight}, {@code typeOfCanopy}) and
 * the {@code ProsperoStressFactorSolver} feeding it a stress factor.
 */
public class BuildProsperoGEOETCavoneFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/ProsperoGEOET_Cavone.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2013-12-15 11:00");
		parameters.put("endDate", "2013-12-16 11:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("canopyHeight", 30.0);
		parameters.put("typeOfCanopy", "multilayer");
		parameters.put("defaultStress", 1.0);
		parameters.put("useRadiationStress", 0);
		parameters.put("useTemperatureStress", 0);
		parameters.put("useVDPStress", 0);
		parameters.put("useWaterStress", 1);
		parameters.put("alpha", 0.005);
		parameters.put("theta", 0.9);
		parameters.put("VPD0", 5.0);
		parameters.put("Tl", -5.0);
		parameters.put("T0", 20.0);
		parameters.put("Th", 45.0);
		parameters.put("waterWiltingPoint", 0.08);
		parameters.put("waterFieldCapacity", 0.27);
		parameters.put("rootsDepth", 1.0);
		parameters.put("depletionFraction", 0.55);
		parameters.put("cropCoefficient", 0.85);

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
