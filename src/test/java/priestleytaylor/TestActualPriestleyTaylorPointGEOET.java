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
import it.geoframe.blogspot.geoet.stressfactor.solver.*;

/**
 * Test ActualPrestleyTaylorModel.
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestActualPriestleyTaylorPointGEOET{
	@Test
    public void Test() throws Exception {
		String startDate= "2013-12-15 00:00";
        String endDate	= "2013-12-15 01:00";
        int timeStepMinutes = 60;
        String fId = "ID";
        String lab1 = "test";
        
        
           
		
		String inPathToNetRad 					="resources/Input/dataET_point/Cavone/1/Net_1.csv";
		String inPathToTemperature 				="resources/Input/dataET_point/Cavone/1/airT_1.csv";
		String inPathToPressure 				="resources/Input/dataET_point/Cavone/1/Pres_1.csv";
		String inPathToSoilHeatFlux 			="resources/Input/dataET_point/Cavone/1/GHF_1.csv";
        String inPathToSoilMoisture 			="resources/Input/dataET_point/Cavone/1/Soil_Moisture_Esercitazione_A.csv";

		String pathToLatentHeatPT	="resources/Output/LatentHeatPT_"+lab1+".csv";
		String pathToEvapotranspirationPT ="resources/Output/ETPrestleyTaylor_"+lab1+".csv";
        
		OmsTimeSeriesIteratorReader tempReader = getTimeseriesReader(inPathToTemperature, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader netradReader = getTimeseriesReader(inPathToNetRad, fId, startDate, endDate, timeStepMinutes);
        OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate, timeStepMinutes);      
        OmsTimeSeriesIteratorReader soilHeatFluxReader 	= getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate, endDate,timeStepMinutes);
        OmsTimeSeriesIteratorReader soilMoistureReader 	= getTimeseriesReader(inPathToSoilMoisture, fId, startDate, endDate,timeStepMinutes);
        
		
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
        
        InputReaderMain Input 		= new InputReaderMain();
        PriestleyTaylorActualETSolverMain PtEt = new PriestleyTaylorActualETSolverMain();
        PTPMStressFactorSolverMain PTstressfactor = new PTPMStressFactorSolverMain();
        OutputWriterMain Output 	= new OutputWriterMain();
		
        
        
        Input.elevation= 579;
		//Input.latitude = 37.97;
		//Input.longitude= 13.57;
        

		
		PtEt.alpha = 1.26;
        PtEt.soilFluxParameterDay = 0.35;
        PtEt.soilFluxParameterNight = 0.75;
        Input.temporalStep = timeStepMinutes;

		
        PTstressfactor.useRadiationStress=false;
        PTstressfactor.useTemperatureStress=false;
        PTstressfactor.useVDPStress=false;
        PTstressfactor.useWaterStress=true;
        PTstressfactor.alpha = 0.005;
        PTstressfactor.theta = 0.85;
        PTstressfactor.VPD0 = 5.0;
        PTstressfactor.Tl = -5.0;
        PTstressfactor.T0 = 15.0;
		PTstressfactor.Th = 35.0;
		PTstressfactor.waterWiltingPoint = 0.20;
		PTstressfactor.waterFieldCapacity = 0.35; 
		PTstressfactor.depth = 1.50;
		PTstressfactor.depletionFraction = 0.45;
		PTstressfactor.cropCoefficient = 0.59;
        

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
            
            soilMoistureReader.nextRecord();
            id2ValueMap = soilMoistureReader.outData;
            Input.inSoilMoisture = id2ValueMap;
            
            Input.process();
            
            PTstressfactor.solve();
            
            PtEt.stressFactor = PTstressfactor.stressSun;
            
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
        soilMoistureReader.close();
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
