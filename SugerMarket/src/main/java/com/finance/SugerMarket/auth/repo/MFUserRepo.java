package com.finance.SugerMarket.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.SugerMarket.auth.model.MFUser;

public interface MFUserRepo extends JpaRepository<MFUser, Integer> {
	public MFUser findByUsername(String username);
}
