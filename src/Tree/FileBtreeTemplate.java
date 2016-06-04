package Tree;

import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.Nodes.DataLocations.FileDataLocation;
import Tree.Nodes.FileNode;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Vector;

public abstract class FileBtreeTemplate<Value extends Sizeofable & Parsable>
{
    protected final int KEY_MAX_SIZE, VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected final Class valueClassType;
    protected int depth;
    protected HashMap<Long, FileNode<Value>> nodeCache;

    public FileBtreeTemplate(int keyMaxSize, int valueMaxSize, int halfMaxSize, Class valueClassType)
    {
        nodeCache = new HashMap<>();
        KEY_MAX_SIZE = keyMaxSize;
        VALUE_MAX_SIZE = valueMaxSize;
        this.HALF_MAX_SIZE = halfMaxSize;
        this.valueClassType = valueClassType;
        this.MAX_SIZE = 2 * halfMaxSize - 1;
        depth = 1;

    }

    public abstract Value search(String key);

    public abstract void insert(String key, Value value) throws Exception;

    public abstract void update(String key, Value value);

    protected abstract void createParentIfRequired(FileNode<Value> oldNodeTemplate, FileNode<Value> newNodeTemplate);

    protected abstract void addVictimToParent(FileNode<Value> startingNode, int victim, FileNode<Value> newNodeTemplate);

