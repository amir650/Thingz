package engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.awt.Color;
import java.awt.Point;

public class ResourceLocations {

	private HashMap <Point, Integer >locationMap;
	private static Color  resourceColor;
	private static int resourceSize = 7;
	private static final int numResourcesToDrop = 1000;

	public ResourceLocations(){
		resourceColor = Color.CYAN;
		this.locationMap = new HashMap<>();
	}

	public synchronized void registerResource(final int x, final int y){
		final Point p = new Point (x, y);
		Integer r = this.locationMap.get(p);
		if ( r != null ) {
			r += numResourcesToDrop;
		} else {
			r = numResourcesToDrop;
		}
		this.locationMap.put(p, r);
	}

	int removeResources(final int x,
						final int y,
						int pay_load_size){

		final Point p = new Point (x, y);
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
		return resourceSize;
	}

	public int getSize() {
		final Collection<Integer> c = this.locationMap.values();
		final Iterator<Integer> itr = c.iterator();
		Integer r = 0;
		while(itr.hasNext()) {
			r+= itr.next();
		}
		return r;
	}
	
	public static Color getResourceColor() {
		return resourceColor;
	}
	
	public Set <Point> getResourceLocations() {
		return this.locationMap.keySet();
	}

	public boolean resourceExistsAtLocation(final int x, final int y){
		final Point p = new Point (x, y);
		return this.locationMap.containsKey(p);
	}

}