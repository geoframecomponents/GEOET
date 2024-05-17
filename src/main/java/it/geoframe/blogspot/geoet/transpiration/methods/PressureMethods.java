package it.geoframe.blogspot.geoet.transpiration.methods;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.transpiration.data.Leaf;
import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class PressureMethods {
	private double saturationVaporPressure;
	private double numerator;
	private double deltaexponential;
	private double denominator;
	private double delta;
	private double exponential;
	private double pressure;
	private double vaporPressure;
	private double t;
	private double expo;
	private double vapourPressureDeficit;
	private double vapourPressureDelta;
	private double factor1;
	private double factor2;
	
	
	private ProblemQuantities variables;
	private Leaf leafparameters;		
	private Parameters parameters;
	
	
	
	public double computeSaturationVaporPressure(double airTemperature, double waterMolarMass, double latentHeatEvaporation, double molarGasConstant) {
		 // Computation of the saturation vapor pressure at air temperature [Pa]
		saturationVaporPressure = 611.0 * exp((waterMolarMass*latentHeatEvaporation/molarGasConstant)*((1.0/273.15)-(1.0/airTemperature)));
		return saturationVaporPressure;}
	
	
	public double computeDelta (double airTemperature, double waterMolarMass, double latentHeatEvaporation, double molarGasConstant) {
		// Computation of delta [Pa K-1]
		// Slope of saturation vapor pressure at air temperature
		numerator = 611 * waterMolarMass * latentHeatEvaporation;
		deltaexponential = exp((waterMolarMass * latentHeatEvaporation / molarGasConstant)*((1/273.15)-(1/airTemperature)));
		denominator = (molarGasConstant * pow(airTemperature,2));
		delta = numerator * deltaexponential / denominator;
		return delta;}
	
	
	public double computePressure (double defaultAtmosphericPressure, double massAirMolecule, double gravityConstant, double elevation, double boltzmannConstant, double airTemperature) {
		exponential = exp(-(massAirMolecule * gravityConstant * elevation)/(boltzmannConstant * airTemperature));
		pressure = defaultAtmosphericPressure * exponential;
		return pressure;}
	
	
	public double computeVaporPressure (double relativeHumidity, double saturationVaporPressure) {
		vaporPressure = relativeHumidity * saturationVaporPressure/100.0;
		return vaporPressure;}
	
	
	public double computeVapourPressureDewPoint(double airTemperature) {
		t = 1-(373.15/(airTemperature));// - (100-(relativeHumidity*100))/5;
		expo = Math.exp(13.3185 * t - 1.976 * Math.pow(t,2) - 0.6445 * Math.pow(t,3) - 0.1229 * Math.pow(t,4));
		return expo;}
	
	
	public double computeVapourPressureDeficit(double vaporPressure, double vaporPressureDew) {
		vapourPressureDeficit = (vaporPressure - vaporPressureDew)/1000;
		return vapourPressureDeficit;}
	
	
	public double computeVapourPressureDelta(double absorbedRadiation, double canopyArea, double airTemperature, double stress, double atmosphericPressure, double residual) {
		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		factor1=(leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 3)) + 2*variables.convectiveTransferCoefficient*canopyArea)/(leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 3)) + 2*variables.convectiveTransferCoefficient*canopyArea + 2*parameters.latentHeatEvaporation*stress*canopyArea*0.622/atmosphericPressure*variables.delta);
		
		factor2= (absorbedRadiation -leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 4)) - residual)/(leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 3)) + 2*variables.convectiveTransferCoefficient*canopyArea + 2*parameters.latentHeatEvaporation*stress*canopyArea*0.622/atmosphericPressure*variables.delta);
		
		vapourPressureDelta= factor1 *(variables.saturationVaporPressure-variables.vaporPressure) + factor2 * variables.delta;
				
		return vapourPressureDelta;}
	
	
	
	
	
	
}
