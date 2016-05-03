package Tree;

/**
 * Created by danial on 5/2/16.
 */
public interface Parsable <T>
{
    byte[] toByteArray();

    void parsefromByteArray(byte[] input);
}
