package top.headfirst.funding.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {

    private String loginacct;
    private String userpswd;
    private String username;
    private String email;
    private String phone_number;
    private String code;

}
