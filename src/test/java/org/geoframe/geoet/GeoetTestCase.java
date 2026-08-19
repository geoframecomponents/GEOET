package org.geoframe.geoet.testsupport;
import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Base class for GEOET tests, resolving test data through the classpath
 * instead of relative filesystem paths (which break depending on the
 * working directory the tests are launched from).
 */
public abstract class GeoetTestCase {

	/**
	 * Resolves an input resource under src/test/resources to an absolute path.
	 *
	 * @param name resource path from the classpath root, e.g. "/Input/dataET_point/1/dem_1.tif"
	 */
	protected String getRes( String name ) throws IOException, URISyntaxException {
		URL url = this.getClass().getResource(name);
		if (url == null) {
			throw new IOException("Resource not found: " + name);
		}
		return Paths.get(url.toURI()).toString();
	}

	/**
	 * Resolves an output file name to an absolute path inside the Output resource
	 * folder (src/test/resources/Output, copied to the classpath at build time).
	 */
	protected String getOutRes( String name ) throws IOException, URISyntaxException {
		URL url = this.getClass().getResource("/Output");
		if (url == null) {
			throw new IOException("Output folder not found on classpath");
		}
		return Paths.get(url.toURI()).resolve(name).toString();
	}

	protected String getTmpPath(String prefix, String ext) throws Exception {
		File tempFile = Files.createTempFile(prefix, ext).toFile();
		String pathOutput = tempFile.getAbsolutePath();
		return pathOutput;
	}
	
	
	protected OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = timeStepMinutes;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
