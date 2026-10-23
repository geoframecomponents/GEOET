package org.geoframe.geoet.core;

/**
 * The evapotranspiration formulas of GEOframe, in one place.
 *
 * <p>
 * All methods are pure and stateless and work on one point and one timestep.
 * Variants of the same equation coming from different historical
 * implementations are kept as separate methods, with the numerics of the
 * originals. Temperatures are in °C, pressure units are part of the parameter
 * names.
 * </p>
 *
 * @author Concetta D'Amato
 * @author Michele Bottazzi
 * @author Giuseppe Formetta
 * @author Marialaura Bancheri
 * @author Silvia Franceschi
 * @author Andrea Antonello
 * @author Riccardo Rigon
 * @author the klab team
 */
public final class GeoframeET {

	private GeoframeET() {
	}

	// ------------------------------------------------------------------------
	// Shared building blocks (FAO-56, Allen et al. 1998)
	// ------------------------------------------------------------------------

	/**
	 * Saturation vapour pressure, FAO-56 eq. 11.
	 *
	 * @param airTemperatureC air temperature [°C]
	 * @return the saturation vapour pressure [kPa]
	 *
	 *         Extracted from classes PenmanMonteithFAOModel, OmsFaoEtp,
	 *         OmsFaoEtpDaily
	 */
	public static double saturationVapourPressure(double airTemperatureC) {
		return 0.6108 * Math.exp((17.27 * airTemperatureC) / (airTemperatureC + 237.3));
	}

	/**
	 * Slope of the saturation vapour pressure curve (Delta), FAO-56 eq. 13.
	 *
	 * @param airTemperatureC air temperature [°C]
	 * @return the slope of the saturation vapour pressure curve [kPa °C-1]
	 *
	 *         Extracted from classes PriestleyTaylorModel,
	 *         PenmanMonteithFAOModel, OmsPresteyTaylorEtpModel, OmsFaoEtp,
	 *         OmsFaoEtpDaily
	 */
	public static double saturationVapourPressureSlope(double airTemperatureC) {
		double den = (airTemperatureC + 237.3) * (airTemperatureC + 237.3);
		return 4098 * saturationVapourPressure(airTemperatureC) / den;
	}

	/**
	 * Psychrometric constant with the fixed latent heat of FAO-56 eq. 8.
	 *
	 * @param atmosphericPressureKPa atmospheric pressure [kPa]
	 * @return the psychrometric constant [kPa °C-1]
	 *
	 *         Extracted from classes PriestleyTaylorModel,
	 *         PenmanMonteithFAOModel, OmsFaoEtp, OmsFaoEtpDaily
	 */
	public static double psychrometricConstantFAO56(double atmosphericPressureKPa) {
		return 0.665 * 0.001 * atmosphericPressureKPa;
	}

	/**
	 * Latent heat of vaporization as a function of temperature.
	 *
	 * @param airTemperatureC air temperature [°C]
	 * @return the latent heat of vaporization [MJ kg-1]
	 *
	 *         Extracted from class OmsPresteyTaylorEtpModel
	 */
	public static double latentHeatOfVaporization(double airTemperatureC) {
		return 2.501 - 0.002361 * airTemperatureC;
	}

	/**
	 * Psychrometric constant with a temperature dependent latent heat.
	 *
	 * @param atmosphericPressureKPa atmospheric pressure [kPa]
	 * @param latentHeat             latent heat of vaporization, see
	 *                               {@link #latentHeatOfVaporization(double)}
	 *                               [MJ kg-1]
	 * @return the psychrometric constant [kPa °C-1]
	 *
	 *         Extracted from class OmsPresteyTaylorEtpModel
	 */
	public static double psychrometricConstant(double atmosphericPressureKPa, double latentHeat) {
		return 0.001013 * atmosphericPressureKPa / (0.622 * latentHeat);
	}

	// ------------------------------------------------------------------------
	// Priestley-Taylor (Priestley and Taylor 1972)
	// ------------------------------------------------------------------------

