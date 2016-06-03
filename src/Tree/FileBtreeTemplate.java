package Tree;

import FileManagement.RandomAccessFileManagement;
import javafx.util.Pair;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Vector;

public abstract class FileBtreeTemplate<Value extends Sizeofable & Parsable>
{
    protected final int KEY_MAX_SIZE, VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected final Class valueClassType;
    protected int depth;
    protected HashMap<Long, FileNode <Value>> nodeCache;

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
    protected abstract void createParentIfRequired(FileNode <Value> oldNodeTemplate, FileNode <Value> newNodeTemplate);
    protected abstract void addVictimToParent(FileNode <Value> startingNode, int victim, FileNode <Value> newNodeTemplate);

    protected void insert(FileNode <Value> startingNode, Pair<String, Value> newData, Long biggerChild, Long smallerChild)
    {
//        System.out.println("adding data, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        if (startingNode.getSize() == 0)
        {
            startingNode.keyValPair.add(newData);
            startingNode.child.add(smallerChild);
            if (smallerChild != null)
            {
                FileNode <Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
                smallerChildNodeTemplate.commitChanges();
            }
            startingNode.child.add(biggerChild);
            if (biggerChild != null)
            {
                FileNode <Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
                biggerChildNodeTemplate.commitChanges();
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
                FileNode <Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
                biggerChildNodeTemplate.commitChanges();
            }
            if (smallerChild != null)
            {
                startingNode.child.set(location, smallerChild);
                FileNode <Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
                smallerChildNodeTemplate.commitChanges();
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
//        System.out.println("data added, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        startingNode.commitChanges();
    }

    public FileNode <Value> getNode(Long obj)
    {
        FileNode <Value> cacheResult = nodeCache.get(obj);
        if(cacheResult == null)
        {
            FileNode <Value> resultNode = new FileNode <Value>(KEY_MAX_SIZE, VALUE_MAX_SIZE, HALF_MAX_SIZE, null, valueClassType);
            resultNode.fetchNodeFromHard(obj);
            nodeCache.put(obj, resultNode);
        }
        return cacheResult;
    }


    protected Value returnValue(String key, FileNode <Value> startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.keyValPair.elementAt(i1).getValue();
    }

    protected void updateValue(String key, Value value, FileNode <Value> startingNodeTemplate, int i1)
    {
        Pair<String, Value> oldKeyValuePair = startingNodeTemplate.keyValPair.elementAt(i1);
        startingNodeTemplate.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
        startingNodeTemplate.commitChanges();
    }

    protected FileNode <Value> createNewMiddleNode(Long parent)
    {
        FileNode <Value> resultNode = new FileNode <Value>(KEY_MAX_SIZE, VALUE_MAX_SIZE, HALF_MAX_SIZE, parent, valueClassType);
        resultNode.fetchNodeFromHard(null);
        nodeCache.put(resultNode.getMyPointer(), resultNode);
        return resultNode;
    }

    protected FileNode <Value> createNewLeafNode(Long parent)
    {
        return createNewMiddleNode(parent);
    }


    protected boolean thisDataExists(String key, FileDataLocation<Value> newLoc)
    {
        if(newLoc.offset == newLoc.node.getSize() || key.compareTo(newLoc.node.keyValPair.elementAt(newLoc.offset).getKey()) != 0)
            return false;
        return true;
    }

    protected FileNode <Value>[] splitCurrentNode(FileNode <Value> startingNode)
    {
        FileNode <Value> newNodeTemplate = createNewLeafNode(startingNode.parent);
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndCreateParentIfRequired(startingNode, newNodeTemplate, offset);
        addVictimToParent(startingNode, victim, newNodeTemplate);
        newNodeTemplate.commitChanges();
        return null;
    }

    protected void moveDataToSiblingAndCreateParentIfRequired(FileNode <Value> oldNodeTemplate, FileNode <Value> newNodeTemplate, int offset)
    {
        for (int i = offset; i <= MAX_SIZE; i++)
        {
            if (oldNodeTemplate.getSize() - 1 < offset || oldNodeTemplate.child.size() - 1 < offset)
                System.out.println("" +
                        "ohhhh nooo");
            if (i == offset) // first node
            {
                Long smallerChild = oldNodeTemplate.child.remove(offset), biggerChild = oldNodeTemplate.child.remove(offset);
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset)
                        , biggerChild, smallerChild);
            } else
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset), oldNodeTemplate.child.remove(offset), null);
        }
        createParentIfRequired(oldNodeTemplate, newNodeTemplate);
    }

    protected FileDataLocation<Value> findLoc(String key, FileNode <Value> startingNodeTemplate)
    {
        if(startingNodeTemplate.getSize() == 0) return new FileDataLocation<Value>(startingNodeTemplate, 0);
        Long nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = startingNodeTemplate.child.elementAt(i1);
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return new FileDataLocation<Value>(startingNodeTemplate, i1);
        else
            nextChild = startingNodeTemplate.child.elementAt(i1);
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
//            nextChild = startingNodeTemplate.child.elementAt(i1);
//        else if (i1 == -1)
//            System.out.println("ohhhh my goood");
//        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
//            return returnValue(key, startingNodeTemplate, i1);
//        else
//            nextChild = startingNodeTemplate.child.elementAt(i1);
//        return search(key, getNode(nextChild));
//    }


//    protected void update(String key, Value value, FileNode <Value> startingNodeTemplate)
//    {
//        FileNode <Value> nextChild;
//        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
//        if (i1 == startingNodeTemplate.getSize())
//            nextChild = startingNodeTemplate.child.elementAt(i1);
//        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
//        {
//            updateValue(key, value, startingNodeTemplate, i1);
//            return;
//        } else
//            nextChild = getNode(startingNodeTemplate.child.elementAt(i1));
//        update(key, value, nextChild);
//    }

    protected String toString(Vector<FileNode <Value>> nodeTemplateQ, int stringDepth)
    {
        if (nodeTemplateQ.size() == 0)
            return "";
        FileNode <Value> currentNodeTemplate = nodeTemplateQ.remove(0);
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
            Long childNode = currentNodeTemplate.child.elementAt(i);
            if (childNode != null) nodeTemplateQ.add(getNode(childNode));
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }
}
