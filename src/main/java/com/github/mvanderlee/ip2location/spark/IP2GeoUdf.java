package com.github.mvanderlee.ip2location.spark;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.github.mvanderlee.ip2location.IP2Location;
import com.github.mvanderlee.ip2location.IP2LocationResult;
import com.github.mvanderlee.ip2location.spark.GeoRowBuilder;
import com.github.mvanderlee.ip2location.spark.GeoSchema;

/**
 * Not Memoized because the Garbage Collection time was greater than the Memoizing time saving
 */
public class IP2GeoUdf implements UDF1<byte[], Row> {
  private final IP2Location ip2geo;

  public IP2GeoUdf (
    Broadcast<byte[]> ip2geoBroadcast
  ) throws IOException {
    this.ip2geo = IP2Location.create(ip2geoBroadcast.getValue());
  }

  private Row getGeoRecordFromLocation(IP2LocationResult res) {
    return new GeoRowBuilder()
      .setIP(res.ip)
      .setCountryCode(res.country_code)
      .setCountry(res.country_name)
      .setState(res.region)
      .setCity(res.city)
      .setLatitude(res.latitude)
      .setLongitude(res.longitude)
      .setZipCode(res.zipcode)
      .setTimezone(res.timezone)
      .build();
  }

  private Row getGeoRecord(byte[] ip) {
    try {
      IP2LocationResult res = ip2geo.query(ip);
      return getGeoRecordFromLocation(res);
    } catch (Exception e) {
      e.printStackTrace();
      try {
        return new GeoRowBuilder().setIP(InetAddress.getByAddress(ip).getHostAddress()).build();
      } catch (UnknownHostException e2) {
        return new GeoRowBuilder().setIP("").build();
      }
    }
  }

  @Override
  public Row call(byte[] arg) throws IOException {
    return getGeoRecord(arg);
  }

  public static void initIP2Geo(SparkSession spark, JavaSparkContext sc, String path) throws IOException {
    byte[] fileContent = sc.binaryFiles(path).collect().get(0)._2.toArray();
    Broadcast<byte[]> ip2geoBroadcast = sc.broadcast(fileContent);

    spark.udf().register(
      "ip2geo",
      new IP2GeoUdf(ip2geoBroadcast),
      GeoSchema.schema
    );
  }
}
