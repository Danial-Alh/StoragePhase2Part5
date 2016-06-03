package Tree.Nodes.DataLocations;

import Tree.Nodes.RamFileNode;
import Primitives.Parsable;
import Primitives.Sizeofable;

public class RamDataLocation<Value extends Sizeofable & Parsable> extends DataLocation<RamFileNode<Value>>
{
    public RamDataLocation(RamFileNode<Value> node, int offset)
    {
        super(node, offset);
    }
}