package it.geoframe.blogspot.geoet.data;

import org.joda.time.DateTime;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class ProblemQuantities {
	
	private static ProblemQuantities uniqueInstance;

	public static ProblemQuantities getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ProblemQuantities();
		}
		return uniqueInstance;
	}
	
	public double areaCanopySun;
	public double areaCanopyShade;
	public double convectiveTransferCoefficient;
	public double delta;
	
	public double evaporation;
	public double fluxEvaporation;
	
	public double transpiration;
	public double fluxTranspiration;
	
	public double evapoTranspiration;
	public double fluxEvapoTranspiration;
	
	public double evapoTranspirationPT;
	public double fluxEvapoTranspirationPT;
	
	public double evapoTranspirationPM;
	public double evapoTranspirationPMdaily;
	public double fluxEvapoTranspirationPM;
	
	
	public double incidentSolarRadiationSoil;
	public double latentHeatTransferCoefficient;
	public double netLong;
	public double radFactorSun;
	public double radFactorShade;
	public double saturationVaporPressure;
	public double sensibleHeatTransferCoefficient;
	public double shortwaveCanopySun;
	public double shortwaveCanopyShade;
	public double solarElevationAngle;
	public double stressRadiationSun;
	public double stressRadiationShade;
	public double stressTemperature;
	public double stressVPD;
	public double stressWater;
	public double evaporationStressWater;
	
	
	public double vaporPressure;
	public double vapourPressureDeficit;
	public double vaporPressureDew;
	public double windInCanopy;
	public double windSoil;
	
	public double leafTemperatureSun;
	public double leafTemperatureShade;
	
	public double latentHeatFluxSun;
	public double sensibleHeatFluxSun;
	
	public double latentHeatFluxShade;
	public double sensibleHeatFluxShade;
	
	public double netLongWaveRadiationSun; 
	public double netLongWaveRadiationShade;
	
	public double energyBalanceResidualSun = 0;
	public double energyBalanceResidualShade = 0;
	
	public DateTime date;
	
	public int hourOfDay;
	public boolean isLigth;
	public double soilFluxparameter;
	
	public double windAtZ; //windAtZ_AboveGroundSurface 
	
	public double windSpeedH;
	
	
}
