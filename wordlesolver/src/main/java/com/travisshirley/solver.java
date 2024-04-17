package com.travisshirley;

//Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class solver {
    //Constants/globals
    //Current list of possible words given current info from guesses
    static ArrayList<String> ValidWords = new ArrayList<String>();
    //List of letters sorted by frequency in the dataset
    static ArrayList<Character> Frequents = new ArrayList<Character>();
    //Default starting word
    static String START = "SALET";

    //Deletes words that are now impossible from ValidWords based on
    //guessResult and guess
    static void eliminateWords(int[] guessResult, String guess) {
        //Create list to hold letters that have unknown positions
        ArrayList<Character> maybes = new ArrayList<Character>();
        for(int i = 0; i < guess.length(); i++) {
            if(guessResult[i] == gameSim.IN_WORD) maybes.add(guess.charAt(i));
        }

        //Eliminate everything that has things in the wrong spots
        for(int i = 0; i < ValidWords.size(); i++) {
            //Store the current word for faster access
            String current = ValidWords.get(i);
            for(int j = 0; j < guess.length(); j++) {
                if(current.charAt(j) == guess.charAt(j)) {
                    if(guessResult[j] == gameSim.IN_WORD ||
                        guessResult[j] == gameSim.NOT_IN_WORD) {
                            //Letter in wrong spot in this word
                            ValidWords.remove(i);
                            i -= 1; //Check new element at this index
                            j = guess.length(); //Break loop early
                        }
                } else {
                    if(guessResult[j] == gameSim.IN_WORD_AT_POSITION) {
                        //Wrong letter in this spot in this word
                        ValidWords.remove(i);
                        i -= 1; //Check new element at this index
                        j = guess.length(); //Break loop early
                    }
                }
            }
        }

        //Eliminate words that DON'T contain letters in unknown positions
        for(int i = 0; i < ValidWords.size(); i++) {
            //Store current for faster access, like above
            String current = ValidWords.get(i);
            for(int j = 0; j < maybes.size(); j++) {
                if(current.indexOf(maybes.get(j)) < 0) {
                    ValidWords.remove(i);
                    i -= 1; //Check new element at this index
                    j = maybes.size(); //Break loop early
                }
            }
        }
    }

    //Tries to pick a good guess given current information
    static String pickGuess(int[] guessResult) {
        //Create temp list to help pick the guess
        ArrayList<String> maybeGuess = new ArrayList<String>();
        for(String entry : ValidWords) {
            maybeGuess.add(entry);
        }

        //Create HashMap to hold count of each letter
        HashMap<Character, Integer> letterCounts = new HashMap<Character,
        Integer>();
        //Set value for each letter of alphabet to count of that letter in 
        //Word
        for(char x : gameSim.Alphabet) {
            letterCounts.put(x, 0);
        }

        /*
        Find the most common letter in each position and only consider those
        for guesses until down to the last word
        */
        int r = 0;
        while(r < gameSim.MAX_LETTERS) {
            //Reset counts
            for(char x : gameSim.Alphabet) {
                letterCounts.replace(x, 0);
            }

            //Get commonality of each letter in each position
            for(int i = 0; i < maybeGuess.size(); i++) {
                //Faster access (again)
                String current = maybeGuess.get(i);
                Character curChar = current.charAt(r);
                letterCounts.replace(curChar, letterCounts.get(curChar) + 1);
            }

            //Find (latest) max in letterCounts
            int max = 0;
            char maxChar = 'z';
            for(char x : gameSim.Alphabet) {
                if(letterCounts.get(x) > max) {
                    max = letterCounts.get(x);
                    maxChar = x;
                }
            }

            //Only consider words with that have the most common letter in this
            //position
            for(int i = 0; i < maybeGuess.size(); i++) {
                //Faster access (again)
                String current = maybeGuess.get(i);
                if(current.charAt(r) != maxChar) {
                    maybeGuess.remove(i);
                    i -= 1; //Check new element at i
                }
            }

            //Advance to next position
            r += 1;
        }
        return maybeGuess.get(0);
    }

    public static void main(String[] args) {
        gameSim game = new gameSim();

        int r = 0;
        int wins = 0;
        int trials = 1000;
        String winner = "!";
        String loser = "?";
        System.out.println("Running " + trials + " trials. Each " + winner +
            " is a win, and each " + loser + " is a loss:");
        long startTime = System.currentTimeMillis();
        long trialStartTime = System.currentTimeMillis();
        long avgTrialTime = 0L;
        while(r < trials) {
            ValidWords.clear();
            ValidWords.addAll(game.WordsList);
            game.setWord("", true);
            //System.out.println("Goal: " + game.Word);
            String guesser = START;
            if(!game.inWordsList(START)) {
                Collections.shuffle(ValidWords);
                guesser = ValidWords.get(0);
            }
            int[] guessRes = game.guess(guesser);
            eliminateWords(guessRes, guesser);
            while(guessRes[0] != gameSim.LOSE && ValidWords.size() > 1) {
                //System.out.println(guesser);
                guesser = pickGuess(guessRes);
                guessRes = game.guess(guesser);
                eliminateWords(guessRes, guesser);
            }
            //System.out.println(ValidWords);
            //System.out.println(game.Guess);
            if(guessRes[0] != gameSim.LOSE) {
                wins++;
                System.out.print(winner);
            } else System.out.print(loser);

            //Advance to next trial
            long trialEndTime = System.currentTimeMillis();
            avgTrialTime += trialEndTime - trialStartTime;
            r++;
        }
        long endTime = System.currentTimeMillis();
        double totalTime = ((double)endTime - (double)startTime) / 1000.0;
        System.out.println("\nWin rate: " + (double)wins / (double)trials);
        System.out.println("Average time per trial: " + 
            ((double)avgTrialTime / (double)trials) + " ms");
        System.out.println("Total time taken: " + totalTime + " sec");
    }
}