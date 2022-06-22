package priestleytaylor;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
//import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.junit.Test;

import it.geoframe.blogspot.geoet.inout.InputReaderMain;
import it.geoframe.blogspot.geoet.inout.OutputWriterMain;
import it.geoframe.blogspot.geoet.priestleytaylor.*;
//import it.geoframe.blogspot.geoet.stressfactor.solver.PTPMStressFactorSolverMain;

/**
 * Test PrestleyTaylorModel.
 * 
 */
//@SuppressWarnings("nls")
public class TestPriestleyTaylorGEOET{
	@Test
    public void Test() throws Exception {
		String startDate= "2013-12-15 00:00";
        String endDate	= "2013-12-15 01:00";
        int timeStepMinutes = 60;
        String fId = "ID";
        
        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        OmsRasterReader DEMreader = new OmsRasterReader();
        
        DEMreader.file = "resources/Input/dataET_point/Cavone/1/dem_1.tif";
		//DEMreader.fileNovalue = -9999.0;
		//DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;
		
        String inPathToNetRad 		="resources/Input/dataET_point/1/Net_1.csv";
		String inPathToTemperature 	="resources/Input/dataET_point/1/airT_1.csv";
		String inPathToPressure		="resources/Input/dataET_point/1/Pres_1.csv";
		String inPathToCentroids 	="resources/Input/dataET_point/1/centroids_ID_1.shp";
        String inPathToSoilHeatFlux ="resources/Input/dataET_point/1/GHF_1.csv";

		String pathToLatentHeatPT	="resources/Output/latentHeatPt.csv";
		String pathToEvapotranspirationPT ="resources/Output/etp_PrestleyTaylor.csv";
        OmsTimeSeriesIteratorReader tempReader = getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader netradReader = getTimeseriesReader(inPathToNetRad, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate, timeStepMinutes);      
        OmsTimeSeriesIteratorReader soilHeatFluxReader 	= getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate, endDate,timeStepMinutes);

        OmsShapefileFeatureReader centroidsReader 		= new OmsShapefileFeatureReader();
        centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;
		
        OmsTimeSeriesIteratorWriter writerLatentHeatPT = new OmsTimeSeriesIteratorWriter();
        writerLatentHeatPT.file = pathToLatentHeatPT;
        writerLatentHeatPT.tStart = startDate;
        writerLatentHeatPT.tTimestep = timeStepMinutes;
        writerLatentHeatPT.fileNovalue="-9999";
		
        OmsTimeSeriesIteratorWriter writerEvapotranspirationPT = new OmsTimeSeriesIteratorWriter();
        writerEvapotranspirationPT.file = pathToEvapotranspirationPT;
        writerEvapotranspirationPT.tStart = startDate;
        writerEvapotranspirationPT.tTimestep = timeStepMinutes;
        writerEvapotranspirationPT.fileNovalue="-9999";
		
        PriestleyTaylorPotentialETSolverMain PtEt = new PriestleyTaylorPotentialETSolverMain();
        InputReaderMain Input 		= new InputReaderMain();
        OutputWriterMain Output 	= new OutputWriterMain();
        
        Input.inCentroids = stationsFC;
		Input.idCentroids= "ID";
		Input.centroidElevation="Elevation";
		Input.inDem = digitalElevationModel; 
        
		PtEt.alpha = 1.26;
        PtEt.soilFluxParameterDay = 0.35;
        PtEt.soilFluxParameterNight = 0.75;
        //PtEt.doHourly = true;
        Input.temporalStep = timeStepMinutes;
        //PtEt.defaultAtmosphericPressure = 101.3;

        while(tempReader.doProcess ) {
            
        	tempReader.nextRecord();
            HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
            Input.inAirTemperature = id2ValueMap;
            Input.tStartDate=startDate;
            Output.doPrintOutputPT = true;
            
            netradReader.nextRecord();
            id2ValueMap = netradReader.outData;
            Input.inNetRadiation = id2ValueMap;

            pressureReader.nextRecord();
            id2ValueMap = pressureReader.outData;
            Input.inAtmosphericPressure = id2ValueMap;
                      
            soilHeatFluxReader.nextRecord();
            id2ValueMap = soilHeatFluxReader.outData;
            Input.inSoilFlux = id2ValueMap;
            
            Input.process();
            
            PtEt.process();
            
            Output.process();
            
          //HashMap<Integer, double[]> outLatentHeat = PtEt.outLatentHeatPt;
            writerLatentHeatPT.inData = Output.outLatentHeatPT;
            writerLatentHeatPT.writeNextLine();	
            
            if (pathToLatentHeatPT != null) {
            	writerLatentHeatPT.close();
			}
            
           // HashMap<Integer, double[]> outEvapotranspiration= PtEt.outEvapotranspirationPt;
            writerEvapotranspirationPT.inData = Output.outEvapoTranspirationPT;
            writerEvapotranspirationPT.writeNextLine();		
			
			if (pathToEvapotranspirationPT != null) {
				writerEvapotranspirationPT.close();
			}
        }
        tempReader.close();
        netradReader.close();    
        soilHeatFluxReader.close();
        pressureReader.close();
        writerLatentHeatPT.close();
        writerEvapotranspirationPT.close();


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
