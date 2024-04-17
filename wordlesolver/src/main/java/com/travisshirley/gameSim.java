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
    static String DEFAULT_FILE = "words.txt";
    //Init of list of valid words
    static ArrayList<String> WordsList = new ArrayList<String>();
    //Init of current correct word
    static String Word = "DEFAULT";
    //Array of all letters in alphabet
    static char[] Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    //Current guess count
    static int Guess = 0;
    //Max guess amount
    static int MAX_GUESSES = 6;

    //Sets the WordsList to the one from newFile
    static void setWordsList(String newFile) {
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

    //Sets the current Word to word, or something randomly.
    //Returns if set was successful or not.
    static boolean setWord(String word, boolean rndm) {
        //Set Word explicitly if non-random approach
        if(!rndm) {
            //Check if word is valid
            boolean has = WordsList.contains(word);

            //Set Word to word if it is valid, or return false
            if(has) {
                Word = word;
                return true;
            }
            return false;
        }

        //Randomly pick Word from WordsList
        Collections.shuffle(WordsList);
        Word = WordsList.get(0);
        return true;
    }

    //Returns how many times c occurs in word
    static int countOccur(char c, String word) {
        int count = 0;
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == c) count++;
        }
        return count;
    }

    /*
    Simulates guessing word.

    Returns: 5-element array that corresponds to correctness of guess.
    -2 = past max guesses
    -1 = invalid word (not in WordsList)
    0 = letter at given index is not in Word at all (black)
    1 = letter at given index is in Word, but not this position (yellow)
    2 = letter at given index is in Word at this position (green)

    Returns have to be handled appropriately by the caller.
    */
    static int[] guess(String word) {
        if(Guess < MAX_GUESSES) {
            //Return all -1s if invalid guess, as described above
            if(!WordsList.contains(word)) {
                return new int[] {-1, -1, -1, -1, -1};
            }

            //Set Guess to current Guess number
            Guess++;

            //Create default array to return
            int[] result = {0, 0, 0, 0, 0};

            //Create HashMap to hold count of each letter
            HashMap<Character, Integer> letterCounts = new HashMap<Character,
                Integer>();
            //Set value for each letter of alphabet to count of that letter in Word
            for(char x : Alphabet) {
                letterCounts.put(x, countOccur(x, Word));
            }

            //Check for correctly positioned letters
            for(int i = 0; i < word.length(); i++) {
                char x = word.charAt(i);
                if(x == Word.charAt(i)) {
                    result[i] = 2;
                    letterCounts.replace(x, letterCounts.get(x) - 1);
                }
            }

            //Check for incorrect (but still present) letters
            for(int i = 0; i < word.length(); i++) {
                char x = word.charAt(i);
                if(Word.indexOf(x) > -1 && letterCounts.get(x) > 0 &&
                    result[i] == 0) {
                    result[i] = 1;
                    letterCounts.replace(x, letterCounts.get(x) - 1);
                }
            }

            //Return the guess result array
            return result;
        }

        //Return indicator of being past max guess amount
        return new int[] {-2, -2, -2, -2, -2};
    }

    //Prints the given array, element by element
    static void printArray(int[] arr, String sep) {
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

    public static void main(String[] args) {
        //Set the default WordsList
        setWordsList(DEFAULT_FILE);

        setWord("SHOOT", false);
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
        printArray(guess("BABOO"), " ");
        System.out.println();
    }
}
