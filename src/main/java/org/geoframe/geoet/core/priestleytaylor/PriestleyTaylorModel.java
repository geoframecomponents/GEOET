package org.geoframe.geoet.core.priestleytaylor;

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
//import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
//import oms3.annotations.Out;
import oms3.annotations.Status;

@Author(name = "Concetta D'Amato, Michele Bottazzi, Giuseppe Formetta, Marialaura Bancheri, Silvia Franceschi Andrea Antonello and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptet")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class PriestleyTaylorModel {

	public static double doET(Parameters parameters, InputTimeSeries input, double radiation) {
		double atmosphericPressure = input.atmosphericPressure / 1000;
		// Computation of Delta [kPa °C-1]
		double denDelta = Math.pow((input.airTemperatureC + 237.3), 2);
		double expDelta = (17.27 * input.airTemperatureC) / (input.airTemperatureC + 237.3);
		double numDelta = 4098 * (0.6108 * Math.exp(expDelta));
		double delta = numDelta / denDelta;
		// Computation of Psicrometric constant [kPa °C-1]
		double psychrometricConstant = 0.665 * 0.001 * atmosphericPressure;
		// Computation of Evapotranspiration [W m-2]
		double result = ((parameters.alpha) * delta * (radiation - input.soilFlux)) / (psychrometricConstant + delta);
		return result; // -----> [W m-2]
	}
}
