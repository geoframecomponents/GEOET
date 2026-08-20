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
package org.geoframe.geoet.solvers;

import java.util.ArrayList;

import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.core.stressfactor.EnvironmentalStress;
import org.geoframe.geoet.core.stressfactor.RepresentativeStressFactor;
import org.geoframe.geoet.core.stressfactor.RepresentativeStressFactorFactory;
import org.geoframe.geoet.core.stressfactor.WaterStressFactor;
import org.geoframe.geoet.core.stressfactor.WaterStressFactorFactory;
import org.hortonmachine.gears.libs.modules.HMModel;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.License;
import oms3.annotations.Out;
import oms3.annotations.Unit;

import org.geoframe.geoet.core.stressfactor.FaoWaterStress;
@Description("This class is used to compute Jarvis stress factors")
@Documentation("")
@Author(name = "Concetta D'Amato and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class JarvisStressFactorSolver extends HMModel {

	@Description("Wilting point")
	@In
	@Unit("-")
	public double[] thetaWp;

	@Description("Field capacity")
	@In
	@Unit("-")
	public double[] thetaFc;

	@Description("Water content")
	@In
	@Unit("-")
	public double[] theta;

	// @Description("Depth of the root.")
	// @In
	// @Unit("m")
	// public double etaR;

	@Description("Depth of the Evaporation layer.")
	@In
	@Unit("m")
	public double etaE;

	@Description("z coordinate read from grid NetCDF file.")
	@In
	@Unit("m")
	public double[] z;

	@Description("Depth of the root from the bottom.")
	@Unit("m")
	public double zR;

	@Description("Depth of the evaporation layer from the bottom")
	@Unit("m")
	public double zE;

	@Description("Depth of the domain")
	@In
	@Unit("m")
	public double totalDepth;

	@Description("The stressed Evapotranspiration from Schymanski model.")
	@In
	@Unit("mm/s")
	public double StressedET;

	@Description("The stressed Transpiration from Prospero model.")
	@In
	@Unit("mm/s")
	public double transpiration;

	@Description("The stressed Evaporation from Prospero model.")
	@In
	@Unit("mm/s")
	public double evaporation;

	@Description("Vector containing the length of each control volume")
	@In
	@Unit("m")
	public double[] deltaZ;

	@Description("Number of control volume for domain discetrization")
	@In
	@Unit("-")
	public int NUM_CONTROL_VOLUMES;

	@Description("ParameterID for the type of soil")
	@In
	@Unit("-")
	public int[] ID;

	@Description("Stress Factor can be evaluated in different way" + " Linear method (Jarvis) --> LinearStressFactor")
	@In
	public String stressFactorModel;

	/*
	 * @Description("Representative Stress Factor can be evaluated in different way"
	 * + " Average method --> AverageMetod" +
	 * " Weighted average method --> SizeWightedMetod")
	 * 
	 * @In public String representativeStressFactorModel;
	 */

	@Description("Representative Stress Factor can be evaluated in different way" + " Average method --> AverageMetod"
			+ " Size wighted method --> SizeWightedMetod"
			+ " Root density wighted method --> RootDensityWeightedMethod")
	@In
	public String representativeTranspirationSFModel;

	@Description("Representative Stress Factor can be evaluated in different way" + " Average method --> AverageMetod"
			+ " Size wighted method --> SizeWightedMetod")
	@In
	public String representativeEvaporationSFModel;

	@Description("The stress factor for each control volumes")
	@Out
	@Unit("-")
	public double[] g;

	@Description("Representative stress factor in the evaporation layer")
	@Out
	@Unit("-")
	public double evaporationStressWater;

	@Description("Vector of G and n, for transpiration")
	@Out
	@Unit("-")
	public double[] GnT;

	@Description("Vector of G and n, for evaporation")
	@Out
	@Unit("-")
	public double[] GnE;

	@Description("The stressed Evapotranspiration for each control volumes")
	@Out
	@Unit("mm/s")
	public double[] StressedETs;

	@Description("The stressed Transpiration for each control volumes")
	@Unit("mm/s")
	public double[] transpirations;

	@Description("The stressed Evaporation for each control volumes within the Evaporation layer")
	@Unit("mm/s")
	public double[] evaporations;

	/////////////////////////////////////////////////////////////////////////////

	@Description("Object dealing with stress factor model for each control volumes")
	WaterStressFactor stressFactor;

	@Description("Object dealing with stress factor model representative of the domain")
	// GeneralSF representativeSF;
	RepresentativeStressFactor representativeTranspirationSF;
	RepresentativeStressFactor representativeEvaporationSF;

	// @Description("Water stress")
	// @In
	// @Unit("-")
	// public double stressWater;

	@In
	public double alpha;
	@In
	public double thetaR;
	@In
	public double VPD0;

	@In
	public double T0;
	@In
	public double Tl;
	@In
	public double Th;

	@In
	public boolean useRadiationStress = true;
	@In
	public boolean useTemperatureStress = true;
	@In
	public boolean useVDPStress = true;
	@In
	public boolean useWaterStress = true;

	@In
	public double defaultStress = 1;

	@Description("It is needed to iterate on the date")
	int step;

	@Description("Stress factor for sun canopy")
	@Out
	@Unit("-")
	public double stressSun;

	@Description("Stress factor for shade canopy")
	@Out
	@Unit("-")
	public double stressShade;

	@Description("ArrayList of variable to be stored in the buffer writer")
	@Out
	public ArrayList<double[]> outputToBuffer;

	/////////////////////////////////////////////////////////////////////////////

	// FaoWaterStress faoWaterStress = new FaoWaterStress();

	public ProblemQuantities variables;
	public InputTimeSeries input;

	@Execute
	public void solve() {
		checkNull(variables, input);

		// System.out.printf("\n\nStart JarvisStressFactorSolver");
		if (step == 0) {

			NUM_CONTROL_VOLUMES = z.length;
			totalDepth = z[NUM_CONTROL_VOLUMES - 1];
			StressedETs = new double[NUM_CONTROL_VOLUMES - 1];

			stressFactor = WaterStressFactorFactory.createStressFactor(stressFactorModel, thetaWp, thetaFc, ID, z,
					deltaZ, NUM_CONTROL_VOLUMES, totalDepth);

			representativeTranspirationSF = RepresentativeStressFactorFactory.createRepresentativeStressFactor(
					representativeTranspirationSFModel, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth, variables);

			representativeEvaporationSF = RepresentativeStressFactorFactory.createRepresentativeStressFactor(
					representativeEvaporationSFModel, z, deltaZ, NUM_CONTROL_VOLUMES, totalDepth, variables);

			// zR = totalDepth + etaR;
			zE = totalDepth + etaE;

			outputToBuffer = new ArrayList<double[]>();
		}

		outputToBuffer.clear();

		zR = totalDepth + variables.rootDepth;

		variables.stressRadiationSun = 1;
		if (useRadiationStress == true) {
			variables.stressRadiationSun = EnvironmentalStress
					.computeRadiationStress(variables.shortwaveCanopySun * 2.1, alpha, thetaR);
		}

		variables.stressRadiationShade = 1;
		if (useRadiationStress == true) {
			variables.stressRadiationShade = EnvironmentalStress
					.computeRadiationStress(variables.shortwaveCanopyShade * 2.1, alpha, thetaR);
		}

		variables.stressTemperature = 1;
		if (useTemperatureStress == true) {
			variables.stressTemperature = EnvironmentalStress.computeTemperatureStress(input.airTemperature, Tl, Th,
					T0);
		}

		variables.stressVPD = 1;
		if (useVDPStress == true) {
			variables.stressVPD = EnvironmentalStress.computeVapourPressureStress(variables.vapourPressureDeficit,
					input.airTemperature, VPD0);
		}

		variables.stressWater = 1;

		g = stressFactor.computeStressFactor(theta, zR, zE);

		GnT = representativeTranspirationSF.computeRepresentativeStressFactor(g, variables.rootDepth, zR);

		GnE = representativeEvaporationSF.computeRepresentativeStressFactor(g, etaE, zE);

		if (useWaterStress == true) {
			variables.stressWater = GnT[0];
			variables.evaporationStressWater = GnE[0];

			// System.out.printf("\nG = %.5f %n", GnT[0]);
			// System.out.printf("GE = %.5f %n", GnE[0]);

		}

		evaporationStressWater = variables.evaporationStressWater;

		stressSun = defaultStress * variables.stressRadiationSun * variables.stressTemperature * variables.stressWater
				* variables.stressVPD;

		stressShade = defaultStress * variables.stressRadiationShade * variables.stressTemperature
				* variables.stressWater * variables.stressVPD;

		//// System.out.printf("\n\nStressFactorBroker Finished, G = %.5f %n", GnT[0]);
		//// System.out.printf("\nGE = %.5f %n", GnE[0]);
		/*
		 * System.out.println("CELJstressSun is  = "+ stressSun);
		 * System.out.println("CELJstressShade is  = "+ stressShade);
		 * System.out.println("CELJsstressRadiationSun is  = "+
		 * variables.stressRadiationSun);
		 * System.out.println("CELJstressTemperature is  = "+
		 * variables.stressTemperature); System.out.println("CELJstressWater is  = "+
		 * stressWater); System.out.println("CELJstressVPD is  = "+
		 * variables.stressVPD); System.out.println("CELJdefault is  = "+
		 * defaultStress);
		 */
		/*
		 * System.out.println("CELJvariables.shortwaveCanopySun is  = "+
		 * variables.shortwaveCanopySun);
		 * System.out.println("CELJvariables.shortwaveCanopyShade is  = "+
		 * variables.shortwaveCanopyShade); System.out.println("CELJalpha is  = "+
		 * alpha); System.out.println("CELJtheta is  = "+ theta);
		 * System.out.println("CELJinput.airTemperature is  = "+ input.airTemperature);
		 * System.out.println("CELJTl is  = "+ Tl); System.out.println("CELJTh is  = "+
		 * Th); System.out.println("CELJT0 is  = "+ T0);
		 */

		// System.out.println("\nvariables.rootDepth = "+ variables.rootDepth);
		// System.out.println("\n\n zR = "+ zR);
		// System.out.println("\nwaterstress = "+ Arrays.toString(g));
		// System.out.println("\ntheta = "+ Arrays.toString(theta));

		outputToBuffer.add(g);

		outputToBuffer.add(new double[] { variables.stressWater });
		outputToBuffer.add(new double[] { variables.evaporationStressWater });
		outputToBuffer.add(new double[] { stressSun });
		outputToBuffer.add(new double[] { stressShade });

		step++;
		// System.out.printf("End JarvisStressFactorSolver");
	}
}
