package Primitives;

/**
 * Created by danial on 5/2/16.
 */
public interface Parsable
{
    byte[] toByteArray();

    void parseFromByteArray(byte[] input);
}
