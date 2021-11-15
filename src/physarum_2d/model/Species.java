/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import physarum_2d.controller.Simulation;
import physarum_2d.view.Constants;

/**
 *
 * @author Alexis Cassion
 */
public class Species {
    
    private Color color;
    private List<Agent> agents;
    private int decayT;


    public Species(Simulation simulation, int speciesIdD, int populationSize, Color color, int decayT) {
        this.color = color;
        this.agents = new ArrayList<>();
        this.decayT = decayT;
        
        for (int i = 0; i < populationSize; i++) {
            this.agents.add(new Agent(
                speciesIdD,
                Vector.randomVector(0, simulation.getWidth(), 0, simulation.getHeight()),
                Vector.randomUnitVect(),
                (Math.PI / 8) / Math.PI,
                90,
                (Math.PI / 8) / Math.PI,
                1,
                Constants.SIMU_STEP_SIZE,
                Constants.SIMU_DEP_T,
                0f 
            ));
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public int getDecayT() {
        return decayT;
    }

    public void setDecayT(int decayT) {
        this.decayT = decayT;
    }
    
}
