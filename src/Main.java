import Tree.BPTree;
import Tree.BTree;
import com.sun.org.apache.xml.internal.utils.Trie;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Main {

    public static void main(String args[])
    {
        BPTree<String, Integer> tree = new BPTree<String, Integer>(2);
        for( int i = 0; i < 9000000; i++) {
            if( i == 3 )
                System.out.println("");
            tree.insert(String.valueOf(i), i);
        }

//        Trie tree = new Trie();
//        for( int i = 0; i < 4000; i++) {
//            if( i == 10 ) {
//                System.out.println("");
//            }
//            tree.put(String.valueOf(i), i);
//        }
//
//        Hashtable<String, Integer> hash = new Hashtable<String, Integer>();
//        System.out.println(tree.toString()+"");

    }
}

