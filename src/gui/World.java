package gui;

import engine.Automaton;
import engine.Hive;
import engine.NukeLocations;
import engine.ResourceLocations;

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

	private Collection<Hive> hives;
	private ResourceLocations resourceLocations;
	private NukeLocations nukeLocations;
	private Timer worldTimer;
	private int currentGeneration;
	private MouseClickStates mouseStates;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	private static final BasicStroke wideStroke = new BasicStroke(1.5f);
	private static final BasicStroke stroke = new BasicStroke(1.5f);

	public World() {

		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.resourceLocations = new ResourceLocations();
		this.nukeLocations = new NukeLocations();
		this.hives = this.initHives();
		this.worldTimer = new Timer(15, (ActionEvent e) -> {
			for(final Hive hive : this.hives) {
				hive.iterate();
			}
			this.currentGeneration++;
			this.repaint();
		});
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.currentGeneration = 0;
		this.mouseStates = MouseClickStates.DROP_RESOURCES;
	}

	private Collection<Hive> initHives() {
		final List<Hive> hives = new ArrayList<>();
		hives.add(new Hive(0, 200, this, this.resourceLocations, this.nukeLocations));
		return Collections.unmodifiableCollection(hives);
	}

	void setMouseState(final MouseClickStates s) {
		this.mouseStates = s;
	}

	public void startWorld() {
		this.worldTimer.start();
	}

	@Override
	public void paintComponent(final Graphics g){
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		//draw the hive
		for(final Hive hive : this.hives) {
			g2d.setColor(hive.getColor());
			final Polygon hive_location = hive.getHivePolygon();
			g2d.fillPolygon(hive_location);

		}
		//draw nukes
		g2d.setColor(NukeLocations.getNukeColor());
		Point tmpPt;
		Set <Point> s = this.nukeLocations.getNukeLocations();
		Iterator<Point> it = s.iterator();
		
		while ( it.hasNext() ) { 
			tmpPt = it.next();
			drawCircle(	(int)tmpPt.getX(), (int)tmpPt.getY(), 10, g);
		}
        //draw the resources
		g2d.setColor(ResourceLocations.getResourceColor());
		s = this.resourceLocations.getResourceLocations();
		it = s.iterator();
		while ( it.hasNext() ) { 
			tmpPt = it.next();
			g2d.fill3DRect(	(int)tmpPt.getX(), 
							(int)tmpPt.getY(), 
							ResourceLocations.getResourceSize(), 
							ResourceLocations.getResourceSize(),
							false);
		}
		this.drawAutomotons(g2d);
		g2d.setComposite(makeComposite(1.0f));
		g2d.setColor(Color.ORANGE);
		g2d.drawString( "minerals available: " + this.resourceLocations.getSize(), 330, 30);
		g2d.drawString( "current generation: " + this.currentGeneration, 330, 40);
	}

	private void drawAutomotons(final Graphics2D g2d) {
		for(final Hive hive : this.hives) {
			for(final Automaton automaton : hive.getAutomatons()) {
				this.drawAutomoton(automaton, g2d);
			}
		}
	}

	private void drawAutomoton(final Automaton automaton,
							   final Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (automaton.getState() == Automaton.AutomotonState.RETURNING_RESOURCES) {
			g2d.setPaint(automaton.getColor());
			g2d.setStroke(wideStroke);
			g2d.draw(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize(), automaton.getSize()));
			g2d.setStroke(stroke);
			g2d.setPaint(ResourceLocations.getResourceColor());
			g2d.fill(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize() - 1, automaton.getSize() - 1));
		} else if (automaton.getState() == Automaton.AutomotonState.DEAD)  {
			final Composite originalComposite = g2d.getComposite();
			g2d.setComposite(makeComposite(.50f));
			g2d.setPaint(automaton.getColor());
			g2d.setStroke(stroke);
			g2d.fill3DRect((int) automaton.getX(), (int) automaton.getY(), automaton.getSize() - 1, automaton.getSize() - 1, true );
			g2d.setComposite(originalComposite);
		} else {
			g2d.setPaint(automaton.getColor());
			g2d.setStroke(stroke);
			g2d.draw(new Ellipse2D.Double(automaton.getX(), automaton.getY(), automaton.getSize(), automaton.getSize()));
			this.paintArrow(automaton, g2d);
		}

	}

	private void paintArrow(final Automaton automaton,
							final Graphics g){
		final Graphics2D g2d = (Graphics2D)g;
		final double x1 = ((automaton.getDeltaX())/ automaton.getDistanceToDestination())*15 + automaton.getX();
		final double y1 = ((automaton.getDeltaY())/ automaton.getDistanceToDestination())*15 + automaton.getY();
		final Line2D l2d = new Line2D.Double(automaton.getX(), automaton.getY(), x1, y1);
		g2d.draw(l2d);
	}

	private static AlphaComposite makeComposite(final float alpha) {
		return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
	}
	
	private static void drawCircle(final int x, final int y, final int radius, final Graphics g){
		g.drawOval(x - radius, y - radius, radius*2, radius*2);
	}

	@Override
	public void mouseReleased(final MouseEvent evt) {
	}

	@Override
	public void mousePressed(final MouseEvent evt) {
	}

	@Override
	public void mouseDragged(final MouseEvent evt){
		
		final int x = evt.getX();
		final int y = evt.getY();

		if (this.mouseStates == MouseClickStates.DROP_RESOURCES) {

//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//
//			if (p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//				return;
//			}

			if( ((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
				this.resourceLocations.registerResource(evt.getX(), evt.getY());
			this.repaint();
		} else if (this.mouseStates == MouseClickStates.NUKE) {
			
//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//
//			if ( p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//				return;
//			}

			if( ((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
				this.nukeLocations.registerNuke(evt.getX(), evt.getY());
			this.repaint();

		}
		//System.out.println("Mouse dragged!  x = " +evt.getX()+ " y = " +evt.getY());
	}

	@Override
	public void mouseMoved(final MouseEvent evt){
//		if( ((evt.getX() > 0) && (evt.getX() < getWidth())) && ((evt.getY() > 0) && (evt.getY() < getHeight()))) {
//
//		}
		//	engine.Hive.setmouseCoordinates(evt.getX(), evt.getY());
		//System.out.println("Mouse moved!  x = " +evt.getX()+ " y = " +evt.getY());
	}

	@Override
	public void mouseClicked(final MouseEvent evt) {
		
		final int x = evt.getX();
		final int y = evt.getY();
		
//		if (this.mouseStates == MouseClickStates.DROP_RESOURCES) {
//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//			if ( p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//				final JPanel panel = this.gameFrame.getGamePanel();
//				panel.removeAll();
//				final JButton b1 = new JButton();
//				//lblChart.setIcon(new ImageIcon(chart.getChart()));
//				b1.setText("+1 carry");
//				b1.addActionListener(
//						e -> {
//							int current_capacity = this.HiveCluster_A.getPayLoadCapacity();
//							System.out.println("current_capacity = " +current_capacity);
//							this.HiveCluster_A.setPayLoadCapacity(++current_capacity);
//							//System.out.println("i just set the capacity to: " +(current_capacity+1));
//						}
//				);
//				panel.add(b1);
//				this.gameFrame.pack();
//				//panel.repaint();
//				return;
//			}
//
//			if( ((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
//				this.resourceLocations.registerResource(evt.getX(), evt.getY());
//			this.repaint();
//			//System.out.println("Resource registered at x = " +evt.getX()+ " y = " +evt.getY());
//		} else if (this.mouseStates == MouseClickStates.NUKE) {
//
//			final Polygon p = this.HiveCluster_A.getHivePolygon();
//
//			if ( p.contains(x, y) ) {
//				System.out.println("user clicks on the hive!");
//
//				final JPanel panel = this.gameFrame.getGamePanel();
//
//				panel.removeAll();
//				final JButton b1 = new JButton();
//				//lblChart.setIcon(new ImageIcon(chart.getChart()));
//				b1.setText("+1 carry");
//				b1.addActionListener(
//						e -> {
//							int current_capacity = this.HiveCluster_A.getPayLoadCapacity();
//							System.out.println("current_capacity = " +current_capacity);
//							this.HiveCluster_A.setPayLoadCapacity(++current_capacity);
//							//System.out.println("i just set the capacity to: " +(current_capacity+1));
//						}
//				);
//
//				panel.add(b1);
//				this.gameFrame.pack();
//				//panel.repaint();
//			}

			if( ((evt.getX() > 0) && (evt.getX() < this.getWidth())) && ((evt.getY() > 0) && (evt.getY() < this.getHeight())))
				this.nukeLocations.registerNuke(evt.getX(), evt.getY());
			this.repaint();

	}

	@Override
	public void mouseEntered(final MouseEvent evt){
		//engine.Hive.mouseInBounds(true);
		//if( ((evt.getX() > 0) && (evt.getX() < 1250)) && ((evt.getY() > 0) && (evt.getY() < 400))) 
	    //engine.Hive.setmouseCoordinates(evt.getX(), evt.getY());
		//System.out.println("Mouse entered canvas!  x = " +evt.getX()+ " y = " +evt.getY());
	}

	@Override
	public void mouseExited(final MouseEvent evt){
		//engine.Hive.mouseInBounds(false);
		//engine.Automaton.resetmouseCoordinates();
		//System.out.println("Mouse exited!  x = " +evt.getX()+ " y = " +evt.getY());
	}

	enum MouseClickStates {
	    DROP_RESOURCES, 
	    NUKE
	}

}
