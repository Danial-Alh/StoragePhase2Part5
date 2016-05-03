package Tree;

import javafx.util.Pair;

import java.util.Vector;

public abstract class BtreeTemplate<Key extends Comparable<? super Key>, Value, PointerType>
{
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    PointerType root;
    int depth;

    protected abstract NodeTemplate getNode(PointerType obj);

    protected abstract NodeTemplate getRootNode();

    protected abstract Value returnValue(Key key, NodeTemplate startingNodeTemplate, int i1);

    protected abstract void updateValue(Key key, Value value, NodeTemplate startingNodeTemplate, int i1);

    protected abstract NodeTemplate createNewMiddleNode(PointerType parent);

    protected abstract NodeTemplate createNewLeafNode(PointerType parent);

    public BtreeTemplate(int halfMaxSize)
    {
        this.HALF_MAX_SIZE = halfMaxSize;
        this.MAX_SIZE = 2 * halfMaxSize - 1;
        depth = 1;
    }

    public void insert(Key key, Value value)
    {
        NodeTemplate rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
        {
            insert(rootNodeTemplate, new Pair<>(key, value),
                    null, null);
            return;
        }
        NodeTemplate newLoc = findLoc(key, rootNodeTemplate);
        if (newLoc != null) // key already exists
            insert(newLoc, new Pair<>(key, value),
                    null, null);
    }

    protected void insert(NodeTemplate startingNode, Pair<Key, Value> newData, PointerType biggerChild, PointerType smallerChild)
    {
        if (startingNode.getSize() == 0)
        {
            startingNode.keyValPair.add(newData);
            startingNode.child.add(smallerChild);
            if (smallerChild != null)
            {
                NodeTemplate smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            startingNode.child.add(biggerChild);
            if (biggerChild != null)
            {
                NodeTemplate biggerChildNodeTemplate = getNode(biggerChild);
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
                NodeTemplate biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (smallerChild != null)
            {
                startingNode.child.set(location, smallerChild);
                NodeTemplate smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
    }

    protected NodeTemplate[] splitCurrentNode(NodeTemplate startingNode)
    {
        NodeTemplate newNodeTemplate = createNewLeafNode(startingNode.parent);
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndParent(startingNode, newNodeTemplate, offset);
        NodeTemplate parentNodeTemplate = getNode(startingNode.parent);
        insert(parentNodeTemplate, startingNode.keyValPair.remove(victim), newNodeTemplate.getMyPointer(), startingNode.getMyPointer());
        return null;
    }

    protected void moveDataToSiblingAndParent(NodeTemplate oldNodeTemplate, NodeTemplate newNodeTemplate, int offset)
    {
        for (int i = offset; i <= MAX_SIZE; i++)
        {
            if (oldNodeTemplate.getSize() - 1 < offset || oldNodeTemplate.child.size() - 1 < offset)
                System.out.println("" +
                        "ohhhh nooo");
            if (i == offset) // first node
            {
                PointerType smallerChild = oldNodeTemplate.child.remove(offset), biggerChild = oldNodeTemplate.child.remove(offset);
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset)
                        , biggerChild, smallerChild);
            } else
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset), oldNodeTemplate.child.remove(offset), null);
        }
        createParentIfRequired(oldNodeTemplate, oldNodeTemplate.parent);
    }

    private void createParentIfRequired(NodeTemplate oldNodeTemplate, PointerType parent)
    {
        if (getNode(parent) == null)
        {
            NodeTemplate parentNodeTemplate = createNewMiddleNode(null);
            oldNodeTemplate.parent = parentNodeTemplate.getMyPointer();
            root = parentNodeTemplate.getMyPointer();
//                newNode.parent = root;
            depth++;
        }
    }

    private NodeTemplate findLoc(Key key, NodeTemplate startingNodeTemplate)
    {
        NodeTemplate nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return null;
        else
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        return (nextChild == null ? startingNodeTemplate : findLoc(key, nextChild));
    }

    public Value search(Key key)
    {
        NodeTemplate rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return null;
        return search(key, rootNodeTemplate);
    }

    protected Value search(Key key, NodeTemplate startingNodeTemplate)
    {
        if (startingNodeTemplate == null)
            return null;

        NodeTemplate nextChild = null;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        else if (i1 == -1)
            System.out.println("ohhhh my goood");
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return returnValue(key, startingNodeTemplate, i1);
        else
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        return search(key, nextChild);
    }

    public void update(Key key, Value value)
    {
        update(key, value, getRootNode());
    }

    protected void update(Key key, Value value, NodeTemplate startingNodeTemplate)
    {
        NodeTemplate nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
        {
            updateValue(key, value, startingNodeTemplate, i1);
            return;
        } else
            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
        update(key, value, nextChild);
    }

    public String toString()
    {
        Vector<NodeTemplate> nodeTemplateQ = new Vector<>();
        nodeTemplateQ.add(getRootNode());
        nodeTemplateQ.add(null);
        return toString(nodeTemplateQ, 1);
    }

    private String toString(Vector<NodeTemplate> nodeTemplateQ, int stringDepth)
    {
        if (nodeTemplateQ.size() == 0)
            return "";
        NodeTemplate currentNodeTemplate = nodeTemplateQ.remove(0);
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
            NodeTemplate childNodeTemplate = getNode(currentNodeTemplate.child.elementAt(i));
            if (childNodeTemplate != null) nodeTemplateQ.add(childNodeTemplate);
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }




















    public abstract class NodeTemplate
    {
        protected Vector<Pair<Key, Value>> keyValPair;
        protected Vector<PointerType> child;
        protected PointerType parent;
        protected int id;

        public NodeTemplate(int halfMaxSize, PointerType parent)
        {
            this.parent = parent;
//        this.id = ++idCounter;
            this.id = 0;
            keyValPair = new Vector<>();
            child = new Vector<>();
        }

        protected abstract PointerType getMyPointer();


        public int getSize()
        {
            return keyValPair.size();
        }


        public String toString()
        {
//        NodeTemplate parentNodeTemplate = getNode(parent);
//        String result = "<<id:" + id + ",ref:" + (parentNodeTemplate == null ? -1 : parentNodeTemplate.id) + ">>";
            String result = "";
            for (Pair<Key, Value> pair : keyValPair)
                result += "**" + pair.getKey().toString();
            result += "**";
            return result;
        }

        protected int binarySearchForLocationToAdd(Key key)
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


        protected int binarySearchForExistence(Key key)
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
    }

}