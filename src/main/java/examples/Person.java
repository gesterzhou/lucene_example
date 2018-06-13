package examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Person implements Serializable {
  private String name;
  private String email;
  private int revenue;
  private String address;
  private String[] phoneNumbers;
  private Page homepage;

  public Person() {}

  public Person(String name, String email, String address) {
    this.name = name;
    this.email = email;
    this.address = address;
    this.homepage = new Page(name);
  }

  public Person(int idx) {
    this.name = createName(idx);
    this.email = "tzhou"+idx+"@example.com";
    this.address = ""+idx+" Lindon St, Portland_OR_"+(97000+idx);
    this.revenue = idx*1000;
    this.homepage = new Page(idx);
    this.phoneNumbers = new String[] { "503633"+(1000+idx), "503634"+(1000+idx)};
  }

  static String createName(int idx) {
    return "Tom"+idx+" Zhou";
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
    sb.append(", revenue=").append(revenue);
    sb.append(", homepage='").append(homepage).append('\'');
    sb.append(", phoneNumbers='").append(Arrays.toString(phoneNumbers)).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
