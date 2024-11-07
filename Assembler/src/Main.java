import java.io.IOException;

/**
 * Driver program for MAL ASM to text-based binary.
 * @author djb
 * @version 2023.11.21
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 1) { //checks if args is bigger then one i.e more then jsut inout file
            System.err.println("Usage: java Main file.mal");
        }
        else {
            String inputfile = args[0];   //if just the file in args
            if(inputfile.endsWith(".mal")) {  //checks if it is a .mal file
                int suffix = inputfile.lastIndexOf("."); //changes the .mal to .bin
                String outputfile = inputfile.substring(0, suffix) + ".bin";
                try {
                    Assembler assem = new Assembler(inputfile, outputfile);  //run an assembler with input file and an empty output file of .bin
                    assem.assemble(); //calls the assemble function
                } catch (IOException ex) {
                    System.err.println("Exception parsing: " + inputfile);
                    System.err.println(ex);
                }
            }
            else {
                System.err.println("Unrecognised file type: " + inputfile);
            }
        }
    }
}
