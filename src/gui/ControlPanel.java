package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class ControlPanel extends JPanel implements ActionListener  {

	private static final int WIDTH = 150;
	private static final int HEIGHT = 400;

	private final World theWorld;
	
	public ControlPanel(final World w) {
		super();
		this.theWorld = w;
		this.setLayout(new GridLayout(0,1));
	    final ButtonGroup group = new ButtonGroup();
	    JRadioButton option = new JRadioButton("Drop Resources", true);
	    option.addActionListener(this);
	    group.add(option);
		this.add(option);
	    option = new JRadioButton("NukeEm!", false);
	    option.addActionListener(this);
	    group.add(option);
		this.add(option);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
    }

    public void actionPerformed(final ActionEvent e) {
    	if ( e.getActionCommand().equals("Drop Resources")) { 
    		System.out.println("drop resources");
			this.theWorld.setMouseState(World.MouseClickStates.DROP_RESOURCES);
    	} else if (e.getActionCommand().equals("NukeEm!")) { 
    		System.out.println("nuke em!");
			this.theWorld.setMouseState(World.MouseClickStates.NUKE);

    	}
  
    }
	
}
