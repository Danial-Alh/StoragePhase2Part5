package Tree;

/**
 * Created by danial on 5/3/16.
 */
public class RamFileBtree <Value> extends BTree<String, Value>
{
    public RamFileBtree(int halfMaxSize)
    {
        super(halfMaxSize);
    }
}
