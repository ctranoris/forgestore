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


import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.Md5Hash;


public class EncryptionUtil {

    //secret key
    private static final byte[] key = new byte[]{'d', '3', '2', 't', 'p', 'd', 'M', 'o', 'I', '8', 'x', 'z', 'a', 'P', 'o', 'd'};


    /**
     * return hash value of string
     *
     * @param str unhashed string
     * @return hash value of string
     */
    public static String hash(String str) {

		Hash hash = new Md5Hash(str);
		
        return hash.toBase64();
    }

    /**
     * return encrypt value
     *
     * @param val unencrypted string
     * @return encrypted string
     */
    public static String encrypt(String val) {

        String retVal = null;
       
        return retVal;
    }

    /**
     * return decrypted value of an encrypted
     *
     * @param val encrypted string
     * @return decrypted string
     */
    public static String decrypt(String val) {
        String retVal = null;
        
        return retVal;
    }


}
