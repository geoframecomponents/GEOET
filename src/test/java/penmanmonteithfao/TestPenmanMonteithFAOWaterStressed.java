package penmanmonteithfao;

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
import it.geoframe.blogspot.geoet.inout.InputReaderMain;
import it.geoframe.blogspot.geoet.inout.OutputWriterMain;
import it.geoframe.blogspot.geoet.penmanmonteithfao.*;
/**
 * Test FAO Hourly evapotranspiration.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
//@SuppressWarnings("nls")
public class TestPenmanMonteithFAOWaterStressed{
	
	@Test
    public void Test() throws Exception {
		String startDate= "2014-01-01 00:00";
        String endDate	= "2014-01-01 23:00";
        int timeStepMinutes = 60;
        String fId = "ID";

        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        OmsRasterReader DEMreader = new OmsRasterReader();
        
        DEMreader.file = "resources/Input/dataET_point/Cavone/1/dem_1.tif";
		//DEMreader.fileNovalue = -9999.0;
		//DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;
        
        String inPathToTemperature 			="resources/Input/dataET_point/1/airT_1.csv";
        String inPathToWind 				="resources/Input/dataET_point/1/Wind_1.csv";
        String inPathToRelativeHumidity 	="resources/Input/dataET_point/1/RH_1.csv";
        String inPathToNetRad 				="resources/Input/dataET_point/1/Net_1.csv";
        String inPathToPressure 			="resources/Input/dataET_point/1/Pres_1.csv";
        String inPathToSoilHeatFlux 		="resources/Input/dataET_point/1/GHF_1.csv";
        String inPathToSoilMoisture 		="resources/Input/dataET_point/1/SoilMoisture18.csv";
        String inPathToCentroids 			="resources/Input/dataET_point/1/centroids_ID_1.shp";
        
        String pathToEvapotranspirationFAO	="resources/Output/2ETwaterStressedFAO.csv";
        String pathToLatentHeatFAO			="resources/Output/2FluxETwaterStressedFAO.csv";


        OmsTimeSeriesIteratorReader tempReader 			= getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader windReader 			= getTimeseriesReader(inPathToWind, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader humReader 			= getTimeseriesReader(inPathToRelativeHumidity, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader netradReader 		= getTimeseriesReader(inPathToNetRad, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader 		= getTimeseriesReader(inPathToPressure, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilMoistureReader 	= getTimeseriesReader(inPathToSoilMoisture, fId, startDate, endDate,timeStepMinutes);
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

        PenmanMonteithFAOWaterStressedSolverMain PmFAO = new PenmanMonteithFAOWaterStressedSolverMain();
        InputReaderMain Input 		= new InputReaderMain();
        OutputWriterMain Output 	= new OutputWriterMain();
        
        Input.inCentroids = stationsFC;
		Input.idCentroids= "ID";
		Input.centroidElevation="Elevation";
		Input.inDem = digitalElevationModel;
        
        PmFAO.cropCoefficient = 0.75; //0.75
        PmFAO.waterWiltingPoint = 0.05;
        PmFAO.waterFieldCapacity = 0.27; 
        PmFAO.rootsDepth = 0.75;
        PmFAO.depletionFraction = 0.55;
        PmFAO.canopyHeight = 0.12;
        PmFAO.soilFluxParameterDay = 0.35;
        PmFAO.soilFluxParameterNight = 0.75;
        
        Input.tStartDate=startDate;
        Input.temporalStep = timeStepMinutes;
       // PmFAO.defaultAtmosphericPressure = 101.3;
       // PmFAO.doHourly = true;
        
        Output.doPrintOutputPM = true;
        
        while( tempReader.doProcess ) {
            tempReader.nextRecord();

            HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
            Input.inAirTemperature = id2ValueMap;            

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

//            PmFAO.pm = pm;
            Input.process();
            PmFAO.process();
            Output.process();
            
            //OmsTimeSeriesIteratorWriter writerLAtentHeatFAO = new OmsTimeSeriesIteratorWriter();
    		//writerLAtentHeatFAO.file = pathToLatentHeatFAO;
    		//writerLAtentHeatFAO.tStart = startDate;
    		//writerLAtentHeatFAO.tTimestep = timeStepMinutes;
    		//writerLAtentHeatFAO.fileNovalue="-9999";
    		
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
