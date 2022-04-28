package it.geoframe.blogspot.geoet.stressfactor.methods;

public class FaoWaterStress {
	
	
	public double computeFAOWaterStress(double soilMoisture, double waterFieldCapacity, double waterWiltingPoint, double rootsDepth, double depletionFraction) {
		
		double totalAvailableWater = 1000*(waterFieldCapacity - waterWiltingPoint)*rootsDepth;
	    double readilyAvailableWater = totalAvailableWater * depletionFraction;
	    double rootZoneDepletation = 1000 * (waterFieldCapacity - soilMoisture) * rootsDepth;
	    double waterStressCoefficient=(rootZoneDepletation<readilyAvailableWater)? 1:(totalAvailableWater - rootZoneDepletation) / (totalAvailableWater - readilyAvailableWater);
	    
	    if (waterStressCoefficient <= 0) {waterStressCoefficient = 0;}
	    if (waterStressCoefficient >= 1) {waterStressCoefficient = 1;}
	    
		return waterStressCoefficient;	
		

	}
	

}

