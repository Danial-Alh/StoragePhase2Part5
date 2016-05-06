package Dictionaries;

import java.util.HashMap;

public class HashMapDictionary extends FileIndexableDictionary
{
    private HashMap<String, WordProperties> hashMap;


    public HashMapDictionary(int halfNodeSize, int keyMaxSize, int valueMaxSize, boolean useFileIndex)
    {
        super(halfNodeSize, keyMaxSize, valueMaxSize, useFileIndex);
        this.hashMap = new HashMap<>();
    }

    public HashMap getHashMap()
    {
        return hashMap;
    }

    @Override
    protected void addItToRam(String word)
    {
        hashMap.put(word, new WordProperties(1));
    }

    @Override
    protected WordProperties searchDataOnRam(String word)
    {
        return hashMap.get(word);
    }

    @Override
    public String toString()
    {
        return hashMap.toString() + super.toString();
    }
}
