package Dictionaries;

import Tree.BTree;

public class BtreeDictionary extends FileIndexableDictionary
{
    private BTree<String, WordProperties> bTree;

    public BtreeDictionary(int halfNodeSize, int keyMaxSize, int valueMaxSize, boolean useFileIndex)
    {
        super(halfNodeSize, keyMaxSize, valueMaxSize, useFileIndex);
        this.bTree = new BTree<>(halfNodeSize);
    }

    @Override
    protected void addItToRam(String word)
    {
        bTree.insert(word, new WordProperties(1));
    }

    @Override
    protected WordProperties searchDataOnRam(String word)
    {
        return bTree.search(word);
    }

    @Override
    public String toString()
    {
        return bTree.toString() + super.toString();
    }

    public BTree<String, WordProperties> getbTree()
    {
        return bTree;
    }
}
