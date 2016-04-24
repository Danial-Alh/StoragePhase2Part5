package Tree;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.Vector;

public class BTree<Key extends Comparable<? super Key>, Value>{
    static int idCounter = 0;
    protected class Node{

        protected final int HALF_MAX_SIZE, MAX_SIZE;
        protected Vector<Pair<Key, Value>> keyValPair;
        protected Vector<Node> child;
        protected Node parent;
        protected int id;

        public int getSize()
        {
            return keyValPair.size();
        }

        public Node(int halfMaxSize, Node parent) {
            this.HALF_MAX_SIZE = halfMaxSize;
            this.MAX_SIZE = 2*halfMaxSize - 1;
            this.parent = parent;
            this.id = ++idCounter;
            keyValPair = new Vector<Pair<Key, Value>>();
            child = new Vector<Node>();
        }

        public String toString()
        {
            String result = "<<id:" + id + ",ref:" + (parent == null ? -1 : parent.id) + ">>";
            for(Pair<Key, Value> pair: keyValPair)
                result += "**" + pair.getKey().toString();
            result += "**";
            return result;
        }

        protected void insert(Pair<Key, Value> newNode, Node biggerChild, Node smallerChild)
        {
            if(getSize() == 0)
            {
                keyValPair.add(newNode);
                child.add(smallerChild);
                child.add(biggerChild);
            }
            else
            {
                int location = binarySearchForLocationToAdd(newNode.getKey());
                if(location >= keyValPair.size())
                {
                    keyValPair.add(newNode);
                    child.add(biggerChild);
                }
                else
                {
                    keyValPair.insertElementAt(newNode, location);
                    child.insertElementAt(biggerChild, location + 1);
                }
                if( biggerChild != null) biggerChild.parent = this;
                if(smallerChild != null)
                {
                    child.set(location, smallerChild);
                    smallerChild.parent = this;
                }
                if(getSize() > MAX_SIZE)
                    splitCurrentNode();
            }
        }

        private int binarySearchForLocationToAdd(Key key) {
            return binarySearchForLocationToAdd(key, 0, keyValPair.size()-1);
        }

        private int binarySearchForLocationToAdd(Key key, int from, int to) {
            if(from > to)
                return -1;
            int mid = (from+to)/2;
            int compareResult = key.compareTo(keyValPair.elementAt(mid).getKey());
            if(compareResult < 0)
            {
                int returnValue = binarySearchForLocationToAdd(key, from, mid-1);
                return (returnValue == -1 ? from : returnValue);
            }
            else if(compareResult == 0)
                return mid;
            else
            {
                int returnValue = binarySearchForLocationToAdd(key, mid+1, to);
                return (returnValue == -1 ? to+1 : returnValue);
            }
        }

        protected Node[] splitCurrentNode() {
            Node newNode = new Node(this.HALF_MAX_SIZE, this.parent);
            int victim = HALF_MAX_SIZE;
            int offset = victim+1;
            moveDataToSiblingAndParent(newNode, offset, victim);
            return null;
        }

        protected void moveDataToSiblingAndParent(Node newNode, int offset, int victimOffset) {
            Pair<Key, Value> victimPair = keyValPair.elementAt(victimOffset);
            for(int i = offset; i < MAX_SIZE+1; i++)
            {
                if(i == offset) // first node
                {
                    Node smallerChild = child.remove(offset)
                            , biggerChild = child.remove(offset);
                    newNode.insert(keyValPair.remove(offset)
                            , biggerChild, smallerChild);
                }
                else
                    newNode.insert(keyValPair.remove(offset), child.remove(offset), null);
            }
            if(parent == null)
            {
                parent = new Node(HALF_MAX_SIZE, null);
                root = parent;
                newNode.parent = root;
                depth++;
            }
            parent.insert(victimPair, newNode, this);
        }
    }

    Node root;
    int depth;

    public BTree(int halfMaxSize) {
        root = new Node(halfMaxSize, null);
        depth = 1;
    }

    public void insert(Key key, Value value)
    {
        if(root.getSize() == 0)
        {
            root.insert(new Pair<Key, Value>(key, value),
                    null, null);
            return;
        }
        Node newLoc = findLoc(key, root);
        if (newLoc != null) // key alreadyexists
            newLoc.insert(new Pair<Key, Value>(key, value),
                    null, null);
    }

    private Node findLoc(Key key, Node startingNode) {

        if(startingNode == null)
            return null;

        for(int i = 0; i < startingNode.getSize(); i++)
        {
            int result = startingNode.keyValPair.elementAt(i).getKey().compareTo(key);
            /*if(result == 0)
                return null;
            else*/ if( result > 0 )
            {
                Node findResult = findLoc(key, startingNode.child.elementAt(i));
                if(findResult == null)
                    return startingNode;
                return findResult;
            }
        }
        Node findResult = findLoc(key, startingNode.child.lastElement());
        if(findResult == null)
            return startingNode;
        return findResult;
    }

    public Value find(Key key)
    {
        return null;
    }

    public String toString()
    {
        Vector<Node> nodeQ = new Vector<Node>();
        nodeQ.add(root);
        nodeQ.add(null);
        return toString(nodeQ, 1);
    }

    private String toString(Vector<Node> nodeQ, int stringDepth)
    {
        if(nodeQ.size() == 0)
            return "";
        Node currentNode = nodeQ.remove(0);
        if(currentNode == null)
        {
            if(stringDepth < this.depth)
            {
                stringDepth++;
                nodeQ.add(null);
            }
            return "\n" + toString(nodeQ, stringDepth);
        }
        for(Node childNode: currentNode.child)
            if(childNode != null) nodeQ.add(childNode);
        return currentNode.toString() + "\t" + toString(nodeQ, stringDepth);
    }
}
