package com.finance.sugarmarket.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.sugarmarket.app.model.Expense;
import com.finance.sugarmarket.app.repo.ExpenseRepo;

@Service
public class ExpenseService {
	
	@Autowired
	private ExpenseRepo expenseRepo;
	
	public List<Expense> findAllExpense(String userName){
		List<Expense> list = expenseRepo.findByUsername(userName);
		list.forEach(x->x.getCreditCard().setUser(null));
		return list;
	}
	
	public void saveExpense(Expense expense) {
		expenseRepo.save(expense);
	}
	
	public void deleteExpense(Integer id) {
		expenseRepo.deleteById(id);
	}
}
