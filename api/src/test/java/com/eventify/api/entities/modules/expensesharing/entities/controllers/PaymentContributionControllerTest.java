package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.ApplicationSecurityTestConfig;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.entities.services.PaymentContributionService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRoleRepository;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.mail.services.MailService;
import com.eventify.api.mail.utils.MailUtil;
import com.eventify.api.utils.TestEntityUtil;
import com.eventify.api.utils.TestRequestUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
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
    private TestEntityUtil testEntityUtil;

    @Autowired
    private TestRequestUtil testRequestUtil;

    @SpyBean
    private MailService mailServiceSpy;

    @MockBean
    private MailUtil mailUtil;

    @InjectMocks
    private PaymentContributionService paymentService;
    @MockBean
    private PaymentContributionRepository paymentRepository;

    @InjectMocks
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;

    @InjectMocks
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private UserEventRoleService userEventRoleService;
    @MockBean
    private UserEventRoleRepository userEventRoleRepository;

    @Test
    @WithMockUser
    void getAll() throws Exception {
        User user1 = testEntityUtil.createTestUser();
        User user2 = testEntityUtil.createTestUser();
        RequestCostShare share1 = new RequestCostShare(user1.getId(), 50.0);
        RequestCostShare share2 = new RequestCostShare(user2.getId(), 50.0);
        Event event = testEntityUtil.createTestEvent();
        ExpenseSharingModule expenseModule = testEntityUtil.createTestExpenseModule(event, List.of(user1, user2));
        PaymentContribution payment = testEntityUtil.createTestPayment(expenseModule, user1, List.of(user1, user2), List.of(share1, share2));

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
    }

    @Test
    @WithMockUser
    void delete() throws Exception {
    }
}