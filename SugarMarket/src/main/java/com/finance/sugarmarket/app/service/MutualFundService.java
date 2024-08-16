package com.finance.sugarmarket.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.sugarmarket.app.model.MutualFund;
import com.finance.sugarmarket.app.repo.MutualFundRepo;

@Service
public class MutualFundService {

	@Autowired
	private MutualFundRepo mutualfundRepo;

	public List<MutualFund> findAllMutualFunds() {
		return mutualfundRepo.findAll();
	}
	
	public void saveMutualFund(MutualFund fund) {
		mutualfundRepo.save(fund);
	}
	
	public void saveAllMutualFund(List<MutualFund> funds) {
		mutualfundRepo.saveAll(funds);
	}
}
