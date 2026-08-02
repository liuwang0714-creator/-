package org.example.sotokenspringboot.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class wxuser {

    private Integer id;

    /** 微信唯一openid */
    private String openid;

    /** 开放平台unionid */
    private String unionid;

    /** 微信session_key */
    private String sessionKey;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 生日 */
    private String birthday;

    /** 头像文件id */
    private Integer userFileId;

    /** 0正常 1封禁 2注销 */
    private Integer state;

    /** 封禁截止时间(毫秒时间戳) */
    private Long banTime;

    /** 角色id */
    private Integer roleId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
