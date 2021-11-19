/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view.gui.observers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import physarum_2d.controller.Simulation;
import physarum_2d.view.GUIObserver;
import physarum_2d.view.Constants;

/**
 *
 * @author Alexis Cassion
 */
public class TrailMapObserver implements GUIObserver {
    
    private final Simulation simulation;
    
    private final Color colorTransparent = new Color(0, 0, 0, 0);
    private Color[][] trailMap;
    private Boolean[] isSpeciesActive;
    private Color[] speciesColors;
    private int nbOfSpecies;

    public TrailMapObserver(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void print(Graphics g) {
        
        this.trailMap = this.simulation.getTrailMap();
        this.isSpeciesActive = this.simulation.getIsSpeciesActive();
        this.speciesColors = this.simulation.getSpeciesColors();
        this.nbOfSpecies = this.simulation.getNbOfSpecies();
        
        Graphics2D g2d = (Graphics2D) g;
        int[] speciesTrailsQuantities = new int[3];
        
        for (int i = 0; i < trailMap.length; i++) {
            for (int j = 0; j < trailMap[0].length; j++) {
                g2d.setColor(trailMap[i][j]);
                g2d.fillRect(i, j, 1, 1);
                //g2d.drawLine(i, j, i, j);
                /*speciesTrailsQuantities[0] = trailMap[i][j].getRed();
                speciesTrailsQuantities[1] = trailMap[i][j].getGreen();
                speciesTrailsQuantities[2] = trailMap[i][j].getBlue();
                for (int k = 0; k < isSpeciesActive.length; k++) {
                    if (isSpeciesActive[k] && speciesTrailsQuantities[k] > 0) {
                        g2d.setColor(speciesColors[k]);
                        g2d.drawLine(i, j, i, j);
                    }
                }*/
                //g2d.setColor(calcColor(this.trailMap[i][j]));
                //currCell = this.trailMap[i][j];
                //g2d.setColor(new Color(currCell.getRed(), currCell.getGreen(), currCell.getBlue()));
                //g2d.drawLine(i, j, i, j);
            }
        }
    }

    private Color calcColor(Color color) {
        int totalDepositAllSpecies = 0;
        int[] deposit = new int[this.isSpeciesActive.length];
        deposit[0] = color.getRed();
        deposit[1] = color.getGreen();
        deposit[2] = color.getBlue();
        
        for (int i = 0; i < this.nbOfSpecies; i++) {
            totalDepositAllSpecies += deposit[i];
        }
        
        if (totalDepositAllSpecies == 0)
            return Constants.SIMU_BACKDROP_COLOR;
        
        float colorRedWAvg=0, colorGreenWAvg=0, colorBlueWAvg=0, colorAlpha=0;
        float[] speciesRatio = new float[this.nbOfSpecies];
        
        for (int i = 0; i < this.isSpeciesActive.length; i++) {
            if (this.isSpeciesActive[i]) {
                speciesRatio[i] = deposit[i] / 255;
                colorRedWAvg += (this.speciesColors[i].getRed() * speciesRatio[i]);
                colorGreenWAvg += (this.speciesColors[i].getGreen()* speciesRatio[i]);
                colorBlueWAvg += (this.speciesColors[i].getBlue()* speciesRatio[i]);
            }
        }
        
        colorRedWAvg /= this.nbOfSpecies;
        colorGreenWAvg /= this.nbOfSpecies;
        colorBlueWAvg /= this.nbOfSpecies;
        
        colorRedWAvg = Math.min(colorRedWAvg, 255);
        colorGreenWAvg = Math.min(colorGreenWAvg, 255);
        colorBlueWAvg = Math.min(colorBlueWAvg, 255);
        colorAlpha = Math.min((50 + totalDepositAllSpecies * 10), 255);
        
        return new Color((int) 255 - colorRedWAvg, (int) 255 - colorGreenWAvg, (int) 255 - colorBlueWAvg, (int) 255);
    }
    
}
