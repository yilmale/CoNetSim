package coNetSim;

class MyLink {
    public double capacity; // should be private 
    public double weight;   // should be private for good practice
    int id;
    public MyLink(double weight, int id) {
        this.id = id; // This is defined in the outer class.
        this.weight = weight;
    } 
    public String toString() { // Always good for debugging
        //return "E"+id;
    	return "";
    }
}