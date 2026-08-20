package org.geoframe.geoet.core.transpiration;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class EnergyBalance {


	public static double computeEnergyBalance(double shortWaveRadiation, double residual, double longWaveRadiation,
			double latentHeatFlux, double sensibleHeatFlux) {

		double energyResidual = shortWaveRadiation - residual - longWaveRadiation - latentHeatFlux - sensibleHeatFlux;
		return energyResidual;
	}

}