package it.geoframe.blogspot.geoet.prospero.methods;

import static java.lang.Math.pow;
import static java.lang.Math.exp;

public class RadiationMethod {
	
	double diffuseExtinctionCoefficient = 0.719;
	double leafScatteringCoefficient = 0.2;
	double canopyReflectionCoefficientDiffuse = 0.036;
	
	public double computeLongWaveRadiationBalance(double leafSide, double longWaveEmittance, double airTemperature, double leafTemperature, double stefanBoltzmannConstant) {
		// Compute the net long wave radiation i.e. the incoming minus outgoing [J m-2 s-1]
		double longWaveRadiation = 4 * leafSide * longWaveEmittance * stefanBoltzmannConstant * (((pow (airTemperature, 3))*leafTemperature - (pow (airTemperature, 4))));
		return longWaveRadiation;	
	}
	
	public double computeAbsordebRadiationSunlit (double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
    	
    	double directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;
		
    	double scatteredExtinctionCoefficient = 0.46/solarElevationAngle;
		
    	double canopyReflectionCoefficientBeam = 1-exp((-2*0.041*directExtinctionCoefficientInCanopy)/(1+directExtinctionCoefficientInCanopy));
	    
    	double directAbsorbedRadiation = shortWaveRadiationDirect*(1-leafScatteringCoefficient)*(1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex));
	    
    	double diffuseAbsorbedRadiation = shortWaveRadiationDiffuse*(1-canopyReflectionCoefficientDiffuse)*
	    		(1-exp(-(diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
	    		(diffuseExtinctionCoefficient/(diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy));
	    
    	double scatteredAbsorbedRadiation = shortWaveRadiationDirect*((1-canopyReflectionCoefficientBeam)*
    			(1-exp(-(directExtinctionCoefficientInCanopy+scatteredExtinctionCoefficient)*leafAreaIndex))*
    			(scatteredExtinctionCoefficient/(directExtinctionCoefficientInCanopy+scatteredExtinctionCoefficient))-
    			(1-leafScatteringCoefficient)*(1-exp(-2*directExtinctionCoefficientInCanopy*leafAreaIndex))/2);

	    double absordebRadiationSunlit = directAbsorbedRadiation + diffuseAbsorbedRadiation + scatteredAbsorbedRadiation;

	    return absordebRadiationSunlit;
	    }
	public double computeAbsordebRadiationShadow (double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
    	double directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;
		double scatteredExtinctionCoefficient = 0.46/solarElevationAngle;
		double canopyReflectionCoefficientBeam = 1-exp((-2*0.041*directExtinctionCoefficientInCanopy)/(1+directExtinctionCoefficientInCanopy));		
		double diffuseAbsorbedRadiationShadow = shortWaveRadiationDiffuse*(	1-canopyReflectionCoefficientBeam)*
				(1-exp(-diffuseExtinctionCoefficient*leafAreaIndex)-(1-exp(-(diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
				(diffuseExtinctionCoefficient/(diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)));
		double scatteredAbsorbedRadiationShadow = shortWaveRadiationDirect*((1-canopyReflectionCoefficientBeam)*(1-exp(-scatteredExtinctionCoefficient*leafAreaIndex)-		    		
				(1-exp(-(scatteredExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
		    	(scatteredExtinctionCoefficient/(scatteredExtinctionCoefficient+directExtinctionCoefficientInCanopy))) - 		
		    	(1-leafScatteringCoefficient)*(1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex)-
		    	(1-exp(-2*directExtinctionCoefficientInCanopy*leafAreaIndex))/2));
		double absordebRadiationShadow = scatteredAbsorbedRadiationShadow + diffuseAbsorbedRadiationShadow;
	    return absordebRadiationShadow;
	    }	
	public double computeSunlitLeafAreaIndex (String typeOfCanopy, double leafAreaIndex, double solarElevationAngle) {
		if ("grassland".equals(typeOfCanopy)) {
			double sunlitLeafAreaIndex = leafAreaIndex;
			return sunlitLeafAreaIndex;
			}
		else {
			double directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;		
			double sunlitLeafAreaIndex = (1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex))/directExtinctionCoefficientInCanopy;
			return sunlitLeafAreaIndex;
		}
	}
}