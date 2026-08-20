package org.geoframe.geoet;

import org.hortonmachine.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
	 * Resolves an output file name to an absolute path inside a per-test-class
	 * subfolder of the Output resource folder (src/test/resources/Output, copied
	 * to the classpath at build time). Namespacing by test class keeps different
	 * tests that reuse the same output file name (e.g. "Evaporation.csv") from
	 * silently overwriting each other.
	 */
	protected String getOutRes( String name ) throws IOException, URISyntaxException {
		URL url = this.getClass().getResource("/Output");
		if (url == null) {
			throw new IOException("Output folder not found on classpath");
		}
		Path dir = Paths.get(url.toURI()).resolve(this.getClass().getSimpleName());
		Files.createDirectories(dir);
		return dir.resolve(name).toString();
	}

	/**
	 * Compares every file this test just wrote (via {@link #getOutRes}) against a
	 * frozen baseline checked in under src/test/resources/golden/&lt;this test's
	 * simple class name&gt;/. A "Created,&lt;timestamp&gt;" header line (stamped
	 * by OmsTimeSeriesIteratorWriter on every run) is ignored so the comparison
	 * only fails on an actual change in the computed values.
	 * <p>
	 * To (re)capture the baseline after an intentional behavior change, delete
	 * the class's golden folder and copy the freshly written
	 * target/test-classes/Output/&lt;class&gt;/ folder in its place.
	 */
	protected void assertGoldenDir() throws IOException, URISyntaxException {
		String className = this.getClass().getSimpleName();

		URL outUrl = this.getClass().getResource("/Output");
		if (outUrl == null) {
			throw new IOException("Output folder not found on classpath");
		}
		Path actualDir = Paths.get(outUrl.toURI()).resolve(className);

		URL goldenUrl = this.getClass().getResource("/golden/" + className);
		if (goldenUrl == null) {
			fail("No golden baseline for " + className + " at src/test/resources/golden/" + className
					+ " -- capture one from a known-good run before relying on this assertion");
			return;
		}
		Path goldenDir = Paths.get(goldenUrl.toURI());

		Set<String> actualFiles = listFileNames(actualDir);
		Set<String> goldenFiles = listFileNames(goldenDir);
		assertEquals("Set of output files changed for " + className, goldenFiles, actualFiles);

		for (String fileName : goldenFiles) {
			List<String> actualLines = readDataLines(actualDir.resolve(fileName));
			List<String> goldenLines = readDataLines(goldenDir.resolve(fileName));
			assertEquals("Output mismatch in " + fileName + " for " + className, goldenLines, actualLines);
		}
	}

	private static Set<String> listFileNames( Path dir ) throws IOException {
		try (Stream<Path> stream = Files.list(dir)) {
			return stream.map(p -> p.getFileName().toString()).collect(Collectors.toCollection(TreeSet::new));
		}
	}

	/** Reads a file's lines, dropping the run-timestamped "Created," header line. */
	private static List<String> readDataLines( Path file ) throws IOException {
		return Files.readAllLines(file).stream().filter(line -> !line.startsWith("Created,"))
				.collect(Collectors.toList());
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
		reader.fileNovalue = "-9999.0";
		reader.initProcess();
		return reader;
	}
}
