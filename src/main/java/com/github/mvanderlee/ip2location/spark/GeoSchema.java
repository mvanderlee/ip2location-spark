package com.github.mvanderlee.ip2location.spark;

import org.apache.spark.sql.types.*;

public class GeoSchema  {
  public GeoSchema() {}

  public static StructType schema = new StructType(
    new StructField[] {
      new StructField("ip",           DataTypes.StringType, false,  Metadata.empty()),
      new StructField("country_code", DataTypes.StringType, true,   Metadata.empty()),
      new StructField("country_name", DataTypes.StringType, true,   Metadata.empty()),
      new StructField("region_name",  DataTypes.StringType, true,   Metadata.empty()),
      new StructField("city_name",    DataTypes.StringType, true,   Metadata.empty()),
      new StructField("latitude",     DataTypes.FloatType,  true,   Metadata.empty()),
      new StructField("longitude",    DataTypes.FloatType,  true,   Metadata.empty()),
      new StructField("zip_code",     DataTypes.StringType, true,   Metadata.empty()),
      new StructField("timezone",     DataTypes.StringType, true,   Metadata.empty()),
    });
}
