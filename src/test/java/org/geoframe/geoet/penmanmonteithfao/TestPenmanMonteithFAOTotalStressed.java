package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.config.Parameters;
import org.geoframe.geoet.core.state.ProblemQuantities;
import org.geoframe.geoet.io.InputPreprocessor;
import org.geoframe.geoet.io.OutputWriter;
import org.geoframe.geoet.core.state.CurrentStepInput;
import org.geoframe.geoet.solvers.PriestleyTaylorPenmanMonteithFAOStressFactorSolver;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithStressFactor;
/**
 * Test FAO Hourly evapotranspiration.
 * 
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestPenmanMonteithFAOTotalStressed extends GeoetTestCase {

	@Test
	public void Test() throws Exception {
		String startDate = "2013-12-15 00:00";
		String endDate = "2015-12-16 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		CurrentStepInput input = new CurrentStepInput();

		OmsRasterReader DEMreader = new OmsRasterReader();

		DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

		String inPathToTemperature = getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToWind = getRes("/Input/dataET_point/1/Wind_1.csv");
		String inPathToRelativeHumidity = getRes("/Input/dataET_point/1/RH_1.csv");
		String inPathToNetRad = getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToPressure = getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToSoilHeatFlux = getRes("/Input/dataET_point/1/GHF_1.csv");
		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");
		String inPathToSoilMosture = getRes("/Input/dataET_point/1/SoilMoisture18.csv");

		String pathToEvapotranspirationFAO = getOutRes("ETPotentialFAOCavone.csv");
		String pathToLatentHeatFAO = getOutRes("FluxETPotentialFAOCavone.csv");

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
		OmsTimeSeriesIteratorReader soilMoistureReader = getTimeseriesReader(inPathToSoilMosture, fId, startDate,
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

		PenmanMonteithFAOSolverWithStressFactor pmFAO = new PenmanMonteithFAOSolverWithStressFactor();
		pmFAO.parameters = parameters;
		pmFAO.variables = variables;
		pmFAO.input = input;
		PriestleyTaylorPenmanMonteithFAOStressFactorSolver pmStressfactor = new PriestleyTaylorPenmanMonteithFAOStressFactorSolver();
		pmStressfactor.variables = variables;
		pmStressfactor.input = input;

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
		inputPreprocessor.tStartDate = startDate;
		inputPreprocessor.temporalStep = timeStepMinutes;

		pmStressfactor.defaultStress = 1.0;
		pmStressfactor.useRadiationStress = false;
		pmStressfactor.useTemperatureStress = false;
		pmStressfactor.useVDPStress = false;
		pmStressfactor.useWaterStress = false;
		pmStressfactor.alpha = 0.005;
		pmStressfactor.theta = 0.85;
		pmStressfactor.VPD0 = 5.0;
		pmStressfactor.Tl = -5.0;
		pmStressfactor.T0 = 15.0;
		pmStressfactor.Th = 35.0;
		pmStressfactor.waterWiltingPoint = 0.10;
		pmStressfactor.waterFieldCapacity = 0.25;
		pmStressfactor.depth = 1.30;
		pmStressfactor.depletionFraction = 0.70;
		pmStressfactor.cropCoefficient = 0.95;

		inputPreprocessor.canopyHeight = 1.30;
		pmFAO.soilFluxParameterDay = 0.35;
		pmFAO.soilFluxParameterNight = 0.75;

		while (tempReader.doProcess) {
			tempReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
			inputPreprocessor.inAirTemperature = id2ValueMap;
			outputWriter.doPrintOutputPM = true;

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

			inputPreprocessor.process();
			pmStressfactor.solve();
			pmFAO.stressFactor = pmStressfactor.stressSun;
			pmFAO.process();
			outputWriter.process();

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
