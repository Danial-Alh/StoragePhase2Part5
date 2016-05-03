package Dictionaries;

import Tree.BTree;

/**
 * Created by danial on 5/3/16.
 */
public class BtreeDictionary
{
    private BTree<String, WordProperties> bTree;

    public BtreeDictionary(int halfNodeSize)
    {
        this.bTree = new BTree<>(halfNodeSize);
    }

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = bTree.search(word);
        if(wordProperties == null)
        {
            wordProperties = new WordProperties(0);
            bTree.insert(word, wordProperties);
        }
        else
        {
            wordProperties.occurrences++;
            bTree.update(word, wordProperties);
        }
    }

    @Override
    public String toString()
    {
        return bTree.toString();
    }
}
