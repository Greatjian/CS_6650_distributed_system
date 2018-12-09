package edu.neu.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class MultiThread {

  private int maxThreadNum;
  private int userNumber;
  private int dayNumber;
  private int testNumber;
  private Client client;

  private int totalRequest = 0;
  private List<Long> latencyList = new ArrayList<>();
  private List<Long> requestList = new ArrayList<>();
  private List<List<Long>> localRequestList = new ArrayList<>();
  private List<List<Long>> localLatencyList = new ArrayList<>();

  private static final String[] PHASES = {"Warmup", "Loading", "Peak", "Cooldown"};
  private static final double[] PHASE_FACTOR = {0.1, 0.5, 1, 0.25};
  private static final int[][] INTERVALS = {{0, 2}, {3, 7}, {8, 18}, {19, 23}};


  public synchronized void incrementTotalRequest() {
    totalRequest++;
  }

  public void addLatency(List<Long> localLatencyList) {
    latencyList.addAll(localLatencyList);
  }

  public void addRequest(List<Long> localRequestList) {
    requestList.addAll(localRequestList);
  }

  public MultiThread(int maxThreadNum, int userNumber, int dayNumber, int testNumber) {
    this.maxThreadNum = maxThreadNum;
    this.userNumber = userNumber;
    this.dayNumber = dayNumber;
    this.testNumber = testNumber;
    this.client = ClientBuilder.newClient();
  }

  public void start(String url) {
    System.out.println("Deleting table...");
    StepClient stepClient = new StepClient(url, client);
    stepClient.deleteTable();
    System.out.println("Delete success!\n");

    Timestamp startWallTime = new Timestamp(System.currentTimeMillis());

    for (int i = 0; i < 4; i++) {

      int intervalStart = INTERVALS[i][0];
      int intervalEnd = INTERVALS[i][1];
      int numThreads = (int) (maxThreadNum * PHASE_FACTOR[i]);
      int iterations = this.testNumber * (intervalEnd - intervalStart + 1);
      final CountDownLatch latch = new CountDownLatch(numThreads);

      Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
      System.out.println(PHASES[i] + ": " + numThreads + " threads running ...\n");

      localRequestList.add(new ArrayList<>());
      localLatencyList.add(new ArrayList<>());
      List<Long> rList = localRequestList.get(i);
      List<Long> lList = localLatencyList.get(i);

      try {
        for (int j = 0; j < numThreads; j++) {
          new Thread(() -> {
            int[] users = new int[3];
            int[] intervals = new int[3];
            int[] stepCounts = new int[3];
            for (int l = 0; l < 3; l++) {
              users[l] = ThreadLocalRandom.current().nextInt(1, this.userNumber + 1);
              intervals[l] = ThreadLocalRandom.current().nextInt(intervalStart, intervalEnd + 1);
              stepCounts[l] = ThreadLocalRandom.current().nextInt(1, 10001);
            }

            for (int k = 0; k < iterations; k++) {
              incrementTotalRequest();
              Timestamp ts1 = new Timestamp(System.currentTimeMillis());
              stepClient.postStepCount(users[0], dayNumber, intervals[0], stepCounts[0]);
              Timestamp ts2 = new Timestamp(System.currentTimeMillis());
              rList.add(ts1.getTime() / 1000);
              lList.add(ts2.getTime() - ts1.getTime());

              incrementTotalRequest();
              Timestamp ts3 = new Timestamp(System.currentTimeMillis());
              stepClient.postStepCount(users[1], dayNumber, intervals[1], stepCounts[1]);
              Timestamp ts4 = new Timestamp(System.currentTimeMillis());
              rList.add(ts3.getTime() / 1000);
              lList.add(ts4.getTime() - ts3.getTime());

              incrementTotalRequest();
              Timestamp ts5 = new Timestamp(System.currentTimeMillis());
              stepClient.getCurrentDay(users[0]);
              Timestamp ts6 = new Timestamp(System.currentTimeMillis());
              rList.add(ts5.getTime() / 1000);
              lList.add(ts6.getTime() - ts5.getTime());

              incrementTotalRequest();
              Timestamp ts7 = new Timestamp(System.currentTimeMillis());
              stepClient.getSingleDay(users[1], dayNumber);
              Timestamp ts8 = new Timestamp(System.currentTimeMillis());
              rList.add(ts7.getTime() / 1000);
              lList.add(ts8.getTime() - ts7.getTime());

              incrementTotalRequest();
              Timestamp ts9 = new Timestamp(System.currentTimeMillis());
              stepClient.postStepCount(users[2], dayNumber, intervals[2], stepCounts[2]);
              Timestamp ts10 = new Timestamp(System.currentTimeMillis());
              rList.add(ts9.getTime() / 1000);
              lList.add(ts10.getTime() - ts9.getTime());
            }
            latch.countDown();
          }).start();
        }
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
      System.out.println(PHASES[i] + " complete: " +
          (endTimestamp.getTime() - startTimestamp.getTime()) / 1000.0 + " seconds\n");
    }

    Timestamp endWallTime = new Timestamp(System.currentTimeMillis());

    for (int i=0; i<4; i++){
      addLatency(localLatencyList.get(i));
      addRequest(localRequestList.get(i));
    }

    Result result = new Result(latencyList, requestList);
    result.writeToCSV();

    double totalWallTime = (endWallTime.getTime() - startWallTime.getTime()) / 1000.0;

    System.out.println("-------------------------------------------");
    System.out.println("Total run time " + totalWallTime + " seconds");
    System.out.println("Total number of requests sent: " + totalRequest);
    System.out.println("Mean latency: " + result.getMeanLatency() / 1000.0 + " seconds.");
    System.out.println("Median latency: " + result.getMedianLatency() / 1000.0 + " seconds.");
    System.out.println("95% latency: " + result.getNinetyFivePercentile() / 1000.0 + " seconds.");
    System.out.println("99% latency: " + result.getNinetyNinePercentile() / 1000.0 + " seconds.");
  }

}
