package examples;

import java.io.Serializable;

public class SystemId implements Serializable {
  private String id;
  private String system;
  public SystemId() {}
  SystemId(String id, String system) {
    this.id = id;
    this.system = system;
  }
  public void setId(String id) {
    this.id = id;
  }
  public void setSystem(String system) {
    this.system = system;
  }
  public String getId() {
    return this.id;
  }
  public String getSystem() {
    return this.system;
  }
}
