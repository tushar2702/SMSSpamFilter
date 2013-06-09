package SMSSpamFilter;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Tushar,Sankalpa,Kosha
 */
public class SMONgram {

    WriteToFile wrf = new WriteToFile();
    ConverterUtils.DataSource source;
    Instances instances;
    FilteredClassifier fc = new FilteredClassifier();
    ConverterUtils.DataSource test_src;
    Instances instancesTest;
    ConverterUtils.DataSource evaluate_src;
    Instances instancesEvaluate;
    
    public int commonCode(int maxValue) throws Exception {
        /**
         * Read the training dataset and set class index to the last attribute
         */
        source = new ConverterUtils.DataSource("train.arff");
        instances = source.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        /**
         * Read the test dataset and set class index to the last attribute
         */
        test_src = new ConverterUtils.DataSource("test.arff");
        instancesTest = test_src.getDataSet();
        instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

         /**
         * Read the evaluation test dataset that contains actual class
         * labels and set class index to the last attribute
         */
        evaluate_src = new ConverterUtils.DataSource("evaluate.arff");
        instancesEvaluate = evaluate_src.getDataSet();
        instancesEvaluate.setClassIndex(instancesTest.numAttributes() - 1);
        
        /**
         * Converting string message into a word vector and setting the options
         * for the filter
         */
        StringToWordVector filter = new StringToWordVector();
        NGramTokenizer n = new NGramTokenizer();
        n.setNGramMinSize(2);
        n.setNGramMaxSize(maxValue);
        filter.setTokenizer(n);
        filter.setInputFormat(instances);


        /**
         * apply filter and set classifier to SMO
         */
        fc.setFilter(filter);
        fc.setClassifier(new SMO());
        fc.buildClassifier(instances);
        // Print the classifier
        //System.out.println(fc);


        /**
         * Once classifier is trained, test each instance from test dataset and
         * calculate its predicted label
         */
        double pred = 0.0;
        String message = "";
        for (int i = 0; i < instancesTest.numInstances(); i++) {
            if (i == 0) {
                wrf.eraseFile("SMONgram_output.txt");
            }
            message = instancesTest.instance(i).toString();
            pred = fc.classifyInstance(instancesTest.instance(i));
            wrf.writeOutput((message + " ----> " + instancesTest.classAttribute().value((int) pred) + "\n"), "SMONgram_output.txt");
        }



        /**
         * Evaluation of the model
         */
        wrf.eraseFile("NgramEvaluation.txt");

        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(fc, instancesEvaluate);
        wrf.writeOutput(evaluation.toClassDetailsString() + "\n \n", "NgramEvaluation.txt");
        wrf.writeOutput(evaluation.toMatrixString() + "\n \n", "NgramEvaluation.txt");
        wrf.writeOutput(evaluation.toSummaryString() + "\n \n", "NgramEvaluation.txt");
        return 100;
    }

    public String smoNgram(String new_message) throws Exception {

        Instance iExample = new DenseInstance(2);
        iExample.setValue(instancesTest.attribute(0), new_message);
        instancesTest.add(iExample);
        
        double fDistribution = fc.classifyInstance(instancesTest.get(instancesTest.size()-1));
        return instancesTest.classAttribute().value((int) fDistribution);
    }
}
