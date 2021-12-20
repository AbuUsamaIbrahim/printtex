package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.Branch;

import java.util.List;

public interface BranchService {
    List<Branch> getAllBranch();

    Branch findBranchById(long id);

    void addBranch(Branch branch);

    List<Branch> getAllBranchExceptUpdateBranch(long id);

    List<Branch> getAllSubBranches();

    List<Branch> getAllMainBranches();
}
