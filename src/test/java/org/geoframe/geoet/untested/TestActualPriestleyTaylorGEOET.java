package org.geoframe.geoet.untested;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.data.Parameters;
import org.geoframe.geoet.data.ProblemQuantities;
import org.geoframe.geoet.inout.InputReaderMain;
import org.geoframe.geoet.inout.InputTimeSeries;
import org.geoframe.geoet.inout.OutputWriterMain;
import org.geoframe.geoet.priestleytaylor.PriestleyTaylorActualETSolverMain;
import org.geoframe.geoet.stressfactor.solver.PTPMStressFactorSolverMain;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.hortonmachine.gears.io.rasterreader.OmsRasterReader;
import org.hortonmachine.gears.io.shapefile.OmsShapefileFeatureReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

/**
 * Test ActualPrestleyTaylorModel.
 * 
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
//@SuppressWarnings("nls")
public class TestActualPriestleyTaylorGEOET extends GeoetTestCase {
	@Test
	public void Test() throws Exception {
		String startDate = "2013-12-15 00:00";
		String endDate = "2013-12-15 01:00";
		int timeStepMinutes = 60;
		String fId = "ID";
		String lab1 = "test";

		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		OmsRasterReader DEMreader = new OmsRasterReader();
		DEMreader.file = getRes("/Input/dataET_point/1/dem_1.tif");
		DEMreader.process();
		GridCoverage2D digitalElevationModel = DEMreader.outRaster;

		String inPathToNetRad = getRes("/Input/dataET_point/1/Net_1.csv");
		String inPathToTemperature = getRes("/Input/dataET_point/1/airT_1.csv");
		String inPathToPressure = getRes("/Input/dataET_point/1/Pres_1.csv");
		String inPathToSoilHeatFlux = getRes("/Input/dataET_point/1/GHF_1.csv");
		String inPathToSoilMoisture = getRes("/Input/dataET_point/1/Soil_Moisture_Esercitazione_A.csv");

		String pathToLatentHeatPT = getOutRes("LatentHeatPT_") + lab1 + ".csv";
		String pathToEvapotranspirationPT = getOutRes("ETPrestleyTaylor_") + lab1 + ".csv";

		OmsTimeSeriesIteratorReader tempReader = getTimeseriesReader(inPathToTemperature, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader netradReader = getTimeseriesReader(inPathToNetRad, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader pressureReader = getTimeseriesReader(inPathToPressure, fId, startDate, endDate,
				timeStepMinutes);
		OmsTimeSeriesIteratorReader soilHeatFluxReader = getTimeseriesReader(inPathToSoilHeatFlux, fId, startDate,
				endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader soilMoistureReader = getTimeseriesReader(inPathToSoilMoisture, fId, startDate,
				endDate, timeStepMinutes);

		String inPathToCentroids = getRes("/Input/dataET_point/1/centroids_ID_1.shp");
		OmsShapefileFeatureReader centroidsReader = new OmsShapefileFeatureReader();
		centroidsReader.file = inPathToCentroids;
		centroidsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = centroidsReader.geodata;

		OmsTimeSeriesIteratorWriter writerLatentHeatPT = new OmsTimeSeriesIteratorWriter();
		writerLatentHeatPT.file = pathToLatentHeatPT;
		writerLatentHeatPT.tStart = startDate;
		writerLatentHeatPT.tTimestep = timeStepMinutes;
		writerLatentHeatPT.fileNovalue = "-9999";

		OmsTimeSeriesIteratorWriter writerEvapotranspirationPT = new OmsTimeSeriesIteratorWriter();
		writerEvapotranspirationPT.file = pathToEvapotranspirationPT;
		writerEvapotranspirationPT.tStart = startDate;
		writerEvapotranspirationPT.tTimestep = timeStepMinutes;
		writerEvapotranspirationPT.fileNovalue = "-9999";

		PriestleyTaylorActualETSolverMain ptEt = new PriestleyTaylorActualETSolverMain();
		ptEt.parameters = parameters;
		ptEt.variables = variables;
		ptEt.input = input;
		PTPMStressFactorSolverMain ptStressfactor = new PTPMStressFactorSolverMain();
		ptStressfactor.variables = variables;
		ptStressfactor.input = input;
		
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

		ptEt.alpha = 1.26;
		ptEt.soilFluxParameterDay = 0.35;
		ptEt.soilFluxParameterNight = 0.75;
		inputReader.temporalStep = timeStepMinutes;

		ptStressfactor.useRadiationStress = false;
		ptStressfactor.useTemperatureStress = false;
		ptStressfactor.useVDPStress = false;
		ptStressfactor.useWaterStress = true;
		ptStressfactor.alpha = 0.005;
		ptStressfactor.theta = 0.85;
		ptStressfactor.VPD0 = 5.0;
		ptStressfactor.Tl = -5.0;
		ptStressfactor.T0 = 15.0;
		ptStressfactor.Th = 35.0;
		ptStressfactor.waterWiltingPoint = 0.20;
		ptStressfactor.waterFieldCapacity = 0.35;
		ptStressfactor.depth = 1.50;
		ptStressfactor.depletionFraction = 0.45;
		ptStressfactor.cropCoefficient = 0.59;

		while (tempReader.doProcess) {

			tempReader.nextRecord();
			HashMap<Integer, double[]> id2ValueMap = tempReader.outData;
			inputReader.inAirTemperature = id2ValueMap;
			inputReader.tStartDate = startDate;
			outputWriter.doPrintOutputPT = true;

			netradReader.nextRecord();
			id2ValueMap = netradReader.outData;
			inputReader.inNetRadiation = id2ValueMap;

			pressureReader.nextRecord();
			id2ValueMap = pressureReader.outData;
			inputReader.inAtmosphericPressure = id2ValueMap;

			soilHeatFluxReader.nextRecord();
			id2ValueMap = soilHeatFluxReader.outData;
			inputReader.inSoilFlux = id2ValueMap;

			soilMoistureReader.nextRecord();
			id2ValueMap = soilMoistureReader.outData;
			inputReader.inSoilMoisture = id2ValueMap;

			inputReader.process();

			ptStressfactor.solve();

			ptEt.stressFactor = ptStressfactor.stressSun;

			ptEt.process();

			outputWriter.process();

			// HashMap<Integer, double[]> outLatentHeat = PtEt.outLatentHeatPt;
			writerLatentHeatPT.inData = outputWriter.outLatentHeatPT;
			writerLatentHeatPT.writeNextLine();

			if (pathToLatentHeatPT != null) {
				writerLatentHeatPT.close();
			}

			// HashMap<Integer, double[]> outEvapotranspiration=
			// PtEt.outEvapotranspirationPt;
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
		soilMoistureReader.close();
		writerLatentHeatPT.close();
		writerEvapotranspirationPT.close();

	}
}
