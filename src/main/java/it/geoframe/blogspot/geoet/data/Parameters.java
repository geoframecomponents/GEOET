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
	
	
	
	public double airSpecificHeat = 1010;
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
	public double defaultAirTemperature = 15.0+273.0;
	  
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
	
	@Description("Radiation parameter k'Pd")
	public double diffuseExtinctionCoefficient = 0.719;
	
	public double leafScatteringCoefficient = 0.2;
	
	@Description("Radiation parameter ρcdP")
	public double canopyReflectionCoefficientDiffuse = 0.036;
	
	
}
