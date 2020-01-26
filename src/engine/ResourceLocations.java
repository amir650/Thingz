package engine;

import shared.Utils;

import java.util.HashMap;
import java.util.Set;
import java.awt.Color;

public enum ResourceLocations {

	INSTANCE;

	private final HashMap<CartesianPoint<Double>, Integer> locationMap;

	ResourceLocations() {
		this.locationMap = new HashMap<>();
	}

	public static ResourceLocations getInstance() {
		return INSTANCE;
	}

	public synchronized void registerResource(final double x,
											  final double y){
		final CartesianPoint<Double> p = new CartesianPoint<>(x, y);
		Integer r = this.locationMap.get(p);
		if (r != null) {
			r += Utils.numResourcesToDrop;
		} else {
			r = Utils.numResourcesToDrop;
		}
		this.locationMap.put(p, r);
	}

	int removeResources(final double x,
						final double y,
						int pay_load_size){
		final CartesianPoint<Double> p = new CartesianPoint<>(x, y);
		Integer r = this.locationMap.get(p);
		
		if ( pay_load_size > r)
			pay_load_size = r;
			
		r -= pay_load_size;
		
		if ( r > 0)
			this.locationMap.put(p, r);
		else
			this.locationMap.remove(p);
		
		return pay_load_size;
	}

	public static int getResourceSize() {
		return Utils.resourceSize;
	}

	public static Color getResourceColor() {
		return Utils.resourceColor;
	}
	
	public Set<CartesianPoint<Double>> getResourceLocations() {
		return this.locationMap.keySet();
	}

//	public boolean resourceExistsAtLocation(final int x, final int y){
//		final Point p = new Point (x, y);
//		return this.locationMap.containsKey(p);
//	}

}