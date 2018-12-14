package com.example;

import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path), API.
 */
@Path("myresource")
public class MyResource {

  private Database DataDao = Database.getInstance();

  @Path("/{userID}/{day}/{timeInterval}/{stepCount}")
  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  public void postData(@PathParam("userID") int user_id, @PathParam("day") int day,
      @PathParam("timeInterval") int timeInterval, @PathParam("stepCount") int stepCount)
      throws SQLException {
    new Kafkaproducer("test").start();
    DataDao.postRecord(new Model(user_id, day, timeInterval, stepCount));
  }

  @GET
  @Path("/current/{userID}")
  @Produces(MediaType.TEXT_PLAIN)
  public int getByUser(@PathParam("userID") String userID) throws SQLException {
    int sum = DataDao.getCurrentDay(Integer.parseInt(userID));
    return sum;
  }

  @Path("/single/{userID}/{day}")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public int getSingleDay(@PathParam("userID") int user_id, @PathParam("day") int day)
      throws SQLException {
    return DataDao.getSingleDay(user_id, day);
  }

  @GET
  @Path("/range/{userID}/{startDay}/{numDays}")
  @Produces(MediaType.TEXT_PLAIN)
  public int[] getByRange(@PathParam("userID") String dayID,
      @PathParam("startDay") String startDay,
      @PathParam("numDays") String numDays) throws SQLException {
    int[] stepCounts = DataDao.getRangeDay(Integer.parseInt(dayID),
        Integer.parseInt(startDay), Integer.parseInt(numDays));
    return stepCounts;
  }

  @DELETE
  @Path("/delete")
  @Produces(MediaType.TEXT_PLAIN)
  public String deleteTable() throws SQLException {
    DataDao.cleanTable();
    return "Deleted!";
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getStatus() {
    return ("alive");
  }

}
