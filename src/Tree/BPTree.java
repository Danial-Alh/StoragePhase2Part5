//package Tree;
//
//import javafx.util.Pair;
//
//public class BPTree<Key extends Comparable<? super Key>, Value> extends BTree<Key, Value>
//{
//
//    public BPTree(int halfMaxSize)
//    {
//        super(halfMaxSize);
//        root = createNewLeafNode(null).getMyPointer();
//    }
//
//    @Override
//    protected Value returnValue(Key key, NodeTemplate startingNode, int i1)
//    {
//        if(startingNode.child.elementAt(i1) == null) // if it is leaf node
//            return super.returnValue(key, startingNode, i1); // it won't dig any more
//        else
//            return search(key, getNode(startingNode.child.elementAt(i1+1)));
//    }
//
//    @Override
//    protected void updateValue(Key key, Value value, NodeTemplate startingNodeTemplate, int i1)
//    {
//        if(startingNodeTemplate.child.elementAt(i1) == null) //it is leaf node
//            super.updateValue(key, value, startingNodeTemplate, i1);// it will add it directly to starting node
//        else
//            update(key, value, startingNodeTemplate.child.elementAt(i1+1));
//    }
//
//    @Override
//    protected NodeTemplate createNewLeafNode(NodeTemplate parent)
//    {
//        return new LeafNodeBPTree(HALF_MAX_SIZE, null);
//    }
//
//    @Override
//    protected NodeTemplate[] splitCurrentNode(NodeTemplate startingNode)
//    {
//        LeafNodeBPTree newNode = new LeafNodeBPTree(this.HALF_MAX_SIZE, startingNode.parent);
//        int victim = HALF_MAX_SIZE;
//        int offset = victim;
//        Pair<Key, Value> victimPair = startingNode.keyValPair.elementAt(victim);
//
//        moveDataToSiblingAndParent(startingNode, newNode, offset);
//        startingNode.child.add(null);
//        insert(startingNode.parent, victimPair, newNode, startingNode);
//
//        LeafNodeBPTree tempNode = ((LeafNodeBPTree)startingNode).rightSibling;
//        ((LeafNodeBPTree)startingNode).rightSibling = newNode;
//        newNode.leftSibling = (LeafNodeBPTree) startingNode;
//        newNode.rightSibling = tempNode;
//        return null;
//    }
//
//
//
//    public class LeafNodeBPTree extends NodeBtree
//    {
//        protected LeafNodeBPTree rightSibling, leftSibling;
//
//        public LeafNodeBPTree(int halfMaxSize, NodeTemplate parent)
//        {
//            super(halfMaxSize, parent);
//            leftSibling = rightSibling = null;
//        }
//
//        @Override
//        public String toString()
//        {
//            return "$$lref:" + (leftSibling == null ? -1 : leftSibling.id) + "$$" + super.toString() + "$$rref:" + (rightSibling == null ? -1 : rightSibling.id) + "$$";
//        }
//    }
//
//}