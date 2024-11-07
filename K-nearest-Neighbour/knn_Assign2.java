import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class knn_Assign2 { 
    public static void main(String[] args) throws IOException{ //if the file doesnt load then an exception is thrown
    
    int TRAIN_SIZE = 200; //amount of SAMPLES
    int TEST_SIZE = 200;
    int FEATURE_SIZE = 61; //4 dimensional space so this will be set to 60 for a 60 dimensional 

    //30 elements all with 4 features so a 30 by 4 array
    double[][] train = new double[TRAIN_SIZE][FEATURE_SIZE];

    try(Scanner data = new Scanner(new File("train_data.txt"))){

        //loop over each row and for each row loop over the column
        for(int i = 0; i<TRAIN_SIZE;i++){
            for(int j =0; j < FEATURE_SIZE;j++){
                //gets the double of the current row and column being iterated over
                if(data.hasNextDouble()){
                    //adds this double to the train array
                    train[i][j] = data.nextDouble();
                }
            }
        }
        data.close();
        }
        double[][] test = new double[TEST_SIZE][FEATURE_SIZE];
        try(Scanner data = new Scanner(new File("test_data.txt"))){

        //loop over each row and for each row loop over the column
        for(int i = 0; i<TEST_SIZE;i++){
            for(int j =0; j < FEATURE_SIZE;j++){
                //gets the double of the current row and column being iterated over
                if(data.hasNextDouble()){
                    //adds this double to the train array
                    test[i][j] = data.nextDouble();
                }
            }
        }
        
        data.close();
        }
        int[] test_label = new int[TEST_SIZE];
        int[] train_label = new int[TRAIN_SIZE];

        try(Scanner label = new Scanner(new File("train_label.txt"))){
            for(int i = 0; i < TRAIN_SIZE;i++){
                if(label.hasNextInt()){ 
                train_label[i] = label.nextInt();
                }
            }
        label.close();
        }
        try(Scanner label = new Scanner(new File("test_label.txt"))){
            for(int i = 0; i < TEST_SIZE;i++){
                if(label.hasNextInt()){ 
                test_label[i] = label.nextInt();
                }
            }
        label.close();
        }
        double[] distances = new double[TEST_SIZE];
        int[] predictions = new int[TEST_SIZE];
        
        
        //FOR (INT K = ) //TO INCREASE THE AMOUNT OF KS 

        //THIS IF FOR ONE K
        for(int i = 0; i < TEST_SIZE;i++){
            for(int j = 0; j<TRAIN_SIZE;j++){
                distances[i] = EuclDistance(test[i],train[j]); 
                }     
                int minimum = 0;
                //this is for k=1
                    for(int j= 0; j < distances.length;j++){
                        if (distances[j] < distances[minimum])
                        minimum=j;
                    }
                    predictions[i] = test_label[minimum];

        }
        accuracy(predictions,test_label);        
         try(PrintWriter writer = new PrintWriter("output.txt")){
                for(int i =0; i <predictions.length ;i++){
                writer.print(predictions[i]+" ");
                }
         }catch (Exception e){
            System.out.println(e);
        }

    
}

 public static double EuclDistance(double[] a, double[] b){
                     double sum =0.0;
                     for(int i =0;i<a.length;i++){
                     double diff = a[i] -b[i]; //diferecne between every feature
                     sum+= (diff*diff); //square and add it 
                     }
                     return Math.sqrt(sum);
 }
 public static double accuracy(int[] predictions, int[] test_label){
    int count =0;
    double accuracy = 0.0;
    for (int i =0; i<predictions.length;i++){
        if(predictions[i] == test_label[i]){
            count++;
        }
        accuracy = (double)count/predictions.length;
        
    }
    System.out.println("Accuracy: " + accuracy);
    return accuracy;

 }
}