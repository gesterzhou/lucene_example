package examples;

import java.io.Serializable;

public class Page implements Serializable {
  private int id; // search integer in int format
  private String title;
  private String content;
  final String desc = "At client and server JVM, initializing cache will create the LuceneServiceImpl object," 
     +" which is a singleton at each JVM."; 

  public Page() {}

  public Page(int id, String title, String content) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  public Page(int idx) {
    this.id = idx;
    this.title = "Pivotal Page:"+idx;
    this.content = desc;
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
