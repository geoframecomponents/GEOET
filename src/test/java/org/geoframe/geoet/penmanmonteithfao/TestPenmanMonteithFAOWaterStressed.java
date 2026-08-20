package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.inout.InputReaderMain;
import org.geoframe.geoet.inout.InputTimeSeries;
import org.geoframe.geoet.inout.OutputWriterMain;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

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
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

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

		PenmanMonteithFAOSolverMain pmFAO = new PenmanMonteithFAOSolverMain();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;
		
		InputReaderMain inputReader = new InputReaderMain();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		OutputWriterMain outputWriter = new OutputWriterMain();
		outputWriter.variables = variables;
		outputWriter.input = input;

		inputReader.inCentroids = stationsFC;
		inputReader.idCentroids = "ID";
		inputReader.centroidElevation = "Elevation";
		inputReader.inDem = digitalElevationModel;

		pmFAO.cropCoefficient = 0.75; // 0.75
		// PmFAO.waterWiltingPoint = 0.05;
		// PmFAO.waterFieldCapacity = 0.27;
		pmFAO.rootsDepth = 0.75;
		// PmFAO.depletionFraction = 0.55;
		inputReader.canopyHeight = 0.12;
		pmFAO.soilFluxParameterDay = 0.35;
		pmFAO.soilFluxParameterNight = 0.75;

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;
		// PmFAO.defaultAtmosphericPressure = 101.3;
		// PmFAO.doHourly = true;

		outputWriter.doPrintOutputPM = true;

		while (tempReader.doProcess) {
			tempReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
			inputReader.inAirTemperature = id2ValueMap;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputReader.inWindVelocity = id2ValueMap;

			humReader.nextRecord();
			id2ValueMap = humReader.outData;
			inputReader.inRelativeHumidity = id2ValueMap;

			netradReader.nextRecord();
			id2ValueMap = netradReader.outData;
			inputReader.inNetRadiation = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputReader.inAtmosphericPressure = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputReader.inSoilMoisture = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputReader.inSoilFlux = id2ValueMap;

//            PmFAO.pm = pm;
			inputReader.process();
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
