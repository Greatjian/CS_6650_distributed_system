package edu.neu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MyClient implements Runnable {

  private Client client;
  private WebTarget webTarget;
  private String threadCount = "20"; //Default: 20
  private String iterations = "100"; //Default: 100
  private String ip = "35.161.211.30";
  private String port = "8080"; //Default: 8080

  public String getThreadCount() {
    return threadCount;
  }

  public String getIterations() {
    return iterations;
  }

  public String getIp() {
    return ip;
  }

  public String getPort() {
    return port;
  }

  public String buildUrl() {
    return "http://distributedsystem-env.z6x5t7jcu4.us-west-1.elasticbeanstalk.com/webapi/myresource";
  }

  public MyClient(Client client) {
    this.client = client;
  }

  public void setMyClient(String threadCount, String iterations, String ip, String port) {
    this.threadCount = threadCount;
    this.iterations = iterations;
    this.ip = ip;
    this.port = port;
  }

  public void assignWebTarget() {
    webTarget = client.target(buildUrl());
  }

  public <T> T postText(Object requestEntity, Class<T> responseType) throws ClientErrorException {
    return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN)
        .post(javax.ws.rs.client.Entity.entity(requestEntity,
            javax.ws.rs.core.MediaType.TEXT_PLAIN), responseType);
  }

  public String getStatus() throws ClientErrorException {
    WebTarget resource = webTarget;
    return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
  }

  @Override
  public void run() {
    for (int i = 0; i < Integer.parseInt(iterations); ++i) {
      // GET test
      LatencyResult.totalSentAddOne();
      long startTimeGet = System.currentTimeMillis();
      if (getStatus().equals("alive")) {
        LatencyResult.totalSuccessAddOne();
      }
      long endTimeGet = System.currentTimeMillis();
      LatencyResult.addLatency(endTimeGet - startTimeGet);

      // POST test
      long startTimePost = System.currentTimeMillis();
      LatencyResult.totalSentAddOne();
      if (Integer.parseInt(postText("Hello", String.class)) == ("Hello".length())) {
        LatencyResult.totalSuccessAddOne();
      }
      long endTimePost = System.currentTimeMillis();
      LatencyResult.addLatency(endTimePost - startTimePost);
    }
  }

  static class LatencyResult {

    private static AtomicInteger totalSent = new AtomicInteger(0);
    private static AtomicInteger totalSuccess = new AtomicInteger(0);
    private static CopyOnWriteArrayList<Long> latencyList = new CopyOnWriteArrayList<>();
    private static double mean;
    private static double median;

    public static double getMean() {
      return mean;
    }

    public static double getMedian() {
      return median;
    }

    public static double calculatePercentile(int val) {
      return latencyList.get((latencyList.size() / 100) * val);
    }

    public static void sortList() {
      Object[] arr = latencyList.toArray();
      Arrays.sort(arr);
      for (int i = 0; i < latencyList.size(); ++i) {
        latencyList.set(i, (Long) arr[i]);
      }
    }

    public static void computeMean() {
      if (latencyList == null || latencyList.size() == 0) {
        throw new NoSuchElementException("Mean = 0");
      } else {
        mean = latencyList.stream().mapToLong(a -> a).average()
            .orElseThrow(IllegalStateException::new);
      }
    }

    public static void computeMedian() {
      if (latencyList == null || latencyList.size() == 0) {
        return;
      }
      sortList();
      int size = latencyList.size();
      if (size % 2 == 0) {
        median = latencyList.get(size / 2);
      } else {
        median = (latencyList.get(size / 2) + latencyList.get((size / 2) + 1)) / 2;
      }
    }

    public static void totalSentAddOne() {
      totalSent.addAndGet(1);
    }

    public static void totalSuccessAddOne() {
      totalSuccess.addAndGet(1);
    }

    public static int getTotalSent() {
      return totalSent.get();
    }

    public static int getTotalSuccess() {
      return totalSuccess.get();
    }

    public static void addLatency(long latency) {
      latencyList.add(latency);
    }
  }
}
