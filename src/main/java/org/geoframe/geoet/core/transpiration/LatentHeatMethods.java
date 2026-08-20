package org.geoframe.geoet.core.transpiration;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.PI;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class LatentHeatMethods {


	public static double computeLatentHeatTransferCoefficient(double airTemperature, double atmosphericPressure,
			int leafStomaSide, double convectiveTransferCoefficient, double airSpecificHeat, double airDensity,
			double molarGasConstant, double molarVolume, double waterMolarMass, double latentHeatEvaporation,
			double poreDensity, double poreArea, double poreDepth, double poreRadius) {

		// Notation from Schymanski & Or, 2017

		// alpha_a
		double thermalDiffusivity = 1.32 * pow(10, -7) * airTemperature - 1.73 * pow(10, -5);
		// D_va
		double binaryDiffusionCoefficient = 1.49 * pow(10, -7) * airTemperature - 1.96 * pow(10, -5);
		// k_dv
		double ratio = binaryDiffusionCoefficient / molarVolume;
		// N_Le
		double lewisNumber = thermalDiffusivity / binaryDiffusionCoefficient;
		// r_sp
		double throatResistance = poreDepth / (poreArea * ratio * poreDensity);

		double constantTerm = 1 / (4 * poreRadius) - 1 / (PI * (1 / sqrt(poreDensity)));
		// r_vs
		double vapourResistance = constantTerm * 1 / (ratio * poreDensity);
		// g_sw,mol
		double molarStomatalConductance = 1 / (throatResistance + vapourResistance);
		// g_sw
		double stomatalConductance = molarStomatalConductance * (molarGasConstant * airTemperature) / atmosphericPressure;
		// System.out.println(stomatalConductance);
		// g_bw
		double boundaryLayerConductance = leafStomaSide * convectiveTransferCoefficient
				/ (airSpecificHeat * airDensity * pow(lewisNumber, 0.66));
		// g_tw
		double totalConductance = 1 / ((1 / stomatalConductance) + (1 / boundaryLayerConductance));
		// g_tw,mol
		double molarTotalConductance = totalConductance * 40;
		// c_E
		double latentHeatTransferCoefficient = (waterMolarMass * latentHeatEvaporation * molarTotalConductance)
				/ atmosphericPressure;
		return latentHeatTransferCoefficient;
	}

	public static double computeLatentHeatFlux(double delta, double leafTemperature, double airTemperature,
			double latentHeatTransferCoefficient, double vaporPressure, double saturationVaporPressure) {
		// Computation of the latent heat flux from leaf [J m-2 s-1]
		// double latentHeatFlux = (sensibleHeatTransferCoefficient* (delta *
		// (leafTemperature - airTemperature) + saturationVaporPressure -
		// vaporPressure))/(sensibleHeatTransferCoefficient/latentHeatTransferCoefficient);

		double latentHeatFlux = (latentHeatTransferCoefficient
				* (delta * (leafTemperature - airTemperature) + saturationVaporPressure - vaporPressure));

		return latentHeatFlux;
	}
}