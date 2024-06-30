package com.finance.sugarmarket.auth.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.sugarmarket.auth.model.MFUser;
import com.finance.sugarmarket.auth.model.MapRoleUser;

public interface MapRoleUserRepo extends JpaRepository<MapRoleUser, Integer>{
	public MapRoleUser findByUser(MFUser user);
}