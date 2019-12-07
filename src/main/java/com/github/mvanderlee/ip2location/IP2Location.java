package com.github.mvanderlee.ip2location;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class IP2Location implements Serializable {

    private class Metadata implements Serializable {
        int databasetype;
        int databasecolumn;
        int databaseyear;
        int databasemonth;
        int databaseday;
        long ipv4databasecount;
        long ipv4databaseaddr;
        long ipv6databasecount;
        long ipv6databaseaddr;
        long ipv4indexbaseaddr;
        long ipv6indexbaseaddr;
        int ipv4columnsize;
        int ipv6columnsize;
    }

    private static final int[] COUNTRY_POSITION = {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    private static final int[] REGION_POSITION = {0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    private static final int[] CITY_POSITION = {0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
    private static final int[] ISP_POSITION = {0, 0, 3, 0, 5, 0, 7, 5, 7, 0, 8, 0, 9, 0, 9, 0, 9, 0, 9, 7, 9, 0, 9, 7, 9};
    private static final int[] LATITUDE_POSITION = {0, 0, 0, 0, 0, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
    private static final int[] LONGITUDE_POSITION = {0, 0, 0, 0, 0, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
    private static final int[] DOMAIN_POSITION = {0, 0, 0, 0, 0, 0, 0, 6, 8, 0, 9, 0, 10, 0, 10, 0, 10, 0, 10, 8, 10, 0, 10, 8, 10};
    private static final int[] ZIPCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 7, 0, 7, 7, 7, 0, 7, 0, 7, 7, 7, 0, 7};
    private static final int[] TIMEZONE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 7, 8, 8, 8, 7, 8, 0, 8, 8, 8, 0, 8};
    private static final int[] NETSPEED_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 11, 0, 11, 8, 11, 0, 11, 0, 11, 0, 11};
    private static final int[] IDDCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 12, 0, 12, 0, 12, 9, 12, 0, 12};
    private static final int[] AREACODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 13, 0, 13, 0, 13, 10, 13, 0, 13};
    private static final int[] WEATHERSTATIONCODE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 14, 0, 14, 0, 14, 0, 14};
    private static final int[] WEATHERSTATIONNAME_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 15, 0, 15, 0, 15, 0, 15};
    private static final int[] MCC_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 16, 0, 16, 9, 16};
    private static final int[] MNC_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 17, 0, 17, 10, 17};
    private static final int[] MOBILEBRAND_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 18, 0, 18, 11, 18};
    private static final int[] ELEVATION_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 19, 0, 19};
    private static final int[] USAGETYPE_POSITION = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 20};

    private IP2LocationSource fileHandle;
    private Metadata metadata;

    public static IP2Location create(String fileName, boolean cache) throws IOException {
        if (cache) {
            File file = new File(fileName);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return new IP2Location(new IP2LocationBuffer(fileContent));
        } else {
            return new IP2Location(new IP2LocationFile(fileName));
        }
    }

    public static IP2Location create(String fileName) throws IOException {
        return IP2Location.create(fileName, false);
    }

    public static IP2Location create(byte[] array) throws IOException {
        return new IP2Location(new IP2LocationBuffer(array));
    }


    public IP2Location(IP2LocationSource source) throws IOException {
        this.fileHandle = source;

        load();
    }


    public void load() throws IOException {
        metadata = new Metadata();
        metadata.databasetype = read8(1);
        metadata.databasecolumn = read8(2);
        metadata.databaseyear = read8(3);
        metadata.databasemonth = read8(4);
        metadata.databaseday = read8(5);
        metadata.ipv4databasecount = read32(6);
        metadata.ipv4databaseaddr = read32(10);
        metadata.ipv6databasecount = read32(14);
        metadata.ipv6databaseaddr = read32(18);
        metadata.ipv4indexbaseaddr = read32(22);
        metadata.ipv6indexbaseaddr = read32(26);
        metadata.ipv4columnsize = metadata.databasecolumn << 2;
        metadata.ipv6columnsize = 16 + ((metadata.databasecolumn - 1) << 2);
    }

    private BigInteger readip(long offset, boolean ip4) throws IOException {
        if (ip4) {
            return new BigInteger(Long.toUnsignedString(read32(offset)));
        }
        else {
            BigInteger a = new BigInteger(Long.toUnsignedString(read32(offset)));
            BigInteger b = new BigInteger(Long.toUnsignedString(read32(offset + 4)));
            BigInteger c = new BigInteger(Long.toUnsignedString(read32(offset + 8)));
            BigInteger d = new BigInteger(Long.toUnsignedString(read32(offset + 12)));

            return d.shiftLeft(96)
                .or(c.shiftLeft(64))
                .or(b.shiftLeft(32))
                .or(a);
        }
    }

    private int read8(long position) throws IOException {
        fileHandle.seek(position - 1);

        return fileHandle.read();
    }

    private long read32(long position) throws IOException {
        fileHandle.seek(position - 1);

        long[] data = new long[4];
        for (int i = 0; i < data.length; i++) {
            data[i] = fileHandle.read();
        }

        return data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0];
    }

    private float readFloat(long position) throws IOException {
        fileHandle.seek(position - 1);

        int[] data = new int[4];
        for (int i = 0; i < data.length; i++) {
            data[i] = fileHandle.read();
        }

        return Float.intBitsToFloat(data[3] << 24 | data[2] << 16 | data[1] << 8 | data[0]);
    }

    private String readString(long position) throws IOException {
        fileHandle.seek(position);

        int i = fileHandle.read();
        char[] data;

        try {
            data = new char[i];

            for (int j = 0; j < i; j++) {
                data[j] = ((char) fileHandle.read());
            }
        } catch (NegativeArraySizeException e) {
            return null;
        }

        return String.copyValueOf(data);
    }

    private void readRecord(long mid, boolean ip4, IP2LocationResult result) throws IOException {
        long off;
        long baseaddr;

        if (ip4) {
            off = 0;
            baseaddr = metadata.ipv4databaseaddr;
        }
        else {
            off = 12;
            baseaddr = metadata.ipv6databaseaddr;
        }

        long rowOffset = baseaddr + mid * (metadata.databasecolumn * 4 + off) + off;

        long offset;

        if (COUNTRY_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (COUNTRY_POSITION[metadata.databasetype] - 1));
            result.country_code = readString(offset);

            offset += 3;
            result.country_name = readString(offset);
        }

        if (REGION_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (REGION_POSITION[metadata.databasetype] - 1));
            result.region = readString(offset);
        }

        if (CITY_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (CITY_POSITION[metadata.databasetype] - 1));
            result.city = readString(offset);
        }

        if (ISP_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (ISP_POSITION[metadata.databasetype] - 1));
            result.isp = readString(offset);
        }

        if (LATITUDE_POSITION[metadata.databasetype] != 0) {
            offset = rowOffset + 4 * (LATITUDE_POSITION[metadata.databasetype] - 1);
            result.latitude = readFloat(offset);
        }

        if (LONGITUDE_POSITION[metadata.databasetype] != 0) {
            offset = rowOffset + 4 * (LONGITUDE_POSITION[metadata.databasetype] - 1);
            result.longitude = readFloat(offset);
        }

        if (DOMAIN_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (DOMAIN_POSITION[metadata.databasetype] - 1));
            result.domain = readString(offset);
        }

        if (ZIPCODE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (ZIPCODE_POSITION[metadata.databasetype] - 1));
            result.zipcode = readString(offset);
        }

        if (TIMEZONE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (TIMEZONE_POSITION[metadata.databasetype] - 1));
            result.timezone = readString(offset);
        }

        if (NETSPEED_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (NETSPEED_POSITION[metadata.databasetype] - 1));
            result.netspeed = readString(offset);
        }

        if (IDDCODE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (IDDCODE_POSITION[metadata.databasetype] - 1));
            result.iddcode = readString(offset);
        }

        if (AREACODE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (AREACODE_POSITION[metadata.databasetype] - 1));
            result.areacode = readString(offset);
        }

        if (WEATHERSTATIONCODE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (WEATHERSTATIONCODE_POSITION[metadata.databasetype] - 1));
            result.weatherstation_code = readString(offset);
        }

        if (WEATHERSTATIONNAME_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (WEATHERSTATIONNAME_POSITION[metadata.databasetype] - 1));
            result.weatherstation_name = readString(offset);
        }

        if (MCC_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (MCC_POSITION[metadata.databasetype] - 1));
            result.mcc = readString(offset);
        }

        if (MNC_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (MNC_POSITION[metadata.databasetype] - 1));
            result.mnc = readString(offset);
        }

        if (MOBILEBRAND_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (MOBILEBRAND_POSITION[metadata.databasetype] - 1));
            result.mobilebrand = readString(offset);
        }

        if (ELEVATION_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (ELEVATION_POSITION[metadata.databasetype] - 1));
            String data = readString(offset);

            if (data != null) {
                result.elevation = Float.parseFloat(data);
            }
        }

        if (USAGETYPE_POSITION[metadata.databasetype] != 0) {
            offset = read32(rowOffset + 4 * (USAGETYPE_POSITION[metadata.databasetype] - 1));
            result.usagetype = readString(offset);
        }
    }

    public IP2LocationResult query(String ip) throws IOException {
        IP2LocationResult result = new IP2LocationResult(ip);

        if (ip == null || ip.equals("")) {
            System.out.println("Failed: Null ip");
            return result;
        }

        boolean ip4;
        long off;
        long indexpos;
        long baseaddr;
        long low = 0;
        long high;
        long mid;
        BigInteger ipfrom;
        BigInteger ipto;
        BigInteger ipno;
        long[] ipLongs;

        try {
            ipLongs = IP2Long.IPToLong(ip);
            ip4 = ipLongs.length == 1;
        } catch (UnknownHostException e) {
            System.out.println("Failed: UnknownHostException IP2Long");
            return result;
        }

        if (ip4) {
            ipno = new BigInteger(Long.toUnsignedString(ipLongs[0]));

            off = 0;
            baseaddr = metadata.ipv4databaseaddr;
            high = metadata.ipv4databasecount;
            if (metadata.ipv4indexbaseaddr > 0) {
                indexpos = ipno.shiftRight(16).shiftLeft(3).longValue() + metadata.ipv4indexbaseaddr;
                low = read32(indexpos);
                high = read32(indexpos + 4);
            }
        }
        else {
            if (metadata.ipv6databasecount == 0) {
                throw new IOException("Please use IPv6 BIN file for IPv6 Addres");
            }
            ipno = new BigInteger(Long.toUnsignedString(ipLongs[0]))
                .shiftLeft(64)
                .or(new BigInteger(Long.toUnsignedString(ipLongs[1])));
            off = 12;
            baseaddr = metadata.ipv6databaseaddr;
            high = metadata.ipv6databasecount;
            if (metadata.ipv6indexbaseaddr > 0) {
                indexpos = ipno.shiftRight(112).shiftLeft(3).longValue() + metadata.ipv6indexbaseaddr;
                low = read32(indexpos);
                high = read32(indexpos + 4);
            }
        }

        while (low <= high) {
            mid = (int)((low + high) / 2);
            ipfrom = readip(baseaddr + mid * (metadata.databasecolumn * 4 + off), ip4);
            ipto = readip(baseaddr + (mid + 1) * (metadata.databasecolumn * 4 + off), ip4);

            if (ipfrom.compareTo(ipno) <=0 && ipno.compareTo(ipto) < 0) {
                readRecord(mid, ip4, result);

                break;
            }

            if (ipno.compareTo(ipfrom) < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return result;
    }

    public IP2LocationResult query(byte[] ip_bytes) throws IOException {
        String ip = InetAddress.getByAddress(ip_bytes).getHostAddress();
        try {
            return query(ip);
        }
        catch(Exception e) {
            System.out.println("FAILED: " + ip);
            throw e;
        }
    }
}
