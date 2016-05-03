import Dictionaries.BtreeDictionary;

public class Main
{

    public static void main(String args[])
    {
//        BTree<String, Integer> tree = new BTree<String, Integer>(2);
//        for( int i = 0; i < 9000000; i++) {
////            if( i == 3 )
////                System.out.println("");
//            tree.insert(String.valueOf(i), i);
////            System.out.println("heap size:\t" + Runtime.getRuntime().totalMemory()/(1024*1024) + "\tfree memory:\t" + Runtime.getRuntime().freeMemory()/(1024*1024) + "\n");
//        }

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

//        RandomAccessFileManagement randomAccessFileWriter = new RandomAccessFileManagement("first.txt");
//        randomAccessFileWriter.write(null);
//        randomAccessFileWriter.read();

    }
}

