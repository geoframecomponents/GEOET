package org.geoframe.geoet.core.state;

import org.joda.time.DateTime;

/**
 * Shared, mutable scratch space passed by reference to every solver in a
 * pipeline: one solver's {@code process()} writes some of these fields,
 * downstream solvers read them back. It is the "bag" the components pass between
 * each other.
 *
 * <p>
 * <b>NOTE:</b> Values are cumulative across every solver family in this codebase (Penman-
 * Monteith FAO, Priestley-Taylor, Prospero, radiation, root density, stress
 * factors); any single solver run only ever touches a subset of these
 * fields.
 * 
 * @author Concetta D'Amato
 * @author Riccardo Rigon
 * @author Andrea Antonello
 */
public class ETProblemQuantities {

	/** Leaf area index of the sunlit canopy fraction. Unit: m2 m-2. */
	public double areaCanopySun;
	/** Leaf area index of the shaded canopy fraction. Unit: m2 m-2. */
	public double areaCanopyShade;
	/**
	 * Leaf boundary-layer convective heat transfer coefficient (thermal
	 * conductivity times Nusselt number, divided by leaf length) - see
	 * {@code SensibleHeatMethods.computeConvectiveTransferCoefficient}.
	 */
	public double convectiveTransferCoefficient;
	/**
	 * Slope of the saturation vapor pressure curve (Δ in the FAO-56/Penman-
	 * Monteith formulation), evaluated at the current air temperature.
	 */
	public double delta;

	/** Soil evaporation for the current step. Unit: mm/time. */
	public double evaporation;
	/** {@link #evaporation} expressed as an energy flux. Unit: W m-2. */
	public double fluxEvaporation;

	/** Canopy transpiration for the current step. Unit: mm/time. */
	public double transpiration;
	/** {@link #transpiration} expressed as an energy flux. Unit: W m-2. */
	public double fluxTranspiration;

	/**
	 * Total evapotranspiration ({@link #transpiration} + {@link
	 * #evaporation}), as combined by {@code TotalEvapoTranspirationSolver}.
	 * Unit: mm/time.
	 */
	public double evapoTranspiration;
	/** {@link #evapoTranspiration} expressed as an energy flux. Unit: W m-2. */
	public double fluxEvapoTranspiration;

	/** Priestley-Taylor evapotranspiration. Unit: mm/time. */
	public double evapoTranspirationPT;
	/** {@link #evapoTranspirationPT} expressed as an energy flux. Unit: W m-2. */
	public double fluxEvapoTranspirationPT;

	/** Penman-Monteith FAO evapotranspiration for the current step. Unit: mm/time. */
	public double evapoTranspirationPM;
	/** Penman-Monteith FAO evapotranspiration, daily total. Unit: mm day-1. */
	public double evapoTranspirationPMdaily;
	/** {@link #evapoTranspirationPM} expressed as an energy flux. Unit: W m-2. */
	public double fluxEvapoTranspirationPM;

