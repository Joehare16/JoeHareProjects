import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.BufferedReader;
/**
 * Write a description of class WorldAndRides here.
 *
 * @author jh2199
 * @version 24.1.2024
 */
public class WorldAndRides{

//stores info about the world
  private int R; //rows
  private int C; //coulmns
  private int V; //Vehicles
  private int Rides; 
  private int B; //Bonus
  private int T; //TimeSteps
  private ArrayList<Ride> RideList;
  

    public WorldAndRides(String worldAndRidesFileName) throws FileFormatException {
        try(BufferedReader worldAndRideData = new BufferedReader(new FileReader(worldAndRidesFileName))){
        String[] worldDetails = worldAndRideData.readLine().split(" ");
        R = Integer.parseInt(worldDetails[0]);
        C = Integer.parseInt(worldDetails[1]);
        V = Integer.parseInt(worldDetails[2]);
        Rides = Integer.parseInt(worldDetails[3]);
        B = Integer.parseInt(worldDetails[4]);
        T = Integer.parseInt(worldDetails[5]);

        RideList = new ArrayList<>();
        for(int i = 0; i < Rides;i++){
            int a,b,x,y,s,f;
            String line = worldAndRideData.readLine();
            if(line == null){
                System.err.println("Not enough rides in file for given amount");
                System.exit(1);
            }
            else if(line != null){
            String[] ride = line.split(" ");
            if(ride.length < 6){
                System.err.println("Not enough data given for this ride");
            }
            a = Integer.parseInt(ride[0]);
            b = Integer.parseInt(ride[1]);
            x = Integer.parseInt(ride[2]);
            y = Integer.parseInt(ride[3]);
            s = Integer.parseInt(ride[4]);
            f = Integer.parseInt(ride[5]);

            Ride Ride = new Ride(a,b,x,y,s,f); //new instance of ride 
            RideList.add(Ride); //adds the ride to the ride list

            HashSet<String> dupeCheck = new HashSet<>();
            for(Ride Rides : RideList){
                String ride1 = Rides.RidetoString();
                if(!(dupeCheck.contains(ride1))){
                    dupeCheck.add(ride1);
                }
                else{
                    System.err.println("duplicate ride found");
                    System.exit(1);
                }

            }
        }
        }
        }
        catch(IOException e){
            System.err.println("File not found");
        }
        //and store the information in this class
        
        
     
       //information in this class
    }
    public class Ride{
        private int a; //start row 
        private int b; //start column
        private int x; //finish row 
        private int y; //finish column
        private int s; //earliest start
        private int f; //latest finish
    
    public Ride(int a,int b,int x,int y,int s,int f){
        this.a = a;
        this.b = b;
        this.x = x;
        this.y = y;
        this.s = s;
        this.f = f;
    }
    public int getSRow(){
        return a;
    }
    public int getSColumn(){
        return b;
    }
    public int getFRow(){
        return x;
    }                   
    public int getFColumn(){
        return y;
    }
    public int earliestStart(){
        return s;
    }
    public int latestFinish(){
        return f;
    }
    public String RidetoString(){
        return String.valueOf(a) + String.valueOf(b) + String.valueOf(x) + String.valueOf(y) + String.valueOf(s) + String.valueOf(f);
    }
    }
   
public int getRows(){
    return R;
}
public int getColumns(){
    return C;
}
public int getVehicles(){
    return V;
}
public int getRides(){
    return Rides;
}
public int getBonus(){
    return B;
}
public int getTime(){
    return T;
}
public ArrayList<Ride> getRideList(){
    return RideList;
}
}

