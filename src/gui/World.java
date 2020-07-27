package gui;

import engine.*;
import shared.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;


public class World extends JPanel implements MouseListener, MouseMotionListener {

    private final Collection<Hive> hives;
    private final Timer worldTimer;
    private int currentGeneration;
    private MouseClickStates mouseStates;

    public World() {
        this.setPreferredSize(new Dimension(Utils.WIDTH, Utils.HEIGHT));
        this.hives = initHives();
        this.worldTimer = new Timer(25, (ActionEvent e) -> {
            for (final Hive hive : this.hives) {
                hive.iterate();
            }
            this.currentGeneration++;
            repaint();
        });
        addMouseListener(this);
        addMouseMotionListener(this);
        this.currentGeneration = 0;
        this.mouseStates = MouseClickStates.DROP_RESOURCES;
    }

    private Collection<Hive> initHives() {
        final List<Hive> hives = new ArrayList<>();
        hives.add(new Hive(0, Utils.MAX_HIVE_SIZE, this));
        return Collections.unmodifiableCollection(hives);
    }

    void setMouseState(final MouseClickStates s) {
        this.mouseStates = s;
    }

    public void startWorld() {
        this.worldTimer.start();
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        //draw the hive
        for (final Hive hive : this.hives) {
            g2d.setColor(hive.getColor());
            final Polygon hive_location = createHivePolygon(hive);
            g2d.fillPolygon(hive_location);
        }
        //draw nukes
        g2d.setColor(NukeLocations.getNukeColor());
        CartesianPoint<Double> tmpPt;
        Set<CartesianPoint<Double>> s = NukeLocations.getInstance().getNukeLocations();
        Iterator<CartesianPoint<Double>> it = s.iterator();

        while (it.hasNext()) {
            tmpPt = it.next();
            drawCircle(tmpPt.getX().intValue(), tmpPt.getY().intValue(), 10, g);
        }
        //draw the resources
        g2d.setColor(ResourceLocations.getResourceColor());
        s = ResourceLocations.getInstance().getResourceLocations();
        it = s.iterator();
        while (it.hasNext()) {
            tmpPt = it.next();
            g2d.fill3DRect(tmpPt.getX().intValue(),
                    tmpPt.getY().intValue(),
                    ResourceLocations.getResourceSize(),
                    ResourceLocations.getResourceSize(),
                    false);
        }
        this.drawAutomatons(g2d);
        g2d.setComposite(makeComposite(1.0f));
        g2d.setColor(Color.ORANGE);
        g2d.drawString("minerals available: " + ResourceLocations.getInstance().getResourceLocations().size(), 330, 30);
        g2d.drawString("current generation: " + this.currentGeneration, 330, 40);
    }

    private static Polygon createHivePolygon(final Hive hive) {
        final Polygon hivePolygon = new Polygon();
        //pentagon
        hivePolygon.addPoint(hive.getHiveLocation().getX().intValue(), hive.getHiveLocation().getY().intValue());
        hivePolygon.addPoint(hive.getHiveLocation().getX().intValue() + 6, hive.getHiveLocation().getY().intValue() + 5);
        hivePolygon.addPoint(hive.getHiveLocation().getX().intValue() + 12, hive.getHiveLocation().getY().intValue() + 5);
        hivePolygon.addPoint(hive.getHiveLocation().getX().intValue() + 18, hive.getHiveLocation().getY().intValue());
        hivePolygon.addPoint(hive.getHiveLocation().getX().intValue() + 10, hive.getHiveLocation().getY().intValue() - 5);
        hivePolygon.translate(-10, 0);

        return hivePolygon;
    }

    private void drawAutomatons(final Graphics2D g2d) {
        for (final Hive hive : this.hives) {
            for (final Automaton automaton : hive.getAutomatons()) {
                drawAutomaton(automaton, g2d);
            }
        }
    }

