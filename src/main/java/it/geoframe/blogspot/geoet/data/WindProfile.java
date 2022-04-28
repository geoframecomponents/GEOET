package it.geoframe.blogspot.geoet.data;

public class WindProfile {
			
	public double computeWindProfile(double windSpeed2m, double height) {
		 // Computation of the wind speed at height H [m s-1]
		double windSpeedH = (windSpeed2m * (Math.log(67.8*height - 5.42)))/4.87;
		return windSpeedH;	
	}

}
