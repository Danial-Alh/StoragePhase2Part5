package Dictionaries;


import Tree.FileBtree;

/**
 * Created by danial on 5/6/16.
 */
public class FileBtreeDictionary
{
    public FileBtree<WordProperties> getFileBtree()
{
    return fileBtree;
}

    private FileBtree<WordProperties> fileBtree;

    public FileBtreeDictionary(int halfNodeSize)
    {
        this.fileBtree = new FileBtree<>(10, 4, halfNodeSize, WordProperties.class);
    }

    public void insertNodeIfnotExists(String word)
    {
        WordProperties wordProperties = fileBtree.search(word);
        if(wordProperties == null)
        {
            wordProperties = new WordProperties(0);
            try
            {
                fileBtree.insert(word, wordProperties);
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
        else
        {
            wordProperties.occurrences++;
//            fileBtree.update(word, wordProperties);
        }
    }

    @Override
    public String toString()
    {
        return fileBtree.toString();
    }
}
