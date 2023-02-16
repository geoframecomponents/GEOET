package it.geoframe.blogspot.geoet.penmanmonteithfao;

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
public


class PenmanMonteithFAOmodel_v139{
	//private double airTemperature;
	private double atmosphericPressure;
	private double netRadiation;
	//private double relativeHumidity;
	private double soilHeatFlux;
	//private double windAtZ;
	private double denDelta;
	private double expDelta;
	private double numDelta;
	private double delta;
	private double psychrometricConstant;
	private double result;
	private double saturationVaporPressure;
	private double vaporPressure;
	private double den;
	private double num;
	
	private Parameters parameters;
	private InputTimeSeries input;

	
	/*public void setNumber(double airTemperature, double atmosphericPressure,
			double netRadiation, double relativeHumidity, 
			double soilHeatFlux, double windAtZ) {
		
		//input = InputTimeSeries.getInstance();
		
		//this.airTemperature = airTemperature;
		//this.atmosphericPressure = atmosphericPressure/1000;
		
		//this.netRadiation = netRadiation * 86400/1E6;
		
		//this.relativeHumidity = relativeHumidity;
		
		//this.soilHeatFlux = soilHeatFlux * 86400/1E6;
		
		//this.netRadiation = netRadiation * input.time/1E6;
		//this.soilHeatFlux = soilHeatFlux * input.time/1E6;
		
		//this.windAtZ = windAtZ;
		}*/

	public double doET(double windAtZ, double radiation) {	
		
		input = InputTimeSeries.getInstance();
		parameters = Parameters.getInstance();
	
		
		atmosphericPressure = input.atmosphericPressure/1000;
		netRadiation = radiation * input.time/1E6;
		soilHeatFlux = input.soilFlux * input.time/1E6;
		
		if (input.time == 3600) 
		{parameters.Cp = 37;} 
		
		denDelta = Math.pow((input.airTemperatureC + 237.3), 2);
        expDelta = (17.27 * input.airTemperatureC) / (input.airTemperatureC + 237.3);
        numDelta = 4098 * (0.6108 * Math.exp(expDelta));
        delta = numDelta / denDelta;
        
        // Computation of Psicrometric constant [kPa °C-1]
        psychrometricConstant = 0.665 * 0.001 * atmosphericPressure;
        
        // Computation of mean saturation vapour pressure [kPa]
        saturationVaporPressure = 0.6108 * Math.exp(expDelta);
        
        // Computation of average hourly actual vapour pressure [kPa]
        vaporPressure = saturationVaporPressure * input.relativeHumidity / 100;
        
        // Computation of ET [mm time-1]
        num = 0.408 * delta * (netRadiation - soilHeatFlux) + (parameters.Cp * psychrometricConstant * windAtZ * (saturationVaporPressure - vaporPressure)) / (input.airTemperatureC + 273);
        den = delta + psychrometricConstant * (1 + parameters.Cd * windAtZ);
        result = (num / den);
        result = (result <0)?0:result;
        
		if (input.time != 86400 && input.time != 3600) 
		{result = result * input.time/86400;}
		
		return result;
		
		
		/*denDelta = Math.pow((airTemperature + 237.3), 2);
        expDelta = (17.27 * airTemperature) / (airTemperature + 237.3);
        numDelta = 4098 * (0.6108 * Math.exp(expDelta));
        delta = numDelta / denDelta;
        // Computation of Psicrometric constant [kPa °C-1]
        psychrometricConstant = 0.665 * 0.001 * atmosphericPressure;
        // Computation of mean saturation vapour pressure [kPa]
        saturationVaporPressure = 0.6108 * Math.exp(expDelta);
        // Computation of average hourly actual vapour pressure [kPa]
        vaporPressure = saturationVaporPressure * relativeHumidity / 100;
        // Computation of ET [mm day-1]
        num = 0.408 * delta * (netRadiation - soilHeatFlux) + (900 * psychrometricConstant * windAtZ * (saturationVaporPressure - vaporPressure)) / (airTemperature + 273);
        den = delta + psychrometricConstant * (1 + 0.34 * windAtZ);
        result = (num / den);
        result = (result <0)?0:result;
        return result;*/

        
		}
	}
	

