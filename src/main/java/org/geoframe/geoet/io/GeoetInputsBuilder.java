package org.geoframe.geoet.io;

import static org.geoframe.geoet.io.GeoetInputsHandler.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hortonmachine.dbs.compat.ADb;
import org.hortonmachine.dbs.compat.IHMPreparedStatement;
import org.hortonmachine.dbs.utils.SqlName;
import org.hortonmachine.gears.io.timeseries.OmsTimeSeriesReader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Writes the GEOET input tables read by {@link GeoetInputsHandler} straight
 * into a database.
 *
 * <p>
 * Collect the pieces with the chainable methods, then {@link #write()} them
 * all at once:
 *
 * <pre>
 * new GeoetInputsBuilder(db)
 *     .parameters(Map.of("elevation", 395.7, "rootsDepth", -2.0, "useWaterStress", 1))
 *     .parameter("jarvisAlpha", 0.005)
 *     .rootDensity(rootDensityIC)
 *     .timeseriesFromCsv(VAR_AIR_TEMPERATURE, "AirTemperature_hourly.csv")
 *     .write();
 * </pre>
 *
 * <p>
 * The tables can live in the same database as the WHETGEO-1D ones, their
 * names don't collide.
 *
 * @author Andrea Antonello (https://g-ant.eu)
 */
public class GeoetInputsBuilder {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
			.withZoneUTC();

	private final ADb db;

	private final Map<String, Object> parameters = new LinkedHashMap<>();
	private double[] rootDensity;
	private final Map<String, Object[]> timeseries = new LinkedHashMap<>(); // variable -> {long[], double[]}

	/**
	 * @param db the database to write into, already open. It is not closed by the builder.
	 */
	public GeoetInputsBuilder(ADb db) {
		this.db = db;
	}

	// ---------------------------------------------------------------- parameters

	/**
	 * Add a parameter, see the {@code PARAM_*} names of {@link GeoetInputsHandler}.
	 * A parameter meant for one solver only can carry its prefix, ex. "jarvisAlpha",
	 * see {@link GeoetInputsHandler#applyTo(Object, String)}.
	 *
	 * @param name  the parameter name.
	 * @param value a number or a text, ex. 0/1 for the {@code use*Stress} switches.
	 * @return the builder.
	 */
	public GeoetInputsBuilder parameter(String name, Object value) {
		if (!(value instanceof Number) && !(value instanceof CharSequence)) {
			throw new IllegalArgumentException("Parameter " + name + " must be a number or a text, got: " + value);
		}
		if (parameters.containsKey(name)) {
			throw new IllegalArgumentException("Parameter " + name + " was already added.");
		}
		parameters.put(name, value instanceof CharSequence ? value.toString() : value);
		return this;
	}

	/**
	 * Add several parameters, see {@link #parameter(String, Object)}.
	 *
	 * <p>
	 * From groovy, named arguments make it read like a table row:
	 * {@code parameters(elevation: 395.7, rootsDepth: -2.0, useWaterStress: 1)}.
	 *
	 * @param values the values by parameter name.
	 * @return the builder.
	 */
	public GeoetInputsBuilder parameters(Map<String, ?> values) {
		values.forEach(this::parameter);
		return this;
	}

	// ---------------------------------------------------------------- roots

	/**
	 * Set the root density profile, one value per real soil cell, ordered bottom
	 * to top like the WHETGEO-1D cells.
	 *
	 * @param values the root density of each cell [-].
	 * @return the builder.
	 */
	public GeoetInputsBuilder rootDensity(double[] values) {
		rootDensity = values;
		return this;
	}

	// ---------------------------------------------------------------- timeseries

	/**
	 * Add the timeseries of a driving variable.
	 *
	 * @param variable the variable name, ex. {@link GeoetInputsHandler#VAR_AIR_TEMPERATURE}.
	 * @param times    the times, epoch milliseconds UTC.
	 * @param values   the values, one per time.
	 * @return the builder.
	 */
	public GeoetInputsBuilder timeseries(String variable, long[] times, double[] values) {
		if (times.length != values.length) {
			throw new IllegalArgumentException(
					"Timeseries " + variable + " has " + times.length + " times but " + values.length + " values.");
		}
		if (timeseries.containsKey(variable)) {
			throw new IllegalArgumentException("Timeseries " + variable + " was already added.");
		}
		timeseries.put(variable, new Object[] { times, values });
		return this;
	}

	/**
	 * Add a timeseries with the same value at every step.
	 *
	 * @param variable    the variable name.
	 * @param startDate   the first time, "yyyy-MM-dd HH:mm" UTC.
	 * @param endDate     the last time, included, "yyyy-MM-dd HH:mm" UTC.
	 * @param stepMinutes the time step [min].
	 * @param value       the value.
	 * @return the builder.
	 */
	public GeoetInputsBuilder constantTimeseries(String variable, String startDate, String endDate, int stepMinutes,
			double value) {
		if (stepMinutes <= 0) {
			throw new IllegalArgumentException("The time step must be positive, got " + stepMinutes);
		}
		long start = DATE_FORMATTER.parseDateTime(startDate).getMillis();
		long end = DATE_FORMATTER.parseDateTime(endDate).getMillis();
		if (end < start) {
			throw new IllegalArgumentException("The end date " + endDate + " is before the start date " + startDate);
		}
		long step = stepMinutes * 60_000L;
		int count = (int) ((end - start) / step) + 1;
		long[] times = new long[count];
		double[] values = new double[count];
		for (int i = 0; i < count; i++) {
			times[i] = start + i * step;
			values[i] = value;
		}
		return timeseries(variable, times, values);
	}

	/**
	 * Add a timeseries from an OMS timeseries CSV file (the {@code @H} format),
	 * which must have exactly one value column.
	 *
	 * @param variable the variable name.
	 * @param csvPath  the CSV file.
	 * @return the builder.
	 */
	public GeoetInputsBuilder timeseriesFromCsv(String variable, String csvPath) throws Exception {
		File csv = new File(csvPath);
		String[] headerParts = null;
		for (String line : Files.readAllLines(csv.toPath())) {
			if (line.startsWith("@H,")) {
				headerParts = line.split(",", -1);
				break;
			}
		}
		if (headerParts == null) {
			throw new IllegalArgumentException("No @H header line found in " + csv.getName());
		}
		// headerParts: [@H, timestamp, value_<id>]
		int numValueCols = headerParts.length - 2;
		if (numValueCols != 1) {
			throw new IllegalArgumentException(
					"Expected exactly one value column in " + csv.getName() + ", found " + numValueCols);
		}

		OmsTimeSeriesReader reader = new OmsTimeSeriesReader();
		reader.file = csv.getAbsolutePath();
		reader.read();
		reader.close();

		int size = reader.outData.size();
		long[] times = new long[size];
		double[] values = new double[size];
		int i = 0;
		for (Map.Entry<DateTime, double[]> entry : reader.outData.entrySet()) {
			times[i] = entry.getKey().getMillis();
			values[i] = entry.getValue()[0];
			i++;
		}
		return timeseries(variable, times, values);
	}

	// ---------------------------------------------------------------- write

	/**
	 * Write the collected pieces, each into its own table. A table that does not
	 * exist is created, a table that already holds rows is an error.
	 */
	public void write() throws Exception {
		if (!parameters.isEmpty()) {
			List<String> fieldDefs = new ArrayList<>();
			List<String> columns = new ArrayList<>();
			fieldDefs.add(COL_ID + " INTEGER PRIMARY KEY");
			columns.add(COL_ID);
			Object[] row = new Object[parameters.size() + 1];
			row[0] = 1;
			int i = 1;
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				Object value = entry.getValue();
				String sqlType = value instanceof String ? "TEXT"
						: value instanceof Integer || value instanceof Long ? "INTEGER" : "REAL";
				fieldDefs.add(entry.getKey() + " " + sqlType);
				columns.add(entry.getKey());
				row[i++] = value;
			}
			insert(TABLE_PARAMETERS, fieldDefs.toArray(new String[0]), columns.toArray(new String[0]),
					List.<Object[]>of(row));
		}
		if (rootDensity != null) {
			List<Object[]> rows = new ArrayList<>(rootDensity.length);
			for (int i = 0; i < rootDensity.length; i++) {
				rows.add(new Object[] { i, rootDensity[i] });
			}
			insert(TABLE_ROOT_DENSITY_IC, new String[] { COL_ID + " INTEGER PRIMARY KEY", COL_ROOT_DENSITY_IC + " REAL" },
					new String[] { COL_ID, COL_ROOT_DENSITY_IC }, rows);
		}
		for (Map.Entry<String, Object[]> entry : timeseries.entrySet()) {
			String variable = entry.getKey();
			long[] times = (long[]) entry.getValue()[0];
			double[] values = (double[]) entry.getValue()[1];
			List<Object[]> rows = new ArrayList<>(times.length);
			for (int i = 0; i < times.length; i++) {
				rows.add(new Object[] { times[i], values[i] });
			}
			insert(TIMESERIES_TABLE_PREFIX + variable,
					new String[] { COL_TIMESTAMP + " INTEGER PRIMARY KEY", variable + " REAL" },
					new String[] { COL_TIMESTAMP, variable }, rows);
		}
	}

	private void insert(String tableName, String[] fieldDefs, String[] columns, List<Object[]> rows)
			throws Exception {
		SqlName table = SqlName.m(tableName);
		if (!db.hasTable(table)) {
			db.createTable(table, fieldDefs);
		} else if (db.getCount(table) > 0) {
			throw new IllegalStateException("The table " + tableName + " already holds data.");
		}

		String sql = "INSERT INTO " + table.fixedDoubleName + " (" + String.join(", ", columns) + ") VALUES ("
				+ "?, ".repeat(columns.length - 1) + "?)";
		db.execOnConnection(conn -> {
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			try (IHMPreparedStatement pStmt = conn.prepareStatement(sql)) {
				for (Object[] row : rows) {
					for (int i = 0; i < row.length; i++) {
						Object value = row[i];
						if (value instanceof Double d) {
							pStmt.setDouble(i + 1, d);
						} else if (value instanceof Integer n) {
							pStmt.setInt(i + 1, n);
						} else if (value instanceof Long n) {
							pStmt.setLong(i + 1, n);
						} else if (value instanceof Number n) {
							pStmt.setDouble(i + 1, n.doubleValue());
						} else {
							pStmt.setString(i + 1, String.valueOf(value));
						}
					}
					pStmt.addBatch();
				}
				pStmt.executeBatch();
				conn.commit();
			} finally {
				conn.setAutoCommit(autoCommit);
			}
			return null;
		});
	}
}
