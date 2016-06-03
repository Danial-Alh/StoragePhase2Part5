package Primitives;

import Primitives.Parsable;
import Primitives.Sizeofable;

import java.math.BigInteger;

public class WordProperties implements Sizeofable, Parsable
{
    int occurrences;

    public int getOccurrences()
    {
        return occurrences;
    }

    public void setOccurrences(int occurrences)
    {
        this.occurrences = occurrences;
    }

    public WordProperties(int occurrences)
    {
        this.occurrences = occurrences;
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


    public byte[] toByteArray()
    {
        BigInteger bigInteger = BigInteger.valueOf(occurrences);
        return bigInteger.toByteArray();
    }

    @Override
    public void parsefromByteArray(byte[] input)
    {
        BigInteger bigInteger = new BigInteger(input);
        occurrences = bigInteger.intValue();
    }
}
