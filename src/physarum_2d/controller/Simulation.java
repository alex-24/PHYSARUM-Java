/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import physarum_2d.model.Agent;
import physarum_2d.model.Species;
import physarum_2d.model.Vector;
import physarum_2d.view.SimuUpdateEventListener;
import physarum_2d.view.SimuUpdateEventSender;
import physarum_2d.view.Constants;
import physarum_2d.view.GUIUpdateEventListener;

/**
 *
 * @author Alexis Cassion
 */
public class Simulation extends Thread implements SimuUpdateEventSender, GUIUpdateEventListener {
    
    private static final Random random = new Random();
    private boolean waitForGUIUpdate = true;
    private boolean isSimulationPaused = false;
    
    
    private final List<SimuUpdateEventListener> simuUpdateEventListeners = new ArrayList<>();
    
    private int width;
    private int height;
    private int populationPercentage;
    private int diffusionKernel;
    private float wProj;
    
    
    
    private List<Species> species = new ArrayList<>();
    
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
        
        //System.out.println("Population of : " + totalPopulation);
        //System.out.println(this.nbOfSpecies + " species");
        //System.out.println(speciesPopulation + " per species");
        
        for (int i = 0; i < this.isSpeciesActive.length; i++) {
            if (this.isSpeciesActive[i]){
                this.species.add(new Species(this, i, speciesPopulation, speciesColor[i], 2));
            } else {
                this.species.add(null);
            }
        }
        
    }

    @Override
    public void run() {
        
        while (true) {
            try {
                this.waitForGUIUpdate = true;
                
                
                // SENSORY STAGE -----------------------------------------------
                //System.out.println("SENSORY STAGE");
                //Collections.shuffle(this.species);
                for (int i=0; i<this.species.size(); i++) {
                    if (this.species.get(i) != null) {
                        for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                            executeSensoryStage(this.species.get(i).getAgents().get(j));
                        }
                    }
                }
                
                
                //System.out.println("-------------------------------------------------");
                // MOTOR STAGE -------------------------------------------------
                //System.out.println("MOTOR STAGE");
                //Collections.shuffle(this.species);
                for (int i=0; i<this.species.size(); i++) {
                    if (this.species.get(i) != null) {
                        for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                            executeMotorStage(this.species.get(i).getAgents().get(j));
                        }
                    }
                }
                
                
                // TRAIL DIFFUSION -------------------------------------------------
                //System.out.println("DIFFUSION STAGE");
                diffuseAndDecayTrailMap();
                
                
                // DELAY NEXT SIMULATION STEP FOR GUI
                notifySimuUpdateEventListeners();
                
                while (this.waitForGUIUpdate || this.isSimulationPaused) {
                    Thread.sleep(1);
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
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
    
    private void executeMotorStage(Agent agent) {
                        
        Vector newPos = agent.getPosition().clone();
        newPos.add(agent.getDirection().clone().scale(agent.getStepSize()));

        boolean wouldHitAWall = (newPos.getX() < 0 || newPos.getY() < 0 || newPos.getX() >= Simulation.this.width || newPos.getY() >= Simulation.this.height);
        boolean wouldMeetOtherSpecies = (wouldHitAWall)? false : wasOtherSpeciesPresent(agent.getSpecies(), trailMap[(int) newPos.getX()][(int) newPos.getY()]);

        if (wouldHitAWall || wouldMeetOtherSpecies) {
            agent.getDirection().rotateRandomly().toUnitVect();
        } else {
            agent.setPosition(newPos);
            deposit(agent);
        }
        
    }
    
    public int getTrailQuantity(int species, int sensorRange, int sensorX, int sensorY){
        
        int ownSpecies = 0;
        int foreignSpecies = 0;
        Color trailMapCell;
        
        for (int i = sensorX - sensorRange; i < sensorX + sensorRange + 1; i++) {
            for (int j = sensorY - sensorRange; j < sensorY + sensorRange + 1; j++) {
                if (i >= 0 && i < this.width && j >= 0 && j < this.height) {
                    trailMapCell = this.trailMap[i][j];
                    switch(species) {
                        case 0://R
                            ownSpecies += trailMapCell.getRed();
                            foreignSpecies += trailMapCell.getGreen() + trailMapCell.getBlue();
                            break;

                        case 1://G
                            ownSpecies += trailMapCell.getGreen() ;
                            foreignSpecies += trailMapCell.getRed() + trailMapCell.getBlue();
                            break;

                        case 2://B
                            ownSpecies += trailMapCell.getBlue();
                            foreignSpecies += trailMapCell.getRed() + trailMapCell.getGreen();
                    }
                }   
            }
        }
        
        return ownSpecies - foreignSpecies;
    }
    
    private void executeSensoryStage(Agent agent) {
        Vector agentPos = agent.getPosition();
        Vector agentDir = agent.getDirection();
        Vector agentSensorF = agentPos.clone().add(agentDir.clone().scale(agent.getSensorOffset()));
        Vector agentSensorFL = agentPos.clone().add(agentDir.clone().rotate(- agent.getSensorAngle()).scale(agent.getSensorOffset()));
        Vector agentSensorFR = agentPos.clone().add(agentDir.clone().rotate(agent.getSensorAngle()).scale(agent.getSensorOffset()));

        int Fx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorF.getX()));
        int Fy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorF.getY()));
        int FLx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorFL.getX()));
        int FLy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorFL.getY()));
        int FRx = Math.max(0, Math.min(Simulation.this.width - 1, (int) agentSensorFR.getX()));
        int FRy = Math.max(0, Math.min(Simulation.this.height - 1, (int) agentSensorFR.getY()));

        int F = getTrailQuantity(agent.getSpecies(), Constants.SIMU_SENSOR_RANGE, Fx, Fy);
        int FL = getTrailQuantity(agent.getSpecies(), Constants.SIMU_SENSOR_RANGE, FLx, FLy);
        int FR = getTrailQuantity(agent.getSpecies(), Constants.SIMU_SENSOR_RANGE, FRx, FRy);

        if (F > FL && F > FR) {
            if (F < 0) {
                agent.getDirection().rotateRandomly().toUnitVect();
            }

        } else if (F < FL && F < FR) {
            if (random.nextDouble() > 0.5) {
                agentDir.rotate(agent.getRotationAngle()).toUnitVect();
            } else {
                agentDir.rotate(-agent.getRotationAngle()).toUnitVect();
            }

        } else if (F < FL) {
            agentDir.rotate(-agent.getRotationAngle()).toUnitVect();

        } else if (F < FR) {
            agentDir.rotate(agent.getRotationAngle()).toUnitVect();
        }
    }
    
    private void deposit(Agent agent) {
        int x = (int) agent.getPosition().getX();
        int y = (int) agent.getPosition().getY();
        Color color = this.trailMap[x][y];
        
        //this.storedDeposits[agent.getSpecies()].add(agent.getPosition());
        
        switch (agent.getSpecies()) {
            case 0://R
                int redPlusDep = Math.min(255, color.getRed() + agent.getDepositionT());
                this.trailMap[x][y] = new Color(redPlusDep, color.getGreen(), color.getBlue());
                break;

            case 1://G
                int greenPlusDep = Math.min(255, color.getGreen() + agent.getDepositionT()); 
                this.trailMap[x][y] = new Color(color.getRed(), greenPlusDep, color.getBlue());
                break;

            case 2://B
                int bluePlusDep = Math.min(255, color.getBlue() + agent.getDepositionT()); 
                this.trailMap[x][y] = new Color(color.getRed(), color.getGreen(), bluePlusDep);
        }
    }

    private void diffuseAndDecayTrailMap() {
        
        // diffuse
        Color[][] trailMap = new Color[this.width][this.height];
        
        for (int i = 0; i < trailMap.length; i++) {
            for (int j = 0; j < trailMap[0].length; j++) {
                trailMap[i][j] = calcDiffusionAt(i, j);
            }
        }
        this.trailMap = trailMap;
        
        Color currentColor;
        double decayFactor = (100.0 - Constants.SIMU_DECAY_PERCENTAGE_T) / 100.0;
        
        // decay
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                currentColor = this.trailMap[i][j];
                
                this.trailMap[i][j] = new Color(
                        Math.max((int)(currentColor.getRed()  * decayFactor), 0),
                        Math.max((int)(currentColor.getGreen()* decayFactor), 0),
                        Math.max((int)(currentColor.getBlue() * decayFactor), 0));
            }
        }
    }
    
    private Color calcDiffusionAt(int x, int y) {
        
        int iStart = x - this.diffusionKernel;
        int iEnd = x + this.diffusionKernel + 1;
        
        int jStart = y - this.diffusionKernel;
        int jEnd = y + this.diffusionKernel + 1;
        
        int R = 0;
        int G = 0;
        int B = 0;
        
        int totalCells = 0;
        //int totalCells = ((int) Math.pow((2 * this.diffusionKernel + 1), 2));
        
        for (int i = iStart; i < iEnd; i++) {
            for (int j = jStart; j < jEnd; j++) {
                if (i >= 0 && i < this.width && j >= 0 && j < this.height) {
                    R += this.trailMap[i][j].getRed();
                    G += this.trailMap[i][j].getGreen();
                    B += this.trailMap[i][j].getBlue();
                    totalCells++;
                }
            }
        }
        
        R /= totalCells;
        G /= totalCells;
        B /= totalCells;
        
        return new Color(R, G, B);
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
        Species species;
        for (int i = 0; i < this.species.size(); i++) {
            species = this.species.get(i);
            colors[i] = (species != null)? species.getColor() : null;
        }
        
        return colors;
    }

    @Override
    public void addSimuUpdateEventListener(SimuUpdateEventListener listener) {
        this.simuUpdateEventListeners.add(listener);
    }

    @Override
    public void notifySimuUpdateEventListeners() {
        //System.out.println("SIMU UPDATE EVENT SENT ->");
        for (SimuUpdateEventListener simuUpdateEventListener : this.simuUpdateEventListeners) {
            simuUpdateEventListener.onSimuUpdateEventTriggered();
        }
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

    @Override
    public void onGUIUpdateEventTriggered() {
        this.waitForGUIUpdate = false;
    }
    
}
