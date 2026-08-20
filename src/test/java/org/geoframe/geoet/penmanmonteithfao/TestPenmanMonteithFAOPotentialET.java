package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.inout.InputReaderMain;
import org.geoframe.geoet.inout.OutputWriterMain;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;
/**
 * Test FAO evapotranspiration.
 * 
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestPenmanMonteithFAOPotentialET extends GeoetTestCase{
	
	@Test
    public void Test() throws Exception {
		String startDate= "2014-01-01 00:00";
        String endDate	= "2014-01-02 00:00";
        int timeStepMinutes = 60;
        String fId = "ID";

        //PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
        OmsRasterReader DEMreader = new OmsRasterReader();
        
        DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		//DEMreader.fileNovalue = -9999.0;
		//DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

        String inPathToTemperature 			=getRes("/Input/dataET_point/1/airT_1.csv");
        String inPathToWind 				=getRes("/Input/dataET_point/1/Wind_1.csv");
        String inPathToRelativeHumidity 	=getRes("/Input/dataET_point/1/RH_1.csv");
        String inPathToNetRad 				=getRes("/Input/dataET_point/1/Net_1.csv");
        String inPathToPressure 			=getRes("/Input/dataET_point/1/Pres_1.csv");
        String inPathToSoilHeatFlux 		=getRes("/Input/dataET_point/1/GHF_1.csv");
        String inPathToCentroids 	=getRes("/Input/dataET_point/1/centroids_ID_1.shp");
       // String inPathToSoilMosture 			=getRes("/Input/dataET_point/1/SoilMoisture18.csv");

        
        String pathToEvapotranspirationFAO	=getOutRes("ETpotentialFAO.csv");
        String pathToLatentHeatFAO			=getOutRes("FluxETpotentialFAO.csv");


        OmsTimeSeriesIteratorReader tempReader 			= getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader windReader 			= getTimeseriesReader(inPathToWind, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader humReader 			= getTimeseriesReader(inPathToRelativeHumidity, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader netradReader 		= getTimeseriesReader(inPathToNetRad, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader 		= getTimeseriesReader(inPathToPressure, fId, startDate, endDate,timeStepMinutes);
        // OmsTimeSeriesIteratorReader soilMostureReader 	= getTimeseriesReader(inPathToSoilMosture, fId, startDate, endDate,timeStepMinutes);
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
	

        PenmanMonteithFAOPotentialETSolverMain PmFAO = new PenmanMonteithFAOPotentialETSolverMain();
        InputReaderMain Input 		= new InputReaderMain();
        OutputWriterMain Output 	= new OutputWriterMain();
        
        Input.inCentroids = stationsFC;
		Input.idCentroids= "ID";
		Input.centroidElevation="Elevation";
		Input.inDem = digitalElevationModel; 
		
        //PmFAO.cropCoefficient = 0.75;
        //PmFAO.waterWiltingPoint = 0.05;
        //PmFAO.waterFieldCapacity = 0.27; 
        //PmFAO.rootsDepth = 0.75;
        //PmFAO.depletionFraction = 0.55;
        Input.canopyHeight = 0.12;
        PmFAO.soilFluxParameterDay = 0.35;
        PmFAO.soilFluxParameterNight = 0.75;
        
        Input.tStartDate=startDate;
        Input.temporalStep = timeStepMinutes;
        
        
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
            
            //   soilMostureReader.nextRecord();
            //   id2ValueMap = soilMostureReader.outData;
            //   PmFAO.inSoilMosture = id2ValueMap;
            
            soilHeatFluxReader.nextRecord();
            id2ValueMap = soilHeatFluxReader.outData;
            Input.inSoilFlux = id2ValueMap;

            Input.process();
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
		writerLatentHeatFAO.close();
        writerEvapotranspirationFAO.close();

        assertGoldenDir();
    }


}
