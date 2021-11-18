/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.view;

import java.awt.Color;

/**
 *
 * @author Alexis Cassion
 */
public class Constants {
    
    public static final Color SIMU_BACKDROP_COLOR = Color.WHITE;
    
    public static final Boolean GUI_DRAW_TRAIL_MAP = true;
    public static final Boolean GUI_DRAW_ALL_AGENTS = false;
    public static final Boolean GUI_DRAW_SINGLE_AGENT = true;
    
    public static final int AGENT_SIZE = 3;
    public static final int SIMU_UPDATE_INTERVAL_IN_MILLIS = 1;
    public static final int SIMU_DIFFUSION_INTERVAL_IN_MILLIS = 25;
    public static final int SIMU_DECAY_INTERVAL_IN_MILLIS = 1;
    
    public static boolean SIMU_AGENTS_SHOULD_FACE_CENTER = false;
    public static final Boolean[] SIMU_IS_SPECIES_ACTIVE = new Boolean[] {false, false, true};
    public static final int SIMU_OVERRIDE_POPULATION_SIZE = 100;
    
    
    public static final int SIMU_WIDTH = 500;
    public static final int SIMU_HEIGHT = 500;
    public static final int SIMU_POPULATION_PERCENTAGE = 10;
    public static final int SIMU_DEP_T = 255;
    public static final int SIMU_DIFFUSION_RANGE = 0;
    public static final int SIMU_DECAY_T = 1;
    public static final int SIMU_STEP_SIZE = 1;
    public static final double SIMU_SENSOR_ANGLE = (Math.PI / 4) / Math.PI;
    public static final double SIMU_ROTATION_ANGLE = (Math.PI / 4) / Math.PI;
    public static final int SIMU_SENSOR_OFFSET = 12;
    public static final int SIMU_SENSOR_RANGE = 3;
    
}
