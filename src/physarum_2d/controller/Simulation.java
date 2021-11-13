/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import physarum_2d.model.Agent;
import physarum_2d.model.Species;
import physarum_2d.model.Vector;
import physarum_2d.view.SimuUpdateEventListener;
import physarum_2d.view.SimuUpdateEventSender;
import physarum_2d.view.Constants;

/**
 *
 * @author Alexis Cassion
 */
public class Simulation implements Runnable, SimuUpdateEventSender {
    
    
    private final List<SimuUpdateEventListener> simuUpdateEventListeners = new ArrayList<>();
    
    private int width;
    private int height;
    private int populationPercentage;
    private int diffusionKernel;
    private float wProj;
    
    
    private List<Species> species = new ArrayList<>();
    private final List<Runnable> agentMotorRunnables = new ArrayList<>();
    private final List<Runnable> agentSensoryRunnables = new ArrayList<>();
    private final List<Thread> agentThreads = new ArrayList<>();

    private final List<Runnable> trailDecayerRunnables = new ArrayList();
    private final List<Thread> trailDecayerThreads = new ArrayList();
    
    private Color[][] trailMap;
    Boolean[] isSpeciesActive;
    private int[] trailsDecayValues;
    private int nbOfSpecies;

    public Simulation(int width, int height, int populationPercentage, Boolean[] isSpeciesActive, Color[] speciesColor, int[] trailsDecayValues, int diffusionKernel, float wProj) {
        this.width = width;
        this.height = height;
        this.populationPercentage = populationPercentage;
        this.isSpeciesActive = isSpeciesActive;
        this.trailsDecayValues = trailsDecayValues;
        this.diffusionKernel = diffusionKernel;
        this.wProj = wProj;
        
        this.trailMap = new Color[this.width][this.height];
        
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.trailMap[i][j] = new Color(0, 0, 0);
            }
        }
        
        int totalPopulation = (int) ((this.width * this.height) * (this.populationPercentage / 100.0));
        
        this.nbOfSpecies = (int) Arrays.stream(this.isSpeciesActive, 0, this.isSpeciesActive.length).filter(t -> t).count();
        int speciesPopulation = totalPopulation / this.nbOfSpecies;
        
        System.out.println("Population of : " + totalPopulation);
        System.out.println(this.nbOfSpecies + " species");
        System.out.println(speciesPopulation + " per species");
        
        for (int i = 0; i < this.isSpeciesActive.length; i++) {
            if (this.isSpeciesActive[i]){
                this.species.add(new Species(this, i, speciesPopulation, speciesColor[i], 2));
            }
        }
        
        prepareRunnables();
        
    }

    @Override
    public void run() {
        
        while (true) {
            try {
                int agentIdx;
                
                // System.out.println("--" + this.species.get(0).getAgents().get(0).getPosition());
                
                // TRAIL DECAY -------------------------------------------------
                System.out.println("DECAY STAGE");
                decayTrailsNonConcurrent();
                
                // MOTOR STAGE -------------------------------------------------
                System.out.println("MOTOR STAGE");
                this.agentThreads.clear();
                agentIdx = 0;
                for (int i=0; i<this.species.size(); i++) {
                    for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                        this.agentThreads.add(new Thread(this.agentMotorRunnables.get(agentIdx)));
                        this.agentThreads.get(agentIdx).start();
                        agentIdx++;
                    }
                }
                for (Thread agentThread : this.agentThreads) {
                        agentThread.join();
                }
                
                System.out.println("SENSORY STAGE");
                // SENSORY STAGE -----------------------------------------------
                this.agentThreads.clear();
                agentIdx = 0;
                for (int i=0; i<this.species.size(); i++) {
                    for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                        this.agentThreads.add(new Thread(this.agentSensoryRunnables.get(agentIdx)));
                        this.agentThreads.get(agentIdx).start();
                        agentIdx++;
                    }
                }
                for (Thread agentThread : this.agentThreads) {
                        agentThread.join();
                }
                
                // DELAY NEXT SIMULATION STEP FOR GUI
                notifySimuUpdateEventListeners();
                Thread.sleep(Constants.SIMULATION_UPDATE_DELAY_IN_MILLIS);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    private void prepareRunnables() {
        for (Species species : this.species) {
            for (Agent agent : species.getAgents()) {
                this.agentMotorRunnables.add(new Runnable() {
                    
                    private boolean wasOtherSpeciesPresent(int species, Color trailMapCell) {
                        
                        switch(species) {
                            case 0://R
                                return trailMapCell.getGreen() != 0 && trailMapCell.getBlue() != 0;
                            
                            case 1://G
                                return trailMapCell.getRed() != 0 && trailMapCell.getBlue() != 0;
                            
                            case 2://B
                                return trailMapCell.getRed() != 0 && trailMapCell.getGreen() != 0;
                                
                            default:
                                return false;
                        }
                    }
                    @Override
                    public void run() {
                        
                        Vector newPos = agent.getPosition().clone();
                        newPos.add(agent.getDirection().clone().scale(agent.getStepSize()));
                        
                        boolean wouldHitAWall = (newPos.getX() < 0 || newPos.getY() < 0 || newPos.getX() >= Simulation.this.width || newPos.getY() >= Simulation.this.height);
                        boolean wouldMeetOtherSpecies = (wouldHitAWall)? false : wasOtherSpeciesPresent(agent.getSpecies(), trailMap[(int) newPos.getX()][(int) newPos.getY()]);
                        
                        if (wouldHitAWall || wouldMeetOtherSpecies) {
                            agent.getDirection().rotate(Math.random() * Math.PI * 2).toUnitVec();
                        } else {
                            agent.setPosition(newPos);
                            depositPheromonesAtCurrentLocation(agent);
                        }
                        
                    }
                });
                
                this.agentSensoryRunnables.add(new Runnable() {
                    // todo: check whole sensor area
                    private int getTrailQuantity(int species, Color trailMapCell){
                        
                        switch(species) {
                            case 0://R
                                return trailMapCell.getRed();
                            
                            case 1://G
                                return trailMapCell.getGreen();
                            
                            case 2://B
                                return trailMapCell.getBlue();
                                
                            default:
                                return 0;
                        }
                    }
                    
                    @Override
                    public void run() {
                        Vector agentPosCopy = agent.getPosition().clone();
                        Vector agentDir = agent.getPosition();
                        Vector agentSensorF = agentPosCopy.add(agentDir.clone().scale(agent.getSensorOffset()));
                        Vector agentSensorFL = agentPosCopy.add(agentDir.clone().rotate(- agent.getSensorAngle()).scale(agent.getSensorOffset()));
                        Vector agentSensorFR = agentPosCopy.add(agentDir.clone().rotate(agent.getSensorAngle()).scale(agent.getSensorOffset()));
                        
                        int Fx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorF.getX()));
                        int Fy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorF.getY()));
                        int FLx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorFL.getX()));
                        int FLy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorFL.getY()));
                        int FRx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorFR.getX()));
                        int FRy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorFR.getY()));
                        
                        int F = getTrailQuantity(agent.getSpecies(), trailMap[Fx][Fy]);
                        int FL = getTrailQuantity(agent.getSpecies(), trailMap[FLx][FLy]);
                        int FR = getTrailQuantity(agent.getSpecies(), trailMap[FRx][FRy]);
                        
                        if (F > FL && F > FR) {
                            // do nothing
                            
                        } else if (F < FL && F >FR) {
                            if (Math.random() > 0.5) {
                                agentDir.rotate(agent.getRotationAngle()).toUnitVec();
                            } else {
                                agentDir.rotate(-agent.getRotationAngle()).toUnitVec();
                            }
                            
                        } else if (F < FL) {
                            agentDir.rotate(-agent.getRotationAngle()).toUnitVec();
                        
                        } else if (F < FR) {
                            agentDir.rotate(agent.getRotationAngle()).toUnitVec();
                        }
                    }
                });
            }
        }
        
        /*for (int i = 0; i < this.height; i++) {
            int lineToDecay = i;
            this.trailDecayerRunnables.add(new Runnable() {
                @Override
                public void run() {
                    Color currentCellColor;
                    
                    for (int j = 0; j < Simulation.this.width; j++) {
                        Simulation.this.trailMap[i][j] = Color.black;
                    }
                    
                }
            });
        }*/
    }
    
    private synchronized void depositPheromonesAtCurrentLocation(Agent agent) {
        int x = (int) agent.getPosition().getX();
        int y = (int) agent.getPosition().getY();
        Color color = this.trailMap[x][y];
        
        
        //System.out.print(this.trailMap[x][y].getRed() + " --> ");
        
        switch (agent.getSpecies()) {
            case 0://R
                int redPlusDep = Math.min(255, color.getRed() + agent.getDepositionT());
                this.trailMap[x][y] = new Color(redPlusDep, color.getGreen(), color.getBlue());
                return;

            case 1://G
                int greenPlusDep = Math.min(255, color.getGreen() + agent.getDepositionT()); 
                this.trailMap[x][y] = new Color(color.getRed(), greenPlusDep, color.getBlue());
                return;

            case 2://B
                int bluePlusDep = Math.min(255, color.getBlue() + agent.getDepositionT()); 
                this.trailMap[x][y] = new Color(color.getRed(), color.getGreen(), bluePlusDep);
                return;        
        }
        
        //System.out.println(this.trailMap[x][y].getRed());
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPopulationPercentage() {
        return populationPercentage;
    }

    public void setPopulationPercentage(int populationPercentage) {
        this.populationPercentage = populationPercentage;
    }

    public int getDiffusionKernel() {
        return diffusionKernel;
    }

    public void setDiffusionKernel(int diffusionKernel) {
        this.diffusionKernel = diffusionKernel;
    }

    public float getwProj() {
        return wProj;
    }

    public void setwProj(float wProj) {
        this.wProj = wProj;
    }

    public List<Species> getSpecies() {
        return species;
    }

    public void setSpecies(List<Species> species) {
        this.species = species;
    }

    public Color[][] getTrailMap() {
        return trailMap;
    }

    public void setTrailMap(Color[][] trailMap) {
        this.trailMap = trailMap;
    }
    
    public Color[] getSpeciesColors() {
        Color[] colors = new Color[this.species.size()];
        for (int i = 0; i < this.species.size(); i++) {
            colors[i] = this.species.get(i).getColor();
        }
        
        return colors;
    }

    @Override
    public void addSimuUpdateEventListener(SimuUpdateEventListener listener) {
        this.simuUpdateEventListeners.add(listener);
    }

    @Override
    public void notifySimuUpdateEventListeners() {
        System.out.println("SIMU UPDATE EVENT SENT");
        for (SimuUpdateEventListener simuUpdateEventListener : this.simuUpdateEventListeners) {
            simuUpdateEventListener.onSimuUpdateEventTriggered();
        }
    }

    private void decayTrailsNonConcurrent() {
        
        Color currentColor;
        
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                currentColor = this.trailMap[i][j];
                this.trailMap[i][j] = new Color(
                        sanitizeValue(currentColor.getRed() - this.trailsDecayValues[0], 0, 255),
                        sanitizeValue(currentColor.getGreen() - this.trailsDecayValues[1], 0, 255),
                        sanitizeValue(currentColor.getBlue() - this.trailsDecayValues[2], 0, 255)
                );
            }
        }
    }
    
    private int sanitizeValue(int value, int min, int max) {
        return Math.max(Math.min(value, max), 0);
    }

    public Boolean[] getIsSpeciesActive() {
        return isSpeciesActive;
    }

    public void setIsSpeciesActive(Boolean[] isSpeciesActive) {
        this.isSpeciesActive = isSpeciesActive;
        this.nbOfSpecies = (int) Arrays.stream(this.isSpeciesActive, 0, this.isSpeciesActive.length).filter(t -> t).count();
    }

    public int getNbOfSpecies() {
        return this.nbOfSpecies;
    }
    
}
