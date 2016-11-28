
package earthtopluto;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Game {
    
 
    private Random random;
    
    // Player - rocket that is managed by play*er.
    private PlayerRocket player;
    
    // Enemy Asteroids.
    private ArrayList<Asteroid> asteroidList = new ArrayList<Asteroid>();
    
    // Explosions
  private ArrayList<Animation> explosionsList;
    private BufferedImage explosionAnimImg;
    
    // List of all the machine gun bullets.
    private ArrayList<Bullet> bulletsList;
   
   private BufferedImage skyColorImg;
    private BufferedImage mars;
    private BufferedImage jupiter;
    private BufferedImage saturn;
    private BufferedImage uranus;
    private BufferedImage neptune;
    private BufferedImage pluto;
   
    // Font that we will use to write statistic to the screen.
    private Font font;
    
    // Statistics (destroyed enemies, run away enemies)
    private int runAwayEnemies;
    private int destroyedEnemies;
    

    public Game()
    {
         Panel2.gameState = Panel2.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
               
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Panel2.gameState = Panel2.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
       random = new Random();
        

        player = new PlayerRocket(Panel2.frameWidth / 4, Panel2.frameHeight / 4);
        
       asteroidList = new ArrayList<Asteroid>();
        
        explosionsList = new ArrayList<Animation>();
        
       bulletsList = new ArrayList<Bullet>();
        
        font = new Font("monospaced", Font.BOLD, 18);
        
        runAwayEnemies = 0;
        destroyedEnemies = 0;
     
    }
    
    /**
     * Load game files (images).
     */
    private void LoadContent()
    {
        
       try 
        {
            // Images of environment
           URL skyColorImgUrl = this.getClass().getResource("/earthtopluto/resources/images/earth.jpg");
            skyColorImg = ImageIO.read(skyColorImgUrl);
    
            // Load images for enemy asteroid
            URL rocketUrl = this.getClass().getResource("/earthtopluto/resources/images/2_helicopter_body.png");
            Asteroid.rocket = ImageIO.read(rocketUrl);
        
            
        
         
            // Imege of explosion animation.
            URL explosionAnimImgUrl = this.getClass().getResource("/earthtopluto/resources/images/explosion_anim.png");
            explosionAnimImg = ImageIO.read(explosionAnimImgUrl);
    
            
            // Rocket machine gun bullet.
            URL bulletImgUrl = this.getClass().getResource("/earthtopluto/resources/images/bullet.png");
            Bullet.bulletImg = ImageIO.read(bulletImgUrl);
            
            
            URL marsUrl = this.getClass().getResource("/earthtopluto/resources/images/mars.jpg");
            mars = ImageIO.read(marsUrl);
            
            URL jupiterUrl = this.getClass().getResource("/earthtopluto/resources/images/jupiter.jpg");
            jupiter = ImageIO.read(jupiterUrl);
            
            URL saturnUrl = this.getClass().getResource("/earthtopluto/resources/images/saturn.jpg");
            saturn = ImageIO.read(saturnUrl);
            URL uranusUrl = this.getClass().getResource("/earthtopluto/resources/images/uranus.jpg");
            uranus = ImageIO.read(uranusUrl);
            URL neptuneUrl = this.getClass().getResource("/earthtopluto/resources/images/neptune.jpg");
            neptune = ImageIO.read(neptuneUrl);
            URL plutoUrl = this.getClass().getResource("/earthtopluto/resources/images/pluto.jpg");
            pluto = ImageIO.read(plutoUrl);
            
        }  
        catch (IOException ex) 
        {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
       
    }
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
        player.Reset(Panel2.frameWidth / 4, Panel2.frameHeight / 4);
        
        Asteroid.restartEnemy();
        
         Bullet.timeOfLastCreatedBullet = 0;
        
        // Empty all the lists.
        asteroidList.clear();
          bulletsList.clear();
  
        explosionsList.clear();
        
        // Statistics
        runAwayEnemies = 0;
        destroyedEnemies = 0;
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime The elapsed game time in nanoseconds.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
 
        /* Player */
        // When player is destroyed and all explosions are finished showing we change game status.
        if( !isPlayerAlive() && explosionsList.isEmpty() ){
            Panel2.gameState = Panel2.GameState.GAMEOVER;
            return; // If player is destroyed, we don't need to do thing below.
        }  
        // When a player is out of machine gun bullets, and all lists 
        // of bullets  and explosions are empyt(end showing) we finish the game.
        if(player.numberOfAmmo <= 0 && 
           bulletsList.isEmpty()  && 
           explosionsList.isEmpty())
        {
            Panel2.gameState = Panel2.GameState.GAMEOVER;
            return;
        }
        
        if(isPlayerAlive() && destroyedEnemies>=15)
        {
            Panel2.gameState = Panel2.GameState.GAMEFINISH;
            return;
        }
        // If player is alive we update him.
        if(isPlayerAlive()){
             isPlayerShooting(gameTime, mousePosition);
             player.isMoving();
            player.Update();
        }
        
        if(Panel3.keyboardKeyState(KeyEvent.VK_N))
        {    destroyedEnemies+=2;
            Panel3.keyboardState[KeyEvent.VK_N]=false;    
        }
        
         if(Panel3.keyboardKeyState(KeyEvent.VK_R))
        {    
            Panel2.gameState=Panel2.GameState.STARTING;
            Panel3.keyboardState[KeyEvent.VK_N]=false;    
        }
        
        
        /* Bullets */
      updateBullets();
        
        
        /* Enemies */
       createEnemyAsteroid(gameTime);
        updateEnemies();
        
        /* Explosions */
        updateExplosions();  
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        // Image for background sky color.
        if(destroyedEnemies<2)
       g2d.drawImage(skyColorImg, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
        
        else if(destroyedEnemies<4)
       g2d.drawImage(mars, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
    
        else  if(destroyedEnemies<6)
       g2d.drawImage(jupiter, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
     
        else if(destroyedEnemies<8)
       g2d.drawImage(saturn, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
      
        else if(destroyedEnemies<10)
       g2d.drawImage(uranus, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
       
        else if(destroyedEnemies<12)
       g2d.drawImage(neptune, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
        
        else 
       g2d.drawImage(pluto, 0, 0, Panel2.frameWidth, Panel2.frameHeight, null);
         
        
         if(isPlayerAlive())
            player.Draw(g2d);
        
        // Draws all the enemies.
        for(int i = 0; i < asteroidList.size(); i++)
        {
            asteroidList.get(i).Draw(g2d);
        }
        
        // Draws all the bullets. 
        for(int i = 0; i < bulletsList.size(); i++)
        {
            bulletsList.get(i).Draw(g2d);
        }
        
       // Draw all explosions.
        for(int i = 0; i < explosionsList.size(); i++)
        {
            explosionsList.get(i).Draw(g2d);
        }
        
        // Draw statistics
         g2d.setFont(font);
           g2d.setColor(Color.darkGray);
        
        g2d.drawString(formatTime(gameTime), Panel2.frameWidth/2 - 45, 21);
        g2d.drawString("DESTROYED: " + destroyedEnemies, 10, 21);
        g2d.drawString("RUNAWAY: "   + runAwayEnemies,   10, 41);
        g2d.drawString("AMMO: "      + player.numberOfAmmo, 10, 81);
        
       
    }
    
    /**
     * Draws some game statistics when game is over.
     * 
     * @param g2d Graphics2D
     * @param gameTime Elapsed game time.
     */
    public void DrawStatistic(Graphics2D g2d, long gameTime){
        g2d.drawString("Time: " + formatTime(gameTime),Panel2.frameWidth/2 - 120, Panel2.frameHeight/3 + 100);
        g2d.drawString("Ammo left: "         + player.numberOfAmmo,Panel2.frameWidth/2 - 120, Panel2.frameHeight/3 + 125);
        g2d.drawString("Destroyed enemies: " + destroyedEnemies,Panel2.frameWidth/2 - 120, Panel2.frameHeight/3 + 150);
        g2d.drawString("Runaway enemies: "   + runAwayEnemies,Panel2.frameWidth/2 - 120 , Panel2.frameHeight/3 + 170);
        g2d.drawString("STATISTICS: ", Panel2.frameWidth/2 - 90, Panel2.frameHeight/3 + 60); 
     }
    
    /**
     * Draws rotated mouse cursor.
     * It rotates the cursor image on the basis of the player helicopter machine gun.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Position of the mouse.
     */
   
    
    /**
     * Format given time into 00:00 format.
     * 
     * @param time Time that is in nanoseconds.
     * @return Time in 00:00 format.
     */
    private static String formatTime(long time){
            // Given time in seconds.
            int sec = (int)(time / Panel2.milisecInNanosec / 1000);

            // Given time in minutes and seconds.
            int min = sec / 60;
            sec = sec - (min * 60);

            String minString, secString;

            if(min <= 9)
                minString = "0" + Integer.toString(min);
            else
                minString = "" + Integer.toString(min);

            if(sec <= 9)
                secString = "0" + Integer.toString(sec);
            else
                secString = "" + Integer.toString(sec);

            return minString + ":" + secString;    
   }
    
    
    
    
    
    /*
     * 
     * Methods for updating the game. 
     * 
     */
    
     
    /**
     * Check if player is alive. If not, set game over status.
     * 
     * @return True if player is alive, false otherwise.
     */
    private boolean isPlayerAlive()
    {
        if(player.health <= 0)
            return false;
        
        return true;
    }
    
    /**
     * Checks if the player is shooting with the machine gun and creates bullets if he shooting.
     * 
     * @param gameTime Game time.
     */
    private void isPlayerShooting(long gameTime, Point mousePosition)
    {
        if(player.isShooting(gameTime))
        {
            Bullet.timeOfLastCreatedBullet = gameTime;
            player.numberOfAmmo--;
            
            Bullet b = new Bullet(player.machineGunXcoordinate, player.machineGunYcoordinate, mousePosition);
            bulletsList.add(b);
        }
    }
    
    
    
    /**
     * Creates a new enemy if it's time.
     * 
     * @param gameTime Game time.
     */
    private void createEnemyAsteroid(long gameTime)
    {
        if(gameTime - Asteroid.timeOfLastCreatedEnemy >= Asteroid.timeBetweenNewEnemies)
        {   
            Asteroid eh = new Asteroid();
            int xCoordinate = Panel2.frameWidth;
            int yCoordinate = random.nextInt(Panel2.frameHeight - Asteroid.rocket.getHeight());
            eh.Initialize(xCoordinate, yCoordinate);
            // Add created enemy to the list of enemies.
            asteroidList.add(eh);
            
            // Speed up enemy speed and aperence.
            Asteroid.speedUp();
            
            // Sets new time for last created enemy.
            Asteroid.timeOfLastCreatedEnemy = gameTime;
        }
    }   
    
    /**
     * Updates all enemies.
     * Move the asteroids and checks if he left the screen.
     * Updates asteroids animations.
     * Checks if enemy was destroyed.
     * Checks if any enemy collision with player.
     */
    private void updateEnemies()
    {
        for(int i = 0; i < asteroidList.size(); i++)
        {
            Asteroid eh = asteroidList.get(i);
            
            eh.Update();
            
            // Is chrashed with player?
            Rectangle playerRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, player.rocket.getWidth(), player.rocket.getHeight());
            Rectangle enemyRectangel = new Rectangle(eh.xCoordinate, eh.yCoordinate, Asteroid.rocket.getWidth(), Asteroid.rocket.getHeight());
            if(playerRectangel.intersects(enemyRectangel)){
                player.health = 0;
                
                // Remove asteroid from the list.
                asteroidList.remove(i);
                
                // Add explosion of player rocket.
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, player.xCoordinate + exNum*60, player.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
                // Add explosion of enemy asteroid.
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, eh.xCoordinate + exNum*60, eh.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
                
                // Because player crashed with enemy the game will be over so we don't need to check other enemies.
                break;
            }
            
            // Check health.
            if(eh.health <= 0){
                // Add explosion of helicopter.
                Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, eh.xCoordinate, eh.yCoordinate - explosionAnimImg.getHeight()/3, 0); // Substring 1/3 explosion image height (explosionAnimImg.getHeight()/3) so that explosion is drawn more at the center of the helicopter.
                explosionsList.add(expAnim);

                // Increase the destroyed enemies counter.
                destroyedEnemies++;
                
                // Remove asteroid from the list.
                asteroidList.remove(i);
                
                // Asteroid was destroyed so we can move to next asteroid.
                continue;
            }
            
            // If the current enemy is left the screen we remove him from the list and update the runAwayEnemies variable.
            if(eh.isLeftScreen())
            {
                asteroidList.remove(i);
                runAwayEnemies++;
            }
        }
    }  
    
    /**
     * Update bullets. 
     * It moves bullets.
     * Checks if the bullet is left the screen.
     * Checks if any bullets is hit any enemy.
     */
    private void updateBullets()
    {
        for(int i = 0; i < bulletsList.size(); i++)
        {
            Bullet bullet = bulletsList.get(i);
            
            // Move the bullet.
            bullet.Update();
            
            // Is left the screen?
            if(bullet.isItLeftScreen()){
                bulletsList.remove(i);
                // Bullet have left the screen so we removed it from the list and now we can continue to the next bullet.
                continue;
            }
            
            // Did hit any enemy?
            // Rectangle of the bullet image.
            Rectangle bulletRectangle = new Rectangle((int)bullet.xCoordinate, (int)bullet.yCoordinate, Bullet.bulletImg.getWidth(), Bullet.bulletImg.getHeight());
            // Go trough all enemis.
            for(int j = 0; j < asteroidList.size(); j++)
            {
                Asteroid eh = asteroidList.get(j);

                // Current enemy rectangle.
                Rectangle enemyRectangel = new Rectangle(eh.xCoordinate, eh.yCoordinate, Asteroid.rocket.getWidth(), Asteroid.rocket.getHeight());

                // Is current bullet over currnet enemy?
                if(bulletRectangle.intersects(enemyRectangel))
                {
                    // Bullet hit the enemy so we reduce his health.
                    eh.health -= Bullet.damagePower;
                    
                    // Bullet was also destroyed so we remove it.
                    bulletsList.remove(i);
                    
                    // That bullet hit enemy so we don't need to check other enemies.
                    break;
                }
            }
        }
    }  

 
    /**
     * Updates all the animations of an explosion and remove the animation when is over.
     */
    private void updateExplosions()
    {
        for(int i = 0; i < explosionsList.size(); i++)
        {
            // If the animation is over we remove it from the list.
            if(!explosionsList.get(i).active)
                explosionsList.remove(i);
        }
    }  
    
      
}
