/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import physarum_2d.controller.Simulation;

/**
 *
 * @author Alexis Cassion
 */
public class AppWindow extends JFrame {

    private final JPanel simulationPanel;
    private final JPanel optionsPanel;

    public AppWindow() {
        
        super("PHYSARUM - A 2D SIMULATION");
        setSize(500, 500);
        setVisible(true);
        
        Simulation simulation = new Simulation(
                200,
                200,
                1,
                new Boolean[] {true, false, false, true},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},
                new int[] {3, 3, 3, 3},
                0,
                0
        );
        
        Thread simulationThread = new Thread(simulation);

        simulationPanel = new SimulationPanel(simulation);
        simulationPanel.setSize(new Dimension(200, 200));
        simulationPanel.setLayout(new BorderLayout(0, 0));

        optionsPanel = new JPanel();
        optionsPanel.setPreferredSize(new Dimension(150, 200));


        //simulationPanel.add(optionsPanel, BorderLayout.LINE_START);


        // color debugging
        simulationPanel.setBackground(Color.WHITE);
        //optionsPanel.setBackground(Color.RED);
        setContentPane(simulationPanel);
        
        //pack();
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        simulationThread.start();
    }
    
}
