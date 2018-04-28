package sample;

public class Hole {
    private String name;
    private double begin;
    private double size;
    private double LeftSpace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Hole()

    {
        name="";
        begin=0;
        size=0;
    }
    public Hole(String hname ,double beginaddress , double length)
    {
        name=hname;
        begin = beginaddress;
        size=length;
    }
    public int CompareToStartAddress(Hole x)
    {
        if(this.getBegin() < x.getBegin())
            return -1;
        else if(this.getBegin() == x.getBegin())
            return 0;
        else
            return 1;
    }

    public double getLeftSpace() {
        return LeftSpace;
    }

    public void setLeftSpace(double leftSpace) {
        LeftSpace = leftSpace;
    }
}
