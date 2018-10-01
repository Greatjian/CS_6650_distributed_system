package edu.neu.client;

import edu.neu.client.MyClient.LatencyResult;
import javax.ws.rs.client.ClientBuilder;
import java.util.ArrayList;
import java.util.List;

public class TestMyClient {

  private List<Thread> listOfThreads = new ArrayList<>();
  private final static double[] PHASE_CONSTANT = {0.1, 0.5, 1, 0.25};
  private final static String[] PHASES = {"Warmup", "Loading", "Peak", "Cooldown"};
  private long startTime;
  private long endTime;
  private long processingTime;

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getProcessingTime() {
    return processingTime;
  }

  public void setProcessingTime(long processingTime) {
    this.processingTime = processingTime;
  }

  private void buildThreads(MyClient myClient, int threadCount) {
    for (int i = 0; i < threadCount; ++i) {
      Thread t = new Thread(myClient);
      listOfThreads.add(t);
    }
  }

  private void joinThreads() {
    for (Thread t : listOfThreads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void startThreads() {
    for (Thread t : listOfThreads) {
      t.start();
    }
  }

  private long calculateWallTime() {
    return getEndTime() - getStartTime();
  }


  private void getLatencyPercentile(int percent) {
    System.out.println(
        percent + "% latency: " + LatencyResult.calculatePercentile(percent));
  }

  private void printPhaseInfo(MyClient myClient) {
    System.out.println("All threads running");
    System.out.println("processing time: " + getProcessingTime()); // processing time
    System.out.println("");
  }

  private void printToConsole(MyClient myClient) {
    System.out.println("Threads: " + myClient.getThreadCount()); // threads
    System.out.println("Iterations: " + myClient.getIterations()); // iterations
    System.out.println(
        "Total number of requests sent: " + LatencyResult.getTotalSent()); // total requests
    System.out
        .println("Total number of successful responses: " + LatencyResult
            .getTotalSuccess()); // successful requests
    System.out
        .println("Mean latency for all requests: " + LatencyResult.getMean()); // mean latency
    System.out.println(
        "Median latency for all requests: " + LatencyResult.getMedian()); // median latency
    getLatencyPercentile(95); // 95% latency
    getLatencyPercentile(99); // 99% latency
  }

  public static void main(String[] args) {

    long start = System.currentTimeMillis();

    for (int i = 0; i < PHASE_CONSTANT.length; i++) {
      System.out.printf(PHASES[i] + " phase:");
      TestMyClient testMyClient = new TestMyClient();
      testMyClient.setStartTime(System.currentTimeMillis());
      MyClient myClient = new MyClient(ClientBuilder.newClient());
      if (args.length == 4) {
        myClient.setMyClient(args[0], args[1], args[2], args[3]);
      }

      myClient.assignWebTarget();
      testMyClient.buildThreads(myClient,
          (int) (PHASE_CONSTANT[i] * Integer.parseInt(myClient.getThreadCount())));
      testMyClient.startThreads();
      testMyClient.joinThreads();

      testMyClient.setEndTime(System.currentTimeMillis());
      testMyClient.setProcessingTime(testMyClient.calculateWallTime());

      testMyClient.printPhaseInfo(myClient);

      if (i == PHASE_CONSTANT.length - 1) {
        LatencyResult.computeMean();
        LatencyResult.computeMedian();
        testMyClient.printToConsole(myClient);
        long end = System.currentTimeMillis();
        System.out.println("Total run time = " + (end - start));
      }
    }
  }
}
