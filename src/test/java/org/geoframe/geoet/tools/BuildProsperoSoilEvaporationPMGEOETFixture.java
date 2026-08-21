package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One-off generator for the {@code ProsperoSoilEvaporationPMGEOET.gpkg}
 * fixture, baking in the same CSVs, elevation/latitude/longitude (from the
 * same {@code dataET_point/1/} DEM/shapefile pair) and scalar literals
 * {@code TestProspero_SoilEvaporationPM_GEOET} currently hardcodes across
 * its four solvers: {@code ProsperoStressFactorSolverWithEvaporation}
 * (stress + evaporation stress), {@code ProsperoSolver} (transpiration),
 * {@code PenmanMonteithFAOSoilEvaporationSolverWithCanopy} (evaporation),
 * and {@code TotalEvapoTranspirationSolver} (combines the two into total
 * evapotranspiration). Note this test uses {@code LAI_1.csv}, not the
 * {@code LAI_10.csv} the Cavone Prospero tests use.
 */
public class BuildProsperoSoilEvaporationPMGEOETFixture {

	public static void main(String[] args) throws Exception {
		String resIn = "src/test/resources/Input/dataET_point/1/";
		String outPath = "src/test/resources/Input/gpkg/ProsperoSoilEvaporationPMGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("startDate", "2013-12-15 11:00");
		parameters.put("endDate", "2013-12-15 12:00");
		parameters.put("timeStepMinutes", 60);
		parameters.put("elevation", 536.0);
		parameters.put("latitude", 40.50554583408996);
		parameters.put("longitude", 16.245253020414495);
		parameters.put("canopyHeight", 1.26);
		parameters.put("typeOfCanopy", "multilayer");
		parameters.put("defaultStress", 1.0);
		parameters.put("useRadiationStress", 0);
		parameters.put("useTemperatureStress", 0);
		parameters.put("useVDPStress", 0);
		parameters.put("useWaterStress", 0);
		parameters.put("alpha", 0.005);
		parameters.put("theta", 0.9);
		parameters.put("VPD0", 5.0);
		parameters.put("Tl", -5.0);
		parameters.put("T0", 20.0);
		parameters.put("Th", 45.0);
		parameters.put("waterWiltingPoint", 0.15);
		parameters.put("waterFieldCapacity", 0.27);
		parameters.put("rootsDepth", 0.75);
		parameters.put("depletionFraction", 0.55);
		parameters.put("cropCoefficient", 0.75);
		parameters.put("evaporationDepth", 0.5);

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
		timeseries.put("leafAreaIndex", resIn + "LAI_1.csv");
		timeseries.put("soilMoisture", resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
