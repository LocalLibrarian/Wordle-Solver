package com.travisshirley;

//Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class solver {
    //Constants/globals
    //Current list of possible words given current info from guesses
    ArrayList<String> ValidWords = new ArrayList<String>();
    //Default starting word
    String START = "SALET";
    //The game simulator
    gameSim game = new gameSim();
    //Enumerator for Algorithms
    public static enum Algorithm {
        SIXTY_TWO
    }

    //For returning results from play() function
    public static class Result {
        //Values
        int numGuesses = 0; //How many guesses to arrive at this result
        boolean won = false; //Whether the game was won or not

        //Constructor
        Result(int guesses, boolean winner) {
            numGuesses = guesses;
            won = winner;
        }
    }

    //Deletes words that are now impossible from ValidWords based on
    //guessResult and guess
    void eliminateWords(int[] guessResult, String guess) {
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
    String sixtyTwoAlgo(int[] guessResult) {
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
        String nextGuess = "";
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

            if(game.Guess == 1) {
                //Try to pick a word eliminating as many other words as possible
                //on the 2nd guess
                nextGuess = nextGuess + maxChar;
            } else {
                //Only consider words with that have the most common letter in
                //this position
                for(int i = 0; i < maybeGuess.size(); i++) {
                    //Faster access (again)
                    String current = maybeGuess.get(i);
                    if(current.charAt(r) != maxChar) {
                        maybeGuess.remove(i);
                        i -= 1; //Check new element at i
                    }
                }
            }

            //Advance to next position
            r += 1;
        }

        //Return the word eliminating as many words as possible if it can,
        //otherwise return word from main method for this algorithm
        if(game.Guess == 1) {
            if(game.inWordsList(nextGuess)) {
                return nextGuess;
            }
            game.Guess -= 1;
            String res = sixtyTwoAlgo(guessResult);
            game.Guess += 1;
            return res;
        }
        return maybeGuess.get(0);
    }

    //Plays the game using the Algorithm from algo
    Result play(Algorithm algo) {
        //Use 64% accurate algorithm, first one I designed
        if(algo == Algorithm.SIXTY_TWO) {
            //Init
            ValidWords.clear();
            ValidWords.addAll(game.WordsList);
            game.setWord("", true);
            String guesser = START;

            //Change the starting word if it is somehow not valid
            if(!game.inWordsList(START)) {
                Collections.shuffle(ValidWords);
                guesser = ValidWords.get(0);
            }

            //First guess
            int[] guessRes = game.guess(guesser);
            eliminateWords(guessRes, guesser);

            //Keep guessing until lose or win
            while(guessRes[0] != gameSim.LOSE && guesser != game.Word) {
                guesser = sixtyTwoAlgo(guessRes);
                guessRes = game.guess(guesser);
                eliminateWords(guessRes, guesser);
            }

            //Return appropriate Result
            if(guessRes[0] != gameSim.LOSE) {
                return new Result(game.Guess, true);
            }
            return new Result(game.MAX_GUESSES, false);
        } 
        
        else { //Return invalid Result since no valid Algorithm was passed
            return new Result(gameSim.LOSE, false);
        }
    }
}