    protected void insert(FileNode<Value> startingNode, Pair<String, Value> newData, Long biggerChild, Long smallerChild)
    {
//        System.out.println("adding data, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        if (startingNode.getSize() == 0)
        {
            startingNode.getKeyValPair().add(newData);
            startingNode.getChild().add(smallerChild);
            if (smallerChild != null)
            {
                FileNode<Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.setParent(startingNode.getMyPointer());
                smallerChildNodeTemplate.commitChanges();
            }
            startingNode.getChild().add(biggerChild);
            if (biggerChild != null)
            {
                FileNode<Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.setParent(startingNode.getMyPointer());
                biggerChildNodeTemplate.commitChanges();
            }
        } else
        {
            int location = startingNode.binarySearchForLocationToAdd(newData.getKey());
            if (location == startingNode.getKeyValPair().size())
            {
                startingNode.getKeyValPair().add(newData);
                startingNode.getChild().add(biggerChild);
            } else
            {
                startingNode.getKeyValPair().insertElementAt(newData, location);
                startingNode.getChild().insertElementAt(biggerChild, location + 1);
            }
            if (biggerChild != null)
            {
                FileNode<Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.setParent(startingNode.getMyPointer());
                biggerChildNodeTemplate.commitChanges();
            }
            if (smallerChild != null)
            {
                startingNode.getChild().set(location, smallerChild);
                FileNode<Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.setParent(startingNode.getMyPointer());
                smallerChildNodeTemplate.commitChanges();
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
//        System.out.println("data added, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        startingNode.commitChanges();
    }

    public FileNode<Value> getNode(Long obj)
    {
        FileNode<Value> cacheResult = nodeCache.get(obj);
        if (cacheResult == null)
        {
            FileNode<Value> resultNode = new FileNode<>(KEY_MAX_SIZE, VALUE_MAX_SIZE, HALF_MAX_SIZE, null, valueClassType);
            resultNode.fetchNodeFromHard(obj);
            nodeCache.put(obj, resultNode);
            cacheResult = resultNode;
        }
        return cacheResult;
    }


    protected Value returnValue(String key, FileNode<Value> startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.getKeyValPair().elementAt(i1).getValue();
    }

    protected void updateValue(String key, Value value, FileNode<Value> startingNodeTemplate, int i1)
    {
        Pair<String, Value> oldKeyValuePair = startingNodeTemplate.getKeyValPair().elementAt(i1);
        startingNodeTemplate.getKeyValPair().set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
        startingNodeTemplate.commitChanges();
    }

    protected FileNode<Value> createNewMiddleNode(Long parent)
    {
        FileNode<Value> resultNode = new FileNode<Value>(KEY_MAX_SIZE, VALUE_MAX_SIZE, HALF_MAX_SIZE, parent, valueClassType);
        resultNode.fetchNodeFromHard(null);
        nodeCache.put(resultNode.getMyPointer(), resultNode);
        return resultNode;
    }

    protected FileNode<Value> createNewLeafNode(Long parent)
    {
        return createNewMiddleNode(parent);
    }


    protected boolean thisDataExists(String key, FileDataLocation<Value> newLoc)
    {
        if (newLoc.getOffset() == newLoc.getNode().getSize() ||
                key.compareTo(newLoc.getNode().getKeyValPair().elementAt(newLoc.getOffset()).getKey()) != 0)
            return false;
        return true;
    }

    protected FileNode<Value>[] splitCurrentNode(FileNode<Value> startingNode)
    {
        FileNode<Value> newNodeTemplate = createNewLeafNode(startingNode.getParent());
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndCreateParentIfRequired(startingNode, newNodeTemplate, offset);
        addVictimToParent(startingNode, victim, newNodeTemplate);
        return null;
    }

    protected void moveDataToSiblingAndCreateParentIfRequired(FileNode<Value> oldNodeTemplate, FileNode<Value> newNodeTemplate, int offset)
    {
        for (int i = offset; i <= MAX_SIZE; i++)
        {
            if (oldNodeTemplate.getSize() - 1 < offset || oldNodeTemplate.getChild().size() - 1 < offset)
                System.out.println("" +
                        "ohhhh nooo");
            if (i == offset) // first node
            {
                Long smallerChild = oldNodeTemplate.getChild().remove(offset), biggerChild = oldNodeTemplate.getChild().remove(offset);
                insert(newNodeTemplate, oldNodeTemplate.getKeyValPair().remove(offset)
                        , biggerChild, smallerChild);
            } else
                insert(newNodeTemplate, oldNodeTemplate.getKeyValPair().remove(offset), oldNodeTemplate.getChild().remove(offset), null);
        }
        createParentIfRequired(oldNodeTemplate, newNodeTemplate);
    }

    protected FileDataLocation<Value> findLoc(String key, FileNode<Value> startingNodeTemplate)
    {
        if (startingNodeTemplate.getSize() == 0) return new FileDataLocation<Value>(startingNodeTemplate, 0);
        Long nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = startingNodeTemplate.getChild().elementAt(i1);
        else if (startingNodeTemplate.getKeyValPair().elementAt(i1).getKey().compareTo(key) == 0)
            return new FileDataLocation<Value>(startingNodeTemplate, i1);
        else
            nextChild = startingNodeTemplate.getChild().elementAt(i1);
        return (nextChild == null ? new FileDataLocation<Value>(startingNodeTemplate, i1) : findLoc(key, getNode(nextChild)));
    }

//    protected Value search(String key, FileNode <Value> startingNodeTemplate)
//    {
//        if (startingNodeTemplate == null)
//            return null;
//
//        Long nextChild = null;
//        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
//        if (i1 == startingNodeTemplate.getSize())
//            nextChild = startingNodeTemplate.getChild().elementAt(i1);
//        else if (i1 == -1)
//            System.out.println("ohhhh my goood");
//        else if (startingNodeTemplate.getKeyValPair().elementAt(i1).getKey().compareTo(key) == 0)
//            return returnValue(key, startingNodeTemplate, i1);
//        else
//            nextChild = startingNodeTemplate.getChild().elementAt(i1);
//        return search(key, getNode(nextChild));
//    }


//    protected void update(String key, Value value, FileNode <Value> startingNodeTemplate)
//    {
//        FileNode <Value> nextChild;
//        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
//        if (i1 == startingNodeTemplate.getSize())
//            nextChild = startingNodeTemplate.getChild().elementAt(i1);
//        else if (startingNodeTemplate.getKeyValPair().elementAt(i1).getKey().compareTo(key) == 0)
//        {
//            updateValue(key, value, startingNodeTemplate, i1);
//            return;
//        } else
//            nextChild = getNode(startingNodeTemplate.getChild().elementAt(i1));
//        update(key, value, nextChild);
//    }

    protected String toString(Vector<FileNode<Value>> nodeTemplateQ, int stringDepth)
    {
        if (nodeTemplateQ.size() == 0)
            return "";
        FileNode<Value> currentNodeTemplate = nodeTemplateQ.remove(0);
        if (currentNodeTemplate == null)
        {
            if (stringDepth < this.depth)
            {
                stringDepth++;
                nodeTemplateQ.add(null);
            }
            return "\n" + toString(nodeTemplateQ, stringDepth);
        }
        for (int i = 0; i < currentNodeTemplate.getChild().size(); i++)
        {
            Long childNode = currentNodeTemplate.getChild().elementAt(i);
            if (childNode != null) nodeTemplateQ.add(getNode(childNode));
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }
}
