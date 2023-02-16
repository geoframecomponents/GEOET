package it.geoframe.blogspot.geoet.prospero.methods;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.prospero.data.Leaf;
import it.geoframe.blogspot.geoet.radiation.methods.*;
import oms3.annotations.Author;
import oms3.annotations.License;
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class Transpiration {
	
	private ProblemQuantities variables;
	private Leaf leafparameters;		
	private Parameters parameters;
	
	RadiationMethod radiationMethods 				= new RadiationMethod();
	SensibleHeatMethods sensibleHeat 				= new SensibleHeatMethods();
	LatentHeatMethods latentHeat 					= new LatentHeatMethods();
	EnergyBalance energyBalance 					= new EnergyBalance();
	SurfaceTemperatureMethods surfaceTemperature	= new SurfaceTemperatureMethods();
	
	

	public double computeTranspiration (double stressSun, double stressShade, double longWaveRadiation, double airTemperature, double time, double nullValue) {
		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		
		variables.fluxTranspiration = 0.0;
		
		//////////////////Transpiration from sun canopy //////////////////	

		variables.energyBalanceResidualSun = 0;
		// Compute the leaf temperature in sunlight	
		variables.leafTemperatureSun = surfaceTemperature.computeSurfaceTemperature(variables.shortwaveCanopySun, variables.energyBalanceResidualSun, variables.sensibleHeatTransferCoefficient,airTemperature, 
				variables.areaCanopySun, stressSun,variables.latentHeatTransferCoefficient,variables.delta,variables.vaporPressure,variables.saturationVaporPressure,leafparameters.leafSide,longWaveRadiation);
		
        // Compute the net longwave radiation in sunlight
		variables.netLongWaveRadiationSun = variables.areaCanopySun*radiationMethods.computeLongWaveRadiationBalance(leafparameters.leafSide, leafparameters.longWaveEmittance, airTemperature, 
				variables.leafTemperatureSun, parameters.stefanBoltzmannConstant);
		
		
		// Compute the latent heat flux from the sunlight area
		variables.latentHeatFluxSun = variables.areaCanopySun*stressSun*latentHeat.computeLatentHeatFlux(variables.delta,  variables.leafTemperatureSun,  airTemperature,  
				variables.latentHeatTransferCoefficient,variables.sensibleHeatTransferCoefficient, variables.vaporPressure, variables.saturationVaporPressure);
		
		
		// Compute the sensible heat flux from the sunlight area
		variables.sensibleHeatFluxSun = variables.areaCanopySun*sensibleHeat.computeSensibleHeatFlux(variables.sensibleHeatTransferCoefficient, variables.leafTemperatureSun, airTemperature);
		
		
		// Compute the residual of the energy balance for the sunlight area								
		variables.energyBalanceResidualSun = energyBalance.computeEnergyBalance(variables.shortwaveCanopySun, variables.energyBalanceResidualSun, variables.netLongWaveRadiationSun, 
				variables.latentHeatFluxSun, variables.sensibleHeatFluxSun);

		
		//////////////////Transpiration from shade canopy //////////////////
		
		// FIRST ITERATION ENERGY BALANCE SHADE
		// Initialization of the residual of the energy balance
        variables.energyBalanceResidualShade = 0;

		// Compute the leaf temperature in shadow
        variables.leafTemperatureShade =  surfaceTemperature.computeSurfaceTemperature(variables.shortwaveCanopyShade, variables.energyBalanceResidualShade, variables.sensibleHeatTransferCoefficient,airTemperature, variables.areaCanopyShade, stressShade,variables.latentHeatTransferCoefficient,variables.delta,variables.vaporPressure,variables.saturationVaporPressure,leafparameters.leafSide,longWaveRadiation);
		
        // Compute the net longwave radiation in shade
		variables.netLongWaveRadiationShade = variables.areaCanopyShade*radiationMethods.computeLongWaveRadiationBalance(leafparameters.leafSide, leafparameters.longWaveEmittance, airTemperature, variables.leafTemperatureShade, parameters.stefanBoltzmannConstant);
		
		
		// Compute the latent heat flux from the shaded area
		variables.latentHeatFluxShade = variables.areaCanopyShade*stressShade*latentHeat.computeLatentHeatFlux(variables.delta, variables.leafTemperatureShade, airTemperature, variables.latentHeatTransferCoefficient,variables.sensibleHeatTransferCoefficient,  variables.vaporPressure,  variables.saturationVaporPressure);
		
		
		// Compute the sensible heat flux from the shaded area				
		variables.sensibleHeatFluxShade = variables.areaCanopyShade*sensibleHeat.computeSensibleHeatFlux(variables.sensibleHeatTransferCoefficient, variables.leafTemperatureShade, airTemperature);
		
		// Compute the residual of the energy balance for the shaded area				
		variables.energyBalanceResidualShade = energyBalance.computeEnergyBalance(variables.shortwaveCanopyShade, variables.energyBalanceResidualShade, variables.netLongWaveRadiationShade, variables.latentHeatFluxShade, variables.sensibleHeatFluxShade);

	
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