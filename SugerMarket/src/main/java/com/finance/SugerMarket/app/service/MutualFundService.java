package com.finance.SugerMarket.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.SugerMarket.app.model.MutualFund;
import com.finance.SugerMarket.app.repo.MutualFundRepo;

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
}
