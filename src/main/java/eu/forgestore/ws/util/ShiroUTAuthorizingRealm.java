/**
 * Copyright 2014 forgestore.eu, University of Patras 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.forgestore.ws.util;

import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.repo.FStoreRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.security.UsernameToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class ShiroUTAuthorizingRealm extends AuthorizingRealm {

	private final List<String> requiredRoles = new ArrayList<String>();
	private static final transient Log logger = LogFactory.getLog(ShiroUTAuthorizingRealm.class.getName());
	
	private FStoreRepository fstoreRepositoryRef;

	
	
	public FStoreRepository getFstoreRepositoryRef() {
		return fstoreRepositoryRef;
	}

	public void setFstoreRepositoryRef(FStoreRepository fstoreRepositoryRef) {
		this.fstoreRepositoryRef = fstoreRepositoryRef;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		logger.info("doGetAuthorizationInfo PrincipalCollection=" + arg0.toString());

		SimpleAuthorizationInfo ai = new SimpleAuthorizationInfo();

		FStoreUser bu = fstoreRepositoryRef.getUserByUsername( arg0.toString() );
		if (bu!=null){

			String r = bu.getRole();
			if ((r==null) || (r.isEmpty())){
				r="ROLE_DEVELOPER"; 
			}
			logger.info("PrincipalCollection Role=" + r);
			ai.addRole(r);
		}
		
		
		return ai;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {

		logger.info("AuthenticationToken at=" + at.toString());

		UsernamePasswordToken token = (UsernamePasswordToken) at;
		logger.info("tokengetUsername at=" + token.getUsername());
		//logger.info("tokengetPassword at=" + String.valueOf(token.getPassword()));
		//logger.info("tokengetPrincipal at=" + token.getPrincipal());
		
		
		FStoreUser bu = fstoreRepositoryRef.getUserByUsername(token.getUsername());
		if (bu == null ){
			throw new AuthenticationException("Sorry! No login for you.");			
		}

		String originalPass = bu.getPassword();
		String suppliedPass = EncryptionUtil.hash(   String.valueOf(token.getPassword())  );
		logger.info("originalPass =" + originalPass );
		logger.info("suppliedPass =" + suppliedPass );
		if  (originalPass.equals( suppliedPass   )) {
			logger.info("======= USER is AUTHENTICATED OK =======");
		} else {
			throw new AuthenticationException("Sorry! No login for you.");
		}

		// try {
		// currentUser.login(token);
		// } catch (AuthenticationException ex) {
		// logger.info(ex.getMessage(), ex);
		// throw new AuthenticationException("Sorry! No login for you.");
		// }
		// // Perform authorization check
		// if (!requiredRoles.isEmpty() && !currentUser.hasAllRoles(requiredRoles)) {
		// logger.info("Authorization failed for authenticated user");
		// throw new AuthenticationException("Sorry! No login for you.");
		// }

		SimpleAuthenticationInfo sa = new SimpleAuthenticationInfo();
		sa.setCredentials(token.getCredentials());
		SimplePrincipalCollection principals = new org.apache.shiro.subject.SimplePrincipalCollection();
		principals.add(token.getPrincipal(), "bakerrealm");
		
		
		sa.setPrincipals(principals);
		return sa;
	}

	public List<String> getRequiredRoles() {
		return requiredRoles;
	}

	public void setRequiredRoles(List<String> roles) {
		requiredRoles.addAll(roles);
	}

	public boolean validate(UsernameToken usernameToken) throws LoginException {

		if (usernameToken == null) {
			throw new SecurityException("noCredential");
		}
		// Validate the UsernameToken

		String pwType = usernameToken.getPasswordType();
		logger.info("UsernameToken user " + usernameToken.getName());
		logger.info("UsernameToken password " + usernameToken.getPassword());
		logger.info("UsernameToken password type " + pwType);

		// if (!WSConstants.PASSWORD_TEXT.equals(pwType)) {
		// if (log.isDebugEnabled()) {
		// logger.debug("Authentication failed - digest passwords are not accepted");
		// }
		// throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
		// }

		if (usernameToken.getPassword() == null) {

			logger.debug("Authentication failed - no password was provided");

			throw new FailedLoginException("Sorry! No login for you.");
		}

		// Validate it via Shiro
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(usernameToken.getName(), usernameToken.getPassword());
		token.setRememberMe(true);
		try {
			currentUser.login(token);
		} catch (AuthenticationException ex) {
			logger.info(ex.getMessage(), ex);
			throw new FailedLoginException("Sorry! No login for you.");
		}
		// Perform authorization check
		if (!requiredRoles.isEmpty() && !currentUser.hasAllRoles(requiredRoles)) {
			logger.info("Authorization failed for authenticated user");
			throw new FailedLoginException("Sorry! No login for you.");
		}

		boolean succeeded = true;

		return succeeded;
	}


}
