import Dictionaries.*;
import Primitives.WordProperties;
import Tree.BTree;
import Tree.FileBtree;

public class Main
{
    public static boolean usefile = true;
    public static int keyS = 10
            , valS = 4
            , halfS = 2
            , range = 10000;
    public static void main(String args[])
    {
        long start = System.currentTimeMillis();
//        bTreeTest();
//        fileBTreeDictionaryTest();
//        btreeDictionaryTest();
//        bptreeDictionaryTest();
        trieDictionaryTest();
//        hashMapDictionaryTest();
        long end = System.currentTimeMillis();

        System.out.println("minutes elapsed: " + (end-start)/60000.0);
        System.out.println("seconds elapsed: " + (end-start)/1000.0);
    }

    private static void fileBTreeDictionaryTest()
    {

        FileBtreeDictionary fileBtreeDictionary = new FileBtreeDictionary(halfS);
        for(int i = 0; i < range; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        printFileBtree(fileBtreeDictionary.getFileBtree());

    }

    private static void bptreeDictionaryTest()
    {
        BptreeDictionary bptreeDictionary = new BptreeDictionary(halfS, keyS, valS, usefile);
        for(int i = 0; i < range; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + bptreeDictionary.getBpTree().search(String.valueOf(i)));

        printFileBtree(bptreeDictionary.getFileBtree());

    }

    private static void hashMapDictionaryTest()
    {
        HashMapDictionary hashMapDictionary = new HashMapDictionary(halfS, keyS, valS, usefile);
        for(int i = 0; i < range; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + hashMapDictionary.getHashMap().get(String.valueOf(i)));

        printFileBtree(hashMapDictionary.getFileBtree());
    }

    private static void trieDictionaryTest()
    {
        TrieDictionary trieDictionary = new TrieDictionary(halfS, keyS, valS, usefile);
        for(int i = 0; i < range; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + trieDictionary.getTrie().get(String.valueOf(i)));
        printFileBtree(trieDictionary.getFileBtree());
    }

    private static void btreeDictionaryTest()
    {
        BtreeDictionary btreeDictionary = new BtreeDictionary(halfS, keyS, valS, usefile);
        for(int i = 0; i < range; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + btreeDictionary.getbTree().search(String.valueOf(i)));
        printFileBtree(btreeDictionary.getFileBtree());
    }

    private static void bTreeTest()
    {
        BTree<Integer, Integer> tree = new BTree<>(halfS);
        for( int i = 0; i < range; i++) {
            if( i == 9 )
                System.out.println("");
            tree.insert(i, i);
//            System.out.println("heap size:\t" + Runtime.getRuntime().totalMemory()/(1024*1024) + "\tfree memory:\t" + Runtime.getRuntime().freeMemory()/(1024*1024) + "\n");
        }
//        System.out.println(tree);
    }

    private static void printFileBtree(FileBtree<WordProperties> fileBtree)
    {
        System.out.println("\n\n*****FileBtree*****\n\n");
        for(int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + fileBtree.search(String.valueOf(i)));
    }
}

