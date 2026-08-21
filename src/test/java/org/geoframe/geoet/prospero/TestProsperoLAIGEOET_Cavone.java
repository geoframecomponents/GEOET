package org.geoframe.geoet.prospero;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.config.Leaf;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.solvers.RadiationSolver;
import org.geoframe.geoet.solvers.ProsperoStressFactorSolver;
import org.geoframe.geoet.solvers.ProsperoSolverWithExplicitLAI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

/**
 * 
 * @author D'Amato Concetta, Michele Bottazzi (concetta.damato@unitn.it)
 * 
 * NOT WORKING
 */
public class TestProsperoLAIGEOET_Cavone extends GeoetTestCase {
	@Test
	public void Test() throws Exception {
		String startDate = "2014-01-01 09:00";
		String endDate = "2014-01-01 14:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		String lab1 = "actual_LAI_20_30928";

		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();
		Leaf leaf = new Leaf();

		////////////////////////////////////////////////////////////////////////////////////////////// int
		////////////////////////////////////////////////////////////////////////////////////////////// stationID
		////////////////////////////////////////////////////////////////////////////////////////////// =
		////////////////////////////////////////////////////////////////////////////////////////////// 1;

		// PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out,
		// System.out);

		OmsRasterReader DEMreader = new OmsRasterReader();
		DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		// DEMreader.fileNovalue = -9999.0;
		// DEMreader.geodataNovalue = Double.NaN;
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

		String inPathToTemperature = getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToWind = getRes("/Input/dataET_point/1/Wind_1.csv");
		String inPathToRelativeHumidity = getRes("/Input/dataET_point/1/RH_1.csv");
		String inPathToShortWaveRadiationDirect = getRes("/Input/dataET_point/1/ShortwaveDirect_1.csv");
		String inPathToShortWaveRadiationDiffuse = getRes("/Input/dataET_point/1/ShortwaveDiffuse_1.csv");
		String inPathToLWRad = getRes("/Input/dataET_point/1/LongDownwelling_1.csv");
		String inPathToNetRad = getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToSoilHeatFlux = getRes("/Input/dataET_point/1/GHF_1.csv");
		String inPathToPressure = getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToLai = getRes("/Input/dataET_point/1/LAI_10.csv");
		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");
		String inPathToSoilMoisture = getRes("/Input/dataET_point/1/SoilMoisture18.csv");

		String outPathToLatentHeatSun = getOutRes("LatentHeatSun") + lab1 + ".csv";
		String outPathToLatentHeatShadow = getOutRes("LatentHeatShadow") + lab1 + ".csv";

		String outPathToFluxTranspiration = getOutRes("FluxTranspiration") + lab1 + ".csv";
		// String outPathToFluxEvapoTranspiration
		// =getOutRes("FluxEvapoTranspiration.csv");
		// String outPathToFluxEvaporation =getOutRes("FluxEvaporation.csv");
		// String outPathToEvapoTranspiration =getOutRes("EvapoTranspiration.csv");
		String outPathToTranspiration = getOutRes("Transpiration") + lab1 + ".csv";
		// String outPathToEvaporation =getOutRes("Evaporation.csv");

		String outPathToLeafTemperatureSun = getOutRes("LeafTemperatureSun") + lab1 + ".csv";
		String outPathToLeafTemperatureShadow = getOutRes("LeafTemperatureSh") + lab1 + ".csv";

		String outPathToSensibleSun = getOutRes("sensibleSun") + lab1 + ".csv";
		String outPathToSensibleShadow = getOutRes("sensibleShadow") + lab1 + ".csv";
		String outPathToRadiationSoil = getOutRes("RadiationSoil") + lab1 + ".csv";
		String outPathToRadiationSun = getOutRes("RadSun") + lab1 + ".csv";
		String outPathToRadiationShadow = getOutRes("RadShadow") + lab1 + ".csv";
		String outPathToCanopy = getOutRes("Canopy") + lab1 + ".csv";
		String outPathToVPD = getOutRes("VPD") + lab1 + ".csv";

		OmsTimeSeriesIteratorReader temperatureReader = getTimeseriesReader(inPathToTemperature, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader windReader = getTimeseriesReader(inPathToWind, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader humidityReader = getTimeseriesReader(inPathToRelativeHumidity, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader shortwaveReaderDirect = getTimeseriesReader(inPathToShortWaveRadiationDirect, fId,
				startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader shortwaveReaderDiffuse = getTimeseriesReader(inPathToShortWaveRadiationDiffuse, fId,
				startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader longwaveReader = getTimeseriesReader(inPathToLWRad, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader leafAreaIndexReader = getTimeseriesReader(inPathToLai, fId, startDate, endDate,
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

		OmsTimeSeriesIteratorWriter latentHeatSunWriter = new OmsTimeSeriesIteratorWriter();
		latentHeatSunWriter.file = outPathToLatentHeatSun;
		latentHeatSunWriter.tStart = startDate;
		latentHeatSunWriter.tTimestep = timeStepMinutes;
		latentHeatSunWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter latentHeatShadowWriter = new OmsTimeSeriesIteratorWriter();
		latentHeatShadowWriter.file = outPathToLatentHeatShadow;
		latentHeatShadowWriter.tStart = startDate;
		latentHeatShadowWriter.tTimestep = timeStepMinutes;
		latentHeatShadowWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter FluxTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		FluxTranspirationWriter.file = outPathToFluxTranspiration;
		FluxTranspirationWriter.tStart = startDate;
		FluxTranspirationWriter.tTimestep = timeStepMinutes;
		FluxTranspirationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter TranspirationWriter = new OmsTimeSeriesIteratorWriter();
		TranspirationWriter.file = outPathToTranspiration;
		TranspirationWriter.tStart = startDate;
		TranspirationWriter.tTimestep = timeStepMinutes;
		TranspirationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter leafTemperatureSunWriter = new OmsTimeSeriesIteratorWriter();
		leafTemperatureSunWriter.file = outPathToLeafTemperatureSun;
		leafTemperatureSunWriter.tStart = startDate;
		leafTemperatureSunWriter.tTimestep = timeStepMinutes;
		leafTemperatureSunWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter leafTemperatureShadowWriter = new OmsTimeSeriesIteratorWriter();
		leafTemperatureShadowWriter.file = outPathToLeafTemperatureShadow;
		leafTemperatureShadowWriter.tStart = startDate;
		leafTemperatureShadowWriter.tTimestep = timeStepMinutes;
		leafTemperatureShadowWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter radiationSunWriter = new OmsTimeSeriesIteratorWriter();
		radiationSunWriter.file = outPathToRadiationSun;
		radiationSunWriter.tStart = startDate;
		radiationSunWriter.tTimestep = timeStepMinutes;
		radiationSunWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter radiationShadowWriter = new OmsTimeSeriesIteratorWriter();
		radiationShadowWriter.file = outPathToRadiationShadow;
		radiationShadowWriter.tStart = startDate;
		radiationShadowWriter.tTimestep = timeStepMinutes;
		radiationShadowWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter sensibleSunWriter = new OmsTimeSeriesIteratorWriter();
		sensibleSunWriter.file = outPathToSensibleSun;
		sensibleSunWriter.tStart = startDate;
		sensibleSunWriter.tTimestep = timeStepMinutes;
		sensibleSunWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter sensibleShadowWriter = new OmsTimeSeriesIteratorWriter();
		sensibleShadowWriter.file = outPathToSensibleShadow;
		sensibleShadowWriter.tStart = startDate;
		sensibleShadowWriter.tTimestep = timeStepMinutes;
		sensibleShadowWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter radiationSoilWriter = new OmsTimeSeriesIteratorWriter();
		radiationSoilWriter.file = outPathToRadiationSoil;
		radiationSoilWriter.tStart = startDate;
		radiationSoilWriter.tTimestep = timeStepMinutes;
		radiationSoilWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter canopyWriter = new OmsTimeSeriesIteratorWriter();
		canopyWriter.file = outPathToCanopy;
		canopyWriter.tStart = startDate;
		canopyWriter.tTimestep = timeStepMinutes;
		canopyWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter vapourPressureDeficitWriter = new OmsTimeSeriesIteratorWriter();
		vapourPressureDeficitWriter.file = outPathToVPD;
		vapourPressureDeficitWriter.tStart = startDate;
		vapourPressureDeficitWriter.tTimestep = timeStepMinutes;
		vapourPressureDeficitWriter.fileNovalue = "-9999";

		RadiationSolver radiation = new RadiationSolver();
		radiation.parameters = parameters;
		radiation.variables = variables;
		radiation.input = input;

		ProsperoStressFactorSolver prosperoStressFactor = new ProsperoStressFactorSolver();
		prosperoStressFactor.variables = variables;
		prosperoStressFactor.input = input;

		ProsperoSolverWithExplicitLAI prospero = new ProsperoSolverWithExplicitLAI();
		prospero.parameters = parameters;
		prospero.variables = variables;
		prospero.input = input;
		prospero.leafparameters = leaf;

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

		inputPreprocessor.canopyHeight = 30;
		prosperoStressFactor.defaultStress = 1.0;

		prosperoStressFactor.useRadiationStress = true;
		prosperoStressFactor.useTemperatureStress = true;
		prosperoStressFactor.useVDPStress = true;
		prosperoStressFactor.useWaterStress = true;

		prosperoStressFactor.alpha = 0.005;
		prosperoStressFactor.theta = 0.9;
		prosperoStressFactor.VPD0 = 5.0;

		prosperoStressFactor.Tl = -5.0;
		prosperoStressFactor.T0 = 20.0;
		prosperoStressFactor.Th = 45.0;
		prospero.typeOfCanopy = "multilayer";
		radiation.typeOfCanopy = "multilayer";
		prosperoStressFactor.waterWiltingPoint = 0.05;
		prosperoStressFactor.waterFieldCapacity = 0.30;
		prosperoStressFactor.rootsDepth = 1.80;
		prosperoStressFactor.depletionFraction = 0.7;
		prosperoStressFactor.cropCoefficient = 0.95;

		while (temperatureReader.doProcess) {
			temperatureReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = temperatureReader.outData;
			inputPreprocessor.inAirTemperature = id2ValueMap;
			// Input.doHourly = true;
			outputWriter.doFullPrint = true;
			// Prospero.typeOfTerrainCover = "FlatSurface";
			inputPreprocessor.tStartDate = startDate;
			inputPreprocessor.temporalStep = timeStepMinutes;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputPreprocessor.inWindVelocity = id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			inputPreprocessor.inRelativeHumidity = id2ValueMap;

			shortwaveReaderDirect.nextRecord();
			id2ValueMap = shortwaveReaderDirect.outData;
			inputPreprocessor.inShortWaveRadiationDirect = id2ValueMap;

			shortwaveReaderDiffuse.nextRecord();
			id2ValueMap = shortwaveReaderDiffuse.outData;
			inputPreprocessor.inShortWaveRadiationDiffuse = id2ValueMap;

			longwaveReader.nextRecord();
			id2ValueMap = longwaveReader.outData;
			inputPreprocessor.inLongWaveRadiation = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputPreprocessor.inSoilFlux = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputPreprocessor.inAtmosphericPressure = id2ValueMap;

			leafAreaIndexReader.nextRecord();
			id2ValueMap = leafAreaIndexReader.outData;
			inputPreprocessor.inLeafAreaIndex = id2ValueMap;

			netRadReader.nextRecord();
			id2ValueMap = netRadReader.outData;
			inputPreprocessor.inNetRadiation = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputPreprocessor.inSoilMoisture = id2ValueMap;

			inputPreprocessor.process();

			radiation.process();

			prosperoStressFactor.solve();

			prospero.stressSun = prosperoStressFactor.stressSun;
			prospero.stressShade = prosperoStressFactor.stressShade;

			prospero.process();

			outputWriter.process();

			latentHeatSunWriter.inData = outputWriter.outLatentHeatSun;
			latentHeatSunWriter.writeNextLine();

			latentHeatShadowWriter.inData = outputWriter.outLatentHeatShade;
			latentHeatShadowWriter.writeNextLine();

			FluxTranspirationWriter.inData = outputWriter.outFluxTranspiration;
			FluxTranspirationWriter.writeNextLine();

			// FluxEvaporationWriter.inData = Output.outFluxEvaporation;
			// FluxEvaporationWriter.writeNextLine();

			// FluxEvapoTranspirationWriter.inData = Output.outFluxEvapoTranspiration;
			// FluxEvapoTranspirationWriter.writeNextLine();

			// EvapoTranspirationWriter.inData = Output.outEvapoTranspiration;
			// EvapoTranspirationWriter.writeNextLine();

			TranspirationWriter.inData = outputWriter.outTranspiration;
			TranspirationWriter.writeNextLine();

			// EvaporationWriter.inData = Output.outEvaporation;
			// EvaporationWriter.writeNextLine();

			leafTemperatureSunWriter.inData = outputWriter.outLeafTemperature;
			leafTemperatureSunWriter.writeNextLine();

			leafTemperatureShadowWriter.inData = outputWriter.outLeafTemperatureShade;
			leafTemperatureShadowWriter.writeNextLine();

			if (outputWriter.doFullPrint == true) {

				radiationSunWriter.inData = outputWriter.outRadiation;
				radiationSunWriter.writeNextLine();

				radiationShadowWriter.inData = outputWriter.outRadiationShade;
				radiationShadowWriter.writeNextLine();

				sensibleSunWriter.inData = outputWriter.outSensibleHeat;
				sensibleSunWriter.writeNextLine();

				sensibleShadowWriter.inData = outputWriter.outSensibleHeatShade;
				sensibleShadowWriter.writeNextLine();

				radiationSoilWriter.inData = outputWriter.outRadiationSoil;
				radiationSoilWriter.writeNextLine();

				canopyWriter.inData = outputWriter.outCanopy;
				canopyWriter.writeNextLine();

				vapourPressureDeficitWriter.inData = outputWriter.outVapourPressureDeficit;
				vapourPressureDeficitWriter.writeNextLine();

				radiationSunWriter.close();
				radiationShadowWriter.close();
				sensibleSunWriter.close();
				sensibleShadowWriter.close();
				radiationSoilWriter.close();
				canopyWriter.close();
				vapourPressureDeficitWriter.close();
			}

		}

		temperatureReader.close();
		windReader.close();
		humidityReader.close();
		shortwaveReaderDirect.close();
		shortwaveReaderDiffuse.close();
		longwaveReader.close();
		soilHeatFluxReader.close();
		pressureReader.close();
		leafAreaIndexReader.close();
		soilMoistureReader.close();

		latentHeatSunWriter.close();
		latentHeatShadowWriter.close();
		// FluxEvapoTranspirationWriter.close();
		// FluxEvaporationWriter.close();
		FluxTranspirationWriter.close();
		// EvapoTranspirationWriter.close();
		TranspirationWriter.close();
		// EvaporationWriter.close();
		leafTemperatureSunWriter.close();
		leafTemperatureShadowWriter.close();

		assertGoldenDir();
	}

}
