
public class Address {
	
	private String streetNumber, street, city, state, zip;

	public Address(String streetNumber, String street, String city, String state, String zip) {
		super();
		this.streetNumber = streetNumber;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Override
	public String toString() {
		return streetNumber + " " + street + "   " + city + ", " + state + " " + zip;
	}

}
