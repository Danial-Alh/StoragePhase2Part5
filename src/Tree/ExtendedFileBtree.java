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

    protected Long addNewRoot(RamFileBtree<Value>.Node newRamRoot, RamFileBtree<Value>.Node ramParent,
                              int newRamPointerLocInParent)
    {
        NodeTemplate newFileNode = convertRamNodeToFileNode(newRamRoot, null);
        roots.put(newFileNode.getMyPointer(),
                new RootInfo(newFileNode.getMyPointer(), new RamDataLocation(ramParent, newRamPointerLocInParent)));
        return newFileNode.getMyPointer();
    }

    @Override
    protected void createParentIfRequired(NodeTemplate oldNodeTemplate, NodeTemplate newNodeTemplate)
    {
        // create parent is not required any time
        // but we should add the new node as a root to roots
        if(oldNodeTemplate.parent == null)
        {
            RootInfo oldNodeRootInfo = roots.get(oldNodeTemplate.getMyPointer());
            roots.put(
                    newNodeTemplate.getMyPointer(),
                    new RootInfo(newNodeTemplate.getMyPointer(), new RamDataLocation(oldNodeRootInfo.locationDetailsInParent.node,
                            oldNodeRootInfo.locationDetailsInParent.offset+1))
            );
        }
    }

    @Override
    protected void addVictimToParent(NodeTemplate smallerChild, int victim, NodeTemplate biggerChild)
    {
        if(smallerChild.parent == null)
            ramFileBtree.insertFromFileNodes( roots.get(smallerChild.getMyPointer()).locationDetailsInParent.node,
                    smallerChild.keyValPair.remove(victim), smallerChild.getMyPointer(), biggerChild.getMyPointer() );
        else
        {
            NodeTemplate parentNodeTemplate = getNode(smallerChild.parent);
            insert(parentNodeTemplate, smallerChild.keyValPair.remove(victim), biggerChild.getMyPointer(),
                    smallerChild.getMyPointer());
        }
    }

    private NodeTemplate convertRamNodeToFileNode(RamFileBtree<Value>.Node newRamNode, Long parent)
    {
        NodeTemplate newFileNode = createNewMiddleNode(parent);

        newFileNode.keyValPair = newRamNode.keyValPair;
        if(newRamNode.childAreOnFile)
            newFileNode.child = newRamNode.fileChild;
        else
        {
            for(int i = 0; i < newRamNode.child.size(); i++)
            {
                RamFileBtree<Value>.Node tempChild = newRamNode.child.elementAt(i);
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

    public void updateParent(Long pointer, RamFileBtree.Node node)
    {
        roots.get(pointer).locationDetailsInParent.node = node;
    }

    public class RootInfo
    {
        private Long rootPointer;
        private RamDataLocation locationDetailsInParent;

        public RootInfo(Long rootPointer, RamDataLocation locationDetailsInParent)
        {
            this.rootPointer = rootPointer;
            this.locationDetailsInParent = locationDetailsInParent;
        }
    }

    class RamDataLocation extends DataLocation<RamFileBtree.Node>
    {
        public RamDataLocation(RamFileBtree.Node node, int offset)
        {
            super(node, offset);
        }
    }
}
