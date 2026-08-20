package org.geoframe.geoet.evaporationfromsoil;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.inout.InputReaderMain;
import org.geoframe.geoet.inout.InputTimeSeries;
import org.geoframe.geoet.inout.OutputWriterMain;
import org.geoframe.geoet.soilevaporation.solver.PMEvaporationFromSoilSolverMain;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

/**
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
public class TestPMEvaporationFromSoilGEOET extends GeoetTestCase {
	@Test
	public void Test() throws Exception {
		String startDate = "2014-01-01 09:00";
		String endDate = "2014-01-01 11:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		////////////////////////////////////////////////////////////////////////////////////////////// int
		////////////////////////////////////////////////////////////////////////////////////////////// stationID
		////////////////////////////////////////////////////////////////////////////////////////////// =
		////////////////////////////////////////////////////////////////////////////////////////////// 1;

		OmsRasterReader DEMreader = new OmsRasterReader();
		DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

		String inPathToTemperature = getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToWind = getRes("/Input/dataET_point/1/Wind_1.csv");
		String inPathToRelativeHumidity = getRes("/Input/dataET_point/1/RH_1.csv");
		String inPathToNetRad = getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToSoilHeatFlux = getRes("/Input/dataET_point/1/GHF_1.csv");
		String inPathToPressure = getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");
		String inPathToSoilMoisture = getRes("/Input/dataET_point/1/SoilMoisture18.csv");

		String outPathToFluxEvaporation = getOutRes("FluxEvaporation.csv");
		String outPathToEvaporation = getOutRes("Evaporation.csv");

		OmsTimeSeriesIteratorReader temperatureReader = getTimeseriesReader(inPathToTemperature, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader windReader = getTimeseriesReader(inPathToWind, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader humidityReader = getTimeseriesReader(inPathToRelativeHumidity, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader soilHeatFluxReader = getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader netRadReader = getTimeseriesReader(inPathToNetRad, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader soilMoistureReader = getTimeseriesReader(inPathToSoilMoisture, fId, startDate,
				endDate, timeStepMinutes);

		OmsShapefileFeatureReader centroidsReader = new OmsShapefileFeatureReader();
		centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;

		OmsTimeSeriesIteratorWriter FluxEvaporationWriter = new OmsTimeSeriesIteratorWriter();
		FluxEvaporationWriter.file = outPathToFluxEvaporation;
		FluxEvaporationWriter.tStart = startDate;
		FluxEvaporationWriter.tTimestep = timeStepMinutes;
		FluxEvaporationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter EvaporationWriter = new OmsTimeSeriesIteratorWriter();
		EvaporationWriter.file = outPathToEvaporation;
		EvaporationWriter.tStart = startDate;
		EvaporationWriter.tTimestep = timeStepMinutes;
		EvaporationWriter.fileNovalue = "-9999";

		InputReaderMain inputReader = new InputReaderMain();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		OutputWriterMain outputWriter = new OutputWriterMain();
		outputWriter.variables = variables;
		outputWriter.input = input;

		PMEvaporationFromSoilSolverMain pmSoilevaporation = new PMEvaporationFromSoilSolverMain();
		pmSoilevaporation.parameters = parameters;
		pmSoilevaporation.variables = variables;
		pmSoilevaporation.input = input;

		inputReader.inCentroids = stationsFC;
		inputReader.idCentroids = "ID";
		inputReader.centroidElevation = "Elevation";
		inputReader.inDem = digitalElevationModel;
		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;

		
		while (temperatureReader.doProcess) {
			temperatureReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = temperatureReader.outData;
			inputReader.inAirTemperature = id2ValueMap;
			inputReader.tStartDate = startDate;
			inputReader.temporalStep = timeStepMinutes;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputReader.inWindVelocity = id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			inputReader.inRelativeHumidity = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputReader.inSoilFlux = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputReader.inAtmosphericPressure = id2ValueMap;

			netRadReader.nextRecord();
			id2ValueMap = netRadReader.outData;
			inputReader.inNetRadiation = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputReader.inSoilMoisture = id2ValueMap;

			inputReader.process();

			pmSoilevaporation.process();

			outputWriter.process();

			FluxEvaporationWriter.inData = outputWriter.outFluxEvaporation;
			FluxEvaporationWriter.writeNextLine();

			EvaporationWriter.inData = outputWriter.outEvaporation;
			EvaporationWriter.writeNextLine();

		}

		temperatureReader.close();
		windReader.close();
		humidityReader.close();
		soilHeatFluxReader.close();
		pressureReader.close();
		soilMoistureReader.close();

		FluxEvaporationWriter.close();
		EvaporationWriter.close();

		assertGoldenDir();
	}

}
