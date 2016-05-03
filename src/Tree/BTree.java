package Tree;

import javafx.util.Pair;

import java.util.Vector;

public class BTree<Key extends Comparable<? super Key>, Value>
{

    static int idCounter = 0;
    Node root;
    int depth;

    public BTree(int halfMaxSize)
    {
        root = new Node(halfMaxSize, null);
        depth = 1;
    }


    public void insert(Key key, Value value)
    {
        if (root.getSize() == 0)
        {
            root.insert(new Pair<>(key, value),
                    null, null);
            return;
        }
        Node newLoc = findLoc(key, root);
        if (newLoc != null) // key already exists
            newLoc.insert(new Pair<>(key, value),
                    null, null);
    }

    private Node findLoc(Key key, Node startingNode)
    {
        Node nextChild;
        int i1 = startingNode.binarySearchForLocationToAdd(key);
        if (i1 == startingNode.getSize())
            nextChild = startingNode.child.elementAt(i1);
        else if(startingNode.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return null;
        else
            nextChild = startingNode.child.elementAt(i1);
        return (nextChild == null ? startingNode : findLoc(key, nextChild));
    }

    public Value search(Key key)
    {
        if(root.getSize() == 0)
            return null;
        return search(key, root);
    }

    protected Value search(Key key, Node startingNode)
    {
        if (startingNode == null)
            return null;

        Node nextChild = null;
        int i1 = startingNode.binarySearchForLocationToAdd(key);
        if (i1 == startingNode.getSize())
            nextChild = startingNode.child.elementAt(i1);
        else if(i1 == -1)
            System.out.println("ohhhh my goood");
        else if(startingNode.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return returnValue(key, startingNode, i1);
        else
            nextChild = startingNode.child.elementAt(i1);
        return search(key, nextChild);
    }

    protected Value returnValue(Key key, Node startingNode, int i1)
    {
        return startingNode.keyValPair.elementAt(i1).getValue();
    }

    public void update(Key key, Value value)
    {
        update(key, value, root);
    }

    protected void update(Key key, Value value, Node startingNode)
    {
        Node nextChild;
        int i1 = startingNode.binarySearchForLocationToAdd(key);
        if (i1 == startingNode.getSize())
            nextChild = startingNode.child.elementAt(i1);
        else if(startingNode.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
        {
            updateValue(key, value, startingNode, i1);
            return;
        }
        else
            nextChild = startingNode.child.elementAt(i1);
        update(key, value, nextChild);
    }

    protected void updateValue(Key key, Value value, Node startingNode, int i1)
    {
        Pair<Key, Value> oldKeyValuePair = startingNode.keyValPair.elementAt(i1);
        startingNode.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
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
        if (nodeQ.size() == 0)
            return "";
        Node currentNode = nodeQ.remove(0);
        if (currentNode == null)
        {
            if (stringDepth < this.depth)
            {
                stringDepth++;
                nodeQ.add(null);
            }
            return "\n" + toString(nodeQ, stringDepth);
        }
        for (Node childNode : currentNode.child)
            if (childNode != null) nodeQ.add(childNode);
        return currentNode.toString() + "\t" + toString(nodeQ, stringDepth);
    }

    protected class Node
    {

        protected final int HALF_MAX_SIZE, MAX_SIZE;
        protected Vector<Pair<Key, Value>> keyValPair;
        protected Vector<Node> child;
        protected Node parent;
        protected int id;

        public Node(int halfMaxSize, Node parent)
        {
            this.HALF_MAX_SIZE = halfMaxSize;
            this.MAX_SIZE = 2 * halfMaxSize - 1;
            this.parent = parent;
            this.id = ++idCounter;
            keyValPair = new Vector<>();
            child = new Vector<>();
        }

        public int getSize()
        {
            return keyValPair.size();
        }

        public String toString()
        {
            String result = "<<id:" + id + ",ref:" + (parent == null ? -1 : parent.id) + ">>";
            for (Pair<Key, Value> pair : keyValPair)
                result += "**" + pair.getKey().toString();
            result += "**";
            return result;
        }

        protected void insert(Pair<Key, Value> newNode, Node biggerChild, Node smallerChild)
        {
            if (getSize() == 0)
            {
                keyValPair.add(newNode);
                child.add(smallerChild);
                if(smallerChild != null)
                    smallerChild.parent = this;
                child.add(biggerChild);
                if(biggerChild != null)
                    biggerChild.parent = this;
            } else
            {
                int location = binarySearchForLocationToAdd(newNode.getKey());
                if (location == keyValPair.size())
                {
                    keyValPair.add(newNode);
                    child.add(biggerChild);
                } else
                {
                    keyValPair.insertElementAt(newNode, location);
                    child.insertElementAt(biggerChild, location + 1);
                }
                if (biggerChild != null) biggerChild.parent = this;
                if (smallerChild != null)
                {
                    child.set(location, smallerChild);
                    smallerChild.parent = this;
                }
                if (getSize() > MAX_SIZE)
                    splitCurrentNode();
            }
        }

        private int binarySearchForLocationToAdd(Key key)
        {
            return binarySearchForLocationToAdd(key, 0, getSize() - 1);
        }

        private int binarySearchForLocationToAdd(Key key, int from, int to)
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


        private int binarySearchForExistence(Key key)
        {
            return binarySearchForExistence(key, 0, getSize() - 1);
        }

        private int binarySearchForExistence(Key key, int from, int to)
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

        protected Node[] splitCurrentNode()
        {
            Node newNode = new Node(this.HALF_MAX_SIZE, this.parent);
            int victim = HALF_MAX_SIZE;
            int offset = victim + 1;
            moveDataToSiblingAndParent(newNode, offset);
            parent.insert(keyValPair.remove(victim), newNode, this);
            return null;
        }

        protected void moveDataToSiblingAndParent(Node newNode, int offset)
        {
            for (int i = offset; i <= MAX_SIZE; i++)
            {
                if (getSize() - 1 < offset || child.size() - 1 < offset)
                    System.out.println("" +
                            "ohhhh nooo");
                if (i == offset) // first node
                {
                    Node smallerChild = child.remove(offset), biggerChild = child.remove(offset);
                    newNode.insert(keyValPair.remove(offset)
                            , biggerChild, smallerChild);
                } else
                    newNode.insert(keyValPair.remove(offset), child.remove(offset), null);
            }
            if (parent == null)
            {
                parent = new Node(HALF_MAX_SIZE, null);
                root = parent;
                newNode.parent = root;
                depth++;
            }
        }
    }

}