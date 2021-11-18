import java.util.List;

public class Order {

	private List<String> items;
	private double total;
	public Order(List<String> items, double total) {

		this.items = items;
		this.total = total;
	}
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	@Override
	public String toString() {
		return items.size() + " items for $" + total;
	}
	
}
