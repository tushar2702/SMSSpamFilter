SMS SPAM FILTERING 
==========================================

NOTE: For running and testing the Java Application, Java version 1.7.0 is required. 
(CCIS 102 WVH Lab machines do not have this version installed.)

This project classifies the text messages into spam or ham. It uses Naive Bayesian classification and 
Support Vector Machine algorithms for classifying the messages into spam or ham. We have also used
Ngrams with SVM for classification. The dataset can be found in the root folder. It is named "SMSSpamCollection.txt"

There is a GUI developed by the team to use this system(we have a JAR file which you can run to test the code). 
The steps to use it is as follows: (Java Application GUI)

1) Go to the folder : The Smart Miners -> SMS Spam Filter -> SMS_Spam_Filter.jar.
2) Double click on the .jar file. A window showing the project title and the group members will appear.
3) Hit the "Let's START" button on the window. This will redirect you to another panel on that window 
     which has the options of selecting a classifier of your choice.
4) Once you select the classifier, hit the next button. This will take some time before it redirects to 
     another panel. This is because the classifier you selected starts the learning process. Also
     this button creates files for saving the evaluation and results of running these algorithms on test data.
5) Once the learning process is done, it will redirect you to a form where you can give your message
     you want to classify.
6) Hit the "Spam or Ham?" button. This will give you the classification for that message.
7) You can click the "back" button to choose another classifier.

The results of running these algorithms and the evaluation are saved in the following files:

1) Naive Bayesian Algorithm - Bayesian_Output.txt
2) Support Vector Machine - SVMOutput.txt and SVMEvaluation.txt
3) Support Vector Machine with N-grams - SMONgram_Output.txt and NgramEvaluation.txt

These files are created in folder SMS Spam Filter.
==========================================

Android Application GUI:

For the Android UI, there should be android sdk with Eclipse installed. Steps to follow to test the application:

1. Unzip the folder SMSSpam.zip in the folder named Android.
2. To load the existing project do:
     2.1 Launch Eclipse	
     2.2 File > new > project
     2.3 In the wizard, choose android project from existing code
     2.4 Use the browse button to find the project folder. 
     2.5 Click Finish
     2.6 The project should open in project explorer. (May be project name might change to "MainActivity")
3. Then, Go to Windows tab --> Android Virtual Device Manager. If there is no emulator present, create one and start it.
4. Wait for the emulator to show the home screen.
5. Once it does, right click on the Project and Run as Android Application.
6. Wait for about 40 secs and the UI will be up.
7. Type any sentence and check whether message is spam or ham.

==========================================