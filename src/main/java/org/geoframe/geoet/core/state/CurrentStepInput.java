package org.geoframe.geoet.core.state;

import org.joda.time.DateTime;


/**
 * Per-step driving variables and site parameters, populated once per
 * timestep by {@code InputPreprocessor}, then read by every solver in that step's
 * pipeline. It is the "bag" {@code InputPreprocessor} fills and the solvers read from.
 * 
 * @author Concetta D'Amato and Riccardo Rigon
 * @author Andrea Antonello
 */
public class CurrentStepInput {

	/** Air temperature for the current step. Unit: K. */
	public double airTemperature;

	/** Air temperature for the current step. Unit: °C. */
	public double airTemperatureC;

	/** Leaf area index for the current step. Unit: m2 m-2. */
	public double leafAreaIndex;

	/** Root depth for the current step. Unit: m. */
	public double rootDepth;

	/** Canopy height for the current step. Unit: m. */
	public double canopyHeight;

	/** Direct-beam shortwave radiation for the current step. Unit: W m-2. */
	public double shortWaveRadiationDirect;

	/** Diffuse shortwave radiation for the current step. Unit: W m-2. */
	public double shortWaveRadiationDiffuse;

	/** Downwelling longwave radiation for the current step. Unit: W m-2. */
	public double longWaveRadiation;

	/** Net radiation for the current step, when supplied directly rather than derived. Unit: W m-2. */
	public double netRadiation;

	/** Wind velocity (typically at 2 m) for the current step. Unit: m s-1. */
	public double windVelocity;

	/** Atmospheric pressure for the current step. Unit: Pa. */
	public double atmosphericPressure;

	/** Relative humidity for the current step. Unit: %. */
	public double relativeHumidity;

	/** Soil heat flux for the current step, when supplied directly rather than derived from net radiation. Unit: W m-2. */
	public double soilFlux;

	/** Soil moisture for the current step. Unit: m3 m-3. */
	public double soilMoisture;

	/** Timestamp of the current step. */
	public DateTime date;

	// public String rootType;

	/** The elevation of the centroid. Unit: m. */
	public double elevation;

	/** The latitude of the centroid. Unit: °. */
	public double latitude;

	/** The longitude of the centroid. Unit: °. */
	public double longitude;

	/** Timestep length, set by {@code InputPreprocessor} as {@code temporalStep * 60}. Unit: seconds. */
	public int time;

	/** Station/centroid identifier for the current step. */
	public int ID;

	/** z coordinate read from the grid. Unit: m. */
	public double[] z;

	/** Vector of Initial Condition for root density */
	public double[] rootDensityIC;

	/** Root growth rate per step, added to {@link org.geoframe.geoet.core.state.ProblemQuantities#rootDensity} by the root-growth methods. */
	public double growthRateRoot;

	/** Medlyn stomatal-conductance model slope parameter g1, used by {@code MedlynStressFactor}. */
	public double g1;

	/** Photosynthetic assimilation rate, used by the Medlyn stomatal-conductance stress factor. */
	public double assimilationRate;

	// @Description("Switch that defines if it is hourly.")
	// @In
	// public boolean doHourly = true;
}
