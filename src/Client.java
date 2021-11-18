import java.util.*;
import java.util.function.Function;

public class Client {

	private String firstName, lastName;
	private int age;
	private Gender gender;
	private Address address;
	private List<Order> orders;
	
	
	public Client(String firstName, String lastName, int age, Gender gender, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.address = address;
		orders = new ArrayList<Order>();
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public List<Order> getOrders() {
		return orders;
	}
	
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public void addOrder(Order order) {
		orders.add(order);
	}
	
	public String toString() {
		return firstName + lastName + "\tState=" + address.getState() + "\tTotal=" + orders.stream().mapToDouble(Order::getTotal).sum() + "\tOrder List: " + orders;
	}
	
}
