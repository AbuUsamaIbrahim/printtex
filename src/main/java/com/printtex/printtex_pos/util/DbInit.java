package com.printtex.printtex_pos.util;

import com.printtex.printtex_pos.model.*;
import com.printtex.printtex_pos.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit {
    @Value("${login.user.name}")
    private String email;

    private final UserRepository userRepository;
    private final TilesRepository tilesRepository;
    private final RoleRepository roleRepository;
    private final UserTilesRepository userTilesRepository;
    private final BranchRepository branchRepository;
    private final BillRepository billRepository;
    private final SaleRepository saleRepository;

    public DbInit(UserRepository userRepository, TilesRepository tilesRepository, RoleRepository roleRepository, UserTilesRepository userTilesRepository, BranchRepository branchRepository, BillRepository billRepository, SaleRepository saleRepository) {
        this.userRepository = userRepository;
        this.tilesRepository = tilesRepository;
        this.roleRepository = roleRepository;
        this.userTilesRepository = userTilesRepository;
        this.branchRepository = branchRepository;
        this.billRepository = billRepository;
        this.saleRepository = saleRepository;
    }

    //    @PostConstruct
    public void setAdminUser() {
        List<String> tilesList = oldTilesList();
        tilesList.addAll(newTilesList());
        List<Tiles> tilesList1 = new ArrayList<>();
        tilesList.forEach(tilesId -> {
            Tiles tiles = tilesRepository.findByTileId(tilesId);
            if (tiles == null) {
                tiles = new Tiles();
                tiles.setTilesName(tilesId);
                tiles.setTileId(tilesId);
                tiles = tilesRepository.save(tiles);
            }
            tilesList1.add(tiles);
        });
        List<String> roleList = new ArrayList<>();
        roleList.add("ADMIN");
        roleList.add("BRANCH");
        roleList.forEach(roleName -> {
            Role role = roleRepository.findByRole(roleName);
            if (role == null) {
                role = new Role();
                role.setActiveStatus(1);
                role.setRole(roleName);
                role = roleRepository.save(role);
            }
        });
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            tilesList1.forEach(tiles -> {
                UserTiles userTiles = new UserTiles();
                userTiles.setUserId(user.getId());
                userTiles.setTilesId(tiles.getId());
                userTilesRepository.save(userTiles);
            });
        }
        int branchCount = branchRepository.countByName("Main Branch");
        if (branchCount == 0) {
            Branch branch = new Branch();
            branch.setIsMainBranch(true);
            branch.setName("Main Branch");
            branch.setAddress("Head Office: 16/B, Ashek Jamadar Lane, Islampur, Dhaka-1100, Bangladesh.");
            branch = branchRepository.save(branch);
            user.setBranch(branch);
            userRepository.save(user);
            /*Branch finalBranch = branch;
            List<Bill> billList = new ArrayList<>();
            billRepository.findAll().forEach(bill -> {
                bill.setUserId(Long.valueOf(String.valueOf(user.getId())));
                bill.setBranchId(finalBranch.getId());
                billList.add(bill);
            });
            billRepository.saveAll(billList);
            List<Sale> saleList = new ArrayList<>();
            saleRepository.findAll().forEach(sale -> {
                sale.setUserId(Long.valueOf(String.valueOf(user.getId())));
                sale.setBranchId(finalBranch.getId());
                saleList.add(sale);
            });
            saleRepository.saveAll(saleList);*/
        }
    }

    public List<String> oldTilesList() {
        List<String> tilesList = new ArrayList<>();
        tilesList.add("stock_info");
        tilesList.add("sales_management");
        tilesList.add("add_item");
        tilesList.add("add_category");
        tilesList.add("add_customer");
        tilesList.add("add_company");
        tilesList.add("add_salesPerson");
        tilesList.add("customer_due");
        tilesList.add("bills");
        tilesList.add("reports");
        tilesList.add("stock_by_branch_for_branch");
//
        tilesList.add("all_stock");
        tilesList.add("stock_by_category");
        tilesList.add("all_bill");
        tilesList.add("bill_by_customer");
        tilesList.add("todays_sale");
        tilesList.add("total_sale");
        tilesList.add("report_by_date");
        tilesList.add("report_by_customer");
        tilesList.add("report_by_salesPerson");
        return tilesList;
    }

    public List<String> newTilesList() {
        List<String> tilesList = new ArrayList<>();
        tilesList.add("stock_by_branch");
        tilesList.add("bill_by_branch");
        tilesList.add("report_by_item");
        tilesList.add("report_by_branch");
        tilesList.add("admin_add_user");
        tilesList.add("admin_tiles_permission");
        tilesList.add("user_access");
        tilesList.add("add_branch");
        tilesList.add("user_management");
        tilesList.add("report_by_eventLog");
        tilesList.add("report_by_branch_and_customer");
        tilesList.add("report_by_branch_and_item");
        tilesList.add("report_by_branch_and_salesperson");
        tilesList.add("allitemadmin");
        tilesList.add("allsalespersonadmin");
        tilesList.add("allcustomeradmin");
        tilesList.add("allUserList");
        return tilesList;
    }


}
