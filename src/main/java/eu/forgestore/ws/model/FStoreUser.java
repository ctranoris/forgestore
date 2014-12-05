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

import eu.forgestore.ws.util.EncryptionUtil;

import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.util.ByteSource;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonIgnoreType;

@Entity(name = "FStoreUser")
@JsonIgnoreProperties(value = { "products" })
public class FStoreUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 0;

	@Basic()
	private String organization = null;
	@Basic()
	private String name = null;
	@Basic()
	private String email = null;
	@Basic()
	private String username = null;
	@Basic()
	private String password = null;
	@Basic()
	private String role = null;
	@Basic()	
	private Boolean active = false;

	/**
	 * 
	 */
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable()
	private List<Product> products = new ArrayList<Product>();
	
	
//	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
//	@JoinTable()
//	private List<BunMetadata> buns = new ArrayList<BunMetadata>();
//	
//
//	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
//	@JoinTable()
//	private List<ApplicationMetadata> apps = new ArrayList<ApplicationMetadata>();

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public void addProduct(Product prod) {
		if (!this.products.contains(prod)) {
			this.products.add(prod);
			prod.setOwner(this);
		}
	}
	
	public void removeFromProducts(Product prod) {
		if (this.products.contains(prod)) {
			this.products.remove(prod);
		}
	}
	
	public void clearProducts() {
		while (!this.products.isEmpty()) {
			removeFromProducts(this.products.iterator().next());
		}
	}
	
	public Product getProductById(long l) {

		for (Iterator iterator = this.products.iterator(); iterator.hasNext();) {
			Product prod = (Product) iterator.next();
			if (prod.getId() == l)
				return prod;
		}
		return null;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String newOrganization) {
		organization = newOrganization;
	}

//	public List<BunMetadata> getBuns() {
//		return buns;
//	}

//	public void addBun(BunMetadata bunsValue) {
//		if (!buns.contains(bunsValue)) {
//			buns.add(bunsValue);
//			bunsValue.setOwner(this);
//		}
//	}
//
//	public void removeFromBuns(BunMetadata bunsValue) {
//		if (buns.contains(bunsValue)) {
//			buns.remove(bunsValue);
//		}
//	}
//
//	public void clearBuns() {
//		while (!buns.isEmpty()) {
//			removeFromBuns(buns.iterator().next());
//		}
//	}
//
//	public void setBuns(List<BunMetadata> newBuns) {
//		buns = newBuns;
//	}

	public int getId() {
		return id;
	}

	public void setId(int newId) {
		id = newId;
	}

	@Override
	public String toString() {
		return "FStoreUser " + " [organization: " + getOrganization() + "]" + " [id: " + getId() + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		if ( (password!=null) && (!password.equals("")))//else will not change it
			this.password = EncryptionUtil.hash(password);
		
		//this.password = password;
	}
	
	public void setPasswordUnencrypted(String password) {
		this.password = password;
	}

//	public BunMetadata getBunById(int bunid) {
//
//		for (Iterator iterator = buns.iterator(); iterator.hasNext();) {
//			BunMetadata bunMetadata = (BunMetadata) iterator.next();
//			if (bunMetadata.getId() == bunid)
//				return bunMetadata;
//		}
//		return null;
//	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
//	public void addApplication(ApplicationMetadata app) {
//		if (!apps.contains(app)) {
//			apps.add(app);
//			app.setOwner(this);
//		}
//	}
//
//	public List<ApplicationMetadata> getApps() {
//		return apps;
//	}
//
//	public void setApps(List<ApplicationMetadata> apps) {
//		this.apps = apps;
//	}
//
//	public ApplicationMetadata getAppById(int appid) {
//
//		for (Iterator iterator = apps.iterator(); iterator.hasNext();) {
//			ApplicationMetadata appMetadata = (ApplicationMetadata) iterator.next();
//			if (appMetadata.getId() == appid)
//				return appMetadata;
//		}
//		return null;
//	}
}
