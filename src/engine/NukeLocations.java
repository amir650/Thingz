package engine;

import shared.Utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;


public enum NukeLocations {

	INSTANCE;

	private final HashMap <CartesianPoint<Double>, Integer >locationMap;

	NukeLocations() {
		this.locationMap = new HashMap<>();
	}

	public static NukeLocations getInstance() {
		return INSTANCE;
	}

	public synchronized void registerNuke(final double x,
										  final double y){
		final CartesianPoint<Double> p = new CartesianPoint<> (x, y);
		this.locationMap.put(p, 100);
	}

	public static Color getNukeColor() {
		return Utils.nukeColor;
	}
	
	public Set<CartesianPoint<Double>> getNukeLocations() {
		return this.locationMap.keySet();
	}

}