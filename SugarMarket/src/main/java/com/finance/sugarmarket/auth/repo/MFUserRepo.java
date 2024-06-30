package com.finance.sugarmarket.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.sugarmarket.auth.model.MFUser;

public interface MFUserRepo extends JpaRepository<MFUser, Integer> {
	public MFUser findByUsername(String username);
}
