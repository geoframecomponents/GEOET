package org.geoframe.geoet.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hortonmachine.dbs.compat.ADb;
import org.hortonmachine.dbs.compat.EDb;
import org.hortonmachine.dbs.compat.IHMPreparedStatement;
import org.hortonmachine.dbs.utils.SqlName;

/**
 * DB-based output handler for GEOET test results, modeled on
 * {@code org.hortonmachine.gears.io.geoframe.whetgeo.Whetgeo1DOutputsHandler}:
 * set the per-step fields and call {@link #write()} once per timestep. Rows
 * are batched internally and flushed every {@code bufferSize} steps (and on
 * {@link #close()}).
 *
 * <p>
 * Table names are prefixed {@value #PREFIX} so a DB viewer can recognize
 * these as GEOET output tables, the same convention
 * {@code Whetgeo1DOutputsHandler} uses ({@code geoframe_whetgeo1d_output_*}).
 *
 * <p>
 * Every per-step output field is independently optional: leaving it
 * {@code null} before the first {@link #write()} omits its column entirely;
 * setting it (boxed {@link Double}, non-null) adds the column. This lets each
 * test opt into exactly the metrics its solver family actually computes,
 * without a fixed enumeration of "modes".
 */
public class GeoetOutputsHandler implements AutoCloseable {

	public static final String PREFIX = "geoframe_geoet";
	public static final String TABLE_OUTPUT_RESULTS = PREFIX + "_output_results";
	/**
	 * Optional table, one row, written once: a snapshot of the input parameters
	 * this run was configured with (e.g. crop coefficient, canopy height, station
	 * elevation/lat/lon, start/end date, timestep - see {@code
	 * GeoetInputsHandler.getParameters()}). Written so the output file is
	 * self-contained even though the parameters themselves were originally read
	 * from a separate input gpkg - without this, nothing connected to just the
	 * output file could know what configuration produced it. Columns are
	 * dynamic, one per parameter key actually set.
	 */
	public static final String TABLE_OUTPUT_PARAMETERS = PREFIX + "_output_parameters";

	public static final String COL_ID = "id";
	public static final String COL_TIMESTAMP = "timestamp";
	public static final String COL_EVAPO_TRANSPIRATION = "evapo_transpiration";
	public static final String COL_FLUX_EVAPO_TRANSPIRATION = "flux_evapo_transpiration";

	// mandatory per-step output - the row key every run always has
	public long timestamp;

	// optional per-step outputs. Each is independently activated by being
	// non-null before the first write() - leave null to omit its column.
	public Double evapoTranspiration;
	public Double fluxEvapoTranspiration;
	public Double evaporation;
	public Double fluxEvaporation;
	public Double transpiration;
	public Double fluxTranspiration;
	public Double latentHeatSun;
	public Double latentHeatShade;
	public Double sensibleHeatSun;
	public Double sensibleHeatShade;
	public Double leafTemperatureSun;
	public Double leafTemperatureShade;
	public Double radiationSun;
	public Double radiationShade;
	public Double radiationSoil;
	public Double canopy;
	public Double vpd;

	/**
	 * Input parameter snapshot, written once on first {@link #write()} if
	 * non-null and non-empty. Values must be {@link String}, {@link Integer} or a
	 * {@link Number} (stored as REAL) - the same convention {@code
	 * GpkgFixtureBuilder} uses for the input {@code parameters} table.
	 */
	public Map<String, Object> parameters;

	/**
	 * When true, existing output tables are dropped and recreated on the first
	 * {@link #write()} call.
	 */
	public boolean dropAndRecreate = false;

	private final ADb db;
	private final int bufferSize;

	private boolean initialized = false;

	private boolean withEvapoTranspiration;
	private boolean withFluxEvapoTranspiration;
	private boolean withEvaporation;
	private boolean withFluxEvaporation;
	private boolean withTranspiration;
	private boolean withFluxTranspiration;
	private boolean withLatentHeatSun;
	private boolean withLatentHeatShade;
	private boolean withSensibleHeatSun;
	private boolean withSensibleHeatShade;
	private boolean withLeafTemperatureSun;
	private boolean withLeafTemperatureShade;
	private boolean withRadiationSun;
	private boolean withRadiationShade;
	private boolean withRadiationSoil;
	private boolean withCanopy;
	private boolean withVpd;

