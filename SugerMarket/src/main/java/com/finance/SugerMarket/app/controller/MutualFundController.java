package com.finance.SugerMarket.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.SugerMarket.app.model.MutualFund;
import com.finance.SugerMarket.app.service.MutualFundService;
import com.finance.SugerMarket.app.utils.MarketDataUtil;
import com.finance.SugerMarket.constants.MFConstants;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class MutualFundController {

	@Autowired
	private MutualFundService mutualFundService;
	
	private static final Logger log = LoggerFactory.getLogger(MutualFundController.class);

	@GetMapping("/get-mutualfunds")
	public List<MutualFund> findAllMutualFunds() {
		return mutualFundService.findAllMutualFunds();
	}
	
	@GetMapping("/get-mf-api-data")
	public List<MutualFund> findMfApiMutualFunds() {
		return MarketDataUtil.getMFList();
	}
	
	@PostMapping("/save-mutualfund")
	public ResponseEntity<String> saveMutualFund(@RequestBody MutualFund fund) {
		try {
			mutualFundService.saveMutualFund(fund);
		} catch (Exception e) {
			log.error("error while saving mutual fund: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MFConstants.FAILED);
		}
		return ResponseEntity.ok(MFConstants.SUCCESS);
	}
}
