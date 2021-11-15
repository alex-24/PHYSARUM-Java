/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view.gui.observers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import physarum_2d.controller.Simulation;
import physarum_2d.model.Agent;
import physarum_2d.model.Vector;
import physarum_2d.view.GUIObserver;
import physarum_2d.view.Constants;

/**
 *
 * @author Alexis Cassion
 */
public class AgentObserver implements GUIObserver {
    
    private final Simulation simulation;
    private final Agent agent;
    private final Color color;

    
    public AgentObserver(Simulation simulation, Agent agent, Color color) {
        this.simulation = simulation;
        this.agent = agent;
        this.color = color;
    }   

    @Override
    public void print(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;
        
        int aSize = Constants.AGENT_SIZE;
        int aSizeHalf = Constants.AGENT_SIZE / 2;
        
        int x = (int) this.agent.getPosition().getX() - aSizeHalf;
        int y = (int) this.agent.getPosition().getY() - aSizeHalf;
        
        g.setColor(this.color.darker().darker());
        g.fillOval(x, y, aSize, aSize);
        
        
        Vector agentPos = agent.getPosition();
        Vector agentDir = agent.getDirection().toUnitVect();
        Vector agentSensorF = agentPos.clone().add(agentDir.clone().scale(agent.getSensorOffset()));
        Vector agentSensorFL = agentPos.clone().add(agentDir.clone().scale(agent.getSensorOffset()).rotate(-agent.getSensorAngle()));
        Vector agentSensorFR = agentPos.clone().add(agentDir.clone().scale(agent.getSensorOffset()).rotate(agent.getSensorAngle()));
        
        //System.out.println("-- F  : " + agentSensorF);
        //System.out.println("-- FL : " + agentSensorFL);
        //System.out.println("-- FR : " + agentSensorFR);

        int Fx = Math.max(0, Math.min(this.simulation.getWidth() - 1, (int) agentSensorF.getX()));
        int Fy = Math.max(0, Math.min(this.simulation.getHeight() - 1, (int) agentSensorF.getY()));
        int FLx = Math.max(0, Math.min(this.simulation.getWidth() - 1, (int) agentSensorFL.getX()));
        int FLy = Math.max(0, Math.min(this.simulation.getHeight() - 1, (int) agentSensorFL.getY()));
        int FRx = Math.max(0, Math.min(this.simulation.getWidth() - 1, (int) agentSensorFR.getX()));
        int FRy = Math.max(0, Math.min(this.simulation.getHeight() - 1, (int) agentSensorFR.getY()));
        
        //g.setColor(Color.RED);
        g.drawLine(x, y, Fx, Fy);
        //g.setColor(Color.GREEN);
        g.drawLine(x, y, FLx, FLy);
        //g.setColor(Color.BLUE);
        g.drawLine(x, y, FRx, FRy);
    }
    
}
