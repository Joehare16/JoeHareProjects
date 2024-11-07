
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;




/**
 * Write a description of class Allocation here.
 *
 * @author jh2199
 * @version 30/1/2024
 */
public class Allocation{

   private int M; //amount of rides to a vehicle
   private HashMap<Integer,ArrayList<WorldAndRides.Ride>> CarRides; //hash map which stores a hashset of the vehicles assgined to that car then maps it to car.
   

    public Allocation(String allocationFileName, WorldAndRides worldAndRides) throws FileFormatException {
        try(BufferedReader allocationData = new BufferedReader(new FileReader(allocationFileName))){
        CarRides = new HashMap<>();
        int currentVehicle = 1;
        String allocationLine;
        while ((allocationLine = allocationData.readLine()) != null){  
            String[] rideNums = allocationLine.split(" ");    //loops through amount of vechiles and reads one line of file.
            M = Integer.parseInt(rideNums[0]);
             //the amount of rides to allocate 
            ArrayList<WorldAndRides.Ride> VehicleRides = new ArrayList<>();
            for(int i = 1; i<=M;i++){
                int rideNum = Integer.parseInt(rideNums[i]);
                WorldAndRides.Ride ride = worldAndRides.getRideList().get(rideNum);
                VehicleRides.add(ride);
            }
            CarRides.put(currentVehicle,VehicleRides);

            currentVehicle++;
        }

    }catch(IOException e){
            System.err.println("File not found");
    }
    }

    public HashMap<Integer,ArrayList<WorldAndRides.Ride>> getCarRides(){
        return CarRides;
    }
    public int getRideAmount(){
        return M;
    }
}
