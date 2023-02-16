package it.geoframe.blogspot.geoet.stressfactor.methods;
import java.lang.Math;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class EnvironmentalStress {
	
	private ProblemQuantities variables;
	
	private double radiationStress = 1;
	private double first;
	private double sqr1;
	private double sqr2;
	private double sqr;
	private double temperatureStress = 1;
	private double c;
	private double b;
	private double vapourPressureStress = 1;

	
	public double computeRadiationStress(double shortWaveRadiation, double alpha, double theta) {
		
		//double radiationStress = 1;
		
		if(shortWaveRadiation <= 0) {radiationStress = 1;}
		
		else {
			//double shortWaveRadiationMicroMol=(shortWaveRadiation);
			first = (alpha*shortWaveRadiation)+1;

			sqr1 = Math.pow(first, 2);
			sqr2 = - 4*theta*alpha*shortWaveRadiation;
			sqr = sqr1+sqr2;
			radiationStress = (1/(2*theta))*(alpha*shortWaveRadiation+1-Math.sqrt((sqr))) ;
		
			if (Double.isNaN(radiationStress)) {radiationStress = 0;}
			if (radiationStress <= 0) {radiationStress = 0;}
			if (radiationStress >= 1) {radiationStress = 1;}
		}
		
		return radiationStress;}
	
	
	
	public double computeTemperatureStress(double airTemperature, double Tl, double Th, double T0) {
		
		airTemperature = airTemperature - 273.15;
		c = (Th-T0)/(T0-Tl);
		b = 1/((T0-Tl)*Math.pow((Th-T0),c));
		
		temperatureStress = b * (airTemperature - Tl) * Math.pow((Th - airTemperature),c);
		
		if (Double.isNaN(temperatureStress)) {temperatureStress = 0;}
		if (temperatureStress <= 0) {temperatureStress = 0;}
	    if (temperatureStress >= 1) {temperatureStress = 1;}
	    
		return temperatureStress;	
	}
	
	public double computeVapourPressureStress(double airTemperature, double VPD0) {
		
		variables = ProblemQuantities.getInstance();
		
		vapourPressureStress = Math.exp(-variables.vapourPressureDeficit/VPD0);
		
		if (Double.isNaN(vapourPressureStress)) {vapourPressureStress = 0;}
		if (vapourPressureStress <= 0) {vapourPressureStress = 0;}
	    if (vapourPressureStress >= 1) {vapourPressureStress = 1;}
	    
		return vapourPressureStress;	
	}
	
}

