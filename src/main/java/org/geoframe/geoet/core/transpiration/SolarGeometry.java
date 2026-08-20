package org.geoframe.geoet.core.transpiration;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.joda.time.DateTime;
import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Concetta D'Amato, Michele Bottazzi and Riccardo Rigon", contact = "concetta.damato@unitn.it")
@License("General Public License Version 3 (GPLv3)")
public class SolarGeometry {
	public static double getSolarElevationAngle(DateTime date, double latitude, double longitude, double time) {
		// from Iqbal, M. (2012). An introduction to solar radiation.
		// Latitude is in radiant, Longitude is in degrees

		int dayOfTheYear = date.getDayOfYear();
		double dayAngle = 2 * PI * (dayOfTheYear - 1) / 365;
		double equationOfTime = 0.017 + 0.4281 * cos(dayAngle) - 7.351 * sin(dayAngle) - 3.349 * cos(2 * dayAngle)
				- 9.7331 * sin(2 * dayAngle);
		double solarNoon = 12 + (4 * (15 - longitude) - equationOfTime) / 60;

		double hour = (time == 86400) ? 12.5 : (double) date.getMillisOfDay() / (1000 * (3600));
		double hourAngleOfSun = PI * (hour - solarNoon) / 12;
		double solarDeclinationAngle = -23.4 * PI * cos(2 * PI * (dayOfTheYear + 10) / 365) / 180;
		double solarElevationAngle = sin(latitude) * sin(solarDeclinationAngle)
				+ cos(latitude) * cos(solarDeclinationAngle) * cos(hourAngleOfSun);

		return solarElevationAngle;

	}

}
