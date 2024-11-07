
public class Main {
  public static void main(String[] args) {
    String worldAndRidesFileName = args[0];
    String allocationFileName = args[1];
    
    Simulation s = new Simulation(worldAndRidesFileName, allocationFileName);
/*  */    s.run();
    
  }
}
