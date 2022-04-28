package it.geoframe.blogspot.geoet.prospero.data;

import static java.lang.Math.PI;
import static java.lang.Math.pow;

public class Leaf {
	
	private static Leaf uniqueInstance;

	public static Leaf getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Leaf();
		}
		return uniqueInstance;
	}
	
	public double leafLength = 0.25;
	public int leafSide = 2;
	public int leafStomaSide = 1;

	public double area = PI*pow(leafLength/2,2);
	
	public double poreRadius = 22 * pow(10,-6);
	public double poreDensity = 35 * pow(10,6);
	public double poreArea = pow(poreRadius,2)*PI;
	public double poreDepth= 2.5 * pow(10,-5);
	
	public double shortWaveAbsorption = 0.8;	
	public double shortWaveReflectance = 0.2;	
	public double shortWaveTransmittance = 0;
	
	public double longWaveAbsorption = 0.8;	
	public double longWaveReflectance = 0.2;	  
	public double longWaveTransmittance = 0;
	public double longWaveEmittance = 0.95;

}
