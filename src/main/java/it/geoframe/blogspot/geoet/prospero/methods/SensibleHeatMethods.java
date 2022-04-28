package it.geoframe.blogspot.geoet.prospero.methods;
import static java.lang.Math.pow;
import static java.lang.Math.abs;

public class SensibleHeatMethods {
	
	public double computeConvectiveTransferCoefficient (double airTemperature, double windVelocity, double leafLength, double criticalReynoldsNumber, double prandtlNumber) { 
		//double criticalReynoldsNumber = this.criticalReynoldsNumber;
		// Formula from Monteith & Unsworth, 2007
		double thermalConductivity = (6.84 * pow(10,-5)) * airTemperature + 5.62 * pow(10,-3); 
		// Formula from Monteith & Unsworth, 2007
		double kinematicViscosity = (9 * pow(10,-8)) * airTemperature - 1.13 * pow(10,-5);
		double reynoldsNumber = (windVelocity * leafLength)/ kinematicViscosity;
		// from Incropera et al, 2006
		double c3 = criticalReynoldsNumber - reynoldsNumber;
		double c2 = (reynoldsNumber + criticalReynoldsNumber - abs(c3))/2;
		double c1 = 0.037 * pow(c2,0.8) - 0.664 * pow(c2,0.5);
		double nusseltNumber = (0.037 * pow(reynoldsNumber,0.8) - c1) * pow(prandtlNumber,0.33);
		// Formula from Schymanski and Or, 2017
		double convectiveTransferCoefficient = (thermalConductivity * nusseltNumber)/leafLength;
		return convectiveTransferCoefficient;
		}
	public double computeSensibleHeatTransferCoefficient(double convectiveTransferCoefficient, int leafSide) {
		double sensibleHeatTransferCoefficient = convectiveTransferCoefficient * leafSide;
		return sensibleHeatTransferCoefficient;
	}
	public double computeSensibleHeatFlux(double sensibleHeatTransferCoefficient, double leafTemperature, double airTemperature) {
		 // Computation of the sensible heat flux from leaf [J m-2 s-1]
		double sensibleHeatFlux = sensibleHeatTransferCoefficient * (leafTemperature - airTemperature);
		return sensibleHeatFlux;	
	}
}