	/** Shortwave radiation reaching bare/exposed soil (i.e. not intercepted by the canopy). Unit: W m-2. */
	public double incidentSolarRadiationSoil;
	/**
	 * Leaf latent-heat transfer coefficient (water-molar-mass times latent
	 * heat of evaporation times molar total conductance, divided by
	 * atmospheric pressure) - see {@code LatentHeatMethods.computeLatentHeatTransferCoefficient}.
	 */
	public double latentHeatTransferCoefficient;
	/**
	 * Residual term used while deriving {@link #incidentSolarRadiationSoil}
	 * in {@code ComputeQuantitiesProspero} (computed as {@code
	 * shortWaveRadiationDirect - netRadiation}, clamped to be non-negative).
	 * Despite the name, this is not literally "net longwave radiation" -
	 * treat its physical meaning with caution beyond that formula.
	 */
	public double netLong;
	/**
	 * PAR-to-shortwave rescaling factor used only inside {@code
	 * ComputeQuantitiesProspero}'s sunlit-canopy radiation computation: the
	 * ratio of PAR-equivalent incoming shortwave (direct+diffuse, each
	 * scaled by 2.1) to the PAR-domain absorbed-radiation model output,
	 * used to convert {@link #shortwaveCanopySun} back from the PAR domain
	 * into W m-2 of total shortwave.
	 */
	public double radFactorSun;
	/** Same as {@link #radFactorSun}, for the shaded canopy fraction. */
	public double radFactorShade;
	/** Saturation vapor pressure at the current air temperature. Unit: Pa. */
	public double saturationVaporPressure;
	/**
	 * Leaf sensible-heat transfer coefficient ({@link
	 * #convectiveTransferCoefficient} times leaf side count) - see
	 * {@code SensibleHeatMethods.computeSensibleHeatTransferCoefficient}.
	 */
	public double sensibleHeatTransferCoefficient;
	/** Shortwave radiation absorbed by the sunlit canopy fraction. Unit: W m-2. */
	public double shortwaveCanopySun;
	/** Shortwave radiation absorbed by the shaded canopy fraction. Unit: W m-2. */
	public double shortwaveCanopyShade;
	/** Solar elevation angle for the current date/time/location. Unit: rad. */
	public double solarElevationAngle;
	/**
	 * Jarvis-type radiation stress multiplier (0-1) for the sunlit canopy,
	 * active only when a stress-factor solver has {@code useRadiationStress}
	 * enabled; 1 means no stress.
	 */
	public double stressRadiationSun;
	/** Same as {@link #stressRadiationSun}, for the shaded canopy fraction. */
	public double stressRadiationShade;
	/**
	 * Jarvis-type air-temperature stress multiplier (0-1), active only when
	 * a stress-factor solver has {@code useTemperatureStress} enabled; 1
	 * means no stress.
	 */
	public double stressTemperature;
	/**
	 * Jarvis-type vapor-pressure-deficit stress multiplier (0-1), active
	 * only when a stress-factor solver has {@code useVDPStress} enabled; 1
	 * means no stress.
	 */
	public double stressVPD;
	/**
	 * Soil-moisture (water) stress multiplier (0-1) applied to canopy
	 * transpiration, active only when a stress-factor solver has {@code
	 * useWaterStress} enabled; 1 means no stress.
	 */
	public double stressWater;
	/**
	 * Soil-moisture stress multiplier (0-1) applied specifically to soil
	 * evaporation (as opposed to {@link #stressWater}, which applies to
	 * canopy transpiration) - see {@code ProsperoStressFactorSolverWithEvaporation}.
	 * Defaults to 1 (no stress).
	 */
	public double evaporationStressWater = 1;

	/** Longwave radiation absorbed by the sunlit canopy fraction. Unit: W m-2. */
	public double longwaveCanopySun;
	/** Longwave radiation absorbed by the shaded canopy fraction. Unit: W m-2. */
	public double longwaveCanopyShade;
	/**
	 * Total radiation absorbed by the sunlit canopy fraction ({@link
	 * #shortwaveCanopySun} plus its longwave counterpart in the "complete"
	 * radiation model). Unit: W m-2.
	 */
	public double absorbedRadiationCanopySun;
	/** Same as {@link #absorbedRadiationCanopySun}, for the shaded canopy fraction. */
	public double absorbedRadiationCanopyShade;
	/** Sunlit leaf temperature minus air temperature, from the leaf energy balance. Unit: K. */
	public double deltaTemperatureSun;
	/** Same as {@link #deltaTemperatureSun}, for the shaded canopy fraction. */
	public double deltaTemperatureShade;
	/**
	 * Longwave radiative feedback (re-emission) from the sunlit leaf back to
	 * its surroundings, subtracted in the leaf energy-balance residual - see
	 * {@code RadiationMethodComplete.computeLeafRadiativeFeedback}. Unit: W m-2.
	 */
	public double leafRadiativeFeedbackSun;
	/** Same as {@link #leafRadiativeFeedbackSun}, for the shaded canopy fraction. */
	public double leafRadiativeFeedbackShade;

	/** Root depth, mirrors {@link ETCurrentStepInput#rootDepth}. Unit: m. */
	public double rootDepth;
	/** Canopy height, mirrors {@link ETCurrentStepInput#canopyHeight}. Unit: m. */
	public double canopyHeight;

