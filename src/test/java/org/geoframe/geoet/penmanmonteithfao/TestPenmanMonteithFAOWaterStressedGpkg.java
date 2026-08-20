package org.geoframe.geoet.penmanmonteithfao;

import java.util.HashMap;

import org.geoframe.geoet.GeoetTestCase;
import org.geoframe.geoet.core.data.Parameters;
import org.geoframe.geoet.core.data.ProblemQuantities;
import org.geoframe.geoet.io.GeoetInputsHandler;
import org.geoframe.geoet.io.GeoetOutputsHandler;
import org.geoframe.geoet.io.InputReader;
import org.geoframe.geoet.io.OutputWriter;
import org.hortonmachine.gears.io.geopackage.GeopackageTimeseriesIterator;
import org.geoframe.geoet.core.data.InputTimeSeries;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.junit.Test;

import org.geoframe.geoet.solvers.PenmanMonteithFAOSolverWithFAOWaterStress;
/**
 * Test FAO Hourly evapotranspiration, driven by a single input GeoPackage
 * ({@code PenmanMonteithFAOWaterStressed.gpkg} - scalar parameters +
 * driving timeseries in one file) instead of the individual CSVs/DEM/
 * shapefile {@link TestPenmanMonteithFAOWaterStressed} reads. Same solver
 * wiring and same golden-checked CSV outputs as the original; also writes a
 * {@code .gpkg} output as a bonus inspectable/chartable artifact (not
 * golden-checked - see {@link GeoetTestCase#assertGoldenDir()}).
 *
 * @author D'Amato Concetta (concetta.damato@unitn.it)
 */
public class TestPenmanMonteithFAOWaterStressedGpkg extends GeoetTestCase {

	private static final int STATION_ID = 1;

	@Test
	public void Test() throws Exception {
		Parameters parameters = new Parameters();
		ProblemQuantities variables = new ProblemQuantities();
		InputTimeSeries input = new InputTimeSeries();

		GeoetInputsHandler inputs = new GeoetInputsHandler(getRes("/input/gpkg/PenmanMonteithFAOWaterStressed.gpkg"));
		inputs.read();

		String startDate = inputs.getString("startDate");
		String endDate = inputs.getString("endDate");
		int timeStepMinutes = inputs.getInt("timeStepMinutes");

		String pathToEvapotranspirationFAO = getOutRes("ETwaterStressedFAO.csv");
		String pathToLatentHeatFAO = getOutRes("FluxETwaterStressedFAO.csv");
		String pathToOutputGpkg = getOutRes("PenmanMonteithFAOWaterStressed.gpkg");

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

		InputReader inputReader = new InputReader();
		inputReader.parameters = parameters;
		inputReader.variables = variables;
		inputReader.input = input;

		OutputWriter outputWriter = new OutputWriter();
		outputWriter.variables = variables;
		outputWriter.input = input;

		// no DEM/shapefile: elevation/latitude/longitude come straight from the gpkg
		inputReader.elevation = inputs.getDouble("elevation");
		inputReader.latitude = inputs.getDouble("latitude");
		inputReader.longitude = inputs.getDouble("longitude");

		pmFAO.cropCoefficient = inputs.getDouble("cropCoefficient");
		// PmFAO.waterWiltingPoint = 0.05;
		// PmFAO.waterFieldCapacity = 0.27;
		pmFAO.rootsDepth = inputs.getDouble("rootsDepth");
		// PmFAO.depletionFraction = 0.55;
		inputReader.canopyHeight = inputs.getDouble("canopyHeight");
		pmFAO.soilFluxParameterDay = inputs.getDouble("soilFluxParameterDay");
		pmFAO.soilFluxParameterNight = inputs.getDouble("soilFluxParameterNight");

		inputReader.tStartDate = startDate;
		inputReader.temporalStep = timeStepMinutes;
		// PmFAO.defaultAtmosphericPressure = 101.3;
		// PmFAO.doHourly = true;

		outputWriter.doPrintOutputPM = true;

		try (GeopackageTimeseriesIterator tempIt = inputs.iterateTimeseries("airTemperature", startDate, endDate, 1000);
				GeopackageTimeseriesIterator windIt = inputs.iterateTimeseries("windVelocity", startDate, endDate, 1000);
				GeopackageTimeseriesIterator humIt = inputs.iterateTimeseries("relativeHumidity", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator netradIt = inputs.iterateTimeseries("netRadiation", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator pressureIt = inputs.iterateTimeseries("atmosphericPressure", startDate,
						endDate, 1000);
				GeopackageTimeseriesIterator soilMoistureIt = inputs.iterateTimeseries("soilMoisture", startDate, endDate,
						1000);
				GeopackageTimeseriesIterator soilFluxIt = inputs.iterateTimeseries("soilFlux", startDate, endDate, 1000);
				GeoetOutputsHandler outputs = new GeoetOutputsHandler(pathToOutputGpkg, 500)) {
			outputs.testName = "TestPenmanMonteithFAOWaterStressedGpkg";
			outputs.startDate = startDate;
			outputs.endDate = endDate;
			outputs.timeStepMinutes = timeStepMinutes;

			while (tempIt.next()) {
				windIt.next();
				humIt.next();
				netradIt.next();
				pressureIt.next();
				soilMoistureIt.next();
				soilFluxIt.next();

				inputReader.inAirTemperature = one(tempIt.value());
				inputReader.inWindVelocity = one(windIt.value());
				inputReader.inRelativeHumidity = one(humIt.value());
				inputReader.inNetRadiation = one(netradIt.value());
				inputReader.inAtmosphericPressure = one(pressureIt.value());
				inputReader.inSoilMoisture = one(soilMoistureIt.value());
				inputReader.inSoilFlux = one(soilFluxIt.value());

				inputReader.process();
				pmFAO.process();
				outputWriter.process();

				writerLatentHeatFAO.inData = outputWriter.outLatentHeatPM;
				writerLatentHeatFAO.writeNextLine();

				writerEvapotranspirationFAO.inData = outputWriter.outEvapoTranspirationPM;
				writerEvapotranspirationFAO.writeNextLine();

				outputs.timestamp = tempIt.timestamp();
				outputs.evapoTranspiration = outputWriter.outEvapoTranspirationPM.get(STATION_ID)[0];
				outputs.fluxEvapoTranspiration = outputWriter.outLatentHeatPM.get(STATION_ID)[0];
				outputs.write();
			}
		}

		writerLatentHeatFAO.close();
		writerEvapotranspirationFAO.close();

		assertGoldenDir();
	}

	private static HashMap<Integer, double[]> one(double value) {
		HashMap<Integer, double[]> m = new HashMap<>();
		m.put(STATION_ID, new double[] { value });
		return m;
	}

}