	/**
	 * Priestley-Taylor evapotranspiration as an energy flux, with the FAO-56
	 * psychrometric constant.
	 *
	 * @param alpha               Priestley-Taylor coefficient [-]
	 * @param atmosphericPressure atmospheric pressure [Pa]
	 * @param airTemperatureC     air temperature [°C]
	 * @param soilFlux            soil heat flux density [W m-2]
	 * @param radiation           net radiation [W m-2]
	 * @return the evapotranspiration as latent heat flux [W m-2]
	 *
	 *         Extracted from class PriestleyTaylorModel
	 */
	public static double priestleyTaylorFlux(double alpha, double atmosphericPressure, double airTemperatureC,
			double soilFlux, double radiation) {
		double delta = saturationVapourPressureSlope(airTemperatureC);
		double gamma = psychrometricConstantFAO56(atmosphericPressure / 1000);
		return (alpha * delta * (radiation - soilFlux)) / (gamma + delta);
	}

	/**
	 * Priestley-Taylor evapotranspiration as a water depth, with a temperature
	 * dependent latent heat and psychrometric constant.
	 *
	 * @param alpha                  Priestley-Taylor coefficient [-]
	 * @param netRadiation           net radiation of the timestep [MJ m-2]
	 * @param soilHeatFlux           soil heat flux of the timestep [MJ m-2]
	 * @param airTemperatureC        air temperature [°C]
	 * @param atmosphericPressureKPa atmospheric pressure [kPa]
	 * @return the evapotranspiration of the timestep [mm]
	 *
	 *         Extracted from class OmsPresteyTaylorEtpModel
	 */
	public static double priestleyTaylorDepth(double alpha, double netRadiation, double soilHeatFlux,
			double airTemperatureC, double atmosphericPressureKPa) {
		double delta = saturationVapourPressureSlope(airTemperatureC);
		double lambda = latentHeatOfVaporization(airTemperatureC);
		double gamma = psychrometricConstant(atmosphericPressureKPa, lambda);
		return alpha * delta * (netRadiation - soilHeatFlux) / ((gamma + delta) * lambda);
	}

	// ------------------------------------------------------------------------
	// FAO-56 Penman-Monteith (Allen et al. 1998)
	// ------------------------------------------------------------------------

	/**
	 * FAO-56 Penman-Monteith reference evapotranspiration, without unit
	 * conversion and without clipping of negative values.
	 *
	 * @param cp                     numerator constant, 900 for daily and 37 for
	 *                               hourly timesteps [K mm s3 Mg-1 per timestep]
	 * @param cd                     denominator constant, 0.34 for the grass
	 *                               reference [s m-1]
	 * @param netRadiation           net radiation of the timestep [MJ m-2]
	 * @param soilHeatFlux           soil heat flux of the timestep [MJ m-2]
	 * @param airTemperatureC        mean air temperature [°C]
	 * @param relativeHumidity       relative humidity [%]
	 * @param wind                   wind speed at 2 m [m s-1]
	 * @param atmosphericPressureKPa atmospheric pressure [kPa]
	 * @return the reference evapotranspiration of the timestep [mm]
	 *
	 *         Extracted from classes PenmanMonteithFAOModel, OmsFaoEtp
	 */
	public static double penmanMonteithFAO(double cp, double cd, double netRadiation, double soilHeatFlux,
			double airTemperatureC, double relativeHumidity, double wind, double atmosphericPressureKPa) {
		double delta = saturationVapourPressureSlope(airTemperatureC);
		double gamma = psychrometricConstantFAO56(atmosphericPressureKPa);
		double saturationVaporPressure = saturationVapourPressure(airTemperatureC);
		double vaporPressure = saturationVaporPressure * relativeHumidity / 100;

		double num = 0.408 * delta * (netRadiation - soilHeatFlux)
				+ (cp * gamma * wind * (saturationVaporPressure - vaporPressure)) / (airTemperatureC + 273);
		double den = delta + gamma * (1 + cd * wind);
		return num / den;
	}

