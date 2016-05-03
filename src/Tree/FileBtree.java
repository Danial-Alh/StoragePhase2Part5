package Tree;

import FileManagement.RandomAccessFileManagement;
import javafx.util.Pair;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class FileBtree <Value extends Sizeofable>
{
    private Long root;
    private int depth;
    private RamFileBtree ramParent;

    public FileBtree(BTree bTree, long keyMaxSize, long ValueMaxSize, RamFileBtree ramParent)
    {
        this.ramParent = ramParent;
    }



    class FileBtNode <Value extends Sizeofable & Parsable>
    {
        protected final Class valueClassType;
        protected Long myPointer, parent;
        private int size;
        protected final int NODE_HALF_MAX_SIZE, NODE_MAX_SIZE;
        protected final int KEY_MAX_SIZE, VALUE_MAX_SIZE;
        protected Vector<Pair<String, Value>> keyValPair;
        protected Vector<Long> child;

        FileBtNode(Long myPointer, int nodeHalfMaxSize, Class valueClassType, int keyMaxSize, int ValueMaxSize)
        {
            this.myPointer = myPointer;
            this.NODE_HALF_MAX_SIZE = nodeHalfMaxSize;
            this.NODE_MAX_SIZE = 2 * nodeHalfMaxSize - 1;
            this.KEY_MAX_SIZE = keyMaxSize;
            this.VALUE_MAX_SIZE = ValueMaxSize;
            this.valueClassType = valueClassType;
            keyValPair = new Vector<>();
            child = new Vector<>();
            fetchNodeFromHard();
        }

        private void fetchNodeFromHard()
        {
            RandomAccessFile instance = RandomAccessFileManagement.getInstance();
            if(myPointer == -1)
                try
                {
                    myPointer = instance.getFilePointer();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }


            try
            {
                instance.seek(myPointer);
                parent = instance.readLong();
                size = instance.readInt();
                for(int i = 0; i < size; i++)
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
                for(int i = 0; i <= size; i++)
                    child.add(instance.readLong());
            } catch (IOException | InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        private void commitChanges()
        {
            RandomAccessFile instance = RandomAccessFileManagement.getInstance();
            try
            {
                instance.seek(myPointer);
                instance.writeLong(parent);
                instance.writeInt(size);
                for(Pair<String, Value> tempKeyVal : keyValPair)
                {
                    instance.writeBytes(tempKeyVal.getKey());
                    byte tempByteArray[] = tempKeyVal.getValue().toByteArray();
                    int emptyBytes = VALUE_MAX_SIZE - tempByteArray.length;
                    instance.write(tempByteArray);
                    instance.write(new byte[emptyBytes]);
                }
                for(long childPointer : child)
                    instance.writeLong(childPointer);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        protected void insert(Pair<String, Value> newNode, long biggerChildPointer, long smallerChildPointer)
        {
            if (getSize() == 0)
            {
                keyValPair.add(newNode);
                child.add(smallerChildPointer);
                child.add(biggerChildPointer);
            } else
            {
                int location = binarySearchForLocationToAdd(newNode.getKey());
                if (location >= keyValPair.size())
                {
                    keyValPair.add(newNode);
                    child.add(biggerChildPointer);
                } else
                {
                    keyValPair.insertElementAt(newNode, location);
                    child.insertElementAt(biggerChildPointer, location + 1);
                }
                if (biggerChildPointer != -1)
                {
                    FileBtNode<Value> biggerChild = new FileBtNode<Value>(biggerChildPointer, NODE_HALF_MAX_SIZE, valueClassType, KEY_MAX_SIZE, VALUE_MAX_SIZE);
                    biggerChild.parent = myPointer;
                    biggerChild.commitChanges();
                }
                if (smallerChildPointer != -1)
                {
                    child.set(location, smallerChildPointer);
                    FileBtNode<Value> smallerChild = new FileBtNode<Value>(smallerChildPointer, NODE_HALF_MAX_SIZE, valueClassType, KEY_MAX_SIZE, VALUE_MAX_SIZE);
                    smallerChild.parent = myPointer;
                    smallerChild.commitChanges();
                }
                if (getSize() > NODE_MAX_SIZE)
                    splitCurrentNode();
            }
            if (child.size() != keyValPair.size() + 1)
                System.out.println("buuuuug");
        }

        private int binarySearchForLocationToAdd(String key)
        {
            return binarySearchForLocationToAdd(key, 0, keyValPair.size() - 1);
        }

        private int binarySearchForLocationToAdd(String key, int from, int to)
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

        protected FileBtNode[] splitCurrentNode()
        {
            FileBtNode newNode = new FileBtNode((long) -1, NODE_HALF_MAX_SIZE, valueClassType, KEY_MAX_SIZE, VALUE_MAX_SIZE);
            int victim = NODE_HALF_MAX_SIZE;
            int offset = victim + 1;
            moveDataToSiblingAndParent(newNode, offset);

            FileBtNode parentNode;
            Pair<String, Value> removedPair = keyValPair.remove(victim);
            if (parent == -1 && ramParent != null)
            {
                // toooooooooooooo false
                ramParent.insert(removedPair.getKey(), removedPair.getValue());
            }
            else if(parent == -1)
            {
                parentNode = new FileBtNode(parent, NODE_HALF_MAX_SIZE, valueClassType, KEY_MAX_SIZE, VALUE_MAX_SIZE);
                parent = parentNode.myPointer;
                root = parentNode.myPointer;
                newNode.parent = root;
                depth++;
                parentNode.insert(removedPair, newNode.myPointer, myPointer);
            }
            else
            {
                parentNode = new FileBtNode(parent, NODE_HALF_MAX_SIZE, valueClassType, KEY_MAX_SIZE, VALUE_MAX_SIZE);
                parentNode.insert(removedPair, newNode.myPointer, myPointer);
            }

            return null;
        }

        protected void moveDataToSiblingAndParent(FileBtNode newNode, int offset)
        {
            for (int i = offset; i <= NODE_MAX_SIZE; i++)
            {
                if (keyValPair.size() - 1 < offset || child.size() - 1 < offset)
                    System.out.println("ohhhh nooo");
                if (i == offset) // first node
                {
                    Long smallerChild = child.remove(offset), biggerChild = child.remove(offset);
                    newNode.insert(keyValPair.remove(offset)
                            , biggerChild, smallerChild);
                } else
                    newNode.insert(keyValPair.remove(offset), child.remove(offset), -1);
            }
        }

        public int getSize()
        {
            return size;
        }
    }
}