package it.geoframe.blogspot.geoet.transpiration.methods;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.radiation.methods.*;
import it.geoframe.blogspot.geoet.transpiration.data.Leaf;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.License;
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Description("This class compute the leaf temperature, the vapour pressure deficit, the sensible heat and latent heat according to "
		+ "Elementary mathematics sheds light on the transpiration budget under water stress [D'Amato e Rigon 2023]")
@License("General Public License Version 3 (GPLv3)")

public class TranspirationBudget {
	
	private ProblemQuantities variables;
	private Leaf leafparameters;		
	private Parameters parameters;
	
	RadiationMethodComplete radiationMethods 				= new RadiationMethodComplete();
	SensibleHeatMethods sensibleHeat 				= new SensibleHeatMethods();
	LatentHeatMethods latentHeat 					= new LatentHeatMethods();
	EnergyBalance energyBalance 					= new EnergyBalance();
	SurfaceTemperatureMethods surfaceTemperature	= new SurfaceTemperatureMethods();
	PressureMethods pressure 			= new PressureMethods(); 
	
	

	public double computeTranspirationBudget (double stressSun, double stressShade,double atmosphericPressure, double airTemperature, double time, double nullValue) {
		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		variables.fluxTranspiration = 0.0;
		
		//////////////////SUN CANOPY ENERGY BALANCE //////////////////	
		variables.energyBalanceResidualSun = 0;
		
		// Compute the leaf temperature in sunlit	
		variables.deltaTemperatureSun = surfaceTemperature.computeDeltaLeafTemperature(variables.absorbedRadiationCanopySun, variables.energyBalanceResidualSun, airTemperature, variables.areaCanopySun, stressSun, atmosphericPressure);
		
		variables.leafTemperatureSun = variables.deltaTemperatureSun + airTemperature;
		
		// Compute the sun leaf radiative feedback
		variables.leafRadiativeFeedbackSun = radiationMethods.computeLeafRadiativeFeedback(airTemperature, variables.leafTemperatureSun, variables.areaCanopySun);
		
		// Compute the sensible heat flux from the sunlit leaf
		variables.sensibleHeatFluxSun = leafparameters.leafSide * variables.convectiveTransferCoefficient * variables.areaCanopySun * variables.deltaTemperatureSun;
				
		// Compute the vapour pressure deficit eq.13 e eq.8 (e∆ = δa + ∆ T∆)
		variables.vapourPressureDeltaSun1 = pressure.computeVapourPressureDelta(variables.absorbedRadiationCanopySun, variables.areaCanopySun, airTemperature, stressSun, atmosphericPressure, variables.energyBalanceResidualSun);
		variables.vapourPressureDeltaSun2 = (variables.saturationVaporPressure-variables.vaporPressure) + variables.delta * variables.deltaTemperatureSun; 
		
		// Compute the latent heat flux from the sunlit leaf
		variables.latentHeatFluxSun = 2*parameters.latentHeatEvaporation*stressSun*variables.areaCanopySun*0.622/atmosphericPressure *((variables.saturationVaporPressure-variables.vaporPressure) + variables.delta*variables.deltaTemperatureSun);
		
		
	
		// Compute the residual of the energy balance for the sunlit leaf							
		variables.energyBalanceResidualSun = variables.absorbedRadiationCanopySun - variables.leafRadiativeFeedbackSun - variables.sensibleHeatFluxSun - variables.latentHeatFluxSun;

		
		
		
		
		
		//////////////////SHADE CANOPY ENERGY BALANCE //////////////////	
		variables.deltaTemperatureShade = surfaceTemperature.computeDeltaLeafTemperature(variables.absorbedRadiationCanopyShade, variables.energyBalanceResidualShade, airTemperature, variables.areaCanopyShade, stressShade, atmosphericPressure);
		
		variables.leafTemperatureShade = variables.deltaTemperatureShade + airTemperature;
		
		// Compute the sun leaf radiative feedback
		variables.leafRadiativeFeedbackShade = radiationMethods.computeLeafRadiativeFeedback(airTemperature, variables.leafTemperatureShade, variables.areaCanopyShade);
		
		// Compute the sensible heat flux from the sunlit leaf
		variables.sensibleHeatFluxShade = leafparameters.leafSide * variables.convectiveTransferCoefficient * variables.areaCanopyShade * variables.deltaTemperatureShade;
				
		// Compute the vapour pressure deficit eq.13 e eq.8 (e∆ = δa + ∆ T∆)
		variables.vapourPressureDeltaShade1 = pressure.computeVapourPressureDelta(variables.absorbedRadiationCanopyShade, variables.areaCanopyShade, airTemperature, stressShade, atmosphericPressure, variables.energyBalanceResidualShade);
		variables.vapourPressureDeltaShade2 = (variables.saturationVaporPressure-variables.vaporPressure) + variables.delta * variables.deltaTemperatureShade; 
		
		// Compute the latent heat flux from the sunlit leaf
		variables.latentHeatFluxShade = 2*parameters.latentHeatEvaporation*stressShade*variables.areaCanopyShade*0.622/atmosphericPressure *((variables.saturationVaporPressure-variables.vaporPressure) + variables.delta*variables.deltaTemperatureShade);
		
		
	
		// Compute the residual of the energy balance for the sunlit leaf							
		variables.energyBalanceResidualShade = variables.absorbedRadiationCanopyShade - variables.leafRadiativeFeedbackShade - variables.sensibleHeatFluxShade - variables.latentHeatFluxShade;

		
		variables.latentHeatFluxSun=(variables.latentHeatFluxSun<0)?0:variables.latentHeatFluxSun;
		variables.latentHeatFluxShade=(variables.latentHeatFluxShade<0)?0:variables.latentHeatFluxShade;
		
		
		
		
		variables.fluxTranspiration = (variables.latentHeatFluxSun+variables.latentHeatFluxShade);// --> W/m2
		
		 
		 
		if (Double.isNaN(variables.fluxTranspiration)) {variables.fluxTranspiration = 0;}
		
		
		if (airTemperature == nullValue) {
			System.out.printf("\nAir temperature is null");
			variables.fluxTranspiration=nullValue;}
		
		
		/*System.out.println("variables.leafTemperatureSun is  = "+ variables.leafTemperatureSun);
		System.out.println("variables.netLongWaveRadiationSun  is  = "+ variables.netLongWaveRadiationSun );
		System.out.println("CELJstressSun is  = "+ stressSun);*/
		
		return variables.fluxTranspiration;
		
		
	
    }
	
	

