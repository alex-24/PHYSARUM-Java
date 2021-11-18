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

/**
 *
 * @author Alexis Cassion
 */
public class Simulation extends Thread implements SimuUpdateEventSender {
    
    private static final Random random = new Random();
    
    
    private final List<SimuUpdateEventListener> simuUpdateEventListeners = new ArrayList<>();
    
    private int width;
    private int height;
    private int populationPercentage;
    private int diffusionKernel;
    private float wProj;
    
    
    
    private List<Species> species = new ArrayList<>();
    List<Vector>[] storedDeposits;
    
    private Color[][] trailMap;
    Boolean[] isSpeciesActive;
    private int[] trailsDecayValues;
    private int nbOfSpecies;
    
    /*private ScheduledExecutorService diffusionExecutor = Executors.newSingleThreadScheduledExecutor();
    private Runnable diffusionRunnable = new Runnable() {
        @Override
        public void run() {
            // TRAIL DIFFUSION -------------------------------------------------
            System.out.println("DIFFUSION STAGE");
            diffuseEntireTrailMap();
        }  
    };
    
    private ScheduledExecutorService decayExecutor = Executors.newSingleThreadScheduledExecutor();
    private Runnable decayRunnable = new Runnable() {
        @Override
        public void run() {
            // TRAIL DECAY -------------------------------------------------
            System.out.println("DECAY STAGE");
            decayEntireTrailMap();
        }  
    };*/

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
        
        this.storedDeposits = new ArrayList[this.isSpeciesActive.length];
        
        for (int i = 0; i < this.storedDeposits.length; i++) {
            this.storedDeposits[i] = new ArrayList<>();
        }
        
        int speciesPopulation = totalPopulation / this.nbOfSpecies;
        
        System.out.println("Population of : " + totalPopulation);
        System.out.println(this.nbOfSpecies + " species");
        System.out.println(speciesPopulation + " per species");
        
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
        
        //this.diffusionExecutor.scheduleAtFixedRate(this.diffusionRunnable, 1, Constants.SIMU_DIFFUSION_INTERVAL_IN_MILLIS, TimeUnit.MILLISECONDS);
        //this.decayExecutor.scheduleAtFixedRate(this.decayRunnable, 1, Constants.SIMU_DECAY_INTERVAL_IN_MILLIS, TimeUnit.MILLISECONDS);
        
