package org.geoframe.geoet.core.priestleytaylor;

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

	/**
	 * Priestley-Taylor evapotranspiration equation. This formulation yields the flux
	 * directly.
	 *
	 * @param alpha               Priestley-Taylor coefficient
	 * @param atmosphericPressure Pa
	 * @param airTemperatureC     °C
	 * @param soilFlux            soil heat flux density, W m-2
	 * @param radiation           net radiation, W m-2
	 * @return evapotranspiration flux, W m-2
	 */
	public static double computeEvapotranspirationFlux(double alpha, double atmosphericPressure,
			double airTemperatureC, double soilFlux, double radiation) {
		double atmosphericPressureKPa = atmosphericPressure / 1000;
		// Computation of Delta [kPa °C-1]
		double denDelta = Math.pow((airTemperatureC + 237.3), 2);
		double expDelta = (17.27 * airTemperatureC) / (airTemperatureC + 237.3);
		double numDelta = 4098 * (0.6108 * Math.exp(expDelta));
		double delta = numDelta / denDelta;
		// Computation of Psicrometric constant [kPa °C-1]
		double psychrometricConstant = 0.665 * 0.001 * atmosphericPressureKPa;
		// Computation of Evapotranspiration [W m-2]
		double result = (alpha * delta * (radiation - soilFlux)) / (psychrometricConstant + delta);
		return result; // -----> [W m-2]
	}
}
