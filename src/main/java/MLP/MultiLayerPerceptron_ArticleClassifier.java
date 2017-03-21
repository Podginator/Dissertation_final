package MLP;

import CorpusConverter.DatasetCreators.DataSetCreator;
import CorpusConverter.DatasetCreators.DataSetExporter;
import CorpusConverter.FileRepository.Article;
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
import java.util.List;
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

    /**
     * Run the Neural Network, entering the args for the correct dataset.
     * Dataset folders should have a Train and Eval CSV.
     *
     *
     * @param args Name of the FOLDER in the dataset.
     */
    public static void main(String[] args) {

        if (args.length == 1) {
            if (!args[0].isEmpty()) {

                String dataset = args[0];
                MultiLayerPerceptron_ArticleClassifier classifier = new MultiLayerPerceptron_ArticleClassifier(dataset);
                try {
                    classifier.BeginCalculations();
                } catch (Exception e) {
                    System.out.println("Error occured. have you checked the dataset " + dataset + " exists");
                }

                return;
            }
        }

        System.out.println("Usage : args 0 = Dataset name");

    }


}
