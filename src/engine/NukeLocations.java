package engine;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Set;


public class NukeLocations {

	private HashMap <Point, Integer >locationMap;
	private static Color nukeColor;

	public NukeLocations(){
		nukeColor = Color.GRAY;
		this.locationMap = new HashMap<>();
	}

	public synchronized void registerNuke(final int x, final int y){
		final Point p = new Point (x, y);
		this.locationMap.put(p, 100);
	}

	public static Color getNukeColor() {
		return nukeColor;
	}
	
	public Set<Point> getNukeLocations() {
		return this.locationMap.keySet();
	}

}