import gui.ControlPanel;
import gui.World;

import java.awt.BorderLayout;

import javax.swing.*;

class Director extends JFrame {

    private Director() {
        super("Director");
        World simWorld = new World();
        this.add(simWorld);
        ControlPanel gamePanel = new ControlPanel(simWorld);
        this.add(gamePanel, BorderLayout.EAST);
        simWorld.startWorld();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(Director::new);
    }

}
