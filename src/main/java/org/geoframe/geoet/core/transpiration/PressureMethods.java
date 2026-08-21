package org.geoframe.geoet.core.transpiration;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

import org.geoframe.geoet.core.config.Leaf;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")
public class PressureMethods {

	public static double computeSaturationVaporPressure(double airTemperature, double waterMolarMass,
			double latentHeatEvaporation, double molarGasConstant) {
		// Computation of the saturation vapor pressure at air temperature [Pa]
		double saturationVaporPressure = 611.0 * exp((waterMolarMass * latentHeatEvaporation / molarGasConstant)
				* ((1.0 / 273.15) - (1.0 / airTemperature)));
		return saturationVaporPressure;
	}

	public static double computeDelta(double airTemperature, double waterMolarMass, double latentHeatEvaporation,
			double molarGasConstant) {
		// Computation of delta [Pa K-1]
		// Slope of saturation vapor pressure at air temperature
		double numerator = 611 * waterMolarMass * latentHeatEvaporation;
		double deltaexponential = exp(
				(waterMolarMass * latentHeatEvaporation / molarGasConstant) * ((1 / 273.15) - (1 / airTemperature)));
		double denominator = (molarGasConstant * pow(airTemperature, 2));
		double delta = numerator * deltaexponential / denominator;
		return delta;
	}

	public static double computePressure(double defaultAtmosphericPressure, double massAirMolecule,
			double gravityConstant, double elevation, double boltzmannConstant, double airTemperature) {
		double exponential = exp(
				-(massAirMolecule * gravityConstant * elevation) / (boltzmannConstant * airTemperature));
		double pressure = defaultAtmosphericPressure * exponential;
		return pressure;
	}

	public static double computeVaporPressure(double relativeHumidity, double saturationVaporPressure) {
		double vaporPressure = relativeHumidity * saturationVaporPressure / 100.0;
		return vaporPressure;
	}

	public static double computeVapourPressureDewPoint(double airTemperature) {
		double t = 1 - (373.15 / (airTemperature));// - (100-(relativeHumidity*100))/5;
		double expo = Math
				.exp(13.3185 * t - 1.976 * Math.pow(t, 2) - 0.6445 * Math.pow(t, 3) - 0.1229 * Math.pow(t, 4));
		return expo;
	}

	public static double computeVapourPressureDeficit(double vaporPressure, double vaporPressureDew) {
		double vapourPressureDeficit = (vaporPressure - vaporPressureDew) / 1000;
		return vapourPressureDeficit;
	}

	public static double computeVapourPressureDelta(ProblemQuantities variables, Leaf leafparameters,
			Parameters parameters, double absorbedRadiation, double canopyArea, double airTemperature, double stress,
			double atmosphericPressure, double residual) {

		double factor1 = (leafparameters.leafSide * canopyArea
				* (leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant * pow(airTemperature, 3))
				+ 2 * variables.convectiveTransferCoefficient * canopyArea)
				/ (leafparameters.leafSide * canopyArea
						* (leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant
								* pow(airTemperature, 3))
						+ 2 * variables.convectiveTransferCoefficient * canopyArea
						+ 2 * parameters.latentHeatEvaporation * stress * canopyArea * 0.622 / atmosphericPressure
								* variables.delta);

		double factor2 = (absorbedRadiation - leafparameters.leafSide * canopyArea
				* (leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant * pow(airTemperature, 4))
				- residual)
				/ (leafparameters.leafSide * canopyArea
						* (leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant
								* pow(airTemperature, 3))
						+ 2 * variables.convectiveTransferCoefficient * canopyArea
						+ 2 * parameters.latentHeatEvaporation * stress * canopyArea * 0.622 / atmosphericPressure
								* variables.delta);

		double vapourPressureDelta = factor1 * (variables.saturationVaporPressure - variables.vaporPressure)
				+ factor2 * variables.delta;

		return vapourPressureDelta;
	}

}
