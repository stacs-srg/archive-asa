/*
 * Created on 05-Oct-2005
 */
package uk.ac.stand.dcs.asa.eventdistributor.pubsub;

import uk.ac.stand.dcs.asa.eventModel.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * This is a basic server(forwarder, proxy,...) in a pub sub system.
 * The server is maintaining the information "which node is interested in which event". 
 * That information is stored in the hashmap lois -
 * list of interested subscribers (key= event type, value= Identifier)
 * 
 * <PRE>
 * lois
 * |-------------------------------------------------------------------------|
 * |predicate X | loin: arraylist(interested node1, interested node 2, ....) |
 * |predicate Y | loin: arraylist(interested node1, interested node 2, ....) |
 * |.... 		| ......... 												 |
 * </PRE>
 * 
 * it implements publish and subscribe Interfaces to provide the necesarry
 * methods to "feed" lois. The server deploys itself/interfaces using rafda
 * 
 * the server access a deployed client object to notify a client about an event.
 * 
 * @author markus
 */
public class EdServer implements ISubscribe, IPublish {

	static HashMap lois; // list of interested subscribers (key= p (search pattern), value= Identifier)
	ISubscriber s, client;
	
	/**
	 * starts server (initiates lois feeds lois with some data)
	 */

	public EdServer() {
	
		lois=new HashMap();
		
	}

	
	public void subscribe(ISubscriber s, IPredicate p) {

		// if no pattern is available -> insert new one
			
		ArrayList loin = (ArrayList) lois.get(p);
		
		if (loin != null)
		{
			loin.add(s);
			lois.put(p, loin);
		} 
		else 
		{
			loin = new ArrayList();
			loin.add(s);
			lois.put(p, loin);
		}

	}

	/**
	 * deletes s from p in lois
	 */
	
	public void unsubscribe(ISubscriber s, IPredicate p) {
		// TODO Auto-generated method stub

	}

	/**
	 * "server" receives a published event checks lois (if no match in lois ->
	 * no forwarding) sends it to relvant subscribers
	 */

	public void publish(Event e) {
		
		/**
		 * check every p in lois if it matches (is true)
		 */
		Set keys=lois.keySet();
		Iterator iterator=keys.iterator();
		int a=0;
		int b=0;
		while(iterator.hasNext())
		{
			a++;
			System.out.println("lois member nbr "+a);
			//gets p(a++) from lois 
			IPredicate p=(IPredicate)iterator.next(); //?????????
			
			if(p.match(e))
			{
				ArrayList loin = (ArrayList) lois.get(p);
				
				if (loin == null) 
				{
					System.out.print("Server received Event " + e.getType());
					System.out.println("But nobody subscribed for it");
				} 
				else 
				{
					
					// forward the event to all subscribers
					for (int i = 0; i < loin.size(); i++) 
					{
						b++;
						System.out.println("loin member nbr "+b);
						System.out.print("Server received Event " + e.getType());
						System.out.println("Event will be sent to " + loin.get(i));

						client = (ISubscriber) loin.get(i);
						client.receive(e);

					}//end of going through all the subscribers in the arraylist loin

				}//end of else where condition was .. if there are subscriber for that p available
			}//end of if where condition was ... if p matches with e
		
		}//end of key iteration
		
	}


}
