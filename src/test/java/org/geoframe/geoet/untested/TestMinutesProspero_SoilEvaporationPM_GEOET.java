package org.geoframe.geoet.untested;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.Leaf;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.geoframe.geoet.solvers.PenmanMonteithFAOSoilEvaporationSolverWithCanopy;
import org.geoframe.geoet.solvers.ProsperoStressFactorSolverWithEvaporation;
import org.geoframe.geoet.solvers.ProsperoSolver;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import org.geoframe.geoet.solvers.TotalEvapoTranspirationSolver;
/**
 * 
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 * 
 * NOT WORKING
 */
public class TestMinutesProspero_SoilEvaporationPM_GEOET extends GeoetTestCase {
	@Test
	public void Test() throws Exception {
		String startDate = "2013-12-15 07:00";
		String endDate = "2013-12-15 12:00";
		int timeStepMinutes = 60; // this needs to be hourly, since the inputs for the test are hourly, otherwise the test will fail.
		String fId = "ID";

		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();
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
		String inPathToLai = getRes("/Input/dataET_point/1/LAI_1.csv");
		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");
		String inPathToSoilMoisture = getRes("/Input/dataET_point/1/SoilMoisture18.csv");

		String outPathToLatentHeatSun = getOutRes("LatentHeatSun.csv");
		String outPathToLatentHeatShadow = getOutRes("LatentHeatShadow.csv");

		String outPathToFluxTranspiration = getOutRes("FluxTranspiration15Min.csv");
		String outPathToFluxEvapoTranspiration = getOutRes("FluxEvapoTranspiration15Min.csv");
		String outPathToFluxEvaporation = getOutRes("FluxEvaporation15Min.csv");
		String outPathToEvapoTranspiration = getOutRes("EvapoTranspiration15Min.csv");
		String outPathToTranspiration = getOutRes("Transpiration15Min.csv");
		String outPathToEvaporation = getOutRes("Evaporation15Min.csv");

		String outPathToLeafTemperatureSun = getOutRes("LeafTemperatureSun.csv");
		String outPathToLeafTemperatureShadow = getOutRes("LeafTemperatureSh.csv");

		String outPathToSensibleSun = getOutRes("sensibleSun.csv");
		String outPathToSensibleShadow = getOutRes("sensibleShadow.csv");
		String outPathToRadiationSoil = getOutRes("RadiationSoil.csv");
		String outPathToRadiationSun = getOutRes("RadSun.csv");
		String outPathToRadiationShadow = getOutRes("RadShadow.csv");
		String outPathToCanopy = getOutRes("Canopy.csv");
		String outPathToVPD = getOutRes("VPD.csv");

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

		OmsTimeSeriesIteratorWriter FluxEvaporationWriter = new OmsTimeSeriesIteratorWriter();
		FluxEvaporationWriter.file = outPathToFluxEvaporation;
		FluxEvaporationWriter.tStart = startDate;
		FluxEvaporationWriter.tTimestep = timeStepMinutes;
		FluxEvaporationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter FluxEvapoTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		FluxEvapoTranspirationWriter.file = outPathToFluxEvapoTranspiration;
		FluxEvapoTranspirationWriter.tStart = startDate;
		FluxEvapoTranspirationWriter.tTimestep = timeStepMinutes;
		FluxEvapoTranspirationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter EvapoTranspirationWriter = new OmsTimeSeriesIteratorWriter();
		EvapoTranspirationWriter.file = outPathToEvapoTranspiration;
		EvapoTranspirationWriter.tStart = startDate;
		EvapoTranspirationWriter.tTimestep = timeStepMinutes;
		EvapoTranspirationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter TranspirationWriter = new OmsTimeSeriesIteratorWriter();
		TranspirationWriter.file = outPathToTranspiration;
		TranspirationWriter.tStart = startDate;
		TranspirationWriter.tTimestep = timeStepMinutes;
		TranspirationWriter.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter EvaporationWriter = new OmsTimeSeriesIteratorWriter();
		EvaporationWriter.file = outPathToEvaporation;
		EvaporationWriter.tStart = startDate;
		EvaporationWriter.tTimestep = timeStepMinutes;
		EvaporationWriter.fileNovalue = "-9999";

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

		PenmanMonteithFAOSoilEvaporationSolverWithCanopy pmSoilevaporation = new PenmanMonteithFAOSoilEvaporationSolverWithCanopy();
		pmSoilevaporation.parameters = parameters;
		pmSoilevaporation.variables = variables;
		pmSoilevaporation.input = input;
		
		TotalEvapoTranspirationSolver totalEvapoTranspiration = new TotalEvapoTranspirationSolver();
		totalEvapoTranspiration.parameters = parameters;
		totalEvapoTranspiration.variables = variables;
		totalEvapoTranspiration.input = input;
		
		ProsperoStressFactorSolverWithEvaporation prosperoStressFactor = new ProsperoStressFactorSolverWithEvaporation();
		prosperoStressFactor.variables = variables;
		prosperoStressFactor.input = input;
		
		ProsperoSolver prospero = new ProsperoSolver();
		prospero.parameters = parameters;
		prospero.variables = variables;
		prospero.input = input;
		prospero.leafparameters = leaf;

		InputReader inputReader = new InputReader();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;

		inputReader.inCentroids = stationsFC;
		inputReader.idCentroids = "ID";
		inputReader.centroidElevation = "Elevation";

		inputReader.inDem = digitalElevationModel;

		inputReader.canopyHeight = 0.2;
		prosperoStressFactor.defaultStress = 1.0;
		// Prospero.doIterative = false;

		prosperoStressFactor.useRadiationStress = false;
		prosperoStressFactor.useTemperatureStress = false;
		prosperoStressFactor.useVDPStress = false;
		prosperoStressFactor.useWaterStress = false;

		prosperoStressFactor.alpha = 0.005;
		prosperoStressFactor.theta = 0.9;
		prosperoStressFactor.VPD0 = 5.0;

		prosperoStressFactor.Tl = -5.0;
		prosperoStressFactor.T0 = 20.0;
		prosperoStressFactor.Th = 45.0;
		prospero.typeOfCanopy = "multilayer";
		prosperoStressFactor.waterWiltingPoint = 0.15;
		prosperoStressFactor.waterFieldCapacity = 0.27;
		prosperoStressFactor.rootsDepth = 0.75;
		prosperoStressFactor.depletionFraction = 0.55;
		prosperoStressFactor.cropCoefficient = 0.75;

		while (temperatureReader.doProcess) {
			temperatureReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = temperatureReader.outData;
			inputReader.inAirTemperature = id2ValueMap;
			// Input.doHourly = true;
			outputWriter.doFullPrint = true;
			// Prospero.typeOfTerrainCover = "FlatSurface";
			inputReader.tStartDate = startDate;
			inputReader.temporalStep = timeStepMinutes;

			windReader.nextRecord();
			id2ValueMap = windReader.outData;
			inputReader.inWindVelocity = id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			inputReader.inRelativeHumidity = id2ValueMap;

			shortwaveReaderDirect.nextRecord();
			id2ValueMap = shortwaveReaderDirect.outData;
			inputReader.inShortWaveRadiationDirect = id2ValueMap;

			shortwaveReaderDiffuse.nextRecord();
			id2ValueMap = shortwaveReaderDiffuse.outData;
			inputReader.inShortWaveRadiationDiffuse = id2ValueMap;

			longwaveReader.nextRecord();
			id2ValueMap = longwaveReader.outData;
			inputReader.inLongWaveRadiation = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputReader.inSoilFlux = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputReader.inAtmosphericPressure = id2ValueMap;

			leafAreaIndexReader.nextRecord();
			id2ValueMap = leafAreaIndexReader.outData;
			inputReader.inLeafAreaIndex = id2ValueMap;

			netRadReader.nextRecord();
			id2ValueMap = netRadReader.outData;
			inputReader.inNetRadiation = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputReader.inSoilMoisture = id2ValueMap;

			inputReader.process();

			pmSoilevaporation.evaporationStressWater = 0.9;
			pmSoilevaporation.process();

			prosperoStressFactor.solve();

			prospero.stressSun = prosperoStressFactor.stressSun;
			prospero.stressShade = prosperoStressFactor.stressShade;
			prospero.process();

			totalEvapoTranspiration.evaporation = pmSoilevaporation.evaporation;
			totalEvapoTranspiration.transpiration = prospero.transpiration;
			totalEvapoTranspiration.process();

			outputWriter.process();

			latentHeatSunWriter.inData = outputWriter.outLatentHeatSun;
			latentHeatSunWriter.writeNextLine();

			latentHeatShadowWriter.inData = outputWriter.outLatentHeatShade;
			latentHeatShadowWriter.writeNextLine();

			FluxTranspirationWriter.inData = outputWriter.outFluxTranspiration;
			FluxTranspirationWriter.writeNextLine();

			FluxEvaporationWriter.inData = outputWriter.outFluxEvaporation;
			FluxEvaporationWriter.writeNextLine();

			FluxEvapoTranspirationWriter.inData = outputWriter.outFluxEvapoTranspiration;
			FluxEvapoTranspirationWriter.writeNextLine();

			EvapoTranspirationWriter.inData = outputWriter.outEvapoTranspiration;
			EvapoTranspirationWriter.writeNextLine();

			TranspirationWriter.inData = outputWriter.outTranspiration;
			TranspirationWriter.writeNextLine();

			EvaporationWriter.inData = outputWriter.outEvaporation;
			EvaporationWriter.writeNextLine();

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
		FluxEvapoTranspirationWriter.close();
		FluxEvaporationWriter.close();
		FluxTranspirationWriter.close();
		EvapoTranspirationWriter.close();
		TranspirationWriter.close();
		EvaporationWriter.close();
		leafTemperatureSunWriter.close();
		leafTemperatureShadowWriter.close();

	}

}
