package com.printtex.printtex_pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printtex.printtex_pos.model.Company;
import com.printtex.printtex_pos.model.CompanyModel;
import com.printtex.printtex_pos.repository.CompanyRepository;
import com.printtex.printtex_pos.service.CompanyService;
import com.printtex.printtex_pos.service.EventLogService;
import com.printtex.printtex_pos.service.ModelMessageService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final ModelMessageService modelMessageService;
    private final EventLogService eventLogService;
    private static String UPLOADED_FOLDER = System.getProperty("user.dir") + "/src/main/resources/static/images/companyimages";

    public CompanyController(CompanyService companyService, CompanyRepository companyRepository, ModelMessageService modelMessageService, EventLogService eventLogService) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.modelMessageService = modelMessageService;
        this.eventLogService = eventLogService;
    }

    @GetMapping(value = "/admin/addcompany")
    public String createCompany(Model model) {
        modelMessageService.setModelMessage(model, "companyModel", new CompanyModel());
        modelMessageService.setEmptyResultMessage(model);
        return "addcompany";

    }

    @PostMapping("/admin/addcompany")
    public String addNewCompany(@Valid @ModelAttribute CompanyModel companyModel, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors() || companyModel.getCompanyLogoUrl().isEmpty() || companyModel.getCompanyNameLogoUrl().isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "You can't save company information with wrong input. please check all fields");
            return "addcompany";
        }
        int companyCount = companyRepository.countByCompanyName(companyModel.getCompanyName());
        if (companyCount > 0) {
            modelMessageService.setModelMessage(model, "result", "This Name is already used ");
            return "addcompany";
        }
        String companyNameLogoLocation = UPLOADED_FOLDER + "/" + UUID.randomUUID().toString();
        String companyLogoLocation = UPLOADED_FOLDER + "/" + UUID.randomUUID().toString();
        writeImageToProjectDirectory(companyModel.getCompanyNameLogoUrl(), companyNameLogoLocation);
        writeImageToProjectDirectory(companyModel.getCompanyLogoUrl(), companyLogoLocation);
        Company company = new Company();
        company.setCompanyName(companyModel.getCompanyName());
        company.setCompanyNameLogoUrl(companyNameLogoLocation + "/" + companyModel.getCompanyNameLogoUrl().getOriginalFilename());
        company.setCompanyLogoUrl(companyLogoLocation + "/" + companyModel.getCompanyLogoUrl().getOriginalFilename());
        company.setCompanyAddress(companyModel.getCompanyAddress());
        companyService.addCompany(company);
        modelMessageService.setModelMessage(model, "result", "Company Added successfully");
        ObjectMapper mapper = new ObjectMapper();
        try {
            String userNewJson = mapper.writeValueAsString(company);
            eventLogService.saveInsertLog(userNewJson, Company.class, String.valueOf(company.getCompanyId()), request);
        } catch (JsonProcessingException e) {

        }

        return "addcompany";
    }

    @GetMapping("/admin/company/edit/id/{companyId}")
    public String getCompanyToUpdate(@PathVariable("companyId") String companyId, Model model) {
        List<Company> companies = getAllCompany();
        if (companies.isEmpty()) {
            modelMessageService.setModelMessage(model, "result", "No Company Yet added");
            return "listcompany";
        }

        Company company = companyService.getCompanyByCompanyId(Integer.valueOf(companyId));
        String name = company.getCompanyName();
        modelMessageService.setModelMessage(model, "name", name);
        modelMessageService.setModelMessage(model, "company", company);
        modelMessageService.setModelMessage(model, "result", "");
        return "updatecompany";

    }

    @GetMapping("/admin/company/update")
    public String showAllCompany() {
        return "redirect:/admin/allcompany";
    }

    @PostMapping("/admin/company/update")
    public String updateCompany(@Valid @ModelAttribute Company company, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMessageService.setModelMessage(model, "result", "Company can't be updated with wrong input");
            return "updatecompany";
        }
        ObjectMapper mapper = new ObjectMapper();
        String oldJson = null;
        try {
            oldJson = mapper.writeValueAsString(company);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        companyService.addCompany(company);

        try {
            String userNewJson = mapper.writeValueAsString(company);
            eventLogService.saveUpdateLog(userNewJson, oldJson, Company.class, String.valueOf(company.getCompanyId()), request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        modelMessageService.setModelMessage(model, "companies", getAllCompany());
        modelMessageService.setModelMessage(model, "result", "Company updated successfully");
        return "listcompany";
    }

    @GetMapping(value = {"/admin/allcompany", "/branch/allcompany"})
    public String getCompanyList(Model model) {
        List<Company> companies = getAllCompany();
        if (!companies.isEmpty()) {
            modelMessageService.setEmptyResultMessage(model);
            modelMessageService.setModelMessage(model, "companies", companies);
        } else {
            modelMessageService.setModelMessage(model, "result", "No Company Yet added");
        }
        return "listcompany";
    }

    private void writeImageToProjectDirectory(MultipartFile multipartFile, String location) {
        try {
            byte[] bytes = multipartFile.getBytes();
            File file = new File(location);
            if (!file.exists()) {
                file.mkdirs();
            }
            Path path = Paths.get(location + "/" + multipartFile.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@GetMapping("/company/image/{companyId}/{type}")
    @ResponseBody
    public String getCompanyNameBytes(int companyId, String type){
        Company company = companyRepository.findCompanyByCompanyId(companyId);
        String location = company.getCompanyNameLogoUrl();
        if(type.equals("logo")){
            location = company.getCompanyLogoUrl();
        }
        File initialFile = new File(location);
        try {
            InputStream targetStream = new FileInputStream(initialFile);
            byte[] downloadableBytes = IOUtils.toByteArray(targetStream);
            return Base64.encodeBase64String(downloadableBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    private List<Company> getAllCompany() {
        List<Company> companyList = new ArrayList<>();
        companyService.getCompanyList().forEach(company -> {
            if (company.getCompanyNameLogoUrl().contains("/static/")) {
                company = new Company(company.getCompanyId(), company.getCompanyName(), company.getCompanyLogoUrl().split(Pattern.quote("/") + "static" + Pattern.quote("/") + "images" + Pattern.quote("/") + "companyimages" + Pattern.quote("/"))[1], company.getCompanyNameLogoUrl().split(Pattern.quote("/") + "static" + Pattern.quote("/") + "images" + Pattern.quote("/") + "companyimages" + Pattern.quote("/"))[1], company.getCompanyAddress());
            }
            companyList.add(company);
        });
        return companyList;
    }

}