	/** Actual vapor pressure at the current air temperature/humidity. Unit: Pa. */
	public double vaporPressure;
	/** Vapor pressure deficit ({@link #saturationVaporPressure} - {@link #vaporPressure}). Unit: Pa (see {@code PressureMethods.computeVapourPressureDeficit}). */
	public double vapourPressureDeficit;
	/** Vapor pressure at dew point, from the dew-point form of the Clausius-Clapeyron relation. Unit: Pa. */
	public double vaporPressureDew;
	/** Wind speed inside the canopy, from the logarithmic wind profile at canopy height (see {@link #computeWindProfile}). Unit: m s-1. */
	public double windInCanopy;
	/** Wind speed at the soil surface, from the logarithmic wind profile at a fixed 0.2 m reference height (see {@link #computeWindProfile}). Unit: m s-1. */
	public double windSoil;

	/**
	 * Sunlit-leaf vapor pressure deficit term, computed via the linearized
	 * energy-balance form in {@code PressureMethods.computeVapourPressureDelta}
	 * (paper reference in that method's call site: "eq.13"). Used together
	 * with {@link #vapourPressureDeltaSun2} in the leaf latent-heat flux
	 * computation.
	 */
	public double vapourPressureDeltaSun1;
	/**
	 * Sunlit-leaf vapor pressure deficit term computed directly as {@code
	 * (saturationVaporPressure - vaporPressure) + delta * deltaTemperatureSun}
	 * (paper reference in the call site: "eq.8", e_Δ = δa + Δ·T_Δ).
	 */
	public double vapourPressureDeltaSun2;
	/** Same as {@link #vapourPressureDeltaSun1}, for the shaded canopy fraction. */
	public double vapourPressureDeltaShade1;
	/** Same as {@link #vapourPressureDeltaSun2}, for the shaded canopy fraction. */
	public double vapourPressureDeltaShade2;

	/** Sunlit leaf temperature ({@link #deltaTemperatureSun} + air temperature). Unit: K. */
	public double leafTemperatureSun;
	/** Same as {@link #leafTemperatureSun}, for the shaded canopy fraction. */
	public double leafTemperatureShade;

	/** Latent heat flux (transpiration) from the sunlit canopy fraction. Unit: W m-2. */
	public double latentHeatFluxSun;
	/** Sensible heat flux from the sunlit canopy fraction. Unit: W m-2. */
	public double sensibleHeatFluxSun;

	/** Same as {@link #latentHeatFluxSun}, for the shaded canopy fraction. */
	public double latentHeatFluxShade;
	/** Same as {@link #sensibleHeatFluxSun}, for the shaded canopy fraction. */
	public double sensibleHeatFluxShade;

	/** Net longwave radiation exchanged by the sunlit canopy fraction. Unit: W m-2. */
	public double netLongWaveRadiationSun;
	/** Same as {@link #netLongWaveRadiationSun}, for the shaded canopy fraction. */
	public double netLongWaveRadiationShade;

	/**
	 * Residual of the sunlit-leaf energy balance (absorbed radiation minus
	 * radiative feedback, sensible and latent heat flux) - driven towards
	 * zero by the iterative leaf-temperature solve in {@code
	 * TranspirationBudget}/{@code ProsperoModel}. Unit: W m-2.
	 */
	public double energyBalanceResidualSun = 0;
	/** Same as {@link #energyBalanceResidualSun}, for the shaded canopy fraction. */
	public double energyBalanceResidualShade = 0;

	/** Current timestamp of the simulation step. */
	public DateTime date;

	/** Hour of day (0-23) derived from {@link #date}, used to decide day/night-only behavior (e.g. soil flux parameter, Priestley-Taylor daylight window). */
	public int hourOfDay;
	/** True if {@link #hourOfDay} falls within the solver's configured daylight window (sic: "Ligth" is the field's actual spelling). */
	public boolean isLigth;
	/**
	 * Day- or night-specific soil-flux coefficient selected via {@link
	 * #isLigth} (see {@code PenmanMonteithFAOSolver.soilFluxParameterDay}/
	 * {@code soilFluxParameterNight}), multiplied by net radiation to derive
	 * soil heat flux when it isn't supplied directly.
	 */
	public double soilFluxparameter;

	/** Wind speed at the reference height above ground surface (see {@link #computeWindProfile}). Unit: m s-1. */
	public double windAtZ; // windAtZ_AboveGroundSurface