    /*public double computeSurfaceTemperature(double shortWaveRadiation, double residual, double sensibleHeatTransferCoefficient, double airTemperature, double surfaceArea, double stress, double latentHeatTransferCoefficient,
    		double delta, double vaporPressure, double saturationVaporPressure, int	side, double longWaveRadiation){
    		
    	double surfaceTemperature1 = (shortWaveRadiation - residual + sensibleHeatTransferCoefficient*airTemperature*surfaceArea +
    							stress * latentHeatTransferCoefficient*(delta*airTemperature + vaporPressure - saturationVaporPressure)*surfaceArea  +
    							side * longWaveRadiation * 4 *1);
    	double surfaceTemperature2 =(1/(sensibleHeatTransferCoefficient*surfaceArea +
    				stress*latentHeatTransferCoefficient * delta *surfaceArea +
    				side * longWaveRadiation/airTemperature * 4*1));
    	double surfaceTemperature = surfaceTemperature1*surfaceTemperature2;
    	return surfaceTemperature;}*/
    
    
    
    /*public double computeEnergyBalance(double shortWaveRadiation, double residual, double longWaveRadiation, double latentHeatFlux, double sensibleHeatFlux){
    		
    	double energyResidual = shortWaveRadiation - residual - longWaveRadiation - latentHeatFlux - sensibleHeatFlux;
    	return energyResidual;}*/
	
}