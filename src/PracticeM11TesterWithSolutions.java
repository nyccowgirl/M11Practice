import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PracticeM11TesterWithSolutions {
	
	public static void main(String[] args) {
		List<Client> clientList = new ArrayList<Client>();
		fillList(clientList);
		// see a sample of the list
		for(int i=0; i<5; i++) {
			System.out.println(clientList.get(i));
		}
		
		// Query 1: What is the average age of all clients?
		double averageAge = clientList.stream()
				.mapToInt(Client::getAge)
				.average()
				.getAsDouble();
		System.out.println("\nAverage age is " + averageAge);
		
		// Query 2: Create a list of all female clients that are 18-25 years old.
		List<Client> female18to25List = clientList.stream()
				.filter(client -> client.getGender()==Gender.FEMALE)
				.filter(client -> client.getAge()>=18 && client.getAge()<=25)
				.collect(Collectors.toList());
		System.out.println("\nFemale clients 18-25 are: " + female18to25List);
		
		// Query 3: Are there any clients who haven't spent any money?
		// approach 1: check for an empty order list
		boolean anyNonSpenders1 = clientList.stream()
				.anyMatch(client -> client.getOrders().isEmpty());
		// approach 2: check no money being spent
		boolean anyNonSpenders2 = clientList.stream()
				.mapToDouble(client -> {
					return client.getOrders().stream()
					.mapToDouble(Order::getTotal)
					.sum();
					})
				.anyMatch(amount -> Math.abs(0-amount)<0.0001);
		System.out.println("\nAny non-spenders? " + anyNonSpenders1);
		System.out.println("Any non-spenders? " + anyNonSpenders2);

		// Query 4: Which client spent the most money?
		Client highestSpendingClient = clientList.stream()
				.max((c1, c2) -> {
					// here is the nested stream!
					double c1Sum = c1.getOrders().stream().mapToDouble(Order::getTotal).sum();
					double c2Sum = c2.getOrders().stream().mapToDouble(Order::getTotal).sum();
					return Double.compare(c1Sum, c2Sum);
				})
				.get();
		System.out.println("\nClient who spent the most money? " + highestSpendingClient);
		
		// Query 5: What is the average amount spent by all male clients?
		double averageMaleTotal = clientList.stream()
				.filter(client -> client.getGender()==Gender.MALE)
				.mapToDouble( client -> client.getOrders().stream().mapToDouble(Order::getTotal).sum())
				.average()
				.getAsDouble();
		System.out.println("\nAverage spent by all male clients? " + averageMaleTotal);

		// Query 6: Create a list of all addresses of clients in California. 
		List<Address> caAddressList = clientList.stream()
				.filter(client -> client.getAddress().getState().equals("CA"))
				.map(Client::getAddress)
				.collect(Collectors.toList());
		System.out.println("\nList of all CA addresses: " + caAddressList);

		
		// Query 7: Create a map of the clients in each state 
		// (key = state, value = list of clients in that state). 
		// Then, with a second statement, either a) print the last   
		// names of all of the clients in CA or b) print all 
		// states with more than 2 clients.
		Map<String, List<Client>> clientMapByState = clientList.stream()
				.collect(Collectors.groupingBy(client -> client.getAddress().getState()));
		//System.out.println(clientMapByState);
		// choice a:
		System.out.println("\nLast names of all CA clients:");
		clientMapByState.get("CA").stream().map(Client::getLastName).forEach(System.out::println);
		// choice b:
		System.out.println("\nStates with more than 2 clients:");
		clientMapByState.entrySet().stream()
				.filter( keyValuePair -> keyValuePair.getValue().size() > 2 )
				.forEach( keyValuePair -> System.out.println(keyValuePair.getKey()));
		
		// Query 8: Assume you are given the map from Query 7 (type Map<String, List<Client>>). 
		// Using that map, find the state with the most clients.
		
		// approach 1: use the keySet
		String stateWithMostClients = clientMapByState.keySet().stream()
                .max( (String state1, String state2) -> 
                	Integer.compare(
                		clientMapByState.get(state1).size(), 
                		clientMapByState.get(state2).size() )
                ).get();
		System.out.println("\nState with the most clients: " + stateWithMostClients);
		
		// approach 2: user the entrySet
		// note: this could be put into a single statement, but I broke it out into
		// two so that you could see the data type returned by the call to entrySet
		Set<Map.Entry<String,List<Client>>> clientMapByStateEntrySet = clientMapByState.entrySet();
		stateWithMostClients = clientMapByStateEntrySet.stream()
				.max( (keyValuePair1, keyValuePair2) -> 
					Integer.compare(
						keyValuePair1.getValue().size(), 
						keyValuePair2.getValue().size())
					)
				.get().getKey();
		System.out.println("State with the most clients: " + stateWithMostClients);
 		
		// Query 9: Create a map of the highest spending clients in each state 
		// (key = state, value = client). With a second statement, print the 
		// highest spending client in CA.
		Map<String, Client> highSpenderMap = clientList.stream() 

			// returns a Map<String, List<Client>> where key = state, value = list of clients
			.collect(Collectors.groupingBy(client -> client.getAddress().getState()))
					
			// returns a List<Client> that is then streamed
			.values().stream()

			// returns a Map<String, Client> where key = state, value = highest spender
			// reminder: we are dealing with a Stream<List<Client>>, so the elements
			// in our stream are lists!
			.collect(Collectors.toMap( 
					// key function goes from List<Client> to String; note that 
					// all clients on this list have the same state, so we can
					// get the state from any one of them
					(List<Client> clientListA) -> clientListA.get(0).getAddress().getState(),
						
					// value function goes from List<Client> to Client, where
					// that client is the biggest spender (max based on order total)
					(List<Client> clientListB) -> clientListB.stream().max(
							(client1, client2) -> {
								double c1Sum = client1.getOrders().stream().mapToDouble(Order::getTotal).sum();
								double c2Sum = client2.getOrders().stream().mapToDouble(Order::getTotal).sum();
								return Double.compare(c1Sum, c2Sum);
							}).get()
					));
		System.out.println("\nHighest spender in CA: " + highSpenderMap.get("CA"));
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
