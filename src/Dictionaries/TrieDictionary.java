package Dictionaries;

import Primitives.WordProperties;
import com.sun.org.apache.xml.internal.utils.Trie;

public class TrieDictionary extends FileIndexableDictionary
{
    private Trie trie;

    public TrieDictionary(int halfNodeSize, int keyMaxSize, int valueMaxSize, boolean useFileIndex)
    {
        super(halfNodeSize, keyMaxSize, valueMaxSize, useFileIndex);
        this.trie = new Trie();
    }

    public Trie getTrie()
    {
        return trie;
    }

    @Override
    protected void addItToRam(String word)
    {
        trie.put(word, new WordProperties(1));
    }

    @Override
    protected WordProperties searchDataOnRam(String word)
    {
        return (WordProperties) trie.get(word);
    }

    @Override
    public String toString()
    {
        return trie.toString() + super.toString();
    }
}
