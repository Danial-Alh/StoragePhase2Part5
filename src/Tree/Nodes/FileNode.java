package Tree.Nodes;

import FileManagement.RandomAccessFileManagement;
import Primitives.Parsable;
import Primitives.Sizeofable;
import javafx.util.Pair;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class FileNode <Value extends Sizeofable & Parsable>
{
    protected Vector<Pair<String, Value>> keyValPair;
    protected final int KEY_MAX_SIZE, VALUE_MAX_SIZE;
    protected final int HALF_MAX_SIZE, MAX_SIZE;
    protected Vector<Long> child;
    protected Long parent, myPointer;
    protected final Class valueClassType;
    protected int id;

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

    public void setMyPointer(Long myPointer)
    {
        this.myPointer = myPointer;
    }

    public FileNode(int key_max_size, int value_max_size, int halfMaxSize, Long parent, Class valueClassType)
    {
        KEY_MAX_SIZE = key_max_size;
        VALUE_MAX_SIZE = value_max_size;
        this.parent = parent;
        this.HALF_MAX_SIZE = halfMaxSize;
        this.valueClassType = valueClassType;
        this.MAX_SIZE = 2 * halfMaxSize - 1;
//        this.id = ++idCounter;
        this.id = 0;
        keyValPair = new Vector<>();
        child = new Vector<>();
    }

    public Long getMyPointer()
    {
        return myPointer;
    }


    public int getSize()
    {
        return keyValPair.size();
    }


    public String toString()
    {
//        NodeTemplate parentNodeTemplate = getNode(parent);
//        String result = "<<id:" + id + ",ref:" + (parentNodeTemplate == null ? -1 : parentNodeTemplate.id) + ">>";
        String result = "";
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
                commitChanges();
                return;
            } catch (IOException e)
            {
                e.printStackTrace();
            }


        try
        {
//                System.out.println("fetching from hard, pointer: " + myPointer);
            this.myPointer = myPointer;
            instance.seek(myPointer);
            parent = instance.readLong();
            if(parent == -1)
                parent = null;
            int size = instance.readInt();
            for (int i = 0; i < size; i++)
            {
                byte tempByteArray[] = new byte[KEY_MAX_SIZE];
                instance.read(tempByteArray, 0, KEY_MAX_SIZE);
                String key = new String(tempByteArray);

                tempByteArray = new byte[VALUE_MAX_SIZE];
                instance.read(tempByteArray);
                Value value = (Value) valueClassType.newInstance();
                value.parsefromByteArray(tempByteArray);

                keyValPair.add(new Pair<String, Value>(key, value));
            }
            for (int i = 0; i <= size; i++)
                child.add(instance.readLong());
        } catch (IOException | InstantiationException | IllegalAccessException e)
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
            instance.writeInt(keyValPair.size());
            for (int i = 0; i < MAX_SIZE; i++)
            {
                if(i < keyValPair.size())
                {
                    Pair<String, Value> tempKeyVal = keyValPair.elementAt(i);
                    instance.writeBytes(tempKeyVal.getKey());
                    byte tempByteArray[] = tempKeyVal.getValue().toByteArray();
                    int emptyBytes = VALUE_MAX_SIZE - tempByteArray.length;
                    instance.write(tempByteArray);
                    instance.write(new byte[emptyBytes]);
                }
                else
                {
                    instance.writeBytes(new String(new byte[KEY_MAX_SIZE], "UTF-8"));
                    int emptyBytes = VALUE_MAX_SIZE;
                    instance.write(new byte[emptyBytes]);
                }
            }
            for (int i = 0; i <= MAX_SIZE; i++)
            {
                if(i < child.size())
                {
                    Long tempChild = child.elementAt(i);
                    long childPointer = (tempChild == null ? -1 : tempChild);
                    instance.writeLong(childPointer);
                }
                else
                    instance.writeLong((long) -1);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
