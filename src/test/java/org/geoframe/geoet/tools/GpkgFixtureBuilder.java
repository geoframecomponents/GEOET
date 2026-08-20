package org.geoframe.geoet.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hortonmachine.dbs.compat.ADb;
import org.hortonmachine.dbs.compat.EDb;
import org.hortonmachine.dbs.compat.IHMPreparedStatement;
import org.hortonmachine.dbs.utils.SqlName;
import org.hortonmachine.gears.io.timeseries.OmsTimeSeriesReader;
import org.joda.time.DateTime;

/**
 * One-off / reusable tool that builds a GEOET test input GeoPackage
 * ({@code parameters} table + one {@code timeseries_<variable>} table per
 * driving variable, see {@link org.geoframe.geoet.io.GeoetInputsHandler})
 * from the same OMS CSVs and scalar literals a test currently hardcodes. Used
 * to author the checked-in fixture files under
 * {@code src/test/resources/input/gpkg/}; not needed at test-run time (the
 * handler just reads the resulting gpkg).
 */
public class GpkgFixtureBuilder {

	/**
	 * @param outputGpkgPath      path to write; overwritten if it already exists
	 * @param parameters          scalar config, one row in the {@code parameters}
	 *                            table; values must be {@link String},
	 *                            {@link Integer} or a {@link Number} (stored as
	 *                            REAL)
	 * @param timeseriesCsvByColumn map of variable name -> OMS-format CSV path;
	 *                            each becomes its own {@code timeseries_<name>}
	 *                            table, independently spanned/resolved
	 */
	public static void build(String outputGpkgPath, Map<String, Object> parameters,
			Map<String, String> timeseriesCsvByColumn) throws Exception {
		java.io.File out = new java.io.File(outputGpkgPath);
		if (out.exists()) {
			out.delete();
		}

		try (ADb db = EDb.GEOPACKAGE.getDb()) {
			db.open(outputGpkgPath);
			writeParameters(db, parameters);
			writeTimeseries(db, timeseriesCsvByColumn);
		}
	}

	private static void writeParameters(ADb db, Map<String, Object> parameters) throws Exception {
		SqlName table = SqlName.m("parameters");

		List<String> fieldDefs = new ArrayList<>();
		fieldDefs.add("id INTEGER PRIMARY KEY");
		for (Map.Entry<String, Object> e : parameters.entrySet()) {
			String sqlType = (e.getValue() instanceof String) ? "TEXT"
					: (e.getValue() instanceof Integer) ? "INTEGER" : "REAL";
			fieldDefs.add(e.getKey() + " " + sqlType);
		}
		db.createTable(table, fieldDefs.toArray(new String[0]));

		String colsCsv = "id, " + String.join(", ", parameters.keySet());
		StringBuilder placeholders = new StringBuilder("?");
		for (int i = 0; i < parameters.size(); i++) {
			placeholders.append(", ?");
		}
		String sql = "INSERT INTO " + table.fixedDoubleName + " (" + colsCsv + ") VALUES (" + placeholders + ")";

		db.execOnConnection(conn -> {
			try (IHMPreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, 1);
				int pos = 2;
				for (Object v : parameters.values()) {
					if (v instanceof String s) {
						ps.setString(pos++, s);
					} else if (v instanceof Integer i) {
						ps.setInt(pos++, i);
					} else {
						ps.setDouble(pos++, ((Number) v).doubleValue());
					}
				}
				ps.addBatch();
				ps.executeBatch();
			}
			return null;
		});
	}

	/**
	 * Writes one {@code timeseries_<variableName>} table per entry, each
	 * independently spanned/resolved (no cross-variable merge) - matches
	 * {@code Whetgeo1DInputsHandler}'s per-timeseries-table convention.
	 */
	private static void writeTimeseries(ADb db, Map<String, String> timeseriesCsvByColumn) throws Exception {
		for (Map.Entry<String, String> e : timeseriesCsvByColumn.entrySet()) {
			String variableName = e.getKey();
			String csvPath = e.getValue();

			OmsTimeSeriesReader reader = new OmsTimeSeriesReader();
			reader.file = csvPath;
			reader.read();
			reader.close();

			SqlName table = SqlName.m("timeseries_" + variableName);
			db.createTable(table, "timestamp INTEGER PRIMARY KEY", variableName + " REAL");

			String sql = "INSERT INTO " + table.fixedDoubleName + " (timestamp, " + variableName + ") VALUES (?, ?)";

			db.execOnConnection(conn -> {
				boolean autoCommit = conn.getAutoCommit();
				conn.setAutoCommit(false);
				try (IHMPreparedStatement ps = conn.prepareStatement(sql)) {
					for (Map.Entry<DateTime, double[]> row : reader.outData.entrySet()) {
						ps.setLong(1, row.getKey().getMillis());
						ps.setDouble(2, row.getValue()[0]);
						ps.addBatch();
					}
					ps.executeBatch();
					conn.commit();
					conn.setAutoCommit(autoCommit);
				}
				return null;
			});
		}
	}
}
