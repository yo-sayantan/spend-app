package com.finance.sugarmarket.auth.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finance.sugarmarket.auth.model.MFUser;

public interface MFUserRepo extends JpaRepository<MFUser, Integer> {
	public MFUser findByUsername(String username);
	
	@Query("SELECT user FROM MFUser user WHERE user.username=:username AND user.isActive=true")
	public MFUser findBYUsernameAndISActive(@Param("username") String username);
	
	@Query("SELECT user.username FROM MFUser user WHERE user.isActive=true")
	public List<String> getActiveUsernameList();
}