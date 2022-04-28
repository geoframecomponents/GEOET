package penmanmonteithfao;

import java.net.URISyntaxException;

import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.shapefile.OmsShapefileFeatureReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
//import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.junit.*;

import it.geoframe.blogspot.geoet.inout.InputReaderMain;
import it.geoframe.blogspot.geoet.inout.OutputWriterMain;
import it.geoframe.blogspot.geoet.penmanmonteithfao.*;
import it.geoframe.blogspot.geoet.stressfactor.solver.PTPMStressFactorSolverMain;
/**
 * Test FAO Hourly evapotranspiration.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
//@SuppressWarnings("nls")
public class TestPenmanMonteithFAOTotalStressed{
	
	@Test
    public void Test() throws Exception {
		String startDate= "2014-01-01 00:00";
        String endDate	= "2015-01-01 00:00";
        int timeStepMinutes = 60;
        String fId = "ID";

        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        OmsRasterReader DEMreader = new OmsRasterReader();
        
        DEMreader.file = "resources/Input/dataET_point/1/dem_1.tif";
		DEMreader.fileNovalue = -9999.0;
		DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

        String inPathToTemperature 			="resources/Input/dataET_point/1/airT_1.csv";
        String inPathToWind 				="resources/Input/dataET_point/1/Wind_1.csv";
        String inPathToRelativeHumidity 	="resources/Input/dataET_point/1/RH_1.csv";
        String inPathToNetRad 				="resources/Input/dataET_point/1/Net_1.csv";
        String inPathToPressure 			="resources/Input/dataET_point/1/Pres_1.csv";
        String inPathToSoilHeatFlux 		="resources/Input/dataET_point/1/GHF_1.csv";
        String inPathToCentroids 			="resources/Input/dataET_point/1/centroids_ID_1.shp";
        String inPathToSoilMosture 			="resources/Input/dataET_point/1/SoilMoisture18.csv";
        
        String pathToEvapotranspirationFAO	="resources/Output/ETtotalStressedFAO.csv";
        String pathToLatentHeatFAO			="resources/Output/FluxETtotalStressedFAO.csv";


        OmsTimeSeriesIteratorReader tempReader 			= getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader windReader 			= getTimeseriesReader(inPathToWind, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader humReader 			= getTimeseriesReader(inPathToRelativeHumidity, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader netradReader 		= getTimeseriesReader(inPathToNetRad, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader 		= getTimeseriesReader(inPathToPressure, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilMoistureReader 	= getTimeseriesReader(inPathToSoilMosture, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilHeatFluxReader 	= getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate, endDate,timeStepMinutes);

        OmsShapefileFeatureReader centroidsReader 		= new OmsShapefileFeatureReader();
        centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;
        
        OmsTimeSeriesIteratorWriter writerEvapotranspirationFAO = new OmsTimeSeriesIteratorWriter();
        writerEvapotranspirationFAO.file = pathToEvapotranspirationFAO;
        writerEvapotranspirationFAO.tStart = startDate;
        writerEvapotranspirationFAO.tTimestep = timeStepMinutes;
        writerEvapotranspirationFAO.fileNovalue="-9999";

		OmsTimeSeriesIteratorWriter writerLatentHeatFAO = new OmsTimeSeriesIteratorWriter();
		writerLatentHeatFAO.file = pathToLatentHeatFAO;
		writerLatentHeatFAO.tStart = startDate;
		writerLatentHeatFAO.tTimestep = timeStepMinutes;
		writerLatentHeatFAO.fileNovalue="-9999";
	
		InputReaderMain Input 							= new InputReaderMain();
		PenmanMonteithFAOTotalStressedSolverMain PmFAO 	= new PenmanMonteithFAOTotalStressedSolverMain();
		PTPMStressFactorSolverMain PMstressfactor 		= new PTPMStressFactorSolverMain();
        OutputWriterMain Output 						= new OutputWriterMain();
        
        Input.inCentroids = stationsFC;
		Input.idCentroids= "ID";
		Input.centroidElevation="Elevation";
		Input.inDem = digitalElevationModel; 
		Input.tStartDate=startDate;
        Input.temporalStep = timeStepMinutes;
		
        PMstressfactor.defaultStress = 1.0;
        PMstressfactor.useRadiationStress=true;
        PMstressfactor.useTemperatureStress=false;
        PMstressfactor.useVDPStress=false;
        PMstressfactor.useWaterStress=true;
        PMstressfactor.alpha = 0.005;
        PMstressfactor.theta = 0.9;
        PMstressfactor.VPD0 = 5.0;
        PMstressfactor.Tl = -5.0;
        PMstressfactor.T0 = 20.0;
		PMstressfactor.Th = 45.0;
		PMstressfactor.waterWiltingPoint = 0.05;
		PMstressfactor.waterFieldCapacity = 0.27; 
		PMstressfactor.rootsDepth = 0.75;
		PMstressfactor.depletionFraction = 0.55;
		PMstressfactor.cropCoefficient = 0.75;
		
		PmFAO.canopyHeight = 0.12;
        PmFAO.soilFluxParameterDay = 0.35;
        PmFAO.soilFluxParameterNight = 0.75;
        
        while( tempReader.doProcess ) {
            tempReader.nextRecord();

            HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
            Input.inAirTemperature = id2ValueMap; 
            Output.doPrintOutputPM = true;
            
            windReader.nextRecord();
            id2ValueMap = windReader.outData;
            Input.inWindVelocity = id2ValueMap;
            
            humReader.nextRecord();
            id2ValueMap = humReader.outData;
            Input.inRelativeHumidity = id2ValueMap;

            netradReader.nextRecord();
            id2ValueMap = netradReader.outData;
            Input.inNetRadiation = id2ValueMap;
            
            pressureReader.nextRecord();
            id2ValueMap = pressureReader.outData;
            Input.inAtmosphericPressure = id2ValueMap;
            
            soilMoistureReader.nextRecord();
            id2ValueMap = soilMoistureReader.outData;
            Input.inSoilMoisture = id2ValueMap;
            
            soilHeatFluxReader.nextRecord();
            id2ValueMap = soilHeatFluxReader.outData;
            Input.inSoilFlux = id2ValueMap;

            Input.process();
            PMstressfactor.solve();
            PmFAO.stressFactor = PMstressfactor.stressSun;
            PmFAO.process();
            Output.process();
            
    		
    		//HashMap<Integer, double[]> outLatentHeat = PmFAO.outLatentHeatFao;
    		writerLatentHeatFAO.inData = Output.outLatentHeatPM;
    		writerLatentHeatFAO.writeNextLine();	

            //HashMap<Integer, double[]> outEvapotranspiration = PmFAO.outEvapotranspirationFao;
            writerEvapotranspirationFAO.inData = Output.outEvapoTranspirationPM;
            writerEvapotranspirationFAO.writeNextLine();	

        }

        tempReader.close();
        windReader.close();
        humReader.close();
        netradReader.close();
        pressureReader.close();
        soilHeatFluxReader.close();
        soilMoistureReader.close();
		writerLatentHeatFAO.close();
        writerEvapotranspirationFAO.close();
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
