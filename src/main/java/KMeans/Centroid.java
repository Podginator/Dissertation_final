package KMeans;

import org.la4j.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * A Centroid is defined by a variable that defines the center, and a list of points around it.
 */
public class Centroid {

    private Vector mCenter;

    private List<Vector> mData;

    public Centroid(Vector center) {
        mCenter = center;
        mData = new ArrayList<>();
    }

    /**
     * Get the center point of the centroid
     * @return
     */
    public Vector getCenter() {
        return mCenter;
    }

    public void setCenter(Vector vec) {
        mCenter = vec;
    }

    /**
     * Add A vector.
     * @param vec The Vector to add
     */
    public void addVector(Vector vec) {
        mData.add(vec);
    }

    /**
     * Get the Data
     * @return The Data
     */
    public List<Vector> getData() {
        return mData;
    }

    // Clear the centroid of data
    public void clearVectors() {
        mData.clear();
    }


    /**
     * Override the equals so that if we're comparing a vector we are actually asking
     * Does this vector equal the center point.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Vector) {
            return mCenter.equals(obj);
        } else if (obj instanceof Centroid) {
            return ((Centroid)obj).getCenter().equals(this.getCenter());
        }

        return super.equals(obj);
    }
}
