package com.amazonaws.lambda.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StepCountsDao {
	
	protected ConnectionManager connectionManager;

    // Single pattern: instantiation is limited to one object.
    private static StepCountsDao instance = null;
    protected StepCountsDao() {
        connectionManager = new ConnectionManager();
    }
    public static StepCountsDao getInstance() {
        if(instance == null) {
            instance = new StepCountsDao();
        }
        return instance;
    }

    /**
     * Create a step count record
     */
    public StepCounts create(StepCounts stepCounts) throws SQLException {
        String insertStepCounts = "INSERT INTO StepData(UserId, RecordDate, TimeInterval, StepCount) " +
            "VALUES(?,?,?,?)"
            + "ON duplicate KEY UPDATE "
            + "StepCount = VALUES(StepCount);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            connection = connectionManager.getConnection();
            insertStmt = connection.prepareStatement(insertStepCounts);
            insertStmt.setInt(1, stepCounts.getUserId());
            insertStmt.setInt(2, stepCounts.getDayId());
            insertStmt.setInt(3, stepCounts.getTimeInterval());
            insertStmt.setInt(4, stepCounts.getStepCount());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
        return stepCounts;
    }

    /**
     * Get the sum of all the step count of a certain day for User with
     * userId
     */
    public int getStepCountByDay(int userId, int dayId) throws SQLException {
        String selectStepCounts = "SELECT SUM(StepCount)" +
            "from StepData " +
            "WHERE UserId=? " +
            "AND RecordDate=?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        int sum = 0;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectStepCounts);
            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, dayId);
            results = selectStmt.executeQuery();
            while(results.next()) {
                sum += results.getInt("SUM(StepCount)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return sum;
    }

    /**
     * Get the sum of all the step count of the most recent day for User with
     * userId
     */
    public int getStepCountCurrent(int userId) throws SQLException {
        String selectStepCounts = "select sum(StepCount) as step "
            + "from ("
            + "select StepCount "
            + "from StepData "
            + "where UserID=?) as UserStep;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        int sum = 0;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectStepCounts);
            selectStmt.setInt(1, userId);
            results = selectStmt.executeQuery();
            while(results.next()) {
                sum = results.getInt("step");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return sum;
    }

    /**
     * Get a list of daily step count sum for the User with userId, from a certain
     * start day to a number of days
     */
    public int[] getStepCountByRange(int userId, int startDay, int numDays) throws SQLException {
        String selectStepCounts = "select RecordDate, step " +
            "from " +
            "(select userID, RecordDate, sum(StepCount) as step " +
            "from StepData " +
            "group by userID, RecordDate) as dayData " +
            "where UserID = ? and RecordDate >= ? and RecordDate < ? " +
            "order by RecordDate;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        int[] steps = new int[numDays];
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectStepCounts);
            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, startDay);
            selectStmt.setInt(3, startDay + numDays - 1);
            results = selectStmt.executeQuery();
            while(results.next()) {
                int curDay = results.getInt("DayId");
                steps[curDay - startDay] = results.getInt("step");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return steps;
    }

}
