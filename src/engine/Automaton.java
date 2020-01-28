package engine;

import shared.Utils;

import java.util.Optional;

public class Automaton {

    private CartesianPoint<Double> position;
    private CartesianPoint<Double> velocity;
    private double distanceToDestination;
    private final int size;
    private CartesianPoint<Double> resourceLocation;
    private final Hive hive;
    private int age;
    private int energy;
    private CartesianPoint<Double> targetLocation;
    private AutomotonState automotonState;

    Automaton(final Hive h,
              final int size) {
        this.hive = h;
        this.position = new CartesianPoint<>(h.getBaseX(), h.getBaseY());
        this.velocity = new CartesianPoint<>(0.0, 0.0);
        this.size = size;
        this.automotonState = AutomotonState.EXPLORATION_CHOOSE_DESTINATION;
        this.age = 0;
        this.energy = Utils.DEFAULT_ENERGY;
        this.targetLocation = new CartesianPoint<>(-1.0, -1.0);
    }

    CartesianPoint<Double> getPosition() {
        return this.position;
    }

    public double getX(){
        return this.position.getX();
    }

    public double getY(){
        return this.position.getY();
    }

    public double getDeltaX() {
        return this.velocity.getX();
    }

    public double getDeltaY() {
        return this.velocity.getY();
    }

    int getAge() {
        return this.age;
    }

    private Hive getHive() {
        return this.hive;
    }

    private void notifyOthersOnReturn(){
        for(final Automaton automaton : this.hive.getAutomatons()){
            final AutomotonState st = automaton.getState();
            if((st == AutomotonState.EXPLORATION_GOTO_DESTINATION) && (automaton != this) &&
                    (CartesianPoint.distance(this.position, automaton.getPosition()) < (this.size + Utils.NOTIFICATION_SENSITIVITY))){
                automaton.setResourceLocation(this.resourceLocation);
                automaton.updateVelocity((automaton.resourceLocation.getX() - automaton.getX()) * Utils.AUTOMATON_SPEED_NORMAL,
                                         (this.resourceLocation.getY() - automaton.getY()) * Utils.AUTOMATON_SPEED_NORMAL);
                automaton.setDistanceToLocation(CartesianPoint.distance(this.resourceLocation, automaton.getPosition()));
                automaton.setState(AutomotonState.SWARMING_TO_RESOURCE);
            }
        }
    }

    private void checkforResource() {
        final Optional<CartesianPoint<Double>> doubleCartesianPoint = ResourceLocations.getInstance().findResource(this);
        if(doubleCartesianPoint.isPresent()) {
            this.updateVelocity((this.hive.getBaseX() - this.position.getX()) * Utils.AUTOMATON_SPEED_WHEN_RETURNING_FOOD,
                    (this.hive.getBaseY() - this.position.getY()) * Utils.AUTOMATON_SPEED_WHEN_RETURNING_FOOD);
            final CartesianPoint<Double> hive_location = this.hive.getHiveLocation();
            this.distanceToDestination = CartesianPoint.distance(hive_location, this.position);
            this.resourceLocation = new CartesianPoint<>(this.position);
            //this.amountOfResourcesFound = ResourceLocations.getInstance().removeResources(p.getX().intValue(), p.getY().intValue(), this.hive.getPayLoadCapacity());
            this.automotonState = AutomotonState.RETURNING_RESOURCES;
            return;
        }
        this.resourceLocation = null;
    }

    private void checkforNuke() {
        for (final CartesianPoint<Double> p : NukeLocations.getInstance().getNukeLocations()) {
            if (CartesianPoint.distance(p, this.position) <= 10) {
                this.energy--;
                updateVelocity(this.velocity.getX() * Utils.VELOCITY_DAMPENING,
                               this.velocity.getY() * Utils.VELOCITY_DAMPENING);
                if (this.energy <= 0) {
                    this.automotonState = AutomotonState.DEAD;
                }
            }
        }
    }

    private int returnMineralsToBase() {
        updatePosition(this.position.getX() + (this.velocity.getX() / this.distanceToDestination),
                       this.position.getY() + (this.velocity.getY() / this.distanceToDestination));
        if(CartesianPoint.distance(this.hive.getHiveLocation(), this.getPosition()) <= this.size) {
            updateVelocity((this.resourceLocation.getX() - this.position.getX()) * Utils.AUTOMATON_SPEED_NORMAL,
                           (this.resourceLocation.getY() - this.position.getY()) * Utils.AUTOMATON_SPEED_NORMAL);
            this.distanceToDestination = CartesianPoint.distance(this.resourceLocation, this.getPosition());
            this.automotonState = AutomotonState.SWARMING_TO_RESOURCE;
            return 1;
        }
        return 0;
    }

