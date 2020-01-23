package engine;

import gui.World;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.awt.geom.Area;

public class Automaton {

	private double xPosition, deltaX;
	private double yPosition, deltaY;
	private double distanceToDestination;
	private final int size;
	private final ResourceLocations resourceList;
	private final NukeLocations nukeList;
	private Color color;
	private final int id;
	private Point resourceLocation;
	private final World world;
	private final Hive hive;
	private int amountOfResourcesFound;
	private int age;
	private int energy;
	private final Point pointOfInterest;
	
	private static final double AUTOMOTON_SPEED_NORMAL = .450;
	private static final double AUTOMOTON_SPEED_WHEN_RETURNING_FOOD = .15;
	private static final double NOTIFICATION_SENSETIVITY = 1;
	private static final Random R = new Random();
	private static final Color BROWN = new Color(139,69,19, 200);

	private AutomotonState automotonState;

	Automaton(final Hive h,
			  final int automoton_size,
			  final World world,
			  final ResourceLocations rl,
			  final NukeLocations nl,
			  final Color automaton_color,
			  final int automoton_id) {
		this.hive = h;
		this.xPosition = h.getBaseX();
		this.yPosition = h.getBaseY();
		this.deltaX = 0;
		this.deltaY = 0;
		this.size = automoton_size;
		this.resourceList = rl;
		this.nukeList = nl;
		this.color = automaton_color;
		this.id = automoton_id;
		this.world = world;
		this.automotonState = AutomotonState.EXPLORATION_CHOOSE_DESTINATION;
		this.color = Color.RED;
		this.age = 0;
		this.energy = 30;
		this.pointOfInterest = new Point();
	}

	void move() {
		
		if (this.automotonState == AutomotonState.EXPLORATION_CHOOSE_DESTINATION) {
			double destination_x, destination_y;
			final int xBoundary = this.world.getWidth();
			final int yBoundary = this.world.getHeight();
			
			double gx;
			do {
				gx = R.nextGaussian();
				destination_x = this.xPosition + (gx * 40);
			} while (destination_x > xBoundary || destination_x < 0 || gx > 1 || gx < -1);
			
			double gy;
			do {
				gy = R.nextGaussian();
				destination_y = this.yPosition + (gy * 40);
			} while (destination_y > yBoundary || destination_y < 0 || gy > 1 || gy < -1);
			this.pointOfInterest.setLocation(destination_x, destination_y);
			this.distanceToDestination = this.pointOfInterest.distance(this.xPosition, this.yPosition);
			this.deltaX = (destination_x - this.xPosition) * AUTOMOTON_SPEED_NORMAL;
			this.deltaY = (destination_y - this.yPosition) * AUTOMOTON_SPEED_NORMAL;
			this.automotonState = AutomotonState.EXPLORATION_GOTO_DESTINATION;
		} else if (this.automotonState == AutomotonState.EXPLORATION_GOTO_DESTINATION) {
			this.xPosition += (this.deltaX / this.distanceToDestination);
			this.yPosition += (this.deltaY / this.distanceToDestination);
			if(this.pointOfInterest.distance(this.xPosition, this.yPosition) < 1){
				this.automotonState = AutomotonState.EXPLORATION_CHOOSE_DESTINATION;
			}
		}
	}

	void notifyOthersOnReturn(final Hive h){
		for(final Automaton automaton : h.getAutomatons()){
			final AutomotonState st = automaton.getState();
			if((st == AutomotonState.EXPLORATION_GOTO_DESTINATION) && (automaton.getId() != this.id) &&
					(Point.distance(this.xPosition, this.yPosition, automaton.getX(), automaton.getY()) < this.size + NOTIFICATION_SENSETIVITY)){
				automaton.setResourceLocation(this.resourceLocation);
				automaton.setDx((this.resourceLocation.getX() - automaton.getX()) * AUTOMOTON_SPEED_NORMAL);
				automaton.setDy((this.resourceLocation.getY() - automaton.getY()) * AUTOMOTON_SPEED_NORMAL);
				automaton.setD(this.resourceLocation.distance(automaton.getX(), automaton.getY()));
				automaton.setState(AutomotonState.SWARMING_TO_RESOURCE);
			}
		}
	}   

