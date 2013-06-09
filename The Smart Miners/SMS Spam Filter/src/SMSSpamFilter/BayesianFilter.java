package SMSSpamFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Tushar, Kosha, Sankalpa
 */
public class BayesianFilter {

    private HashMap<String, Integer> hams; // each ham word with its count
    private HashMap<String, Integer> spams; // each spam word with its count
    private int hamCount = 0; // total #ham messages in dataset
    private int spamCount = 0; // total #spam messages in dataset
    private int spamWordCount = 0; // total #words in spam tuples in dataset
    private int hamWordCount = 0; // total #words in ham tuples in dataset
    private int uniqueWordCount = 0; // unique terms in vocabulary
    private int totalMessageCount; // total #messages in dataset
    private String strLine = null;
    private String label = null; // label in {ham,spam}
    private double ps = 0.0; // probability of word being a spam
    private double ph = 0.0; // probability of word being a ham
    // conditional probability hashmaps
    private HashMap<String, Double> condSpamProb;
    private HashMap<String, Double> condHamProb;
    // final ham or spam score which will be compared to predict label
    private double hamScore = 0.0;
    private double spamScore = 0.0;

    BayesianFilter() {
        hams = new HashMap<>();
        spams = new HashMap<>();
        condSpamProb = new HashMap<>();
        condHamProb = new HashMap<>();
    }

