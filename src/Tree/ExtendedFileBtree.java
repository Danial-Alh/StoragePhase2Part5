package Tree;

import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.Nodes.DataLocations.FileDataLocation;
import Tree.Nodes.FileNode;
import Tree.Nodes.DataLocations.RamDataLocation;
import Tree.Nodes.RamFileNode;
import javafx.util.Pair;

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
        return loc.getNode().getKeyValPair().elementAt(loc.getOffset()).getValue();
    }

    @Override
    public void insert(String key, Value value) throws Exception
    {

    }

    public void insert(String key, Value value, Long startingNode) /*throws Exception*/
    {
//        if(key.getBytes().length > KEY_MAX_SIZE || value.sizeof() > VALUE_MAX_SIZE)
//            throw new Exception("length exceeded");
        FileNode<Value> rootNodeTemplate = getNode(startingNode);
        if (rootNodeTemplate.getSize() == 0)
        {
            insert(rootNodeTemplate, new Pair<>(key, value),
                    null, null);
            return;
        }
        FileDataLocation<Value> newLoc = findLoc(key, rootNodeTemplate);
        if (!thisDataExists(key, newLoc)) // if key not exists
            insert(newLoc.getNode(), new Pair<>(key, value),
                    null, null);
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
    }

    @Override
    protected void addVictimToParent(FileNode<Value> smallerChild, int victim, FileNode<Value> biggerChild)
    {
        if(smallerChild.getParent() == null)
        {
            // we should add the new node as a root to roots
            RootInfo oldNodeRootInfo = roots.get(smallerChild.getMyPointer());
            roots.put(
                    biggerChild.getMyPointer(),
                    new RootInfo(biggerChild.getMyPointer(), new RamDataLocation<>(oldNodeRootInfo.locationDetailsInParent.getNode(),
                            oldNodeRootInfo.locationDetailsInParent.getOffset() + 1))
            );
            ramFileBtree.insertFromFileNodes(roots.get(smallerChild.getMyPointer()).locationDetailsInParent.getNode(),
                    smallerChild.getKeyValPair().remove(victim), smallerChild.getMyPointer(), biggerChild.getMyPointer());
        }
        else
        {
            FileNode<Value> parentNodeTemplate = getNode(smallerChild.getParent());
            insert(parentNodeTemplate, smallerChild.getKeyValPair().remove(victim), biggerChild.getMyPointer(),
                    smallerChild.getMyPointer());
        }
    }

    private FileNode<Value> convertRamNodeToFileNode(RamFileNode<Value> newRamNode, Long parent)
    {
        FileNode<Value> newFileNode = createNewMiddleNode(parent);

        newFileNode.setKeyValPair(newRamNode.getKeyValPair());
        if(newRamNode.isChildAreOnFile())
            newFileNode.setChild(newRamNode.getFileChild());
        else
        {
            for(int i = 0; i < newRamNode.getChild().size(); i++)
            {
                RamFileNode<Value> tempChild = newRamNode.getChild().elementAt(i);
                if(tempChild == null)
                    newFileNode.getChild().add(null);
                else
                    newFileNode.getChild().add(
                            convertRamNodeToFileNode(tempChild, newFileNode.getMyPointer()).getMyPointer()
                    );
            }

        }
        return newFileNode;
    }

    public void updateParent(Long pointer, RamFileNode node)
    {
        roots.get(pointer).locationDetailsInParent.setNode(node);
    }

    public void update(String key, Value value, Long startingNode)
    {
        FileNode<Value> rootNodeTemplate = getNode(startingNode);
        if (rootNodeTemplate.getSize() == 0)
            return;
        FileDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return;
        updateValue(key, value, loc.getNode(), loc.getOffset());
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
