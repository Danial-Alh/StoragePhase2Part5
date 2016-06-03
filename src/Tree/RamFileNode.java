package Tree;

import javafx.util.Pair;

import java.util.Vector;

/**
 * Created by danial on 6/2/16.
 */
public class RamFileNode <Value extends Sizeofable & Parsable>
{
    boolean childAreOnFile;
    //        protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected Vector<Pair<String, Value>> keyValPair;
    protected Vector<RamFileNode> child;
    protected Vector<Long> fileChild;
    protected RamFileNode parent;
    protected int id;

    public RamFileNode(int halfMaxSize, RamFileNode parent)
    {
        childAreOnFile = false;
//            this.HALF_MAX_SIZE = halfMaxSize;
//            this.MAX_SIZE = 2 * halfMaxSize - 1;
        this.parent = parent;
        this.id = /*++idCounter*/0;
        keyValPair = new Vector<>();
        child = new Vector<>();
    }

    public int getSize()
    {
        return keyValPair.size();
    }


    public String toString()
    {
//        Node parentNodeTemplate = getNode(parent);
//        String result = "<<id:" + id + ",ref:" + (parentNodeTemplate == null ? -1 : parentNodeTemplate.id) + ">>";
        String result = "";
        for (Pair<String, Value> pair : keyValPair)
            result += "**" + pair.getKey();
        result += "**";
        return result;
    }

    protected int binarySearchForLocationToAdd(String key)
    {

        return getSize() == 0 ? 0 :
                binarySearchForLocationToAdd(key, 0, getSize() - 1);
    }

    protected int binarySearchForLocationToAdd(String key, int from, int to)
    {
        if (from > to)
            return -1;
        int mid = (from + to) / 2;
        int compareResult = key.compareTo(keyValPair.elementAt(mid).getKey());
        if (compareResult < 0)
        {
            int returnValue = binarySearchForLocationToAdd(key, from, mid - 1);
            return (returnValue == -1 ? from : returnValue);
        } else if (compareResult == 0)
            return mid;
        else
        {
            int returnValue = binarySearchForLocationToAdd(key, mid + 1, to);
            return (returnValue == -1 ? to + 1 : returnValue);
        }
    }


    protected int binarySearchForExistence(String key)
    {
        return binarySearchForExistence(key, 0, getSize() - 1);
    }


    protected int binarySearchForExistence(String key, int from, int to)
    {
        if (from > to)
            return -1;
        int mid = (from + to) / 2;
        int compareResult = key.compareTo(keyValPair.elementAt(mid).getKey());
        if (compareResult == 0)
            return mid;
        if (compareResult < 0)
            return binarySearchForExistence(key, from, mid - 1);
        else
            return binarySearchForExistence(key, mid + 1, to);
    }

    protected RamFileNode getMyPointer()
    {
        return this;
    }
}
