package it.geoframe.blogspot.geoet.inout;

import org.joda.time.DateTime;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;
import oms3.annotations.Unit;
@Description("")
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Reading input")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class InputTimeSeries {
	
	private static InputTimeSeries uniqueInstance;

	public static InputTimeSeries getInstance() {
		if (uniqueInstance == null) {
		uniqueInstance = new InputTimeSeries();
	}
	return uniqueInstance;
	}
	public double airTemperature;
	
	public double airTemperatureC;
	
	public double leafAreaIndex;
	
	public double rootDepth;
	
	public double canopyHeight;
	
	public double shortWaveRadiationDirect;
	
	public double shortWaveRadiationDiffuse;
	
	public double longWaveRadiation;
	
	public double netRadiation;
	
	public double windVelocity;
	
	public double atmosphericPressure;
	
	public double relativeHumidity;
	
	public double soilFlux;
	
	public double soilMoisture;
	
	public DateTime date; 
	
	//public String rootType;
	
	@Description("The elevation of the centroid.")
	@In
	@Unit("m")
	public double elevation;
	
	@Description("The latitude of the centroid.")
	@In
	@Unit("°")
	public double latitude;
	
	@Description("The longitude of the centroid.")
	@In
	@Unit("°")
	public double longitude;
	
	public int time;
	
	public int ID;
	
	@Description("z coordinate read from grid NetCDF file.")
	@Unit("m")
	public double[] z;
	
	@Description("Vector of Initial Condition for root density")
	@Unit("-")
	public double[] rootDensityIC;
	
	public double growthRateRoot;
	
	//@Description("Switch that defines if it is hourly.")
	//@In
	//public boolean doHourly = true;
}
