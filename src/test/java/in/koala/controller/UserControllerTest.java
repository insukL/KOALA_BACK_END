package in.koala.controller;

import com.google.gson.Gson;
import in.koala.controller.dto.SingInRequest;
import in.koala.service.UserService;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("회원 가입")
    @Test
    void SingInTest() throws Exception {
        //given
        final SingInRequest singInRequest = singInRequest();
        doReturn(false).when(userService).isFindEmailDuplicated(singInRequest.getFindEmail());
        doReturn(new )

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/sing-ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(singInRequest))
        );

        //then
        final MvcResult mvcResult = resultActions.andExpect(status().isCreated()).andReturn();
        final String token = mvcResult.getResponse().getContentAsString();
        assertThat();
    }
    private SingInRequest singInRequest(){
        final SingInRequest singInRequest = new SingInRequest();
        singInRequest.setNickname("nickname");
        singInRequest.setAccount("account");
        singInRequest.setFindEmail("asd@asd.com");
        singInRequest.setPassword("password");
        return singInRequest;
    }
}