    public int train() {

        BufferedReader br;
        try {

            /**
             * Read training dataset and get label and message for each tuple.
             * Depending on whether the label is ham or spam, each word in the
             * tuple will be stored with its count in the respective hashmap
             */
            br = new BufferedReader(new FileReader("bayesian_train.txt"));
            String arr[] = null;

            while ((strLine = br.readLine()) != null) {

                arr = strLine.split("\\W+");
                label = arr[0];
                if (label.equals("ham")) {
                    hamCount++;
                    for (int i = 1; i < arr.length; i++) {
                        String currHam = arr[i].toLowerCase();
                        if (!spams.containsKey(currHam) && !hams.containsKey(currHam)) {
                            uniqueWordCount++;

                            /**
                             * Each word is also put in another hashmaps which
                             * will store the probability of occurrence of each
                             * word in ham or spam
                             */
                            condHamProb.put(currHam, -1.0);
                            condSpamProb.put(currHam, -1.0);
                        }
                        hamWordCount++;

                        /**
                         * If word is already present in ham hashmap then
                         * increment its count otherwise add it to the hashmap
                         * with intial count set to 1
                         */
                        if (hams.containsKey(currHam)) {
                            int existingCount = hams.get(currHam);
                            existingCount++;
                            hams.put(currHam, existingCount);
                        } else {
                            hams.put(currHam, 1);
                        }

                    }
                }

                // Similarly do for spam message
                if (label.equals("spam")) {
                    spamCount++;
                    for (int i = 1; i < arr.length; i++) {
                        String currSpam = arr[i].toLowerCase();
                        if (!spams.containsKey(currSpam) && !hams.containsKey(currSpam)) {
                            uniqueWordCount++;
                            condHamProb.put(currSpam, -1.0);
                            condSpamProb.put(currSpam, -1.0);
                        }
                        spamWordCount++;
                        if (spams.containsKey(currSpam)) {
                            int existingCount = spams.get(currSpam);
                            existingCount++;
                            spams.put(currSpam, existingCount);
                        } else {
                            spams.put(currSpam, 1);
                        }

                    }
                }
            }

            totalMessageCount = hamCount + spamCount;
            ps = (double) spamCount / totalMessageCount;
            ph = (double) hamCount / totalMessageCount;

            /**
             * Calculate conditional probability for each word occuring in ham
             * or spam with Laplace smoothing
             */

            int counter = 0;
            for (String spamWord : condSpamProb.keySet()) {
                double probVal = 0.0;
                if (spams.containsKey(spamWord)) {
                    probVal = (double) (spams.get(spamWord) + 1) / (spamWordCount + uniqueWordCount);
                } else {
                    probVal = (double) 1 / (spamWordCount + uniqueWordCount);
                }
                condSpamProb.put(spamWord, probVal);
            }

            for (String hamWord : condHamProb.keySet()) {
                double probVal = 0.0;
                if (hams.containsKey(hamWord)) {
                    probVal = (double) (hams.get(hamWord) + 1) / (hamWordCount + uniqueWordCount);
                } else {
                    probVal = (double) 1 / (hamWordCount + uniqueWordCount);
                }
                condHamProb.put(hamWord, probVal);
            }


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        
        testAndEvaluate();
        return 100;
    }

    /**
     * Predicts a label{Spam or Ham} given a String message
     *
     * @param message
     * @return Label
     */
    public String predictLabel(String message) {
        // if no message do nothing
        if (message == null) {
            return null;
        }
        String[] tokenArray = message.toLowerCase().split("\\W+");
        // tokens that are already present in dataset
        List<String> commonTokens = new ArrayList<String>();

        for (String token : tokenArray) {
            if (condSpamProb.containsKey(token)) {
                commonTokens.add(token);
            }
        }

        /**
         * It is better to perform the computation by adding logarithms of
         * probabilities instead of multiplying probabilities because
         * multiplication might cause floating point underflow
         */
        // initial spam and ham score
        spamScore = Math.log(ps);
        hamScore = Math.log(ph);

        /**
         * find cumulative spamscore and hamscore
         */
        for (String s : commonTokens) {
            spamScore += Math.log(condSpamProb.get(s));
            hamScore += Math.log(condHamProb.get(s));
        }
        /**
         * Compare score of ham and spam and predict output
         */
        if (hamScore > spamScore) {
            return "ham";
        } else {
            return "spam";
        }

    }

    /**
     * Testing and evaluating the model based on various evaluation measures
     */
    public void testAndEvaluate() {
        String line;
        String actualLabel;
        String predictedLabel;
        BufferedReader brreader;

        int truePos = 0; // # of true positive tuples
        int trueNeg = 0; // # of true negative tuples
        int falsePos = 0; // # of false positive tuples
        int falseNeg = 0; // # of false negative tuples

        double accuracy = 0;
        double sensitivity = 0;
        double specificity = 0;
        double precision = 0;
        double recall = 0;

        int i = 1;
        try {
            WriteToFile wrf = new WriteToFile();
            brreader = new BufferedReader(new FileReader("bayesian_test.txt"));
            String arr[];
            int lineNum = 1;
            
            while ((line = brreader.readLine()) != null) {
                if (lineNum == 1)
                wrf.eraseFile("Bayesian_Output.txt");
                arr = line.split("\t");
                actualLabel = arr[0];
                predictedLabel = predictLabel(arr[1]);
                wrf.writeOutput((i + " ---> Actual Label :" + actualLabel + " ------ Predicted Label  " + predictedLabel + "\n"),"Bayesian_Output.txt");
                i++;

                if (predictedLabel.equals(actualLabel)) {
                    if (actualLabel.equals("spam")) {
                        truePos++;
                    } else {
                        trueNeg++;
                    }
                } else {
                    if (predictedLabel.equals("spam")) {
                        falsePos++;
                    } else {
                        falseNeg++;
                    }
                }
                
                  lineNum++;
            }

            accuracy = (double) (truePos + trueNeg) / (trueNeg + truePos + falseNeg + falsePos);
            sensitivity = (double) truePos / (truePos + falseNeg);
            specificity = (double) trueNeg / (trueNeg + falsePos);
            precision = (double) truePos / (truePos + falsePos);

            wrf.writeOutput(("\n\nBayesian Evaluation : \n"),"Bayesian_Output.txt");
            wrf.writeOutput(("Accuracy is " + accuracy + "\n"),"Bayesian_Output.txt");
            wrf.writeOutput(("Sensitivity/Recall is " + sensitivity + "\n"),"Bayesian_Output.txt");
            wrf.writeOutput(("Specificity is " + specificity + "\n"),"Bayesian_Output.txt");
            wrf.writeOutput(("Precision is " + precision + "\n"),"Bayesian_Output.txt");

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
}
