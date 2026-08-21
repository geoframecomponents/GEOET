package org.geoframe.geoet.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hortonmachine.dbs.compat.ADb;
import org.hortonmachine.dbs.compat.EDb;
import org.hortonmachine.dbs.utils.SqlName;
import org.hortonmachine.dbs.utils.DbTimeseriesIterator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Reads a GEOET test's input GeoPackage: one {@code parameters} row (scalar
 * config, e.g. crop coefficient, canopy height, station elevation/lat/lon)
 * and one {@code timeseries_<variable>} table per driving variable (e.g.
 * {@code timeseries_airTemperature}), each independently spanned/resolved -
 * the same convention and {@code ADb}/{@code SqlName}-based approach as
 * {@code org.hortonmachine.gears.io.geoframe.whetgeo.Whetgeo1DInputsHandler}.
 *
 * <p>
 * Deliberately not {@code AutoCloseable}: {@link #iterateTimeseries} hands
 * out a {@link DbTimeseriesIterator} that closes the handler's own
 * shared connection when the iterator itself is closed (same as
 * {@code Whetgeo1DInputsHandler}, for the same reason) - closing the handler
 * afterwards would double-close it.
 */
public class GeoetInputsHandler {

	public static final String TABLE_PARAMETERS = "parameters";
	public static final String TIMESERIES_TABLE_PREFIX = "timeseries_";
	public static final String COL_ID = "id";
	public static final String COL_TIMESTAMP = "timestamp";

	public static final String PARAM_START_DATE = "startDate";
	public static final String PARAM_END_DATE = "endDate";
	public static final String PARAM_TIME_STEP_MINUTES = "timeStepMinutes";
	public static final String PARAM_ELEVATION = "elevation";
	public static final String PARAM_LATITUDE = "latitude";
	public static final String PARAM_LONGITUDE = "longitude";
	public static final String PARAM_CANOPY_HEIGHT = "canopyHeight";
	public static final String PARAM_TYPE_OF_CANOPY = "typeOfCanopy";
	public static final String PARAM_CROP_COEFFICIENT = "cropCoefficient";
	public static final String PARAM_ROOTS_DEPTH = "rootsDepth";
	public static final String PARAM_SOIL_FLUX_PARAMETER_DAY = "soilFluxParameterDay";
	public static final String PARAM_SOIL_FLUX_PARAMETER_NIGHT = "soilFluxParameterNight";
	public static final String PARAM_DEFAULT_STRESS = "defaultStress";
	public static final String PARAM_USE_RADIATION_STRESS = "useRadiationStress";
	public static final String PARAM_USE_TEMPERATURE_STRESS = "useTemperatureStress";
	public static final String PARAM_USE_VDP_STRESS = "useVDPStress";
	public static final String PARAM_USE_WATER_STRESS = "useWaterStress";
	public static final String PARAM_ALPHA = "alpha";
	public static final String PARAM_THETA = "theta";
	public static final String PARAM_VPD0 = "VPD0";
	public static final String PARAM_TL = "Tl";
	public static final String PARAM_T0 = "T0";
	public static final String PARAM_TH = "Th";
	public static final String PARAM_WATER_WILTING_POINT = "waterWiltingPoint";
	public static final String PARAM_WATER_FIELD_CAPACITY = "waterFieldCapacity";
	public static final String PARAM_DEPTH = "depth";
	public static final String PARAM_DEPLETION_FRACTION = "depletionFraction";
	public static final String PARAM_EVAPORATION_DEPTH = "evaporationDepth";

	public static final String VAR_AIR_TEMPERATURE = "airTemperature";
	public static final String VAR_WIND_VELOCITY = "windVelocity";
	public static final String VAR_RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String VAR_SHORT_WAVE_RADIATION_DIRECT = "shortWaveRadiationDirect";
	public static final String VAR_SHORT_WAVE_RADIATION_DIFFUSE = "shortWaveRadiationDiffuse";
	public static final String VAR_LONG_WAVE_RADIATION = "longWaveRadiation";
	public static final String VAR_NET_RADIATION = "netRadiation";
	public static final String VAR_ATMOSPHERIC_PRESSURE = "atmosphericPressure";
	public static final String VAR_SOIL_FLUX = "soilFlux";
	public static final String VAR_SOIL_MOISTURE = "soilMoisture";
	public static final String VAR_LEAF_AREA_INDEX = "leafAreaIndex";

	private final ADb db;
	private final Map<String, Object> parameters = new HashMap<>();

	public GeoetInputsHandler(ADb db) {
		this.db = db;
	}

	public GeoetInputsHandler(String gpkgPath) throws Exception {
		this.db = EDb.GEOPACKAGE.getDb();
		this.db.open(gpkgPath);
	}

	/** Reads the {@code parameters} table's single row into memory. */
	public void read() throws Exception {
		SqlName table = SqlName.m(TABLE_PARAMETERS);
		Set<String> cols = new HashSet<>();
		for (String[] col : db.getTableColumns(table)) {
			cols.add(col[0]);
		}
		cols.remove(COL_ID);

		db.execOnResultSet("SELECT * FROM " + table.fixedDoubleName + " LIMIT 1", rs -> {
			if (rs.next()) {
				for (String col : cols) {
					parameters.put(col, rs.getObject(columnIndex(rs, col)));
				}
			}
			return null;
		});
	}

	private static int columnIndex(org.hortonmachine.dbs.compat.IHMResultSet rs, String colName) throws Exception {
		int count = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= count; i++) {
			if (rs.getMetaData().getColumnName(i).equalsIgnoreCase(colName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Column not found: " + colName);
	}

	/** Returns a parameter as a double, throwing if absent. */
	public double getParameterDouble(String name) {
		Object v = requireParam(name);
		return ((Number) v).doubleValue();
	}

	/** Returns a parameter as an int, throwing if absent. */
	public int getParameterInt(String name) {
		Object v = requireParam(name);
		return ((Number) v).intValue();
	}

	/** Returns a parameter as a String, throwing if absent. */
	public String getParameterString(String name) {
		Object v = requireParam(name);
		return String.valueOf(v);
	}

	private Object requireParam(String name) {
		if (!parameters.containsKey(name)) {
			throw new IllegalArgumentException("Parameter not found in gpkg 'parameters' table: " + name);
		}
		return parameters.get(name);
	}

	/**
	 * The full set of parameters read from the {@code parameters} table, e.g. to
	 * snapshot them into an output gpkg via {@link GeoetOutputsHandler#parameters}
	 * so the output file is self-contained even though the parameters themselves
	 * were originally read from a separate input gpkg.
	 */
	public Map<String, Object> getParameters() {
		return new HashMap<>(parameters);
	}

	/**
	 * Buffered cursor over one variable's {@code timeseries_<variableName>}
	 * table, filtered to a date range. Advance the iterators for every variable
	 * a test needs in lockstep - each is independent, so nothing requires them
	 * to share a common span or resolution.
	 *
	 * @param variableName e.g. {@code "airTemperature"}; resolves to table
	 *                     {@code timeseries_airTemperature}
	 * @param startDate    inclusive start, format {@code "yyyy-MM-dd HH:mm"}
	 *                     (UTC), or null for no lower limit
	 * @param endDate      inclusive end, format {@code "yyyy-MM-dd HH:mm"} (UTC),
	 *                     or null for no upper limit
	 */
	public DbTimeseriesIterator iterateTimeseries(String variableName, String startDate, String endDate,
			int bufferSize) throws Exception {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZoneUTC();
		Long startMillis = (startDate != null) ? fmt.parseDateTime(startDate).getMillis() : null;
		Long endMillis = (endDate != null) ? fmt.parseDateTime(endDate).getMillis() : null;
		// GpkgFixtureBuilder always names the value column after the variable itself
		return new DbTimeseriesIterator(db, TIMESERIES_TABLE_PREFIX + variableName, COL_TIMESTAMP,
				variableName, startMillis, endMillis, bufferSize);
	}
}
