package KMeans;

import com.google.common.collect.Lists;
import org.datavec.api.util.MathUtils;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * A very basic KMeans implementation accepting multivariate data.
 */
public class KMeans {

    /** The amount of K */
    private int mK;

    /** The Max number of iterations */
    private int mIterations;

    /** The Data to form the KMeans Cluster */
    private List<Vector> mData;

    /** The K Centroids */
    private List<Centroid> mCentroids;

    /** The Number of features in each data */
    private int mNumFeatures;

    /**
     * Constructor
     *
     * @param k          the amount of centroids
     * @param iterations the amount of interations needed to decide
     */
    public KMeans(int k, int iterations, int features, List<Vector> data) {
        mK = k;
        mIterations = iterations;
        mCentroids = new ArrayList<Centroid>();
        mNumFeatures = features;
        mData = data;


        calculateClusters();
    }

    /**
     * Returns how many Ks
     * @return the Ks.
     */
    public int getK() {
        return mK;
    }

    /**
     * Perform the calculations, involving iterations and
     * setting initial centroids
     */
    private void calculateClusters() {
        // Initialize vectors to be random initially.
        setInitialCentroids();

        // Then continue
        for (int i = 0; i < mIterations; i++) {
            // Clear the centroids
            mCentroids.forEach(e -> {
                e.clearVectors();
            });

            // Return the previous centroids
            List<Vector> prevCenters = mCentroids.stream()
                    .map(e -> e.getCenter())
                    .collect(Collectors.toList());

            assignCentroids();
            recalculateCentroids();

            double distance = 0.0;
            for (int j = 0; j < mCentroids.size(); j++) {
                distance += distance(prevCenters.get(j), mCentroids.get(j).getCenter());
            }


            // If The centroids haven't moved, we are done.
            if (distance == 0.0) {
                break;
            }
        }
    }

    /**
     * Assign the data to the nearest centroid.
     */
    public void assignCentroids() {
        double distance = 0.0;

        for (Vector data : mData) {
            double min = Double.MAX_VALUE;
            int stored = 0;


            for (int i = 0; i < mK; i++) {
                Centroid c = mCentroids.get(i);

                // Calculate Distance
                distance = distance(data, c.getCenter());

                if (distance < min) {
                    min = distance;
                    stored = i;
                }
            }
            mCentroids.get(stored).addVector(data);
        }
    }


    /**
     * Recalculate the position fo the centroids.
     */
    public void recalculateCentroids() {
        for (Centroid centroid : mCentroids) {
            Vector vecSum = BasicVector.zero(mNumFeatures);

            for (Vector data : centroid.getData()) {
                vecSum = vecSum.add(data);
            }

            // Reset the new list.
            int dataSize = centroid.getData().size();
            if (dataSize > 0) {
                vecSum = vecSum.divide(dataSize);
                centroid.setCenter(vecSum);
            }
        }
    }

    /**
     * Return the Cluster the passed in vector is closest to
     *
     * @param vect the vector to return
     * @return the Cluster it exists in
     */
    public int getCluster(Vector vect) {
        // Returns the cluster of the passed in item.
        double distance = 0.0;
        double min = Double.MAX_VALUE;
        int res = 0;
        for (int i = 0; i < mCentroids.size(); i++) {
            Centroid c = mCentroids.get(i);

            // Calculate Distance
            distance = distance(vect, c.getCenter());

            if (distance < min) {
                min = distance;
                res = i;
            }
        }

        return res;
    }

    /**
     * Return some random using the k-means++ method
     *
     * Step 1) Choose one of your data points at random as an initial centroid.
     * Step 2) Calculate D(x)D(x), the distance between your initial centroid and all other data points, xx.
     * Step 3) Choose your next centroid from the remaining datapoints with probability proportional to D(x)D(x)
     * Step 4) Repeat until all centroids have been assigned.
     */
    private void setInitialCentroids() {
        // Step 1) Choose one of your data points at random as an initial centroid.

        // Choose a random data point
        int index = ThreadLocalRandom.current().nextInt(0, mData.size() - 1);
        Vector initialCluster = mData.get(index);
        mCentroids.add(0, new Centroid(initialCluster));

        Random random = new Random();
        // Step 2) Calculate D(x)D(x), the distance between your initial centroid and all other data points, xx.
        for (int j = 1; j < mK; j++) {
            // For each data point x, compute D(x)
            double[] dxdx = distanceToXSquared();

            // Add one new data point as a center.
            Vector vec;
            double r = random.nextDouble() * dxdx[dxdx.length - 1];
            for (int i = 1; i < dxdx.length; i++) {
                // Step 3) Choose your next centroid from the remaining datapoints with probability proportional to D(x)2
                if (dxdx[i] >= r) {
                    vec = mData.get(i);
                    mCentroids.add(new Centroid(vec));
                    break;
                }
            }
        }
    }

    /**
     * Calculate the distance between two vectors.
     *
     * @param a
     * @param b
     * @return the distance between two vectors
     */
    private double distance(Vector a, Vector b) {
        return (a.subtract(b)).norm();
    }


    /**
     * Calculate distance between the centroids and the other vectors.
     * D(x)
     * @param x
     * @return
     */
    private double[] distanceToXSquared() {
        //calculate how many centroids
        int nCentroids = mCentroids.size();
        double[] res = new double[mData.size()];

        List<Vector> centroidVecs = mCentroids.stream().map(e -> e.getCenter()).collect(Collectors.toList());
        List<Vector> dataSansCentroids = mData.stream()
                                            .filter( e -> { return !centroidVecs.contains(e); })
                                            .collect(Collectors.toList());

        /**
         * Iterate over each index
         */
        double distance = 0.0;
        int nearestCentroidIndex;
        Vector nearestCentroid;
        for (int i = 0; i < dataSansCentroids.size(); i++) {
            Vector vec = dataSansCentroids.get(i);
            nearestCentroidIndex = getCluster(vec);
            nearestCentroid = mCentroids.get(nearestCentroidIndex).getCenter();

            distance += Math.pow(
                    MathUtils.euclideanDistance(
                        Lists.newArrayList(vec).stream().mapToDouble(Double::doubleValue).toArray(),
                        Lists.newArrayList(nearestCentroid).stream().mapToDouble(Double::doubleValue).toArray()),
                     2);
            res[i] = distance;
        }


        return res;


    }
}
