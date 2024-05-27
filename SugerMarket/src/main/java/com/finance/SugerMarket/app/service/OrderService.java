package com.finance.SugerMarket.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.SugerMarket.app.dto.FundData;
import com.finance.SugerMarket.app.dto.MarketData;
import com.finance.SugerMarket.app.dto.MutualFundPortfolio;
import com.finance.SugerMarket.app.model.OrderDetail;
import com.finance.SugerMarket.app.repo.OrderRepo;
import com.finance.SugerMarket.app.utils.MarketDataUtil;
import com.finance.SugerMarket.app.utils.MathUtil;
import com.finance.SugerMarket.constants.MFConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

	@Autowired
	private OrderRepo orderRepo;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public MutualFundPortfolio getMutualFundPortfolio(String userName) {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		List<OrderDetail> orderDetails = orderRepo.findByUserUsernameOrderByDateOfEventAsc(userName);

		List<FundData> fundData = getAllFundDataByUser(orderDetails, executorService);

		Double currentAmount = 0.0;
		Double investedAmount = 0.0;
		Double dayChange = 0.0;
		Double preveousDayAmount = 0.0;
		for (FundData fund : fundData) {
			currentAmount += fund.getCurrentAmount();
			investedAmount += fund.getInvestedAmount();
			dayChange += fund.getDay1ChangeAmount();
			preveousDayAmount = preveousDayAmount + fund.getCurrentAmount() - fund.getDay1ChangeAmount();
		}

		Double returnAmount = currentAmount - investedAmount;
		Double returnPercentage = returnAmount / investedAmount * 100;
		Double xirr = findAvgXIRR(fundData, currentAmount);
		Double day1Return = (currentAmount - preveousDayAmount) / preveousDayAmount * 100;

		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		return new MutualFundPortfolio(currentAmount, investedAmount, returnAmount, returnPercentage, xirr, dayChange,
				day1Return, fundData);

	}

	private List<FundData> getAllFundDataByUser(List<OrderDetail> orderDetails, ExecutorService executorService) {
		List<FundData> list = new ArrayList<>();

		Map<String, List<OrderDetail>> orderDetailMap = orderDetails.stream()
				.collect(Collectors.groupingBy(ord -> ord.getMutualFund().getSchemeCode()));

		List<Future<FundData>> futures = new ArrayList<>();

		for (String key : orderDetailMap.keySet()) {
			Future<FundData> future = executorService.submit(() -> createFundData(orderDetailMap.get(key), key));
			futures.add(future);
		}

		Integer id = 1;
		for (Future<FundData> future : futures) {
			try {
				FundData data = future.get();
				data.setId(id++);
				list.add(data);
			} catch (Exception e) {
				// Handle exceptions
				e.printStackTrace();
			}
		}

		return list;
	}

	private FundData createFundData(List<OrderDetail> orderDetails, String schemeCode) throws Exception {

		// fetch market data
		MarketData marketData = MarketDataUtil.getHistoricalData(schemeCode);

		FundData data = new FundData();
		data.setFundName(orderDetails.get(0).getMutualFund().getSchemeName());
		long updatedDateMillis = System.currentTimeMillis();
		updatedDateMillis = sdf.parse(sdf.format(new Date(updatedDateMillis))).getTime();
		while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
			updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
		}
		Date updatedDate = new Date(updatedDateMillis);
		if (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
			log.info(sdf.format(updatedDate) + " is not available for " + data.getFundName());
		}
		Double currentNav = marketData.getNavData().get(new Date(updatedDateMillis));

		Double investedAmount = 0.0;
		Double totalUnits = 0.0;
		List<Double> amounts = new ArrayList<>();
		List<Date> dates = new ArrayList<>();
		Queue<OrderDetail> queue = new LinkedList<>();
		for (OrderDetail ord : orderDetails) {
			ord.setUser(null);
			if (ord.getSide().equals(MFConstants.BUY)) {
				queue.offer(ord);
				investedAmount += ord.getAmount();
				totalUnits += ord.getUnits();
				amounts.add(ord.getAmount());
			}
			if (ord.getSide().equals(MFConstants.SELL)) {
				investedAmount -= getSellInvestedAmount(ord.getAmount(), queue, ord.getUnits());
				totalUnits -= ord.getUnits();
				amounts.add(-1 * ord.getAmount());
			}
			dates.add(ord.getDateOfEvent());

			Double returnP = ((currentNav - ord.getNav()) / ord.getNav()) * 100;
			Double returnValue = (ord.getAmount() * returnP) / 100;
			ord.setTotalReturn(String.format("%.2f", returnValue) + "(" + String.format("%.2f", returnP) + "%)");
			ord.setCurrenValue(ord.getAmount() + returnValue);
		}

		data.setMeta(marketData.getMeta());

		Double currentValue = currentNav * totalUnits;
		Double returnPercentage = totalUnits > 0.0 ? MathUtil.getReturnPercentage(amounts, currentValue) : null;
		Double temp = currentValue * -1;
		amounts.add(temp);
		dates.add(updatedDate);
		Double xirr = totalUnits > 0.0 ? MathUtil.findXIRR(0.1, amounts, dates) : null;

		// set values
		data.setCurrentAmount(currentValue);
		data.setInvestedAmount(investedAmount);
		data.setTotalUnits(totalUnits);
		data.setReturnPercentage(returnPercentage);
		data.setReturnAmount(currentValue - investedAmount);
		data.setXirrValue(xirr);
		data.setCurrentNav(currentNav);
		data.setOrderDetails(orderDetails);
		data.setUpdatedDate(updatedDate);

		// set historical data
		// 1 day
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreDay1 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setDay1Change(((currentNav - beofreDay1) * 100) / beofreDay1);
			data.setDay1ChangeAmount(currentValue - (totalUnits * beofreDay1));
		}

		// 1 W
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 7;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreW1 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setWeek1Change(((currentNav - beofreW1) * 100) / beofreW1);
		}

		// 1 M
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 30;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreM1 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setMonth1Change(((currentNav - beofreM1) * 100) / beofreM1);
		}

		// 6 M
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 30 * 6;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreM6 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setMonth6Change(((currentNav - beofreM6) * 100) / beofreM6);
		}

		// 1 Y
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 30 * 12;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreY1 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setYear1Change(((currentNav - beofreY1) * 100) / beofreY1);
		}

		// 3 Y
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 30 * 12 * 3;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreY3 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setYear3Change(((currentNav - beofreY3) * 100) / beofreY3);
		}

		// 5 Y
		updatedDateMillis = updatedDate.getTime() - MFConstants.ONE_DAY_IN_MILLIS * 30 * 12 * 5;
		if (marketData.getNavData().firstKey().before(new Date(updatedDateMillis))) {
			while (marketData.getNavData().get(new Date(updatedDateMillis)) == null) {
				updatedDateMillis = updatedDateMillis - MFConstants.ONE_DAY_IN_MILLIS;
			}
			Double beofreY5 = marketData.getNavData().get(new Date(updatedDateMillis));
			data.setYear5Change(((currentNav - beofreY5) * 100) / beofreY5);
		}

		// All
		Double beofreAll = marketData.getNavData().firstEntry().getValue();
		data.setAllTimeChange(((currentNav - beofreAll) * 100) / beofreAll);

		return data;
	}

	private Double getSellInvestedAmount(Double amount, Queue<OrderDetail> queue, Double units) {
		Double sellInvestedAmount = 0.0;

		while (!queue.isEmpty() && units > 0.0) {
			if (queue.peek().getUnits().compareTo(units) == 1) {
				sellInvestedAmount += units * queue.peek().getNav();
				units = 0.0;
				queue.peek().setUnits(queue.peek().getUnits() - units);
			} else if (queue.peek().getUnits().equals(units)) {
				sellInvestedAmount += units * queue.peek().getNav();
				units = 0.0;
				queue.poll();
			} else { // queue.peek().getUnits() < units
				sellInvestedAmount += queue.peek().getUnits() * queue.peek().getNav();
				units = units - queue.peek().getUnits();
				queue.poll();
			}
		}

		return sellInvestedAmount;
	}

	private Double findAvgXIRR(List<FundData> fundData, Double currentAmount) {
		TreeMap<Date, Double> dateToTotalAmount = new TreeMap<>();

		for (FundData data : fundData) {
			if (data.getTotalUnits().equals(0.0))
				continue;
			for (OrderDetail orderDetail : data.getOrderDetails()) {
				dateToTotalAmount.merge(orderDetail.getDateOfEvent(),
						orderDetail.getSide().equals(MFConstants.BUY) ? orderDetail.getAmount()
								: -1 * orderDetail.getAmount(),
						Double::sum);
			}
		}

		List<Date> dates = new ArrayList<>(dateToTotalAmount.keySet());
		List<Double> amounts = new ArrayList<>(dateToTotalAmount.values());

		dates.add(new Date(System.currentTimeMillis() - MFConstants.ONE_DAY_IN_MILLIS));
		amounts.add(currentAmount * -1);

		return MathUtil.findXIRR(0.1, amounts, dates);
	}

}
