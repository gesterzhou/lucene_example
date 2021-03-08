package examples;

import java.io.Serializable;

public class ExampleIdentifier implements Serializable {
  private String exampleId;
  private SystemId[] systemIds;
  public ExampleIdentifier() {}
  public ExampleIdentifier(String exampleId, SystemId[] systemIds) {
    this.exampleId = exampleId;
    this.systemIds = systemIds;
  }
  public void setExampleId(String exampleId) {
    this.exampleId = exampleId;
  }
  public void setSystemIds(SystemId[] systemIds) {
    this.systemIds = systemIds;
  }
  public String getExampleId() {
    return this.exampleId;
  }
  public SystemId[] getSystemIds() {
    return this.systemIds;
  }
}
