package org.geoframe.geoet.core.transpiration;

import static java.lang.Math.pow;

import org.geoframe.geoet.core.config.Leaf;
import org.geoframe.geoet.core.state.ETProblemQuantities;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class SurfaceTemperatureMethods {

	public static double computeSurfaceTemperature(double shortWaveRadiation, double residual,
			double sensibleHeatTransferCoefficient, double airTemperature, double surfaceArea, double stress,
			double latentHeatTransferCoefficient, double delta, double vaporPressure, double saturationVaporPressure,
			int side, double longWaveRadiation) {

		double surfaceTemperature1 = (shortWaveRadiation - residual
				+ sensibleHeatTransferCoefficient * airTemperature * surfaceArea
				+ stress * latentHeatTransferCoefficient
						* (delta * airTemperature + vaporPressure - saturationVaporPressure) * surfaceArea
				+ side * longWaveRadiation * 4 * 1);

		double surfaceTemperature2 = (1 / (sensibleHeatTransferCoefficient * surfaceArea
				+ stress * latentHeatTransferCoefficient * delta * surfaceArea
				+ side * longWaveRadiation / airTemperature * 4 * 1));

		double surfaceTemperature = surfaceTemperature1 * surfaceTemperature2;

		return surfaceTemperature;
	}

	/**
	 * @param stefanBoltzmannConstant W m-2 K-4
	 * @param latentHeatEvaporation   latent heat of vaporization of water, J
	 *                                kg-1
	 */
	public static double computeDeltaLeafTemperature(ETProblemQuantities variables, Leaf leafparameters,
			double stefanBoltzmannConstant, double latentHeatEvaporation, double absorbedRadiation, double residual,
			double airTemperature, double canopyArea, double stress, double atmosphericPressure) {

		double surfaceTemperature1 = absorbedRadiation
				- leafparameters.leafSide * canopyArea
						* (leafparameters.longWaveEmittance * stefanBoltzmannConstant * pow(airTemperature, 4))
				- 2 * latentHeatEvaporation * stress * canopyArea * 0.622 / atmosphericPressure
						* (variables.saturationVaporPressure - variables.vaporPressure)
				- residual;

		double surfaceTemperature2 = 1 / (leafparameters.leafSide * canopyArea
				* (leafparameters.longWaveEmittance * stefanBoltzmannConstant * pow(airTemperature, 3))
				+ 2 * variables.convectiveTransferCoefficient * canopyArea + 2 * latentHeatEvaporation * stress
						* canopyArea * 0.622 / atmosphericPressure * variables.delta);

		double deltaTemperature = surfaceTemperature1 * surfaceTemperature2;

		return deltaTemperature;

	}

}