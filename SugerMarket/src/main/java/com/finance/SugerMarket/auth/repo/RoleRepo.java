package com.finance.SugerMarket.auth.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.SugerMarket.auth.model.MFRole;

public interface RoleRepo extends JpaRepository<MFRole, Integer>{

}