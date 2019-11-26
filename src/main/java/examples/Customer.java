package examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Customer implements Serializable {
  protected String name;
  protected String symbol; // search integer in string format
  protected int revenue;
  protected int SSN; // search int
  protected Collection<String> phoneNumbers;
//  private Person contact;
  protected Collection<Person> contacts;
  protected Page[] myHomePages;

  public Customer() {}

  public void addContact(Person contact) {
    this.contacts.add(contact);
  }

  public Customer(int idx) {
    this.name = Person.createName(idx);
    this.symbol = ""+idx;
    this.revenue = 1000 * idx;
    this.SSN = idx;
    this.phoneNumbers = new ArrayList<String>();
    this.phoneNumbers.add("503533"+(1000+idx));
    this.phoneNumbers.add("503534"+(1000+idx));
    this.contacts = Collections.singleton(new Person(idx));
    this.myHomePages = new Page[] { new Page(idx) };
//    this.contact = new Person(idx);
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

  public Collection<String> getPhoneNumbers() {
    return this.phoneNumbers;
  }

  public Collection<Person> getContacts() {
    return contacts;
  }

  public Page[] getMyHomePages() {
    return this.myHomePages;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Customer{");
    sb.append("name='").append(name).append('\'');
    sb.append("symbol='").append(symbol).append('\'');
    sb.append(", revenue=").append(revenue);
    sb.append(", SSN=").append(SSN);
    sb.append(", phoneNumbers=").append(phoneNumbers);
    sb.append(", contacts='").append(contacts).append('\'');
    sb.append('}');
    return sb.toString();
  }

}
