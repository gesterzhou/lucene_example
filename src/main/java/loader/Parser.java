package loader;

import java.text.ParseException;
import java.util.Optional;

/**
 * Given a type <T> and a String array parses the line into the <T>
 * @author wmarkito
 */
public interface Parser<T> {

  Optional<T> parseLine(String[] line) throws ParseException;

  boolean hasHeader();
}
