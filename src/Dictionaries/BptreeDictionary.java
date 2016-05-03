package Dictionaries;

import Tree.BPTree;
import Tree.BTree;

/**
 * Created by danial on 5/3/16.
 */
public class BptreeDictionary
{
    public BPTree<String, WordProperties> getBpTree()
    {
        return bpTree;
    }

    private BPTree<String, WordProperties> bpTree;

    public BptreeDictionary(int halfNodeSize)
    {
        this.bpTree = new BPTree<>(halfNodeSize);
    }

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = bpTree.search(word);
        if(wordProperties == null)
        {
            wordProperties = new WordProperties(0);
            bpTree.insert(word, wordProperties);
        }
        else
        {
            wordProperties.occurrences++;
//            bpTree.update(word, wordProperties);
        }
    }

    @Override
    public String toString()
    {
        return bpTree.toString();
    }
}
