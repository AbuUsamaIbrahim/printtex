package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.model.Branch;
import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final LoginController loginController;

    public BranchServiceImpl(BranchRepository branchRepository, LoginController loginController) {
        this.branchRepository = branchRepository;
        this.loginController = loginController;
    }

    @Override
    public List<Branch> getAllBranch() {
        User user = loginController.getAuthenticatedUser();
        if (user.getRole().equals("1")) {
            return branchRepository.findAllByOrderByNameAsc();
        }
        return branchRepository.findAllById(user.getBranch().getId());
    }

    @Override
    public Branch findBranchById(long id) {
        return branchRepository.findBranchById(id);
    }

    @Override
    public void addBranch(Branch branch) {
        branchRepository.save(branch);
    }

    @Override
    public List<Branch> getAllBranchExceptUpdateBranch(long id) {
        return branchRepository.findAllByWithoutUpdateCustomer(id);
    }

    @Override
    public List<Branch> getAllMainBranches() {
        return branchRepository.findAllByIsMainBranchTrue();
    }

    @Override
    public List<Branch> getAllSubBranches() {
        return branchRepository.findAllByIsMainBranchFalse();
    }
}
