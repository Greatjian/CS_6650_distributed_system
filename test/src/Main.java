import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {

  public static void main(String[] args) {
    List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>(1290000));
    Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

    final CountDownLatch latch = new CountDownLatch(128);

    try {
      for (int i = 0; i < 128; i++) {
        int ii = i;
        new Thread(new Runnable() {
          @Override
          public void run() {
            for (int j = 0; j < 1000; j++) {
              list.add(j);
            }
            latch.countDown();
          }
        }).start();
      }
      latch.await();
      Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
      System.out.println(endTimestamp.compareTo(startTimestamp));
      System.out.println(list.size());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
