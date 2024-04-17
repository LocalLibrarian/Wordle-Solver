package com.travisshirley;

//Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class solver {
    //Constants/globals
    //Current list of possible words given current info from guesses
    static ArrayList<String> ValidWords = new ArrayList<String>();
    //List of letters sorted by frequency in the dataset
    static ArrayList<Character> Frequents = new ArrayList<Character>();
    //Default starting word
    String START = "ADIEU";

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

    //Gathers frequency data about the dataset. MUST RUN before deciding first
    //guess, then doesn't need to run ever again
    static void gatherFrequencies() {
        //Init all letters to 0
        HashMap<Character, Integer> counts = new HashMap<Character, Integer>();
        for(char x : gameSim.Alphabet) {
            counts.put(x, 0);
        }

        //Get count of all letters in the dataset
        for(int i = 0; i < ValidWords.size(); i++) {
            //Faster access
            String current = ValidWords.get(i);
            for(int j = 0; j < gameSim.MAX_LETTERS; j++) {
                //Same thing as above
                Character curChar = current.charAt(j);
                counts.replace(curChar, counts.get(curChar) + 1);
            }
        }

        //Sort everything
        ArrayList<Integer> temp = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
            temp.add(entry.getValue());
        }
        Collections.sort(temp); 
        for (int num : temp) {
            for (Entry<Character, Integer> entry : counts.entrySet()) {
                if (entry.getValue().equals(num)) {
                    Frequents.add(entry.getKey());
                }
            }
        }
        
        //Reverse order so first element is most frequent
        Collections.reverse(Frequents);
    }

    public static void main(String[] args) {
        gameSim game = new gameSim();
        String goal = "MOSSY";
        String tryer = "BRASS";
        game.setWord(goal, false);
        ValidWords = (ArrayList<String>)game.WordsList.clone();
        gatherFrequencies();
        eliminateWords(game.guess(tryer), tryer);
        System.out.println(ValidWords.size());
        System.out.println(ValidWords.indexOf(goal));
        System.out.println(ValidWords);
    }
}