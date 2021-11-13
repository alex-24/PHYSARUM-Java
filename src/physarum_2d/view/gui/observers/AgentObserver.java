/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view.gui.observers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import physarum_2d.model.Agent;
import physarum_2d.view.GUIObserver;
import physarum_2d.view.UI_CONSTANTS;

/**
 *
 * @author Alexis Cassion
 */
public class AgentObserver implements GUIObserver {
    
    private final Agent agent;
    private final Color color;

    
    public AgentObserver(Agent agent, Color color) {
        this.agent = agent;
        this.color = color;
    }   

    @Override
    public void print(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;
        
        g.setColor(this.color);
        g.fillOval((int) this.agent.getPosition().getX(), (int) this.agent.getPosition().getY(), UI_CONSTANTS.AGENT_SIZE, UI_CONSTANTS.AGENT_SIZE);
    }
    
}
