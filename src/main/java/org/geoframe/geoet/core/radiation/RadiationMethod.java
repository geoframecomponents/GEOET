package org.geoframe.geoet.core.radiation;

import static java.lang.Math.pow;

import org.geoframe.geoet.core.data.Parameters;

import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.exp;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class RadiationMethod {

	// double diffuseExtinctionCoefficient = 0.719; //k'Pd
	// double leafScatteringCoefficient = 0.2;
	// double canopyReflectionCoefficientDiffuse = 0.036; //ρcdP

	public static double computeLongWaveRadiationBalance(double leafSide, double longWaveEmittance,
			double airTemperature, double leafTemperature, double stefanBoltzmannConstant) {
		// Compute the net long wave radiation i.e. the incoming minus outgoing [J m-2
		// s-1]
		double longWaveRadiation = 4 * leafSide * longWaveEmittance * stefanBoltzmannConstant
				* (((pow(airTemperature, 3)) * leafTemperature - (pow(airTemperature, 4))));
		return longWaveRadiation;
	}
	// QUESTO SAREBBE IL FEEDBACK RADIATIVO - la radiazione ad onda lunga incoming
	// calcolata con la Ta

	public static double computeAbsorbedRadiationSunlit(Parameters parameters, double leafAreaIndex,
			double solarElevationAngle, double shortWaveRadiationDirect, double shortWaveRadiationDiffuse) {
		// Ryu et all 2011

		double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle; // kb

		double scatteredExtinctionCoefficient = 0.46 / solarElevationAngle; // k'Pb

		double canopyReflectionCoefficientBeam = 1
				- exp((-2 * 0.041 * directExtinctionCoefficientInCanopy) / (1 + directExtinctionCoefficientInCanopy)); // ρcbP

		// Ryu et all 2011 eq.3
		double directAbsorbedRadiation = shortWaveRadiationDirect * (1 - parameters.leafScatteringCoefficient)
				* (1 - exp(-directExtinctionCoefficientInCanopy * leafAreaIndex));

		// Ryu et all 2011 eq.4
		double diffuseAbsorbedRadiation = shortWaveRadiationDiffuse
				* (1 - parameters.canopyReflectionCoefficientDiffuse)
				* (1 - exp(-(parameters.diffuseExtinctionCoefficient + directExtinctionCoefficientInCanopy)
						* leafAreaIndex))
				* (parameters.diffuseExtinctionCoefficient
						/ (parameters.diffuseExtinctionCoefficient + directExtinctionCoefficientInCanopy));

		// Ryu et all 2011 eq.5
		double scatteredAbsorbedRadiation = shortWaveRadiationDirect * ((1 - canopyReflectionCoefficientBeam)
				* (1 - exp(-(directExtinctionCoefficientInCanopy + scatteredExtinctionCoefficient) * leafAreaIndex))
				* (scatteredExtinctionCoefficient
						/ (directExtinctionCoefficientInCanopy + scatteredExtinctionCoefficient))
				- (1 - parameters.leafScatteringCoefficient)
						* (1 - exp(-2 * directExtinctionCoefficientInCanopy * leafAreaIndex)) / 2);

		double absordebRadiationSunlit = directAbsorbedRadiation + diffuseAbsorbedRadiation
				+ scatteredAbsorbedRadiation;

		return absordebRadiationSunlit;
	}

	public static double computeAbsorbedRadiationShadow(Parameters parameters, double leafAreaIndex,
			double solarElevationAngle, double shortWaveRadiationDirect, double shortWaveRadiationDiffuse) {

		double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle;
		double scatteredExtinctionCoefficient = 0.46 / solarElevationAngle;

		double canopyReflectionCoefficientBeam = 1
				- exp((-2 * 0.041 * directExtinctionCoefficientInCanopy) / (1 + directExtinctionCoefficientInCanopy));

		double diffuseAbsorbedRadiationShadow = shortWaveRadiationDiffuse * (1 - canopyReflectionCoefficientBeam) * (1
				- exp(-parameters.diffuseExtinctionCoefficient * leafAreaIndex)
				- (1 - exp(-(parameters.diffuseExtinctionCoefficient + directExtinctionCoefficientInCanopy)
						* leafAreaIndex))
						* (parameters.diffuseExtinctionCoefficient
								/ (parameters.diffuseExtinctionCoefficient + directExtinctionCoefficientInCanopy)));

		double scatteredAbsorbedRadiationShadow = shortWaveRadiationDirect * ((1 - canopyReflectionCoefficientBeam) * (1
				- exp(-scatteredExtinctionCoefficient * leafAreaIndex)
				- (1 - exp(-(scatteredExtinctionCoefficient + directExtinctionCoefficientInCanopy) * leafAreaIndex))
						* (scatteredExtinctionCoefficient
								/ (scatteredExtinctionCoefficient + directExtinctionCoefficientInCanopy)))
				- (1 - parameters.leafScatteringCoefficient)
						* (1 - exp(-directExtinctionCoefficientInCanopy * leafAreaIndex)
								- (1 - exp(-2 * directExtinctionCoefficientInCanopy * leafAreaIndex)) / 2));

		double absordebRadiationShadow = scatteredAbsorbedRadiationShadow + diffuseAbsorbedRadiationShadow;

		return absordebRadiationShadow;
	}

	public static double computeSunlitLeafAreaIndex(String typeOfCanopy, double leafAreaIndex, double solarElevationAngle) {

		if ("grassland".equals(typeOfCanopy)) {
			return leafAreaIndex;
		} else {
			double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle;
			double sunlitLeafAreaIndex = (1 - exp(-directExtinctionCoefficientInCanopy * leafAreaIndex))
					/ directExtinctionCoefficientInCanopy;
			return sunlitLeafAreaIndex;
		}
	}

}