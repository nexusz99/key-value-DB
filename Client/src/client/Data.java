
package client;

public class Data {
    private String key;
    private String value;
    private int count;

    public Data(String key,String value)
    {
        this.key = key;
        this.value = value;
        this.count = 2;
    }

    public Data(String key)
    {
        this.key = key;
        this.count = 1;
    }

}
