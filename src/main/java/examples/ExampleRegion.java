package examples;

import java.io.Serializable;

public class ExampleRegion implements Serializable {
  private ExampleIdentifier exampleIdentifier;
  public ExampleRegion() {}
  public ExampleRegion(ExampleIdentifier exampleIdentifier) {
    this.exampleIdentifier = exampleIdentifier;
  }
  public void setExampleIdentifier(ExampleIdentifier exampleIdentifier) {
    this.exampleIdentifier = exampleIdentifier;
  }
  public ExampleIdentifier getExampleIdentifier() {
    return this.exampleIdentifier;
  }
}
