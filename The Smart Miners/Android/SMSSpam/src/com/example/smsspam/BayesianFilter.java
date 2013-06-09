package com.example.smsspam;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.Toast;


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
	        hams = new HashMap<String, Integer>();
	        spams = new HashMap<String, Integer>();
	        condSpamProb = new HashMap<String, Double>();
	        condHamProb = new HashMap<String, Double>();
	    }

	    public void train(Context context) {

	        BufferedReader br;
	        try {

	            /**
	             * Read training dataset and get label and message for each
	             * tuple. Depending on whether the label is ham or spam, each word
	             * in the tuple will be stored with its count in the respective
	             * hashmap 
	             */
	        	
	        	
	        	InputStream raw= context.getAssets().open("SMSSpamCollection.txt");
	            br = new BufferedReader(new InputStreamReader(raw, "UTF8"));
	           
	            
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
                             * Each word is also put in another hashmaps
                             * which will store the probability of occurrence
                             * of each word in ham or spam 
                             */
                            condHamProb.put(currHam, -1.0);
                            condSpamProb.put(currHam, -1.0);
                        }
                        hamWordCount++;

                        /**
                         * If word is already present in ham hashmap
                         * then increment its count otherwise add it
                         * to the hashmap with intial count set to 1 
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

	            
	            Toast.makeText(context, totalMessageCount + " ", Toast.LENGTH_LONG).show(); 
	            
	            
	            
	            
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }

	    }
	    
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
	         * It is better to perform the computation by adding
	         * logarithms of probabilities instead of multiplying probabilities
	         * because multiplication might cause floating point underflow 
	         */
	        
	        // initial spam score
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

}
