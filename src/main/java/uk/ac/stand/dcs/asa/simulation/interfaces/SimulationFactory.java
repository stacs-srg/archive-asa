package uk.ac.stand.dcs.asa.simulation.interfaces;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface SimulationFactory {
    public P2PSim makeSimulation(int num_nodes);
    public void showProgress();
}
