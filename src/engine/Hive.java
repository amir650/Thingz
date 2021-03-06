package engine;

import gui.World;
import shared.Utils;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Hive {

	private final List <Automaton> automatons;
	private final CartesianPoint<Double> hiveLocation;
	private final int automatonSize = 5;
	private final int hiveMaxSize;
	private final Color hiveColor;
	private final World world;
	private final AtomicLong minerals;

	public Hive(final int initialSize,
				final int maxSize,
				final World world) {
		this.hiveMaxSize = maxSize;
		this.hiveColor = Color.yellow;
		this.world = world;
		this.hiveLocation = new CartesianPoint<>((double)Utils.WIDTH/2, (double)Utils.HEIGHT/2);
		this.automatons = createAutomatons(initialSize);
		this.minerals = new AtomicLong(0);
	}

	private List<Automaton> createAutomatons(final int initialSize) {
		final List<Automaton> automatons = new ArrayList<>();
		for(int i = 0; i < initialSize; i++){
			automatons.add(new Automaton(this, this.automatonSize));
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

	public List<Automaton> getAutomatons() {
		return this.automatons;
	}

	World getWorld() {
		return this.world;
	}

	public void iterate() {
		spawn();
		updateAutomatons();
		nukes();
		age();
	}

	private void nukes() {
		for (final CartesianPoint<Double> p : NukeLocations.getInstance().getNukeLocations()) {
			for(final Automaton automaton : this.automatons) {
				if (CartesianPoint.distance(p, automaton.getPosition()) <= 10) {
					automaton.poison();
				}
			}
		}
	}

	private void spawn() {
		if(this.automatons.size() < this.hiveMaxSize) {
			this.automatons.add(new Automaton(this, this.automatonSize));
		}
	}

	private void updateAutomatons() {
		this.automatons.forEach(Automaton::update);
	}

	private void age() {
		this.automatons.forEach(Automaton::incrementAge);
		this.automatons.removeIf(automaton -> automaton.getAge().longValue() > Utils.AUTOMATON_REMOVE_CORPSE_AGE);
	}

	void depositMinerals(final long minerals) {
		this.minerals.addAndGet(minerals);
	}

}
