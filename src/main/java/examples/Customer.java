package examples;

import java.io.Serializable;

public class Customer implements Serializable {
  private String name;
  private String symbol; // search integer in string format
  private int revenue;
  private int SSN; // search int
  private Person contact; // search nested object 

  public Customer() {}

  public Customer(String name, String symbol, int revenue, int ssn, Person contact) {
    this.name = name;
    this.symbol = symbol;
    this.revenue = revenue;
    this.SSN = ssn;
    this.contact = contact;
  }

  public Customer(int idx) {
    this.name = Person.createName(idx);
    this.symbol = ""+idx;
    this.revenue = 1000 * idx;
    this.SSN = idx;
    this.contact = new Person(idx);
  }

  public String getName() {
    return name;
  }
  
  public String getSymbol() {
    return symbol;
  }

  public double getRevenue() {
    return revenue;
  }

  public int getSSN() {
    return SSN;
  }

  public Person getContact() {
    return contact;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Customer{");
    sb.append("name='").append(name).append('\'');
    sb.append("symbol='").append(symbol).append('\'');
    sb.append(", revenue=").append(revenue);
    sb.append(", SSN=").append(SSN);
    sb.append(", contact='").append(contact).append('\'');
    sb.append('}');
    return sb.toString();
  }

}
