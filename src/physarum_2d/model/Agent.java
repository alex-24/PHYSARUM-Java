/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.model;

import java.awt.Point;

/**
 *
 * @author Alexis Cassion
 */
public class Agent {
    
    private int species;
    private Vector position;
    private Vector direction;
    private double sensorAngle;
    private double sensorOffset;
    private double rotationAngle;
    private int sensorRange;
    private int stepSize;
    private int depositionT;
    private double probChangeDir;

    public Agent(int species, Vector position, Vector direction, double sensorAngle, double sensorOffset, double rotationAngle, int sensorRange, int stepSize, int depositionT, double probChangeDir) {
        this.species = species;
        this.position = position;
        this.direction = direction;
        this.sensorAngle = sensorAngle;
        this.sensorOffset = sensorOffset;
        this.rotationAngle = rotationAngle;
        this.sensorRange = sensorRange;
        this.stepSize = stepSize;
        this.depositionT = depositionT;
        this.probChangeDir = probChangeDir;
    }

    

    public int getSpecies() {
        return species;
    }

    public void setSpecies(int species) {
        this.species = species;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public double getSensorAngle() {
        return sensorAngle;
    }

    public void setSensorAngle(double sensorAngle) {
        this.sensorAngle = sensorAngle;
    }

    public double getSensorOffset() {
        return sensorOffset;
    }

    public void setSensorOffset(double sensorOffset) {
        this.sensorOffset = sensorOffset;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public int getSensorRange() {
        return sensorRange;
    }

    public void setSensorRange(int sensorRange) {
        this.sensorRange = sensorRange;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getDepositionT() {
        return depositionT;
    }

    public void setDepositionT(int depositionT) {
        this.depositionT = depositionT;
    }

    public double getProbChangeDir() {
        return probChangeDir;
    }

    public void setProbChangeDir(double probChangeDir) {
        this.probChangeDir = probChangeDir;
    }
    
}
