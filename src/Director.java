import gui.ControlPanel;
import gui.World;

import java.awt.BorderLayout;

import javax.swing.*;

class Director extends JFrame {

    private final World simWorld;
    private final ControlPanel gamePanel;

    private Director() {
        super("Director");
        this.simWorld = new World();
        this.add(this.simWorld);
        this.gamePanel = new ControlPanel(this.simWorld);
        this.add(this.gamePanel, BorderLayout.EAST);
        this.simWorld.startWorld();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(Director::new);
    }

}
