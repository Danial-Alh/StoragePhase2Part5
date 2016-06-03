package Tree;

public class RamDataLocation<Value extends Sizeofable & Parsable> extends DataLocation<RamFileNode<Value>>
{
    public RamDataLocation(RamFileNode<Value> node, int offset)
    {
        super(node, offset);
    }
}