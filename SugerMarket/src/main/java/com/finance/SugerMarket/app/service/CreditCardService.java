package com.finance.SugerMarket.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.SugerMarket.app.model.CreditCard;
import com.finance.SugerMarket.app.repo.CreditCardRepo;
import com.finance.SugerMarket.app.repo.ExpenseRepo;
import com.finance.SugerMarket.constants.MFConstants;

@Service
public class CreditCardService {
	
	@Autowired
	private CreditCardRepo creditCardRepo;
	@Autowired
	private ExpenseRepo expenseRepo;
	
	private static final String DELETE_MSG = "Can not delete as it reffered in child table";
	
	public List<CreditCard> findAllCreditCard(String userName) {
		List<CreditCard> list = creditCardRepo.findByUsername(userName);
		list.forEach(x->x.setUser(null));
		return list;
	}
	
	public void saveCreditCard(CreditCard cardDeatil) {
		creditCardRepo.save(cardDeatil);
	}
	
	public CreditCard findByCreditCardId(Integer id) {
		return creditCardRepo.findById(id).get();
	}
	
	public String deleteCreditCard(Integer id) {
		if(expenseRepo.findByCreditCardId(id).size() > 0) {
			return DELETE_MSG;
		}
		creditCardRepo.deleteById(id);
		return MFConstants.SUCCESS;
	}

}
