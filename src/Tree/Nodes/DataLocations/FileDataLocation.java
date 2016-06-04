package Tree.Nodes.DataLocations;

import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.Nodes.FileNode;

public class FileDataLocation<Value extends Sizeofable & Parsable> extends DataLocation<FileNode<Value>>
{
    public FileDataLocation(FileNode<Value> node, int offset)
    {
        super(node, offset);
    }
}