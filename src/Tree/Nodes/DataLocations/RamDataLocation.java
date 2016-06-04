package Tree.Nodes.DataLocations;

import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.Nodes.RamFileNode;

public class RamDataLocation<Value extends Sizeofable & Parsable> extends DataLocation<RamFileNode<Value>>
{
    public RamDataLocation(RamFileNode<Value> node, int offset)
    {
        super(node, offset);
    }
}