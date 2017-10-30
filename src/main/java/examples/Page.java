package examples;

import java.io.Serializable;

public class Page implements Serializable {
  private int id; // search integer in int format
  private String title;
  private String content;

  public Page() {}

  public Page(int id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  public Page(String title) {
    this.id = 0;
    this.title = "PivotalPage"+title;
    this.content = "Hello world no 0";
  }

  public Page(int idx) {
    this.id = idx;
    this.title = "PivotalPage"+idx;
    if (id % 10 == 0) {
      this.title += " manager";
    } else {
      this.title += " developer";
    }
    this.content = "Hello world no " + id;
  }

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Page{");
    sb.append("id=").append(id);
    sb.append(", title='").append(title).append('\'');
    sb.append(", content='").append(content).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
