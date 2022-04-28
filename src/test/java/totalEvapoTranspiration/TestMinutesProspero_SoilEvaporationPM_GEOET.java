package totalEvapoTranspiration;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
//import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.junit.*;
import it.geoframe.blogspot.geoet.inout.*;
//import it.geoframe.blogspot.geoet.prospero.data.*;
//import it.geoframe.blogspot.geoet.prospero.methods.*;
import it.geoframe.blogspot.geoet.prospero.solver.*;
import it.geoframe.blogspot.geoet.soilevaporation.solver.*;
//import it.geoframe.blogspot.geoet.stressfactor.methods.*;
import it.geoframe.blogspot.geoet.stressfactor.solver.*;
import it.geoframe.blogspot.geoet.totalEvapoTranspiration.*;

/**
 * Test LysProspero-Schymanski & Or evapotranspiration.
 * @author D'Amato Concetta, Michele Bottazzi (concetta.damato@unitn.it)
 */
public class TestMinutesProspero_SoilEvaporationPM_GEOET{
	@Test
    public void Test() throws Exception {
		String startDate= "2013-12-15 07:00";
        String endDate	= "2013-12-15 12:00";
        int timeStepMinutes = 15;
        String fId = "ID";
        
        //////////////////////////////////////////////////////////////////////////////////////////////int stationID = 1;

        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        
        OmsRasterReader DEMreader = new OmsRasterReader();
		DEMreader.file = "resources/Input/dataET_point/Cavone/1/dem_1.tif";
		//DEMreader.fileNovalue = -9999.0;
		//DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;
              
		String inPathToTemperature 				="resources/Input/dataET_point/TestCase/airT_1.csv";
        String inPathToWind 					="resources/Input/dataET_point/TestCase/Wind_1.csv";
        String inPathToRelativeHumidity 		="resources/Input/dataET_point/TestCase/RH_1.csv";
        String inPathToShortWaveRadiationDirect ="resources/Input/dataET_point/TestCase/ShortwaveDirect_1.csv";
        String inPathToShortWaveRadiationDiffuse="resources/Input/dataET_point/TestCase/ShortwaveDiffuse_1.csv";
        String inPathToLWRad 					="resources/Input/dataET_point/TestCase/LongDownwelling_1.csv";
        String inPathToNetRad 					="resources/Input/dataET_point/TestCase/Net_1.csv";
        String inPathToSoilHeatFlux 			="resources/Input/dataET_point/TestCase/GHF_1.csv";
        String inPathToPressure 				="resources/Input/dataET_point/TestCase/Pres_1.csv";
        String inPathToLai 						="resources/Input/dataET_point/TestCase/LAI_1.csv";
        String inPathToCentroids 				="resources/Input/dataET_point/TestCase/centroids_ID_1.shp";
        String inPathToSoilMoisture				="resources/Input/dataET_point/TestCase/SoilMoisture18.csv";
        
        String outPathToLatentHeatSun			="resources/Output/LatentHeatSun.csv";
        String outPathToLatentHeatShadow		="resources/Output/LatentHeatShadow.csv";
        
        String outPathToFluxTranspiration		="resources/Output/FluxTranspiration15Min.csv";
        String outPathToFluxEvapoTranspiration	="resources/Output/FluxEvapoTranspiration15Min.csv";
        String outPathToFluxEvaporation			="resources/Output/FluxEvaporation15Min.csv";
        String outPathToEvapoTranspiration		="resources/Output/EvapoTranspiration15Min.csv";
        String outPathToTranspiration			="resources/Output/Transpiration15Min.csv";
        String outPathToEvaporation				="resources/Output/Evaporation15Min.csv";
        
        String outPathToLeafTemperatureSun		="resources/Output/LeafTemperatureSun.csv";
		String outPathToLeafTemperatureShadow	="resources/Output/LeafTemperatureSh.csv";
		
		String outPathToSensibleSun				="resources/Output/sensibleSun.csv";
		String outPathToSensibleShadow			="resources/Output/sensibleShadow.csv";
        String outPathToRadiationSoil 			="resources/Output/RadiationSoil.csv";
		String outPathToRadiationSun			="resources/Output/RadSun.csv";
		String outPathToRadiationShadow			="resources/Output/RadShadow.csv";
		String outPathToCanopy					="resources/Output/Canopy.csv";
		String outPathToVPD						="resources/Output/VPD.csv";
		
        OmsTimeSeriesIteratorReader temperatureReader	= getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader windReader 		 	= getTimeseriesReader(inPathToWind, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader humidityReader 		= getTimeseriesReader(inPathToRelativeHumidity, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader shortwaveReaderDirect 	= getTimeseriesReader(inPathToShortWaveRadiationDirect, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader shortwaveReaderDiffuse 	= getTimeseriesReader(inPathToShortWaveRadiationDiffuse, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader longwaveReader 		= getTimeseriesReader(inPathToLWRad, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader 		= getTimeseriesReader(inPathToPressure, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader leafAreaIndexReader	= getTimeseriesReader(inPathToLai, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilHeatFluxReader 	= getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader netRadReader 		= getTimeseriesReader(inPathToNetRad, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilMoistureReader 	= getTimeseriesReader(inPathToSoilMoisture, fId, startDate, endDate,timeStepMinutes);
         

        OmsShapefileFeatureReader centroidsReader 		= new OmsShapefileFeatureReader();
        centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;
		
		OmsTimeSeriesIteratorWriter latentHeatSunWriter = new OmsTimeSeriesIteratorWriter();
		latentHeatSunWriter.file = outPathToLatentHeatSun;
		latentHeatSunWriter.tStart = startDate;
		latentHeatSunWriter.tTimestep = timeStepMinutes;
		latentHeatSunWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter latentHeatShadowWriter = new OmsTimeSeriesIteratorWriter();
		latentHeatShadowWriter.file = outPathToLatentHeatShadow;
		latentHeatShadowWriter.tStart = startDate;
		latentHeatShadowWriter.tTimestep = timeStepMinutes;
		latentHeatShadowWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter FluxTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		FluxTranspirationWriter.file = outPathToFluxTranspiration;
		FluxTranspirationWriter.tStart = startDate;
		FluxTranspirationWriter.tTimestep = timeStepMinutes;
		FluxTranspirationWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter FluxEvaporationWriter = new OmsTimeSeriesIteratorWriter();
		FluxEvaporationWriter.file = outPathToFluxEvaporation;
		FluxEvaporationWriter.tStart = startDate;
		FluxEvaporationWriter.tTimestep = timeStepMinutes;
		FluxEvaporationWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter FluxEvapoTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		FluxEvapoTranspirationWriter.file = outPathToFluxEvapoTranspiration;
		FluxEvapoTranspirationWriter.tStart = startDate;
		FluxEvapoTranspirationWriter.tTimestep = timeStepMinutes;
		FluxEvapoTranspirationWriter.fileNovalue="-9999";

		OmsTimeSeriesIteratorWriter EvapoTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		EvapoTranspirationWriter.file = outPathToEvapoTranspiration;
		EvapoTranspirationWriter.tStart = startDate;
		EvapoTranspirationWriter.tTimestep = timeStepMinutes;
		EvapoTranspirationWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter TranspirationWriter = new OmsTimeSeriesIteratorWriter();
		TranspirationWriter.file = outPathToTranspiration;
		TranspirationWriter.tStart = startDate;
		TranspirationWriter.tTimestep = timeStepMinutes;
		TranspirationWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter EvaporationWriter = new OmsTimeSeriesIteratorWriter();
		EvaporationWriter.file = outPathToEvaporation;
		EvaporationWriter.tStart = startDate;
		EvaporationWriter.tTimestep = timeStepMinutes;
		EvaporationWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter leafTemperatureSunWriter = new OmsTimeSeriesIteratorWriter();
		leafTemperatureSunWriter.file = outPathToLeafTemperatureSun;
		leafTemperatureSunWriter.tStart = startDate;
		leafTemperatureSunWriter.tTimestep = timeStepMinutes;
		leafTemperatureSunWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter leafTemperatureShadowWriter = new OmsTimeSeriesIteratorWriter();
		leafTemperatureShadowWriter.file = outPathToLeafTemperatureShadow;
		leafTemperatureShadowWriter.tStart = startDate;
		leafTemperatureShadowWriter.tTimestep = timeStepMinutes;
		leafTemperatureShadowWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter radiationSunWriter = new OmsTimeSeriesIteratorWriter();
		radiationSunWriter.file = outPathToRadiationSun;
		radiationSunWriter.tStart = startDate;
		radiationSunWriter.tTimestep = timeStepMinutes;
		radiationSunWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter radiationShadowWriter = new OmsTimeSeriesIteratorWriter();
		radiationShadowWriter.file = outPathToRadiationShadow;
		radiationShadowWriter.tStart = startDate;
		radiationShadowWriter.tTimestep = timeStepMinutes;
		radiationShadowWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter sensibleSunWriter = new OmsTimeSeriesIteratorWriter();
		sensibleSunWriter.file = outPathToSensibleSun;
		sensibleSunWriter.tStart = startDate;
		sensibleSunWriter.tTimestep = timeStepMinutes;
		sensibleSunWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter sensibleShadowWriter = new OmsTimeSeriesIteratorWriter();
		sensibleShadowWriter.file = outPathToSensibleShadow;
		sensibleShadowWriter.tStart = startDate;
		sensibleShadowWriter.tTimestep = timeStepMinutes;
		sensibleShadowWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter radiationSoilWriter = new OmsTimeSeriesIteratorWriter();
		radiationSoilWriter.file = outPathToRadiationSoil;
		radiationSoilWriter.tStart = startDate;
		radiationSoilWriter.tTimestep = timeStepMinutes;
		radiationSoilWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter canopyWriter = new OmsTimeSeriesIteratorWriter();
		canopyWriter.file = outPathToCanopy;
		canopyWriter.tStart = startDate;
		canopyWriter.tTimestep = timeStepMinutes;
		canopyWriter.fileNovalue="-9999";
		
		OmsTimeSeriesIteratorWriter vapourPressureDeficitWriter = new OmsTimeSeriesIteratorWriter();
		vapourPressureDeficitWriter.file = outPathToVPD;
		vapourPressureDeficitWriter.tStart = startDate;
		vapourPressureDeficitWriter.tTimestep = timeStepMinutes;
		vapourPressureDeficitWriter.fileNovalue="-9999";
		
		InputReaderMain Input 								= new InputReaderMain();
		ProsperoStressFactorSolverMain ProsperoStressFactor = new ProsperoStressFactorSolverMain();
		PMEvaporationFromSoilAfterCanopySolverMain PMsoilevaporation = new PMEvaporationFromSoilAfterCanopySolverMain();
		ProsperoSolverMain Prospero 						= new ProsperoSolverMain();
		TotalEvapoTranspirationSolverMain TotalEvapoTranspiration = new TotalEvapoTranspirationSolverMain();
		OutputWriterMain Output 							= new OutputWriterMain();
		
		
		Input.inCentroids = stationsFC;
		Input.idCentroids= "ID";
		Input.centroidElevation="Elevation";
		
		Input.inDem = digitalElevationModel; 


		Prospero.canopyHeight = 0.2;
		ProsperoStressFactor.defaultStress = 1.0;
		//Prospero.doIterative = false;
		
		
		ProsperoStressFactor.useRadiationStress=false;
		ProsperoStressFactor.useTemperatureStress=false;
		ProsperoStressFactor.useVDPStress=false;
		ProsperoStressFactor.useWaterStress=false;

	
		
		ProsperoStressFactor.alpha = 0.005;
		ProsperoStressFactor.theta = 0.9;
		ProsperoStressFactor.VPD0 = 5.0;
        	
		ProsperoStressFactor.Tl = -5.0;
		ProsperoStressFactor.T0 = 20.0;
		ProsperoStressFactor.Th = 45.0;
		Prospero.typeOfCanopy="multilayer";
		ProsperoStressFactor.waterWiltingPoint = 0.15;
		ProsperoStressFactor.waterFieldCapacity = 0.27; 
		ProsperoStressFactor.rootsDepth = 0.75;
		ProsperoStressFactor.depletionFraction = 0.55;
		ProsperoStressFactor.cropCoefficient = 0.75;
		
		while(temperatureReader.doProcess ) {
        	temperatureReader.nextRecord();
        	
       		

            HashMap<Integer, double[]> id2ValueMap = temperatureReader.outData;
            Input.inAirTemperature = id2ValueMap;
            //Input.doHourly = true;
            Output.doFullPrint = true;
           //Prospero.typeOfTerrainCover = "FlatSurface";
            Input.tStartDate = startDate;
            Input.temporalStep = timeStepMinutes;

            windReader.nextRecord();
            id2ValueMap = windReader.outData;
            Input.inWindVelocity = id2ValueMap;

            humidityReader.nextRecord();
            id2ValueMap = humidityReader.outData;
            Input.inRelativeHumidity = id2ValueMap;

            shortwaveReaderDirect.nextRecord();
            id2ValueMap = shortwaveReaderDirect.outData;
            Input.inShortWaveRadiationDirect = id2ValueMap;
            
            shortwaveReaderDiffuse.nextRecord();
            id2ValueMap = shortwaveReaderDiffuse.outData;
            Input.inShortWaveRadiationDiffuse = id2ValueMap;
            
            longwaveReader.nextRecord();
            id2ValueMap = longwaveReader.outData;
            Input.inLongWaveRadiation = id2ValueMap;
            
            soilHeatFluxReader.nextRecord();
            id2ValueMap = soilHeatFluxReader.outData;
            Input.inSoilFlux = id2ValueMap;
            
            pressureReader.nextRecord();
            id2ValueMap = pressureReader.outData;
            Input.inAtmosphericPressure = id2ValueMap;
            
            leafAreaIndexReader.nextRecord();
            id2ValueMap = leafAreaIndexReader.outData;
            Input.inLeafAreaIndex = id2ValueMap;
            
            netRadReader.nextRecord();
            id2ValueMap = netRadReader.outData;
            Input.inNetRadiation = id2ValueMap;
            
            soilMoistureReader.nextRecord();
            id2ValueMap = soilMoistureReader.outData;
            Input.inSoilMoisture = id2ValueMap;
           
            
        
            Input.process();

            PMsoilevaporation.evaporationStressWater = 0.9;
            PMsoilevaporation.process();
            
            ProsperoStressFactor.solve();
            
            Prospero.stressSun = ProsperoStressFactor.stressSun;
            Prospero.stressShade = ProsperoStressFactor.stressShade;
            Prospero.process();
            
            TotalEvapoTranspiration.evaporation = PMsoilevaporation.evaporation;
            TotalEvapoTranspiration.transpiration = Prospero.transpiration;
            TotalEvapoTranspiration.process();
            
            Output.process();
             
            latentHeatSunWriter.inData = Output.outLatentHeatSun;
            latentHeatSunWriter.writeNextLine();

			latentHeatShadowWriter.inData = Output.outLatentHeatShade;
            latentHeatShadowWriter.writeNextLine();		
            
            FluxTranspirationWriter.inData = Output.outFluxTranspiration;
            FluxTranspirationWriter.writeNextLine();
            
            FluxEvaporationWriter.inData = Output.outFluxEvaporation;
            FluxEvaporationWriter.writeNextLine();
            
            FluxEvapoTranspirationWriter.inData = Output.outFluxEvapoTranspiration;
            FluxEvapoTranspirationWriter.writeNextLine();	
			
            EvapoTranspirationWriter.inData = Output.outEvapoTranspiration;
            EvapoTranspirationWriter.writeNextLine();
			
			TranspirationWriter.inData = Output.outTranspiration;
			TranspirationWriter.writeNextLine();
    		
			EvaporationWriter.inData = Output.outEvaporation;
			EvaporationWriter.writeNextLine();
			
			leafTemperatureSunWriter.inData = Output.outLeafTemperature;
			leafTemperatureSunWriter.writeNextLine();			 	

			leafTemperatureShadowWriter.inData = Output.outLeafTemperatureShade;
			leafTemperatureShadowWriter.writeNextLine();

			
			if (Output.doFullPrint == true) {
						 	

			radiationSunWriter.inData = Output.outRadiation;
			radiationSunWriter.writeNextLine();			 	

			radiationShadowWriter.inData = Output.outRadiationShade;
			radiationShadowWriter.writeNextLine();			 	
			
			sensibleSunWriter.inData = Output.outSensibleHeat;
			sensibleSunWriter.writeNextLine();			 	
			
			sensibleShadowWriter.inData = Output.outSensibleHeatShade;
			sensibleShadowWriter.writeNextLine();
			
			radiationSoilWriter.inData = Output.outRadiationSoil;
			radiationSoilWriter.writeNextLine();
			
			canopyWriter.inData = Output.outCanopy;
			canopyWriter.writeNextLine();
			
			vapourPressureDeficitWriter.inData = Output.outVapourPressureDeficit;
			vapourPressureDeficitWriter.writeNextLine();
			
			
			radiationSunWriter.close();
			radiationShadowWriter.close();
			sensibleSunWriter.close();
			sensibleShadowWriter.close();
			radiationSoilWriter.close();
			canopyWriter.close();
			vapourPressureDeficitWriter.close();
			}
			
			}
       
        temperatureReader.close();        
        windReader.close();
        humidityReader.close();     
        shortwaveReaderDirect.close();
        shortwaveReaderDiffuse.close();
        longwaveReader.close();
        soilHeatFluxReader.close();
        pressureReader.close();
        leafAreaIndexReader.close();
        soilMoistureReader.close();
                
        latentHeatSunWriter.close();
		latentHeatShadowWriter.close();
		FluxEvapoTranspirationWriter.close();
		FluxEvaporationWriter.close();
		FluxTranspirationWriter.close();
		EvapoTranspirationWriter.close();
		TranspirationWriter.close();
		EvaporationWriter.close();
		leafTemperatureSunWriter.close();
		leafTemperatureShadowWriter.close();



    }

    private OmsTimeSeriesIteratorReader getTimeseriesReader( String path, String id, String startDate, String endDate,
            int timeStepMinutes ) throws URISyntaxException {
        OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
        reader.file = path;
        reader.idfield = id;
        reader.tStart =startDate;
        reader.tTimestep = timeStepMinutes;
        reader.tEnd = endDate;
        reader.fileNovalue = "-9999.0";
        reader.initProcess();
        return reader;
    }

}
