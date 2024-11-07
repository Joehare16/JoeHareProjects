
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Write a description of class Simulation here.
 *
 * @author (jh2199)
 * @version (4/2/2024)
 */
public class Simulation
{
   String worldAndRidesFileName;
   String allocationFileName; 
    
   public Simulation(String worldAndRidesFileName, String allocationFileName) {
       this.worldAndRidesFileName = worldAndRidesFileName;
       this.allocationFileName = allocationFileName;
   }
    
    
   public void run() {
       
       try {
           WorldAndRides worldAndRides = new WorldAndRides(worldAndRidesFileName);
        
           Allocation allocation = new Allocation(allocationFileName, worldAndRides);
           
           int score = 0; // the total score 
           HashMap<Integer,ArrayList<WorldAndRides.Ride>> CarRides = allocation.getCarRides();
           for (Map.Entry<Integer, ArrayList<WorldAndRides.Ride>> entry : CarRides.entrySet()) {
           

            int[] intersect = {0,0}; //starting point
            int carScore = 0; //score for this specific car
            int TimeStep = 0; //resets clock to 0;

            ArrayList<WorldAndRides.Ride> rides = entry.getValue();
            for (WorldAndRides.Ride ride : rides){
                int distanceFromRide = calcDistance(intersect[0],intersect[1],ride.getSRow(),ride.getSColumn()); //calculates the distance between ride and current vehicle location
                TimeStep = TimeStep + distanceFromRide;
                int rideDistance = calcDistance(ride.getSRow(),ride.getSColumn(),ride.getFRow(),ride.getFColumn());
                if(TimeStep == ride.earliestStart()){ //score if car gets to earliest point;
                    carScore = carScore + (rideDistance + worldAndRides.getBonus());
                }
                if(TimeStep < ride.earliestStart()){
                    while(TimeStep != ride.earliestStart()){
                        TimeStep++;
                    }
                    carScore = carScore + (rideDistance + worldAndRides.getBonus());;
                }
                int late = ride.latestFinish() - rideDistance;
               
                if(TimeStep > ride.earliestStart() && TimeStep <= late){
                    carScore += rideDistance;
                }
                if(TimeStep > late){
                    carScore = carScore + 0;
                }
                TimeStep = TimeStep + rideDistance;
                
                int finishRow = ride.getFRow();
                int finishColumn = ride.getFColumn();
                intersect [0] = finishRow;
                intersect[1] = finishColumn;
                }
                score = score + carScore;
            }
           System.out.println(score);
       } catch (FileFormatException e) {
           System.out.println ("ERROR "+ e.description());
       }
    
   }
   public int calcDistance(int startRow,int startColumn,int endRow,int endColumn){
        return Math.abs(startRow-endRow) + Math.abs(startColumn - endColumn);
   }
  
}

