package it.geoframe.blogspot.geoet.stressfactor.methods;
import java.lang.Math;

import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.prospero.methods.PressureMethods;

public class EnvironmentalStress {
	
	private ProblemQuantities variables;
	PressureMethods pressure = new PressureMethods();
	
	
	public double computeRadiationStress(double shortWaveRadiation, double alpha, double theta) {
		double shortWaveRadiationMicroMol=(shortWaveRadiation);
		double first = (alpha*shortWaveRadiationMicroMol)+1;

		double sqr1 = Math.pow(first, 2);
		double sqr2 = - 4*theta*alpha*shortWaveRadiationMicroMol;
		double sqr = sqr1+sqr2;
		double result = (1/(2*theta))*(alpha*shortWaveRadiationMicroMol+1-Math.sqrt((sqr))) ;
		
		if (Double.isNaN(result)) {result = 0;}
		if (result <= 0) {result = 0;}
	    if (result >= 1) {result = 1;}
	    
		return result;	
	}
	
	public double computeTemperatureStress(double airTemperature, double Tl, double Th, double T0) {
		airTemperature = airTemperature -273;
		double c = (Th-T0)/(T0-Tl);
		double b = 1/((T0-Tl)*Math.pow((Th-T0),c));
		double result = b* (airTemperature - Tl)* Math.pow((Th-airTemperature),c);
		
		if (Double.isNaN(result)) {result = 0;}
		
		if (result <= 0) {result = 0;}
	    if (result >= 1) {result = 1;}
	    
		return result;	
	}
	
	public double computeVapourPressureStress(double airTemperature, double VPD0) {
		
		variables = ProblemQuantities.getInstance();
		
		double result = Math.exp(-variables.vapourPressureDeficit/VPD0);
		
		if (Double.isNaN(result)) {result = 0;}
		if (result <= 0) {result = 0;}
	    if (result >= 1) {result = 1;}
	    
		return result;	
	}
	
}

