package com.travisshirley;

//Imports
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class gameSim {
    //Globals/constants
    //Default data file name
    String DEFAULT_FILE = "words.txt";
    //Init of list of valid words
    public ArrayList<String> WordsList = new ArrayList<String>();
    //Init of current correct word
    String Word = "DEFAULT";
    //Array of all letters in alphabet
    static char[] Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    //Current guess count
    int Guess = 0;
    //Max guess amount
    int MAX_GUESSES = 6;
    //Invalid guess number
    public static int INVALID = -1;
    //Past max guess number
    public static int LOSE = -2;
    //Letter is not in word at all
    public static int NOT_IN_WORD = 0;
    //Letter is in word at a different position
    public static int IN_WORD = 1;
    //Letter is in word at current position
    public static int IN_WORD_AT_POSITION = 2;
    //Max letters in a guess
    public static int MAX_LETTERS = 5;
    

    //Sets the WordsList to the one from newFile
    public void setWordsList(String newFile) {
        File file = new File(newFile);
        try {
            //Try to open newFile
            Scanner newFileScanner = new Scanner(file);

            //Empty the WordsList
            WordsList.clear();

            //Read all words from file and store them in memory at WordsList
            while(newFileScanner.hasNextLine()) {
                WordsList.add(newFileScanner.nextLine());
            }

            //Close no longer needed resources
            newFileScanner.close();
        } catch (Exception e) {
            System.out.println("Failed to open file " + newFile + ", " +
                "cancelling open.");
            e.printStackTrace();
        }
    }

    //Sets the current Word to word, or something randomly and resets Guess
    //Returns if set was successful or not.
    public boolean setWord(String word, boolean rndm) {
        //Set Word explicitly if non-random approach
        if(!rndm) {
            //Check if word is valid
            boolean has = WordsList.contains(word);

            //Set Word to word if it is valid, or return false
            if(has) {
                Word = word;
                Guess = 0;
                return true;
            }
            return false;
        }

        //Randomly pick Word from WordsList
        Collections.shuffle(WordsList);
        Word = WordsList.get(0);
        Guess = 0;
        return true;
    }

    //Returns how many times c occurs in word
    int countOccur(char c, String word) {
        int count = 0;
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == c) count++;
        }
        return count;
    }

    //Returns whether word is in WordsList and thus is a valid guess
    public boolean inWordsList(String word) {
        return WordsList.contains(word);
    }

    /*
    Simulates guessing word.

    Returns: 5-element array that corresponds to correctness of guess.
    LOSE = past max guesses
    INVALID = invalid word (not in WordsList)
    NOT_IN_WORD = letter at given index is not in Word at all (black)
    IN_WORD = letter at given index is in Word, but not this position (yellow)
    IN_WORD_AT_POSITION = letter at given index is in Word at this position 
        (green)

    Returns have to be handled appropriately by the caller.
    */
    public int[] guess(String word) {
        if(Guess < MAX_GUESSES) {
            //Return all INVALIDs if invalid guess, as described above
            if(!inWordsList(word)) {
                return new int[] {INVALID, INVALID, INVALID, INVALID, INVALID};
            }

            //Set Guess to current Guess number
            Guess++;

            //Create default array to return
            int[] result = {NOT_IN_WORD, NOT_IN_WORD, NOT_IN_WORD, NOT_IN_WORD,
                NOT_IN_WORD};

            //Create HashMap to hold count of each letter
            HashMap<Character, Integer> letterCounts = new HashMap<Character,
                Integer>();
            //Set value for each letter of alphabet to count of that letter in 
            //Word
            for(char x : Alphabet) {
                letterCounts.put(x, countOccur(x, Word));
            }

            //Check for correctly positioned letters
            for(int i = 0; i < word.length(); i++) {
                char x = word.charAt(i);
                if(x == Word.charAt(i)) {
                    result[i] = IN_WORD_AT_POSITION;
                    letterCounts.replace(x, letterCounts.get(x) - 1);
                }
            }

            //Check for incorrect (but still present) letters
            for(int i = 0; i < word.length(); i++) {
                char x = word.charAt(i);
                if(Word.indexOf(x) > -1 && letterCounts.get(x) > 0 &&
                    result[i] == NOT_IN_WORD) {
                    result[i] = IN_WORD;
                    letterCounts.replace(x, letterCounts.get(x) - 1);
                }
            }

            //Return the guess result array
            return result;
        }

        //Return indicator of being past max guess amount
        return new int[] {LOSE, LOSE, LOSE, LOSE, LOSE};
    }

    //Prints the given array, element by element
    public void printArray(int[] arr, String sep) {
        System.out.print("{");
        for (int i = 0; i < arr.length; i++) {
            if(i == arr.length - 1) {
                System.out.print(arr[i]);
            } else {
                System.out.print(arr[i] + sep);
            }
        }
        System.out.print("}");
    }

    //Constructors
    public gameSim() {
        super();
        setWordsList(DEFAULT_FILE);
    }
    public gameSim(String startFile) {
        super();
        setWordsList(startFile);
    }
}