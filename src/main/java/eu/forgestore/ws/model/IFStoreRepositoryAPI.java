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

package eu.forgestore.ws.model;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;


public interface IFStoreRepositoryAPI {

	//utility

	Response getEntityImage(String uuid, String imgfile);
	
	//USER related methods 
	Response getUsers(); 
	Response getUserById(int userid);
	Response addUser(FStoreUser user);
	Response updateUserInfo(int userid, FStoreUser user);
	Response deleteUser(int userid);
	Response getAllWidgetsofUser(int userid);
	Response getAllCoursesofUser(int userid);

	//Sessions
	public Response addUserSession(UserSession userSession);
	public Response getUserSessions();
	
	//categories
	Response getCategories();
	Response getCategoryById(int catid);
	Response addCategory(Category c);
	Response updateCategory(int catId, Category c);
	Response deleteCategory(int catId);
	
	
	//Widgets Related API methods
	Response getWidgets(Long categoryid);
	Response getWidgetByID(int appid);
	Response getWidgetUUID(String uuid);		
	Response updateWidget(int aid,  List<Attachment> attachements);
	Response addWidget(List<Attachment> attachements);
	void deleteWidget(int appid);
	
	//courses Related API methods
	Response getCourses(Long categoryid);
	Response getCoursetByID(int courseid);
	Response updateCourse(int cid,  List<Attachment> attachements);
	Response addCourse( List<Attachment> attachements);
	void deleteCourse(int courseid);
	
	//FIREAdapters related API methods
	Response getFIREAdapters(Long categoryid);
	Response getFIREAdapterByID(int faid);	
	Response updateFIREAdapter(int faid, List<Attachment> attachements);
	Response addFIREAdapter( List<Attachment> attachements);
	void deleteFIREAdapter( int faid);
	
}
