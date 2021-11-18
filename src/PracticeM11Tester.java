import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PracticeM11Tester {
	
	public static void main(String[] args) {
		List<Client> clientList = new ArrayList<Client>();
		fillList(clientList);
		// see a sample of the list
		for(int i=0; i<5; i++) {
			System.out.println(clientList.get(i));
		}

		// Query 1 - What is average age of all clients?
		double average = clientList.stream()
				.mapToInt(Client::getAge)
				.average()
				.getAsDouble();
		System.out.println("Average age: " + average);

		// Query 2 - Create a list of all female clients that are 18-25 years old.
		List<Client> femaleList = clientList.stream()
				.filter(client -> client.getGender().equals(Gender.FEMALE))
				.filter(client -> client.getAge() >= 18 && client.getAge() <= 25)
				.collect(Collectors.toList());
		System.out.println("List of female clients from 18-25: " + femaleList);

		// Query 3 - Are there any clients who haven't spent any money?
		boolean noPurch = clientList.stream()
				.anyMatch(client -> client.getOrders().isEmpty());
		System.out.println("Any clients who haven't spent money: " + noPurch);

		// Query 4 - Which client spent the most money?
		Client bigSpender = clientList.stream()
				.max((c1, c2) -> {
					double c1Total = c1.getOrders().stream().mapToDouble(Order::getTotal).sum();
					double c2Total = c2.getOrders().stream().mapToDouble(Order::getTotal).sum();
					return Double.compare(c1Total, c2Total);
				})
				.get();
		System.out.println("Client who spent the most money: " + bigSpender.getFirstName() + bigSpender.getLastName());

		// Query 5 - What is the average amount spent by all male clients?
		double averageMaleTotal = clientList.stream()
				.filter(client -> client.getGender().equals(Gender.MALE))
				.mapToDouble(client -> client.getOrders().stream().mapToDouble(Order::getTotal).sum())
				.average()
				.getAsDouble();
		System.out.println("Total average spent by all male clients: " + averageMaleTotal);

		// Query 6 - Create a list of all addresses of clients in California.
		List<Address> addressesCAList = clientList.stream()
				.filter(client -> client.getAddress().getState().equals("CA"))
				.map(Client::getAddress)
				.collect(Collectors.toList());
		System.out.println("List of CA addresses: " + addressesCAList);

		// Query 7 - Create a map of the clients in each state (key = state, value = list of clients in that state).
		// Then with a second statement, either a) print the last names of all of the clients in CA or b) print all
		// states with more than 2 clients.
		Map<String, List<Client>> clientByState = clientList.stream()
				.collect(Collectors.groupingBy(client -> client.getAddress().getState()));
		System.out.println("Last names of all CA clients: ");
		clientByState.get("CA").stream().map(Client::getLastName).forEach(System.out::println);
		System.out.println("All states with more than 2 clients: ");
		clientByState.entrySet().stream()
				.filter(keyValuePair -> keyValuePair.getValue().size() > 2)
				.forEach(keyValuePair -> System.out.println(keyValuePair.getKey()));

		// Query 8 - Find the state with the most clients.
		String stateWithMostClients = clientByState.keySet().stream()
				.max((String s1, String s2) -> Integer.compare(clientByState.get(s1).size(), clientByState.get(s2).size()))
				.get();
		System.out.println("State with the most clients: " + stateWithMostClients);

		// Query 9 - Create a map of the highest spending clients in each state (key = state, value = client).
		// Using a second statement, print the highest spending client in CA.
		Map<String, Client> highestSpenderByState = clientList.stream()
				.collect(Collectors.groupingBy(client -> client.getAddress().getState()))
				.values().stream()
				.collect(Collectors.toMap(
						(List<Client> clientStateList) -> clientStateList.get(0).getAddress().getState(),
						(List<Client> highSpenderList) -> highSpenderList.stream()
								.max((c1, c2) -> {
									double c1Total = c1.getOrders().stream().mapToDouble(Order::getTotal).sum();
									double c2Total = c2.getOrders().stream().mapToDouble(Order::getTotal).sum();
									return Double.compare(c1Total, c2Total);
								}).get()
				));
		System.out.println("Highest spender in CA: " + highestSpenderByState.get("CA").getFirstName() +
				highestSpenderByState.get("CA").getLastName());
	}

	private static void fillList(List<Client> clientList) {
		try (Scanner clientFileScan = new Scanner(new FileReader(new File("ClientData.csv")));
				Scanner orderFileScan = new Scanner(new FileReader(new File("OrderData.csv")))) {

			/* create a list of orders */
			List<Order> orderList = new ArrayList<Order>();
			while (orderFileScan.hasNext()) {
				String orderLine = orderFileScan.nextLine();
				Scanner orderLineScan = new Scanner(orderLine);
				orderLineScan.useDelimiter(",");
				String allOrderString = orderLineScan.next();
				double total = Double.parseDouble(orderLineScan.next());
				
				List<String> itemList = new ArrayList<String>();
				Scanner allOrderStringScan = new Scanner(allOrderString);
				allOrderStringScan.useDelimiter(";");
				while (allOrderStringScan.hasNext()) {
					itemList.add(allOrderStringScan.next());
				}
				Order order = new Order(itemList, total);
				orderList.add(order);
			}
			int orderNum = 0;

			while (clientFileScan.hasNext()) {
				String clientLine = clientFileScan.nextLine();
				Scanner clientLineScan = new Scanner(clientLine);
				clientLineScan.useDelimiter(",");
				String firstName = clientLineScan.next();
				String lastName = clientLineScan.next();
				int age = Integer.parseInt(clientLineScan.next());
				String genderString = clientLineScan.next();
				Gender gender;
				if (genderString.equalsIgnoreCase("M")) {
					gender = Gender.MALE;
				} else if (genderString.equalsIgnoreCase("F")) {
					gender = Gender.FEMALE;
				} else {
					gender = Gender.OTHER_OR_UNSPECIFIED;
				}
				String streetNumber = clientLineScan.next();
				String street = clientLineScan.next();
				String city = clientLineScan.next();
				String state = clientLineScan.next();
				String zip = clientLineScan.next();
				int numOrders = Integer.parseInt(clientLineScan.next());
				Client c = new Client(firstName, lastName, age, gender,
						new Address(streetNumber, street, city, state, zip));
				clientList.add(c);
				for (int i = 0; i < numOrders; i++) {
					c.addOrder(orderList.get(orderNum));
					orderNum = (orderNum + 1) % orderList.size();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
