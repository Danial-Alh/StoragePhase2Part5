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
    protected RamFileNode<Value> root;
    ExtendedFileBtree<Value> extendedFileBtree;
    private static final long MEMORY_LIMIT = 30;
    protected final long MB = 1024*1024;

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

    public void update(String key, Value value)
    {
        RamFileNode<Value> rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return;
        RamDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
        {
            updateOnExtendedFileBtree(key, value, loc);
            return;
        }
        updateValue(key, value, loc.node, loc.offset);
    }

    private void updateOnExtendedFileBtree(String key, Value value, RamDataLocation<Value> loc)
    {
        if(loc.node.childAreOnFile)
            extendedFileBtree.update(key, value, loc.node.fileChild.elementAt(loc.offset));
    }


    public void insert(String key, Value value) throws Exception
    {
        if(key.getBytes().length > KEY_MAX_SIZE || value.sizeof() > VALUE_MAX_SIZE)
            throw new Exception("length exceeded");
        RamFileNode<Value> rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
        {
            insert(rootNodeTemplate, new Pair<>(key, value),
                    null, null,
                    null, null);
            return;
        }
        RamDataLocation<Value> newLoc = findLoc(key, rootNodeTemplate);
        if (!thisDataExists(key, newLoc)) // if key not exists
        {
            if (newLoc.node.childAreOnFile)
                insert(newLoc.node, new Pair<>(key, value),
                        null, null,
                        null, null);
            else
                extendedFileBtree.insert(key,value, newLoc.node.fileChild.elementAt(newLoc.offset));
            checkMemoryAndFreeIfRequired();
        }

    }

    protected void checkMemoryAndFreeIfRequired()
    {
        if(memoryLimitExceeded())
        {
            extendedFileBtree.invalidateRoots();
            RamFileNode<Value> rootNode = getRootNode();
            rootNode.childAreOnFile = true;
            for(int i = 0; i <= rootNode.getSize(); i++)
            {
                Long childFileNodePtr = extendedFileBtree.addNewRoot(rootNode.child.elementAt(i), rootNode, i);
                if(i >= rootNode.fileChild.size())
                    rootNode.fileChild.add(childFileNodePtr);
                else
                    rootNode.fileChild.set(i, childFileNodePtr);
                rootNode.child.set(i, null);
            }
            Runtime.getRuntime().gc();
        }

    }

    protected boolean memoryLimitExceeded()
    {
        if(Runtime.getRuntime().freeMemory()/MB < MEMORY_LIMIT)
            return true;
        return false;
    }

    public Value search(String key)
    {
        RamFileNode<Value> rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return null;
        RamDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return searchOnExtendedFileBtree(key, loc);
        return loc.node.keyValPair.elementAt(loc.offset).getValue();
//        return search(key, rootNodeTemplate);
    }

    private Value searchOnExtendedFileBtree(String key, RamDataLocation<Value> loc)
    {
        if(loc.node.childAreOnFile)
            return extendedFileBtree.search(key, loc.node.fileChild.elementAt(loc.offset));
        return null;
    }

    protected RamFileNode<Value> getRootNode()
    {
        return root;
    }

    private RamFileNode<Value> getNode(RamFileNode<Value> node)
    {
        return node;
    }

    protected void createParentIfRequired(RamFileNode<Value> oldNodeTemplate, RamFileNode<Value> newNodeTemplate)
    {
        if (oldNodeTemplate.parent == null)
        {
            RamFileNode<Value> parentNodeTemplate = createNewMiddleNode(null);
            oldNodeTemplate.parent = parentNodeTemplate.getMyPointer();
            root = parentNodeTemplate.getMyPointer();
//            newNode.parent = root;
            depth++;
        }
    }

    protected void addVictimToParent(RamFileNode<Value> startingNode, int victim, RamFileNode<Value> newNodeTemplate)
    {
        RamFileNode<Value> parentNodeTemplate = getNode(startingNode.parent);
        insert(parentNodeTemplate, startingNode.keyValPair.remove(victim),
                newNodeTemplate.getMyPointer(), startingNode.getMyPointer(),
                null, null);
    }

    public String toString()
    {
        Vector<RamFileNode<Value>> nodeTemplateQ = new Vector<>();
        nodeTemplateQ.add(getRootNode());
        nodeTemplateQ.add(null);
        return toString(nodeTemplateQ, 1);
    }

    protected void insert(RamFileNode<Value> startingNode, Pair<String, Value> newData,
                          RamFileNode<Value> biggerChild, RamFileNode<Value> smallerChild,
                          Long biggerFileChild, Long smallerFileChild)
    {
//        System.out.println("adding data, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
        if (startingNode.getSize() == 0)
        {
            startingNode.keyValPair.add(newData);
            startingNode.child.add(smallerChild);
            startingNode.fileChild.add(smallerFileChild);
            if (smallerChild != null)
            {
                RamFileNode<Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (smallerFileChild != null)
                extendedFileBtree.updateParent(smallerFileChild, startingNode.getMyPointer());

            startingNode.child.add(biggerChild);
            startingNode.fileChild.add(biggerFileChild);
            if (biggerChild != null)
            {
                RamFileNode<Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (biggerFileChild != null)
                extendedFileBtree.updateParent(biggerFileChild, startingNode.getMyPointer());
        } else
        {
            int location = startingNode.binarySearchForLocationToAdd(newData.getKey());
            if (location == startingNode.keyValPair.size())
            {
                startingNode.keyValPair.add(newData);
                startingNode.child.add(biggerChild);
                startingNode.fileChild.add(biggerFileChild);
            } else
            {
                startingNode.keyValPair.insertElementAt(newData, location);
                startingNode.child.insertElementAt(biggerChild, location + 1);
                startingNode.fileChild.insertElementAt(biggerFileChild, location + 1);
            }
            if (biggerChild != null)
            {
                RamFileNode<Value> biggerChildNodeTemplate = getNode(biggerChild);
                biggerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (biggerFileChild != null)
                extendedFileBtree.updateParent(biggerFileChild, startingNode.getMyPointer());
            if (smallerChild != null)
            {
                startingNode.child.set(location, smallerChild);
                RamFileNode<Value> smallerChildNodeTemplate = getNode(smallerChild);
                smallerChildNodeTemplate.parent = startingNode.getMyPointer();
            }
            if (smallerFileChild != null)
            {
                startingNode.fileChild.set(location, smallerFileChild);
                extendedFileBtree.updateParent(smallerFileChild, startingNode.getMyPointer());
            }
            if (startingNode.getSize() > MAX_SIZE)
                splitCurrentNode(startingNode);
        }
//        System.out.println("data added, pointer: " + startingNode.myPointer + " node size: " + startingNode.getSize());
    }

    public void insertFromFileNodes(RamFileNode<Value> node, Pair<String, Value> newData, Long smallerChildPointer, Long biggerChildPointer)
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

    protected Value returnValue(String key, RamFileNode<Value> startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.keyValPair.elementAt(i1).getValue();
    }

    protected void updateValue(String key, Value value, RamFileNode<Value> startingNodeTemplate, int i1)
    {
        Pair<String, Value> oldKeyValuePair = startingNodeTemplate.keyValPair.elementAt(i1);
        startingNodeTemplate.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
    }

    protected RamFileNode<Value> createNewMiddleNode(RamFileNode<Value> parent)
    {
        RamFileNode<Value> resultNode = new RamFileNode<Value>(HALF_MAX_SIZE, parent);
        return resultNode;
    }

    protected RamFileNode<Value> createNewLeafNode(RamFileNode<Value> parent)
    {
        return createNewMiddleNode(parent);
    }


    protected boolean thisDataExists(String key, RamDataLocation<Value> newLoc)
    {
        if(newLoc.offset == newLoc.node.getSize() || key.compareTo(((Pair<String, Value>) newLoc.node.keyValPair.elementAt(newLoc.offset)).getKey()) != 0)
            return false;
        return true;
    }

    protected RamFileNode<Value>[] splitCurrentNode(RamFileNode<Value> startingNode)
    {
        RamFileNode<Value> newNodeTemplate = createNewLeafNode(startingNode.parent);
        int victim = HALF_MAX_SIZE;
        int offset = victim + 1;
        moveDataToSiblingAndCreateParentIfRequired(startingNode, newNodeTemplate, offset);
        addVictimToParent(startingNode, victim, newNodeTemplate);
        return null;
    }

    protected void moveDataToSiblingAndCreateParentIfRequired(RamFileNode<Value> oldNodeTemplate, RamFileNode<Value> newNodeTemplate, int offset)
    {
        for (int i = offset; i <= MAX_SIZE; i++)
        {
            if (oldNodeTemplate.getSize() - 1 < offset || oldNodeTemplate.child.size() - 1 < offset)
                System.out.println("" +
                        "ohhhh nooo");
            if (i == offset) // first node
            {
                RamFileNode<Value> smallerChild = oldNodeTemplate.child.remove(offset), biggerChild = oldNodeTemplate.child.remove(offset);
                Long smallerFileChild = oldNodeTemplate.fileChild.remove(offset), biggerFileChild = oldNodeTemplate.fileChild.remove(offset);
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset),
                        biggerChild, smallerChild,
                        biggerFileChild, smallerFileChild);
            } else
                insert(newNodeTemplate, oldNodeTemplate.keyValPair.remove(offset),
                        oldNodeTemplate.child.remove(offset), null,
                        oldNodeTemplate.fileChild.remove(offset), null);
        }
        createParentIfRequired(oldNodeTemplate, newNodeTemplate);
    }

    protected RamDataLocation<Value> findLoc(String key, RamFileNode<Value> startingNodeTemplate)
    {
        if(startingNodeTemplate.getSize() == 0) return new RamDataLocation<Value>(startingNodeTemplate, 0);
        RamFileNode<Value> nextChild;
        int i1 = startingNodeTemplate.binarySearchForLocationToAdd(key);
        if (i1 == startingNodeTemplate.getSize())
            nextChild = startingNodeTemplate.child.elementAt(i1);
        else if (startingNodeTemplate.keyValPair.elementAt(i1).getKey().compareTo(key) == 0)
            return new RamDataLocation<Value>(startingNodeTemplate, i1);
        else
            nextChild = startingNodeTemplate.child.elementAt(i1);
        return (nextChild == null ? new RamDataLocation<Value>(startingNodeTemplate, i1) : findLoc(key, getNode(nextChild)));
    }

    protected String toString(Vector<RamFileNode<Value>> nodeTemplateQ, int stringDepth)
    {
        if (nodeTemplateQ.size() == 0)
            return "";
        RamFileNode<Value> currentNodeTemplate = nodeTemplateQ.remove(0);
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
            RamFileNode<Value> childNode = currentNodeTemplate.child.elementAt(i);
            if (childNode != null) nodeTemplateQ.add(getNode(childNode));
        }
        return currentNodeTemplate.toString() + "\t" + toString(nodeTemplateQ, stringDepth);
    }

}