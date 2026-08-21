package org.geoframe.geoet.core.config;


import oms3.annotations.Author;
import oms3.annotations.License;

/**
 * Physical constants and model coefficients shared by every solver, plus
 * the default values substituted for missing/no-data driving-variable
 * readings. It is a settings/constants bag.
 */
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")
public class Parameters {


	/** Specific heat capacity of air at constant pressure. Unit: J kg-1 K-1. */
	public double airSpecificHeat = 1010; // J kg-1 K-1
	/** Air density. Unit: kg m-3. */
	public double airDensity = 1.2690;
	/** Boltzmann constant. Unit: J K-1. */
	public double boltzmannConstant = 1.38066e-23;
	/** Reynolds number above which leaf boundary-layer flow is treated as turbulent rather than laminar. Fixed constant. */
	public double criticalReynoldsNumber = 3000; 	//fixed
	/** Standard gravitational acceleration. Unit: m s-2. */
	public double gravityConstant = 9.80665;
	/** Latent heat of vaporization of water. Unit: J kg-1. */
	public double latentHeatEvaporation = 2.45e6; // J/kg
	/** Mass of a single air molecule (29 g mol-1 converted via Avogadro's number). Unit: kg. */
	public double massAirMolecule = 29*1.66054e-27;
	/** Universal (molar) gas constant. Unit: J mol-1 K-1. */
	public double molarGasConstant = 8.314472;
	/** Molar volume of an ideal gas. Unit: m3 mol-1. */
	public double molarVolume = 0.023;
	/** Prandtl number of air (ratio of momentum to thermal diffusivity). Fixed constant. */
	public double prandtlNumber = 0.71; 			// fixed
	/** Stefan-Boltzmann constant. Unit: W m-2 K-4. */
	public double stefanBoltzmannConstant = 5.670373e-8;
	/** Molar mass of water. Unit: kg mol-1. */
	public double waterMolarMass = 0.018;


	/** The air temperature default value in case of missing data. Unit: K. */
	public double defaultAirTemperature = 15.0+273.15;

	/** The wind default value in case of missing data. Unit: m s-1. */
	public double defaultWindVelocity = 0.5;

	/** The humidity default value in case of missing data. Unit: %. */
	public double defaultRelativeHumidity = 70.0;

	/** The short wave radiation default value in case of missing data. Unit: W m-2. */
	public double defaultShortWaveRadiationDirect = 0.0;

	/** The atmospheric pressure default value in case of missing data. Unit: Pa. */
	public double defaultAtmosphericPressure = 101325.0;

	/** The soilflux default value in case of missing data. Unit: W m-2. */
	public double defaultSoilFlux = 0.0;

	/** The leaf area index default value in case of missing data. Unit: m2 m-2. */
	public double defaultLeafAreaIndex = 1.0;

	/** Default soil moisture. Unit: m3 m-3. */
	public double defaultSoilMoisture = 0.20;

	/** Default stress-factor multiplier (1 = no stress) substituted when a stress-factor solver isn't otherwise wired in. */
	public double defaultStress = 1;

	/** Coefficient Cp eq. Penman-Monteith FAO equal to 900 in the case of a daily time step and equal to 37 in the case of a hourly time step */
	public double Cp = 900;

	/** Coefficient Cd eq. Penman-Monteith FAO equal to 0.34 */
	public double Cd = 0.34;

	/** Coefficient alpha eq. Priestley-Taylor */
	public double alpha = 1.26;

	/** extinction coefficient for diffuse and scattered diffuse PAR k'Pd */
	public double diffuseExtinctionCoefficient = 0.719;

	/** Leaf scattering coefficient for PAR σPAR */
	public double leafScatteringCoefficient = 0.2;

	/** Canopy reflectance for diffuse PAR ρcdP */
	public double canopyReflectionCoefficientDiffuse = 0.036;

	/** Soil reflectance for PAR ρsP */
	public double soilReflectance = 0.11; // Derived from [Sellers et al., 1996] except for WSA, SAV, BSV and OSH [Asner et al., 1998; Roberts et al., 1993].

	/** Extinction coefficient for longwave radiation k'L */
	public double extinctionCoefficientLongRadiation = 0.78; // [Goudriaan, 1977]

	/** Emissivity of the leaf ɛl */
	public double leafEmissivity = 0.98;

	/** Emissivity of the soil ɛs */
	public double soilEmissivity = 0.94;

	/** Default root depth substituted when not otherwise supplied. Unit: m. */
	public double defaultRootDepth = 0.5;

	/** Default canopy height substituted when not otherwise supplied. Unit: m. */
	public double defaultCanopyHeigth = 0.3;

	/** Default photosynthetic assimilation rate substituted when not otherwise supplied - used by the Medlyn stomatal-conductance stress factor. */
	public double defaultAssimilationRate = 0.5;


}
