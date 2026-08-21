package org.geoframe.geoet.evaporationfromsoil;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.io.OutputWriter;

import java.util.HashMap;

import org.geoframe.geoet.solvers.*;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.*;

/**
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
public class TestPMStressedEvaporationFromSoilGEOET extends GeoetTestCase {
	@Test
	public void Test() throws Exception {
		String startDate = "2013-12-15 00:00";
		String endDate = "2013-12-15 02:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

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

		InputPreprocessor inputPreprocessor = new InputPreprocessor();
		inputPreprocessor.parameters = parameters;
		inputPreprocessor.variables = variables;
		inputPreprocessor.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;

		PriestleyTaylorPenmanMonteithFAOStressFactorSolver pmWaterStressFactor = new PriestleyTaylorPenmanMonteithFAOStressFactorSolver();
		pmWaterStressFactor.variables = variables;
		pmWaterStressFactor.input = input;
		PenmanMonteithFAOSoilEvaporationSolver pmSoilevaporation = new PenmanMonteithFAOSoilEvaporationSolver();
		pmSoilevaporation.parameters = parameters;
		pmSoilevaporation.variables = variables;
		pmSoilevaporation.input = input;

		inputPreprocessor.inCentroids = stationsFC;
		inputPreprocessor.idCentroids = "ID";
		inputPreprocessor.centroidElevation = "Elevation";
		inputPreprocessor.inDem = digitalElevationModel;
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		pmWaterStressFactor.defaultStress = 1.0;

		pmWaterStressFactor.useWaterStress = false;
		pmWaterStressFactor.waterWiltingPoint = 0.16;
		pmWaterStressFactor.waterFieldCapacity = 0.27;
		pmWaterStressFactor.depth = 0.25;
		pmWaterStressFactor.depletionFraction = 0.75;

		while (temperatureReader.doProcess) {
			temperatureReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = temperatureReader.outData;
			inputPreprocessor.inAirTemperature = id2ValueMap;
			inputPreprocessor.tStartDate = startDate;
			inputPreprocessor.temporalStep = timeStepMinutes;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputPreprocessor.inWindVelocity = id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			inputPreprocessor.inRelativeHumidity = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputPreprocessor.inSoilFlux = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputPreprocessor.inAtmosphericPressure = id2ValueMap;

			netRadReader.nextRecord();
			id2ValueMap = netRadReader.outData;
			inputPreprocessor.inNetRadiation = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputPreprocessor.inSoilMoisture = id2ValueMap;
			inputPreprocessor.process();

			pmWaterStressFactor.solve();

			pmSoilevaporation.evaporationStressWater = pmWaterStressFactor.stressSun;

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
