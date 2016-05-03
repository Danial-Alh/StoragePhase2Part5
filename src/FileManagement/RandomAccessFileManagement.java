package FileManagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by danial on 5/1/16.
 */
public class RandomAccessFileManagement
{
    int sizeOf = 8;
    private String path;
    private static RandomAccessFile instance = null;
    private RandomAccessFileManagement()
    {
        path = "";
    }

    public static RandomAccessFile getInstance()
    {
        if(instance == null)
            try
            {
                instance = new RandomAccessFile("scores.html", "rw");
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        return instance;
    }

    public String read()
    {
        RandomAccessFile file = null;
        try
        {
            file = new RandomAccessFile("scores.html", "rw");
            for (int i = 0; i < 10; i++)
            {
                int temp = file.readInt();
                int addr = file.readInt();
                long current = file.getFilePointer();
                file.seek(addr);
                int temp2 = file.readInt();
                file.seek(current);
                System.out.println("what read: " + temp + "\tpointer: " + addr + "\twhat read from pointer: " + temp2);
            }

            file.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public void write(String string)
    {
        RandomAccessFile file = null;
        try
        {
            file = new RandomAccessFile("scores.html", "rw");
            for (int i = 0; i < 20; i++)
            {
                long current = file.getFilePointer();
                int addr = i < 10 ? (20 - i - 1) * sizeOf : -1;
                System.out.println("current i: " + i + "\tpointer: " + current + "\twritern pointer: " + addr);
                file.writeInt(i);
                file.writeInt(addr);
            }

            file.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void writeVal()
    {

    }
}
