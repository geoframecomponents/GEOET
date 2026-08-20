package org.geoframe.geoet.transpiration.methods;

import org.geoframe.geoet.data.Leaf;
import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.radiation.methods.RadiationMethod;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class ProsperoModel {

	public static double computeTranspiration(ProblemQuantities variables, Leaf leafparameters, Parameters parameters,
			double stressSun, double stressShade, double longWaveRadiation, double airTemperature, double time,
			double nullValue) {

		variables.fluxTranspiration = 0.0;

		////////////////// Transpiration from sun canopy //////////////////

		variables.energyBalanceResidualSun = 0;
		// Compute the leaf temperature in sunlight
		variables.leafTemperatureSun = SurfaceTemperatureMethods.computeSurfaceTemperature(variables.shortwaveCanopySun,
				variables.energyBalanceResidualSun, variables.sensibleHeatTransferCoefficient, airTemperature,
				variables.areaCanopySun, stressSun, variables.latentHeatTransferCoefficient, variables.delta,
				variables.vaporPressure, variables.saturationVaporPressure, leafparameters.leafSide, longWaveRadiation);

		// Compute the net longwave radiation in sunlight
		variables.netLongWaveRadiationSun = variables.areaCanopySun * RadiationMethod.computeLongWaveRadiationBalance(
				leafparameters.leafSide, leafparameters.longWaveEmittance, airTemperature, variables.leafTemperatureSun,
				parameters.stefanBoltzmannConstant);

		// Compute the latent heat flux from the sunlight area
		variables.latentHeatFluxSun = variables.areaCanopySun * stressSun
				* LatentHeatMethods.computeLatentHeatFlux(variables.delta, variables.leafTemperatureSun, airTemperature,
						variables.latentHeatTransferCoefficient, variables.vaporPressure,
						variables.saturationVaporPressure);

		// Compute the sensible heat flux from the sunlight area
		variables.sensibleHeatFluxSun = variables.areaCanopySun * SensibleHeatMethods.computeSensibleHeatFlux(
				variables.sensibleHeatTransferCoefficient, variables.leafTemperatureSun, airTemperature);

		// Compute the residual of the energy balance for the sunlight area
		variables.energyBalanceResidualSun = EnergyBalance.computeEnergyBalance(variables.shortwaveCanopySun,
				variables.energyBalanceResidualSun, variables.netLongWaveRadiationSun, variables.latentHeatFluxSun,
				variables.sensibleHeatFluxSun);

		////////////////// Transpiration from shade canopy //////////////////

		// FIRST ITERATION ENERGY BALANCE SHADE
		// Initialization of the residual of the energy balance
		variables.energyBalanceResidualShade = 0;

		// Compute the leaf temperature in shadow
		variables.leafTemperatureShade = SurfaceTemperatureMethods.computeSurfaceTemperature(
				variables.shortwaveCanopyShade, variables.energyBalanceResidualShade,
				variables.sensibleHeatTransferCoefficient, airTemperature, variables.areaCanopyShade, stressShade,
				variables.latentHeatTransferCoefficient, variables.delta, variables.vaporPressure,
				variables.saturationVaporPressure, leafparameters.leafSide, longWaveRadiation);

		// Compute the net longwave radiation in shade
		variables.netLongWaveRadiationShade = variables.areaCanopyShade * RadiationMethod
				.computeLongWaveRadiationBalance(leafparameters.leafSide, leafparameters.longWaveEmittance,
						airTemperature, variables.leafTemperatureShade, parameters.stefanBoltzmannConstant);

		// Compute the latent heat flux from the shaded area
		variables.latentHeatFluxShade = variables.areaCanopyShade * stressShade
				* LatentHeatMethods.computeLatentHeatFlux(variables.delta, variables.leafTemperatureShade,
						airTemperature, variables.latentHeatTransferCoefficient, variables.vaporPressure,
						variables.saturationVaporPressure);

		// Compute the sensible heat flux from the shaded area
		variables.sensibleHeatFluxShade = variables.areaCanopyShade * SensibleHeatMethods.computeSensibleHeatFlux(
				variables.sensibleHeatTransferCoefficient, variables.leafTemperatureShade, airTemperature);

		// Compute the residual of the energy balance for the shaded area
		variables.energyBalanceResidualShade = EnergyBalance.computeEnergyBalance(variables.shortwaveCanopyShade,
				variables.energyBalanceResidualShade, variables.netLongWaveRadiationShade,
				variables.latentHeatFluxShade, variables.sensibleHeatFluxShade);

		variables.latentHeatFluxSun = (variables.latentHeatFluxSun < 0) ? 0 : variables.latentHeatFluxSun;
		variables.latentHeatFluxShade = (variables.latentHeatFluxShade < 0) ? 0 : variables.latentHeatFluxShade;

		variables.fluxTranspiration = (variables.latentHeatFluxSun + variables.latentHeatFluxShade);// --> W/m2

		if (Double.isNaN(variables.fluxTranspiration)) {
			variables.fluxTranspiration = 0;
		}

		if (airTemperature == nullValue) {
			// System.out.printf("\nAir temperature is null");
			variables.fluxTranspiration = nullValue;
		}

		/*
		 * System.out.println("variables.leafTemperatureSun is  = "+
		 * variables.leafTemperatureSun);
		 * System.out.println("variables.netLongWaveRadiationSun  is  = "+
		 * variables.netLongWaveRadiationSun );
		 */

		return variables.fluxTranspiration;

	}

}