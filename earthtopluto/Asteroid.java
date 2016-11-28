/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package earthtopluto;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Enemy helicopter.
 * 
 * @author www.gametutorial.net
 */

public class Asteroid {
    
    // For creating new enemies.
    private static final long timeBetweenNewEnemiesInit = Panel2.secInNanosec * 3;
    public static long timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
    public static long timeOfLastCreatedEnemy = 0;
    
    // Health of the Asteroid
    public int health;
    
    // Position of the Asteroid on the screen.
    public int xCoordinate;
    public int yCoordinate;
    
    // Moving speed and direction.
    private static final double movingXspeedInit = -4;
    private static double movingXspeed = movingXspeedInit;
    
    // Images of enemy  Asteroid. Images are loaded and set in Game class in LoadContent() method.
    public static BufferedImage rocket;

    public void Initialize(int xCoordinate, int yCoordinate)
    {
        health = 100;
        
        // Sets enemy position.
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
          
        // Moving speed and direction of enemy.
        Asteroid.movingXspeed = -4;
    }
    
    /**
     * It sets speed and time between enemies to the initial properties.
     */
    public static void restartEnemy(){
        Asteroid.timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
        Asteroid.timeOfLastCreatedEnemy = 0;
        Asteroid.movingXspeed = movingXspeedInit;
    }
    
    
    /**
     * It increase enemy speed and decrease time between new enemies.
     */
    public static void speedUp(){
        if(Asteroid.timeBetweenNewEnemies > Panel2.secInNanosec)
            Asteroid.timeBetweenNewEnemies -= Panel2.secInNanosec / 100;
        
        Asteroid.movingXspeed -= 0.25;
    }
    
    
    /**
     * Checks if the enemy is left the screen.
     * 
     * @return true if the enemy is left the screen, false otherwise.
     */
    public boolean isLeftScreen()
    {
        if(xCoordinate < 0 - rocket.getWidth()) // When the entire helicopter is out of the screen.
            return true;
        else
            return false;
    }
    
       
    public void Update()
    {
        xCoordinate += movingXspeed;
    }
    
   
    public void Draw(Graphics2D g2d)
    { 
        g2d.drawImage(rocket, xCoordinate, yCoordinate, null);
    }
    
}

