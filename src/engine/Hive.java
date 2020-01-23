package engine;

import gui.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

public class Hive {

	private List <Automaton> automatons;
	private ResourceLocations resourceList;
	private NukeLocations nukeList;
	private Point hiveLocation;
	private int automotonSize = 5;
	private int hiveMaxSize;
	private Color hiveColor;
	private World theWorld;
	private Polygon hivePolygon;
	private int automotonIDCounter;
	private int mineralCount;
	private int payLoadSize;

	private static final Color BROWN = new Color(139,69,19, 200);

	public Hive(final int initialSize,
				final int maxSize,
				final World world,
				final ResourceLocations resourceLocations,
				final NukeLocations nukeLocations) {

		this.automatons = new ArrayList<>(initialSize);
		this.hiveMaxSize = maxSize;
		this.resourceList = resourceLocations;
		this.nukeList = nukeLocations;
		this.hiveColor = Color.yellow;
		this.theWorld = world;
		this.hiveLocation = new Point(100, 100);

		for(int i = 0; i < initialSize; i++){
			this.automatons.add(new Automaton(this, this.automotonSize, world, this.resourceList, this.nukeList, Color.RED, this.automotonIDCounter));
			this.automotonIDCounter++;
		}

		this.hivePolygon = new Polygon();

		//pentagon
		this.hivePolygon.addPoint((int) this.hiveLocation.getX(), (int) this.hiveLocation.getY());
		this.hivePolygon.addPoint((int) this.hiveLocation.getX() + 6, (int) this.hiveLocation.getY() + 5);
		this.hivePolygon.addPoint((int) this.hiveLocation.getX() + 12, (int) this.hiveLocation.getY() + 5);
		this.hivePolygon.addPoint((int) this.hiveLocation.getX() + 18, (int) this.hiveLocation.getY());
		this.hivePolygon.addPoint((int) this.hiveLocation.getX() + 10, (int) this.hiveLocation.getY() - 5);
		this.hivePolygon.translate(-10, 0);
		this.mineralCount = 0;
		this.payLoadSize = 1;
	} 

	public List<Automaton> getAutomatons() {
		return this.automatons;
	}

	public void iterate() {
		this.spawn();
		this.exploreCanvas();
		this.returnResources();
		this.swarmtoResource();
		this.age();
		this.calculateDamages();
	}

	private void spawn() {
		if(this.automatons.size() < this.hiveMaxSize) {
			this.automatons.add(new Automaton(this, this.automotonSize, this.theWorld, this.resourceList, this.nukeList, Color.RED, this.automotonIDCounter));
			this.automotonIDCounter++;
		}
	}
	
	private void age() {
		Automaton tmpA;
		final Iterator<Automaton> iter = this.automatons.iterator();
		while (iter.hasNext()) {
			tmpA = iter.next();
			tmpA.incrementAge();
			if (tmpA.getState() != Automaton.AutomotonState.DEAD && tmpA.getAge() > 3000) {
				tmpA.setState(Automaton.AutomotonState.DEAD);
				tmpA.setColor(Hive.BROWN);
			} else {
				if (tmpA.getAge() > 3300) {
					iter.remove();
				}
			}
		}
	}

	public Polygon getHivePolygon() {
		return this.hivePolygon;
	}

	private void exploreCanvas(){
		for (final Automaton automaton : this.automatons) {
			if (automaton.getState() == Automaton.AutomotonState.EXPLORATION_CHOOSE_DESTINATION ||
				automaton.getState() == Automaton.AutomotonState.EXPLORATION_GOTO_DESTINATION) {
				automaton.iterate();
			}
		}
	}

	private void swarmtoResource(){
		for (final Automaton automaton : this.automatons) {
			if (automaton.getState() == Automaton.AutomotonState.SWARMING_TO_RESOURCE) {
				automaton.swarmToResourceLocation();
			}
		}
	}
	
	private void calculateDamages(){
		for (final Automaton automaton : this.automatons) {
			automaton.checkforNuke();
		}
	}

	private void returnResources(){
		Automaton tmpA;
		for (final Automaton automaton : this.automatons) {
			tmpA = automaton;
			if (tmpA.getState() == Automaton.AutomotonState.RETURNING_RESOURCES) {
				this.mineralCount += tmpA.returnMineralsToBase();
				tmpA.notifyOthersOnReturn(this);
			}
		}
	}

	public Color getColor() {
		return this.hiveColor;
	}

	double getBaseX() {
		return this.hiveLocation.getX();
	}

	double getBaseY() {
		return this.hiveLocation.getY();
	}
	
	Point getHiveLocation() {
		return this.hiveLocation;
	}

	int getPayLoadCapacity() {
		return this.payLoadSize;
	} 

}
