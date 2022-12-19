package it.geoframe.blogspot.geoet.inout;

import static java.lang.Math.pow;

//import java.util.Arrays;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hortonmachine.gears.utils.CrsUtilities;
import org.hortonmachine.gears.utils.geometry.GeometryUtilities;
import org.hortonmachine.hmachine.i18n.HortonMessageHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import it.geoframe.blogspot.geoet.data.Parameters;
import it.geoframe.blogspot.geoet.data.ProblemQuantities;
import it.geoframe.blogspot.geoet.prospero.data.*;
import it.geoframe.blogspot.geoet.prospero.methods.*;
//import it.geoframe.blogspot.geoet.stressfactor.methods.*;



@Description("")

@Author(name = "Concetta D'Amato, Michele Bottazzi", contact = "concetta.damato@unitn.it")
@Keywords("Reading input")
@Label("")
@Name("")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class InputReaderMain {
	
	/////////////////////////////////////////////
	// ENVIRONMENTAL VARIABLES - INPUT
	/////////////////////////////////////////////

	@Description("Air temperature.")
	@In
	@Unit("K")
	public HashMap<Integer, double[]> inAirTemperature;
	
	@Description("The wind speed.")
	@In
	@Unit("m s-1")
	public HashMap<Integer, double[]> inWindVelocity;
	
	@Description("The air relative humidity.")
	@In
	@Unit("%")
	public HashMap<Integer, double[]> inRelativeHumidity;
	
	@Description("The short wave radiation at the surface.")
	@In
	@Unit("W m-2")
	public HashMap<Integer, double[]> inShortWaveRadiationDirect;
	
	@Description("The short wave radiation at the surface.")
	@In
	@Unit("W m-2")
	public HashMap<Integer, double[]> inShortWaveRadiationDiffuse;
	
	@Description("The long wave radiation at the surface.")
	@In
	@Unit("W m-2")
	public HashMap<Integer, double[]> inLongWaveRadiation;
	
	@Description("The Net long wave radiation at the surface.")
	@In
	@Unit("W m-2")
	public HashMap<Integer, double[]> inNetRadiation;
	
	@Description("The net Radiation default value in case of missing data.")
    @In
    @Unit("W m-2")
    public double defaultNetRadiation = 200.0;

	@Description("The atmospheric pressure.")
	@In
	@Unit("Pa")
	public HashMap<Integer, double[]> inAtmosphericPressure;

	@Description("The soilflux.")
	@In
	@Unit("W m-2")
	public HashMap<Integer, double[]> inSoilFlux;
	
	@Description("Leaf area index.")
	@In
	@Unit("m2 m-2")
	public HashMap<Integer, double[]> inLeafAreaIndex;
	
	@Description("Input soil moisture.")
	@In
	@Unit("m3 m-3")
	public HashMap<Integer, double[]> inSoilMoisture;
	
	@Description("The elevation of the centroid.")
	@In
	@Unit("m")
	public String centroidElevation;
	
	@Description("The shape file with the station measuremnts")
	@In
	public SimpleFeatureCollection inCentroids;
	
	@Description("The name of the field containing the ID of the station in the shape file")
	@In
	public String idCentroids;
	
	@Description("The map of the Digital Elevation Model")
	@In
	public GridCoverage2D inDem;
	
	@Description("the linked HashMap with the coordinate of the stations")
	LinkedHashMap<Integer, Coordinate> stationCoordinates;
	
	@Description("Final target CRS")
	CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
	
	@In public double canopyHeight;
	
	@In
	public String typeOfCanopy;
	
	
	@Description("Type of transpiring area")
	@In
	public String typeOfTerrainCover;

	@Description("The first day of the simulation.")
	@In
	public String tStartDate;
	public DateTime date;
	
	@Description("The first day of the simulation.")
	@In
	public int temporalStep;
	
	@Description("It is needed to iterate on the date")
	int step;
	
	
	double nullValue = -9999.0;
	
	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.UTC);

	
	@Description("The station ID in the timeseries file")
	@In
	@Unit ("-")
	public int ID;
	
	@Out 
	public boolean  doProcess1 = false;


	PressureMethods pressure = new PressureMethods(); 
	
	private HortonMessageHandler msg = HortonMessageHandler.getInstance();
	private Leaf leafparameters;
	private Parameters parameters;
	private ProblemQuantities variables;
	private InputTimeSeries input;
	

	
	@Execute
	public void process() throws Exception {
		System.out.print("\nStart InputReaderMain");
		
		leafparameters = Leaf.getInstance();
		parameters = Parameters.getInstance();
		variables = ProblemQuantities.getInstance();
		input = InputTimeSeries.getInstance();
		
		input.time = temporalStep*60;
		
		DateTime startDateTime = formatter.parseDateTime(tStartDate);
		variables.date=startDateTime.plusMinutes(temporalStep*step);
		
		//System.out.println("data inizio = "+ (startDateTime));
		//System.out.println("data variabile = "+ (variables.date));
		
		stationCoordinates = getCoordinate(0,inCentroids, idCentroids);
		Iterator<Integer> idIterator = stationCoordinates.keySet().iterator();
		CoordinateReferenceSystem sourceCRS = inDem.getCoordinateReferenceSystem2D();

		
		Set<Entry<Integer, double[]>> entrySet = inAirTemperature.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {
			
			Integer ID = entry.getKey();
			Coordinate coordinate = (Coordinate) stationCoordinates.get(idIterator.next());
			Point [] idPoint= getPoint(coordinate,sourceCRS, targetCRS);
						
			input.elevation = coordinate.z;
			input.longitude = (idPoint[0].getX());
			input.latitude = Math.toRadians(idPoint[0].getY());
			input.ID = ID;
				
	///////////////////////////////////////////// INPUT READER /////////////////////////////////////////////
			System.out.printf("\ndata   " + variables.date);
			
			input.airTemperature = inAirTemperature.get(ID)[0]+273.15;
			if (input.airTemperature == (nullValue+273.15)) {input.airTemperature = parameters.defaultAirTemperature;}		
			variables.leafTemperatureSun = input.airTemperature;
			variables.leafTemperatureShade = input.airTemperature;
			//System.out.println("\nair temperature  = "+ input.airTemperature);
			
			input.leafAreaIndex = parameters.defaultLeafAreaIndex;
			if (inLeafAreaIndex != null){input.leafAreaIndex = inLeafAreaIndex.get(ID)[0];}
			if (input.leafAreaIndex == nullValue) {input.leafAreaIndex = parameters.defaultLeafAreaIndex;}
			
			if (inShortWaveRadiationDirect != null) {input.shortWaveRadiationDirect = inShortWaveRadiationDirect.get(ID)[0];}
			if (input.shortWaveRadiationDirect == nullValue) {input.shortWaveRadiationDirect = parameters.defaultShortWaveRadiationDirect;}
			
			if (inShortWaveRadiationDiffuse != null) {input.shortWaveRadiationDiffuse = inShortWaveRadiationDiffuse.get(ID)[0];}
			if (input.shortWaveRadiationDiffuse == nullValue) {input.shortWaveRadiationDiffuse = 0.159*input.shortWaveRadiationDirect;} 						
			
			if (inLongWaveRadiation != null) {input.longWaveRadiation = inLongWaveRadiation.get(ID)[0];}
			if (input.longWaveRadiation == nullValue) {input.longWaveRadiation = leafparameters.longWaveEmittance * parameters.stefanBoltzmannConstant * pow (input.airTemperature, 4);}	 
			
			if (inNetRadiation != null) {input.netRadiation = inNetRadiation.get(ID)[0];}
			if (input.netRadiation == nullValue) {input.netRadiation = defaultNetRadiation;}	
			
			input.windVelocity = parameters.defaultWindVelocity;
			if (inWindVelocity != null){input.windVelocity = inWindVelocity.get(ID)[0];}
			if (input.windVelocity == nullValue) {input.windVelocity = parameters.defaultWindVelocity;}
			if (input.windVelocity == 0) {input.windVelocity = parameters.defaultWindVelocity;}			
			
			input.atmosphericPressure = parameters.defaultAtmosphericPressure;
			if (inAtmosphericPressure != null){input.atmosphericPressure = inAtmosphericPressure.get(ID)[0];}
			if (input.atmosphericPressure == nullValue) {input.atmosphericPressure = pressure.computePressure(parameters.defaultAtmosphericPressure, parameters.massAirMolecule, parameters.gravityConstant, input.elevation, parameters.boltzmannConstant, input.airTemperature);}			
			
			input.relativeHumidity = parameters.defaultRelativeHumidity;
			if (inRelativeHumidity != null){input.relativeHumidity = inRelativeHumidity.get(ID)[0];}
			if (input.relativeHumidity == nullValue) {input.relativeHumidity = parameters.defaultRelativeHumidity;}				
			
			input.soilFlux = parameters.defaultSoilFlux;
			if (inSoilFlux != null){input.soilFlux = inSoilFlux.get(ID)[0];}
			if (input.soilFlux == nullValue) {input.soilFlux = parameters.defaultSoilFlux;}
			
			input.soilMoisture = parameters.defaultSoilMoisture;
			if (inSoilMoisture != null){input.soilMoisture = inSoilMoisture.get(ID)[0];}
			if (input.soilMoisture == nullValue) {input.soilMoisture = parameters.defaultSoilMoisture;}
				
		}
		
		step++;
		System.out.print("\n\nEnd InputReaderMain");
	}

	private Point[] getPoint(Coordinate coordinate, CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
			throws Exception{
		Point[] point = new Point[] { GeometryUtilities.gf().createPoint(coordinate) };
		CrsUtilities.reproject(sourceCRS, targetCRS, point);
		return point;
	}
	
    
    private LinkedHashMap<Integer, Coordinate> getCoordinate(int nStaz,
			SimpleFeatureCollection collection, String idField)
					throws Exception {
		LinkedHashMap<Integer, Coordinate> id2CoordinatesMcovarianceMatrix = new LinkedHashMap<Integer, Coordinate>();
		FeatureIterator<SimpleFeature> iterator = collection.features();
		Coordinate coordinate = null;
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				int name = ((Number) feature.getAttribute(idField)).intValue();
				coordinate = ((Geometry) feature.getDefaultGeometry())
						.getCentroid().getCoordinate();
				double z = 0;
				if (centroidElevation != null) {
					try {
						z = ((Number) feature.getAttribute(centroidElevation))
								.doubleValue();
					} catch (NullPointerException e) {
						System.out.println("kriging.noPointZ");
						//pm.errorMessage(msg.message("kriging.noPointZ"));
						throw new Exception(msg.message("kriging.noPointZ"));}}
				coordinate.z = z;
				id2CoordinatesMcovarianceMatrix.put(name, coordinate);}} 
		finally {
			iterator.close();}
		return id2CoordinatesMcovarianceMatrix;
		}
    
}