	/**
	 * FAO-56 Penman-Monteith evapotranspiration on SI inputs. Negative results
	 * are set to 0, and timesteps other than one hour or one day are scaled
	 * linearly from the daily value.
	 *
	 * @param Cp                  numerator constant for daily timesteps, 900;
	 *                            37 is used whenever {@code timestep} is one
	 *                            hour, regardless of this value
	 * @param Cd                  denominator constant, 0.34 for the grass
	 *                            reference [s m-1]
	 * @param atmosphericPressure atmospheric pressure [Pa]
	 * @param airTemperatureC     air temperature [°C]
	 * @param relativeHumidity    relative humidity [%]
	 * @param soilFlux            soil heat flux density [W m-2]
	 * @param timestep            length of the timestep [s]
	 * @param windAtZ             wind speed at the reference height [m s-1]
	 * @param radiation           net radiation [W m-2]
	 * @return the evapotranspiration of the timestep [mm]
	 *
	 *         Extracted from class PenmanMonteithFAOModel
	 */
	public static double penmanMonteithFAODepth(double Cp, double Cd, double atmosphericPressure,
			double airTemperatureC, double relativeHumidity, double soilFlux, int timestep, double windAtZ,
			double radiation) {
		double netRadiation = radiation * timestep / 1E6;
		double soilHeatFlux = soilFlux * timestep / 1E6;
		double cp = (timestep == 3600) ? 37 : Cp;

		double result = penmanMonteithFAO(cp, Cd, netRadiation, soilHeatFlux, airTemperatureC, relativeHumidity,
				windAtZ, atmosphericPressure / 1000);
		result = (result < 0) ? 0 : result;

		if (timestep != 86400 && timestep != 3600) {
			result = result * timestep / 86400;
		}
		return result;
	}

	/**
	 * Daily FAO-56 Penman-Monteith reference evapotranspiration with the
	 * saturation vapour pressure averaged over the minimum and maximum
	 * temperatures, and no soil heat flux.
	 *
	 * @param netRadiation           daily net radiation [MJ m-2 day-1]
	 * @param wind                   wind speed at 2 m [m s-1]
	 * @param maxTemperatureC        daily maximum air temperature [°C]
	 * @param minTemperatureC        daily minimum air temperature [°C]
	 * @param relativeHumidity       mean relative humidity [%]
	 * @param atmosphericPressureKPa atmospheric pressure [kPa]
	 * @return the daily reference evapotranspiration [mm day-1]
	 *
	 *         Extracted from class OmsFaoEtpDaily
	 */
	public static double penmanMonteithFAODailyMinMax(double netRadiation, double wind, double maxTemperatureC,
			double minTemperatureC, double relativeHumidity, double atmosphericPressureKPa) {
		double meanTemperature = (maxTemperatureC + minTemperatureC) / 2.0;
		double delta = saturationVapourPressureSlope(meanTemperature);
		double gamma = psychrometricConstantFAO56(atmosphericPressureKPa);

		double es = (saturationVapourPressure(maxTemperatureC) + saturationVapourPressure(minTemperatureC)) / 2.0;
		double ea = relativeHumidity / 100.0 * es;

		double num1 = 0.408 * delta * netRadiation;
		double num2 = gamma * 900.0 / (meanTemperature + 273) * wind * (es - ea);
		double den = delta + gamma * (1 + 0.34 * wind);
		return (num1 + num2) / den;
	}

	// ------------------------------------------------------------------------
	// Penman-Monteith with canopy resistance (Wigmosta et al. 1994)
	// ------------------------------------------------------------------------

	private static final double Z0_SNOW = 0.010;
	private static final double CLOSURE = 4000.0;
	private static final double RSMAX = 5000.0;
	private static final double VPDMINFACTOR = 0.1;
	private static final double A_SVP = 0.61078;
	private static final double B_SVP = 17.269;
	private static final double C_SVP = 237.3;
	private static final double CP_PM = 1013.0; // specific heat of moist air J/kg/C (Handbook of Hydrology)
	private static final int SEC_PER_DAY = 86400;
	private static final double HUGE_RESIST = 1.e20;
	private static final double VON_K = 0.41; // Von Karman constant
	private static final double ZREF = 2.0; // reference height for wind speed

