package sample;

public class Process {

    private String id;
    private double size;
    private int holeIndex;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Process()
    {
        id="";
        size=0;

    }

    public Process(String pname , int psize)
    {
        id=pname;
        size=psize;
    }

    public int getHoleIndex() {
        return holeIndex;
    }

    public void setHoleIndex(int holeIndex) {
        this.holeIndex = holeIndex;
    }
}
