/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geoframe.blogspot.geoet.priestleytaylor;

//import static java.lang.Math.pow;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map.Entry;
//import java.util.Set;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
//import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;
import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.inout.*;
//import org.jgrasstools.gears.libs.modules.JGTConstants;
//import org.jgrasstools.gears.libs.modules.JGTModel;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormatter;
//import com.vividsolutions.jts.geom.Coordinate;

@Description("Calculate evapotraspiration based on the Priestley Taylor model")
@Author(name = "Giuseppe Formetta, Silvia Franceschi, Andrea Antonello & Concetta D'Amato", contact = "maryban@hotmail.it, concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptetp")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")

public class PriestleyTaylorActualETSolverMain{

	//@Description("Switch that defines if it is hourly.")
	//@In
	//public boolean doHourly;

	//@Description("The mean hourly air temperature.")
    //@In
    //@Unit("C")
    //public HashMap<Integer, double[]> inAirTemperature;

    //@Description("The temperature default value in case of missing data.")
    //@In
    //@Unit("C")
    //public double defaultAirTemperature = 15.0;

	@Description("The alpha parameter.")
	@In
	@Unit("-")
	public double alpha;

	@Description("The coefficient for the soil heat flux during daylight")
	@In
	public double soilFluxParameterDay;

	@Description("The coefficient for the soil heat flux during nighttime")
	@In
	public double soilFluxParameterNight;
	
	//@Description("The net Radiation at the grass surface in W/m2 for the current hour.")
    //@In
    //@Unit("MJ m-2 hour-1")
    //public HashMap<Integer, double[]> inNetRadiation;

    //@Description("The net Radiation default value in case of missing data.")
    //@In
    //@Unit("MJ m-2 hour-1")
    //public double defaultNetRadiation = 2.0;

    //@Description("The hourly net Radiation default value in case of missing data.")
    //@In
    //@Unit("Watt m-2")
    //public double defaultHourlyNetradiation = 0.0;

    //@Description("The atmospheric pressure in kPa.")
    //@In
    //@Unit("KPa")
    //public HashMap<Integer, double[]> inAtmosphericPressure;

    //@Description("The pressure default value in case of missing data.")
    //@In
    //@Unit("KPa")
    //public double defaultAtmosphericPressure;
	
    //@Description("The soilflux.")
    //@In
    //@Unit("W m-2")
    //public HashMap<Integer, double[]> inSoilFlux;
    
	@Description("The soilflux default value in case of missing data.")
	@In
	@Unit("W m-2")
	public double defaultSoilFlux = 0.0;

	//@Description("The mean hourly air temperature.")
	//@In
	//public String tStartDate;
	
	//double latentHeatEvaporation = 2.45*pow(10,6);

    double nullValue = -9999.0;
    
    @Description("stress factor")
	@In 
	@Unit("-")
	public double stressFactor;

  
	
    //@Description("the linked HashMap with the coordinate of the stations")
	//LinkedHashMap<Integer, Coordinate> stationCoordinates;

	//@Description("The latent heat.")
	//@Unit("W/m2")
	//@Out
	//public HashMap<Integer, double[]> outLatentHeatPt;
	
	@Description("The evapotranspiration.")
	@Unit("mm time-1")
	@Out
	public double evapoTranspirationPT ;
	
	//@Description("The time step of the simulation.")
	//@In
	//public int temporalStep;

	//private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	int step;
	//public int time;

	private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	

	@Execute
	public void process() throws Exception {
		
		System.out.printf("\n\nStart PriestleyTaylorActualETSolverMain");
		
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		
		//if (doHourly == true) {time =temporalStep*60;} 
		//else {time = temporalStep;}
		
		//time =temporalStep*60;
		
		//DateTime startDateTime = formatter.parseDateTime(tStartDate);
		//DateTime date=(doHourly==false)?startDateTime.plusDays(step).plusHours(12):startDateTime.plusMinutes(temporalStep*step);

		//DateTime date=startDateTime.plusMinutes(temporalStep*step);
		
		
		//outLatentHeatPt = new HashMap<Integer, double[]>();
		//outEvapotranspirationPt = new HashMap<Integer, double[]>();

		//Set<Entry<Integer, double[]>> entrySet = inAirTemperature.entrySet();
		//for( Entry<Integer, double[]> entry : entrySet ) {
            //Integer basinId = entry.getKey();

		input.airTemperatureC = input.airTemperature - 273.15;
		
            
		//double airTemperature = inAirTemperature.get(basinId)[0];
		//if (airTemperature == (nullValue)) {airTemperature = defaultAirTemperature;}		
			
		//double netRadiation = inNetRadiation.get(basinId)[0];
		//if (netRadiation == (nullValue)) {netRadiation = defaultNetRadiation;}
		//netRadiation = netRadiation * 86400/1E6;
				
		//double soilFlux = defaultSoilFlux;
		//if (inSoilFlux != null){soilFlux = inSoilFlux.get(basinId)[0];}
		//if (soilFlux == nullValue) {soilFlux = defaultSoilFlux;}
		//soilFlux = soilFlux * 86400/1E6;			
						
		//double atmosphericPressure = defaultAtmosphericPressure;
		//if (inAtmosphericPressure != null){atmosphericPressure = inAtmosphericPressure.get(basinId)[0]/1000;}
		//if (atmosphericPressure == (nullValue/1000)) {atmosphericPressure = defaultAtmosphericPressure;}

		//DateTime date = input.date;
		
		int hourOfDay = variables.date.getHourOfDay();
		boolean isLigth = false;
		if (hourOfDay > 6 && hourOfDay < 18) {isLigth = true;}
		
		double soilFluxparameter;
		if (isLigth == true) {soilFluxparameter = soilFluxParameterDay;}
		
		else {soilFluxparameter = soilFluxParameterNight;}
	    
		if (input.soilFlux == defaultSoilFlux) {input.soilFlux = soilFluxparameter * input.netRadiation;}
	
	    ETPriestleyTaylor PT = new ETPriestleyTaylor();
	    PT.setNumber(alpha, input.airTemperatureC, input.atmosphericPressure, input.netRadiation, input.soilFlux);
	   
	    
	    variables.fluxEvapoTranspirationPT = (input.netRadiation<0)?0:PT.doET()* stressFactor ;
	    variables.fluxEvapoTranspirationPT =(variables.fluxEvapoTranspirationPT<0)?0:variables.fluxEvapoTranspirationPT;
	    
		variables.evapoTranspirationPT = variables.fluxEvapoTranspirationPT * (input.time/parameters.latentHeatEvaporation);
	    variables.evapoTranspirationPT =(variables.evapoTranspirationPT<0)?0:variables.evapoTranspirationPT;
		
	    System.out.println("\nflux of evapotranspiration  = "+variables.fluxEvapoTranspirationPT);
	    System.out.println("\nevapotranspiration  = "+variables.evapoTranspirationPT);
	    
	    evapoTranspirationPT = variables.evapoTranspirationPT;
	    //outEvapotranspirationPt.put((Integer)  basinId, new double[]{petp * time / 86400});
	    //outLatentHeatPt.put((Integer)  basinId, new double[]{petp * latentHeatEvaporation / 86400});
	    System.out.printf("\n\nEnd PriestleyTaylorActualETSolverMain");	
			//step++;
		}
}









