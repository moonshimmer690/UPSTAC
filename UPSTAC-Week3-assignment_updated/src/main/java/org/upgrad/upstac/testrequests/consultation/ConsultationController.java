package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);




    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @Autowired
    TestRequestFlowService  testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;



    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations()  {

        //Implemented this method to get the list of test requests having status as 'LAB_TEST_COMPLETED'
        // Used the findBy() method from testRequestQueryService class and returned the result

        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor()  {

        // Created an object of User class and store the current logged in user
        //Implemented this method to return the list of test requests assigned to current doctor by making use of above object
        //Used the findByDoctor() method from testRequestQueryService class to get the list

        User doctor = userLoggedInService.getLoggedInUser();
        return testRequestQueryService.findByDoctor(doctor);

    }



    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        // Implemented this method to assign a particular test request to the current doctor(logged in user)
        //Created an object of User class and get the current logged in user
        //Created an object of TestRequest class and used the assignForConsultation() method of testRequestUpdateService to assign the particular id to the current user
        // and returned the above created object

        try {
            User doctor = userLoggedInService.getLoggedInUser();
            TestRequest testrequest = testRequestUpdateService.assignForConsultation(id,doctor);
            return testrequest;
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id,@RequestBody CreateConsultationRequest testResult) {

        // Implemented this method to update the result of the current test request id with test doctor comments
        // Created an object of the User class to get the logged in user
        // Created an object of TestResult class and used updateConsultation() method from testRequestUpdateService class
        //to update the current test request id with the testResult details by the current user(object created)

        try {
            User doctor = userLoggedInService.getLoggedInUser();
            TestRequest testrequest = testRequestUpdateService.updateConsultation(id,testResult,doctor);
            return testrequest;
        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



}
