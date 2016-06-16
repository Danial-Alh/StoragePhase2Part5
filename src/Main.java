import Dictionaries.*;
import Primitives.WordProperties;
import Tree.BTree;
import Tree.FileBtree;
import Tree.RamFileBtree;
import Vector.FileVector;
import com.sun.glass.ui.Size;

public class Main
{
    public static boolean usefile = true;
    public static int keyS = 10, valS = 4, halfS = 2, range = 600000;
    public static long start;

    public static void main(String args[])
    {
        if(args.length > 0)
            halfS = Integer.parseInt(args[0]);
        if(args.length > 1)
            range = Integer.parseInt(args[1]);
        start = System.currentTimeMillis();
//        bTreeTest();
//        fileBTreeDictionaryTest();
//        btreeDictionaryTest();
//        bptreeDictionaryTest();
//        trieDictionaryTest();
//        hashMapDictionaryTest();
//        ramFileTest();
        fileVectorTest();
        long end = System.currentTimeMillis();

        System.out.println("minutes elapsed: " + (end - start) / 60000.0);
        System.out.println("seconds elapsed: " + (end - start) / 1000.0);
    }

    private static void fileVectorTest()
    {
        FileVector<WordProperties> fileVector = new FileVector<>(WordProperties.class);
        int size = 100000;
        Long ptr[] = new Long[size];
        for(int i = 0; i < size; i++)
        {
            ptr[i] = fileVector.writeElementAt(null, 0, new WordProperties(2));
        }
        for(int i = 0; i < size; i++)
        {
            WordProperties wordProperties = fileVector.elementAt(ptr[i], 0);
            wordProperties.setOccurrences(wordProperties.getOccurrences()*(i+1));
            fileVector.writeElementAt(ptr[i], 0, wordProperties);
        }

        for(int i = 0; i < size; i++)
        {
            System.out.println("i: "+ fileVector.elementAt(ptr[i], 0).getOccurrences());
        }
    }

    private static void ramFileTest()
    {
        double timeMin = Double.MAX_VALUE;
        start = System.currentTimeMillis();
        RamFileBtree<WordProperties> ramFileBtree = new RamFileBtree<>(10, new WordProperties(0).sizeof(), halfS, WordProperties.class);
        try
        {
            for (int i = 0; i < range; i++)
            {
                ramFileBtree.insert(String.valueOf(i), new WordProperties(i));
//                if (i % 100000 == 0)
//                {
//                    long end = System.currentTimeMillis();
//                    System.out.println("nodes inserted: " + i);
//                    System.out.println("minutes elapsed: " + (end - start) / 60000.0);
//                    System.out.println("seconds elapsed: " + (end - start) / 1000.0);
//                }
            }
            long end = System.currentTimeMillis();
//            System.out.println("halfs ----------->>>>>>> " + halfS);
            double timeInterval = (end - start) / 1000.0;
            System.out.println(timeInterval);
//            System.out.println("minutes elapsed: " + (timeInterval / 60.0));
//            System.out.println("seconds elapsed: " + timeInterval);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
//        for(int i = 0; i < range; i++)
//            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
//        for(int i = 0; i < 9; i++)
//            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
//        for(int i = 0; i < 8; i++)
//            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
//        for(int i = 0; i < 7; i++)
//            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
    }

    private static void fileBTreeDictionaryTest()
    {

        FileBtreeDictionary fileBtreeDictionary = new FileBtreeDictionary(halfS);
        for (int i = 0; i < range; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 9; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 8; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 7; i++)
            fileBtreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        printFileBtree(fileBtreeDictionary.getFileBtree());

    }

    private static void bptreeDictionaryTest()
    {
        BptreeDictionary bptreeDictionary = new BptreeDictionary(halfS, keyS, valS, usefile);
        for (int i = 0; i < range; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 9; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 8; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 7; i++)
            bptreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + bptreeDictionary.getBpTree().search(String.valueOf(i)));

        printFileBtree(bptreeDictionary.getFileBtree());

    }

    private static void hashMapDictionaryTest()
    {
        HashMapDictionary hashMapDictionary = new HashMapDictionary(halfS, keyS, valS, usefile);
        for (int i = 0; i < range; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 9; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 8; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 7; i++)
            hashMapDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + hashMapDictionary.getHashMap().get(String.valueOf(i)));

        printFileBtree(hashMapDictionary.getFileBtree());
    }

    private static void trieDictionaryTest()
    {
        TrieDictionary trieDictionary = new TrieDictionary(halfS, keyS, valS, usefile);
        for (int i = 0; i < range; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 9; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 8; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 7; i++)
            trieDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + trieDictionary.getTrie().get(String.valueOf(i)));
        printFileBtree(trieDictionary.getFileBtree());
    }

    private static void btreeDictionaryTest()
    {
        BtreeDictionary btreeDictionary = new BtreeDictionary(halfS, keyS, valS, usefile);
        for (int i = 0; i < range; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 9; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 8; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));
        for (int i = 0; i < 7; i++)
            btreeDictionary.insertNodeIfnotExists(String.valueOf(i));

        for (int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + btreeDictionary.getbTree().search(String.valueOf(i)));
        printFileBtree(btreeDictionary.getFileBtree());
    }

    private static void bTreeTest()
    {
        BTree<Integer, Integer> tree = new BTree<>(halfS);
        for (int i = 0; i < range; i++)
        {
            if (i == 9)
                System.out.println("");
            tree.insert(i, i);
//            System.out.println("heap size:\t" + Runtime.getRuntime().totalMemory()/(1024*1024) + "\tfree memory:\t" + Runtime.getRuntime().freeMemory()/(1024*1024) + "\n");
        }
//        System.out.println(tree);
    }

    private static void printFileBtree(FileBtree<WordProperties> fileBtree)
    {
        System.out.println("\n\n*****FileBtree*****\n\n");
        for (int i = 0; i < range; i++)
            System.out.println(String.valueOf(i) + " --> " + fileBtree.search(String.valueOf(i)));
    }
}

