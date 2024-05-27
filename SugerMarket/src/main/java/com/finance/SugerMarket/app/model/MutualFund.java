package com.finance.SugerMarket.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mutual_fund")
public class MutualFund {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_mutual_fund_id")
	private Integer id;
	@Column(name = "amc_name")
    private String amcName;
	@Column(name = "scheme_name")
	private String schemeName;
	@Column(name = "option")
    private String option;
	@Column(name = "plan_type")
    private String planType;
	@Column(name = "scheme_code")
	private String schemeCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAmcName() {
		return amcName;
	}

	public void setAmcName(String amcName) {
		this.amcName = amcName;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	
	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public MutualFund() {
		super();
	}

	public MutualFund(String amcName, String schemeName, String schemeCode, String option, String planType) {
		super();
        this.amcName = amcName;
        this.schemeName = schemeName;
        this.schemeCode = schemeCode;
        this.option = option;
        this.planType = planType;
    }

}
