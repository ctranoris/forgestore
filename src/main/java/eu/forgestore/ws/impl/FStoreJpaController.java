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

package eu.forgestore.ws.impl;

import eu.forgestore.ws.model.FStoreProperty;
import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.model.Category;
import eu.forgestore.ws.model.Course;
import eu.forgestore.ws.model.FIREAdapter;
import eu.forgestore.ws.model.Product;
import eu.forgestore.ws.model.UserSession;
import eu.forgestore.ws.model.Widget;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class maintains the entity manager and get a broker element from DB
 * 
 * @author ctranoris
 * 
 */
public class FStoreJpaController {
	private static final transient Log logger = LogFactory.getLog(FStoreJpaController.class.getName());


	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;


	public void initData() {
		FStoreUser admin = readFStoreUserById(1);
		logger.info("======================== admin  = " + admin);
		
		if (admin==null){
			FStoreUser bu = new FStoreUser();
			bu.setName("FStore Administrator");
			bu.setUsername("admin");
			bu.setPassword("changeme");
			bu.setEmail("");
			bu.setOrganization("");
			bu.setRole("ROLE_ADMIN");
			bu.setActive(true);
			saveUser(bu);
			
			Category c = new Category();
			c.setName("None");
			saveCategory(c);
			

			FStoreProperty p = new FStoreProperty();
			p.setName("adminEmail");
			p.setValue("info@example.org");
			saveProperty(p);
			p = new FStoreProperty();
			p.setName("activationEmailSubject");
			p.setValue("Activation Email Subject");
			saveProperty(p);
			p = new FStoreProperty("mailhost", "example.org");
			saveProperty(p);
			p = new FStoreProperty("mailuser", "exampleusername");
			saveProperty(p);
			p = new FStoreProperty("mailpassword", "pass");
			saveProperty(p);
			
			
		}
		
		
	}

	public long countInstalledBuns() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT COUNT(s) FROM InstalledBun s");
		return (Long) q.getSingleResult();
	}


	public FStoreJpaController() {
		logger.info(">>>>>>>>>>>>>> FStoreJpaController constructor  <<<<<<<<<<<<<<<<<<");
		
		
		
	}

	public String echo(String message) {
		return "Echo processed: " + message;

	}
	
	public void deleteAllProducts() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();

		Query q = entityManager.createQuery("DELETE FROM Product ");
		q.executeUpdate();
		entityManager.flush();

		entityTransaction.commit();

	}

	

	public void deleteAllUsers() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();

		Query q = entityManager.createQuery("DELETE FROM FStoreUser ");
		q.executeUpdate();
		entityManager.flush();

		entityTransaction.commit();

	}

	public void saveUser(FStoreUser bu) {
		logger.info("Will save FStoreUser = " + bu.getName());

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();

		entityManager.persist(bu);
//		List<ApplicationMetadata> apps = bu.getApps();
//		for (ApplicationMetadata app : apps) {			
//			entityManager.persist(app.getCategory());
//			entityManager.persist(app);
//		}
//		
//		List<BunMetadata> buns = bu.getBuns() ;
//		for (BunMetadata bun : buns) {			
//			entityManager.persist(bun.getCategory());
//			entityManager.persist(bun);
//		}
		
		
		entityManager.flush();
		entityTransaction.commit();

	}
	

	public FStoreUser readFStoreUserByUsername(String username) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q = entityManager.createQuery("SELECT m FROM FStoreUser m WHERE m.username='" + username + "'");
		return (q.getResultList().size()==0)?null:(FStoreUser) q.getSingleResult();
	}
	

	public FStoreUser readFStoreUserByEmail(String email) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q = entityManager.createQuery("SELECT m FROM FStoreUser m WHERE m.email='" + email + "'");
		return (q.getResultList().size()==0)?null:(FStoreUser) q.getSingleResult();
	}

	
	public FStoreUser readFStoreUserById(long l) {
		
		
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		return entityManager.find(FStoreUser.class, l);
		
//		Query q = entityManager.createQuery("SELECT m FROM FStoreUser m WHERE m.id=" + userid );		
//		return (q.getResultList().size()==0)?null:(FStoreUser) q.getSingleResult();
		
	}
	
	
	
	public FStoreUser updateFStoreUser(FStoreUser bu) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		FStoreUser resis = entityManager.merge(bu);
		entityTransaction.commit();

		return resis;
	}
	
	@SuppressWarnings("unchecked")
	public List<FStoreUser> readUsers(int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM FStoreUser m");
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}
	
	public long countUsers() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT COUNT(s) FROM FStoreUser s");
		return (Long) q.getSingleResult();
	}

	public void getAllUsersPrinted() {
		logger.info("================= getAllUsers() ==================START");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		List<FStoreUser> lb = entityManager.createQuery("select p from FStoreUser p").getResultList();
		for (Iterator iterator = lb.iterator(); iterator.hasNext();) {
			FStoreUser bu = (FStoreUser) iterator.next();
			logger.info("	======> FStoreUser found: " + bu.getName() + ", Id: " + bu.getId() + ", Id: " + bu.getOrganization() + ", username: " + bu.getUsername());

			List<Product> products = bu.getProducts();
			for (Product prod : products) {
				logger.info("	======> bunMetadata found: " + prod.getName() + 
						", Id: " + prod.getId() + ", getUuid: " + prod.getUuid()
						+ ", getName: " + prod.getName());
			}
			

		}
		logger.info("================= getAll() ==================END");
	}

	public void saveProduct(Product prod) {
		logger.info("Will save Product = " + prod.getName());

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		entityManager.persist(prod);
		entityManager.flush();
		entityTransaction.commit();

	}
	
	public Product updateProduct(Product bm) {
		logger.info("================= updateProduct ==================");
		logger.info("bmgetId="+bm.getId());
		logger.info("bm getName= "+bm.getName());
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		Product resis = entityManager.merge(bm);
		entityTransaction.commit();

		return resis;
	}

	
	public List<FIREAdapter> readFIREAdaptersMetadata(Long categoryid, int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
		if ((categoryid!=null) && (categoryid>=0))
			q = entityManager.createQuery("SELECT a FROM FIREAdapter a WHERE a.categories.id="+categoryid+" ORDER BY a.id");
		else
			q = entityManager.createQuery("SELECT a FROM FIREAdapter a ORDER BY a.id");

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}


	public Product readProductByUUID(String uuid) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM Product m WHERE m.uuid='" + uuid + "'");
		return (q.getResultList().size()==0)?null:(Product) q.getSingleResult();
	}
	
	public Product readProductByID(int id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Product u = entityManager.find(Product.class, id);
		return u;
	}

