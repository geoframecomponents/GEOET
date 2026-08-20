package org.geoframe.geoet.io.gpkg;

import java.util.ArrayList;
import java.util.List;

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
	public static final String TABLE_OUTPUT_METADATA = PREFIX + "_output_metadata";

	public static final String COL_ID = "id";
	public static final String COL_TIMESTAMP = "timestamp";
	public static final String COL_TEST_NAME = "test_name";
	public static final String COL_START_DATE = "start_date";
	public static final String COL_END_DATE = "end_date";
	public static final String COL_TIME_STEP_MINUTES = "time_step_minutes";

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

	// metadata, written once on first write() if set beforehand
	public String testName;
	public String startDate;
	public String endDate;
	public Integer timeStepMinutes;

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
		SqlName metadataTable = SqlName.m(TABLE_OUTPUT_METADATA);

		if (dropAndRecreate) {
			for (String t : List.of(TABLE_OUTPUT_RESULTS, TABLE_OUTPUT_METADATA)) {
				db.executeInsertUpdateDeleteSql("DROP TABLE IF EXISTS \"" + t + "\"");
			}
		}

		boolean withMetadata = (testName != null || startDate != null || endDate != null || timeStepMinutes != null);
		if (withMetadata && !db.hasTable(metadataTable)) {
			db.createTable(metadataTable, COL_ID + " INTEGER PRIMARY KEY", COL_TEST_NAME + " TEXT",
					COL_START_DATE + " TEXT", COL_END_DATE + " TEXT", COL_TIME_STEP_MINUTES + " INTEGER");

			String sqlMetadata = String.format("""
					INSERT INTO %s (%s, %s, %s, %s)
					VALUES (?, ?, ?, ?)
					""", TABLE_OUTPUT_METADATA, COL_TEST_NAME, COL_START_DATE, COL_END_DATE, COL_TIME_STEP_MINUTES);

			db.execOnConnection(conn -> {
				try (IHMPreparedStatement ps = conn.prepareStatement(sqlMetadata)) {
					ps.setString(1, testName);
					ps.setString(2, startDate);
					ps.setString(3, endDate);
					if (timeStepMinutes != null) {
						ps.setInt(4, timeStepMinutes);
					} else {
						ps.setObject(4, null);
					}
					ps.addBatch();
					ps.executeBatch();
				}
				return null;
			});
		}

		resultCols = new ArrayList<>(List.of(COL_TIMESTAMP));
		if (withEvapoTranspiration)
			resultCols.add("evapo_transpiration");
		if (withFluxEvapoTranspiration)
			resultCols.add("flux_evapo_transpiration");
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
