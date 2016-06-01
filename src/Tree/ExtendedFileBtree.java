package Tree;

import java.util.HashMap;

public class ExtendedFileBtree<Value extends Sizeofable & Parsable> extends FileBtreeTemplate<Value>
{
    public HashMap<Long, RootInfo> roots;
    public ExtendedFileBtree(int keyMaxSize, int valueMaxSize, int halfMaxSize, Class valueClassType)
    {
        super(keyMaxSize, valueMaxSize, halfMaxSize, valueClassType);
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
                    new RootInfo(newNodeTemplate.getMyPointer(), oldNodeRootInfo.parentPointer,
                            oldNodeRootInfo.pointerLocationInParent+1)
            );
        }
    }

    @Override
    protected void addVictimToParent(NodeTemplate smallerChild, int victim, NodeTemplate biggerChild)
    {
        if(smallerChild.parent == null)
            roots.get(smallerChild.getMyPointer()).parentPointer.insertFromFileNodes(
                    smallerChild.keyValPair.remove(victim), smallerChild.getMyPointer(), biggerChild.getMyPointer() );
        else
        {
            NodeTemplate parentNodeTemplate = getNode(smallerChild.parent);
            insert(parentNodeTemplate, smallerChild.keyValPair.remove(victim), biggerChild.getMyPointer(),
                    smallerChild.getMyPointer());
        }
    }

    protected void invalidateRoots()
    {
        roots.clear();
    }

    protected Long addNewRoot(BTree<String, Value>.Node newRamRoot, BTree<String, Value>.Node ramParent,
                              int newRamPointerLocInParent)
    {
        NodeTemplate newFileNode = convertRamNodeToFileNode(newRamRoot, null);
        roots.put(newFileNode.getMyPointer(),
                new RootInfo(newFileNode.getMyPointer(), ramParent, newRamPointerLocInParent));
        return newFileNode.getMyPointer();
    }

    private NodeTemplate convertRamNodeToFileNode(BTree<String, Value>.Node newRamNode, Long parent)
    {
        NodeTemplate newFileNode = createNewMiddleNode(parent);

        newFileNode.keyValPair = newRamNode.keyValPair;
        if(newRamNode.childAreOnFile)
            newFileNode.child = newRamNode.fileChild;
        else
        {
            for(int i = 0; i < newRamNode.child.size(); i++)
            {
                BTree<String, Value>.Node tempChild = newRamNode.child.elementAt(i);
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

    public class RootInfo
    {
        public Long rootPointer;
        public BTree<String, Value>.Node parentPointer;
        public int pointerLocationInParent;

        public RootInfo(Long rootPointer, BTree<String, Value>.Node parentPointer, int pointerLocationInParent)
        {
            this.rootPointer = rootPointer;
            this.parentPointer = parentPointer;
            this.pointerLocationInParent = pointerLocationInParent;
        }
    }
}
