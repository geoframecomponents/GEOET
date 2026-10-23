package org.geoframe.geoet.io;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
 * WHETGEO-1D's own input handler.
 *
 * <p>
 * {@code AutoCloseable}: closing this closes the underlying connection, but
 * only if it was opened by the {@code String gpkgPath} constructor - if an
 * already-open {@link ADb} was passed in instead, this handler doesn't own
 * it and leaves it open for the caller to close. {@link DbTimeseriesIterator}s
 * handed out by {@link #iterateTimeseries} only close their own statement,
 * not the connection, so this handler should be closed last, after every
 * iterator obtained from it.
 */
public class GeoetInputsHandler implements AutoCloseable {

	public static final String TABLE_PARAMETERS = "parameters";
	public static final String TIMESERIES_TABLE_PREFIX = "timeseries_";
	public static final String COL_ID = "id";
	public static final String COL_TIMESTAMP = "timestamp";

	/** Optional per-cell root-density table - see {@link #rootDensityIC}'s javadoc. */
	public static final String TABLE_ROOT_DENSITY_IC = "root_density_ic";
	public static final String COL_ROOT_DENSITY_IC = "rootDensityIC";

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
	private final boolean ownsDb;
	private final Map<String, Object> parameters = new HashMap<>();

	/**
	 * Per real soil cell (0-indexed, same convention as WHETGEO-1D's own
	 * {@code Whetgeo1DInputsHandler} z/eta), the real root-density profile
	 * {@code RootDensitySolver} needs when configured with {@code
	 * rootDensityModel = "CostantMethod"} (and the raw input to {@code
	 * LinearGrowthMethod}/{@code ExponentialGrowthMethod}, via {@code
	 * InputPreprocessor}) - null if {@link #TABLE_ROOT_DENSITY_IC} is absent
	 * from this run's input gpkg (a run with no root-water-uptake coupling has
	 * no reason to carry it).
	 */
	public double[] rootDensityIC;

	public GeoetInputsHandler(ADb db) {
		this.db = db;
		this.ownsDb = false;
	}

	public GeoetInputsHandler(String gpkgPath) throws Exception {
		this.db = EDb.GEOPACKAGE.getDb();
		this.db.open(gpkgPath);
		this.ownsDb = true;
	}

	/** Closes the underlying connection, but only if this handler opened it itself. */
	@Override
	public void close() throws Exception {
		if (ownsDb) {
			db.close();
		}
	}

	/** Reads the {@code parameters} table's single row, and {@link #rootDensityIC} if present. */
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

		SqlName rootDensityTable = SqlName.m(TABLE_ROOT_DENSITY_IC);
		if (db.hasTable(rootDensityTable)) {
			int n = (int) db.getCount(rootDensityTable);
			rootDensityIC = new double[n];
			String sql = "SELECT " + COL_ID + ", " + COL_ROOT_DENSITY_IC + " FROM " + rootDensityTable.fixedDoubleName
					+ " ORDER BY " + COL_ID;
			db.execOnResultSet(sql, rs -> {
				while (rs.next()) {
					rootDensityIC[rs.getInt(1)] = rs.getDouble(2);
				}
				return null;
			});
		}
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
	 * Parameters stored under a name different from the solver field they fill,
	 * as {parameter name, solver field name}.
	 */
	private static final String[][] FIELD_ALIASES = { //
			{ PARAM_ROOTS_DEPTH, "rootDepth" }, //
			{ PARAM_THETA, "thetaR" }, //
			{ PARAM_TIME_STEP_MINUTES, "temporalStep" }, //
			{ COL_ROOT_DENSITY_IC, "rootIC" } //
	};

	/**
	 * Same as {@link #applyTo(Object, String)}, without a prefix.
	 */
	public List<String> applyTo(Object solver) throws Exception {
		return applyTo(solver, null);
	}

	/**
	 * Set on a solver (or on an {@link InputPreprocessor}, an
	 * {@code ETCurrentStepInput}, ...) the parameters this handler read, and the
	 * {@link #rootDensityIC}. A public field is set when a parameter has its name,
	 * ex. {@code VPD0}, or an alias of it, ex. {@code rootsDepth} for
	 * {@code rootDepth}. Numbers are converted to the type of the field, 0/1 to
	 * boolean.
	 *
	 * <p>
	 * The prefix picks a parameter that is meant for one solver only, when two
	 * solvers have a field with the same name: with prefix "jarvis", the
	 * parameter {@code jarvisAlpha} fills the field {@code alpha}, and wins over
	 * a plain {@code alpha} parameter.
	 *
	 * <p>
	 * A scalar parameter never fills an array field and vice versa, ex. the
	 * {@code theta} parameter fills {@code thetaR}, not the per-cell {@code theta}
	 * array of the Jarvis solver. The simulation dates are left to the caller.
	 *
	 * @param solver the solver, ex. a {@code JarvisStressFactorSolverWithNetRadiation}.
	 * @param prefix the prefix of the solver's own parameters, ex. "jarvis", or
	 *               <code>null</code>.
	 * @return the names of the solver fields that were set.
	 */
	public List<String> applyTo(Object solver, String prefix) throws Exception {
		Map<String, Object> values = new HashMap<>(parameters);
		if (rootDensityIC != null) {
			values.put(COL_ROOT_DENSITY_IC, rootDensityIC);
		}

		List<String> setFields = new ArrayList<>();
		for (Field field : solver.getClass().getFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			String name = field.getName();
			for (String[] alias : FIELD_ALIASES) {
				if (alias[1].equals(name)) {
					name = alias[0];
					break;
				}
			}
			String parameterName = name;
			if (prefix != null && !prefix.isEmpty()) {
				String prefixed = prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				if (values.containsKey(prefixed)) {
					parameterName = prefixed;
				}
			}
			Object value = values.get(parameterName);
			if (value == null || value.getClass().isArray() != field.getType().isArray()) {
				continue;
			}
			field.set(solver, convert(value, field.getType(), parameterName, field.getName()));
			setFields.add(field.getName());
		}
		return setFields;
	}

	private static Object convert(Object value, Class<?> type, String parameterName, String fieldName) {
		if (type.isInstance(value)) {
			return value;
		}
		if (value instanceof Number number) {
			if (type == double.class || type == Double.class) {
				return number.doubleValue();
			}
			if ((type == int.class || type == Integer.class) && number.doubleValue() == number.intValue()) {
				return number.intValue();
			}
			if (type == boolean.class || type == Boolean.class) {
				return number.doubleValue() != 0;
			}
		}
		throw new IllegalArgumentException("The parameter " + parameterName + " = " + value
				+ " can't fill the solver field " + fieldName + ", which is a " + type.getSimpleName());
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