	/**
	 * Penman-Monteith evapotranspiration of a vegetated surface, with canopy and
	 * aerodynamic resistances from the vegetation parameters.
	 *
	 * @param airTemperatureC         air temperature [°C]
	 * @param atmosphericPressure     atmospheric pressure [Pa]
	 * @param relativeHumidity        relative humidity [%]
	 * @param wind                    wind speed at the reference height [m s-1]
	 * @param snowWaterEquivalent     snow water equivalent; values greater than
	 *                                0 switch to the snow roughness [mm]
	 * @param shortRadiation          incoming shortwave radiation [W m-2]
	 * @param netRadiation            net radiation [W m-2]
	 * @param displacement            vegetation displacement height [m]
	 * @param roughness               vegetation roughness length [m]
	 * @param minStomatalResistance   minimum stomatal resistance [s m-1]
	 * @param rgl                     radiation limit for transpiration [W m-2]
	 * @param lai                     leaf area index [-]
	 * @param architecturalResistance canopy architectural resistance [s m-1]
	 * @return the evapotranspiration [mm hour-1]
	 *
	 *         Extracted from class OmsPenmanEtp
	 */
	public static double penmanMonteithVegetation(double airTemperatureC, double atmosphericPressure,
			double relativeHumidity,
			double wind, double snowWaterEquivalent, double shortRadiation, double netRadiation,
			double displacement, double roughness, double minStomatalResistance, double rgl, double lai,
			double architecturalResistance) {
		double tair = airTemperatureC;
		double rs = minStomatalResistance;

		// vapour pressure deficit in Pa
		double vpd = saturationVapourPressureIceCorrected(tair) * (1 - relativeHumidity / 100) * 1000.0;
		double ra = aerodynamicResistance(displacement, roughness, ZREF, wind, snowWaterEquivalent);

		// slope of the saturated vapor pressure curve in Pa/K
		double slope = saturationVapourPressureSlopeIceCorrected(tair) * 1000.0;

		// factor for canopy resistance based on photosynthesis
		double dayFactor;
		double f = 0.0;
		if (rs > 0.) {
			if (rgl < 0) {
				throw new IllegalArgumentException("Invalid value of RGL for the current class.");
			} else if (rgl == 0) {
				f = shortRadiation;
			} else {
				f = shortRadiation / rgl;
			}
			dayFactor = (1. + f) / (f + rs / RSMAX);
		} else
			dayFactor = 1.;

		// factor for canopy resistance based on temperature
		double tFactor = .08 * tair - 0.0016 * tair * tair;
		tFactor = (tFactor <= 0.0) ? 1e-10 : tFactor;

		// factor for canopy resistance based on vpd
		double vpdFactor = 1 - vpd / CLOSURE;
		vpdFactor = (vpdFactor < VPDMINFACTOR) ? VPDMINFACTOR : vpdFactor;

		// canopy resistance in s/m
		double rc = rs / (lai * tFactor * vpdFactor) * dayFactor;
		rc = (rc > RSMAX) ? RSMAX : rc;

		// latent heat of vaporization, eq. 4.2.1 Handbook of Hydrology, assume Ts is Tair
		double lv = 2501000 - 2361 * tair;
		// psychrometric constant (Pa/C), eq. 4.2.28 Handbook of Hydrology
		double gamma = 1628.6 * atmosphericPressure / lv;
		// air density (kg/m3), eq. 4.2.4 Handbook of Hydrology
		double r_air = 0.003486 * atmosphericPressure / (275 + tair);

		// not dividing by the density of water (~1000 kg/m3) gives mm instead of m
		double evap = ((slope * netRadiation + r_air * CP_PM * vpd / ra)
				/ (lv * (slope + gamma * (1 + (rc + architecturalResistance) / ra))) * SEC_PER_DAY) / 24.0;

		if (vpd >= 0.0 && evap < 0.0)
			evap = 0.0;
		return evap;
	}

	/**
	 * Slope of the saturated vapour pressure curve, based on
	 * {@link #saturationVapourPressureIceCorrected(double)} (Handbook of
	 * Hydrology eq. 4.2.3).
	 *
	 * @param temp air temperature [°C]
	 * @return the slope of the saturated vapour pressure curve [kPa °C-1]
	 *
	 *         Extracted from class OmsPenmanEtp
	 */
	private static double saturationVapourPressureSlopeIceCorrected(double temp) {
		return (B_SVP * C_SVP) / ((C_SVP + temp) * (C_SVP + temp)) * saturationVapourPressureIceCorrected(temp);
	}

