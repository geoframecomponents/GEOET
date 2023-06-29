package it.geoframe.blogspot.geoet.radiation.methods;
import static java.lang.Math.pow;
import it.geoframe.blogspot.geoet.data.Parameters;
import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.exp;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class RadiationMethod {
	
	//double diffuseExtinctionCoefficient = 0.719; //k'Pd
	//double leafScatteringCoefficient = 0.2; 
	//double canopyReflectionCoefficientDiffuse = 0.036; //ρcdP
	
	//Queste due righe le ho aggiunte nperchè stavo iniziando a fare la modifica di restyle del modello
	private Parameters parameters;
	//private ProblemQuantities variables;
	private double longWaveRadiation;
	private double directExtinctionCoefficientInCanopy;
	private double scatteredExtinctionCoefficient;
	private double canopyReflectionCoefficientBeam;
	private double directAbsorbedRadiation;
	private double diffuseAbsorbedRadiation;
	private double absordebRadiationSunlit;
	private double scatteredAbsorbedRadiation;
	private double diffuseAbsorbedRadiationShadow;
	private double scatteredAbsorbedRadiationShadow;
	private double absordebRadiationShadow;
	private double sunlitLeafAreaIndex;
	
	
	
	
	
	public double computeLongWaveRadiationBalance(double leafSide, double longWaveEmittance, double airTemperature, double leafTemperature, double stefanBoltzmannConstant) {
		// Compute the net long wave radiation i.e. the incoming minus outgoing [J m-2 s-1]
		longWaveRadiation = 4 * leafSide * longWaveEmittance * stefanBoltzmannConstant * (((pow (airTemperature, 3))*leafTemperature - (pow (airTemperature, 4))));
		return longWaveRadiation;}
	//QUESTO SAREBBE IL FEEDBACK RADIATIVO - la radiazione ad onda lunga incoming calcolata con la Ta
	
	public double computeAbsorbedRadiationSunlit (double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
		parameters = Parameters.getInstance();
    	
		//Ryu et all 2011
	
    	directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle; //kb
		
    	scatteredExtinctionCoefficient = 0.46/solarElevationAngle; //k'Pb
		
    	canopyReflectionCoefficientBeam = 1-exp((-2*0.041*directExtinctionCoefficientInCanopy)/(1+directExtinctionCoefficientInCanopy)); //ρcbP
    	
    	//Ryu et all 2011 eq.3
    	directAbsorbedRadiation = shortWaveRadiationDirect*(1-parameters.leafScatteringCoefficient)*(1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex));
    	
    	//Ryu et all 2011 eq.4
    	diffuseAbsorbedRadiation = shortWaveRadiationDiffuse*(1-parameters.canopyReflectionCoefficientDiffuse)*
	    		(1-exp(-(parameters.diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
	    		(parameters.diffuseExtinctionCoefficient/(parameters.diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy));
	    
    	//Ryu et all 2011 eq.5 
    	scatteredAbsorbedRadiation = shortWaveRadiationDirect*((1-canopyReflectionCoefficientBeam)*
    			(1-exp(-(directExtinctionCoefficientInCanopy+scatteredExtinctionCoefficient)*leafAreaIndex))*
    			(scatteredExtinctionCoefficient/(directExtinctionCoefficientInCanopy+scatteredExtinctionCoefficient))-
    			(1-parameters.leafScatteringCoefficient)*(1-exp(-2*directExtinctionCoefficientInCanopy*leafAreaIndex))/2);

	    absordebRadiationSunlit = directAbsorbedRadiation + diffuseAbsorbedRadiation + scatteredAbsorbedRadiation;

	    return absordebRadiationSunlit;}
	
	
	public double computeAbsorbedRadiationShadow (double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
		parameters = Parameters.getInstance();
		
    	directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;
		scatteredExtinctionCoefficient = 0.46/solarElevationAngle;
		
		canopyReflectionCoefficientBeam = 1-exp((-2*0.041*directExtinctionCoefficientInCanopy)/(1+directExtinctionCoefficientInCanopy));		
		
		diffuseAbsorbedRadiationShadow = shortWaveRadiationDiffuse*(	1-canopyReflectionCoefficientBeam)*
				(1-exp(-parameters.diffuseExtinctionCoefficient*leafAreaIndex)-(1-exp(-(parameters.diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
				(parameters.diffuseExtinctionCoefficient/(parameters.diffuseExtinctionCoefficient+directExtinctionCoefficientInCanopy)));
		
		scatteredAbsorbedRadiationShadow = shortWaveRadiationDirect*((1-canopyReflectionCoefficientBeam)*(1-exp(-scatteredExtinctionCoefficient*leafAreaIndex)-		    		
				(1-exp(-(scatteredExtinctionCoefficient+directExtinctionCoefficientInCanopy)*leafAreaIndex))*
		    	(scatteredExtinctionCoefficient/(scatteredExtinctionCoefficient+directExtinctionCoefficientInCanopy))) - 		
		    	(1-parameters.leafScatteringCoefficient)*(1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex)-
		    	(1-exp(-2*directExtinctionCoefficientInCanopy*leafAreaIndex))/2));
		
		absordebRadiationShadow = scatteredAbsorbedRadiationShadow + diffuseAbsorbedRadiationShadow;
	    
		return absordebRadiationShadow;}
	
	
	public double computeSunlitLeafAreaIndex (String typeOfCanopy, double leafAreaIndex, double solarElevationAngle) {
		
		if ("grassland".equals(typeOfCanopy)) {
			sunlitLeafAreaIndex = leafAreaIndex;
			return sunlitLeafAreaIndex;}
		
		else {
			directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;		
			sunlitLeafAreaIndex = (1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex))/directExtinctionCoefficientInCanopy;
			return sunlitLeafAreaIndex;}}
	
	
}