/*
* GNU GPL v3 License
 *
 * Copyright 2019 Concetta D'Amato
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
package it.geoframe.blogspot.geoet.stressfactor.methods;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.inout.InputTimeSeries;
import oms3.annotations.Author;
import oms3.annotations.License;

/**
 * Computation of the stress factor by Medlyn et al. 2011
 * @author Concetta D'Amato
 */

@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")



public class MedlynStressFactor {
	
	private ProblemQuantities variables;
	private InputTimeSeries input;
	private double stressMedlyn;
	

	
	public double stressFactorMedlyn() {
		
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		
		stressMedlyn = 1.6*(1+input.g1/Math.sqrt(variables.vapourPressureDeficit))*(input.assimilationRate/variables.carbonDioxideLeafConcentration);
		
		
		return stressMedlyn;}


		
			

	}
