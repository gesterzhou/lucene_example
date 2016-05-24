package examples;

import java.io.Serializable;

public class Person implements Serializable {
  private String name;
  private String email;
  private String address;

  public Person() {}

  public Person(String name, String email, String address) {
    this.name = name;
    this.email = email;
    this.address = address;
  }
  
  public Person(int idx, String title) {
    this.name = title+"Tom"+idx+" Zhou";
    this.email = "tzhou"+idx+"@example.com";
    this.address = ""+idx+" Lindon St, Portland_OR_"+(97000+idx);
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Person{");
    sb.append("name='").append(name).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", address='").append(address).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
