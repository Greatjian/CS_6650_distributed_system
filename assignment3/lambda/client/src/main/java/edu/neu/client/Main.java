package edu.neu.client;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {

    String ipAddress = "localhost";
    int maxThreadNum = 16;
    int userNumber = 100000;
    int dayNumber = 1;
    int testNumber = 100;

    if (args.length > 0) {
      ipAddress = args[0];
    }
    if (args.length > 1) {
      maxThreadNum = Integer.parseInt(args[1]);
    }
    if (args.length > 2) {
      userNumber = Integer.parseInt(args[2]);
    }
    if (args.length > 3) {
      dayNumber = Integer.parseInt(args[3]);
    }
    if (args.length > 4) {
      testNumber = Integer.parseInt(args[4]);
    }

    String uri = "http://" + ipAddress + ":8080/webapi/myresource";

    MultiThread multiThread = new MultiThread(maxThreadNum, userNumber, dayNumber, testNumber);
    multiThread.start(uri);
  }

}
