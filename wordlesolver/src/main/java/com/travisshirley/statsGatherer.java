package com.travisshirley;

import java.util.Scanner;

//Imports
import com.travisshirley.solver.Algorithm;
import com.travisshirley.solver.Result;

public class statsGatherer {
    //Runs numTrials trials of Algorithm algo to gather statistics
    private static void runTrials(int numTrials, Algorithm algo) {
        //solver class init
        solver solution = new solver();

        //General stats init
        int num = 0;
        int wins = 0;
        int totalGuesses = 0;
        System.out.println("Running " + numTrials + " trials...");
        long avgTrialTime = 0L;
        long startTime = System.currentTimeMillis();

        //Run each trial
        while(num < numTrials) {
            long trialStartTime = System.currentTimeMillis();
            Result result = solution.play(algo);
            long trialEndTime = System.currentTimeMillis();

            //Increase wins if won
            if(result.won) {
                wins++;
                totalGuesses += result.numGuesses;
            }

            //Advance to next trial
            avgTrialTime += trialEndTime - trialStartTime;
            num++;
        }

        //Output all the results after the trials are done
        long endTime = System.currentTimeMillis();
        double totalTime = ((double)endTime - (double)startTime) / 1000.0;
        double winRate = ((double)wins / (double)numTrials) * 100;
        System.out.println("Done!\nWin rate: " + winRate + "%");
        System.out.println("Average guesses per win: " + 
            ((double)totalGuesses / (double)numTrials));
        System.out.println("Average time per trial: " + 
            ((double)avgTrialTime / (double)numTrials) + " ms");
        System.out.println("Total time taken: " + totalTime + " sec");
    }

    public static void main(String args[]) {
        //Create the Scanner for user input and init the input String
        Scanner inputScanner = new Scanner(System.in);
        String input = "";

        //Init other input variables
        int trials = 0;
        int choice = 0;

        while(!input.equals("q")) {
            System.out.println("This program allows you to run the various " +
                "algorithms in solver.java and display statistics about their" +
                    " accuracy.");
            //Get input
            System.out.print("Please enter the number of trials to run, or " +
                "q to quit: ");
            input = inputScanner.nextLine();
            System.out.println();

            //Try to get trials as an int
            if(!input.equals("q")) {
                try {
                    trials = Integer.valueOf(input);
                    System.out.println("Please pick an algorithm to use. Options:");
                    System.out.println("1. SixtyTwo");
                    System.out.print("Enter your choice, or q to exit: ");
                    input = inputScanner.nextLine();
                    System.out.println();

                    //Try to get choice as an int
                    if(!input.equals("q")) {
                        try {
                            choice = Integer.valueOf(input);

                            //Run trials trials times using choice algorithm
                            switch(choice) {
                                case 1:
                                    runTrials(trials, Algorithm.SIXTY_TWO);
                                    break;
                            }
                            System.out.println();
                        } catch(Exception e) {
                            System.out.println("You did not enter a valid " +
                                "choice.");
                            System.out.println();
                        }
                    }
                } catch(Exception e) {
                    System.out.println("You did not enter a valid integer.");
                    System.out.println();
                }
            }
        }

        //Close resources
        inputScanner.close();
    }
}
