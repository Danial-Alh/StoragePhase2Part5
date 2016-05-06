package Dictionaries;

import Tree.BPTree;

public class BptreeDictionary extends FileIndexableDictionary
{
    private BPTree<String, WordProperties> bpTree;

    public BptreeDictionary(int halfNodeSize, int keyMaxSize, int valueMaxSize, boolean useFileIndex)
    {
        super(halfNodeSize, keyMaxSize, valueMaxSize, useFileIndex);
        this.bpTree = new BPTree<>(halfNodeSize);
    }

    @Override
    protected void addItToRam(String word)
    {
        bpTree.insert(word, new WordProperties(1));
    }

    @Override
    protected WordProperties searchDataOnRam(String word)
    {
        return bpTree.search(word);
    }

    @Override
    public String toString()
    {
        return bpTree.toString() + super.toString();
    }

    public BPTree<String, WordProperties> getBpTree()
    {
        return bpTree;
    }
}
