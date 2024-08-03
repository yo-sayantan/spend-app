package com.finance.sugarmarket.agent.constants;

import java.util.HashMap;
import java.util.Map;

public class Schedulers {
	
	private static Schedulers instance;
	
	Map<String, String> jobs;
	
	private Schedulers() {
        jobs = new HashMap<>();
        //add jobs here
        jobs.put("UpdateBudgetAgent", "00 00 00 ? * * *");
        jobs.put("RemoveInactiveUsersAgent", "00 00/15 * ? * * *");
        jobs.put("UpdateMutualFundAgent", "00 00 00-10/02 ? * * *");
    }

    public static synchronized Schedulers getInstance() {
        if (instance == null) {
            instance = new Schedulers();
        }
        return instance;
    }
    
    public Map<String, String> getAllJobs(){
    	return jobs;
    }
	
}