	private List<String> resultCols;
	private String sqlInsertResults;

	private final List<Long> tsBuf = new ArrayList<>();
	private final List<Double[]> valuesBuf = new ArrayList<>();

	public GeoetOutputsHandler(String dbPath, int bufferSize) throws Exception {
		// each test run produces a fresh output gpkg; a stale file from a previous
		// run would otherwise collide with this run's timestamps as duplicate keys
		java.io.File existing = new java.io.File(dbPath);
		if (existing.exists()) {
			existing.delete();
		}
		this.db = EDb.GEOPACKAGE.getDb();
		this.db.open(dbPath);
		this.bufferSize = bufferSize;
	}

	/** Accumulate the current step and flush to DB when the buffer is full. */
	public void write() throws Exception {
		if (!initialized) {
			initialize();
		}

		tsBuf.add(timestamp);
		Double[] row = new Double[resultCols.size() - 1]; // minus timestamp col
		int i = 0;
		if (withEvapoTranspiration)
			row[i++] = evapoTranspiration;
		if (withFluxEvapoTranspiration)
			row[i++] = fluxEvapoTranspiration;
		if (withEvaporation)
			row[i++] = evaporation;
		if (withFluxEvaporation)
			row[i++] = fluxEvaporation;
		if (withTranspiration)
			row[i++] = transpiration;
		if (withFluxTranspiration)
			row[i++] = fluxTranspiration;
		if (withLatentHeatSun)
			row[i++] = latentHeatSun;
		if (withLatentHeatShade)
			row[i++] = latentHeatShade;
		if (withSensibleHeatSun)
			row[i++] = sensibleHeatSun;
		if (withSensibleHeatShade)
			row[i++] = sensibleHeatShade;
		if (withLeafTemperatureSun)
			row[i++] = leafTemperatureSun;
		if (withLeafTemperatureShade)
			row[i++] = leafTemperatureShade;
		if (withRadiationSun)
			row[i++] = radiationSun;
		if (withRadiationShade)
			row[i++] = radiationShade;
		if (withRadiationSoil)
			row[i++] = radiationSoil;
		if (withCanopy)
			row[i++] = canopy;
		if (withVpd)
			row[i++] = vpd;
		valuesBuf.add(row);

		if (tsBuf.size() >= bufferSize) {
			flush();
		}
	}

	/** Flush remaining rows and close. */
	@Override
	public void close() throws Exception {
		flush();
		db.close();
	}

