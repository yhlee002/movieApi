package com.portfolio.demo.project.dto.loginlog;

import com.portfolio.demo.project.entity.loginlog.LoginLog;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Setter
@Getter
@NoArgsConstructor
public class LoginLogPagenationParam {
    private int totalPageCnt;
    private long totalElementCnt;
    private int currentPage;
    private int size;
    private List<LoginLogParam> loginLogList;

    public LoginLogPagenationParam(Page<LoginLog> loginLog) {
        this.totalPageCnt = loginLog.getTotalPages();
        this.totalElementCnt = loginLog.getTotalElements();
        this.currentPage = loginLog.getPageable().getPageNumber();
        this.size = loginLog.getPageable().getPageSize();
        this.loginLogList = loginLog.getContent().stream().map(LoginLogParam::create).collect(Collectors.toList());
    }
}