    public int getSize() {
        return this.size;
    }

    public AutomotonState getState() {
        return this.automotonState;
    }

    public double getDistanceToDestination() {
        return this.distanceToDestination;
    }

    private CartesianPoint<Double> getTargetLocation() {
        return this.targetLocation;
    }

    private void updatePosition(final double xPosition,
                                final double yPosition) {
        this.position = new CartesianPoint<>(xPosition, yPosition);
    }

    private void updateVelocity(final double deltaX, final double deltaY) {
        this.velocity = new CartesianPoint<>(deltaX, deltaY);
    }

    void incrementAge() {
        this.age++;
        if(this.age > Utils.AUTOMATON_AGE_LIMIT) {
            this.automotonState = AutomotonState.DEAD;
        }
    }

    private void setDistanceToLocation(final double d) {
        this.distanceToDestination = d;
    }

    private void setState(final AutomotonState s) {
        this.automotonState = s;
    }

    private void setResourceLocation(final CartesianPoint<Double> rl) {
        this.resourceLocation = rl;
    }

    void iterate() {
        this.automotonState.update(this);
        //this.checkforResource();
        this.checkforNuke();
    }

    public enum AutomotonState {
        RETURNING_RESOURCES {
            @Override
            public void update(final Automaton automaton) {
                automaton.hive.mineralCount += automaton.returnMineralsToBase();
                automaton.notifyOthersOnReturn();
            }
        },
        SWARMING_TO_RESOURCE {
            @Override
            public void update(final Automaton automaton) {
                if( CartesianPoint.distance(automaton.resourceLocation, automaton.getPosition()) < 1){
                    automaton.updatePosition(automaton.resourceLocation.getX(), automaton.resourceLocation.getY());
                    automaton.automotonState = AutomotonState.EXPLORATION_CHOOSE_DESTINATION;
                    automaton.checkforResource();
                } else {
                    automaton.updatePosition(automaton.position.getX() + (automaton.velocity.getX() / automaton.distanceToDestination),
                                             automaton.position.getY() + (automaton.velocity.getY() / automaton.distanceToDestination));
                }
            }
        },
        EXPLORATION_CHOOSE_DESTINATION {
            @Override
            public void update(final Automaton automaton) {
                double destinationX, destinationY;
                final int xBoundary = automaton.getHive().getWorld().getWidth();
                final int yBoundary = automaton.getHive().getWorld().getHeight();

                double gx;
                do {
                    gx = Utils.R.nextGaussian();
                    destinationX = automaton.getX() + (gx * 40);
                } while (destinationX > xBoundary || destinationX < 0 || gx > 1 || gx < -1);

                double gy;
                do {
                    gy = Utils.R.nextGaussian();
                    destinationY = automaton.getY() + (gy * 40);
                } while (destinationY > yBoundary || destinationY < 0 || gy > 1 || gy < -1);
                automaton.targetLocation = new CartesianPoint<>(destinationX, destinationY);
                automaton.setDistanceToLocation( CartesianPoint.distance(automaton.getTargetLocation(), automaton.getPosition()));
                automaton.updateVelocity((destinationX - automaton.getX()) * Utils.AUTOMATON_SPEED_NORMAL,
                                         (destinationY - automaton.getY()) * Utils.AUTOMATON_SPEED_NORMAL);
                automaton.setState(AutomotonState.EXPLORATION_GOTO_DESTINATION);
                automaton.checkforResource();
            }
        },
        EXPLORATION_GOTO_DESTINATION {
            @Override
            public void update(final Automaton automaton) {
                automaton.updatePosition(automaton.position.getX() + (automaton.getDeltaX() / automaton.getDistanceToDestination()),
                                         automaton.position.getY() + (automaton.getDeltaY() / automaton.getDistanceToDestination()));
                if(CartesianPoint.distance(automaton.getTargetLocation(), automaton.getPosition()) < 1) {
                    automaton.setState(AutomotonState.EXPLORATION_CHOOSE_DESTINATION);
                }
                //automaton.checkforResource();
            }
        },
        DEAD {
            @Override
            public void update(final Automaton automaton) {
            }
        };

        protected abstract void update(Automaton automaton);
    }
}