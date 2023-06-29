package it.geoframe.blogspot.geoet.prospero.methods;

import static java.lang.Math.pow;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.prospero.data.Leaf;
import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class SurfaceTemperatureMethods {
	
	private double surfaceTemperature1;
	private double surfaceTemperature2;
	private double surfaceTemperature;
	private double deltaTemperature;
	
	private ProblemQuantities variables;
	private Leaf leafparameters;		
	private Parameters parameters;
	
	

	public double computeSurfaceTemperature(double shortWaveRadiation, double residual, double sensibleHeatTransferCoefficient, double airTemperature, double surfaceArea, double stress, double latentHeatTransferCoefficient,
    		double delta, double vaporPressure, double saturationVaporPressure, int	side, double longWaveRadiation){
    		
    	surfaceTemperature1 = (shortWaveRadiation - residual + sensibleHeatTransferCoefficient * airTemperature * surfaceArea +
    							stress * latentHeatTransferCoefficient*(delta * airTemperature + vaporPressure - saturationVaporPressure) * surfaceArea + side * longWaveRadiation * 4 * 1);
    	
    	surfaceTemperature2 =(1/(sensibleHeatTransferCoefficient*surfaceArea + stress*latentHeatTransferCoefficient * delta *surfaceArea + side * longWaveRadiation / airTemperature * 4 * 1));
    	
    	surfaceTemperature = surfaceTemperature1*surfaceTemperature2;
    	
    	return surfaceTemperature;}
	
	
	
	
	
	public double computeDeltaLeafTemperature(double absorbedRadiation, double residual, double airTemperature, double canopyArea, double stress, double atmosphericPressure){
    		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		surfaceTemperature1= absorbedRadiation -leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 4)) -2*parameters.latentHeatEvaporation*stress*canopyArea*0.622/atmosphericPressure*(variables.saturationVaporPressure-variables.vaporPressure)-residual;
		
		surfaceTemperature2=1/(leafparameters.leafSide*canopyArea*(leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant*pow (airTemperature, 3)) + 2*variables.convectiveTransferCoefficient*canopyArea + 2*parameters.latentHeatEvaporation*stress*canopyArea*0.622/atmosphericPressure*variables.delta);
		
    	deltaTemperature = surfaceTemperature1*surfaceTemperature2;
    	
    	return deltaTemperature;
    	
	}


}