package MLP;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by podginator on 22/01/2017.
 */
public class MultiLayerPerceptron_ArticleClassifier {


    /** Learning Rate */
    private double mLearningRate = 0.05;

    /** batch Size */
    private int mBatchSize = 150;

    /** Epoch Size */
    private int mEpochSize = 150;

    private String mName;

    private MultiLayerNetwork DataSetIterator;

    private Evaluation mEval = new Evaluation(2);

    private MultiLayerNetwork mModel;


    private RecordReader mTrainData;

    private RecordReader mEvalData;

    /**
     *
     * @param name The name of the dataset.
     */
    public MultiLayerPerceptron_ArticleClassifier(String name) {
        mName = name;

        try {
            mTrainData = new CSVRecordReader();
            mEvalData = new CSVRecordReader();
            mTrainData.initialize(new FileSplit(new File(System.getProperty("user.dir") + "/dataset/" + mName + "/train.csv")));
            mEvalData.initialize(new FileSplit(new File(System.getProperty("user.dir") + "/dataset/" + mName + "/eval.csv")));

        } catch (InterruptedException | IOException e) {
            throw new IllegalArgumentException("No Such Test Data");
        }

    }

    private void initModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT)
                .learningRate(mLearningRate)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(12).nOut(10)
                        .weightInit(WeightInit.XAVIER)
                        .activation("relu")
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation("softmax").weightInit(WeightInit.XAVIER)
                        .nIn(10).nOut(2).build())
                .pretrain(false).backprop(true).build();


        mModel = new MultiLayerNetwork(conf);
        mModel.init();

    }

    public void BeginCalculations() {
        DataSetIterator trainIter = new RecordReaderDataSetIterator(mTrainData, mBatchSize, 0, 2);
        DataSetIterator testIter = new RecordReaderDataSetIterator(mEvalData, mBatchSize, 0, 2);

        initModel();
        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains
        mModel.setListeners(new StatsListener(statsStorage));


        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
                .epochTerminationConditions(new MaxEpochsTerminationCondition(700))
                .epochTerminationConditions(new ScoreImprovementEpochTerminationCondition(20, 0.01))
                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(20, TimeUnit.MINUTES))
                .scoreCalculator(new DataSetLossCalculator(trainIter, true))
                .evaluateEveryNEpochs(1)
                .modelSaver(new LocalFileModelSaver(System.getProperty("user.dir") + "/dataset/" + mName + "/"))
                .build();

        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, mModel, trainIter);

        //Conduct early stopping training:
        EarlyStoppingResult result = trainer.fit();

        System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Score at best epoch: " + result.getBestModelScore());



//        for (int n = 0; n < mEpochSize; n++) {
//            mModel.fit(trainIter);
//        }

        System.out.println("Evaluating model...");
        while (testIter.hasNext()) {
            DataSet t = testIter.next();
            INDArray features = t.getFeatureMatrix();
            INDArray lables = t.getLabels();
            INDArray predicted = mModel.output(features, false);

            mEval.eval(lables, predicted);

        }

        //Print the evaluation statistics
        System.out.println(mEval.stats());

    }

    private boolean initializeRecords() {
        try {
            mTrainData = new CSVRecordReader();
            mEvalData = new CSVRecordReader();
            mTrainData.initialize(new FileSplit(new File(System.getProperty("user.dir") + "/dataset/" + mName + "/train.csv")));
            mEvalData.initialize(new FileSplit(new File(System.getProperty("user.dir") + "/dataset/" + mName + "/eval.csv")));

        } catch (InterruptedException | IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Show the charted data.
     */
    public void showChart() {
        //Training is complete. Code that follows is for plotting the data & predictions only

        //Plot the data:
        double xMin = 0;
        double xMax = 1.0;
        double yMin = -0.2;
        double yMax = 0.8;

        //Let's evaluate the predictions at every point in the x/y input space
        int nPointsPerAxis = 100;
        double[][] evalPoints = new double[nPointsPerAxis * nPointsPerAxis][2];
        int count = 0;
        for (int i = 0; i < nPointsPerAxis; i++) {
            for (int j = 0; j < nPointsPerAxis; j++) {
                double x = i * (xMax - xMin) / (nPointsPerAxis - 1) + xMin;
                double y = j * (yMax - yMin) / (nPointsPerAxis - 1) + yMin;

                evalPoints[count][0] = x;
                evalPoints[count][1] = y;

                count++;
            }
        }

        INDArray allXYPoints = Nd4j.create(evalPoints);
        INDArray predictionsAtXYPoints = mModel.output(allXYPoints);

        //Get all of the training data in a single array, and plot it:
        initializeRecords();
        int nTrainPoints = 1000;
        DataSetIterator trainIter = new RecordReaderDataSetIterator(mTrainData, nTrainPoints, 0, 2);
        DataSet ds = trainIter.next();
        //PlotUtil.plotTrainingData(ds.getFeatures(), ds.getLabels(), allXYPoints, predictionsAtXYPoints, nPointsPerAxis);

        int nTestPoints = 500;
        DataSetIterator testIter = new RecordReaderDataSetIterator(mEvalData, nTestPoints, 0, 2);
        ds = testIter.next();
        INDArray testPredicted = mModel.output(ds.getFeatures());
        //PlotUtil.plotTestData(ds.getFeatures(), ds.getLabels(), testPredicted, allXYPoints, predictionsAtXYPoints, nPointsPerAxis);
    }
}
