package Tree;

import javafx.util.Pair;

import java.util.Vector;

public class RamFileBtree<Value extends Sizeofable & Parsable>
{
    protected final int KEY_MAX_SIZE;
    protected final int VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected final Class valueClassType;
    protected int depth;
    protected Node root;
    ExtendedFileBtree<Value> extendedFileBtree;

    public RamFileBtree(int keyMaxSize, int valueMaxSize, int halfMaxSize, Class valueClassType)
    {
        KEY_MAX_SIZE = keyMaxSize;
        VALUE_MAX_SIZE = valueMaxSize;
        this.HALF_MAX_SIZE = halfMaxSize;
        this.valueClassType = valueClassType;
        this.MAX_SIZE = 2 * halfMaxSize - 1;
        depth = 1;

        extendedFileBtree = new ExtendedFileBtree<>(1, 1, 1, valueClassType, this);

    }

    protected Node getRootNode()
    {
        return root;
    }

    private Node getNode(Node node)
    {
        return node;
    }

    public void insert(String key, Value value) throws Exception
    {
        if(key.getBytes().length > KEY_MAX_SIZE || value.sizeof() > VALUE_MAX_SIZE)
            throw new Exception("length exceeded");
        Node rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
        {
            insert(rootNodeTemplate, new Pair<>(key, value),
                    null, null);
            return;
        }
        DataLocation newLoc = findLoc(key, rootNodeTemplate);
        if (!thisDataExists(key, newLoc)) // if key not exists
            insert(newLoc.node, new Pair<>(key, value),
                    null, null);
    }

    
    protected void createParentIfRequired(Node oldNodeTemplate, Node newNodeTemplate)
    {
        if (oldNodeTemplate.parent == null)
        {
            Node parentNodeTemplate = createNewMiddleNode(null);
            oldNodeTemplate.parent = parentNodeTemplate.getMyPointer();
            root = parentNodeTemplate.getMyPointer();
//            newNode.parent = root;
            depth++;
        }
    }

    
    protected void addVictimToParent(Node startingNode, int victim, Node newNodeTemplate)
    {
        Node parentNodeTemplate = getNode(startingNode.parent);
        insert(parentNodeTemplate, startingNode.keyValPair.remove(victim), newNodeTemplate.getMyPointer(),
                startingNode.getMyPointer());
    }

    public Value search(String key)
    {
        Node rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return null;
        DataLocation loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return null;
        return loc.node.keyValPair.elementAt(loc.offset).getValue();
//        return search(key, rootNodeTemplate);
    }

    public void update(String key, Value value)
    {
        Node rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return;
        DataLocation loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return;
        updateValue(key, value, loc.node, loc.offset);
    }

    public String toString()
    {
        Vector<Node> nodeTemplateQ = new Vector<>();
        nodeTemplateQ.add(getRootNode());
        nodeTemplateQ.add(null);
        return toString(nodeTemplateQ, 1);
    }

