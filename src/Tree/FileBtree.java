package Tree;

import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.Nodes.DataLocations.FileDataLocation;
import Tree.Nodes.FileNode;
import javafx.util.Pair;

import java.util.Vector;

public class FileBtree<Value extends Sizeofable & Parsable> extends FileBtreeTemplate<Value>
{
    Long root;

    public FileBtree(int keyMaxSize, int valueMaxSize, int halfMaxSize, Class valueClassType)
    {
        super(keyMaxSize, valueMaxSize, halfMaxSize, valueClassType);
        this.root = createNewLeafNode(null).getMyPointer();
    }

    protected FileNode<Value> getRootNode()
    {
        return getNode(root);
    }

    public void insert(String key, Value value) throws Exception
    {
        if(key.getBytes().length > KEY_MAX_SIZE || value.sizeof() > VALUE_MAX_SIZE)
            throw new Exception("length exceeded");
        FileNode<Value> rootNodeTemplate = getRootNode();
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
    protected void createParentIfRequired(FileNode<Value> oldNodeTemplate, FileNode<Value> newNodeTemplate)
    {
        if (oldNodeTemplate.getParent() == null)
        {
            FileNode<Value> parentNodeTemplate = createNewMiddleNode(null);
            oldNodeTemplate.setParent(parentNodeTemplate.getMyPointer());
            root = parentNodeTemplate.getMyPointer();
//            newNode.parent = root;
            depth++;
        }
    }

    @Override
    protected void addVictimToParent(FileNode<Value> startingNode, int victim, FileNode<Value> newNodeTemplate)
    {
        FileNode<Value> parentNodeTemplate = getNode(startingNode.getParent());
        insert(parentNodeTemplate, startingNode.getKeyValPair().remove(victim), newNodeTemplate.getMyPointer(),
                startingNode.getMyPointer());
    }

    public Value search(String key)
    {
        FileNode<Value> rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return null;
        FileDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return null;
        return loc.getNode().getKeyValPair().elementAt(loc.getOffset()).getValue();
//        return search(key, rootNodeTemplate);
    }

    public void update(String key, Value value)
    {
        FileNode<Value> rootNodeTemplate = getRootNode();
        if (rootNodeTemplate.getSize() == 0)
            return;
        FileDataLocation<Value> loc = findLoc(key, rootNodeTemplate);
        if(!thisDataExists(key, loc))
            return;
        updateValue(key, value, loc.getNode(), loc.getOffset());
    }

    public String toString()
    {
        Vector<FileNode<Value>> nodeTemplateQ = new Vector<>();
        nodeTemplateQ.add(getRootNode());
        nodeTemplateQ.add(null);
        return toString(nodeTemplateQ, 1);
    }
}