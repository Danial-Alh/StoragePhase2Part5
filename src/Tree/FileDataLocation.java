package Tree;

public class FileDataLocation<Value extends Sizeofable & Parsable> extends DataLocation<FileNode<Value>>
{
    public FileDataLocation(FileNode<Value> node, int offset)
    {
        super(node, offset);
    }
}