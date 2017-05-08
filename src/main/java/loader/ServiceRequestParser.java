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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ServiceRequestParser implements Parser<ServiceRequest> {

  private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss a";

  protected int convertStringDateToInt(final String inputDate) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
    if (inputDate.isEmpty()) {
      return 0;
    }
    Date date = formatter.parse(inputDate);
    return (int)date.getTime();
  }

  public boolean hasHeader() { return true; }

  public Optional<ServiceRequest> parseLine(final String[] line) throws ParseException {
    if (line.length > 0) {
      ServiceRequest serviceRequest = new ServiceRequest();
      serviceRequest.setUniqueKey(Integer.parseInt(line[0]));
      serviceRequest.setCreatedDate(convertStringDateToInt(line[1]));
      serviceRequest.setClosedDate(convertStringDateToInt(line[2]));
      serviceRequest.setAgency(line[3]);
      serviceRequest.setAgencyName(line[4]);
      serviceRequest.setComplaintType(line[5]);
      serviceRequest.setDescriptor(line[6]);
      serviceRequest.setLocationType(line[7]);
      serviceRequest.setIncidentZip(line[8]);
      serviceRequest.setIncidentAddress(line[9]);
      serviceRequest.setStreetName(line[10]);
      serviceRequest.setCrossStreet_1(line[11]);
      serviceRequest.setCrossStreet_2(line[12]);
      serviceRequest.setIntersectionStreet_1(line[13]);
      serviceRequest.setIntersectionStreet_2(line[14]);
      serviceRequest.setAddressType(line[15]);
      serviceRequest.setCity(line[16]);
      serviceRequest.setLandmark(line[17]);
      serviceRequest.setFacilityType(line[18]);
      serviceRequest.setStatus(line[19]);
      serviceRequest.setDueDate(convertStringDateToInt(line[20]));
      serviceRequest.setResolutionDescription(line[21]);
      serviceRequest.setResolutionActionUpdateDate(convertStringDateToInt(line[22]));
      serviceRequest.setCommunityBoard(line[23]);
      serviceRequest.setBorough(line[24]);
      serviceRequest.setX_coordinate(Float.parseFloat(line[25]));
      serviceRequest.setY_coordinate(Float.parseFloat(line[26]));
      serviceRequest.setParkFacilityName(line[27]);
      serviceRequest.setParkBorough(line[28]);
      serviceRequest.setSchoolName(line[29]);
      serviceRequest.setSchoolNumber(line[30]);
      serviceRequest.setSchoolRegion(line[31]);
      serviceRequest.setSchoolCode(line[32]);
      serviceRequest.setSchoolPhoneNumber(line[33]);
      serviceRequest.setSchoolAddress(line[34]);
      serviceRequest.setSchoolCity(line[35]);
      serviceRequest.setSchoolState(line[36]);
      serviceRequest.setSchoolZip(line[37]);
      serviceRequest.setSchoolNotFound(line[38]);
      serviceRequest.setSchoolOrCityWideComplaint(line[39]);
      serviceRequest.setVehicleType(line[40]);
      serviceRequest.setTaxiCompanyBorough(line[41]);
      serviceRequest.setTaxiPickUpLocation(line[42]);
      serviceRequest.setBridgeHighwayName(line[43]);
      serviceRequest.setBridgeHighwayDirection(line[44]);
      serviceRequest.setRoadRamp(line[45]);
      serviceRequest.setBridgeHighwaySegment(line[46]);
      serviceRequest.setGarageLotName(line[47]);
      serviceRequest.setFerryDirection(line[48]);
      serviceRequest.setFerryTerminalName(line[49]);
      serviceRequest.setLatitude(Float.parseFloat(line[50]));
      serviceRequest.setLongitude(Float.parseFloat(line[51]));
      serviceRequest.setLocation(line[52]);

      return Optional.of(serviceRequest);

    } else {
      throw new IllegalArgumentException(String.format("Line does not contain minimal number of fields. Found {%d}. Required {%d}", line.length, 1));
    }
  }

}
