package com.amazonaws.lambda.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.SQLException;
import java.util.Map;

public class post implements RequestHandler<Object, String>{
	
	@Override
    public String handleRequest(Object input, Context context) {
		try {
		StepCountsDao stepCountsDao = StepCountsDao.getInstance();
		Map<String, String> params = (Map) input;
		StepCounts stepCounts = new StepCounts(Integer.parseInt(params.get("userID")),
            Integer.parseInt(params.get("dayID")),
            Integer.parseInt(params.get("timeInterval")),
            Integer.parseInt(params.get("stepCount")));
		stepCountsDao.create(stepCounts);
		return "post sucess";
		} catch (SQLException e) {
			e.printStackTrace();	
		}
		return "";
	}

}
