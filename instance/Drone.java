package model.instance;


public class Drone 
{
    final String iD; 
    double reachOfDrone; 
    
    public Drone(String iD, double reachOfDrone)
    {
        this.iD = iD; 
        this.reachOfDrone = reachOfDrone; 
    }
    
    public String getID()
    {
        return iD; 
    }
    
    public double getReachOfDrone()
    {
         return reachOfDrone; 
    }
    
    public void setDroneReach(double newReach)
    {
        this.reachOfDrone = newReach; 
    }
}
