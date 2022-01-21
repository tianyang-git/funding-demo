package top.headfirst.funding.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginVO implements Serializable {

    // DefaultSerializer requires a Serializable payload
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String email;

}
