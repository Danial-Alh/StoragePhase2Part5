import Dictionaries.*;
import Tree.BTree;
import Tree.FileBtree;

public class Main
{

    public static void main(String args[])
    {
//        try
//        {
//            if(new File("tempfile.bi").exists())
//                new File("tempfile.bi").delete();
//            RandomAccessFile randomAccessFileManagement = new RandomAccessFile("tempfile.bi", "rwd");
//            randomAccessFileManagement.writeBytes(new String(new byte[11], "UTF-8"));
////            randomAccessFileManagement.writeBytes("aaaaaaaa");
//            int emptyBytes = 4;
//            randomAccessFileManagement.write(new byte[emptyBytes]);
//            randomAccessFileManagement.close();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        bTreeTest();
        fileBTreeTest();
//        btreeDictionaryTest();
//        bptreeDictionaryTest();
//        trieDictionaryTest();
//        hashMapDictionaryTest();
    }

    private static void fileBTreeTest()
    {
        FileBtree<WordProperties> tree = new FileBtree<>(10, 4, 2, WordProperties.class);
        long start = System.currentTimeMillis();
        for( int i = 0; i < 1000000; i++)
        {
            if( i == 9 )
                System.out.println("");
            try
            {
//                System.out.println("\n\ninserting new node: " + i);
                tree.insert(String.valueOf(i), new WordProperties(1));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
//            System.out.println("heap size:\t" + Runtime.getRuntime().totalMemory()/(1024*1024) + "\tfree memory:\t" + Runtime.getRuntime().freeMemory()/(1024*1024) + "\n");
        }
        long end = System.currentTimeMillis();

        System.out.println(/*tree + "\n"+*/
        "time elapsed: " + (end-start)/60000.0);
    }

    private static void bptreeDictionaryTest()
    {
        BptreeDictionary btreeDictionary = new BptreeDictionary(2);
        for(int i = 0; i < 10; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < 10; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        System.out.println(btreeDictionary);

        for(int i = 0; i < 10; i++)
            System.out.println(String.valueOf(i) + " --> " + btreeDictionary.getBpTree().search(String.valueOf(i)));
    }

    private static void hashMapDictionaryTest()
    {
        HashMapDictionary hashMapDictionary = new HashMapDictionary();
        for(int i = 0; i < 10; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < 10; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

//        System.out.println(trieDictionary);

        for(int i = 0; i < 10; i++)
            System.out.println(String.valueOf(i) + " --> " + hashMapDictionary.getHashMap().get(String.valueOf(i)));
    }

    private static void trieDictionaryTest()
    {
        TrieDictionary trieDictionary = new TrieDictionary();
        for(int i = 0; i < 10; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < 10; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

//        System.out.println(trieDictionary);

        for(int i = 0; i < 10; i++)
            System.out.println(String.valueOf(i) + " --> " + trieDictionary.getTrie().get(String.valueOf(i)));
    }

    private static void btreeDictionaryTest()
    {
        BtreeDictionary btreeDictionary = new BtreeDictionary(2);
        for(int i = 0; i < 10; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for(int i = 0; i < 10; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 9; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 8; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for(int i = 0; i < 7; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        System.out.println(btreeDictionary);

        for(int i = 0; i < 10; i++)
            System.out.println(String.valueOf(i) + " --> " + btreeDictionary.getbTree().search(String.valueOf(i)));
    }

    private static void bTreeTest()
    {
        BTree<Integer, Integer> tree = new BTree<>(2);
        for( int i = 0; i < 100000; i++) {
            if( i == 9 )
                System.out.println("");
            tree.insert(i, i);
//            System.out.println("heap size:\t" + Runtime.getRuntime().totalMemory()/(1024*1024) + "\tfree memory:\t" + Runtime.getRuntime().freeMemory()/(1024*1024) + "\n");
        }
//        System.out.println(tree);
    }
}

