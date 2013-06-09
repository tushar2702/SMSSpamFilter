package SMSSpamFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class WriteToFile {

    public void writeOutput(String content, String filename) {

        try {

            File file = new File(filename);

            // if file doesnt exist, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(content);
            bufferWritter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eraseFile(String filename) {

        try {

            File file = new File(filename);

            FileWriter fileWritter = new FileWriter(file.getName(), false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("");
            bufferWritter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
