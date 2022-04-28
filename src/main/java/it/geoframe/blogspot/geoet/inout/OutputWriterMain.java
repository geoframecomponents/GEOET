package it.geoframe.blogspot.geoet.inout;

import java.util.HashMap;

import org.geotools.feature.SchemaException;

import it.geoframe.blogspot.geoet.data.ProblemQuantities;
//import it.geoframe.blogspot.geoet.prospero.data.*;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;


@Description("")

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@Keywords("Evapotranspiration")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class OutputWriterMain {
	

	@Description("The latent heat.")
	@Unit("mm h-1")
	@Out
	public HashMap<Integer, double[]> outLatentHeatSun;
	
	@Description("The latent heat.")
	@Unit("mm h-1")
	@Out
	public HashMap<Integer, double[]> outLatentHeatShade;
	
	@Description("Evaporation from soil.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outFluxEvaporation;
	
	@Description("The transpirated water.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outFluxTranspiration;
	
	@Description("The Evapotranspirated water.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outFluxEvapoTranspiration;
	
	@Description("The Evaporated water.")
	@Unit("mm h-1")
	@Out
	public HashMap<Integer, double[]> outEvaporation;
	
	@Description("The Transpirated water.")
	@Unit("mm h-1")
	@Out
	public HashMap<Integer, double[]> outTranspiration;
	
	@Description("The Evapotranspirated water.")
	@Unit("mm h-1")
	@Out
	public HashMap<Integer, double[]> outEvapoTranspiration;
	
	@Description("The sensible heat.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outSensibleHeat;
	
	@Description("The sensible heat.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outSensibleHeatShade;
	
	@Description("The leaf Temperature.")
	@Unit("K")
	@Out
	public HashMap<Integer, double[]> outLeafTemperature;
	
	@Description("The leaf Temperature.")
	@Unit("K")
	@Out
	public HashMap<Integer, double[]> outLeafTemperatureShade;
	
	@Description("The solar radiation absorbed by the sunlit canopy.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outRadiation;
	
	@Description("The solar radiation absorbed by the shaded canopy.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outRadiationShade;
	
	@Description("The solar radiation absorbed by the shaded canopy.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outRadiationSoil;
	
	@Description("Fraction of highlighted canopy.")
	@Unit("-")
	@Out
	public HashMap<Integer, double[]> outCanopy;
	
	@Description("Vapour pressure Deficit")
	@Unit("-")
	@Out
	public HashMap<Integer, double[]> outVapourPressureDeficit;
	
	@Description("The Evapotranspirated wateR .")
	@Unit("mm time-1")
	@Out
	public HashMap<Integer, double[]> outEvapoTranspirationPT;
	
	@Description("The flux of Evapotranspirated water.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outLatentHeatPT;
	
	@Description("The Evapotranspirated water.")
	@Unit("mm time-1")
	@Out
	public HashMap<Integer, double[]> outEvapoTranspirationPM;
	
	@Description("The flux of Evapotranspirated water.")
	@Unit("W m-2")
	@Out
	public HashMap<Integer, double[]> outLatentHeatPM;
	
	@Description("Choose if you want to print only the latent heat or all the other outputs.")
	@In
	public boolean doFullPrint = false;
	
	@In
	public boolean doPrintOutputPT = false;
	
	@In
	public boolean doPrintOutputPM = false;
	
	private ProblemQuantities variables;
	private InputTimeSeries input;
	

	@Execute
	public void process() throws Exception {
		System.out.print("\n\nStart OutputWriterMain");
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		
		outLatentHeatShade 	    = new HashMap<Integer, double[]>();
		outLatentHeatSun		= new HashMap<Integer, double[]>();
		outFluxTranspiration 	= new HashMap<Integer, double[]>();
		outFluxEvaporation 		= new HashMap<Integer, double[]>();
		
		outFluxEvapoTranspiration = new HashMap<Integer, double[]>();
		
		outEvapoTranspiration 	= new HashMap<Integer, double[]>();
		outEvaporation			= new HashMap<Integer, double[]>();
		outTranspiration		= new HashMap<Integer, double[]>();
		outLeafTemperatureShade	= new HashMap<Integer, double[]>();
		outLeafTemperature 		= new HashMap<Integer, double[]>();
		
		outEvapoTranspirationPT = new HashMap<Integer, double[]>();
		outLatentHeatPT			= new HashMap<Integer, double[]>();
		
		outEvapoTranspirationPM = new HashMap<Integer, double[]>();
		outLatentHeatPM			= new HashMap<Integer, double[]>();
		
		if (doFullPrint == true) {
			outRadiation 		= new HashMap<Integer, double[]>();
			outRadiationShade 	= new HashMap<Integer, double[]>();
			outSensibleHeat 	= new HashMap<Integer, double[]>();
			outSensibleHeatShade = new HashMap<Integer, double[]>();
			outRadiationSoil 	= new HashMap<Integer, double[]>();
			outCanopy 			= new HashMap<Integer, double[]>();
			outVapourPressureDeficit = new HashMap<Integer, double[]>();
			}
		
		//doPrintOutputPT= false;
		//doPrintOutputPM= false;
		
		if (doPrintOutputPT == false && doPrintOutputPM == false) {
		
			if (doFullPrint == true) {
				storeResultFull(input.ID, variables.latentHeatFluxSun, variables.latentHeatFluxShade, variables.evaporation, variables.transpiration, variables.evapoTranspiration,variables.fluxEvaporation,variables.fluxTranspiration,variables.fluxEvapoTranspiration, 
					variables.sensibleHeatFluxSun, variables.sensibleHeatFluxShade, variables.leafTemperatureSun, variables.leafTemperatureShade, variables.shortwaveCanopySun, variables.shortwaveCanopyShade,variables.incidentSolarRadiationSoil,variables.areaCanopySun, variables.vapourPressureDeficit);}
			else {
				storeResult(input.ID, variables.latentHeatFluxSun, variables.latentHeatFluxShade, variables.evaporation, variables.transpiration, variables.evapoTranspiration,variables.fluxEvaporation,variables.fluxTranspiration,variables.fluxEvapoTranspiration,variables.leafTemperatureSun, variables.leafTemperatureShade);
				}}
		
		
		if (doPrintOutputPT == true) {
			storeResultPT(input.ID, variables.evapoTranspirationPT, variables.fluxEvapoTranspirationPT);}
		
		if (doPrintOutputPM == true) {
			storeResultPM(input.ID, variables.evapoTranspirationPM, variables.fluxEvapoTranspirationPM);}
		
		System.out.print("\nEnd OutputWriterMain");	
	}
	
	
	private void storeResultFull(int ID,double latentHeatSun, double latentHeatShadow,double evaporation,double transpiration,double evapoTranspiration,double fluxEvaporation, double fluxTranspiration, double fluxEvapoTranspiration,
			double sensibleHeatFluxLight, double sensibleHeatFluxShadow,double leafTemperatureSun, double leafTemperatureShadow,double radiationCanopyInLight, double radiationCanopyInShadow, double incidentSolarRadiationSoil,double leafInSunlight, double vapourPressureDeficit) 
			throws SchemaException {		
		
		outLatentHeatSun.put(ID, new double[]{latentHeatSun});
		outLatentHeatShade.put(ID, new double[]{latentHeatShadow});
		outFluxEvaporation.put(ID, new double[]{fluxEvaporation});
		outFluxTranspiration.put(ID, new double[]{fluxTranspiration});
		outFluxEvapoTranspiration.put(ID, new double[]{fluxEvapoTranspiration});
		outEvaporation.put(ID, new double[]{evaporation});
		outTranspiration.put(ID, new double[]{transpiration});
		outEvapoTranspiration.put(ID, new double[]{evapoTranspiration});
		outLeafTemperature.put(ID, new double[]{leafTemperatureSun});
		outLeafTemperatureShade.put(ID, new double[]{leafTemperatureShadow});
		
		
		outSensibleHeat.put(ID, new double[]{sensibleHeatFluxLight});
		outSensibleHeatShade.put(ID, new double[]{sensibleHeatFluxShadow});
		outRadiation.put(ID, new double[]{radiationCanopyInLight});
		outRadiationShade.put(ID, new double[]{radiationCanopyInShadow});
		outRadiationSoil.put(ID, new double[]{incidentSolarRadiationSoil});
		outCanopy.put(ID, new double[]{leafInSunlight});
		outVapourPressureDeficit.put(ID, new double[]{vapourPressureDeficit});}
	
	
	private void storeResult(int ID,double latentHeatSun, double latentHeatShadow,double evaporation,double transpiration,double evapoTranspiration,double fluxEvaporation, double fluxTranspiration, double fluxEvapoTranspiration,
			double leafTemperatureSun, double leafTemperatureShadow) 
			throws SchemaException {	
		
		outLatentHeatSun.put(ID, new double[]{latentHeatSun});
		outLatentHeatShade.put(ID, new double[]{latentHeatShadow});
		outFluxEvaporation.put(ID, new double[]{fluxEvaporation});
		outFluxTranspiration.put(ID, new double[]{fluxTranspiration});
		outFluxEvapoTranspiration.put(ID, new double[]{fluxEvapoTranspiration});
		outEvaporation.put(ID, new double[]{evaporation});
		outTranspiration.put(ID, new double[]{transpiration});
		outEvapoTranspiration.put(ID, new double[]{evapoTranspiration});
		outLeafTemperature.put(ID, new double[]{leafTemperatureSun});
		outLeafTemperatureShade.put(ID, new double[]{leafTemperatureShadow});
		}
	
	private void storeResultPT(int ID,double evapoTranspirationPT, double fluxEvapoTranspirationPT) 
			throws SchemaException {	
		
		outEvapoTranspirationPT.put(ID, new double[]{evapoTranspirationPT});
		outLatentHeatPT.put(ID, new double[]{fluxEvapoTranspirationPT});
		
		}
	
	private void storeResultPM(int ID,double evapoTranspirationPM, double fluxEvapoTranspirationPM) 
			throws SchemaException {	
		
		outEvapoTranspirationPM.put(ID, new double[]{evapoTranspirationPM});
		outLatentHeatPM.put(ID, new double[]{fluxEvapoTranspirationPM});
		
		}
	
}