    private void drawAutomaton(final Automaton automaton,
                               final Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (automaton.getState() == Automaton.AutomotonState.RETURN_RESOURCES) {
            g2d.setPaint(calculateAutomatonColor(automaton));
            g2d.setStroke(Utils.wideStroke);
            g2d.draw(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize(), automaton.getSize()));
            g2d.setStroke(Utils.stroke);
            g2d.setPaint(ResourceLocations.getResourceColor());
            g2d.fill(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize() - 1, automaton.getSize() - 1));
        } else if (automaton.getState() == Automaton.AutomotonState.DEAD) {
            final Composite originalComposite = g2d.getComposite();
            g2d.setComposite(makeComposite(.50f));
            g2d.setPaint(calculateAutomatonColor(automaton));
            g2d.setStroke(Utils.stroke);
            g2d.fill3DRect((int) automaton.getX(), (int) automaton.getY(), automaton.getSize() - 1, automaton.getSize() - 1, true);
            g2d.setComposite(originalComposite);
        } else {
            g2d.setPaint(calculateAutomatonColor(automaton));
            g2d.setStroke(Utils.stroke);
            g2d.draw(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize(), automaton.getSize()));
            this.paintArrow(automaton, g2d);
        }

    }

    private Color calculateAutomatonColor(final Automaton automaton) {
        return automaton.getState() == Automaton.AutomotonState.DEAD ? Color.pink : Color.RED;
    }

    private void paintArrow(final Automaton automaton,
                            final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        final double x1 = ((automaton.getDeltaX()) / automaton.getDistanceToDestination()) * 15 + automaton.getX();
        final double y1 = ((automaton.getDeltaY()) / automaton.getDistanceToDestination()) * 15 + automaton.getY();
        final Line2D l2d = new Line2D.Double(automaton.getX(), automaton.getY(), x1, y1);
        g2d.draw(l2d);
    }

    private static AlphaComposite makeComposite(final float alpha) {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    }

    private static void drawCircle(final int x,
                                   final int y,
                                   final int radius,
                                   final Graphics g) {
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public void mouseReleased(final MouseEvent evt) {
    }

    @Override
    public void mousePressed(final MouseEvent evt) {
    }

    @Override
    public void mouseDragged(final MouseEvent evt) {

        if (this.mouseStates == MouseClickStates.DROP_RESOURCES) {
//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//
//			if (p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//				return;
//			}
            if (((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
                ResourceLocations.getInstance().registerResource(evt.getX(), evt.getY());
            this.repaint();
        } else if (this.mouseStates == MouseClickStates.NUKE) {
//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//
//			if ( p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//				return;
//			}
            if (((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
                NukeLocations.getInstance().registerNuke(evt.getX(), evt.getY());
            this.repaint();
        }
        //System.out.println("Mouse dragged!  x = " +evt.getX()+ " y = " +evt.getY());
    }

    @Override
    public void mouseMoved(final MouseEvent evt) {
    }

    enum MouseClickStates {
        DROP_RESOURCES,
        NUKE
    }

    @Override
    public void mouseClicked(final MouseEvent evt) {
        final double x = evt.getX();
        final double y = evt.getY();
        if (this.mouseStates == MouseClickStates.DROP_RESOURCES) {
            if (((x > 0) && (x < this.getWidth())) &&
                    ((y > 0) && (y < this.getHeight()))) {
                final CartesianPoint<Double> p = new CartesianPoint<>(x, y);
                ResourceLocations.getInstance().registerResource(p);
                System.out.println("Dropped resource at " +p);
            }
            this.repaint();
        } else if (this.mouseStates == MouseClickStates.NUKE) {
            if (((x > 0) && (x < this.getWidth())) && ((y > 0) && (y < this.getHeight())))
                NukeLocations.getInstance().registerNuke(x, y);
            this.repaint();
        }
    }

    @Override
    public void mouseEntered(final MouseEvent evt) {
    }

    @Override
    public void mouseExited(final MouseEvent evt) {
    }

}
