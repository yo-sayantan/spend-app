package com.finance.SugerMarket.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.SugerMarket.app.model.MutualFund;

public interface MutualFundRepo extends JpaRepository<MutualFund, Integer> {
	public MutualFund findMutualFundBySchemeCode(String schemeCode);
	public MutualFund findMutualFundBySchemeName(String schemeName);
}
