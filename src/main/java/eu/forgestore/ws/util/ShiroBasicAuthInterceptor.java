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

import java.security.Principal;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.common.security.UsernameToken;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.SimpleCookie;

public class ShiroBasicAuthInterceptor extends AbstractPhaseInterceptor<Message> {
	private static final transient Log logger = LogFactory.getLog(ShiroBasicAuthInterceptor.class.getName());

	private ShiroUTValidator validator;

	public ShiroBasicAuthInterceptor() {
		this(Phase.UNMARSHAL);
	}

	public ShiroBasicAuthInterceptor(String phase) {
		super(phase);
	}

	public void handleMessage(Message message) throws Fault {

		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser != null) {
			logger.info("handleMessage currentUser = " + currentUser.toString());
			logger.info("currentUser.getPrincipal() = " + currentUser.getPrincipal());
			logger.info("SecurityUtils.getSubject().getSession() = " + currentUser.getSession().getId() );
			logger.info("currentUser.getSession().getAttribute(  aKey ) = " + currentUser.getSession().getAttribute("aKey") );
			logger.info("message.getId() = " + message.getId() );
			
			
			
			// Here We are getting session from Message
		    HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
		    HttpSession  session = request.getSession();
		   
			logger.info("HttpSession session.getId() = " + session.getId() );
			
			if (currentUser.getPrincipal() != null) {
				logger.info("User [" + currentUser.getPrincipal() + "] IS ALREADY logged in successfully. =========================");

				if (currentUser.isAuthenticated()) {
					logger.info("User [" + currentUser.getPrincipal() + "] IS isAuthenticated and logged in successfully. =========================");
					return;
				}

				if (currentUser.isRemembered()) {
					logger.info("User [" + currentUser.getPrincipal() + "] IS REMEMBERED and logged in successfully. =========================");
					return;
				}
			}
		}
		
		AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);
		if (policy == null || policy.getUserName() == null || policy.getPassword() == null) {
			String name = null;
			if (policy != null) {
				name = policy.getUserName();
			}
			String error = "No user credentials are available";
			logger.warn(error + " " + "for name: " + name);
			throw new SecurityException(error);
		}

		try {

			UsernameToken token = convertPolicyToToken(policy);


			String s = validator.validate(token);
			//
			// Create a Principal/SecurityContext
			//bale principal apo to validator
//			Principal p = null;
//			if (s!=null) {
//				p = new SimplePrincipal( s );
//			}
//
//			message.put(SecurityContext.class, createSecurityContext(p));
			currentUser.getSession().setAttribute("aKey", UUID.randomUUID().toString());
			
		} catch (Exception ex) {
			throw new Fault(ex);
		}
	}

	protected UsernameToken convertPolicyToToken(AuthorizationPolicy policy) throws Exception {

		UsernameToken token = new UsernameToken(policy.getUserName(), policy.getPassword(), policy.getAuthorizationType(), false, "", "");

		return token;
	}

	protected SecurityContext createSecurityContext(final Principal p) {
		return new SecurityContext() {

			public Principal getUserPrincipal() {
				return p;
			}

			public boolean isUserInRole(String arg0) {
				return false;
			}
		};
	}

	public ShiroUTValidator getValidator() {
		return validator;
	}

	public void setValidator(ShiroUTValidator validator) {
		this.validator = validator;
	}

}
