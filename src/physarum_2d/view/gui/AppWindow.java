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
import physarum_2d.view.Constants;

/**
 *
 * @author Alexis Cassion
 */
public class AppWindow extends JFrame {

    private final JPanel simulationPanel;
    private final JPanel optionsPanel;

    public AppWindow() {
        
        super("PHYSARUM - A 2D SIMULATION");
        
        setVisible(true);
        setLocationRelativeTo(null);
        
        Boolean[] isSpeciesActive = Constants.SIMU_IS_SPECIES_ACTIVE;
        Color[] speciesColors = new Color[] {Color.RED, Color.GREEN, Color.BLUE};
        int[] speciesDecayPercentage = new int[] {Constants.SIMU_DECAY_T, Constants.SIMU_DECAY_T, Constants.SIMU_DECAY_T};
        
        Simulation simulation = new Simulation(
                Constants.SIMU_WIDTH,
                Constants.SIMU_HEIGHT,
                Constants.SIMU_POPULATION_PERCENTAGE,
                isSpeciesActive,
                speciesColors,
                speciesDecayPercentage,
                Constants.SIMU_DIFFUSION_RANGE,
                0
        );
        

        simulationPanel = new SimulationPanel(simulation);
        
        setLayout(new BorderLayout(0, 0));

        optionsPanel = new JPanel();
        optionsPanel.setPreferredSize(new Dimension(150, 200));


        //simulationPanel.add(optionsPanel, BorderLayout.LINE_START);


        simulationPanel.setPreferredSize(new Dimension(Constants.SIMU_WIDTH, Constants.SIMU_HEIGHT));
        
        // color debugging
        simulationPanel.setBackground(Color.WHITE);
        //optionsPanel.setBackground(Color.RED);
        setBackground(Color.BLACK);
        //setContentPane(simulationPanel);
        add(simulationPanel);
        pack();
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        simulation.start();
    }
    
}
