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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

public class AjaxUserFilter extends UserFilter {
	

	private static final transient Log logger = LogFactory.getLog(AjaxUserFilter.class.getName());
	
	
	
	
	
	@Override
	protected boolean isAccessAllowed(ServletRequest arg0, ServletResponse arg1, Object arg2) {
    	logger.info("=======> AjaxUserFilter: isAccessAllowed <=============");
    	Boolean b = super.isAccessAllowed(arg0, arg1, arg2);
    	logger.info("=======> AjaxUserFilter: isAccessAllowed = "+b);
		return b;
	}
	
    @Override
    protected boolean onAccessDenied(ServletRequest request,
        ServletResponse response) throws Exception {

        HttpServletRequest req = WebUtils.toHttp(request);
        
    	logger.info("=======> AjaxUserFilter:onAccessDenied <============= MEethod = "+ req.getMethod());
    	
        if (req.getMethod().equals("OPTIONS")){ //useful to pass CORS Options
        	//usually for most browsers in OPTIONS there is no JSESSION ID cookie,
        	//therefore there is a problem with the authentication
        	//still bypassing the OPTIONS here seams not an authorization problem
        	return true; 
        }
        
        String xmlHttpRequest = req.getHeader("X-Requested-With");
        if ( xmlHttpRequest != null ){
            if ( xmlHttpRequest.equalsIgnoreCase("XMLHttpRequest") )  {
                HttpServletResponse res = WebUtils.toHttp(response);
                res.sendError(401);
                return false;
            }

        	logger.info("=======> AjaxUserFilter:onAccessDenied xmlHttpRequest  X-Requested-With="+xmlHttpRequest);
        }
        
        
        HttpServletResponse res = WebUtils.toHttp(response);
        res.sendError(401);
//        xmlHttpRequest = req.getHeader("Origin"); //USED FOR CORS support on 401 error
//        if ( xmlHttpRequest != null ){
//            res.addHeader("Access-Control-Allow-Origin", xmlHttpRequest);
//            res.addHeader("Access-Control-Allow-Credentials", "true");
//            
//        	logger.info("=======> AjaxUserFilter:onAccessDenied xmlHttpRequest Origin="+xmlHttpRequest);
//        	
//        }
        return false;
        //return super.onAccessDenied(request, response);
    }
}  