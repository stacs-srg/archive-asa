/*
 * Created on 25-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.exampleApplication;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author stuart
 */
public class TestDrawing extends JPanel implements MouseListener {
    
//    public void paint(Graphics g){
//        super.paint(g);
//        setBackground(Color.WHITE);
//        g.fillOval(20, 20, 10, 10);
//    }
    
    public TestDrawing(){
        super();
        this.addMouseListener(this);
    }
    
	public void paint(Graphics g) {
	    super.paint(g);
	    setBackground(Color.WHITE);
	    
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    Ellipse2D.Double circle = new Ellipse2D.Double(300, 300, 100, 100);
	    Stroke s=g2d.getStroke();
	    g2d.setStroke(new BasicStroke(3));
	    g2d.draw(circle);
	    
	    g2d.setStroke(s);
	    
	    Ellipse2D.Double circle2 = new Ellipse2D.Double(350, 350, 100, 100);
	    g2d.setColor(new Color(0.36f,0.25f,0.78f,0.75f));
	    g2d.fill(circle2);
	    
	    //We reuse our GeneralPath filled shape.  We translate
	    //and rotate this shape as we did before.
	    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	    path.moveTo(0.0f,0.0f);
	    path.lineTo(0.0f,125.0f);
	    path.quadTo(100.0f,100.0f,225.0f,125.0f);
	    path.curveTo(260.0f,100.0f,130.0f,50.0f,225.0f,0.0f);
	    path.closePath();
	   	    
	    URL imageURL=TestDrawing.class.getResource("/Pictures/background.jpg");
	    BufferedImage bufferedImage = null;
	    try {
            bufferedImage = ImageIO.read(imageURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2d.drawImage(bufferedImage,500,100,bufferedImage.getWidth()/2,bufferedImage.getHeight()/2,null);
        
	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/8.0);
	    g2d.transform(at);
	    at.setToTranslation(0.0f,150.0f);
	    g2d.transform(at);
	    
	    g2d.setColor(Color.ORANGE);
	    g2d.fill(path);
	    
	    Font exFont = new Font("Verdana",Font.PLAIN,24);
	    g2d.setFont(exFont);
	    g2d.setColor(Color.black);
	    g2d.drawString("P2PApplication",0.0f,0.0f);
	    
	    g2d.fillOval(20, 20, 10, 10);
	}

    public void mouseClicked(MouseEvent arg0) {
        int clickCount=arg0.getClickCount();
        if(clickCount==2){
            System.out.println("Mouse double-clicked at: (" + arg0.getX()+ ","+arg0.getY()+")");
        }
        
    }

    public void mouseEntered(MouseEvent arg0) {
        
        // No action
    }

    public void mouseExited(MouseEvent arg0) {
        
        // No action
    }

    public void mousePressed(MouseEvent arg0) {
        
        // No action
    }

    public void mouseReleased(MouseEvent arg0) {
        
        // No action
    }

}
