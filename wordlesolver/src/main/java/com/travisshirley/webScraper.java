package com.travisshirley;

//Imports
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.FileWriter;

public class webScraper {
    //Constants
    static final String OUTFILE = "words.txt"; //Output file name
    static final String STARTPAGE = //Starting page URL
        "https://www.bestwordlist.com/5letterwords.htm";
    static final int MAXPAGENUM = 26; //Ending page number in URLs
    static final String MIDPAGESTART = //URL beginning for other pages
        "https://www.bestwordlist.com/5letterwordspage";
    static final String MIDPAGEEND = ".htm"; //URL ending for other pages

    public static void main(String[] args) {
        int wordNum = 0;
        int pageNum = 1;

        //Create/open the OUTFILE
        FileWriter outFile;
        try {
            outFile = new FileWriter(OUTFILE);
        

            //Create the WebDriver
            WebDriver driver = new ChromeDriver();

            //Set the first URL
            String url = STARTPAGE;

            //Go through all 26 pages
            while(pageNum <= MAXPAGENUM) {
                //Open the page
                driver.get(url);

                //Get the words
                String text = "NULL";
                if(pageNum == 1) {
                    text = driver.findElement(By.xpath(
                        "/html/body/table/tbody/tr/td[2]/p[3]/span"
                        )).getText();
                } else {
                    text = driver.findElement(By.xpath(
                        "/html/body/table/tbody/tr/td[2]/p[4]/span"
                        )).getText();
                }
                
                //Split words by spaces
                String[] split = text.split(" ");

                //Write words to OUTFILE
                for(int i = 0; i < split.length; i++) {
                    try {
                        //Increase number of words by 1
                        wordNum += 1;

                        if(wordNum == 1) {
                            outFile.write(split[i]);
                        } else {
                            outFile.write("\n" + split[i]);
                        }
                    } catch (Exception e) {
                        System.out.println("Could not write to " + 
                            OUTFILE + ", " + "aborting...");
                        System.out.println(e.getStackTrace());
                        System.exit(2);
                    }
                }

                //Update page number
                pageNum += 1;

                //Set next URL
                url = MIDPAGESTART + pageNum + MIDPAGEEND;
            }
                
            //Close resources
            driver.quit();
            outFile.close();

            //Output number of words and pages read
            System.out.println("Successfully read " + (pageNum - 1) + " pages "+
                "and " + wordNum + " words.");

        } catch (Exception e) {
            System.out.println("Could not open/create " + OUTFILE + ", " +
                        "aborting...");
            System.out.println(e.getStackTrace());
            System.exit(1);
        }
    }
}