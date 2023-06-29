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

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.data.WindProfile;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import it.geoframe.blogspot.geoet.stressfactor.methods.FaoWaterStress;

@Description("Calculates evapotranspiration at any timestep using FAO Penman-Monteith equation and water stress factor according FAO formulation")
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("evapotraspiration, hydrology")
@Label("")
@Name("PenmanMonteithFAO Evapotranspiration")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")

public class PenmanMonteithFAOSolverMain {

    
	@Description("The crop coefficient.")
	@Unit("[-]")
	@In
	public double cropCoefficient=1;
	
	@Description("the water content at wilting point.")
	@Unit("[m3 m-3]")
	@In
	public double waterWiltingPoint=0;
	
	@Description("the water content at field capacity.")
	@Unit("[m3 m-3]")
	@In
	public double waterFieldCapacity=0;
	
	@Description("the rooting depth.")
	@Unit("[m]")
	@In
	public double rootsDepth;
	
	@Description("average fraction of Total Available Soil Water (TAW) that can be depleted from the root zone before moisture stress (reduction in ET) occurs [0-1].")
	@In
	public double depletionFraction=0;
	
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
	
	@In
	public boolean  doProcess3;
	
	@Out
	public boolean  doProcess4;
	
	int step;

	@Description("Height of the canopy.")
	@Unit("[m]")
	@In
	public double canopyHeight;
	
    double nullValue = -9999.0;
    
    
    WindProfile windAtZ = new WindProfile();
    PenmanMonteithFAOModel FAO = new PenmanMonteithFAOModel();
    FaoWaterStress waterStress = new FaoWaterStress();
    
    
    private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;

    @Execute
    public void process() throws Exception {
    	
    	System.out.printf("\n\nStart PenmanMonteithFAOSolverMain");
    	
    	parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
    	
		input.airTemperatureC = input.airTemperature - 273.15;
 
		variables.hourOfDay = variables.date.getHourOfDay();
		variables.isLigth = false;
		if (variables.hourOfDay > 6 && variables.hourOfDay < 18) {variables.isLigth = true;}
		
		if (variables.isLigth == true) {variables.soilFluxparameter = soilFluxParameterDay;}
		else {variables.soilFluxparameter = soilFluxParameterNight;}
		    
		if (input.soilFlux == defaultSoilFlux) {input.soilFlux = variables.soilFluxparameter * input.netRadiation;}
    	
		
	
		
        variables.windAtZ = windAtZ.computeWindProfile(input.windVelocity,canopyHeight);
    
        if (waterWiltingPoint == 0.0 && waterFieldCapacity == 0.0 && depletionFraction == 0.0) {
            variables.stressWater =1;}
        else 
        	variables.stressWater = waterStress.computeFAOWaterStress(input.soilMoisture, waterFieldCapacity, waterWiltingPoint, rootsDepth, depletionFraction);
            
            
////////////////// Chapter 2 - FAO Penman-Monteith equation 6 (https://www.fao.org/3/X0490E/x0490e06.htm#TopOfPage) //////////////////
        variables.evapoTranspirationPM = FAO.doET(variables.windAtZ, input.netRadiation) * variables.stressWater * cropCoefficient;// --> mm/time
	    	
	  	variables.fluxEvapoTranspirationPM = variables.evapoTranspirationPM * parameters.latentHeatEvaporation / input.time;

            
	    if (variables.evapoTranspirationPM < 0) {variables.evapoTranspirationPM = 0;}
	    if (variables.fluxEvapoTranspirationPM < 0) {variables.fluxEvapoTranspirationPM = 0;}
	    			
	    //System.out.println("\netp   "+variables.evapoTranspirationPM);
	    //System.out.println("flux etp   "+variables.fluxEvapoTranspirationPM);
            
	    System.out.printf("\nEnd PenmanMonteithFAOSolverMain"); 

    }

   
}
