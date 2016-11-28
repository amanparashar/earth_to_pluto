
package earthtopluto;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Panel2 extends Panel3 
   {
    public static int frameWidth;
    public static int frameHeight;
    public static final long secInNanosec = 1000000000L;
    public static final long milisecInNanosec = 1000000L;
    private final int GAME_FPS = 60;
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, GAMEFINISH, DESTROYED}
    public static GameState gameState;
    private long gameTime;
    private long lastTime;
    private BufferedImage main_picture;
    private BufferedImage camp;     
    private BufferedImage gameTitleImg;
     private Game p4;
    private Font font;
    
    
    
    public Panel2 ()
    {
        super();
        gameState = GameState.VISUALIZING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }
    
      private void LoadContent()
    {
        try 
        {
            URL main_pictureUrl = this.getClass().getResource("/earthtopluto/resources/images/main_picture1.jpg");
            main_picture = ImageIO.read(main_pictureUrl);
            URL campUrl = this.getClass().getResource("/earthtopluto/resources/images/camp.png");
            camp = ImageIO.read(campUrl);
            URL gameTitleImgUrl = this.getClass().getResource("/earthtopluto/resources/images/game_title.png");
            gameTitleImg = ImageIO.read(gameTitleImgUrl);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Panel2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
        
        
        public void drawMenuBackground(Graphics2D g2d){
        g2d.drawImage(main_picture, 0, -20, Panel2.frameWidth, Panel2.frameHeight + 45 , null);
      g2d.drawImage(camp, 5, 10, Panel2.frameWidth/10, Panel2.frameHeight/10, null);
        g2d.setColor(Color.white);
         g2d.drawString("MADE BY : AMAN PARASHAR", 7, frameHeight - 10);
         } 
    
      
      
    @Override
    public void paintComponent(Graphics g) {
          Graphics2D  g2d = (Graphics2D)g;
           super.paintComponent(g2d);
           Draw(g2d);
         }
    
    
  private void Initialize()
    {
        font = new Font("monospaced", Font.BOLD, 28);
    }
 
    
   private void GameLoop()
    { 
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        long beginTime, timeTaken, timeLeft;
       while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    
                   p4.UpdateGame(gameTime, mousePosition());
                    
                    lastTime = System.nanoTime();
                break;
                case GAMEOVER:
                    //...
                break;
                   case GAMEFINISH:
                    //...
                break; 
                case MAIN_MENU:
                    //...
                break;
                case OPTIONS:
                    //...
                break;
                case GAME_CONTENT_LOADING:
                    //...
                break;
                case STARTING:
                    Initialize();
                    LoadContent();
                    gameState = GameState.MAIN_MENU;
                break;
                case VISUALIZING:
                    
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            repaint();
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; 
            if (timeLeft < 10) 
                timeLeft = 10; 
            try {
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }            
    
   @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
               p4.Draw(g2d, mousePosition(), gameTime);
            break;
            case GAMEOVER:
                drawMenuBackground(g2d);
                g2d.setColor(Color.white);
                g2d.drawString("Press ENTER to restart or ESC to exit.", frameWidth/2 - 300, frameHeight/4 + 30);
                  p4.DrawStatistic(g2d, gameTime);
                g2d.setFont(font);
                g2d.drawString("GAME OVER", frameWidth/2 - 90, frameHeight/4);
            break;
                
                case GAMEFINISH:
                drawMenuBackground(g2d);
                g2d.setColor(Color.white);
                g2d.drawString("Press ENTER to restart or ESC to exit.", frameWidth/2 - 300, frameHeight/4 + 30);
                  p4.DrawStatistic(g2d, gameTime);
                g2d.setFont(font);
                g2d.drawString("GAME WON", frameWidth/2 - 90, frameHeight/4);
            break;
                    
            case MAIN_MENU:
                drawMenuBackground(g2d);
                g2d.drawImage(gameTitleImg, frameWidth/2 - gameTitleImg.getWidth()/4,frameHeight/4 ,gameTitleImg.getWidth()/2,gameTitleImg.getHeight()/2, null);
                g2d.setColor(Color.white);
                g2d.drawString("Use w, a, d or arrow keys to move the rocket.", frameWidth / 2 - 117, frameHeight / 2 - 30);
                g2d.drawString("Use O button to fire the bullets", frameWidth / 2 - 80, frameHeight / 2);
                g2d.drawString("Press any key to start the game or ESC to exit.", frameWidth / 2 - 114, frameHeight / 2 + 30);
                g2d.drawString("Destroying two asteroids takes you to next planet",frameWidth / 2 - 114, frameHeight / 2 + 60);
                g2d.drawString("Press R to Restart  ",frameWidth / 2 - 40, frameHeight / 2 + 90);
                break;
            case OPTIONS:
                //...
            break;
            case GAME_CONTENT_LOADING:
                g2d.setColor(Color.white);
                g2d.drawString("GAME is LOADING", frameWidth/2 - 50, frameHeight/2);
            break;
        }
    }
   
 
 private void newGame()
    {
         
        gameTime = 0;
        lastTime = System.nanoTime();
         p4 = new Game();
    }
    
    private void restartGame()
    {
        gameTime = 0;
        lastTime = System.nanoTime();
        p4.RestartGame();
        gameState = GameState.PLAYING;
    }
    
  private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }   
    
  @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        
        switch(gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
            break;
            case MAIN_MENU:
                newGame();
            break;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }  
    
       
}




