package org.geoframe.geoet.priestleytaylor;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import org.geoframe.geoet.solvers.PriestleyTaylorSolver;
/**
 * Test PrestleyTaylorModel.
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestPriestleyTaylorGEOET extends GeoetTestCase{
	@Test
    public void Test() throws Exception {
		String startDate= "2013-12-15 00:00";
        String endDate	= "2013-12-16 00:00";
        int timeStepMinutes = 60;
        String fId = "ID";
        
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();
        
        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        OmsRasterReader DEMreader = new OmsRasterReader();
        
        DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		//DEMreader.fileNovalue = -9999.0;
		//DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;
		
        String inPathToNetRad 		=getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToTemperature 	=getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToPressure		=getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToCentroids 	=getRes("/Input/dataET_point/1/centroids_ID_1.shp");
        String inPathToSoilHeatFlux =getRes("/Input/dataET_point/1/GHF_1.csv");

		String pathToLatentHeatPT	=getOutRes("latentHeatPtnew.csv");
		String pathToEvapotranspirationPT =getOutRes("etp_PrestleyTaylornew.csv");
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
		
        PriestleyTaylorSolver ptEt = new PriestleyTaylorSolver();
        ptEt.parameters = parameters;
        ptEt.variables = variables;
        ptEt.input = input;
        
		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;
        
        inputPreprocessor.inCentroids = stationsFC;
		inputPreprocessor.idCentroids= "ID";
		inputPreprocessor.centroidElevation="Elevation";
		inputPreprocessor.inDem = digitalElevationModel; 
        
		ptEt.alpha = 1.26;
        ptEt.soilFluxParameterDay = 0.35;
        ptEt.soilFluxParameterNight = 0.75;

        
        //PtEt.doHourly = true;
        inputPreprocessor.temporalStep = timeStepMinutes;
        //PtEt.defaultAtmosphericPressure = 101.3;

        while(tempReader.doProcess ) {
            
        	tempReader.nextRecord();
            HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
            inputPreprocessor.inAirTemperature = id2ValueMap;
            inputPreprocessor.tStartDate=startDate;
            outputWriter.doPrintOutputPT = true;
            
            netradReader.nextRecord();
            id2ValueMap = netradReader.outData;
            inputPreprocessor.inNetRadiation = id2ValueMap;

            pressureReader.nextRecord();
            id2ValueMap = pressureReader.outData;
            inputPreprocessor.inAtmosphericPressure = id2ValueMap;
                      
            soilHeatFluxReader.nextRecord();
            id2ValueMap = soilHeatFluxReader.outData;
            inputPreprocessor.inSoilFlux = id2ValueMap;
            
            inputPreprocessor.process();
            
            ptEt.process();
            
            outputWriter.process();
            
          //HashMap<Integer, double[]> outLatentHeat = PtEt.outLatentHeatPt;
            writerLatentHeatPT.inData = outputWriter.outLatentHeatPT;
            writerLatentHeatPT.writeNextLine();	
            
            if (pathToLatentHeatPT != null) {
            	writerLatentHeatPT.close();
			}
            
           // HashMap<Integer, double[]> outEvapotranspiration= PtEt.outEvapotranspirationPt;
            writerEvapotranspirationPT.inData = outputWriter.outEvapoTranspirationPT;
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

        assertGoldenDir();
    }
}
