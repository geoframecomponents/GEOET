package org.geoframe.geoet.tools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoframe.geoet.io.GeoetInputsHandler;

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
		String outPath = "src/test/resources/Input/gpkg/PMStressedEvaporationFromSoilGEOET.gpkg";

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put(GeoetInputsHandler.PARAM_START_DATE, "2013-12-15 00:00");
		parameters.put(GeoetInputsHandler.PARAM_END_DATE, "2013-12-15 02:00");
		parameters.put(GeoetInputsHandler.PARAM_TIME_STEP_MINUTES, 60);
		parameters.put(GeoetInputsHandler.PARAM_ELEVATION, 536.0);
		parameters.put(GeoetInputsHandler.PARAM_LATITUDE, 40.50554583408996);
		parameters.put(GeoetInputsHandler.PARAM_LONGITUDE, 16.245253020414495);
		parameters.put(GeoetInputsHandler.PARAM_DEFAULT_STRESS, 1.0);
		parameters.put(GeoetInputsHandler.PARAM_USE_WATER_STRESS, 0);
		parameters.put(GeoetInputsHandler.PARAM_WATER_WILTING_POINT, 0.16);
		parameters.put(GeoetInputsHandler.PARAM_WATER_FIELD_CAPACITY, 0.27);
		parameters.put(GeoetInputsHandler.PARAM_DEPTH, 0.25);
		parameters.put(GeoetInputsHandler.PARAM_DEPLETION_FRACTION, 0.75);

		Map<String, String> timeseries = new LinkedHashMap<>();
		timeseries.put(GeoetInputsHandler.VAR_AIR_TEMPERATURE, resIn + "airT_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_WIND_VELOCITY, resIn + "Wind_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_RELATIVE_HUMIDITY, resIn + "RH_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_NET_RADIATION, resIn + "Net_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_ATMOSPHERIC_PRESSURE, resIn + "Pres_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_FLUX, resIn + "GHF_1.csv");
		timeseries.put(GeoetInputsHandler.VAR_SOIL_MOISTURE, resIn + "SoilMoisture18.csv");

		GpkgFixtureBuilder.build(outPath, parameters, timeseries);
		System.out.println("Wrote " + outPath);
	}
}
