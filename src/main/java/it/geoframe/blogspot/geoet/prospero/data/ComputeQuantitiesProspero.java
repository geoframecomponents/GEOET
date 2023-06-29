/*
 * GNU GPL v3 License
 *
 * Copyright 2019 Niccolo` Tubini
 *
 * This program is free software: you can redistribute it and/or modify
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

package it.geoframe.blogspot.geoet.prospero.data;
import org.joda.time.DateTime;
import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.data.WindProfile;
import it.geoframe.blogspot.geoet.prospero.methods.*;
import it.geoframe.blogspot.geoet.radiation.methods.RadiationMethod;
import oms3.annotations.Author;
import oms3.annotations.Bibliography;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Keywords;
import oms3.annotations.License;

@Description("This class compute some of the quantities to solve the Prospero model.")
@Documentation("")
@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("")
@Bibliography("")
@License("General Public License Version 3 (GPLv3)")


public class ComputeQuantitiesProspero {
	
	private Leaf leafparameters;
	private Parameters parameters;
	private ProblemQuantities variables;
	SensibleHeatMethods sensibleHeat 	= new SensibleHeatMethods();
	LatentHeatMethods latentHeat 		= new LatentHeatMethods();
	PressureMethods pressure 			= new PressureMethods(); 
	RadiationMethod radiationMethods 	= new RadiationMethod();
	SolarGeometry solarGeometry 		= new SolarGeometry();
	
	
	public void computeQuantitiesProspero(double windVelocity, double canopyHeight, double airTemperature, double relativeHumidity, double atmosphericPressure, DateTime date, double latitude, double longitude, 
				double time, double leafAreaIndex, String typeOfCanopy, double shortWaveRadiationDirect, double shortWaveRadiationDiffuse, double netRadiation) {
		
		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		
		
		////////// WIND
		WindProfile windVelocityProfile = new WindProfile();
		variables.windInCanopy = windVelocityProfile.computeWindProfile(windVelocity, canopyHeight);
		variables.windSoil = windVelocityProfile.computeWindProfile(windVelocity, 0.2);

		////////// Compute the saturation pressure
		variables.saturationVaporPressure = pressure.computeSaturationVaporPressure(airTemperature, parameters.waterMolarMass, parameters.latentHeatEvaporation, parameters.molarGasConstant);			
		
		////////// Compute the actual vapour pressure and the vapour pressure deficit
		variables.vaporPressure = pressure.computeVaporPressure(relativeHumidity, variables.saturationVaporPressure);	
		variables.vaporPressureDew = pressure.computeVapourPressureDewPoint(airTemperature);		
		variables.vapourPressureDeficit = pressure.computeVapourPressureDeficit(variables.vaporPressure, variables.vaporPressureDew);
		
		////////// Compute the delta
		variables.delta = pressure.computeDelta(airTemperature, parameters.waterMolarMass, parameters.latentHeatEvaporation, parameters.molarGasConstant);			
		
		////////// Compute the convective transfer coefficient - hc
		variables.convectiveTransferCoefficient = sensibleHeat.computeConvectiveTransferCoefficient(airTemperature, variables.windInCanopy, leafparameters.leafLength, parameters.criticalReynoldsNumber, parameters.prandtlNumber);
		
		////////// Compute the sensible transfer coefficient - cH
		variables.sensibleHeatTransferCoefficient = sensibleHeat.computeSensibleHeatTransferCoefficient(variables.convectiveTransferCoefficient, leafparameters.leafSide);
		
		////////// Compute the latent transfer coefficient - cE
		variables.latentHeatTransferCoefficient = latentHeat.computeLatentHeatTransferCoefficient(airTemperature, atmosphericPressure, leafparameters.leafStomaSide, variables.convectiveTransferCoefficient, parameters.airSpecificHeat,
				parameters.airDensity, parameters.molarGasConstant, parameters.molarVolume, parameters.waterMolarMass, parameters.latentHeatEvaporation, leafparameters.poreDensity, leafparameters.poreArea, leafparameters.poreDepth, leafparameters.poreRadius);			

		
		// RADIATION SUN
		variables.solarElevationAngle = solarGeometry.getSolarElevationAngle(date, latitude,longitude,time);
		variables.shortwaveCanopySun = radiationMethods.computeAbsorbedRadiationSunlit(leafAreaIndex, variables.solarElevationAngle, shortWaveRadiationDirect*2.1, shortWaveRadiationDiffuse*2.1);
		
		if (variables.shortwaveCanopySun == 0 && shortWaveRadiationDirect == 0 && shortWaveRadiationDiffuse == 0 ) {
			variables.shortwaveCanopySun=0;}
		else { 
			variables.radFactorSun = (shortWaveRadiationDirect*2.1 + shortWaveRadiationDiffuse*2.1)/ variables.shortwaveCanopySun;
			variables.shortwaveCanopySun = (shortWaveRadiationDirect+shortWaveRadiationDiffuse)/variables.radFactorSun;
		}
		// Compute the area in sunlight
		variables.areaCanopySun = radiationMethods.computeSunlitLeafAreaIndex(typeOfCanopy,leafAreaIndex, variables.solarElevationAngle);

		
		
		

		// RADIATION SHADOW
		variables.shortwaveCanopyShade = radiationMethods.computeAbsorbedRadiationShadow(leafAreaIndex, variables.solarElevationAngle, shortWaveRadiationDirect*2.1, shortWaveRadiationDiffuse*2.1);
		
		if (variables.shortwaveCanopyShade == 0 && shortWaveRadiationDirect == 0 && shortWaveRadiationDiffuse == 0 ) {
			variables.shortwaveCanopyShade=0;}
		
		else { 
			variables.radFactorShade = (shortWaveRadiationDirect*2.1 + shortWaveRadiationDiffuse*2.1)/ variables.shortwaveCanopyShade;
			variables.shortwaveCanopyShade = (shortWaveRadiationDirect+shortWaveRadiationDiffuse)/variables.radFactorShade;
			}
		
		
		
		if (variables.solarElevationAngle <0) {
			variables.shortwaveCanopySun=0;
			variables.shortwaveCanopyShade=0;
			variables.areaCanopySun=0;
		 }
		
		
		
		
		
		// Compute the area in shadow
		variables.areaCanopyShade = leafAreaIndex - variables.areaCanopySun;				
		variables.netLong = shortWaveRadiationDirect-netRadiation; 
		variables.netLong=(variables.netLong<0)?0:variables.netLong;
		
		variables.incidentSolarRadiationSoil = shortWaveRadiationDirect + shortWaveRadiationDiffuse - variables.shortwaveCanopySun - variables.shortwaveCanopyShade-variables.netLong;
		variables.incidentSolarRadiationSoil=(variables.incidentSolarRadiationSoil<0)?0:variables.incidentSolarRadiationSoil;
		
		if (variables.solarElevationAngle <0) {
			variables.incidentSolarRadiationSoil=0;
		 }
		
						
	}
	
}