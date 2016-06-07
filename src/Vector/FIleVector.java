package Vector;

import FileManagement.RandomAccessFileManagement;
import Primitives.Parsable;
import Primitives.Sizeofable;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FIleVector<Value extends Sizeofable & Parsable>
{
    private final int LONG_SIZE = 10;
    private final int INDEX_SIZE = 10;
    private final Long NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX = (long) (LONG_SIZE * INDEX_SIZE);
    private final Class valueClassType;

    public FIleVector(Class valueClassType)
    {
        this.valueClassType = valueClassType;
    }

    public Value elementAt(Long indexPtr, int offset)
    {
        if(indexPtr == -1 || indexPtr == null)
            return null;
        Long valuePtrOnFile = findValuePtrOnFile(indexPtr, offset, false);
        return readValueAt(valuePtrOnFile);
    }

    private Value readValueAt(Long valuePtrOnFile)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Value value = null;
        try
        {
            value = (Value) valueClassType.newInstance();
            byte tempByteArray[] = new byte[value.sizeof()];
            value.parsefromByteArray(tempByteArray);
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public Long writeElementAt(Long indexPtr, int offset,Value value)
    {
        if(indexPtr == null || indexPtr == -1)
            indexPtr =  createNewIndex();
        Long valuePtrOnFile = findValuePtrOnFile(indexPtr, offset, true);
        writeValueAt(valuePtrOnFile, value);
        return indexPtr;
    }

    private void writeValueAt(Long valuePtrOnFile, Value value)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        try
        {
            instance.seek(valuePtrOnFile);
            instance.write(value.toByteArray());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Long findValuePtrOnFile(Long indexPtr, int offset, boolean createNewIndexOnNotExist)
    {
        RandomAccessFile instance = RandomAccessFileManagement.getMyInstance();
        Long tempIndexPtr = indexPtr;
        while (true)
        {
            try
            {
                if(offset < INDEX_SIZE)
                    return tempIndexPtr + offset*LONG_SIZE;
                instance.seek(tempIndexPtr + NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX);
                Long tempNextIndexPtr = instance.readLong();
                if(tempNextIndexPtr == -1)
                {
                    tempNextIndexPtr = createNewIndex();
                    instance.seek(tempIndexPtr + NEXT_INDEX_Ptr_LOC_REL_TO_CURR_INDEX);
                    instance.writeLong(tempNextIndexPtr);
                }
                tempIndexPtr = tempNextIndexPtr;
                offset -= INDEX_SIZE;

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
            for(int i = 0; i <= INDEX_SIZE; i++)
                instance.writeLong(-1);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnAdd;
    }


}
