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
    private final List<Runnable> agentGUIRunnables = new ArrayList();
    private final List<Thread> agentGUIThreads = new ArrayList();



    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        
        this.simulation.addSimuUpdateEventListener(this);
        
        setSize(this.simulation.getWidth(), this.simulation.getHeight());
        
        // OBSERVER : trail map
        this.trailMapObserver = new TrailMapObserver(
                this.simulation.getTrailMap(),
                this.simulation.getIsSpeciesActive(),
                this.simulation.getSpeciesColors());
           
        // OBSERVER : agents
        for (Species species : this.simulation.getSpecies()){
            for (Agent agent : species.getAgents()) {
                AgentObserver agentObserver = new AgentObserver(agent, species.getColor());
                this.agentObservers.add(agentObserver);
                this.agentGUIRunnables.add((Runnable) () -> agentObserver.print(SimulationPanel.this.getGraphics()));
            }
        }
        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        
        System.out.println("PAINTING GRAPHIC");
        
        this.trailMapObserver.print(g);
        /*for (Runnable agentGUIRunnable : this.agentGUIRunnables) {
            agentGUIRunnable.run();
        }*/
        for (AgentObserver agentObserver : agentObservers) {
            agentObserver.print(g);
        }
        /*try {
            this.agentGUIThreads.clear();
            
            this.agentGUIRunnables.stream().map((r) -> {
                this.agentGUIThreads.add(new Thread(r));
                return r;
            });
            
            this.agentGUIThreads.stream().forEachOrdered((_item) -> _item.start());

            for (Thread t : this.agentGUIThreads) {
                t.join();
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    
    
    @Override
    public void onSimuUpdateEventTriggered() {
        System.out.println("SIMU UPDATE EVENT RECEIVED");
        repaint();
    }

    /*public Simulation getSimulation() {
        return simulation;
    }

    
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        setSize(this.simulation.getWidth(), this.simulation.getHeight());
        onSimuUpdateEventTriggered();
    }*/

    
    
    
    
    
}
