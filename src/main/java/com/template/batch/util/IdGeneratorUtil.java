package com.template.batch.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorUtil {

  public static AtomicLong userSeq = new AtomicLong();
  public static AtomicLong jobSeq = new AtomicLong();

  public static final String PRE_FIX = "J";

  public static String generateJobId(String batchJobType) {
    StringBuffer sb = new StringBuffer();
    sb.append(batchJobType);
    sb.append(getIpAddress());
    sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    sb.append(idSeq(jobSeq, 4));
    return sb.toString();
  }


  public static String generateUserId() {
    StringBuffer sb = new StringBuffer();
    sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
    sb.append(idSeq(userSeq,4));
    return sb.toString();
  }

  private static String idSeq(AtomicLong atomicLong, int len) {
    Long expect = Long.parseLong("1" + String.format("%0" + len +"d", 0));
    String numStr = String.format("%0" + len +"d", atomicLong.getAndIncrement());
    atomicLong.compareAndSet(expect, 0);
    return numStr;
  }


  public static String getIpAddress()  {
    String hostAddress = null;
    try {
      hostAddress = Inet4Address.getLocalHost().getHostAddress();
      StringTokenizer st = new StringTokenizer(hostAddress, ".");
      String ipA = st.nextToken();
      String ipB = st.nextToken();
      String ipC = st.nextToken();
      String ipD = st.nextToken();

      return ipC+ipD;
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}
