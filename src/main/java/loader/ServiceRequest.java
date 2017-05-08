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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

//TODO: DataSerializable, PDX...
@XmlRootElement(name = "ServiceRequest")
public class ServiceRequest implements Serializable {

  public ServiceRequest setUniqueKey(final int uniqueKey) {
    this.uniqueKey = uniqueKey;
    return this;
  }

  public ServiceRequest setCreatedDate(final int createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public ServiceRequest setClosedDate(final int closedDate) {
    this.closedDate = closedDate;
    return this;
  }

  public ServiceRequest setAgency(final String agency) {
    this.agency = agency;
    return this;
  }

  public ServiceRequest setAgencyName(final String agencyName) {
    this.agencyName = agencyName;
    return this;
  }

  public ServiceRequest setComplaintType(final String complaintType) {
    this.complaintType = complaintType;
    return this;
  }

  public ServiceRequest  setDescriptor(final String descriptor) {
    this.descriptor = descriptor;
    return this;
  }

  public ServiceRequest  setLocationType(final String locationType) {
    this.locationType = locationType;
    return this;
  }

  public ServiceRequest  setIncidentZip(final String incidentZip) {
    this.incidentZip = incidentZip;
    return this;
  }

  public ServiceRequest  setIncidentAddress(final String incidentAddress) {
    this.incidentAddress = incidentAddress;
    return this;
  }

  public ServiceRequest  setStreetName(final String streetName) {
    this.streetName = streetName;
    return this;
  }

  public ServiceRequest  setCrossStreet_1(final String crossStreet_1) {
    this.crossStreet_1 = crossStreet_1;
    return this;
  }

  public ServiceRequest  setCrossStreet_2(final String crossStreet_2) {
    this.crossStreet_2 = crossStreet_2;
    return this;
  }

  public ServiceRequest  setIntersectionStreet_1(final String intersectionStreet_1) {
    this.intersectionStreet_1 = intersectionStreet_1;
    return this;
  }

  public ServiceRequest  setIntersectionStreet_2(final String intersectionStreet_2) {
    this.intersectionStreet_2 = intersectionStreet_2;
    return this;
  }

  public ServiceRequest  setAddressType(final String addressType) {
    this.addressType = addressType;
    return this;
  }

  public ServiceRequest  setCity(final String city) {
    this.city = city;
    return this;
  }

  public ServiceRequest  setLandmark(final String landmark) {
    this.landmark = landmark;
    return this;
  }

  public ServiceRequest  setFacilityType(final String facilityType) {
    this.facilityType = facilityType;
    return this;
  }

  public ServiceRequest  setStatus(final String status) {
    this.status = status;
    return this;
  }

  public ServiceRequest  setDueDate(final int dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  public ServiceRequest setResolutionDescription(final String resolutionDescription) {
    this.resolutionDescription = resolutionDescription;
    return this;
  }

  public ServiceRequest setResolutionActionUpdateDate(final int resolutionActionUpdateDate) {
    this.resolutionActionUpdateDate = resolutionActionUpdateDate;
    return this;
  }

  public ServiceRequest setCommunityBoard(final String communityBoard) {
    this.communityBoard = communityBoard;
    return this;
  }

  public ServiceRequest setBorough(final String borough) {
    this.borough = borough;
    return this;
  }

  public ServiceRequest setX_coordinate(final float x_coordinate) {
    this.x_coordinate = x_coordinate;
    return this;
  }

  public ServiceRequest setY_coordinate(final float y_coordinate) {
    this.y_coordinate = y_coordinate;
    return this;
  }

  public ServiceRequest setParkFacilityName(final String parkFacilityName) {
    this.parkFacilityName = parkFacilityName;
    return this;
  }

  public ServiceRequest setParkBorough(final String parkBorough) {
    this.parkBorough = parkBorough;
    return this;
  }

  public ServiceRequest setSchoolName(final String schoolName) {
    this.schoolName = schoolName;
    return this;
  }

  public ServiceRequest setSchoolNumber(final String schoolNumber) {
    this.schoolNumber = schoolNumber;
    return this;
  }

  public ServiceRequest setSchoolRegion(final String schoolRegion) {
    this.schoolRegion = schoolRegion;
    return this;
  }

  public ServiceRequest setSchoolCode(final String schoolCode) {
    this.schoolCode = schoolCode;
    return this;
  }

  public ServiceRequest setSchoolPhoneNumber(final String schoolPhoneNumber) {
    this.schoolPhoneNumber = schoolPhoneNumber;
    return this;
  }

  public ServiceRequest setSchoolAddress(final String schoolAddress) {
    this.schoolAddress = schoolAddress;
    return this;
  }

  public ServiceRequest setSchoolCity(final String schoolCity) {
    this.schoolCity = schoolCity;
    return this;
  }

  public ServiceRequest setSchoolState(final String schoolState) {
    this.schoolState = schoolState;
    return this;
  }

  public ServiceRequest setSchoolZip(final String schoolZip) {
    this.schoolZip = schoolZip;
    return this;
  }

  public ServiceRequest setSchoolNotFound(final String schoolNotFound) {
    this.schoolNotFound = schoolNotFound;
    return this;
  }

  public ServiceRequest setSchoolOrCityWideComplaint(final String schoolOrCityWideComplaint) {
    this.schoolOrCityWideComplaint = schoolOrCityWideComplaint;
    return this;
  }

  public ServiceRequest setVehicleType(final String vehicleType) {
    this.vehicleType = vehicleType;
    return this;
  }

  public ServiceRequest setTaxiCompanyBorough(final String taxiCompanyBorough) {
    this.taxiCompanyBorough = taxiCompanyBorough;
    return this;
  }

  public ServiceRequest setTaxiPickUpLocation(final String taxiPickUpLocation) {
    this.taxiPickUpLocation = taxiPickUpLocation;
    return this;
  }

  public ServiceRequest setBridgeHighwayName(final String bridgeHighwayName) {
    this.bridgeHighwayName = bridgeHighwayName;
    return this;
  }

  public ServiceRequest setBridgeHighwayDirection(final String bridgeHighwayDirection) {
    this.bridgeHighwayDirection = bridgeHighwayDirection;
    return this;
  }

  public ServiceRequest setRoadRamp(final String roadRamp) {
    this.roadRamp = roadRamp;
    return this;
  }

  public ServiceRequest setBridgeHighwaySegment(final String bridgeHighwaySegment) {
    this.bridgeHighwaySegment = bridgeHighwaySegment;
    return this;
  }

  public ServiceRequest setGarageLotName(final String garageLotName) {
    this.garageLotName = garageLotName;
    return this;
  }

  public ServiceRequest setFerryDirection(final String ferryDirection) {
    this.ferryDirection = ferryDirection;
    return this;
  }

  public ServiceRequest setFerryTerminalName(final String ferryTerminalName) {
    this.ferryTerminalName = ferryTerminalName;
    return this;
  }

  public ServiceRequest setLatitude(final float latitude) {
    this.latitude = latitude;
    return this;
  }

  public ServiceRequest setLongitude(final float longitude) {
    this.longitude = longitude;
    return this;
  }

  public ServiceRequest setLocation(final String location) {
    this.location = location;
    return this;
  }

  private int  uniqueKey;
  private int  createdDate;
  private int  closedDate;
  private String  agency,
          agencyName,
          complaintType,
          descriptor,
          locationType;
  private String  incidentZip;
  private String  incidentAddress,
          streetName,
          crossStreet_1,
          crossStreet_2,
          intersectionStreet_1,
          intersectionStreet_2,
          addressType,
          city,
          landmark,
          facilityType,
          status;
  private int    dueDate;
  private String  resolutionDescription;
  private int    resolutionActionUpdateDate;
  private String  communityBoard,
          borough;
  private float   x_coordinate, y_coordinate;
  private String  parkFacilityName,
          parkBorough,
          schoolName,
          schoolNumber,
          schoolRegion,
          schoolCode,
          schoolPhoneNumber,
          schoolAddress,
          schoolCity,
          schoolState,
          schoolZip,
          schoolNotFound,
          schoolOrCityWideComplaint,
          vehicleType,
          taxiCompanyBorough,
          taxiPickUpLocation,
          bridgeHighwayName,
          bridgeHighwayDirection,
          roadRamp,
          bridgeHighwaySegment,
          garageLotName,
          ferryDirection,
          ferryTerminalName;
  private float   latitude, longitude;

  private String  location;



  public int getUniqueKey() {
    return uniqueKey;
  }

  public int getCreatedDate() {
    return createdDate;
  }

  public int getClosedDate() {
    return closedDate;
  }

  public String getAgency() {
    return agency;
  }

  public String getAgencyName() {
    return agencyName;
  }

  public String getComplaintType() {
    return complaintType;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public String getLocationType() {
    return locationType;
  }

  public String getIncidentZip() {
    return incidentZip;
  }

  public String getIncidentAddress() {
    return incidentAddress;
  }

  public String getStreetName() {
    return streetName;
  }

  public String getCrossStreet_1() {
    return crossStreet_1;
  }

  public String getCrossStreet_2() {
    return crossStreet_2;
  }

  public String getIntersectionStreet_1() {
    return intersectionStreet_1;
  }

  public String getIntersectionStreet_2() {
    return intersectionStreet_2;
  }

  public String getAddressType() {
    return addressType;
  }

  public String getCity() {
    return city;
  }

  public String getLandmark() {
    return landmark;
  }

  public String getFacilityType() {
    return facilityType;
  }

  public String getStatus() {
    return status;
  }

  public int getDueDate() {
    return dueDate;
  }

  public String getResolutionDescription() {
    return resolutionDescription;
  }

  public int getResolutionActionUpdateDate() {
    return resolutionActionUpdateDate;
  }

  public String getCommunityBoard() {
    return communityBoard;
  }

  public String getBorough() {
    return borough;
  }

  public float getX_coordinate() {
    return x_coordinate;
  }

  public float getY_coordinate() {
    return y_coordinate;
  }

  public String getParkFacilityName() {
    return parkFacilityName;
  }

  public String getParkBorough() {
    return parkBorough;
  }

  public String getSchoolName() {
    return schoolName;
  }

  public String getSchoolNumber() {
    return schoolNumber;
  }

  public String getSchoolRegion() {
    return schoolRegion;
  }

  public String getSchoolCode() {
    return schoolCode;
  }

  public String getSchoolPhoneNumber() {
    return schoolPhoneNumber;
  }

  public String getSchoolAddress() {
    return schoolAddress;
  }

  public String getSchoolCity() {
    return schoolCity;
  }

  public String getSchoolState() {
    return schoolState;
  }

  public String getSchoolZip() {
    return schoolZip;
  }

  public String getSchoolNotFound() {
    return schoolNotFound;
  }

  public String getSchoolOrCityWideComplaint() {
    return schoolOrCityWideComplaint;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public String getTaxiCompanyBorough() {
    return taxiCompanyBorough;
  }

  public String getTaxiPickUpLocation() {
    return taxiPickUpLocation;
  }

  public String getBridgeHighwayName() {
    return bridgeHighwayName;
  }

  public String getBridgeHighwayDirection() {
    return bridgeHighwayDirection;
  }

  public String getRoadRamp() {
    return roadRamp;
  }

  public String getBridgeHighwaySegment() {
    return bridgeHighwaySegment;
  }

  public String getGarageLotName() {
    return garageLotName;
  }

  public String getFerryDirection() {
    return ferryDirection;
  }

  public String getFerryTerminalName() {
    return ferryTerminalName;
  }

  public float getLatitude() {
    return latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public String getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return "ServiceRequest{" + "uniqueKey=" + uniqueKey + ", createdDate=" + createdDate + ", closedDate=" + closedDate + ", agency='" + agency + '\'' + ", agencyName='" + agencyName + '\'' + ", complaintType='" + complaintType + '\'' + ", descriptor='" + descriptor + '\'' + ", locationType='" + locationType + '\'' + ", incidentZip='" + incidentZip + '\'' + ", incidentAddress='" + incidentAddress + '\'' + ", streetName='" + streetName + '\'' + ", crossStreet_1='" + crossStreet_1 + '\'' + ", crossStreet_2='" + crossStreet_2 + '\'' + ", intersectionStreet_1='" + intersectionStreet_1 + '\'' + ", intersectionStreet_2='" + intersectionStreet_2 + '\'' + ", addressType='" + addressType + '\'' + ", city='" + city + '\'' + ", landmark='" + landmark + '\'' + ", facilityType='" + facilityType + '\'' + ", status='" + status + '\'' + ", dueDate=" + dueDate + ", resolutionDescription='" + resolutionDescription + '\'' + ", resolutionActionUpdateDate=" + resolutionActionUpdateDate + ", communityBoard='" + communityBoard + '\'' + ", borough='" + borough + '\'' + ", x_coordinate=" + x_coordinate + ", y_coordinate=" + y_coordinate + ", parkFacilityName='" + parkFacilityName + '\'' + ", parkBorough='" + parkBorough + '\'' + ", schoolName='" + schoolName + '\'' + ", schoolNumber='" + schoolNumber + '\'' + ", schoolRegion='" + schoolRegion + '\'' + ", schoolCode='" + schoolCode + '\'' + ", schoolPhoneNumber='" + schoolPhoneNumber + '\'' + ", schoolAddress='" + schoolAddress + '\'' + ", schoolCity='" + schoolCity + '\'' + ", schoolState='" + schoolState + '\'' + ", schoolZip='" + schoolZip + '\'' + ", schoolNotFound='" + schoolNotFound + '\'' + ", schoolOrCityWideComplaint='" + schoolOrCityWideComplaint + '\'' + ", vehicleType='" + vehicleType + '\'' + ", taxiCompanyBorough='" + taxiCompanyBorough + '\'' + ", taxiPickUpLocation='" + taxiPickUpLocation + '\'' + ", bridgeHighwayName='" + bridgeHighwayName + '\'' + ", bridgeHighwayDirection='" + bridgeHighwayDirection + '\'' + ", roadRamp='" + roadRamp + '\'' + ", bridgeHighwaySegment='" + bridgeHighwaySegment + '\'' + ", garageLotName='" + garageLotName + '\'' + ", ferryDirection='" + ferryDirection + '\'' + ", ferryTerminalName='" + ferryTerminalName + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + ", location='" + location + '\'' + '}';
  }

  public ServiceRequest() {};

  public ServiceRequest(final int uniqueKey,
                        final int createdDate,
                        final int closedDate,
                        final String agency,
                        final String agencyName,
                        final String complaintType,
                        final String descriptor,
                        final String locationType,
                        final String incidentZip,
                        final String incidentAddress,
                        final String streetName,
                        final String crossStreet_1,
                        final String crossStreet_2,
                        final String intersectionStreet_1,
                        final String intersectionStreet_2,
                        final String addressType,
                        final String city,
                        final String landmark,
                        final String facilityType,
                        final String status,
                        final int dueDate,
                        final String resolutionDescription,
                        final int resolutionActionUpdateDate,
                        final String communityBoard,
                        final String borough,
                        final float x_coordinate,
                        final float y_coordinate,
                        final String parkFacilityName,
                        final String parkBorough,
                        final String schoolName,
                        final String schoolNumber,
                        final String schoolRegion,
                        final String schoolCode,
                        final String schoolPhoneNumber,
                        final String schoolAddress,
                        final String schoolCity,
                        final String schoolState,
                        final String schoolZip,
                        final String schoolNotFound,
                        final String schoolOrCityWideComplaint,
                        final String vehicleType,
                        final String taxiCompanyBorough,
                        final String taxiPickUpLocation,
                        final String bridgeHighwayName,
                        final String bridgeHighwayDirection,
                        final String roadRamp,
                        final String bridgeHighwaySegment,
                        final String garageLotName,
                        final String ferryDirection,
                        final String ferryTerminalName,
                        final float latitude,
                        final float longitude,
                        final String location) {
    this.uniqueKey = uniqueKey;
    this.createdDate = createdDate;
    this.closedDate = closedDate;
    this.agency = agency;
    this.agencyName = agencyName;
    this.complaintType = complaintType;
    this.descriptor = descriptor;
    this.locationType = locationType;
    this.incidentZip = incidentZip;
    this.incidentAddress = incidentAddress;
    this.streetName = streetName;
    this.crossStreet_1 = crossStreet_1;
    this.crossStreet_2 = crossStreet_2;
    this.intersectionStreet_1 = intersectionStreet_1;
    this.intersectionStreet_2 = intersectionStreet_2;
    this.addressType = addressType;
    this.city = city;
    this.landmark = landmark;
    this.facilityType = facilityType;
    this.status = status;
    this.dueDate = dueDate;
    this.resolutionDescription = resolutionDescription;
    this.resolutionActionUpdateDate = resolutionActionUpdateDate;
    this.communityBoard = communityBoard;
    this.borough = borough;
    this.x_coordinate = x_coordinate;
    this.y_coordinate = y_coordinate;
    this.parkFacilityName = parkFacilityName;
    this.parkBorough = parkBorough;
    this.schoolName = schoolName;
    this.schoolNumber = schoolNumber;
    this.schoolRegion = schoolRegion;
    this.schoolCode = schoolCode;
    this.schoolPhoneNumber = schoolPhoneNumber;
    this.schoolAddress = schoolAddress;
    this.schoolCity = schoolCity;
    this.schoolState = schoolState;
    this.schoolZip = schoolZip;
    this.schoolNotFound = schoolNotFound;
    this.schoolOrCityWideComplaint = schoolOrCityWideComplaint;
    this.vehicleType = vehicleType;
    this.taxiCompanyBorough = taxiCompanyBorough;
    this.taxiPickUpLocation = taxiPickUpLocation;
    this.bridgeHighwayName = bridgeHighwayName;
    this.bridgeHighwayDirection = bridgeHighwayDirection;
    this.roadRamp = roadRamp;
    this.bridgeHighwaySegment = bridgeHighwaySegment;
    this.garageLotName = garageLotName;
    this.ferryDirection = ferryDirection;
    this.ferryTerminalName = ferryTerminalName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.location = location;
  }
}


