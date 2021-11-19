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
    public static final Boolean GUI_DRAW_ALL_AGENTS = true;
    public static final Boolean GUI_DRAW_SINGLE_AGENT = false;
    public static final Boolean GUI_DRAW_AGENT_SENSORS = false;
    
    public static final int AGENT_SIZE = 0;
    public static final int SIMU_UPDATE_INTERVAL_IN_MILLIS = 50;
    public static final int SIMU_DIFFUSION_INTERVAL_IN_MILLIS = 25;
    public static final int SIMU_DECAY_INTERVAL_IN_MILLIS = 1;
    
    public static boolean SIMU_AGENTS_SHOULD_FACE_CENTER = true;
    public static final Boolean[] SIMU_IS_SPECIES_ACTIVE = new Boolean[] {true, true, true};
    public static final int SIMU_OVERRIDE_POPULATION_SIZE = -10;
    
    
    public static final int SIMU_WIDTH = 300;
    public static final int SIMU_HEIGHT = 300;
    public static final int SIMU_POPULATION_PERCENTAGE = 65;
    public static final int SIMU_DEP_T = 5;
    public static final int SIMU_DIFFUSION_RANGE = 1;
    public static final int[] SIMU_DECAY_PERCENTAGE_T = new int[] {2, 2, 2};
    public static final int SIMU_STEP_SIZE = 1;
    public static final double SIMU_SENSOR_ANGLE = 1.0;
    public static final double SIMU_ROTATION_ANGLE = 1.0;
    public static final int SIMU_SENSOR_OFFSET = 9;
    public static final int SIMU_SENSOR_RANGE = 1;
    
}
