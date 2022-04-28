package it.geoframe.blogspot.geoet.prospero.methods;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class PressureMethods {
	
	public double computeSaturationVaporPressure(double airTemperature, double waterMolarMass, double latentHeatEvaporation, double molarGasConstant) {
		 // Computation of the saturation vapor pressure at air temperature [Pa]
		double saturationVaporPressure = 611.0 * exp((waterMolarMass*latentHeatEvaporation/molarGasConstant)*((1.0/273.0)-(1.0/airTemperature)));
		return saturationVaporPressure;
	}
	
	public double computeDelta (double airTemperature, double waterMolarMass, double latentHeatEvaporation, double molarGasConstant) {
		// Computation of delta [Pa K-1]
		// Slope of saturation vapor pressure at air temperature
		double numerator = 611 * waterMolarMass * latentHeatEvaporation;
		double exponential = exp((waterMolarMass * latentHeatEvaporation / molarGasConstant)*((1/273.0)-(1/airTemperature)));
		double denominator = (molarGasConstant * pow(airTemperature,2));
		double delta = numerator * exponential / denominator;
		return delta;
	}
	
	public double computePressure (double defaultAtmosphericPressure, double massAirMolecule, double gravityConstant, double elevation, double boltzmannConstant, double airTemperature) {
		double exponential = exp(-(massAirMolecule * gravityConstant * elevation)/(boltzmannConstant * airTemperature));
		double pressure = defaultAtmosphericPressure * exponential;
		return pressure;
	}
	public double computeVaporPressure (double relativeHumidity, double saturationVaporPressure) {
		double vaporPressure = relativeHumidity * saturationVaporPressure/100.0;
		return vaporPressure;
	}
	
	public double computeVapourPressureDewPoint(double airTemperature) {
		double t = 1-(373.15/(airTemperature));// - (100-(relativeHumidity*100))/5;
		double expo = Math.exp(13.3185 * t - 1.976 * Math.pow(t,2) - 0.6445 * Math.pow(t,3) - 0.1229 * Math.pow(t,4));
		return expo;
	}
	public double computeVapourPressureDeficit(double vaporPressure, double vaporPressureDew) {
		double vapourPressureDeficit = (vaporPressure - vaporPressureDew)/1000;
		return vapourPressureDeficit;
	}
}
