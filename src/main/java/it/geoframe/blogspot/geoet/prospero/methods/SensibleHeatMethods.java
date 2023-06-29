package it.geoframe.blogspot.geoet.prospero.methods;
import static java.lang.Math.pow;

import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.abs;


@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class SensibleHeatMethods {
	private double thermalConductivity;
	private double kinematicViscosity;
	private double reynoldsNumber;
	private double c3;
	private double c2;
	private double c1;
	private double nusseltNumber;
	private double convectiveTransferCoefficient;
	private double sensibleHeatTransferCoefficient;
	private double sensibleHeatFlux;
	
	public double computeConvectiveTransferCoefficient (double airTemperature, double windVelocity, double leafLength, double criticalReynoldsNumber, double prandtlNumber) { 
		
		// Formula from Monteith & Unsworth, 2007
		thermalConductivity = (6.84 * pow(10,-5)) * airTemperature + 5.62 * pow(10,-3); 
		
		// Formula from Monteith & Unsworth, 2007
		kinematicViscosity = (9 * pow(10,-8)) * airTemperature - 1.13 * pow(10,-5);
		reynoldsNumber = (windVelocity * leafLength)/ kinematicViscosity;
		
		// from Incropera et al, 2006
		c3 = criticalReynoldsNumber - reynoldsNumber;
		c2 = (reynoldsNumber + criticalReynoldsNumber - abs(c3))/2;
		c1 = 0.037 * pow(c2,0.8) - 0.664 * pow(c2,0.5);
		nusseltNumber = (0.037 * pow(reynoldsNumber,0.8) - c1) * pow(prandtlNumber,0.33);
		
		// Formula from Schymanski and Or, 2017
		convectiveTransferCoefficient = (thermalConductivity * nusseltNumber)/leafLength;
		return convectiveTransferCoefficient;}
	
	
	public double computeSensibleHeatTransferCoefficient(double convectiveTransferCoefficient, int leafSide) {
		sensibleHeatTransferCoefficient = convectiveTransferCoefficient * leafSide;
		return sensibleHeatTransferCoefficient;}
	
	
	public double computeSensibleHeatFlux(double sensibleHeatTransferCoefficient, double leafTemperature, double airTemperature) {
		 // Computation of the sensible heat flux from leaf [J m-2 s-1]
		sensibleHeatFlux = sensibleHeatTransferCoefficient * (leafTemperature - airTemperature);
		return sensibleHeatFlux;}
	
}