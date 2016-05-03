package Dictionaries;

import Tree.Parsable;
import Tree.Sizeofable;

/**
 * Created by danial on 5/3/16.
 */
public class WordProperties implements Sizeofable, Parsable<WordProperties>
{
    int occurrences;

    public WordProperties(int occurrences)
    {
        this.occurrences = occurrences;
    }

    @Override
    public byte[] toByteArray()
    {
        return new byte[0];
    }

    @Override
    public void parsefromByteArray(byte[] input)
    {

    }

    @Override
    public int sizeof()
    {
        return 4;
    }

    @Override
    public String toString()
    {
        return String.valueOf(occurrences);
    }
}
