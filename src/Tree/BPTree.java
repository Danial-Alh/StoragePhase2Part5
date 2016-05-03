package Tree;

import javafx.util.Pair;

public class BPTree<Key extends Comparable<? super Key>, Value> extends BTree<Key, Value>
{

    public BPTree(int halfMaxSize)
    {
        super(halfMaxSize);
        root = new LeafNode(halfMaxSize, null);
    }


    @Override
    protected Value returnValue(Key key, Node startingNode, int i1)
    {
        if(startingNode.child.elementAt(i1) == null) // if it is leaf node
            return super.returnValue(key, startingNode, i1); // it won't dig any more
        else
            return search(key, startingNode.child.elementAt(i1+1));
    }

    @Override
    protected void updateValue(Key key, Value value, Node startingNode, int i1)
    {
        if(startingNode.child.elementAt(i1) == null) //it is leaf node
            super.updateValue(key, value, startingNode, i1);// it will add it directly to starting node
        else
            update(key, value, startingNode.child.elementAt(i1+1));
    }

    protected class LeafNode extends Node
    {
        protected LeafNode rightSibling, leftSibling;

        public LeafNode(int halfMaxSize, Node parent)
        {
            super(halfMaxSize, parent);
            leftSibling = rightSibling = null;
        }

        @Override
        protected Node[] splitCurrentNode()
        {
            LeafNode newNode = new LeafNode(this.HALF_MAX_SIZE, this.parent);
            int victim = HALF_MAX_SIZE;
            int offset = victim;
            Pair<Key, Value> victimPair = keyValPair.elementAt(victim);
            moveDataToSiblingAndParent(newNode, offset);
            child.add(null);
            parent.insert(victimPair, newNode, this);
            LeafNode tempNode = rightSibling;
            rightSibling = newNode;
            newNode.leftSibling = this;
            newNode.rightSibling = tempNode;
            return null;
        }

        @Override
        public String toString()
        {
            return "$$lref:" + (leftSibling == null ? -1 : leftSibling.id) + "$$" + super.toString() + "$$rref:" + (rightSibling == null ? -1 : rightSibling.id) + "$$";
        }
    }
}
