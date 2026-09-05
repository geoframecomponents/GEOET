package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ETProblemQuantities;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.core.state.ETCurrentStepInput;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithFAOWaterStress;
/**
 * Test FAO Hourly evapotranspiration.
 * 
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestPenmanMonteithFAOWaterStressed extends GeoetTestCase {

	@Test
	public void Test() throws Exception {
		String startDate = "2014-01-01 00:00";
		String endDate = "2014-01-02 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		Parameters parameters = new Parameters();
		ETProblemQuantities variables = new ETProblemQuantities();
		ETCurrentStepInput input = new ETCurrentStepInput();

		OmsRasterReader DEMreader = new OmsRasterReader();

		DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

		String inPathToTemperature = getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToWind = getRes("/Input/dataET_point/1/Wind_1.csv");
		String inPathToRelativeHumidity = getRes("/Input/dataET_point/1/RH_1.csv");
		String inPathToNetRad = getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToPressure = getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToSoilHeatFlux = getRes("/Input/dataET_point/1/nan.csv");
		String inPathToSoilMoisture = getRes("/Input/dataET_point/1/SoilMoisture18.csv");
		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");

		String pathToEvapotranspirationFAO = getOutRes("ETwaterStressedFAO.csv");
		String pathToLatentHeatFAO = getOutRes("FluxETwaterStressedFAO.csv");

		OmsTimeSeriesIteratorReader tempReader = getTimeseriesReader(inPathToTemperature, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader windReader = getTimeseriesReader(inPathToWind, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader humReader = getTimeseriesReader(inPathToRelativeHumidity, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader netradReader = getTimeseriesReader(inPathToNetRad, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader soilMoistureReader = getTimeseriesReader(inPathToSoilMoisture, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader soilHeatFluxReader = getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate,
				endDate, timeStepMinutes);

		OmsShapefileFeatureReader centroidsReader = new OmsShapefileFeatureReader();
		centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;

		OmsTimeSeriesIteratorWriter writerEvapotranspirationFAO = new OmsTimeSeriesIteratorWriter();
		writerEvapotranspirationFAO.file = pathToEvapotranspirationFAO;
		writerEvapotranspirationFAO.tStart = startDate;
		writerEvapotranspirationFAO.tTimestep = timeStepMinutes;
		writerEvapotranspirationFAO.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter writerLatentHeatFAO = new OmsTimeSeriesIteratorWriter();
		writerLatentHeatFAO.file = pathToLatentHeatFAO;
		writerLatentHeatFAO.tStart = startDate;
		writerLatentHeatFAO.tTimestep = timeStepMinutes;
		writerLatentHeatFAO.fileNovalue = "-9999";

		PenmanMonteithFAOSolverWithFAOWaterStress pmFAO = new PenmanMonteithFAOSolverWithFAOWaterStress();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;
		
		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;

		inputPreprocessor.inCentroids = stationsFC;
		inputPreprocessor.idCentroids = "ID";
		inputPreprocessor.centroidElevation = "Elevation";
		inputPreprocessor.inDem = digitalElevationModel;

		pmFAO.cropCoefficient = 0.75; // 0.75
		// PmFAO.waterWiltingPoint = 0.05;
		// PmFAO.waterFieldCapacity = 0.27;
		pmFAO.rootsDepth = 0.75;
		// PmFAO.depletionFraction = 0.55;
		inputPreprocessor.canopyHeight = 0.12;
		pmFAO.soilFluxParameterDay = 0.35;
		pmFAO.soilFluxParameterNight = 0.75;

		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;
		// PmFAO.defaultAtmosphericPressure = 101.3;
		// PmFAO.doHourly = true;

		outputWriter.doPrintOutputPM = true;

		while (tempReader.doProcess) {
			tempReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
			inputPreprocessor.inAirTemperature = id2ValueMap;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputPreprocessor.inWindVelocity = id2ValueMap;

			humReader.nextRecord();
			id2ValueMap = humReader.outData;
			inputPreprocessor.inRelativeHumidity = id2ValueMap;

			netradReader.nextRecord();
			id2ValueMap = netradReader.outData;
			inputPreprocessor.inNetRadiation = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputPreprocessor.inAtmosphericPressure = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputPreprocessor.inSoilMoisture = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputPreprocessor.inSoilFlux = id2ValueMap;

//            PmFAO.pm = pm;
			inputPreprocessor.process();
			pmFAO.process();
			outputWriter.process();

			// OmsTimeSeriesIteratorWriter writerLAtentHeatFAO = new
			// OmsTimeSeriesIteratorWriter();
			// writerLAtentHeatFAO.file = pathToLatentHeatFAO;
			// writerLAtentHeatFAO.tStart = startDate;
			// writerLAtentHeatFAO.tTimestep = timeStepMinutes;
			// writerLAtentHeatFAO.fileNovalue="-9999";

			// HashMap<Integer, double[]> outLatentHeat = PmFAO.outLatentHeatFao;
			writerLatentHeatFAO.inData = outputWriter.outLatentHeatPM;
			writerLatentHeatFAO.writeNextLine();

			// HashMap<Integer, double[]> outEvapotranspiration =
			// PmFAO.outEvapotranspirationFao;
			writerEvapotranspirationFAO.inData = outputWriter.outEvapoTranspirationPM;
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

		assertGoldenDir();
	}

}
