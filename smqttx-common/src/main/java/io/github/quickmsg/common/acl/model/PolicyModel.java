package io.github.quickmsg.common.acl.model;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclType;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PolicyModel {

    private String subject;

    private String source;

    private AclAction action;

    private AclType aclType;

}
