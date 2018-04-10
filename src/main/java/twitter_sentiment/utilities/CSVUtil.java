package twitter_sentiment.utilities;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVUtil {

    @Value("${csv.houseOfRepresentatives}")
    private String housePath;

    @Value("${csv.senators}")
    private String senatePath;

    private ArrayList<String> twitterHandles;


    public ArrayList<String> getTwitterHandles() {
        // read csv if not already in memory
        if (twitterHandles == null) {
            twitterHandles = loadTwitterHandles();
        }

        return twitterHandles;
    }

    private ArrayList<String> loadTwitterHandles() {

        ArrayList<String> result = new ArrayList<>();

        // read house of representatives csv
        try (BufferedReader fin = new BufferedReader(new FileReader(housePath))){
            String line;

            while ((line = fin.readLine()) != null) {
                String handle = line.split(",")[0];
                result.add(handle);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // read senate csv
        try (BufferedReader fin = new BufferedReader(new FileReader(senatePath))){
            String line;

            while ((line = fin.readLine()) != null) {
                String handle = line.split(",")[0];
                result.add(handle);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return result;
    }
}
