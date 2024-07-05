package com.portfolio.demo.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.request.CreateMemberRequest;
import com.portfolio.demo.project.dto.member.request.MultiDeleteRequest;
import com.portfolio.demo.project.dto.member.request.MultiUpdateRoleRequest;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

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

        MvcResult result = mockMvc.perform(post("/members/")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String resultStr = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        Result<MemberResponse> createResult = objectMapper.readValue(resultStr, new TypeReference<Result<MemberResponse>>() {
        });
        MemberResponse createdMember = createResult.getData();

        mockMvc.perform(get("/members/" + createdMember.getMemNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").value(createdMember.getMemNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regDate").exists());
    }

    /* 메세지 전송 API의 불필요한 호출로 CI 과정의 테스트 진행 X
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

        mockMvc.perform(post("/members/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(param))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memNo").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regDate").exists());
    }

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

        mockMvc.perform(post("/members/")
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

        MvcResult joinResult = mockMvc.perform(post("/members/")
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
        MvcResult certResult = mockMvc.perform(get("/members/certification")
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
        mockMvc.perform(post("/members/sign-in/check")
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

        mockMvc.perform(post("/members/cert-mail/validation")
                        .content(obj2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(true))
                .andReturn();

        // certification 성공 후
        mockMvc.perform(post("/members/sign-in/check")
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

        MvcResult result = mockMvc.perform(post("/members/cert-message")
                        .content(obj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String resultStr = result.getResponse().getContentAsString();
        System.out.println(resultStr);
    }
    */

    @Test
    void 여러_회원의_권한_업데이트() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Member member1 = MemberTestDataBuilder.randomIdentifierUser()
                .name("테스트계정1").password("123456789!@#qwE").build();
        Member member2 = MemberTestDataBuilder.randomIdentifierUser()
                .name("테스트계정2").password("123456789!@#qwE").build();

        // 회원 1 저장
        MvcResult result = mockMvc.perform(post("/members/")
                        .content(asJsonString(member1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String resultStr1 = result.getResponse().getContentAsString();
        Result<MemberResponse> response1 = objectMapper.readValue(resultStr1, new TypeReference<Result<MemberResponse>>() {});

        // 회원 2 저장
        MvcResult result2 = mockMvc.perform(post("/members/")
                        .content(asJsonString(member2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String resultStr2 = result2.getResponse().getContentAsString();
        Result<MemberResponse> response2 = objectMapper.readValue(resultStr2, new TypeReference<Result<MemberResponse>>() {});

        MultiUpdateRoleRequest request = new MultiUpdateRoleRequest();
        request.setMemNoList(Arrays.asList(response1.getData().getMemNo(), response2.getData().getMemNo()));
        request.setRole(MemberRole.ROLE_ADMIN);

        // 회원 1, 회원 2의 권한 변경(일반회원 -> 관리자)
        mockMvc.perform(post("/members/multi-role")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].role").value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].role").value("ROLE_ADMIN"))
                .andDo(print());

    }

    @Test
    void 여러_회원_삭제() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Member member1 = MemberTestDataBuilder.randomIdentifierUser()
                .name("테스트계정1").password("123456789!@#qwE").build();
        Member member2 = MemberTestDataBuilder.randomIdentifierUser()
                .name("테스트계정2").password("123456789!@#qwE").build();

        // 회원 1 저장
        MvcResult result = mockMvc.perform(post("/members/")
                        .content(asJsonString(member1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String resultStr1 = result.getResponse().getContentAsString();
        Result<MemberResponse> response1 = objectMapper.readValue(resultStr1, new TypeReference<Result<MemberResponse>>() {});

        // 회원 2 저장
        MvcResult result2 = mockMvc.perform(post("/members/")
                        .content(asJsonString(member2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String resultStr2 = result2.getResponse().getContentAsString();
        Result<MemberResponse> response2 = objectMapper.readValue(resultStr2, new TypeReference<Result<MemberResponse>>() {});

        List<Long> memNoList = Arrays.asList(response1.getData().getMemNo(), response2.getData().getMemNo());
        MultiDeleteRequest request = new MultiDeleteRequest();
        request.setMemNoList(memNoList);

        // 회원 1, 회원 2 삭제
        mockMvc.perform(post("/members/batch-delete")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
                .andDo(print());

        mockMvc.perform(get("/members/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0))
                .andDo(print());
    }
}
