package Dictionaries;

import Tree.FileBtree;

public abstract class FileIndexableDictionary
{
    private final static int MB = 1024*1024;
    private final boolean useFileIndex;
    protected FileBtree<WordProperties> fileBtree;

    public FileIndexableDictionary(int halfNodeSize, int keyMaxSize, int valueMaxSize, boolean useFileIndex)
    {
        this.useFileIndex = useFileIndex;
        if(useFileIndex)
            fileBtree = new FileBtree<>(keyMaxSize, valueMaxSize, halfNodeSize, WordProperties.class);
    }

    protected abstract void addItToRam(String word);
    protected abstract WordProperties searchDataOnRam(String word);

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = searchDataOnRam(word);
        if(wordProperties == null)
        {
            if(useFileIndex)
            {
                wordProperties = fileBtree.search(word);
                if (wordProperties == null)
                    addItToRamIfMemoryLimit(word);
                else
                    updateFileData(word, wordProperties);
            }
            else
                addItToRam(word);
        }
        else
            updateRamData(wordProperties);
    }

    private void updateFileData(String word, WordProperties wordProperties)
    {
        updateRamData(wordProperties);
        fileBtree.update(word, wordProperties);
    }

    protected void addItToRamIfMemoryLimit(String word)
    {
        if(memoryLimitExceeded())
            try
            {
                fileBtree.insert(word, new WordProperties(1));
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        else
            addItToRam(word);
    }

    protected boolean memoryLimitExceeded()
    {
        if(Runtime.getRuntime().freeMemory()/MB < 10)
            return true;
        return false;
    }

    protected void updateRamData(WordProperties wordProperties)
    {
        wordProperties.occurrences++;
//            bpTree.update(word, wordProperties);
    }

    @Override
    public String toString()
    {
        return "\n\n****FileBTree****\n\n" + fileBtree.toString();
    }

    public FileBtree<WordProperties> getFileBtree()
    {
        return fileBtree;
    }
}
