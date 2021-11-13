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
import physarum_2d.view.GUIObserver;
import physarum_2d.view.UI_CONSTANTS;

/**
 *
 * @author Alexis Cassion
 */
public class TrailMapObserver implements GUIObserver {
    
    private final Color[][] trailMap;
    private final Boolean[] isSpeciesActive;
    private final Color[] speciesColor;
    private final int nbOfSpecies;
    
    private final Color colorTransparent = new Color(0, 0, 0, 0);

    public TrailMapObserver(Color[][] trailMap, Boolean[] isSpeciesActive, Color[] speciesColor) {
        this.trailMap = trailMap;
        this.speciesColor = speciesColor;
        this.isSpeciesActive = isSpeciesActive;
        this.nbOfSpecies = (int) Arrays.stream(isSpeciesActive, 0, 4).filter(t -> t).count();
    }

    @Override
    public void print(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;
        
        for (int i = 0; i < this.trailMap.length; i++) {
            for (int j = 0; j < this.trailMap[0].length; j++) {
                g2d.setColor(calcColor(this.trailMap[i][j]));
                g2d.drawLine(i, j, i, j);
            }
        }
    }

    private Color calcColor(Color color) {
        int totalDepositAllSpecies = 0;
        int[] deposit = new int[this.isSpeciesActive.length];
        deposit[0] = color.getRed();
        deposit[1] = color.getGreen();
        deposit[2] = color.getBlue();
        deposit[3] = color.getAlpha();
        
        if (deposit[0] == 0 && deposit[1] == 0 && deposit[2] == 0 && deposit[3] == 0) {
            return this.colorTransparent;
        }
        
        
        for (int i = 0; i < this.nbOfSpecies; i++) {
            totalDepositAllSpecies += deposit[i];
        }
        
        if (totalDepositAllSpecies == 0)
            return UI_CONSTANTS.SIMU_BACKDROP_COLOR;
        
        float colorRedWAvg=0, colorGreenWAvg=0, colorBlueWAvg=0, colorAlpha=0;
        float[] speciesRatio = new float[this.nbOfSpecies];
        
        for (int i = 0; i < this.isSpeciesActive.length; i++) {
            if (this.isSpeciesActive[i]) {
                speciesRatio[i] = deposit[i] / 255;
                colorRedWAvg += (this.speciesColor[i].getRed() * speciesRatio[i]);
                colorGreenWAvg += (this.speciesColor[i].getGreen()* speciesRatio[i]);
                colorBlueWAvg += (this.speciesColor[i].getBlue()* speciesRatio[i]);
            }
        }
        
        colorRedWAvg /= this.nbOfSpecies;
        colorGreenWAvg /= this.nbOfSpecies;
        colorBlueWAvg /= this.nbOfSpecies;
        colorAlpha = Math.min(50 + totalDepositAllSpecies * 10, 255);
        
        return new Color(colorRedWAvg, colorGreenWAvg, colorBlueWAvg, colorAlpha);
    }
    
}