    protected void insert(Node startingNode, Pair<String, Value> newData, Node biggerChild, Node smallerChild)
    {
//        System.out.println("adding data, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        if (startingNode.getSize() == 0)
        {
            startingNode.keyValPair.add(newData);
            startingNode.child.add(smallerChild);
            if (smallerChild != null)
            {
                Node smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            startingNode.child.add(biggerChild);
            if (biggerChild != null)
            {
                Node biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
        } else
        {
            int location = startingNode.binarySearchForLocationToAdd(newData.getKey());
            if (location == startingNode.keyValPair.size())
            {
                startingNode.keyValPair.add(newData);
                startingNode.child.add(biggerChild);
            } else
            {
                startingNode.keyValPair.insertElementAt(newData, location);
                startingNode.child.insertElementAt(biggerChild, location + 1);
            }
            if (biggerChild != null)
            {
                Node biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (smallerChild != null)
            {
                startingNode.child.set(location, smallerChild);
                Node smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
//        System.out.println("data added, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
    }

    public void insertFromFileNodes(Node node, Pair<String, Value> newData, Long smallerChildPointer, Long biggerChildPointer)
    {

        int location = node.binarySearchForLocationToAdd(newData.getKey());
        if (location == node.keyValPair.size())
        {
            node.keyValPair.add(newData);
            node.fileChild.add(biggerChildPointer);
        } else
        {
            node.keyValPair.insertElementAt(newData, location);
            node.fileChild.insertElementAt(biggerChildPointer, location + 1);
        }
        if (biggerChildPointer != null)
        {
            extendedFileBtree.updateParent(biggerChildPointer, node);
        }
        if (smallerChildPointer != null)
        {
            node.fileChild.set(location, smallerChildPointer);
            extendedFileBtree.updateParent(smallerChildPointer, node);
        }
        if (node.getSize() > MAX_SIZE)
            splitCurrentNode(node);
    }

    protected Value returnValue(String key, Node startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.keyValPair.elementAt(i1).getValue();
    }

    protected void updateValue(String key, Value value, Node startingNodeTemplate, int i1)
    {
        Pair<String, Value> oldKeyValuePair = startingNodeTemplate.keyValPair.elementAt(i1);
        startingNodeTemplate.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
    }

    protected Node createNewMiddleNode(Node parent)
    {
        Node resultNode = new Node(HALF_MAX_SIZE, parent);
        return resultNode;
    }

    protected Node createNewLeafNode(Node parent)
    {
        return createNewMiddleNode(parent);
    }


    protected boolean thisDataExists(String key, DataLocation newLoc)
    {
        if(newLoc.offset == newLoc.node.getSize() || key.compareTo(newLoc.node.keyValPair.elementAt(newLoc.offset).getKey()) != 0)
            return false;
        return true;
    }

    protected Node[] splitCurrentNode(Node startingNode)
    {
        Node newNodeTemplate = createNewLeafNode(startingNode.parent);
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndCreateParentIfRequired(startingNode, newNodeTemplate, offset);
        addVictimToParent(startingNode, victim, newNodeTemplate);
        return null;
    }

    protected void moveDataToSiblingAndCreateParentIfRequired(Node oldNodeTemplate, Node newNodeTemplate, int offset)
    {
        for (int i = offset; i <= MAX_SIZE; i++)
        {
            if (oldNodeTemplate.getSize() - 1 < offset || oldNodeTemplate.child.size() - 1 < offset)
                System.out.println("" +
                        "ohhhh nooo");
            if (i == offset) // first node
            {
                Node smallerChild = oldNodeTemplate.child.remove(offset), biggerChild = oldNodeTemplate.child.remove(offset);
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset)
                        , biggerChild, smallerChild);
            } else
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset), oldNodeTemplate.child.remove(offset), null);
        }
        createParentIfRequired(oldNodeTemplate, newNodeTemplate);
    }

    protected DataLocation findLoc(String key, Node startingNodeTemplate)
    {
        if(startingNodeTemplate.getSize() == 0) return new DataLocation(startingNodeTemplate, 0);
        Node nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = startingNodeTemplate.child.elementAt(i1);
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return new DataLocation(startingNodeTemplate, i1);
        else
            nextChild = startingNodeTemplate.child.elementAt(i1);
        return (nextChild == null ? new DataLocation(startingNodeTemplate, i1) : findLoc(key, getNode(nextChild)));
    }

    protected String toString(Vector<Node> nodeTemplateQ, int stringDepth)
    {
        if (nodeTemplateQ.size() == 0)
            return "";
        Node currentNodeTemplate = nodeTemplateQ.remove(0);
        if (currentNodeTemplate == null)
        {
            if (stringDepth < this.depth)
            {
                stringDepth++;
                nodeTemplateQ.add(null);
            }
            return "\n" + toString(nodeTemplateQ, stringDepth);
        }
        for (int i = 0; i < currentNodeTemplate.child.size(); i++)
        {
            Node childNode = currentNodeTemplate.child.elementAt(i);
            if (childNode != null) nodeTemplateQ.add(getNode(childNode));
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }

    class DataLocation
    {
        Node node;
        int offset;

        public DataLocation(Node node, int offset)
        {
            this.node = node;
            this.offset = offset;
        }
    }

    protected class Node
    {
        boolean childAreOnFile;
        protected final int HALF_MAX_SIZE, MAX_SIZE;
        protected Vector<Pair<String, Value>> keyValPair;
        protected Vector<Node> child;
        protected Vector<Long> fileChild;
        protected Node parent;
        protected int id;

        public Node(int halfMaxSize, Node parent)
        {
            childAreOnFile = false;
            this.HALF_MAX_SIZE = halfMaxSize;
            this.MAX_SIZE = 2 * halfMaxSize - 1;
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

        protected Node getMyPointer()
        {
            return this;
        }
    }

}