package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.ApplicationSecurityTestConfig;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingRepository;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShareRepository;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.entities.services.PaymentContributionService;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.eventify.api.entities.modules.expensesharing.utils.DistributionUtil;
import com.eventify.api.entities.modules.expensesharing.utils.PaymentsUtil;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.utils.TestEntityUtil;
import com.eventify.api.utils.TestRequestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ApplicationSecurityTestConfig.class,
        properties = {"command.line.runner.enabled=false"}
)
class PaymentContributionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private TestEntityUtil testEntityUtil;

    @Autowired
    private TestRequestUtil testRequestUtil;

    @MockBean
    private PaymentsUtil paymentsUtil;

    @Autowired
    DistributionUtil distributionUtil;

    @InjectMocks
    private PaymentContributionService paymentService;
    @MockBean
    private PaymentContributionRepository paymentRepository;

    @InjectMocks
    private ExpenseSharingService expenseService;
    @MockBean
    private ExpenseSharingRepository expenseRepository;

    @MockBean
    private CostShareRepository costShareRepository;

    @InjectMocks
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void getAll() throws Exception {
        User payer = testEntityUtil.createTestUser();
        User user2 = testEntityUtil.createTestUser();
        RequestCostShare share1 = new RequestCostShare(payer.getId(), 50.0);
        RequestCostShare share2 = new RequestCostShare(user2.getId(), 50.0);
        Event event = testEntityUtil.createTestEvent();
        ExpenseSharingModule expenseModule = testEntityUtil.createTestExpenseModule(event, List.of(payer, user2));
        PaymentContribution payment = testEntityUtil.createTestPayment(expenseModule, payer, List.of(payer, user2), List.of(share1, share2));

        when(paymentRepository.findAllByExpenseModuleId(expenseModule.getId())).thenReturn(List.of(payment));

        mockMvc.perform(testRequestUtil.getRequest("/modules/expense-sharing/ " + expenseModule.getId() + "/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value(payment.getTitle()))
                .andExpect(jsonPath("$[0].amount").value(payment.getAmount()))
                .andExpect(jsonPath("$[0].shareType", Matchers.is("EQUAL")));
    }

    @Test
    @WithMockUser
    void create() throws Exception {
        User payer = testEntityUtil.createTestUser();
        User user2 = testEntityUtil.createTestUser();
        RequestCostShare share1 = new RequestCostShare(payer.getId(), 50.0);
        RequestCostShare share2 = new RequestCostShare(user2.getId(), 50.0);
        List<RequestCostShare> shares = List.of(share1, share2);
        Event event = testEntityUtil.createTestEvent();
        ExpenseSharingModule expenseModule = testEntityUtil.createTestExpenseModule(event, List.of(payer, user2));
        PaymentContribution payment = testEntityUtil.createTestPayment(expenseModule, payer, List.of(payer, user2), shares);

        when(expenseRepository.findById(expenseModule.getId())).thenReturn(Optional.of(expenseModule));
        when(userRepository.findById(payer.getId())).thenReturn(Optional.of(payer));
        doNothing().when(paymentsUtil).validateUserIds(expenseModule, payer, shares);
        when(paymentRepository.save(any(PaymentContribution.class))).thenReturn(payment);
        when(costShareRepository.saveAll(anyList())).thenReturn(payment.getShares());

        mockMvc.perform(testRequestUtil.postRequest("/modules/expense-sharing/ " + expenseModule.getId() + "/payments",
                String.format("{\"title\": \"%s\", \"amount\": %s, \"userId\": \"%s\", \"shareType\": \"%s\", \"shares\": ",
                        payment.getTitle(),
                        payment.getAmount(),
                        payer.getId(),
                        payment.getShareType()
                ) + objectMapper.writeValueAsString(shares) + "}"
                )
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(payment.getTitle()))
                .andExpect(jsonPath("$.amount").value(payment.getAmount()))
                .andExpect(jsonPath("$.shareType", Matchers.is("EQUAL")))
                .andExpect(jsonPath("$.payer.displayName").value(payer.getDisplayName()))
                .andExpect(jsonPath("$.shares.length()", Matchers.is(2)));
    }

    @Test
    @WithMockUser
    void delete() throws Exception {
        User payer = testEntityUtil.createTestUser();
        User user2 = testEntityUtil.createTestUser();
        RequestCostShare share1 = new RequestCostShare(payer.getId(), 50.0);
        RequestCostShare share2 = new RequestCostShare(user2.getId(), 50.0);
        List<RequestCostShare> shares = List.of(share1, share2);
        Event event = testEntityUtil.createTestEvent();
        ExpenseSharingModule expenseModule = testEntityUtil.createTestExpenseModule(event, List.of(payer, user2));
        PaymentContribution payment = testEntityUtil.createTestPayment(expenseModule, payer, List.of(payer, user2), shares);

        when(userRepository.findByEmail(payer.getEmail())).thenReturn(Optional.of(payer));
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        mockMvc.perform(testRequestUtil.deleteRequest(
                "/modules/expense-sharing/ " + expenseModule.getId() + "/payments/" + payment.getId()
        ))
                .andExpect(status().isOk());

        verify(paymentRepository, times(1)).deleteById(payment.getId());
    }
}