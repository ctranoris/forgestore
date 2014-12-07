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

package eu.forgestore.ws.repo;

import eu.forgestore.ws.impl.FStoreJpaController;
import eu.forgestore.ws.model.FStoreProperty;
import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.model.Category;
import eu.forgestore.ws.model.Course;
import eu.forgestore.ws.model.FIREAdapter;
import eu.forgestore.ws.model.Product;
import eu.forgestore.ws.model.UserSession;
import eu.forgestore.ws.model.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ctranoris
 *
 */
public class FStoreRepository {

	private static final transient Log logger = LogFactory.getLog(FStoreRepository.class.getName());
	private static FStoreJpaController fstoreJpaController;
	
	
	
	public FStoreRepository(){
	}
	
	
	/**
	 * Add new fstore user
	 * 
	 * @param s
	 *            FStoreUser to add
	 * @return the FStoreUser
	 */
	public FStoreUser addFStoreUserToUsers(FStoreUser s) {
		fstoreJpaController.saveUser(s);
		return s;
	}
	
//	public BunMetadata addBunMetadataToBuns(BunMetadata bm){
//		fstoreJpaController.saveBunMetadata(bm);
//		return bm;
//	}
//	
	public Collection<FStoreUser> getUserValues() {

		List<FStoreUser> ls = fstoreJpaController.readUsers(0, 100000);
//		HashMap<Integer, FStoreUser> cb = new HashMap<>();
//		
//		for (FStoreUser buser : ls) {
//			cb.put(buser.getId() , buser);
//		}
		
		return ls;
	}
	
	public FStoreUser updateUserInfo(int userid, FStoreUser user) {
		FStoreUser bm = fstoreJpaController.updateFStoreUser(user);
		return bm;
	}
	
//	public BunMetadata updateBunInfo(long l, BunMetadata bm) {
//		BunMetadata bmr = fstoreJpaController.updateBunMetadata(bm);
//		return bmr;
//	}
	
	public Product updateProductInfo(Product bm) {
		Product bmr = fstoreJpaController.updateProduct(bm);
		return bmr;
	}


	public void deleteUser(int userid) {
		fstoreJpaController.deleteUser(userid);
	}
	
	public void deleteProduct(int bunid) {
		fstoreJpaController.deleteProduct(bunid);
		
	}


	public FStoreUser getUserByID(int userid) {
		return fstoreJpaController.readFStoreUserById(userid);
	}

	public FStoreUser getUserByUsername(String un) {
		return fstoreJpaController.readFStoreUserByUsername(un);
	}
	

	public FStoreUser getUserByEmail(String email) {
		return fstoreJpaController.readFStoreUserByEmail(email);
		}

	

	public Product getProductByID(int bunid) {
		return (Product) fstoreJpaController.readProductByID(bunid);
	}
	
	public Product getProductByUUID(String uuid) {
		return (Product) fstoreJpaController.readProductByUUID(uuid);
	}


	
	
	public FStoreJpaController getfstoreJpaController() {
		return fstoreJpaController;
	}

	public void setfstoreJpaController(FStoreJpaController fstoreJpaController) {
		this.fstoreJpaController = fstoreJpaController;
		logger.info("======================== SETing setfstoreJpaController ========================");
		this.fstoreJpaController.initData();
	}



	public Object getCategories() {

		List<Category> ls = fstoreJpaController.readCategories(0, 100000);
		return ls;	}


	public Category addCategory(Category c) {
		fstoreJpaController.saveCategory(c);
		return c;
	}


	public Category getCategoryByID(int catid) {
		return fstoreJpaController.readCategoryByID(catid);
	}


	public Category updateCategoryInfo(Category c) {
		Category bmr = fstoreJpaController.updateCategory(c);
		return bmr;
	}


	public void deleteCategory(int catid) {
		fstoreJpaController.deleteCategory(catid);
		
	}


	public List<Widget> getWidgets(Long categoryid) {
		List<Widget> ls = fstoreJpaController.readWidgetMetadata(categoryid, 0, 100000);		
		return ls;
	}
	

	public List<Widget> getUserWidgets(FStoreUser u) {
		List<Widget> ls = fstoreJpaController.readWidgetMetadataByOwnerId(u.getId(), 0, 100000);	
		return ls;
	}


	public List<Course> getCourses(Long categoryid) {
		List<Course> ls = fstoreJpaController.readCoursesMetadata(categoryid, 0, 100000);		
		return ls;
	}


	public List<FIREAdapter> getFIREAdapters(Long categoryid) {
		List<FIREAdapter> ls = fstoreJpaController.readFIREAdaptersMetadata(categoryid,0, 100000);
		return ls;
	}

	
	
	public FStoreProperty addproperty(FStoreProperty p) {
		fstoreJpaController.saveProperty(p);
		return p;
	}

	public void deleteProperty(int propid) {
		fstoreJpaController.deleteProperty(propid);
		
	}
	

	public FStoreProperty updateProperty(FStoreProperty p) {
		FStoreProperty bp = fstoreJpaController.updateProperty(p);
		return bp;
	}

	public Object getProperties() {

		List<FStoreProperty> ls = fstoreJpaController.readProperties(0, 100000);
		return ls;	
	}
	
	public static FStoreProperty getPropertyByName(String name) {
		return fstoreJpaController.readPropertyByName(name);
	}


	public FStoreProperty getPropertyByID(int propid) {
		return fstoreJpaController.readPropertyByID(propid);
	}


	public UserSession addUserSession(UserSession userSession) {
		fstoreJpaController.saveUserSession(userSession);
		return userSession;
		
	}


	public FStoreUser getUserBySessionId(String sessionId) {
		UserSession u = fstoreJpaController.readUserBySessionId(sessionId);
		if (u !=null)
			return u.getUser();
		
		return  null;
	}


	public List<Course> getUserCourses(FStoreUser u) {
		List<Course> ls = fstoreJpaController.readCoursesMetadataByOwnerId(u.getId(), 0, 100000);	
		return ls;
	}


	public List<FIREAdapter> getUserFIREAdapters(FStoreUser u) {
		List<FIREAdapter> ls = fstoreJpaController.readFIREAdaptersMetadataByOwnerId(u.getId(), 0, 100000);	
		return ls;
	}


}
