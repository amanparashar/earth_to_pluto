
package earthtopluto;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
public class Main_Frame extends JFrame {

  private Main_Frame()
  {
      this.setTitle("Earth To Pluto");
      
   
       this.setSize(800,600);
      this.setLocationRelativeTo(null);
                       this.setResizable(false);
        
     
       this.setContentPane(new Panel2());            
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
  }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main_Frame();
            }
        });

    }
}
