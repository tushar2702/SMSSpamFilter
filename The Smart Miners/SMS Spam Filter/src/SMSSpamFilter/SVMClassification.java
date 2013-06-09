package SMSSpamFilter;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.*;

/**
 *
 * @author Tushar, Kosha, Sankalpa
 */
public class SVMClassification {

    WriteToFile wrf = new WriteToFile();
    FilteredClassifier fc = new FilteredClassifier();
    DataSource source;
    Instances instances;
    DataSource test_src;
    Instances instancesTest;
    DataSource evaluate_src;
    Instances instancesEvaluate;

    public int commonCode() throws Exception {

        /**
         * Read the training dataset and set class index to the last attribute
         */
        source = new DataSource("train.arff");
        instances = source.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        /**
         * Converting string message into a word vector and setting the options
         * for the filter
         */
        StringToWordVector filter = new StringToWordVector();
        String[] options = new String[2];
        options[0] = "weka.classifiers.functions.supportVector.StringKernel";
        options[1] = "U";
        filter.setOptions(options);
        filter.setInputFormat(instances);

        /**
         * apply filter and set classifier to SMO
         */        
        fc.setFilter(filter);
        fc.setClassifier(new SMO());
        fc.buildClassifier(instances);

        /**
         * Read the test dataset and set class index to the last attribute
         */
        test_src = new DataSource("test.arff");
        instancesTest = test_src.getDataSet();
        instancesTest.setClassIndex(instancesTest.numAttributes() - 1);
        
        /**
         * Read the evaluation test dataset that contains actual class
         * labels and set class index to the last attribute
         */
        evaluate_src = new DataSource("evaluate.arff");
        instancesEvaluate = evaluate_src.getDataSet();
        instancesEvaluate.setClassIndex(instancesTest.numAttributes() - 1);

        /**
         * Once classifier is trained, test each instance from test dataset and
         * calculate its predicted label
         */
        for (int i = 0; i < instancesTest.numInstances(); i++) {
            if (i == 0) {
                wrf.eraseFile("SVMOutput.txt");
            }
            String message = instancesTest.instance(i).toString();
            double pred = fc.classifyInstance(instancesTest.instance(i));
            wrf.writeOutput((message + " ----> " + instancesTest.classAttribute().value((int) pred) + "\n"), "SVMOutput.txt");
        }

        /**
         * Evaluation of the model
         */
        wrf.eraseFile("SVMEvaluation.txt");

        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(fc, instancesEvaluate);
        wrf.writeOutput(evaluation.toClassDetailsString() + "\n \n", "SVMEvaluation.txt");
        wrf.writeOutput(evaluation.toMatrixString() + "\n \n", "SVMEvaluation.txt");
        wrf.writeOutput(evaluation.toSummaryString() + "\n \n", "SVMEvaluation.txt");

        return 100;
    }

    public String svm(String message_new) throws Exception {
        Instance iExample = new DenseInstance(2);
        iExample.setValue(instancesTest.attribute(0), message_new);
        instancesTest.add(iExample);
        
        double fDistribution = fc.classifyInstance(instancesTest.lastInstance());
        return instances.classAttribute().value((int) fDistribution);
    }
}
