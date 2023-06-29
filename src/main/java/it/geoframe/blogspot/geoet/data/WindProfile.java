package it.geoframe.blogspot.geoet.data;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class WindProfile {
	private ProblemQuantities variables;
			
	public double computeWindProfile(double windSpeed2m, double height) {
		variables = ProblemQuantities.getInstance();
		
		 // Computation of the wind speed at height H [m s-1]
		variables.windSpeedH = (windSpeed2m * (Math.log(67.8*height - 5.42)))/4.87;
		
		return variables.windSpeedH;	
	}

}
