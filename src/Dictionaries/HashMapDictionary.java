package Dictionaries;

import java.util.HashMap;

/**
 * Created by danial on 5/3/16.
 */
public class HashMapDictionary
{
    private HashMap<String, WordProperties> hashMap;

    public HashMapDictionary()
    {
        this.hashMap = new HashMap<>();
    }

    public HashMap getHashMap()
    {
        return hashMap;
    }

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = new WordProperties(0);
        wordProperties = hashMap.putIfAbsent(word, wordProperties);

        if(wordProperties != null)
        {
            wordProperties.occurrences++;
//            hashMap.update(word, wordProperties);
        }
    }

    @Override
    public String toString()
    {
        return hashMap.toString();
    }
}
