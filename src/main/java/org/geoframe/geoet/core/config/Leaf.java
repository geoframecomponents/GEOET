package org.geoframe.geoet.core.config;

import static java.lang.Math.PI;
import static java.lang.Math.pow;

/**
 * Fixed leaf-scale geometric, stomatal and optical parameters shared by the
 * transpiration/radiation solvers (Prospero, TranspirationBudget, the
 * "complete" radiation model). It is a constants bag.
 * 
 * @author Concetta D'Amato, Michele Bottazzi and Riccardo Rigon
 * @author Andrea Antonello
 */
public class Leaf {

	/** Characteristic leaf length, used as the leaf boundary-layer length scale in the sensible-heat transfer coefficient. Unit: m. */
	public double leafLength = 0.25;
	/** Number of leaf sides exposed to convective/radiative exchange (2 for a flat leaf exchanging on both faces). */
	public int leafSide = 2;
	/** Number of leaf sides bearing stomata (1 = hypostomatous, stomata on one face only). */
	public int leafStomaSide = 1;

	/** Leaf area, from {@link #leafLength} treated as a disc diameter. Unit: m2. */
	public double area() {
		return PI*pow(leafLength/2,2);
	}

	/** Stomatal pore radius. Unit: m. */
	public double poreRadius = 22 * pow(10,-6);
	/** Stomatal pore density (pores per unit leaf area). Unit: m-2. */
	public double poreDensity = 35 * pow(10,6);
	/** Cross-sectional area of a single stomatal pore, from {@link #poreRadius}. Unit: m2. */
	public double poreArea() {
		return pow(poreRadius,2)*PI;
	}
	/** Stomatal pore depth. Unit: m. */
	public double poreDepth= 2.5 * pow(10,-5);

	/** Fraction of incident shortwave radiation absorbed by the leaf. Unit: -. */
	public double shortWaveAbsorption = 0.8;
	/** Fraction of incident shortwave radiation reflected by the leaf. Unit: -. */
	public double shortWaveReflectance = 0.2;
	/** Fraction of incident shortwave radiation transmitted through the leaf. Unit: -. */
	public double shortWaveTransmittance = 0;

	/** Fraction of incident longwave radiation absorbed by the leaf. Unit: -. */
	public double longWaveAbsorption = 0.8;
	/** Fraction of incident longwave radiation reflected by the leaf. Unit: -. */
	public double longWaveReflectance = 0.2;
	/** Fraction of incident longwave radiation transmitted through the leaf. Unit: -. */
	public double longWaveTransmittance = 0;
	/** Leaf longwave emissivity, used in the leaf's own thermal emission term. Unit: -. */
	public double longWaveEmittance = 0.95;

}
