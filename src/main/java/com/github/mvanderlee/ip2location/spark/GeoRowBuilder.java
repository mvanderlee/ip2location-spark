package com.github.mvanderlee.ip2location.spark;

import java.io.Serializable;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * GeoRecord
 */
public class GeoRowBuilder implements Serializable {
  private String ip = null;
  private String countryCode = null;
  private String country = null;
  private String state = null;
  private String city = null;
  private Float latitude = null;
  private Float longitude = null;
  private String zipCode = null;
  private String timezone = null;
  private String officeId = null;
  private String description = null;


  public GeoRowBuilder() {}

  public GeoRowBuilder setIP(String ip) {
    this.ip = cleanValue(ip);
    return this;
  }

  public GeoRowBuilder setCountryCode(String countryCode) {
    this.countryCode = cleanValue(countryCode);
    return this;
  }

  public GeoRowBuilder setCountry(String country) {
    this.country = cleanValue(country);
    return this;
  }

  public GeoRowBuilder setState(String state) {
    this.state = cleanValue(state);
    return this;
  }

  public GeoRowBuilder setCity(String city) {
    this.city = cleanValue(city);
    return this;
  }

  public GeoRowBuilder setLatitude(Float latitude) {
    this.latitude = cleanValue(latitude);
    return this;
  }

  public GeoRowBuilder setLongitude(Float longitude) {
    this.longitude = cleanValue(longitude);
    return this;
  }

  public GeoRowBuilder setZipCode(String zipCode) {
    this.zipCode = cleanValue(zipCode);
    return this;
  }

  public GeoRowBuilder setTimezone(String timezone) {
    this.timezone = cleanValue(timezone);
    return this;
  }

  public GeoRowBuilder setOfficeId(String officeId) {
    this.officeId = cleanValue(officeId);
    return this;
  }

  public GeoRowBuilder setDescription(String description) {
    this.description = cleanValue(description);
    return this;
  }

  public Row build() {
    return RowFactory.create(
      ip,
      countryCode,
      country,
      state,
      city,
      latitude,
      longitude,
      zipCode,
      timezone,
      officeId,
      description
    );
  }

  private String cleanValue(String value) {
    if (value == null || value.equals("-")) {
      return null;
    }
    return value;
  }

  private Float cleanValue(Float value) {
    if (value == null || value == 0) {
      return null;
    }
    return value;

  }
}