	private static String placeholders(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append("?");
		}
		return sb.toString();
	}

	private void initialize() throws Exception {
		withEvapoTranspiration = (evapoTranspiration != null);
		withFluxEvapoTranspiration = (fluxEvapoTranspiration != null);
		withEvaporation = (evaporation != null);
		withFluxEvaporation = (fluxEvaporation != null);
		withTranspiration = (transpiration != null);
		withFluxTranspiration = (fluxTranspiration != null);
		withLatentHeatSun = (latentHeatSun != null);
		withLatentHeatShade = (latentHeatShade != null);
		withSensibleHeatSun = (sensibleHeatSun != null);
		withSensibleHeatShade = (sensibleHeatShade != null);
		withLeafTemperatureSun = (leafTemperatureSun != null);
		withLeafTemperatureShade = (leafTemperatureShade != null);
		withRadiationSun = (radiationSun != null);
		withRadiationShade = (radiationShade != null);
		withRadiationSoil = (radiationSoil != null);
		withCanopy = (canopy != null);
		withVpd = (vpd != null);

		SqlName resultsTable = SqlName.m(TABLE_OUTPUT_RESULTS);
		SqlName parametersTable = SqlName.m(TABLE_OUTPUT_PARAMETERS);

		if (dropAndRecreate) {
			for (String t : List.of(TABLE_OUTPUT_RESULTS, TABLE_OUTPUT_PARAMETERS)) {
				db.executeInsertUpdateDeleteSql("DROP TABLE IF EXISTS \"" + t + "\"");
			}
		}

		boolean withParameters = (parameters != null && !parameters.isEmpty());
		if (withParameters && !db.hasTable(parametersTable)) {
			List<String> paramFieldDefs = new ArrayList<>();
			paramFieldDefs.add(COL_ID + " INTEGER PRIMARY KEY");
			for (Map.Entry<String, Object> e : parameters.entrySet()) {
				String sqlType = (e.getValue() instanceof String) ? "TEXT"
						: (e.getValue() instanceof Integer) ? "INTEGER" : "REAL";
				paramFieldDefs.add(e.getKey() + " " + sqlType);
			}
			db.createTable(parametersTable, paramFieldDefs.toArray(new String[0]));

			String paramColsCsv = "id, " + String.join(", ", parameters.keySet());
			String sqlParameters = "INSERT INTO " + TABLE_OUTPUT_PARAMETERS + " (" + paramColsCsv + ") VALUES ("
					+ placeholders(parameters.size() + 1) + ")";

			db.execOnConnection(conn -> {
				try (IHMPreparedStatement ps = conn.prepareStatement(sqlParameters)) {
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

		resultCols = new ArrayList<>(List.of(COL_TIMESTAMP));
		if (withEvapoTranspiration)
			resultCols.add(COL_EVAPO_TRANSPIRATION);
		if (withFluxEvapoTranspiration)
			resultCols.add(COL_FLUX_EVAPO_TRANSPIRATION);
		if (withEvaporation)
			resultCols.add("evaporation");
		if (withFluxEvaporation)
			resultCols.add("flux_evaporation");
		if (withTranspiration)
			resultCols.add("transpiration");
		if (withFluxTranspiration)
			resultCols.add("flux_transpiration");
		if (withLatentHeatSun)
			resultCols.add("latent_heat_sun");
		if (withLatentHeatShade)
			resultCols.add("latent_heat_shade");
		if (withSensibleHeatSun)
			resultCols.add("sensible_heat_sun");
		if (withSensibleHeatShade)
			resultCols.add("sensible_heat_shade");
		if (withLeafTemperatureSun)
			resultCols.add("leaf_temperature_sun");
		if (withLeafTemperatureShade)
			resultCols.add("leaf_temperature_shade");
		if (withRadiationSun)
			resultCols.add("radiation_sun");
		if (withRadiationShade)
			resultCols.add("radiation_shade");
		if (withRadiationSoil)
			resultCols.add("radiation_soil");
		if (withCanopy)
			resultCols.add("canopy");
		if (withVpd)
			resultCols.add("vpd");

		if (!db.hasTable(resultsTable)) {
			List<String> fieldDefs = new ArrayList<>();
			for (String c : resultCols) {
				fieldDefs.add(c + (c.equals(COL_TIMESTAMP) ? " INTEGER PRIMARY KEY" : " REAL"));
			}
			db.createTable(resultsTable, fieldDefs.toArray(new String[0]));
		}

		sqlInsertResults = String.format("""
				INSERT INTO %s (%s)
				VALUES (%s)
				""", TABLE_OUTPUT_RESULTS, String.join(", ", resultCols), placeholders(resultCols.size()));

		initialized = true;
	}

	private void flush() throws Exception {
		if (tsBuf.isEmpty())
			return;
		int n = tsBuf.size();

		db.execOnConnection(conn -> {
			boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			try (IHMPreparedStatement ps = conn.prepareStatement(sqlInsertResults)) {
				for (int r = 0; r < n; r++) {
					int pos = 1;
					ps.setLong(pos++, tsBuf.get(r));
					for (Double v : valuesBuf.get(r)) {
						ps.setDouble(pos++, v);
					}
					ps.addBatch();
				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(autoCommit);
			}
			return null;
		});

		tsBuf.clear();
		valuesBuf.clear();
	}
}
