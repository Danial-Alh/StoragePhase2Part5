package Tree;

import FileManagement.RandomAccessFileManagement;
import javafx.util.Pair;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Vector;

public abstract class FileBtreeTemplate<Value extends Sizeofable & Parsable>
{
    protected final int KEY_MAX_SIZE;
    protected final int VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected final Class valueClassType;
    protected int depth;
    protected HashMap<Long, NodeTemplate> nodeCache;

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
    protected abstract void createParentIfRequired(NodeTemplate oldNodeTemplate, NodeTemplate newNodeTemplate);
    protected abstract void addVictimToParent(NodeTemplate startingNode, int victim, NodeTemplate newNodeTemplate);

    protected void insert(NodeTemplate startingNode, Pair<String, Value> newData, Long biggerChild, Long smallerChild)
    {
//        System.out.println("adding data, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        if (startingNode.getSize() == 0)
        {
            startingNode.keyValPair.add(newData);
            startingNode.child.add(smallerChild);
            if (smallerChild != null)
            {
                NodeTemplate smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
                smallerChildNodeTemplate.commitChanges();
            }
            startingNode.child.add(biggerChild);
            if (biggerChild != null)
            {
                NodeTemplate biggerChildNodeTemplate = getNode(biggerChild);
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
                NodeTemplate biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
                biggerChildNodeTemplate.commitChanges();
            }
            if (smallerChild != null)
            {
                startingNode.child.set(location, smallerChild);
                NodeTemplate smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
                smallerChildNodeTemplate.commitChanges();
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
//        System.out.println("data added, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        startingNode.commitChanges();
    }

    public NodeTemplate getNode(Long obj)
    {
        NodeTemplate cacheResult = nodeCache.get(obj);
        if(cacheResult == null)
        {
            NodeTemplate resultNode = new NodeTemplate(HALF_MAX_SIZE, null);
            resultNode.fetchNodeFromHard(obj);
            nodeCache.put(obj, resultNode);
        }
        return cacheResult;
    }


    protected Value returnValue(String key, NodeTemplate startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.keyValPair.elementAt(i1).getValue();
    }

    protected void updateValue(String key, Value value, NodeTemplate startingNodeTemplate, int i1)
    {
        Pair<String, Value> oldKeyValuePair = startingNodeTemplate.keyValPair.elementAt(i1);
        startingNodeTemplate.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
        startingNodeTemplate.commitChanges();
    }

    protected NodeTemplate createNewMiddleNode(Long parent)
    {
        NodeTemplate resultNode = new NodeTemplate(HALF_MAX_SIZE, parent);
        resultNode.fetchNodeFromHard(null);
        nodeCache.put(resultNode.getMyPointer(), resultNode);
        return resultNode;
    }

    protected NodeTemplate createNewLeafNode(Long parent)
    {
        return createNewMiddleNode(parent);
    }


    protected boolean thisDataExists(String key, DataLocation newLoc)
    {
        if(newLoc.offset == newLoc.node.getSize() || key.compareTo(newLoc.node.keyValPair.elementAt(newLoc.offset).getKey()) != 0)
            return false;
        return true;
    }

    protected NodeTemplate[] splitCurrentNode(NodeTemplate startingNode)
    {
        NodeTemplate newNodeTemplate = createNewLeafNode(startingNode.parent);
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndCreateParentIfRequired(startingNode, newNodeTemplate, offset);
        addVictimToParent(startingNode, victim, newNodeTemplate);
        newNodeTemplate.commitChanges();
        return null;
    }

    protected void moveDataToSiblingAndCreateParentIfRequired(NodeTemplate oldNodeTemplate, NodeTemplate newNodeTemplate, int offset)
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

    protected DataLocation findLoc(String key, NodeTemplate startingNodeTemplate)
    {
        if(startingNodeTemplate.getSize() == 0) return new DataLocation(startingNodeTemplate, 0);
        Long nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = startingNodeTemplate.child.elementAt(i1);
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return new DataLocation(startingNodeTemplate, i1);
        else
            nextChild = startingNodeTemplate.child.elementAt(i1);
        return (nextChild == null ? new DataLocation(startingNodeTemplate, i1) : findLoc(key, getNode(nextChild)));
    }

//    protected Value search(String key, NodeTemplate startingNodeTemplate)
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


//    protected void update(String key, Value value, NodeTemplate startingNodeTemplate)
//    {
//        NodeTemplate nextChild;
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

    protected String toString(Vector<NodeTemplate> nodeTemplateQ, int stringDepth)
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
            Long childNode = currentNodeTemplate.child.elementAt(i);
            if (childNode != null) nodeTemplateQ.add(getNode(childNode));
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }

    class DataLocation
    {
        NodeTemplate node;
        int offset;

        public DataLocation(NodeTemplate node, int offset)
        {
            this.node = node;
            this.offset = offset;
        }
    }

    public class NodeTemplate
    {
        protected Vector<Pair<String, Value>> keyValPair;
        protected Vector<Long> child;
        protected Long parent, myPointer;
        protected int id;

        public NodeTemplate(int halfMaxSize, Long parent)
        {
            this.parent = parent;
//        this.id = ++idCounter;
            this.id = 0;
            keyValPair = new Vector<>();
            child = new Vector<>();
        }

        protected Long getMyPointer()
        {
            return myPointer;
        }


        public int getSize()
        {
            return keyValPair.size();
        }


        public String toString()
        {
//        NodeTemplate parentNodeTemplate = getNode(parent);
//        String result = "<<id:" + id + ",ref:" + (parentNodeTemplate == null ? -1 : parentNodeTemplate.id) + ">>";
            String result = "";
            for (Pair<String, Value> pair : keyValPair)
                result += "**" + pair.getKey().toString();
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


        protected void fetchNodeFromHard(Long myPointer)
        {
            RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
            if (myPointer == null)
                try
                {
                    instance.seek(instance.getChannel().size());
                    this.myPointer = instance.getFilePointer();
//                    System.out.println("creating new node; pointer: " + this.myPointer);
                    commitChanges();
                    return;
                } catch (IOException e)
                {
                    e.printStackTrace();
                }


            try
            {
//                System.out.println("fetching from hard, pointer: " + myPointer);
                this.myPointer = myPointer;
                instance.seek(myPointer);
                parent = instance.readLong();
                if(parent == -1)
                    parent = null;
                int size = instance.readInt();
                for (int i = 0; i < size; i++)
                {
                    byte tempByteArray[] = new byte[KEY_MAX_SIZE];
                    instance.read(tempByteArray, 0, KEY_MAX_SIZE);
                    String key = new String(tempByteArray);

                    tempByteArray = new byte[VALUE_MAX_SIZE];
                    instance.read(tempByteArray);
                    Value value = (Value) valueClassType.newInstance();
                    value.parsefromByteArray(tempByteArray);

                    keyValPair.add(new Pair<String, Value>(key, value));
                }
                for (int i = 0; i <= size; i++)
                    child.add(instance.readLong());
            } catch (IOException | InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        protected void commitChanges()
        {
//            System.out.println("committing, pointer: " + myPointer);
            RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
            try
            {
                instance.seek(myPointer);
                instance.writeLong(parent == null ? -1 : parent);
                instance.writeInt(keyValPair.size());
                for (int i = 0; i < MAX_SIZE; i++)
                {
                    if(i < keyValPair.size())
                    {
                        Pair<String, Value> tempKeyVal = keyValPair.elementAt(i);
                        instance.writeBytes(tempKeyVal.getKey());
                        byte tempByteArray[] = tempKeyVal.getValue().toByteArray();
                        int emptyBytes = VALUE_MAX_SIZE - tempByteArray.length;
                        instance.write(tempByteArray);
                        instance.write(new byte[emptyBytes]);
                    }
                    else
                    {
                        instance.writeBytes(new String(new byte[KEY_MAX_SIZE], "UTF-8"));
                        int emptyBytes = VALUE_MAX_SIZE;
                        instance.write(new byte[emptyBytes]);
                    }
                }
                for (int i = 0; i <= MAX_SIZE; i++)
                {
                    if(i < child.size())
                    {
                        Long tempChild = child.elementAt(i);
                        long childPointer = (tempChild == null ? -1 : tempChild);
                        instance.writeLong(childPointer);
                    }
                    else
                        instance.writeLong((long) -1);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

}
