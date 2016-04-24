import Tree.BPTree;
import Tree.BTree;

public class Main {

    public static void main(String args[])
    {
        BPTree<Integer, Integer> tree = new BPTree<Integer, Integer>(3);
        for( int i = 0; i < 4000; i++) {
            if( i == 10 )
                System.out.println("");
            tree.insert(i, i);
        }
        System.out.println(tree.toString()+"");

    }
}

