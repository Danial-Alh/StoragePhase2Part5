package Dictionaries;

import Tree.BTree;
import com.sun.org.apache.xml.internal.utils.Trie;

/**
 * Created by danial on 5/3/16.
 */
public class TrieDictionary
{
    private Trie trie;

    public TrieDictionary()
    {
        this.trie = new Trie();
    }

    public Trie getTrie()
    {
        return trie;
    }

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = (WordProperties) trie.get(word);
        if(wordProperties == null)
        {
            wordProperties = new WordProperties(0);
            trie.put(word, wordProperties);
        }
        else
        {
            wordProperties.occurrences++;
//            trie.update(word, wordProperties);
        }
    }

    @Override
    public String toString()
    {
        return trie.toString();
    }
}
