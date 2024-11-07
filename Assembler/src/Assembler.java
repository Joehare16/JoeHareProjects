import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Coordinate the translation of MAL assembly code to text-based binary.
 * 
 * @author Joe Hare - jh2199
 * @version
 */
public class Assembler
{
    // The lines of the input file.
    private List<String> input;
    // Where to write the output.
    private PrintWriter output;
    /**
     * Create an assembler.
     * @param inputfile The input file.
     * @param outputfile The output file.
     */
    public Assembler(String inputfile, String outputfile)
        throws IOException
    {
        input = Files.readAllLines(Paths.get(inputfile)); //input is the file that been inputted
        output = new PrintWriter(new FileWriter(outputfile)); //creates a printwriter that the output lines can be written to
    }

    /**
     * Translate the input file, line by line.
     * 
     */
    public void assemble()
    {
        for(String line : input) { //goes through every line of the input and passes it into the translation
            translateOneInstruction(line);
        }
        output.close(); //once all the line have been translated
    }

    /**
     * Translate one line of MAL assembly code to text-based binary.
     * @param line The line to translate.
     */
    private void translateOneInstruction(String line)
    {
        StringBuilder cBin = new StringBuilder();
        String[] parts = line.replaceAll(",", " ").split("\\s+");
        for(int i =0;i<parts.length;i++) {
            System.out.println(parts[i]);
        }
        if (parts.length ==  3){  //if the string splits into 3 then the second item has to be a register the third can be either number or register
            String command =parts[0];
            cBin.append(operand(command));;
            String reg = parts[1];
            cBin.append(operand(reg));
            if((isDigit(parts[2]))){
                String num = parts[2];
                cBin.append("00");
                cBin.append(System.lineSeparator()).append(binConvert(Integer.parseInt(num))); //checks if the third item is a num then adds variable num

            }
            else{
                String reg1 = parts[2];
                cBin.append(operand(reg1)); //if the item is not a num then set to reg1
            }

        }
        else{
            String command = parts[0];
            cBin.append(operand(command)).append("0000").append(System.lineSeparator());//if the string only splits into two items then it has to be a command followed by a number
            String num = parts[1];
            cBin.append(binConvert(Integer.parseInt(num)));
        }
        line = cBin.toString();
        output.println(line);
    }
    public static StringBuilder binConvert(int binary) {
        StringBuilder Bin = new StringBuilder();
        String binary1 = Integer.toBinaryString(binary);
        int padding = 8 - binary1.length();
        for(int i = 0; i < padding;i++) {
            Bin.append("0");
        }
        Bin.append(binary1);

        return Bin;
    }
    public static String operand (String Operand){
        HashMap<String,String> OpBin = new HashMap<>();
        OpBin.put("LOADN","0000");
        OpBin.put("LOADA","0001");
        OpBin.put("ADD","0010");
        OpBin.put("SUB","0011");
        OpBin.put("JMP","0100");
        OpBin.put("JGT","0101");
        OpBin.put("JLT","0110");
        OpBin.put("JEQ","0111");
        OpBin.put("COPY","1000");
        OpBin.put("STORE","1001");
        OpBin.put("A","01");
        OpBin.put("D","10");

        return OpBin.get(Operand);
    }
    public static boolean isDigit(String split3) {
        try {
            Integer.parseInt(split3);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
