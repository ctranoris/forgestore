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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.security.UsernameToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

/**
 * 
 * This class is not used. See in beans.xml it is commented
 * @author ctranoris
 *
 * 
 */
public class ShiroUTValidator {

	private final List<String> requiredRoles = new ArrayList<String>();
	private static final transient Log logger = LogFactory.getLog(ShiroUTValidator.class.getName());

	private SecurityManager securityManager;
	
	
//	public ShiroUTValidator(String iniResourcePath) {
//		Factory<SecurityManager> factory = new IniSecurityManagerFactory(iniResourcePath);
//		SecurityUtils.setSecurityManager(factory.getInstance());
//		
//		
//	}

	public List<String> getRequiredRoles() {
		return requiredRoles;
	}

	public void setRequiredRoles(List<String> roles) {
		requiredRoles.addAll(roles);
	}

	public String validate(UsernameToken usernameToken) throws LoginException {
		
		if (usernameToken == null ) {
			throw new SecurityException("noCredential");
		}
		// Validate the UsernameToken

		String pwType = usernameToken.getPasswordType();
		logger.info("UsernameToken user " + usernameToken.getName());
		logger.info("UsernameToken password " + usernameToken.getPassword());
		logger.info("UsernameToken password type " + pwType);

//		if (!WSConstants.PASSWORD_TEXT.equals(pwType)) {
//			if (log.isDebugEnabled()) {
//				logger.debug("Authentication failed - digest passwords are not accepted");
//			}
//			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
//		}
			
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
			currentUser.getSession().setAttribute("aKey", UUID.randomUUID().toString());
		} catch (AuthenticationException ex) {
			logger.info(ex.getMessage(), ex);
			throw new FailedLoginException("Sorry! No login for you.");
		}
		// Perform authorization check
		if (!requiredRoles.isEmpty() && !currentUser.hasAllRoles(requiredRoles)) {
			logger.info("Authorization failed for authenticated user");
			throw new FailedLoginException("Sorry! No login for you.");
		}

		
		return  (String) currentUser.getPrincipal();
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		logger.info("=============== setSecurityManager ===================================================");
		this.securityManager = securityManager;
		SecurityUtils.setSecurityManager( this.securityManager );
	}
}
