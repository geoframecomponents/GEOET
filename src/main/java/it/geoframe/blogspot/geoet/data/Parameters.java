package it.geoframe.blogspot.geoet.data;


import static java.lang.Math.pow;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.In;
import oms3.annotations.License;
import oms3.annotations.Unit;

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class Parameters {
	
	private static Parameters uniqueInstance;

	public static Parameters getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Parameters();
		}
		return uniqueInstance;
	}
	
	
	
	public double airSpecificHeat = 1010; // J kg-1 K-1
	public double airDensity = 1.2690;
	public double boltzmannConstant = 1.38066*pow(10,-23);
	public double criticalReynoldsNumber = 3000; 	//fixed
	public double gravityConstant = 9.80665;
	public double latentHeatEvaporation = 2.45*pow(10,6); // J/kg
	public double massAirMolecule = 29*1.66054*pow(10,-27);
	public double molarGasConstant = 8.314472;
	public double molarVolume = 0.023;
	public double prandtlNumber = 0.71; 			// fixed
	public double stefanBoltzmannConstant = 5.670373 * pow(10,-8); 
	public double waterMolarMass = 0.018;
	
	
	@Description("The air temperature default value in case of missing data.")
	@In
	@Unit("K")
	public double defaultAirTemperature = 15.0+273.15;
	  
	@Description("The wind default value in case of missing data.")
	@In
	@Unit("m s-1")
	public double defaultWindVelocity = 0.5;
	
	@Description("The humidity default value in case of missing data.")
	@In
	@Unit("%")
	public double defaultRelativeHumidity = 70.0;
		
	@Description("The short wave radiation default value in case of missing data.")
	@In
	@Unit("W m-2")
	public double defaultShortWaveRadiationDirect = 0.0;
	
	@Description("The atmospheric pressure default value in case of missing data.")
	@In
	@Unit("Pa")
	public double defaultAtmosphericPressure = 101325.0;
	
	@Description("The soilflux default value in case of missing data.")
	@In
	@Unit("W m-2")
	public double defaultSoilFlux = 0.0;
	
	@Description("The leaf area index default value in case of missing data.")
	@In
	@Unit("m2 m-2")
	public double defaultLeafAreaIndex = 1.0;
		
	@Description("Default soil moisture.")
	@In
	@Unit("m3 m-3")
	public double defaultSoilMoisture = 0.20;
	
	@In
	public double defaultStress = 1;
	
	@Description("Coefficient Cp eq. Penman-Monteith FAO equal to 900 in the case of a daily time step and equal to 37 in the case of a hourly time step")
	public double Cp = 900; 
	
	@Description("Coefficient Cd eq. Penman-Monteith FAO equal to 0.34")
	public double Cd = 0.34; 
	
	@Description("Coefficient alpha eq. Priestley-Taylor")
	public double alpha = 1.26; 
	
	@Description("extinction coefficient for diffuse and scattered diffuse PAR k'Pd")
	public double diffuseExtinctionCoefficient = 0.719;
	
	@Description("Leaf scattering coefficient for PAR σPAR")
	public double leafScatteringCoefficient = 0.2;
	
	@Description("Canopy reflectance for diffuse PAR ρcdP")
	public double canopyReflectionCoefficientDiffuse = 0.036;
	
	@Description("Soil reflectance for PAR ρsP")
	public double soilReflectance = 0.11; // Derived from [Sellers et al., 1996] except for WSA, SAV, BSV and OSH [Asner et al., 1998; Roberts et al., 1993]. 
	
	@Description("Extinction coefficient for longwave radiation k'L")
	public double extinctionCoefficientLongRadiation = 0.78; // [Goudriaan, 1977]
	
	@Description("Emissivity of the leaf ɛl")
	public double leafEmissivity = 0.98; 
	
	@Description("Emissivity of the soil ɛs")
	public double soilEmissivity = 0.94; 
	
	public double defaultRootDepth = 0.5;
	
	public double defaultCanopyHeigth = 0.3;
	
	
	
	
}
