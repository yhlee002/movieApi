package com.portfolio.demo.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.portfolio.demo.project.config.WebMvcConfig;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.request.CreateMemberRequest;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ResourceBundle;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MemberApiTest {

    @Autowired
    private MockMvc mockMvc;

    private String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void 일반_회원_조회() throws Exception {
        Member user = MemberTestDataBuilder.user().build();

        CreateMemberRequest param = new CreateMemberRequest();
        param.setIdentifier(user.getIdentifier());
        param.setName(user.getName());
        param.setPassword(user.getPassword());
        param.setPhone(user.getPhone());
        param.setProvider(user.getProvider());
        param.setRole(user.getRole());
        param.setCertification(user.getCertification());

        MvcResult result = mockMvc.perform(post("/member")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String resultStr = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        Result<MemberResponse> createResult = objectMapper.readValue(resultStr, new TypeReference<Result<MemberResponse>>(){});
        MemberResponse createdMember = createResult.getData();

        mockMvc.perform(get("/member/" + createdMember.getMemNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").value(createdMember.getMemNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regDate").exists());
    }

    @Test
    void 관리자_등록() throws Exception {
        Member admin = MemberTestDataBuilder.admin().build();

        CreateMemberRequest param = new CreateMemberRequest();
        param.setIdentifier(admin.getIdentifier());
        param.setName(admin.getName());
        param.setPassword(admin.getPassword());
        param.setPhone(admin.getPhone());
        param.setProvider(admin.getProvider());
        param.setRole(admin.getRole());
        param.setCertification(admin.getCertification());

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(param))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regDate").exists());
    }

    /* 메세지 전송 API의 불필요한 호출로 CI 과정의 테스트 진행 X
    @Test
    void 일반_회원_가입() throws Exception {
        Member user = MemberTestDataBuilder.randomIdentifierUser()
                .build();

        CreateMemberRequest param = new CreateMemberRequest();
        param.setIdentifier(user.getIdentifier());
        param.setName(user.getName());
        param.setPassword(user.getPassword());
        param.setPhone(user.getPhone());
        param.setProvider(user.getProvider());
        param.setRole(user.getRole());
        param.setCertification(user.getCertification());

        mockMvc.perform(post("/member")
                        .content(asJsonString(param))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regDate").exists());
    }

    @Test
    void 회원가입_후_인증_과정() throws Exception {
        Member member = MemberTestDataBuilder.user().password("123456789!@#qwE").build();

        MvcResult joinResult = mockMvc.perform(post("/member")
                        .content(asJsonString(member))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Gson gson = new Gson();

        String memberStr = joinResult.getResponse().getContentAsString();
        JsonObject resultObj = gson.fromJson(memberStr, JsonObject.class);
        JsonObject foundMember = resultObj.getAsJsonObject("data");
        Long memNo = foundMember.get("memNo").getAsLong();

        // certKey 조회해오기(api 작성해야 함)
        MvcResult certResult = mockMvc.perform(get("/certification")
                        .param("certificationId", member.getIdentifier())
                        .param("certificationType", "EMAIL")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andReturn();

        String certResultStr = certResult.getResponse().getContentAsString(); // JSON화해서 certKey 걸러내기

        JsonObject certResultObj = gson.fromJson(certResultStr, JsonObject.class);
        JsonObject response = certResultObj.getAsJsonObject("data");
        String certKey = response.get("key").getAsString();

        JsonObject obj = new JsonObject();
        obj.addProperty("identifier", member.getIdentifier());
        obj.addProperty("password", "123456789!@#qwE");

        // certification 되기 전
        mockMvc.perform(post("/sign-in/check")
                        .content(obj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("not certified"))
                .andReturn();

        // certification 수행
        JsonObject obj2 = new JsonObject();
        obj2.addProperty("memNo", memNo);
        obj2.addProperty("certKey", certKey);

        mockMvc.perform(post("/cert-mail/validation")
                        .content(obj2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(true))
                .andReturn();

        // certification 성공 후
        mockMvc.perform(post("/sign-in/check")
                        .content(obj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("matched")) // not certified
                .andReturn();
    }

    @Test
    void 인증번호_메세지_전송() throws Exception {
        ResourceBundle resource = ResourceBundle.getBundle("Res_ko_KR_keys");
        String samplePhoneNumber = resource.getString("messageSender");

        JsonObject obj = new JsonObject();
        obj.addProperty("phone", samplePhoneNumber);

        MvcResult result = mockMvc.perform(post("/cert-message")
                        .content(obj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String resultStr = result.getResponse().getContentAsString();
        System.out.println(resultStr);
    }
    */
}
