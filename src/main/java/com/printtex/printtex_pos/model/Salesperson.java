package com.printtex.printtex_pos.model;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Salesperson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "salesperson_id")
    private int id;

    @Column(name = "salesperson_name")
    @NotEmpty(message = "*Please provide salesperson name")
    private String salespersonName;

    @Column(name = "mobileNo")
    @NotEmpty(message = "*Please provide mobile no.")
    @Size(min = 14, max = 14, message = "Mobile number must be 14 digit")
    @Pattern(regexp = "^(?:\\+?88)?01[15-9]\\d{8}$", message = "Mobile number must be start with +880")
    private String mobileNo;

    private Long branchId;


    public Salesperson() {
    }

    public Salesperson(String salespersonName, String mobileNo, Long branchId) {
        this.salespersonName = salespersonName;
        this.mobileNo = mobileNo;
        this.branchId = branchId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalespersonName() {
        return salespersonName;
    }

    public void setSalespersonName(String salespersonName) {
        this.salespersonName = salespersonName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
