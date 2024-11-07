package stage2.src;

public class Main {
    public static void main(String[] args) throws FileFormatException {
      String worldAndRidesFileName = args[0];
     
      Allocation a = new Allocation(worldAndRidesFileName);
      a.printAllocation();
    }
  }