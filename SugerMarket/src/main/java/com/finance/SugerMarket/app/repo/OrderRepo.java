package com.finance.SugerMarket.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.finance.SugerMarket.app.model.OrderDetail;

public interface OrderRepo extends JpaRepository<OrderDetail, Integer> {

	@Query("SELECT o FROM OrderDetail o WHERE o.user.username = :username ORDER BY o.dateOfEvent ASC")
	public List<OrderDetail> findByUserUsernameOrderByDateOfEventAsc(String username);
}