	void checkforResource(){
		
		final Set<Point> s = this.resourceList.getResourceLocations();
		final Iterator <Point> it = s.iterator();
		final int resource_size = ResourceLocations.getResourceSize();
		
		while ( it.hasNext() ) {
			final Point p = it.next();
			if (p.distance(this.xPosition, this.yPosition) < resource_size) {
				final Area resource_area = new Area(new Rectangle((int)p.getX(), (int)p.getY(), resource_size, resource_size ));
				if (resource_area.contains(this.xPosition, this.yPosition)) {
					this.deltaX = (this.hive.getBaseX() - this.xPosition) * AUTOMOTON_SPEED_WHEN_RETURNING_FOOD;
					this.deltaY = (this.hive.getBaseY() - this.yPosition) * AUTOMOTON_SPEED_WHEN_RETURNING_FOOD;
					final Point hive_location = this.hive.getHiveLocation();
					this.distanceToDestination = hive_location.distance(this.xPosition, this.yPosition);
					this.resourceLocation = new Point((int) this.xPosition, (int) this.yPosition);
					this.amountOfResourcesFound = this.resourceList.removeResources((int)p.getX(), (int)p.getY(), this.hive.getPayLoadCapacity());
					this.automotonState = AutomotonState.RETURNING_RESOURCES;
					return;
				}
			}
		}
		this.resourceLocation = null;
	}
	
	void checkforNuke() {
		for (final Point p : this.nukeList.getNukeLocations()) {
			if (p.distance(this.xPosition, this.yPosition) <= 10) {
				this.energy--;
				this.deltaX = this.deltaX * .96;
				this.deltaY = this.deltaY * .96;
				if (this.energy <= 0) {
					this.automotonState = AutomotonState.DEAD;
					this.color = BROWN;
				}
			}
		}
	}

	int getAge() {
		return this.age;
	}

	void swarmToResourceLocation(){
		if(this.resourceLocation.distance(this.xPosition, this.yPosition) < 1){
			this.xPosition = this.resourceLocation.getX();
			this.yPosition = this.resourceLocation.getY();
			this.color = Color.RED;
			this.automotonState = AutomotonState.EXPLORATION_CHOOSE_DESTINATION;
			this.checkforResource();
		} else {
			this.xPosition += (this.deltaX / this.distanceToDestination);
			this.yPosition += (this.deltaY / this.distanceToDestination);
		}
	}

	int returnMineralsToBase(){
		this.xPosition += (this.deltaX / this.distanceToDestination);
		this.yPosition += (this.deltaY / this.distanceToDestination);
		final Point hive_location = this.hive.getHiveLocation();
		if(hive_location.distance(this.xPosition, this.yPosition) <= this.size){
			this.deltaX = (this.resourceLocation.getX() - this.xPosition) * AUTOMOTON_SPEED_NORMAL;
			this.deltaY = (this.resourceLocation.getY() - this.yPosition) * AUTOMOTON_SPEED_NORMAL;
			this.distanceToDestination = this.resourceLocation.distance(this.xPosition, this.yPosition);
			this.automotonState = AutomotonState.SWARMING_TO_RESOURCE;
			return this.amountOfResourcesFound;
		}
		return 0;
	}

	void incrementAge() {
		this.age++;
	}

	public int getSize() {
		return this.size;
	}

	public double getX(){
		return this.xPosition;
	}
	
	public double getY(){
		return this.yPosition;
	}

	public double getDeltaX() {
		return this.deltaX;
	}

	public double getDeltaY() {
		return this.deltaY;
	}

	public Color getColor() {
		return this.color;
	}

	private int getId() {
		return this.id;
	}

	public AutomotonState getState() {
		return this.automotonState;
	}

	public double getDistanceToDestination() {
		return this.distanceToDestination;
	}
	
	private void setDx(final double delta_x) {
		this.deltaX = delta_x;
	}
	
	private void setDy(final double delta_y) {
		this.deltaY = delta_y;
	}
	
	private void setD(final double d) {
		this.distanceToDestination = d;
	}
	
	void setState(final AutomotonState s) {
		this.automotonState = s;
	}
	
	private void setResourceLocation(final Point rl) {
		this.resourceLocation = rl;
	}
	
	void setColor(final Color c) {
		this.color = c;
	}

	public void iterate() {
		this.move();
		this.checkforResource();
	}

	public enum AutomotonState {
		RETURNING_RESOURCES {
			@Override
			public <T> T update(Automaton automaton) {
				return null;
			}
		},
		SWARMING_TO_RESOURCE {
			@Override
			public <T> T update(Automaton automaton) {
				return null;
			}
		},
		EXPLORATION_CHOOSE_DESTINATION {
			@Override
			public <T> T update(Automaton automaton) {
				return null;
			}
		},
		EXPLORATION_GOTO_DESTINATION {
			@Override
			public <T> T update(Automaton automaton) {
				return null;
			}
		},
		DEAD {
			@Override
			public <T> T update(Automaton automaton) {
				return null;
			}
		};

		public abstract <T> T update(Automaton automaton);
	}
}