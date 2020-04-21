package Models;

public class Damaged_part {
    String part_name;int cost;
    public Damaged_part(String part_name,int cost){
        this.part_name=part_name;
        this.cost=cost;
    }

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}
