package it.geoframe.blogspot.geoet.prospero.methods;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class SurfaceTemperatureMethods {
	
	private double surfaceTemperature1;
	private double surfaceTemperature2;
	private double surfaceTemperature;

	public double computeSurfaceTemperature(double shortWaveRadiation, double residual, double sensibleHeatTransferCoefficient, double airTemperature, double surfaceArea, double stress, double latentHeatTransferCoefficient,
    		double delta, double vaporPressure, double saturationVaporPressure, int	side, double longWaveRadiation){
    		
    	surfaceTemperature1 = (shortWaveRadiation - residual + sensibleHeatTransferCoefficient * airTemperature * surfaceArea +
    							stress * latentHeatTransferCoefficient*(delta * airTemperature + vaporPressure - saturationVaporPressure) * surfaceArea + side * longWaveRadiation * 4 * 1);
    	
    	surfaceTemperature2 =(1/(sensibleHeatTransferCoefficient*surfaceArea + stress*latentHeatTransferCoefficient * delta *surfaceArea + side * longWaveRadiation / airTemperature * 4 * 1));
    	
    	surfaceTemperature = surfaceTemperature1*surfaceTemperature2;
    	
    	return surfaceTemperature;}


}