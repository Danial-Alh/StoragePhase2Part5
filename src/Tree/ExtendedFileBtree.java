package Tree;

import java.util.HashMap;

public class ExtendedFileBtree<Value extends Sizeofable & Parsable> extends FileBtreeTemplate<Value>
{
    private HashMap<Long, RootInfo> roots;
    private RamFileBtree<Value> ramFileBtree;

    public ExtendedFileBtree(int keyMaxSize, int valueMaxSize, int halfMaxSize, Class valueClassType, RamFileBtree<Value> ramFileBtree)
    {
        super(keyMaxSize, valueMaxSize, halfMaxSize, valueClassType);
        roots = new HashMap<>();
        this.ramFileBtree = ramFileBtree;
    }

    @Override
    public Value search(String key)
    {
        return null;
    }

    public Value search(String key, Long startingNode)
    {
        FileNode<Value> rootNodeTemplate = getNode(startingNode);
        if (rootNodeTemplate.getSize() == 0)
            return null;
        FileDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return null;
        return loc.node.keyValPair.elementAt(loc.offset).getValue();
    }

    @Override
    public void insert(String key, Value value) throws Exception
    {

    }

    @Override
    public void update(String key, Value value)
    {

    }

    protected void invalidateRoots()
    {
        roots.clear();
    }

    protected Long addNewRoot(RamFileNode<Value> newRamRoot, RamFileNode<Value> ramParent,
                              int newRamPointerLocInParent)
    {
        FileNode<Value> newFileNode = convertRamNodeToFileNode(newRamRoot, null);
        roots.put(newFileNode.getMyPointer(),
                new RootInfo(newFileNode.getMyPointer(), new RamDataLocation<Value>(ramParent, newRamPointerLocInParent)));
        return newFileNode.getMyPointer();
    }

    @Override
    protected void createParentIfRequired(FileNode<Value> oldNodeTemplate, FileNode<Value> newNodeTemplate)
    {
        // create parent is not required any time
        // but we should add the new node as a root to roots
        if(oldNodeTemplate.parent == null)
        {
            RootInfo oldNodeRootInfo = roots.get(oldNodeTemplate.getMyPointer());
            roots.put(
                    newNodeTemplate.getMyPointer(),
                    new RootInfo(newNodeTemplate.getMyPointer(), new RamDataLocation<Value>(oldNodeRootInfo.locationDetailsInParent.node,
                            oldNodeRootInfo.locationDetailsInParent.offset+1))
            );
        }
    }

    @Override
    protected void addVictimToParent(FileNode<Value> smallerChild, int victim, FileNode<Value> biggerChild)
    {
        if(smallerChild.parent == null)
            ramFileBtree.insertFromFileNodes( roots.get(smallerChild.getMyPointer()).locationDetailsInParent.node,
                    smallerChild.keyValPair.remove(victim), smallerChild.getMyPointer(), biggerChild.getMyPointer() );
        else
        {
            FileNode<Value> parentNodeTemplate = getNode(smallerChild.parent);
            insert(parentNodeTemplate, smallerChild.keyValPair.remove(victim), biggerChild.getMyPointer(),
                    smallerChild.getMyPointer());
        }
    }

    private FileNode<Value> convertRamNodeToFileNode(RamFileNode<Value> newRamNode, Long parent)
    {
        FileNode<Value> newFileNode = createNewMiddleNode(parent);

        newFileNode.keyValPair = newRamNode.keyValPair;
        if(newRamNode.childAreOnFile)
            newFileNode.child = newRamNode.fileChild;
        else
        {
            for(int i = 0; i < newRamNode.child.size(); i++)
            {
                RamFileNode<Value> tempChild = newRamNode.child.elementAt(i);
                if(tempChild == null)
                    newFileNode.child.add(null);
                else
                    newFileNode.child.add(
                            convertRamNodeToFileNode(tempChild, newFileNode.getMyPointer()).getMyPointer()
                    );
            }

        }
        return newFileNode;
    }

    public void updateParent(Long pointer, RamFileNode node)
    {
        roots.get(pointer).locationDetailsInParent.node = node;
    }

    public class RootInfo
    {
        private Long rootPointer;
        private RamDataLocation<Value> locationDetailsInParent;

        public RootInfo(Long rootPointer, RamDataLocation<Value> locationDetailsInParent)
        {
            this.rootPointer = rootPointer;
            this.locationDetailsInParent = locationDetailsInParent;
        }
    }
}
