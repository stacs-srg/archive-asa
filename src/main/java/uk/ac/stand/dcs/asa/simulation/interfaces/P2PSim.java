/*
 * Created on Jan 18, 2005 at 3:45:48 PM.
 */
package uk.ac.stand.dcs.asa.simulation.interfaces;

import uk.ac.stand.dcs.asa.impl.Route;
import uk.ac.stand.dcs.asa.interfaces.IDistanceCalculator;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IP2PNode;

import java.util.Random;

/**
 * @author al
 */
public interface P2PSim {

    int key2Index(IKey key);

    IP2PNode[] getNodes();

    Random getRand();

    Route makeRoute(IP2PNode node, IKey k);

    void showRoute(IP2PNode node, IKey k, Route rt);

    int getNodeCount();

    IDistanceCalculator getDistanceCalculator();

    SpanTreeGen getSpanTreeGen();

	void initialiseP2PLinks();
	
	void showNode(int index);
}
