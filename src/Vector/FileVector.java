package Vector;

import FileManagement.RandomAccessFileManagement;
import Primitives.Parsable;
import Primitives.Sizeofable;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileVector<Value extends Sizeofable & Parsable>
{
    private final int INDEX_DATA_SIZE = 10;
    private final Long NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX = (long) (Long.BYTES * INDEX_DATA_SIZE);
    private final Class valueClassType;

    public FileVector(Class valueClassType)
    {
        this.valueClassType = valueClassType;
    }

    public Value elementAt(Long indexPtr, int offset)
    {
        if(indexPtr == -1 || indexPtr == null)
            return null;
        Long valuePtrOnFile = findValuePtrOnFile(indexPtr, offset);
        if(valuePtrOnFile == null)
            return null;
        return readValueAt(valuePtrOnFile);
    }

    public Long writeElementAt(Long indexPtr, int offset,Value value)
    {
        if(indexPtr == null || indexPtr == -1)
            indexPtr =  createNewIndex();
        Long valuePtrOnFile = createIndexAndFindValuePtrOnFile(indexPtr, offset);
        writeValueAt(valuePtrOnFile, value);
        return indexPtr;
    }

    private Long createIndexAndFindValuePtrOnFile(Long indexPtr, int offset)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Long tempIndexPtr = indexPtr;
        while (true)
        {
            try
            {
                if(offset < INDEX_DATA_SIZE)
                {
                    instance.seek(tempIndexPtr + offset*Long.BYTES);
                    Long resPtr = instance.readLong();
                    if(resPtr == -1)
                        resPtr = writeNewValueOnFile(tempIndexPtr + offset*Long.BYTES);
                    return resPtr;
                }

                instance.seek(tempIndexPtr + NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX);
                Long tempNextIndexPtr = instance.readLong();
                if(tempNextIndexPtr == -1)
                {
                    tempNextIndexPtr = createNewIndex();
                    instance.seek(tempIndexPtr + NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX);
                    instance.writeLong(tempNextIndexPtr);
                }
                tempIndexPtr = tempNextIndexPtr;
                offset -= INDEX_DATA_SIZE;

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Value readValueAt(Long valuePtrOnFile)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Value value = null;
        try
        {
            value = (Value) valueClassType.newInstance();
            if(valuePtrOnFile < 0)
                System.out.println("ljlskdjfl");
            instance.seek(valuePtrOnFile);
            int size = instance.readInt();
            byte tempByteArray[] = new byte[size];
            instance.read(tempByteArray);
            value.parseFromByteArray(tempByteArray);
        } catch (InstantiationException | IllegalAccessException | IOException e)
        {
            e.printStackTrace();
        }
        return value;
    }

    private void writeValueAt(Long valuePtrOnFile, Value value)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        try
        {
            instance.seek(valuePtrOnFile);
            byte[] bytes = value.toByteArray();
            instance.writeInt(bytes.length);
            instance.write(bytes);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Long writeNewValueOnFile(Long ptrInIndex)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Long resPtr = null;
        try
        {
            resPtr = instance.getChannel().size();
            instance.seek(ptrInIndex);
            instance.writeLong(resPtr);
            instance.seek(resPtr);
            instance.writeInt(-1);
            instance.write(new byte[((Value) valueClassType.newInstance()).sizeof()]);
        } catch (IOException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return resPtr;
    }

    private Long findValuePtrOnFile(Long indexPtr, int offset)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Long tempIndexPtr = indexPtr;
        while (true)
        {
            try
            {
                if(offset < INDEX_DATA_SIZE)
                {
                    instance.seek(tempIndexPtr + offset*Long.BYTES);
                    Long resPtr = instance.readLong();
                    return resPtr == -1 ? null : resPtr;
                }

                instance.seek(tempIndexPtr + NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX);
                Long tempNextIndexPtr = instance.readLong();
                if(tempNextIndexPtr == -1)
                    return null;
                tempIndexPtr = tempNextIndexPtr;
                offset -= INDEX_DATA_SIZE;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Long createNewIndex()
    {
        Long returnAdd = null;
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        try
        {
            instance.seek(instance.getChannel().size());
            returnAdd = instance.getFilePointer();
            for(int i = 0; i <= INDEX_DATA_SIZE; i++) // one more write for ptr at end to next index
                instance.writeLong(-1);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnAdd;
    }


}
