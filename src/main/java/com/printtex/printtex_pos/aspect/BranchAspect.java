package com.printtex.printtex_pos.aspect;

import com.printtex.printtex_pos.controller.LoginController;
import com.printtex.printtex_pos.service.ModelMessageService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
//@Scope("session")
public class BranchAspect {
    private final ModelMessageService modelMessageService;
    private final LoginController loginController;

    public BranchAspect(ModelMessageService modelMessageService, LoginController loginController) {
        this.modelMessageService = modelMessageService;
        this.loginController = loginController;
    }


    @Around("allController()")
    public String getRoleBasedAccess(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getRequestFromParams(joinPoint);
        Model model = getModelFromParams(joinPoint);

        try {
            return getDtoReturn(request, model, joinPoint);
        } catch (Throwable throwable) {
            modelMessageService.setModelMessage(model, "result", "Internal Server Error. Sorry For this Inconvenience");
            return "access-denied";
        }
    }

    public HttpServletRequest getRequestFromParams(ProceedingJoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        HttpServletRequest request = null;
        for (int i = 0; i < signatureArgs.length; i++) {
            if (signatureArgs[i] instanceof HttpServletRequest) {
                request = (HttpServletRequest) signatureArgs[i];
                break;
            }
        }
        return request;
    }

    public Model getModelFromParams(ProceedingJoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        Model model = null;
        for (int i = 0; i < signatureArgs.length; i++) {
            if (signatureArgs[i] instanceof Model) {
                model = (Model) signatureArgs[i];
                break;
            }
        }
        return model;
    }

    public String getDtoReturn(HttpServletRequest request, Model model, ProceedingJoinPoint joinPoint) throws Throwable {
        if (loginController.isBranch(model)) {
            return "branch/" + joinPoint.proceed();
        }
        return (String) joinPoint.proceed();
    }

    @Pointcut("(execution(public String com.printtex.printtex_pos.controller.*.*(..)) && args(..)) && !execution(public String com.printtex.printtex_pos.controller.LoginController.*(..)) &&  !execution(public String com.printtex.printtex_pos.controller.CustomerController.updateCustomer(..))&& !execution(public String com.printtex.printtex_pos.controller.CustomerController.deleteOrEditCustomer(..)) && !execution(public String com.printtex.printtex_pos.controller.UserController.*(..))&& !execution(public String com.printtex.printtex_pos.controller.CompanyController.addNewCompany(..)))")
    public void allController() {
    }
}
