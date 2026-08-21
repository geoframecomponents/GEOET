package org.geoframe.geoet.core.penmanmonteithfao;

//import static java.lang.Math.pow;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import org.jgrasstools.gears.libs.modules.JGTConstants;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormatter;
//
//import com.vividsolutions.jts.geom.Coordinate;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;

@Description("Calculates evapotranspiration at hourly timestep using FAO Penman-Monteith equation")
@Author(name = "Concetta D'Amato, Michele Bottazzi, Giuseppe Formetta, Marialaura Bancheri, Silvia Franceschi Andrea Antonello and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptet")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class PenmanMonteithFAOModel {

	/**
	 * FAO-56 Penman-Monteith reference evapotranspiration equation (Allen et
	 * al. 1998).
	 *
	 * @param Cp                  bulk aerodynamic resistance coefficient, 900
	 *                            for a daily timestep, 37 for hourly (this
	 *                            method substitutes 37 itself whenever
	 *                            {@code timestep} is exactly one hour,
	 *                            regardless of what's passed in)
	 * @param Cd                  bulk surface resistance coefficient, 0.34
	 * @param atmosphericPressure Pa
	 * @param airTemperatureC     °C
	 * @param relativeHumidity    %
	 * @param soilFlux            soil heat flux density, W m-2
	 * @param timestep            timestep length, s
	 * @param windAtZ             wind speed at the reference height used by
	 *                            the surface resistance term, m s-1
	 * @param radiation           net radiation, W m-2
	 * @return evapotranspiration, mm per timestep
	 */
	public static double computeEvapotranspirationDepth(double Cp, double Cd, double atmosphericPressure,
			double airTemperatureC, double relativeHumidity, double soilFlux, int timestep, double windAtZ,
			double radiation) {

		double atmosphericPressureKPa = atmosphericPressure / 1000;
		double netRadiation = radiation * timestep / 1E6;
		double soilHeatFlux = soilFlux * timestep / 1E6;

		double cp = (timestep == 3600) ? 37 : Cp;

		double denDelta = Math.pow((airTemperatureC + 237.3), 2);
		double expDelta = (17.27 * airTemperatureC) / (airTemperatureC + 237.3);
		double numDelta = 4098 * (0.6108 * Math.exp(expDelta));
		double delta = numDelta / denDelta;

		// Computation of Psicrometric constant [kPa °C-1]
		double psychrometricConstant = 0.665 * 0.001 * atmosphericPressureKPa;

		// Computation of mean saturation vapour pressure [kPa]
		double saturationVaporPressure = 0.6108 * Math.exp(expDelta);

		// Computation of average hourly actual vapour pressure [kPa]
		double vaporPressure = saturationVaporPressure * relativeHumidity / 100;

		// Computation of ET [mm time-1]
		double num = 0.408 * delta * (netRadiation - soilHeatFlux)
				+ (cp * psychrometricConstant * windAtZ * (saturationVaporPressure - vaporPressure))
						/ (airTemperatureC + 273);
		double den = delta + psychrometricConstant * (1 + Cd * windAtZ);
		double result = (num / den);
		result = (result < 0) ? 0 : result;

		if (timestep != 86400 && timestep != 3600) {
			result = result * timestep / 86400;
		}

		return result;

	}
}