//	public BunMetadata readBunMetadataByUUID(String uuid) {
//		EntityManager entityManager = entityManagerFactory.createEntityManager();
//
//		Query q = entityManager.createQuery("SELECT m FROM BunMetadata m WHERE m.uuid='" + uuid + "'");
//		return (q.getResultList().size()==0)?null:(BunMetadata) q.getSingleResult();
//	}
//	
//	public BunMetadata readBunMetadataByID(int bunid) {
//		EntityManager entityManager = entityManagerFactory.createEntityManager();
//		BunMetadata u = entityManager.find(BunMetadata.class, bunid);
//		return u;
//	}


	public void deleteUser(int userid) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		FStoreUser u = entityManager.find(FStoreUser.class, userid);
		
		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();

		entityManager.remove(u);

		entityTransaction.commit();
	}
	
	public void deleteProduct(int id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Product p = entityManager.find(Product.class, id);		

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		entityManager.remove(p);
		entityTransaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<Product> readProducts(Long categoryid, int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
		if ((categoryid!=null) && (categoryid>=0))
			q = entityManager.createQuery("SELECT a FROM Product a WHERE a.category.id="+categoryid+" ORDER BY a.id");
		else
			q = entityManager.createQuery("SELECT a FROM Product a ORDER BY a.id");

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}

	
	public void getAllProductsPrinted() {
		logger.info("================= getAllProductsPrinted() ==================START");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		List<Product> lb = readProducts(null,0,10000);
		for (Iterator iterator = lb.iterator(); iterator.hasNext();) {
			Product prod = (Product) iterator.next();
			
				logger.info("	=================> Product found: " + prod.getName() + ", Id: " + prod.getId() + ", getUuid: " + prod.getUuid()
						+ ", getName: " + prod.getName()
						+ ", Owner.name: " + prod.getOwner().getName() );
			

		}
		logger.info("================= getAllProductsPrinted() ==================END");

	}
	

	


	public List<Category> readCategories(int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM Category m  ORDER BY m.id");
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}

	public void saveCategory(Category c) {
		logger.info("Will category = " + c.getName());

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		entityManager.persist(c);
		entityManager.flush();
		entityTransaction.commit();
	}

	public Category readCategoryByID(int catid) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Category u = entityManager.find(Category.class, catid);
		return u;
	}

	public Category updateCategory(Category c) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		Category  resis = entityManager.merge(c);
		entityTransaction.commit();

		return resis;
	}

	public void deleteCategory(int catid) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Category c = entityManager.find(Category.class, catid);

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		entityManager.remove(c);
		entityTransaction.commit();
		
	}

	public void deleteAllCategories() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();

		Query q = entityManager.createQuery("DELETE FROM Category");
		q.executeUpdate();
		entityManager.flush();

		entityTransaction.commit();
		
	}

	public void getAllCategoriesPrinted() {
		logger.info("================= getAllCategoriesPrinted() ==================START");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		List<Category> lb = entityManager.createQuery("select p from Category p").getResultList();
		for (Iterator iterator = lb.iterator(); iterator.hasNext();) {
			Category sm = (Category) iterator.next();
			logger.info("	======> Category found: " + sm.getName() + ", Id: " + sm.getId()  );			

		}
		
	}


	
	public List<Widget> readWidgetMetadata(Long categoryid, int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
		if ((categoryid!=null) && (categoryid>=0))
			q = entityManager.createQuery("SELECT a FROM Widget a WHERE a.categories.id="+categoryid+" ORDER BY a.id");
		else
			q = entityManager.createQuery("SELECT a FROM Widget a ORDER BY a.id");
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}


	public List<Widget> readWidgetMetadataByOwnerId(int userid,  int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
			q = entityManager.createQuery("SELECT a FROM Widget a WHERE a.owner.id="+userid+" ORDER BY a.id");

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}
	
	
	
	public List<Course> readCoursesMetadata(Long categoryid, int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
		if ((categoryid!=null) && (categoryid>=0))
			q = entityManager.createQuery("SELECT a FROM Course a WHERE a.categories.id="+categoryid+" ORDER BY a.id");
		else
			q = entityManager.createQuery("SELECT a FROM Course a ORDER BY a.id");
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}
	
	

	public void saveProperty(FStoreProperty p) {
		logger.info("Will FStoreProperty = " + p.getName());

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		entityManager.persist(p);
		entityManager.flush();
		entityTransaction.commit();
		
	}

	public void deleteProperty(int propid) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		FStoreProperty c = entityManager.find(FStoreProperty.class, propid);

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		entityManager.remove(c);
		entityTransaction.commit();
		
	}

	public FStoreProperty updateProperty(FStoreProperty p) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		FStoreProperty  bp = entityManager.merge(p);
		entityTransaction.commit();

		return bp;
	}

	public List<FStoreProperty> readProperties(int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM FStoreProperty m  ORDER BY m.id");
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();

	}

	public FStoreProperty readPropertyByName(String name) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM FStoreProperty m WHERE m.name='" + name + "'");
		return (q.getResultList().size()==0)?null:(FStoreProperty) q.getSingleResult();

	}

	public FStoreProperty readPropertyByID(int propid) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		FStoreProperty u = entityManager.find(FStoreProperty.class, propid);
		return u;

	}

	public UserSession saveUserSession(UserSession userSession) {
		logger.info("Will userSession = " + userSession.getUsername() );

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		FStoreUser u = entityManager.find(FStoreUser.class, userSession.getUser().getId());
		userSession.setUser(u);

		logger.info("Will userSession = " + u.toString() );

		entityManager.persist(u);
		entityManager.persist(userSession);
		entityManager.flush();
		entityTransaction.commit();
		
		return userSession;
		
	}

	public UserSession readUserBySessionId(String sessionId) {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Query q = entityManager.createQuery("SELECT m FROM UserSession m WHERE m.sessionId='" + sessionId + "'");
		return (q.getResultList().size()==0)?null:(UserSession) q.getSingleResult();
		
	}

	public List<Course> readCoursesMetadataByOwnerId(int userid,  int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
			q = entityManager.createQuery("SELECT a FROM Course a WHERE a.owner.id="+userid+" ORDER BY a.id");

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}

	public List<FIREAdapter> readFIREAdaptersMetadataByOwnerId(int userid,  int firstResult, int maxResults) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query q;
		
			q = entityManager.createQuery("SELECT a FROM FIREAdapter a WHERE a.owner.id="+userid+" ORDER BY a.id");

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);
		return q.getResultList();
	}


	

	
}
