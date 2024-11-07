package stage2.src;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Write a description of class Allocation here.
 *
 * @author jh2199
 * @version 30/1/2024
 */
public class Allocation {
    
    private HashMap<Integer, ArrayList<Integer>> AllocationFile;
    private HashMap<Integer, ArrayList<WorldAndRides.Ride>> CarRides; //hash map which stores a hashset of the vehicles assgined to that car then maps it to car.
    private Set<Integer> allocatedRideIndices = new HashSet<>();

    public Allocation(String worldAndRidesFileName) throws FileFormatException {
        WorldAndRides worldAndRides = new WorldAndRides(worldAndRidesFileName);
        ArrayList<WorldAndRides.Ride> rideList = worldAndRides.getRideList();
        Map<Integer, WorldAndRides.Ride> RideMap = new HashMap<>();
        AllocationFile = new HashMap<>();
        for (int i = 0; i < worldAndRides.getRides(); i++){
            RideMap.put(i+1, rideList.get(i));
        }
        System.out.println("RideMap contents:");
        for (Map.Entry<Integer, WorldAndRides.Ride> entry : RideMap.entrySet()) {
            int rideIndex = entry.getKey();
            WorldAndRides.Ride ride = entry.getValue();
            System.out.println("Ride Index: " + rideIndex);
            System.out.println("Ride Details: " + ride.getSRow() + ride.getSColumn());
            System.out.println(calcDistance(0,0,ride.getSRow(),ride.getSColumn()));
        }
        for (int i = 0; i < worldAndRides.getVehicles(); i++) {
            AllocationFile.put(i+1,new ArrayList<Integer>());
        }
        

        List<Map.Entry<Integer, WorldAndRides.Ride>> list = new ArrayList<>(RideMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, WorldAndRides.Ride>>() {
            @Override
            public int compare(Map.Entry<Integer, WorldAndRides.Ride> ride1, Map.Entry<Integer, WorldAndRides.Ride> ride2) {

                int distance1 = calcDistance(0, 0, ride1.getValue().getSRow(), ride1.getValue().getSColumn());
                int distance2 = calcDistance(0, 0, ride2.getValue().getSRow(), ride2.getValue().getSColumn());

                return Integer.compare(distance1, distance2);
            }

        });
        System.out.println("Sorted List of RideMap entries:");
        for (Map.Entry<Integer, WorldAndRides.Ride> entry : list) {
            int rideIndex = entry.getKey();
            WorldAndRides.Ride ride = entry.getValue();
            System.out.println("Ride Index: " + rideIndex);
            System.out.println("Ride Details: " + ride.getSColumn());
        }
        // create an optimised allocation file
        for (int vehicle = 1; vehicle <= worldAndRides.getVehicles(); vehicle++) {
            //for the amount of vehicles in the ride list find the closest ride to the start point and initlaise it as the starting point
            int rideIndex = list.get(vehicle-1).getKey();
            AllocationFile.get(vehicle).add(rideIndex);
        }
        updateAllocatedRidesMap(AllocationFile);
        for (Map.Entry<Integer, WorldAndRides.Ride> entry1 : RideMap.entrySet()) {
            int rideIndex = entry1.getKey();
            WorldAndRides.Ride ride = entry1.getValue();

            //int bonus = worldAndRides.getBonus();
            int minDistance = Integer.MAX_VALUE;
            int closestVehicle = -1;

            if (allocatedRideIndices.contains(rideIndex)) {
                continue;
            }

            for (Map.Entry<Integer, ArrayList<Integer>> entry : AllocationFile.entrySet()){

                int VehicleId = entry.getKey();
                ArrayList<Integer> allocatedRides = entry.getValue();

                int lastRideIndex = allocatedRides.get(allocatedRides.size() - 1);
                WorldAndRides.Ride lastRide = RideMap.get(lastRideIndex);


                    int rideDistance = calcDistance(lastRide.getFRow(), lastRide.getFColumn(), ride.getSRow(), ride.getSColumn());
                    //int ridePoints = pointsCalc(entry1.getValue(), lastRide, RideDistance, bonus);
                    if (rideDistance < minDistance) {
                        minDistance = rideDistance;
                        closestVehicle = VehicleId;

                    }
                }
            AllocationFile.get(closestVehicle).add(rideIndex);
            updateAllocatedRidesMap(AllocationFile);
            }

        //Collections.sort(rideList, Comparator.comparingInt((Ride::earliestStart)));
        // for (Ride ride : rideList) {
        CarAllocation(AllocationFile, worldAndRides);
    }
    public void updateAllocatedRidesMap(Map<Integer, ArrayList<Integer>> AllocationFile) {
        allocatedRideIndices.clear(); // Clear the set to rebuild it from scratch
        for (ArrayList<Integer> allocatedRides : AllocationFile.values()) {
            allocatedRideIndices.addAll(allocatedRides);
        }
    }
public void printAllocationFile(){
    System.out.println("Printing AllocationFile:");
    for (Map.Entry<Integer, ArrayList<Integer>> entry : AllocationFile.entrySet()) {
        int vehicle = entry.getKey();
        List<Integer> allocatedRides = entry.getValue();

        System.out.print("Vehicle " + vehicle + ": ");
        for (int rideIndex : allocatedRides) {
            System.out.print(rideIndex + " ");
        }
        System.out.println();
    }
    System.out.println("End of AllocationFile printing");
}
    
    public void CarAllocation(HashMap<Integer, ArrayList<Integer>> AllocationFile,WorldAndRides worldAndRides) throws FileFormatException { //take the optimised file andallocate the cars using it 
        CarRides = new HashMap<>();
            int currentVehicle = 1;
            for (Map.Entry<Integer, ArrayList<Integer>> entry : AllocationFile.entrySet()) { 
               ArrayList<Integer> rideIndices = entry.getValue();
                ArrayList<WorldAndRides.Ride> VehicleRides = new ArrayList<>();
                for(int rideIndex: rideIndices){
                    rideIndex--;
                    WorldAndRides.Ride ride = worldAndRides.getRideList().get(rideIndex);
                    VehicleRides.add(ride);
                }
                CarRides.put(currentVehicle,VehicleRides);
                currentVehicle++;
            }
    
        }
    public HashMap<Integer,ArrayList<WorldAndRides.Ride>> getCarRides(){
        return CarRides;
    }
      public void printAllocation(){
        String filename = "Allocation.txt";
        try (FileWriter writer = new FileWriter(filename)) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : AllocationFile.entrySet()) {
                ArrayList<Integer> values = entry.getValue();
                int count = values.size();
                StringBuilder line = new StringBuilder();
                
                line.append(count).append(" ");
                for (int i = 0; i < values.size(); i++) {
                    line.append(values.get(i));
                    if (i < values.size() - 1) {
                        line.append(" ");
                    }
                }
                line.append("\n");

                System.out.println(line.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    public int calcDistance(int startRow,int startColumn,int endRow,int endColumn){
        return Math.abs(startRow-endRow) + Math.abs(startColumn - endColumn);
   }
}


