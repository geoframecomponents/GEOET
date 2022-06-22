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
package it.geoframe.blogspot.geoet.penmanmonteithfao;

import oms3.annotations.Author;
//import static java.lang.Math.pow;
//import java.util.HashMap;
//import java.util.Map.Entry;
//import java.util.Set;
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
//import org.jgrasstools.gears.libs.modules.JGTConstants;
//import org.jgrasstools.gears.libs.modules.JGTModel;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormatter;
import it.geoframe.blogspot.geoet.data.*;
//import it.geoframe.blogspot.geoet.prospero.methods.*;
import it.geoframe.blogspot.geoet.inout.*;


@Description("Calculates potential evapotranspiration at any timestep using FAO Penman-Monteith equation")
@Author(name = "Concetta D'Amato, Michele Bottazzi", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("ptet")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")

public class PenmanMonteithFAOPotentialETSolverMain {

    //@Description("The net Radiation at the grass surface in W/m2 for the current hour.")
	//@In
	//@Unit("MJ m-2 hour-1")
	//public HashMap<Integer, double[]> inNetRadiation;

	//@Description("The net Radiation default value in case of missing data.")
	//@In
	//@Unit("MJ m-2 hour-1")
	//public double defaultNetRadiation = 2.0;

	//@Description("The average hourly wind speed.")
	//@In
	//@Unit("m s-1")
	//public HashMap<Integer, double[]> inWindVelocity;

	//@Description("The wind default value in case of missing data.")
	//@In
	//@Unit("m s-1")
	//public double defaultWindVelocity = 0.5;

	//@Description("The mean hourly air temperature.")
	//@In
	//@Unit("C")
	//public HashMap<Integer, double[]> inAirTemperature;

	//@Description("The temperature default value in case of missing data.")
	// @In
	////@Unit("C")
	//public double defaultAirTemperature = 15.0;

	//@Description("The average air hourly relative humidity.")
	//@In
	//@Unit("%")
	//public HashMap<Integer, double[]> inRelativeHumidity;

	//@Description("The humidity default value in case of missing data.")
	//@In
	//@Unit("%")
	//public double defaultRelativeHumidity = 70.0;

	//@Description("The atmospheric pressure in kPa.")
	//@In
	//@Unit("KPa")
	//public HashMap<Integer, double[]> inAtmosphericPressure;

	// @Description("The pressure default value in case of missing data.")
	//@In
	//@Unit("KPa")
	//public double defaultAtmosphericPressure;
    
	//@Description("The soilflux.")
	//@In
	//@Unit("W m-2")
	//public HashMap<Integer, double[]> inSoilFlux;
    
	//@Description("The soilflux default value in case of missing data.")
	//@In
	//@Unit("W m-2")
	//public double defaultSoilFlux = 0.0;

	//@Description("The reference evapotranspiration.")
	//@Unit("mm hour-1")
	//@Out
	// public HashMap<Integer, double[]> outEvapotranspirationFao;
    
	//@Description("The latent heat.")
	//@Unit("W m-2")
	//@Out
	//public HashMap<Integer, double[]> outLatentHeatFao;
        
	////@Description("Switch that defines if it is hourly.")
	//@In
	//public boolean doHourly;
	
	//@Description("The mean hourly air temperature.")
	//@In
	//public String tStartDate;
	
	//@Description("The first day of the simulation.")
	//@In
	//public int temporalStep;
	
	@Description("The soilflux default value in case of missing data.")
	@In
	@Unit("W m-2")
	public double defaultSoilFlux = 0.0;
	
	@Description("The coefficient for the soil heat flux during daylight")
	@In
	public double soilFluxParameterDay;

	@Description("The coefficient for the soil heat flux during nighttime")
	@In
	public double soilFluxParameterNight;
	
	@Out 
	public boolean  doProcessOut = false;
	
	@In
	public boolean  doProcess;
	
	
	int step;
	//public int time;
	
	@Description("Height of the canopy.")
	@Unit("[m]")
	@In
	public double canopyHeight;
	
    double nullValue = -9999.0;
    //double latentHeatEvaporation = 2.45*pow(10,6);

    //private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

    private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	
    @Execute
    public void process() throws Exception {
    	
    	parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
    	
		input.airTemperatureC = input.airTemperature - 273.15;
		
		//netRadiation = netRadiation * 86400/1E6;
		
		//atmosphericPressure == (nullValue/1000
				
		//soilFlux = soilFlux * 86400/1E6;
		
		variables.hourOfDay = variables.date.getHourOfDay();
		variables.isLigth = false;
		if (variables.hourOfDay > 6 && variables.hourOfDay < 18) {variables.isLigth = true;}
		
		if (variables.isLigth == true) {variables.soilFluxparameter = soilFluxParameterDay;}
		else {variables.soilFluxparameter = soilFluxParameterNight;}
		    
		if (input.soilFlux == defaultSoilFlux) {input.soilFlux = variables.soilFluxparameter * input.netRadiation;}
    	
    	//outEvapotranspirationFao = new HashMap<Integer, double[]>();
    	//outLatentHeatFao = new HashMap<Integer, double[]>();
        
        //if (doHourly == true) {
		//	time =temporalStep*60;

		//	} else {
		//	time = 86400;
		//	}
       
		//DateTime startDateTime = formatter.parseDateTime(tStartDate);
		//DateTime date=(doHourly==false)?startDateTime.plusDays(step).plusHours(12):startDateTime.plusMinutes(temporalStep*step);
        //Set<Entry<Integer, double[]>> entrySet = inAirTemperature.entrySet();
		//for( Entry<Integer, double[]> entry : entrySet ) {
         //   Integer basinId = entry.getKey();
/*
            double airTemperature = inAirTemperature.get(basinId)[0];
			if (airTemperature == (nullValue)) {airTemperature = defaultAirTemperature;}		
			  	
			double netRadiation = inNetRadiation.get(basinId)[0];
			if (netRadiation == (nullValue)) {netRadiation = defaultNetRadiation;}
			netRadiation = netRadiation * 86400/1E6;

			double windVelocity = inWindVelocity.get(basinId)[0];
			if (windVelocity == (nullValue)) {windVelocity = defaultWindVelocity;}		
			//double windSpeedH = (windVelocity * (Math.log(67.8*canopyHeight - 5.42)))/4.87;
			
			double atmosphericPressure = inAtmosphericPressure.get(basinId)[0]/1000;
			if (atmosphericPressure == (nullValue/1000)) {atmosphericPressure = defaultAtmosphericPressure;}		

			double relativeHumidity = inRelativeHumidity.get(basinId)[0];
			if (relativeHumidity == (nullValue)) {relativeHumidity = defaultRelativeHumidity;}	
			
			double soilFlux = defaultSoilFlux;
			if (inSoilFlux != null){soilFlux = inSoilFlux.get(basinId)[0];}
			if (soilFlux == nullValue) {soilFlux = defaultSoilFlux;}
			soilFlux = soilFlux * 86400/1E6;

			//double rootZoneDepletation = 1000 * (waterFieldCapacity - soilMosture) * rootsDepth;

			//double waterStressCoefficient=(rootZoneDepletation<readilyAvailableWater)? 1:(totalAvailableWater - rootZoneDepletation) / (totalAvailableWater - readilyAvailableWater);
		/*	System.out.println("");
			System.out.println("soilMosture            "+soilMosture);
			System.out.println("totalAvailableWater    "+totalAvailableWater);
			System.out.println("rootZone               "+rootZoneDepletation);
			System.out.println("readilyAvailableWater  "+readilyAvailableWater);
			System.out.println("waterStressCoefficient "+waterStressCoefficient);	*/
			
		/*int hourOfDay = date.getHourOfDay();

			boolean islight = false;
			if (hourOfDay > 6 && hourOfDay < 18) {
				islight = true;
			}
			double soilFluxparameter;
			if (netRadiation > 0) {soilFluxparameter = 0.35;}
			else {soilFluxparameter = 0.75;}     
			
			double soilHeatFlux = (soilFlux==defaultSoilFlux)?(soilFluxparameter * netRadiation):soilFlux;			
*/
	
            
            WindProfile windAtZ = new WindProfile();
            PenmanMonteithFAOmodel FAO = new PenmanMonteithFAOmodel();
         
            
            //windSpeedWithZ.setParams(canopyHeight, input.windVelocity);
            
            variables.windAtZ = windAtZ.computeWindProfile(canopyHeight, input.windVelocity);
   

	    	
	    	FAO.setNumber(input.airTemperatureC, input.atmosphericPressure, input.netRadiation, input.relativeHumidity, input.soilFlux, variables.windAtZ);
	    	
	    	variables.evapoTranspirationPMdaily = FAO.doET(); // --> mm/day
	    	
	    	variables.evapoTranspirationPM = variables.evapoTranspirationPMdaily * input.time/86400;
	    	
	    	variables.fluxEvapoTranspirationPM = variables.evapoTranspirationPMdaily * parameters.latentHeatEvaporation / 86400; 
	    	
	    	
	    			
	    	System.out.println("\netp   "+variables.evapoTranspirationPM);
	    	System.out.println("flux etp   "+variables.fluxEvapoTranspirationPM);
            //outLatentHeatFao.put(basinId, new double[]{pmfaoetp * latentHeatEvaporation / 86400});
            //outEvapotranspirationFao.put(basinId, new double[]{pmfaoetp*time/86400});
        //}
       // step++;

    }

  
}
