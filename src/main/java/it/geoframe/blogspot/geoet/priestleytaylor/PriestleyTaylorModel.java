package it.geoframe.blogspot.geoet.priestleytaylor;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;

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
//import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
//import oms3.annotations.Out;
import oms3.annotations.Status;
//import oms3.annotations.Unit;
//import oms3.annotations.Execute;
//import oms3.annotations.override;
//import java.io.*; 
@Description("Calculate evapotraspiration based on the Priestley-Taylor model")
@Author(name = "Concetta D'Amato, Michele Bottazzi, Giuseppe Formetta, Marialaura Bancheri, Silvia Franceschi Andrea Antonello and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptet")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
//abstract
//public abstract class PriestleyTaylor implements Evapotranspiration{
class PriestleyTaylorModel{
	//private double alpha;
	//private double airTemperature;
	private double atmosphericPressure;
	//private double netRadiation;
	//private double soilHeatFlux;
	private double denDelta;
	private double expDelta;
	private double numDelta;
	private double delta;
	private double psychrometricConstant;
	private double result;
	
	private InputTimeSeries input;
	private Parameters parameters;
	
	/*public void setNumber(double alpha, double airTemperature, double atmosphericPressure,
			double netRadiation, double soilHeatFlux) {
		//this.alpha = alpha;
		//this.airTemperature=airTemperature;
		//this.atmosphericPressure=atmosphericPressure/1000;
		//this.netRadiation=netRadiation;
		//this.soilHeatFlux=soilHeatFlux;
		}*/
	
	public double doET(double radiation) {
		
		input = InputTimeSeries.getInstance();
		parameters = Parameters.getInstance();
		
		atmosphericPressure=input.atmosphericPressure/1000;
		
		// Computation of Delta [kPa °C-1]
		denDelta = Math.pow((input.airTemperatureC + 237.3), 2);
		expDelta = (17.27 * input.airTemperatureC) / (input.airTemperatureC + 237.3);
		numDelta = 4098 * (0.6108 * Math.exp(expDelta));
		delta = numDelta / denDelta;
		// Computation of Psicrometric constant [kPa °C-1]
		psychrometricConstant = 0.665 * 0.001 * atmosphericPressure;
		// Computation of Evapotranspiration  [W m-2]
		result = ((parameters.alpha) * delta * (radiation - input.soilFlux)) / (psychrometricConstant + delta);
		return result;  // -----> [W m-2]
		
	}
	}
	

