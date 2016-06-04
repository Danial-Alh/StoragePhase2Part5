package Tree.Nodes;

import Primitives.Parsable;
import Primitives.Sizeofable;
import javafx.util.Pair;

import java.util.Vector;

/**
 * Created by danial on 6/2/16.
 */
public class RamFileNode <Value extends Sizeofable & Parsable>
{
    boolean childAreOnFile;

    public boolean isChildAreOnFile()
    {
        return childAreOnFile;
    }

    public void setChildAreOnFile(boolean childAreOnFile)
    {
        this.childAreOnFile = childAreOnFile;
    }

    public Vector<Pair<String, Value>> getKeyValPair()
    {
        return keyValPair;
    }

    public void setKeyValPair(Vector<Pair<String, Value>> keyValPair)
    {
        this.keyValPair = keyValPair;
    }

    public Vector<RamFileNode> getChild()
    {
        return child;
    }

    public void setChild(Vector<RamFileNode> child)
    {
        this.child = child;
    }

    public Vector<Long> getFileChild()
    {
        return fileChild;
    }

    public void setFileChild(Vector<Long> fileChild)
    {
        this.fileChild = fileChild;
    }

    public RamFileNode getParent()
    {
        return parent;
    }

    public void setParent(RamFileNode parent)
    {
        this.parent = parent;
    }

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
        fileChild = new Vector<>();
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

    public int binarySearchForLocationToAdd(String key)
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

    public RamFileNode getMyPointer()
    {
        return this;
    }
}
