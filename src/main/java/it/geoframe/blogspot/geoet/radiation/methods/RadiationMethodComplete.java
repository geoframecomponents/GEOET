package it.geoframe.blogspot.geoet.radiation.methods;
import static java.lang.Math.pow;
import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import it.geoframe.blogspot.geoet.transpiration.data.Leaf;
import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.exp;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class RadiationMethodComplete {
	
	private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	private Leaf leafparameters;
	

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
	private double leafRadiativeFeedback;
	

	
	public double computeAbsorbedRadiationSunlit (double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
		parameters = Parameters.getInstance();
    	
		//Ryu et all 2011
	
    	directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle; //kb
		
    	scatteredExtinctionCoefficient = 0.46/solarElevationAngle; //k'Pb [de Pury and Farquhar, 1997]
		
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

	    absordebRadiationSunlit = directAbsorbedRadiation + diffuseAbsorbedRadiation + scatteredAbsorbedRadiation + variables.directAbsorbedRadiationReflectedSoilSunlit;

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
		
		absordebRadiationShadow = scatteredAbsorbedRadiationShadow + diffuseAbsorbedRadiationShadow + variables.directAbsorbedRadiationReflectedSoilShadow;
	    
		return absordebRadiationShadow;}
	
	
	public double computeSunlitLeafAreaIndex (String typeOfCanopy, double leafAreaIndex, double solarElevationAngle) {
		
		if ("grassland".equals(typeOfCanopy)) {
			sunlitLeafAreaIndex = leafAreaIndex;
			return sunlitLeafAreaIndex;}
		
		else {
			directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle;		
			sunlitLeafAreaIndex = (1-exp(-directExtinctionCoefficientInCanopy*leafAreaIndex))/directExtinctionCoefficientInCanopy;
			return sunlitLeafAreaIndex;}}
	
	
	public void computeAbsorbedRadiationReflectedSoil (String typeOfCanopy, double leafAreaIndex, double shortWaveRadiationDirect,double shortWaveRadiationDiffuse) {
		
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
    	
		//Ryu et all 2011
	
    	canopyReflectionCoefficientBeam = 1-exp((-2*0.041*directExtinctionCoefficientInCanopy)/(1+directExtinctionCoefficientInCanopy)); //ρcbP
    	
    	//Ryu et all 2011 eq. 8
    	if ("grassland".equals(typeOfCanopy)) {
    		parameters.soilReflectance = 0.25;} // Derived from [Sellers et al., 1996] except for WSA, SAV, BSV and OSH [Asner et al., 1998; Roberts et al., 1993]. 
    	
    	variables.directAbsorbedRadiationReflectedSoilSunlit = ((1-canopyReflectionCoefficientBeam)*shortWaveRadiationDirect + (1-parameters.canopyReflectionCoefficientDiffuse)*shortWaveRadiationDiffuse - 
    			(variables.shortwaveCanopySun+variables.shortwaveCanopyShade)) * parameters.soilReflectance * exp(-parameters.diffuseExtinctionCoefficient*leafAreaIndex);
    	
    	//Ryu et all 2011 eq. 9
    	variables.directAbsorbedRadiationReflectedSoilShadow = ((1-canopyReflectionCoefficientBeam)*shortWaveRadiationDirect + (1-parameters.canopyReflectionCoefficientDiffuse)*shortWaveRadiationDiffuse - 
    			(variables.shortwaveCanopySun+variables.shortwaveCanopyShade)) * parameters.soilReflectance * (1 - exp(-parameters.diffuseExtinctionCoefficient*leafAreaIndex));}
	
	
	/*public double computeLongWaveRadiationBalance(double leafSide, double longWaveEmittance, double airTemperature, double leafTemperature, double stefanBoltzmannConstant) {
		// Compute the net long wave radiation i.e. the incoming minus outgoing [J m-2 s-1]
		longWaveRadiation = 4 * leafSide * longWaveEmittance * stefanBoltzmannConstant * (((pow (airTemperature, 3))*leafTemperature - (pow (airTemperature, 4))));
		return longWaveRadiation;}*/
	
	public double computeLeafRadiativeFeedback(double airTemperature, double leafTemperature, double canopyArea) {
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		leafRadiativeFeedback = leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 3) * leafTemperature);
		return leafRadiativeFeedback;}
	
	
	
	
	public void computeAirEmissivity() {
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		// Compute the emissivity of air according to Prata 1996
		//Eq. 11
		variables.precipitableWater= 4650 * variables.saturationVaporPressure/input.airTemperature;
		
		variables.airEmissivity = (1 - ( 1 + variables.precipitableWater) * exp( - (pow((1.2 + 3*variables.precipitableWater) ,0.5))));}
	
	public void computeRadiativeConductance() {
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		// Compute the radiative conductance g_r [kg m-2 s-1]
		//Table A1
		variables.radiativeConductance = (4*parameters.leafEmissivity*parameters.stefanBoltzmannConstant*pow(input.airTemperature,3))/parameters.airSpecificHeat;}
	
	
	
	
	
	public void computeAbsorbedLongwaveRadiation (double leafAreaIndex, double solarElevationAngle) {
		
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
    	
		//Ryu et all 2011
		directExtinctionCoefficientInCanopy = 0.5/solarElevationAngle; //kb
		
    	
    	//Ryu et all 2011 eq. 20
    	variables.absorbedLongwaveRadiationSunlit = - parameters.extinctionCoefficientLongRadiation*parameters.stefanBoltzmannConstant*pow(input.airTemperature,4)*
    			(parameters.leafEmissivity*(1-variables.airEmissivity)*(1-exp(-(directExtinctionCoefficientInCanopy+parameters.extinctionCoefficientLongRadiation)*leafAreaIndex))/(directExtinctionCoefficientInCanopy+parameters.extinctionCoefficientLongRadiation)+
    			(1-parameters.soilEmissivity)*(parameters.leafEmissivity-variables.airEmissivity)*(1-exp(-2*parameters.extinctionCoefficientLongRadiation*leafAreaIndex))/(2*parameters.extinctionCoefficientLongRadiation)*
    			(1-exp(-(directExtinctionCoefficientInCanopy-parameters.extinctionCoefficientLongRadiation)*leafAreaIndex))/(directExtinctionCoefficientInCanopy-parameters.extinctionCoefficientLongRadiation)) 
    			- parameters.airSpecificHeat*variables.radiativeConductance*(variables.leafTemperatureSun - input.airTemperature);
    			
    			
    	//Ryu et all 2011 eq. 21
    	variables.absorbedLongwaveRadiationShadow = - parameters.extinctionCoefficientLongRadiation*parameters.stefanBoltzmannConstant*pow(input.airTemperature,4)*
    			(parameters.leafEmissivity*(1-variables.airEmissivity) * (1-exp(-parameters.extinctionCoefficientLongRadiation*leafAreaIndex))/parameters.extinctionCoefficientLongRadiation
    					-(1-parameters.soilEmissivity) * (parameters.leafEmissivity-variables.airEmissivity)
    					* exp(-parameters.extinctionCoefficientLongRadiation*leafAreaIndex)*(1-exp(-parameters.extinctionCoefficientLongRadiation*leafAreaIndex))/parameters.extinctionCoefficientLongRadiation) - variables.absorbedLongwaveRadiationSunlit
    			- parameters.airSpecificHeat*variables.radiativeConductance*(variables.leafTemperatureSun - input.airTemperature) - parameters.airSpecificHeat*variables.radiativeConductance*(variables.leafTemperatureShade - input.airTemperature);
    	}
		
	
	public void computeLongRadiationFromSoil () {
		input = InputTimeSeries.getInstance();
		variables = ProblemQuantities.getInstance();
		
		variables.soilTemperature = input.airTemperature;
		variables.longRadiationFromSoil = parameters.soilEmissivity*parameters.stefanBoltzmannConstant* pow(variables.soilTemperature,4);
	}
	
	public void computeLongRadiationFromShadeLeaf () {
		input = InputTimeSeries.getInstance();
		variables = ProblemQuantities.getInstance();
		
		variables.longRadiationFromShadeLeaf = parameters.leafEmissivity*parameters.stefanBoltzmannConstant* pow(variables.leafTemperatureShade,4);
	}
	
	
	
	public void computeIncidentRadiation () {
		input = InputTimeSeries.getInstance();
		variables = ProblemQuantities.getInstance();
		

		variables.NewincidentSolarRadiationSoil = input.shortWaveRadiationDirect + input.shortWaveRadiationDiffuse + input.longWaveRadiation  
				-  variables.shortwaveCanopySun - variables.shortwaveCanopyShade 
				- variables.absorbedLongwaveRadiationSunlit - variables.absorbedLongwaveRadiationShadow
				- variables.longRadiationFromSoil + variables.longRadiationFromShadeLeaf;}
	
	
	
}

