package org.geoframe.geoet.core.stressfactor;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")
public class EnvironmentalStress {

	public static double computeRadiationStress(double shortWaveRadiation, double alpha, double theta) {

		double radiationStress = 1;

		// if(shortWaveRadiation <= 0) {radiationStress = 1;}
		if (shortWaveRadiation <= 0) {
			radiationStress = 0.15;
		} else {
			// double shortWaveRadiationMicroMol=(shortWaveRadiation);
			double first = (alpha * shortWaveRadiation) + 1;

			double sqr1 = Math.pow(first, 2);
			double sqr2 = -4 * theta * alpha * shortWaveRadiation;
			double sqr = sqr1 + sqr2;
			radiationStress = (1 / (2 * theta)) * (alpha * shortWaveRadiation + 1 - Math.sqrt((sqr)));

			if (Double.isNaN(radiationStress)) {
				radiationStress = 0;
			}
			if (radiationStress <= 0) {
				radiationStress = 0;
			}
			if (radiationStress >= 1) {
				radiationStress = 1;
			}
		}

		return radiationStress;
	}

	public static double computeTemperatureStress(double airTemperature, double Tl, double Th, double T0) {

		airTemperature = airTemperature - 273.15;
		double c = (Th - T0) / (T0 - Tl);
		double b = 1 / ((T0 - Tl) * Math.pow((Th - T0), c));

		double temperatureStress = b * (airTemperature - Tl) * Math.pow((Th - airTemperature), c);

		if (Double.isNaN(temperatureStress)) {
			temperatureStress = 0;
		}
		if (temperatureStress <= 0) {
			temperatureStress = 0;
		}
		if (temperatureStress >= 1) {
			temperatureStress = 1;
		}

		return temperatureStress;
	}

	// vapourPressureDeficit from ProblemQuantities
	public static double computeVapourPressureStress(double vapourPressureDeficit, double airTemperature, double VPD0) {

		double vapourPressureStress = Math.exp(-vapourPressureDeficit / VPD0);

		if (Double.isNaN(vapourPressureStress)) {
			vapourPressureStress = 0;
		}
		if (vapourPressureStress <= 0) {
			vapourPressureStress = 0;
		}
		if (vapourPressureStress >= 1) {
			vapourPressureStress = 1;
		}

		return vapourPressureStress;
	}

}
