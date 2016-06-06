package Tree.Nodes;

import FileManagement.RandomAccessFileManagement;
import Primitives.Parsable;
import Primitives.Sizeofable;
import Tree.ExtendedFileBtree;
import Tree.RamFileBtree;
import javafx.util.Pair;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class FileNode<Value extends Sizeofable & Parsable>
{
    protected final int KEY_MAX_SIZE, VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected final Class valueClassType;
    protected Vector<Pair<String, Value>> keyValPair;
    protected Vector<Long> child;
    protected Long parent, myPointer;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    protected int id;

    public FileNode(int key_max_size, int value_max_size, int halfMaxSize, Long parent, Class valueClassType)
    {
        KEY_MAX_SIZE = key_max_size;
        VALUE_MAX_SIZE = value_max_size;
        this.parent = parent;
        this.HALF_MAX_SIZE = halfMaxSize;
        this.valueClassType = valueClassType;
        this.MAX_SIZE = 2 * halfMaxSize - 1;
        this.id = RamFileBtree.getNewID();
        keyValPair = new Vector<>();
        child = new Vector<>();
    }

    public Vector<Pair<String, Value>> getKeyValPair()
    {
        return keyValPair;
    }

    public void setKeyValPair(Vector<Pair<String, Value>> keyValPair)
    {
        this.keyValPair = keyValPair;
    }

    public Vector<Long> getChild()
    {
        return child;
    }

    public void setChild(Vector<Long> child)
    {
        this.child = child;
    }

    public Long getParent()
    {
        return parent;
    }

    public void setParent(Long parent)
    {
        this.parent = parent;
    }

    public Long getMyPointer()
    {
        return myPointer;
    }

    public void setMyPointer(Long myPointer)
    {
        this.myPointer = myPointer;
    }

    public int getSize()
    {
        return keyValPair.size();
    }

    @Override
    public String toString()
    {
        String result = "<<id:" + id + ">>";
        for (Pair<String, Value> pair : keyValPair)
            result += "**" + pair.getKey().toString();
        result += "**";
        return result;
    }

    public String toString(FileNode<Value> parentNode)
    {
        String result = "<<id:" + id + ",ref:" + (parentNode == null ? -1 : parentNode.id) + ">>";
//        String result = "";
        for (Pair<String, Value> pair : keyValPair)
            result += "**" + pair.getKey().toString();
        result += "**";
        return result;
    }

    public int binarySearchForLocationToAdd(String key)
    {

        return getSize() == 0 ? 0 :
                binarySearchForLocationToAdd(key, 0, getSize() - 1);
    }

    protected int binarySearchForLocationToAdd(String key, int from, int to)
    {
        if (from > to)
            return -1;
        int mid = (from + to) / 2;
        int compareResult = key.compareTo(keyValPair.elementAt(mid).getKey());
        if (compareResult < 0)
        {
            int returnValue = binarySearchForLocationToAdd(key, from, mid - 1);
            return (returnValue == -1 ? from : returnValue);
        } else if (compareResult == 0)
            return mid;
        else
        {
            int returnValue = binarySearchForLocationToAdd(key, mid + 1, to);
            return (returnValue == -1 ? to + 1 : returnValue);
        }
    }


    protected int binarySearchForExistence(String key)
    {
        return binarySearchForExistence(key, 0, getSize() - 1);
    }


    protected int binarySearchForExistence(String key, int from, int to)
    {
        if (from > to)
            return -1;
        int mid = (from + to) / 2;
        int compareResult = key.compareTo(keyValPair.elementAt(mid).getKey());
        if (compareResult == 0)
            return mid;
        if (compareResult < 0)
            return binarySearchForExistence(key, from, mid - 1);
        else
            return binarySearchForExistence(key, mid + 1, to);
    }


    public void fetchNodeFromHard(Long myPointer)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        if (myPointer == null)
            try
            {
                instance.seek(instance.getChannel().size());
                this.myPointer = instance.getFilePointer();
//                    System.out.println("creating new node; pointer: " + this.myPointer);
                commitNewNodeOnFile();
                return;
            } catch (IOException e)
            {
                e.printStackTrace();
            }


        try
        {
//                System.out.println("fetching from hard, pointer: " + myPointer);
            if(myPointer < 0 || myPointer >= instance.getChannel().size())
                System.out.println("ohhhhh");
            this.myPointer = myPointer;
            instance.seek(myPointer);
            parent = instance.readLong();
            if(parent < -1)
                System.out.println("jjjjjjjjjjjjjjjjjjjjj1");
            if (parent == -1)
                parent = null;
            id = instance.readInt();
            if(id < 0)
                System.out.println("jjjjjjjjjjjjjjjjjjjjj2");
            int size = instance.readInt();
            if(size < 0)
                System.out.println("jjjjjjjjjjjjjjjjjjjjj3");
            for (int i = 0; i < size; i++)
            {
                int readSize = 0;
                readSize = instance.readInt();
                byte tempByteArray[] = new byte[readSize];
                instance.read(tempByteArray, 0, readSize);
                String key;
                key = new String(tempByteArray, "UTF-8");

                readSize = instance.readInt();
                tempByteArray = new byte[readSize];
                instance.read(tempByteArray);
                Value value = (Value) valueClassType.newInstance();
                value.parsefromByteArray(tempByteArray);
                if(!key.equalsIgnoreCase(value.toString()))
                    System.out.println("conflict");
                keyValPair.add(new Pair<>(key, value));
            }
            for (int i = 0; i <= size; i++)
            {
                long childPtr = instance.readLong();
                if(childPtr < -1)
                    System.out.println("jjjjjjjjjjjjjjjjjjjjj4");
                if(childPtr == -1)
                    child.add(null);
                else
                    child.add(childPtr);
            }
        } catch (IOException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void commitNewNodeOnFile()
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        try
        {
            instance.seek(myPointer);
//            instance.writeLong(parent == null ? -1 : parent);
            instance.writeLong(-1);
            instance.writeInt(id);
//            instance.writeInt(keyValPair.size());
            instance.writeInt(-1);
            for (int i = 0; i < MAX_SIZE; i++)
            {
                instance.writeInt(-1);
                instance.writeBytes(new String(new byte[KEY_MAX_SIZE], "UTF-8"));
                instance.writeInt(-1);
                instance.write(new byte[VALUE_MAX_SIZE]);
            }
            for (int i = 0; i <= MAX_SIZE; i++)
                instance.writeLong(-1);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void commitChanges()
    {
//            System.out.println("committing, pointer: " + myPointer);
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        try
        {
            instance.seek(myPointer);
            instance.writeLong(parent == null ? -1 : parent);
            instance.writeInt(id);
            instance.writeInt(keyValPair.size());
            for (int i = 0; i < MAX_SIZE; i++)
            {
                if (i < keyValPair.size())
                {
//                    int emptyBytes;
                    byte[] tempByteArray;
                    Pair<String, Value> tempKeyVal = keyValPair.elementAt(i);
                    tempByteArray = tempKeyVal.getKey().getBytes();
                    instance.writeInt(tempByteArray.length);
                    instance.write(tempByteArray);
//                    emptyBytes = KEY_MAX_SIZE - tempByteArray.length;
//                    instance.write(new byte[emptyBytes]);
//                    instance.writeBytes(new String(bytes, "UTF-8"));
                    tempByteArray = tempKeyVal.getValue().toByteArray();
                    instance.writeInt(tempByteArray.length);
                    instance.write(tempByteArray);
//                    emptyBytes = VALUE_MAX_SIZE - tempByteArray.length;
//                    instance.write(new byte[emptyBytes]);
                }
// else
//                {
//                    instance.writeBytes(new String(new byte[KEY_MAX_SIZE], "UTF-8"));
//                    int emptyBytes = VALUE_MAX_SIZE;
//                    instance.write(new byte[emptyBytes]);
//                }
            }
            for (int i = 0; i <= MAX_SIZE; i++)
            {
                if (i < child.size())
                {
                    Long tempChild = child.elementAt(i);
                    long childPointer = (tempChild == null ? -1 : tempChild);
                    instance.writeLong(childPointer);
                }
//                else
//                    instance.writeLong((long) -1);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
