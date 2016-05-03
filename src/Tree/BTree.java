package Tree;

import javafx.util.Pair;

public class BTree<Key extends Comparable<? super Key>, Value> extends BtreeTemplate<Key, Value, BtreeTemplate.NodeTemplate>
{

    public BTree(int halfMaxSize)
    {
        super(halfMaxSize);
        root = createNewMiddleNode(null).getMyPointer();
    }

    protected NodeTemplate getNode(NodeTemplate obj)
    {
        return obj;
    }

    protected NodeTemplate getRootNode()
    {
        return root;
    }

    protected Value returnValue(Key key, NodeTemplate startingNodeTemplate, int i1)
    {
        return startingNodeTemplate.keyValPair.elementAt(i1).getValue();
    }

    protected void updateValue(Key key, Value value, NodeTemplate startingNodeTemplate, int i1)
    {
        Pair<Key, Value> oldKeyValuePair = startingNodeTemplate.keyValPair.elementAt(i1);
        startingNodeTemplate.keyValPair.set(i1, new Pair<>(oldKeyValuePair.getKey(), value));
    }

    protected NodeTemplate createNewMiddleNode(NodeTemplate parent)
    {
        return new NodeBtree(this.HALF_MAX_SIZE, parent);
    }

    protected NodeTemplate createNewLeafNode(NodeTemplate parent)
    {
        return createNewMiddleNode(parent);
    }

    public class NodeBtree extends NodeTemplate
    {
        public NodeBtree(int halfMaxSize, NodeTemplate parent)
        {
            super(halfMaxSize, parent);
        }

        @Override
        protected NodeBtree getMyPointer()
        {
            return this;
        }

    }
}