        while (true) {
            try {
                System.out.println("-------------------------------------------------");
                // System.out.println("--" + this.species.get(0).getAgents().get(0).getPosition());
                //System.out.println("--" + this.species.get(0).getAgents().get(0).getDirection());
                
                // MOTOR STAGE -------------------------------------------------
                //Collections.shuffle(this.species);
                System.out.println("MOTOR STAGE");
                Collections.shuffle(this.species);
                for (int i=0; i<this.species.size(); i++) {
                    if (this.species.get(i) != null) {
                        for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                            executeMotorStage(this.species.get(i).getAgents().get(j));
                        }
                    }
                }
                
                
                System.out.println("SENSORY STAGE");
                // SENSORY STAGE -----------------------------------------------
                //Collections.shuffle(this.species);
                for (int i=0; i<this.species.size(); i++) {
                    if (this.species.get(i) != null) {
                        for (int j=0; j<this.species.get(i).getAgents().size(); j++) {
                            executeSensoryStage(this.species.get(i).getAgents().get(j));
                        }
                    }
                }
                
                
                // TRAIL DIFFUSION -------------------------------------------------
                System.out.println("DIFFUSION STAGE");
                diffuseEntireTrailMap();

                // TRAIL DECAY -------------------------------------------------
                System.out.println("DECAY STAGE");
                decayEntireTrailMap();
                
                
                // DEPOSIT -------------------------------------------------
                System.out.println("DEPOSIT STAGE");
                placeStoredDeposits();
                
                
                // DELAY NEXT SIMULATION STEP FOR GUI
                notifySimuUpdateEventListeners();
                Thread.sleep(Constants.SIMU_UPDATE_INTERVAL_IN_MILLIS);
                
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
            storeDeposit(agent);
        }
        
    }
    
    public int getTrailQuantity(int species, int sensorRange, int sensorX, int sensorY){
        
        int result = 0;
        Color trailMapCell;
        
        for (int i = sensorX - sensorRange; i < sensorX + sensorRange + 1; i++) {
            for (int j = sensorY - sensorRange; j < sensorY + sensorRange + 1; j++) {
                if (i >= 0 && i < this.width && j >= 0 && j < this.height) {
                    trailMapCell = this.trailMap[i][j];
                    switch(species) {
                        case 0://R
                            result += trailMapCell.getRed() - (trailMapCell.getGreen() + trailMapCell.getBlue());
                            break;

                        case 1://G
                            result += trailMapCell.getGreen() - (trailMapCell.getRed() + trailMapCell.getBlue());
                            break;

                        case 2://B
                            result += trailMapCell.getBlue() - (trailMapCell.getRed() + trailMapCell.getGreen());
                            break;
                    }
                }   
            }
        }
        
        return result;
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
        int FL = getTrailQuantity(agent.getSpecies(), Constants.SIMU_SENSOR_RANGE, FRx, FLy);
        int FR = getTrailQuantity(agent.getSpecies(), Constants.SIMU_SENSOR_RANGE, FRx, FRy);

        if (F > FL && F > FR) {
            if (F < 0) {
                agent.getDirection().rotateRandomly().toUnitVect();
            }

        } else if (F < FL && F >FR) {
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
    
    private void storeDeposit(Agent agent) {
        int x = (int) agent.getPosition().getX();
        int y = (int) agent.getPosition().getY();
        Color color = this.trailMap[x][y];
        
        this.storedDeposits[agent.getSpecies()].add(agent.getPosition());
        
        /*switch (agent.getSpecies()) {
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
        }*/
        
        //diffuseDeposit(x, y);
    }
    
    private void placeStoredDeposits() {
        
        Color trailMapCell;
        Agent firstAgent;
        List<Vector> depositOfSpecies;
        
        for (int i=0; i<this.storedDeposits.length; i++) {
            depositOfSpecies = this.storedDeposits[i];
            
            for (Vector location : depositOfSpecies) {
                int x = (int) location.getX();
                int y = (int) location.getY();
                
                
                trailMapCell = this.trailMap[x][y];

                if (this.species.get(i) != null) {
                    firstAgent = this.species.get(i).getAgents().get(0);
                    
                    switch (i) {
                        case 0://R
                            int redPlusDep = Math.min(255, trailMapCell.getRed() + firstAgent.getDepositionT());
                            this.trailMap[x][y] = new Color(redPlusDep, trailMapCell.getGreen(), trailMapCell.getBlue());
                            break;

                        case 1://G
                            int greenPlusDep = Math.min(255, trailMapCell.getGreen() + firstAgent.getDepositionT()); 
                            this.trailMap[x][y] = new Color(trailMapCell.getRed(), greenPlusDep, trailMapCell.getBlue());
                            break;

                        case 2://B
                            int bluePlusDep = Math.min(255, trailMapCell.getBlue() + firstAgent.getDepositionT()); 
                            this.trailMap[x][y] = new Color(trailMapCell.getRed(), trailMapCell.getGreen(), bluePlusDep);
                    }
                }
                diffuseDeposit(x, y);
            }
        }
        
        for (int i = 0; i < this.storedDeposits.length; i++) {
            this.storedDeposits[i].clear();
        }
    }

    private void diffuseDeposit(int x, int y) {
        
        int diffusionAreaSize = (2 * this.diffusionKernel) + 1;
        Color[][] difusionArea = new Color[diffusionAreaSize][diffusionAreaSize];
        int relative_i;
        int relative_j;
        
        for (int i = x - this.diffusionKernel; i < x + this.diffusionKernel + 1; i++) {
            
            relative_i = i - (x - this.diffusionKernel);
            
            for (int j = y - this.diffusionKernel; j < y + this.diffusionKernel + 1; j++) {
                relative_j = j - (y - this.diffusionKernel);
                difusionArea[relative_i][relative_j] = calcDiffusionAt(i, j);
            }
        }
        
        for (int i = x - this.diffusionKernel; i < x + this.diffusionKernel + 1; i++) {
            
            relative_i = i - (x - this.diffusionKernel);
            
            for (int j = y - this.diffusionKernel; j < y + this.diffusionKernel + 1; j++) {
                relative_j = j - (y - this.diffusionKernel);
                if (i >= 0 && i < this.width && j >= 0 && j < this.height){
                    this.trailMap[i][j] = difusionArea[relative_i][relative_j];
                }
            }
        }
    }

    private void diffuseEntireTrailMap() {
        
        Color[][] trailMap = new Color[this.width][this.height];
        
        for (int i = 0; i < trailMap.length; i++) {
            for (int j = 0; j < trailMap[0].length; j++) {
                //trailMap[i][j] = blendTrail(this.trailMap[i][j], calcDiffusionAt(i, j));
                trailMap[i][j] = calcDiffusionAt(i, j);
            }
        }
        this.trailMap = trailMap;
    }
    
    public static Color blendTrail(Color t1, Color t2) {
        double totalAlpha = t1.getAlpha() + t2.getAlpha();
        double weight0 = t1.getAlpha() / totalAlpha;
        double weight1 = t2.getAlpha() / totalAlpha;

        double r = weight0 * t1.getRed() + weight1 * t2.getRed();
        double g = weight0 * t1.getGreen() + weight1 * t2.getGreen();
        double b = weight0 * t1.getBlue() + weight1 * t2.getBlue();
        double a = Math.max(t1.getAlpha(), t2.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
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
        int ownCellWeight = 1;
        //int totalCells = 2 * this.diffusionKernel + ownCellWeight;
        
        for (int i = iStart; i < iEnd; i++) {
            for (int j = jStart; j < jEnd; j++) {
                if (/*i != x && j != y && */i >= 0 && i < this.width && j >= 0 && j < this.height) {
                    R += this.trailMap[i][j].getRed() * ((i == x && j == y)? ownCellWeight : 1);
                    G += this.trailMap[i][j].getGreen() * ((i == x && j == y)? ownCellWeight : 1);
                    B += this.trailMap[i][j].getBlue() * ((i == x && j == y)? ownCellWeight : 1);
                    totalCells++;
                }
            }
        }
        
        R /= totalCells;
        G /= totalCells;
        B /= totalCells;
        
        R = clampValue(R, 0, 255);
        G = clampValue(G, 0, 255);
        B = clampValue(B, 0, 255);
        
        return new Color(R, G, B);
    }
    
    private void decayEntireTrailMap() {
        
        Color currentColor;
        
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                currentColor = this.trailMap[i][j];
                this.trailMap[i][j] = new Color(
                        clampValue(currentColor.getRed() - this.trailsDecayValues[0], 0, 255),
                        clampValue(currentColor.getGreen() - this.trailsDecayValues[1], 0, 255),
                        clampValue(currentColor.getBlue() - this.trailsDecayValues[2], 0, 255)
                );
                /*this.trailMap[i][j] = new Color(
                        clampValue((int) (currentColor.getRed() / 100) * (100 - this.trailsDecayValues[0]), 0, 255),
                        clampValue((int) (currentColor.getGreen() / 100) * (100 - this.trailsDecayValues[1]), 0, 255),
                        clampValue((int) (currentColor.getBlue() / 100) * (100 - this.trailsDecayValues[2]), 0, 255)
                );*/
            }
        }
    }
    
    private int clampValue(int value, int min, int max) {
        return Math.max(Math.min(value, max), 0);
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
        System.out.println("SIMU UPDATE EVENT SENT ->");
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
    
}
