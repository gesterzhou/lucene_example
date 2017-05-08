/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import org.apache.geode.cache.Region;

/**
 * Load data into GemFire in batch and parallel
 * Should be generified in order to contribute back to Geode.
 */
public class Loader {
//  private static final String FILE_LOCATION = "/Users/gzhou/Downloads/311-sample.csv";
  private String[] fieldNames;

  public Loader(String fileLocation, Region region) throws FileNotFoundException, ParseException {
    ServiceRequestParser parser = new ServiceRequestParser();

    Scanner scanner = new Scanner(new File(fileLocation));
    int count = 0;
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      if (count == 0) {
        fieldNames = line.split(",");
      } else {
        line = convertCommaInString(line);
        String fields[] = line.split(",");
        System.out.println(Arrays.toString(fields));
        Optional<ServiceRequest> result = parser.parseLine(fields);
        region.put(result.get().getUniqueKey(), result.get());
//        region.put(Integer.toString(result.get().getUniqueKey()), result.get());
      }
      count++;
    }
    scanner.close();
    System.out.println("Loaded "+count+" entries");
  }
  
  private String convertCommaInString(String line) {
    char [] chars = line.toCharArray();
    boolean q = false;
    for (int i=0; i<chars.length; i++) {
      if (!q && chars[i]=='"') {
        q = true;
      } else if (q) {
        // found the first "
        if (chars[i]==',') {
          chars[i] = '.';
        } else if (chars[i]=='"') {
          q = false;
        }
      }
    }
    return String.valueOf(chars);
  }

}