	/**
	 * Logarithmic wind profile (FAO-56 chapter 3, equation 47): extrapolates
	 * a wind speed measured at 2 m to another reference height.
	 *
	 * @param windSpeed2m wind speed measured at 2 m, m s-1
	 * @param height      target reference height, m
	 * @return wind speed at {@code height}, m s-1
	 */
	public static double computeWindProfile(double windSpeed2m, double height) {
		return (windSpeed2m * (Math.log(67.8 * height - 5.42))) / 4.87;
	}

	/**
	 * Shortwave radiation reflected off the soil and re-absorbed by the
	 * sunlit canopy, in the "complete" radiation model ({@code
	 * RadiationMethodComplete}). Unit: W m-2.
	 */
	public double directAbsorbedRadiationReflectedSoilSunlit;
	/** Same as {@link #directAbsorbedRadiationReflectedSoilSunlit}, for the shaded canopy fraction. */
	public double directAbsorbedRadiationReflectedSoilShadow;
	/** Net longwave radiation absorbed by the sunlit canopy, in the "complete" radiation model. Unit: W m-2. */
	public double absorbedLongwaveRadiationSunlit;
	/** Same as {@link #absorbedLongwaveRadiationSunlit}, for the shaded canopy fraction. */
	public double absorbedLongwaveRadiationShadow;

	/** Atmospheric (clear-sky) emissivity, from {@code RadiationMethodComplete}'s Brutsaert-style formula using {@link #precipitableWater}. Unit: -. */
	public double airEmissivity;
	/** Precipitable water estimate, from saturation vapor pressure and air temperature (Prata 1996-style formula). Unit: cm (as used in the {@link #airEmissivity} formula). */
	public double precipitableWater;
	/** Radiative conductance term used in the "complete" radiation model's leaf-temperature linearization. Unit: see {@code RadiationMethodComplete}. */
	public double radiativeConductance;

	/**
	 * Soil surface temperature; in {@code RadiationMethodComplete} this is
	 * currently just set equal to air temperature (a simplifying
	 * assumption, not an independently solved quantity). Unit: K.
	 */
	public double soilTemperature;
	/** Longwave radiation emitted by the soil (Stefan-Boltzmann law using {@link #soilTemperature} and soil emissivity). Unit: W m-2. */
	public double longRadiationFromSoil;

	/**
	 * Longwave radiation reaching the soil from the shaded-leaf layer, in
	 * the "complete" radiation model. Note there is no corresponding
	 * "...FromSunLeaf" field even though the formula that uses this one
	 * subtracts sunlit/shaded absorbed longwave terms separately.
	 */
	public double longRadiationFromShadeLeaf;
	/**
	 * Alternate computation of {@link #incidentSolarRadiationSoil} used
	 * only by the "complete" radiation model classes ({@code
	 * RadiationMethodComplete}/{@code ComputeRadiationQuantitiesComplete});
	 * kept as a separate field rather than reusing {@link
	 * #incidentSolarRadiationSoil} for reasons not evident from the code.
	 */
	public double NewincidentSolarRadiationSoil;

	/** Number of control volumes the root-density domain is discretized into. */
	public int NUM_CONTROL_VOLUMES;

	/** Root density profile over the discretized domain, one value per control volume. Unit: -. */
	public double[] rootDensity;

	/** Sum of {@link #rootDensity} over the domain, used to normalize root-density-weighted stress factors. */
	public double sumRootDensity;

	/** Current root-growth timestep index, used by the root-growth methods to detect the first step (no growth applied yet). */
	public double step;

	/** Depth of the simulated domain. Unit: m. */
	public double totalDepth;

	/** Depth of the root zone measured from the bottom of the domain ({@link #totalDepth} + {@link #rootDepth}). Unit: m. */
	public double zR;

	/** Leaf-internal CO2 concentration, used by the Medlyn stomatal-conductance stress factor. Unit: see {@code MedlynStressFactor} (ppm-like ratio). */
	public double carbonDioxideLeafConcentration;

	/** Not currently read or written anywhere in this codebase - presumably intended for a Medlyn-model stress output that was never wired up. */
	public double stressMedlyn;

}
