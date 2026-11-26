package com.corebanking.api.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "branches")
public class Branch {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "branch_id")
	private Long branchId;
	
	@Column(name = "branch_name", nullable = false)
	private String branchName;
	
	@Column(name = "ifsc_code", nullable = false, unique = true)
	private String ifscCode;
	
	@Column(nullable = false)
	private String city;
	
	public long getBranchId() {
		return branchId;
	}
	
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}
	
	public String getBranchName() {
		return branchName;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public String getIfscCode() {
		return ifscCode;
	}
	
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
}
