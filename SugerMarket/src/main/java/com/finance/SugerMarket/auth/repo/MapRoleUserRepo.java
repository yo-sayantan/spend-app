package com.finance.SugerMarket.auth.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.SugerMarket.auth.model.MFUser;
import com.finance.SugerMarket.auth.model.MapRoleUser;

public interface MapRoleUserRepo extends JpaRepository<MapRoleUser, Integer>{
	public MapRoleUser findByUser(MFUser user);
}