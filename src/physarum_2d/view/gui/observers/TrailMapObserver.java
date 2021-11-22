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
        
        for (int i = 0; i < this.trailMap.length; i++) {
            for (int j = 0; j < this.trailMap[0].length; j++) {
                //g2d.setColor(trailMap[i][j]);
                //g2d.setColor(new Color(255 - trailMap[i][j].getRed(), 255 - trailMap[i][j].getGreen(), 255 - trailMap[i][j].getBlue()));
                g2d.setColor(calcColor(this.trailMap[i][j]));
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
        double[] colorFactors = new double[this.isSpeciesActive.length];
        colorFactors[0] = (color.getRed() > 0)? color.getRed() / 255.0 : 0;
        colorFactors[1] = (color.getGreen() > 0)? color.getGreen() / 255.0 : 0;
        colorFactors[2] = (color.getBlue() > 0)? color.getBlue() / 255.0 : 0;
        
        double alphaFactor = 1 - (Arrays.stream(colorFactors).sum());
        alphaFactor *= 4;
        
        int R = 0;
        int G = 0;
        int B = 0;
        
        for (int i = 0; i < this.isSpeciesActive.length; i++) {
            if (this.isSpeciesActive[i]) {
                R += this.speciesColors[i].getRed() * colorFactors[i];
                G += this.speciesColors[i].getGreen() * colorFactors[i];
                B += this.speciesColors[i].getBlue() * colorFactors[i];
            }
        }
        
        int d = 75;
        R = ((int) (R / d)) * d;
        G = ((int) (G / d)) * d;
        B = ((int) (B / d)) * d;
        
        
        //R = (int) Math.abs(Math.sin(R) * R * 2) % this.speciesColors[1].getRed() ;
        
        /*R = (int) Math.abs((2 * Math.sin(R) * R));
        G = (int) Math.abs((2 * Math.sin(G) * G));
        B = (int) Math.abs((2 * Math.sin(B) * B));*/
        
        R = (int) Math.abs((2 * Math.sin(R) * R)) % 255;
        G = (int) Math.abs((2 * Math.sin(G) * G)) % 255;
        B = (int) Math.abs((2 * Math.sin(B) * B)) % 255;
        
        /*R = (int) Math.abs((2 * Math.cos(R) * R));
        G = (int) Math.abs((2 * Math.cos(G) * G));
        B = (int) Math.abs((2 * Math.cos(B) * B));*/
        
        /*R = (int) Math.abs((2 * Math.cos(R) * R)) % 255;
        G = (int) Math.abs((2 * Math.cos(G) * G)) % 255;
        B = (int) Math.abs((2 * Math.cos(B) * B)) % 255;*/
        
        /*R = (int) (-2 * Math.tan(R) * R) % 255;
        G = (int) (-2 * Math.tan(G) * G) % 255;
        B = (int) (-2 * Math.tan(B) * B) % 255;*/
        
        R = (int) Math.min((R), 255);
        G = (int) Math.min((G), 255);
        B = (int) Math.min((B), 255);
        
        R = Math.max(R, 0);
        G = Math.max(G, 0);
        B = Math.max(B, 0);
        
        int A = Math.max(0, Math.min((int) ((R + G + B) * alphaFactor), 255));
        
        return new Color(R, G, B);
    }
    
}