	/**
	 * Saturated vapour pressure with the correction over ice below 0 °C
	 * (Handbook of Hydrology eq. 4.2.2).
	 *
	 * @param temp air temperature [°C]
	 * @return the saturated vapour pressure [kPa]
	 *
	 *         Extracted from class OmsPenmanEtp
	 */
	private static double saturationVapourPressureIceCorrected(double temp) {
		double svp = A_SVP * Math.exp((B_SVP * temp) / (C_SVP + temp));
		if (temp < 0)
			svp *= 1.0 + .00972 * temp + .000042 * temp * temp;
		return svp;
	}

	/**
	 * Aerodynamic resistance of the vegetation layer.
	 *
	 * @param displacement        vegetation displacement height [m]
	 * @param roughness           vegetation roughness length [m]
	 * @param zRef                reference height of the wind speed [m]
	 * @param windSpeed           wind speed; 0 or less gives the maximum
	 *                            resistance [m s-1]
	 * @param snowWaterEquivalent snow water equivalent; values greater than 0
	 *                            switch to the snow roughness [mm]
	 * @return the aerodynamic resistance [s m-1]
	 *
	 *         Extracted from class OmsPenmanEtp
	 */
	private static double aerodynamicResistance(double displacement, double roughness, double zRef,
			double windSpeed, double snowWaterEquivalent) {
		double ra;
		double k2 = VON_K * VON_K;

		if (displacement > zRef)
			zRef = displacement + zRef + roughness;

		if (snowWaterEquivalent > 0) {
			ra = Math.log((2. + Z0_SNOW) / Z0_SNOW) * Math.log(zRef / Z0_SNOW) / k2;
		} else {
			ra = Math.log((2. + (1.0 / 0.63 - 1.0) * displacement) / roughness)
					* Math.log((2. + (1.0 / 0.63 - 1.0) * displacement) / (0.1 * roughness)) / k2;
		}

		if (windSpeed > 0.) {
			ra /= windSpeed;
		} else {
			ra = HUGE_RESIST;
		}
		return ra;
	}

	// ------------------------------------------------------------------------
	// Hargreaves
	// ------------------------------------------------------------------------

	/**
	 * Reference evapotranspiration with the modified Hargreaves equation
	 * (Droogers and Allen 2002).
	 *
	 * @param tMax     mean daily maximum air temperature [°C]
	 * @param tMin     mean daily minimum air temperature [°C]
	 * @param tAvg     mean daily air temperature [°C]
	 * @param rainfall precipitation of the timestep [mm]
	 * @param solarRad mean daily extraterrestrial radiation [MJ m-2 day-1]
	 * @param daysInTs number of days in the timestep [-]
	 * @return the reference evapotranspiration of the timestep [mm]
	 *
	 *         Extracted from class OmsPotentialEvapotranspiredWaterVolume
	 */
	public static double hargreavesModifiedReferenceDepth(double tMax, double tMin, double tAvg, double rainfall,
			double solarRad, double daysInTs) {
		double range = Math.max(tMax - tMin - 0.0123 * rainfall, 0);
		return 0.0013 * 0.408 * solarRad * (tAvg + 17) * Math.pow(range, 0.76) * daysInTs;
	}

	/**
	 * Potential evapotranspiration of a crop or land cover, Kc times the
	 * reference evapotranspiration.
	 *
	 * @param cropCoefficient             crop coefficient Kc [-]
	 * @param referenceEvapotranspiration reference evapotranspiration [mm]
	 * @return the potential evapotranspiration [mm]
	 *
	 *         Extracted from class OmsPotentialEvapotranspiredWaterVolume
	 */
	public static double cropPotentialEvapotranspiration(double cropCoefficient,
			double referenceEvapotranspiration) {
		return cropCoefficient * referenceEvapotranspiration;
	}
}
