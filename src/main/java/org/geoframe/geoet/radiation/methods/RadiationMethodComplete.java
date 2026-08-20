package org.geoframe.geoet.radiation.methods;

import static java.lang.Math.pow;

import org.geoframe.geoet.data.Leaf;
import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.inout.InputTimeSeries;

import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.exp;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class RadiationMethodComplete {

	public static double computeAbsorbedRadiationSunlit(Parameters parameters, ProblemQuantities variables,
			double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,
			double shortWaveRadiationDiffuse) {

		// Ryu et all 2011

		double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle; // kb

		double scatteredExtinctionCoefficient = 0.46 / solarElevationAngle; // k'Pb [de Pury and Farquhar, 1997]

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

		double absordebRadiationSunlit = directAbsorbedRadiation + diffuseAbsorbedRadiation + scatteredAbsorbedRadiation
				+ variables.directAbsorbedRadiationReflectedSoilSunlit;

		return absordebRadiationSunlit;
	}

	public static double computeAbsorbedRadiationShadow(Parameters parameters, ProblemQuantities variables,
			double leafAreaIndex, double solarElevationAngle, double shortWaveRadiationDirect,
			double shortWaveRadiationDiffuse) {

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

		double absordebRadiationShadow = scatteredAbsorbedRadiationShadow + diffuseAbsorbedRadiationShadow
				+ variables.directAbsorbedRadiationReflectedSoilShadow;

		return absordebRadiationShadow;
	}

	public static double computeSunlitLeafAreaIndex(String typeOfCanopy, double leafAreaIndex,
			double solarElevationAngle) {

		if ("grassland".equals(typeOfCanopy)) {
			return leafAreaIndex;
		}

		else {
			double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle;
			double sunlitLeafAreaIndex = (1 - exp(-directExtinctionCoefficientInCanopy * leafAreaIndex))
					/ directExtinctionCoefficientInCanopy;
			return sunlitLeafAreaIndex;
		}
	}

	public static void computeAbsorbedRadiationReflectedSoil(Parameters parameters, ProblemQuantities variables,
			String typeOfCanopy, double leafAreaIndex, double shortWaveRadiationDirect,
			double shortWaveRadiationDiffuse, double directExtinctionCoefficientInCanopy) {

		// Ryu et all 2011

		double canopyReflectionCoefficientBeam = 1
				- exp((-2 * 0.041 * directExtinctionCoefficientInCanopy) / (1 + directExtinctionCoefficientInCanopy)); // ρcbP

		// Ryu et all 2011 eq. 8
		if ("grassland".equals(typeOfCanopy)) {
			parameters.soilReflectance = 0.25;
		} // Derived from [Sellers et al., 1996] except for WSA, SAV, BSV and OSH [Asner
			// et al., 1998; Roberts et al., 1993].

		variables.directAbsorbedRadiationReflectedSoilSunlit = ((1 - canopyReflectionCoefficientBeam)
				* shortWaveRadiationDirect
				+ (1 - parameters.canopyReflectionCoefficientDiffuse) * shortWaveRadiationDiffuse
				- (variables.shortwaveCanopySun + variables.shortwaveCanopyShade)) * parameters.soilReflectance
				* exp(-parameters.diffuseExtinctionCoefficient * leafAreaIndex);

		// Ryu et all 2011 eq. 9
		variables.directAbsorbedRadiationReflectedSoilShadow = ((1 - canopyReflectionCoefficientBeam)
				* shortWaveRadiationDirect
				+ (1 - parameters.canopyReflectionCoefficientDiffuse) * shortWaveRadiationDiffuse
				- (variables.shortwaveCanopySun + variables.shortwaveCanopyShade)) * parameters.soilReflectance
				* (1 - exp(-parameters.diffuseExtinctionCoefficient * leafAreaIndex));
	}

	public static double computeLeafRadiativeFeedback(Parameters parameters, ProblemQuantities variables,
			Leaf leafparameters, double airTemperature, double leafTemperature, double canopyArea) {

		double leafRadiativeFeedback = leafparameters.leafSide * canopyArea * (leafparameters.longWaveEmittance
				* parameters.stefanBoltzmannConstant * pow(airTemperature, 3) * leafTemperature);
		return leafRadiativeFeedback;
	}

	public static void computeAirEmissivity(ProblemQuantities variables, InputTimeSeries input) {
		// Compute the emissivity of air according to Prata 1996
		// Eq. 11
		variables.precipitableWater = 4650 * variables.saturationVaporPressure / input.airTemperature;

		variables.airEmissivity = (1
				- (1 + variables.precipitableWater) * exp(-(pow((1.2 + 3 * variables.precipitableWater), 0.5))));
	}

	public static void computeRadiativeConductance(Parameters parameters, ProblemQuantities variables, InputTimeSeries input) {
		// Compute the radiative conductance g_r [kg m-2 s-1]
		// Table A1
		variables.radiativeConductance = (4 * parameters.leafEmissivity * parameters.stefanBoltzmannConstant
				* pow(input.airTemperature, 3)) / parameters.airSpecificHeat;
	}

	/**
	 * Compute the absorbed longwave radiation in the canopy according to Ryu et al.
	 * 2011. Returns the direct extinction coefficient in the canopy.
	 * 
	 * <p>Needed in {@link #computeAbsorbedRadiationReflectedSoil(Parameters, ProblemQuantities, String, double, double, double, double)}
	 * 
	 * @param parameters
	 * @param variables
	 * @param input
	 * @param leafAreaIndex
	 * @param solarElevationAngle
	 * @return
	 */
	public static double computeAbsorbedLongwaveRadiation(Parameters parameters, ProblemQuantities variables,
			InputTimeSeries input, double leafAreaIndex, double solarElevationAngle) {

		// Ryu et all 2011
		double directExtinctionCoefficientInCanopy = 0.5 / solarElevationAngle; // kb

		// Ryu et all 2011 eq. 20
		variables.absorbedLongwaveRadiationSunlit = -parameters.extinctionCoefficientLongRadiation
				* parameters.stefanBoltzmannConstant * pow(input.airTemperature, 4)
				* (parameters.leafEmissivity * (1 - variables.airEmissivity)
						* (1 - exp(
								-(directExtinctionCoefficientInCanopy + parameters.extinctionCoefficientLongRadiation)
										* leafAreaIndex))
						/ (directExtinctionCoefficientInCanopy + parameters.extinctionCoefficientLongRadiation)
						+ (1 - parameters.soilEmissivity) * (parameters.leafEmissivity - variables.airEmissivity)
								* (1 - exp(-2 * parameters.extinctionCoefficientLongRadiation * leafAreaIndex))
								/ (2 * parameters.extinctionCoefficientLongRadiation)
								* (1 - exp(-(directExtinctionCoefficientInCanopy
										- parameters.extinctionCoefficientLongRadiation) * leafAreaIndex))
								/ (directExtinctionCoefficientInCanopy - parameters.extinctionCoefficientLongRadiation))
				- parameters.airSpecificHeat * variables.radiativeConductance
						* (variables.leafTemperatureSun - input.airTemperature);

		// Ryu et all 2011 eq. 21
		variables.absorbedLongwaveRadiationShadow = -parameters.extinctionCoefficientLongRadiation
				* parameters.stefanBoltzmannConstant * pow(input.airTemperature, 4)
				* (parameters.leafEmissivity * (1 - variables.airEmissivity)
						* (1 - exp(-parameters.extinctionCoefficientLongRadiation * leafAreaIndex))
						/ parameters.extinctionCoefficientLongRadiation
						- (1 - parameters.soilEmissivity) * (parameters.leafEmissivity - variables.airEmissivity)
								* exp(-parameters.extinctionCoefficientLongRadiation * leafAreaIndex)
								* (1 - exp(-parameters.extinctionCoefficientLongRadiation * leafAreaIndex))
								/ parameters.extinctionCoefficientLongRadiation)
				- variables.absorbedLongwaveRadiationSunlit
				- parameters.airSpecificHeat * variables.radiativeConductance
						* (variables.leafTemperatureSun - input.airTemperature)
				- parameters.airSpecificHeat * variables.radiativeConductance
						* (variables.leafTemperatureShade - input.airTemperature);
		return directExtinctionCoefficientInCanopy;
	}

	public static void computeLongRadiationFromSoil(Parameters parameters, ProblemQuantities variables,
			InputTimeSeries input) {

		variables.soilTemperature = input.airTemperature;
		variables.longRadiationFromSoil = parameters.soilEmissivity * parameters.stefanBoltzmannConstant
				* pow(variables.soilTemperature, 4);
	}

	public static void computeLongRadiationFromShadeLeaf(Parameters parameters, ProblemQuantities variables,
			InputTimeSeries input) {

		variables.longRadiationFromShadeLeaf = parameters.leafEmissivity * parameters.stefanBoltzmannConstant
				* pow(variables.leafTemperatureShade, 4);
	}

	public static void computeIncidentRadiation(Parameters parameters, ProblemQuantities variables,
			InputTimeSeries input) {
		variables.NewincidentSolarRadiationSoil = input.shortWaveRadiationDirect + input.shortWaveRadiationDiffuse
				+ input.longWaveRadiation - variables.shortwaveCanopySun - variables.shortwaveCanopyShade
				- variables.absorbedLongwaveRadiationSunlit - variables.absorbedLongwaveRadiationShadow
				- variables.longRadiationFromSoil + variables.longRadiationFromShadeLeaf;
	}

}
