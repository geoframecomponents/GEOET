package org.geoframe.geoet.core.penmanmonteithfao;

import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.InputTimeSeries;

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

	public static double doET(Parameters parameters, InputTimeSeries input, double windAtZ, double radiation) {

		double atmosphericPressure = input.atmosphericPressure / 1000;
		double netRadiation = radiation * input.time / 1E6;
		double soilHeatFlux = input.soilFlux * input.time / 1E6;

		if (input.time == 3600) {
			parameters.Cp = 37;
		}

		double denDelta = Math.pow((input.airTemperatureC + 237.3), 2);
		double expDelta = (17.27 * input.airTemperatureC) / (input.airTemperatureC + 237.3);
		double numDelta = 4098 * (0.6108 * Math.exp(expDelta));
		double delta = numDelta / denDelta;

		// Computation of Psicrometric constant [kPa °C-1]
		double psychrometricConstant = 0.665 * 0.001 * atmosphericPressure;

		// Computation of mean saturation vapour pressure [kPa]
		double saturationVaporPressure = 0.6108 * Math.exp(expDelta);

		// Computation of average hourly actual vapour pressure [kPa]
		double vaporPressure = saturationVaporPressure * input.relativeHumidity / 100;

		// Computation of ET [mm time-1]
		double num = 0.408 * delta * (netRadiation - soilHeatFlux)
				+ (parameters.Cp * psychrometricConstant * windAtZ * (saturationVaporPressure - vaporPressure))
						/ (input.airTemperatureC + 273);
		double den = delta + psychrometricConstant * (1 + parameters.Cd * windAtZ);
		double result = (num / den);
		result = (result < 0) ? 0 : result;

		if (input.time != 86400 && input.time != 3600) {
			result = result * input.time / 86400;
		}

		return result;

	}
}
