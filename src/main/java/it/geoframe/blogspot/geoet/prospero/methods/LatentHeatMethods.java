package it.geoframe.blogspot.geoet.prospero.methods;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import oms3.annotations.Author;
import oms3.annotations.License;

import static java.lang.Math.PI;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")

public class LatentHeatMethods {
	
	private double thermalDiffusivity;
	private double binaryDiffusionCoefficient;
	private double ratio;
	private double lewisNumber;
	private double throatResistance;
	private double constantTerm;
	private double vapourResistance;
	private double molarStomatalConductance;
	private double stomatalConductance;
	private double boundaryLayerConductance;
	private double totalConductance;
	private double molarTotalConductance;
	private double latentHeatTransferCoefficient;
	private double latentHeatFlux;
	
	
	public double computeLatentHeatTransferCoefficient (double airTemperature, double atmosphericPressure, int leafStomaSide,double convectiveTransferCoefficient,
		double airSpecificHeat, double airDensity, double molarGasConstant, double molarVolume, double waterMolarMass, double latentHeatEvaporation, 
		double poreDensity,	double poreArea, double poreDepth, double poreRadius) {
				
		// Notation from Schymanski & Or, 2017
		
		// alpha_a
		thermalDiffusivity = 1.32 * pow(10,-7) * airTemperature - 1.73 * pow(10,-5);
		// D_va
		binaryDiffusionCoefficient = 1.49 * pow(10,-7) * airTemperature - 1.96 * pow(10,-5);
		// k_dv
		ratio = binaryDiffusionCoefficient/molarVolume;
		// N_Le
		lewisNumber = thermalDiffusivity/binaryDiffusionCoefficient;
		// r_sp
		throatResistance = poreDepth/(poreArea*ratio*poreDensity);
		
		constantTerm= 1/(4*poreRadius) - 1/(PI * (1/sqrt(poreDensity)));
		// r_vs
		vapourResistance = constantTerm * 1/(ratio * poreDensity);
		// g_sw,mol
		molarStomatalConductance = 1/(throatResistance + vapourResistance);
		// g_sw
		stomatalConductance = molarStomatalConductance * (molarGasConstant * airTemperature)/atmosphericPressure  ;
		//System.out.println(stomatalConductance);
		// g_bw
		boundaryLayerConductance = leafStomaSide*convectiveTransferCoefficient/(airSpecificHeat*airDensity*pow(lewisNumber,0.66));
		// g_tw
		totalConductance = 1/ ((1/stomatalConductance) + (1/boundaryLayerConductance));
		// g_tw,mol
		molarTotalConductance = totalConductance*40;
		// c_E
		latentHeatTransferCoefficient = (waterMolarMass * latentHeatEvaporation * molarTotalConductance) / atmosphericPressure;
		return latentHeatTransferCoefficient;	
		}
	public double computeLatentHeatFlux(double delta, double leafTemperature, double airTemperature, double latentHeatTransferCoefficient,double sensibleHeatTransferCoefficient, double vaporPressure, double saturationVaporPressure) {
		 // Computation of the latent heat flux from leaf [J m-2 s-1]
		//double latentHeatFlux = (sensibleHeatTransferCoefficient* (delta * (leafTemperature - airTemperature) + saturationVaporPressure - vaporPressure))/(sensibleHeatTransferCoefficient/latentHeatTransferCoefficient);
		
		latentHeatFlux = (latentHeatTransferCoefficient* (delta * (leafTemperature - airTemperature) + saturationVaporPressure - vaporPressure));
		
		return latentHeatFlux;	
	}
	}