/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import physarum_2d.controller.Simulation;
import physarum_2d.model.Agent;
import physarum_2d.model.Species;
import physarum_2d.view.gui.observers.AgentObserver;
import physarum_2d.view.gui.observers.TrailMapObserver;
import physarum_2d.view.GUIObserver;
import physarum_2d.view.SimuUpdateEventListener;

/**
 *
 * @author Alexis Cassion
 */
public class SimulationPanel extends JPanel implements SimuUpdateEventListener {
    
    private Simulation simulation;
    private final List<AgentObserver> agentObservers = new ArrayList<>();
    private final TrailMapObserver trailMapObserver;



    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        
        this.simulation.addSimuUpdateEventListener(this);
        
        setSize(this.simulation.getWidth(), this.simulation.getHeight());
        
        // OBSERVER : trail map
        this.trailMapObserver = new TrailMapObserver(this.simulation);
           
        // OBSERVER : agents
        for (Species species : this.simulation.getSpecies()){
            for (Agent agent : species.getAgents()) {
                AgentObserver agentObserver = new AgentObserver(this.simulation, agent, species.getColor());
                this.agentObservers.add(agentObserver);
            }
        }
        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        
        System.out.println("PAINTING GRAPHIC");
        
        this.trailMapObserver.print(g);
        this.agentObservers.forEach((agentObserver) -> agentObserver.print(g));
    }
    
    
    
    @Override
    public void onSimuUpdateEventTriggered() {
        System.out.println("SIMU UPDATE EVENT RECEIVED");
        repaint();
    }
    
}
