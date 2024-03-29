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
        
        Vector position;
        Vector direction;
        
        populationSize = (Constants.SIMU_OVERRIDE_POPULATION_SIZE > - 1)? Constants.SIMU_OVERRIDE_POPULATION_SIZE : populationSize;
        
        for (int i = 0; i < populationSize; i++) {
            position = Vector.randomVector(0, simulation.getWidth(), 0, simulation.getHeight());
            
            if (Constants.SIMU_AGENTS_SHOULD_FACE_CENTER) {
                direction = position.clone().orientTowardsCoord(new Vector(simulation.getWidth() / 2, simulation.getHeight() / 2)).scale(-1).toUnitVect(); // face screen center
            } else {
                direction = Vector.randomUnitVect();
            }
            
            this.agents.add(new Agent(
                speciesIdD,
                position,
                direction, 
                Constants.SIMU_SENSOR_ANGLE,
                Constants.SIMU_SENSOR_OFFSET,
                Constants.SIMU_ROTATION_ANGLE,
                Constants.SIMU_SENSOR_RANGE,
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
