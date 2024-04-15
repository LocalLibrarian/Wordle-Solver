package com.travisshirley;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;

public class sampler {
    //Constants
    static final String INFILE = "words.txt";
    static final String SAMPLE_FILE_NAME_START = "words_sample";
    static final String SAMPLE_FILE_NAME_END = ".txt";

    public static void main(String[] args) {
        //Open the INFILE
        File inFile = new File(INFILE);

        //Create the scanner for reading the INFILE and store its contents in
        //memory
        Scanner inFileScanner;
        ArrayList<String> words = new ArrayList<String>();
        try {
            inFileScanner = new Scanner(inFile);
            while(inFileScanner.hasNextLine()) {
                words.add(inFileScanner.nextLine());
            }

            //Close inFileScanner since it's no longer needed
            inFileScanner.close();
        } catch (Exception e) {
            System.out.println("Failed to open " + INFILE + ", aborting...");
            e.printStackTrace();
            System.exit(1);
        }

        //Create the Scanner for user input
        Scanner inputScanner = new Scanner(System.in);

        //Init input, start loop where we get it continuously and perform
        //actions accordingly
        String input = "NULL";
        Integer size = 0;
        while(!input.equals("q")) {
            System.out.println("This program allows you to output a random " +
                "sample of any size equal to or lesser than the dataset of " +
                INFILE + ".");

            //Get input
            System.out.print("Please enter the sample size, or q to quit: ");
            input = inputScanner.nextLine();
            System.out.println();

            if(!input.equals("q")) {
                try { //Try to convert input to an int and complain on fail
                    size = Integer.valueOf(input);

                    //Randomly shuffle words (for getting samples)
                    Collections.shuffle(words);

                    //Create the outFile for this sample size
                    String fileName = 
                        SAMPLE_FILE_NAME_START + size + SAMPLE_FILE_NAME_END;
                    FileWriter outFile = new FileWriter(fileName);

                    //Write the first size words to outFile for the sample
                    for(int i = 0; i < size; i++) {
                        if(i == 0) {
                            outFile.write(words.get(i));
                        } else {
                            outFile.write("\n" + words.get(i));
                        }
                    }

                    //Tell user process was successful
                    System.out.println("Successfully created sample of size " +
                        size + " in file " + fileName + ".");
                    System.out.println();

                    //Close outFile since it is no longer needed
                    outFile.close();
                } catch (Exception e) {
                    System.out.println("An integer of size equal to or less than " +
                        "the size of the dataset was not entered!");
                    System.out.println();
                }
            }
        }

        //Close resources
        inputScanner.close();
    }
}