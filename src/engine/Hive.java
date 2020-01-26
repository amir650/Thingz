package engine;

import gui.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.List;

public class Hive {

	private final List <Automaton> automatons;
	private final CartesianPoint<Double> hiveLocation;
	private final int automotonSize = 5;
	private final int hiveMaxSize;
	private final Color hiveColor;
	private final World world;
	int mineralCount;
	private final int payLoadSize;

	public Hive(final int initialSize,
				final int maxSize,
				final World world) {
		this.hiveMaxSize = maxSize;
		this.hiveColor = Color.yellow;
		this.world = world;
		this.hiveLocation = new CartesianPoint<>(100.0, 100.0);
		this.automatons = createAutomatons(initialSize);
		this.mineralCount = 0;
		this.payLoadSize = 1;
	}

	private List<Automaton> createAutomatons(final int initialSize) {
		final List<Automaton> automatons = new ArrayList<>();
		for(int i = 0; i < initialSize; i++){
			automatons.add(new Automaton(this, this.automotonSize));
		}
		return automatons;
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

	public CartesianPoint<Double> getHiveLocation() {
		return this.hiveLocation;
	}

	int getPayLoadCapacity() {
		return this.payLoadSize;
	}

	public List<Automaton> getAutomatons() {
		return this.automatons;
	}

	World getWorld() {
		return this.world;
	}

	public void iterate() {
		spawn();
		updateAutomatons();
		age();
	}

	private void updateAutomatons() {
		for(final Automaton automaton : this.automatons) {
			automaton.iterate();
		}
	}

	private void spawn() {
		if(this.automatons.size() < this.hiveMaxSize) {
			this.automatons.add(new Automaton(this, this.automotonSize));
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
			} else {
				if (tmpA.getAge() > 3300) {
					iter.remove();
				}
			}
		}
	}

}
