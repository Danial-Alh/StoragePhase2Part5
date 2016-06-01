package Tree;

class DataLocation<T>
{
    T node;
    int offset;

    public DataLocation(T node, int offset)
    {
        this.node = node;
        this.offset = offset;
    }
}