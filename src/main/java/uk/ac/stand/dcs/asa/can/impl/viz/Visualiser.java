/*
 * Created on Apr 29, 2005 at 3:03:15 PM.
 */
package uk.ac.stand.dcs.asa.can.impl.viz;

import uk.ac.stand.dcs.asa.can.simulation.CanSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.P2PSim;
import uk.ac.stand.dcs.asa.simulation.interfaces.SimulationFactory;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import java.awt.*;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class Visualiser extends JFrame {

    final DrawingPanel panel;
    
    public static void main(String args[]) {
        if( args.length < 2 ) {
            Error.hardError("Usage: SimulationFactory numNodes");
        }
        String FactoryName = args[0];
        String node_count_string = args[1];
        int num_nodes = 0;
        try {
            num_nodes = Integer.parseInt(node_count_string);
        }
        catch ( NumberFormatException e) {
            Error.hardError("Illegal parameter: " + node_count_string + "must be an integer" );
        }
                
        Diagnostic.setLevel(Diagnostic.NONE);
        
        SimulationFactory sf = null;
        try {
            sf = ((SimulationFactory)Class.forName( FactoryName ).newInstance());
        } catch (Exception e) {
            Error.exceptionError("Cannot instantiate simulation factory", e);
        }
        P2PSim sim = sf.makeSimulation(num_nodes);
        if( ! ( sim instanceof CanSim ) ) {
            Error.hardError("Cannot simulation factory is not a CanSim - this tool only works with Can Simulations" );
        }     
      
        try {
            Visualiser frame = new Visualiser(sim);
            DrawingPanel panel = frame.getPanel();
            panel.addNodes( sim.getNodes() );
            frame.setVisible(true);
            panel.addRoute( sim.makeRoute(sim.getNodes()[1], sim.getNodes()[9].getKey()) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public Visualiser(P2PSim sim) {
        super();
        setBounds(100, 100, 900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new DrawingPanel(sim);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().setBounds(4, 30, 492, 341);
        repaint();
    }
    /**
     * @return Returns the panel.
     */
    public DrawingPanel getPanel() {
        return panel;
    }
}
