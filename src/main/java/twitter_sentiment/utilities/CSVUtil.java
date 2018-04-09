package twitter_sentiment.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVUtil {

    public static ArrayList<String> loadTwitterHandles() {

        // Ryan: I'd add these paths to a properties file and use @Value so you can hot swap them without having to recompile and redeploy
        String housePath = "/Users/kevingraham/Documents/Development/java/twitter_sentiment/src/main/resources/csv/115th-Congress-House-seeds.csv";
        String senatePath = "/Users/kevingraham/Documents/Development/java/twitter_sentiment/src/main/resources/csv/115th-Congress-Senate-seeds.csv";

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
