/**
* OcutagAuthScheme.java
* OcutagHelper
* Created by Pradeep Bohra
* File Version - 1.2 (08/January/2013)
* 
* Copyright 2012 Ricoh Innovations, All rights reserved.
*/
package com.ricoh.ripl.calandroid.framework;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
/**
* This class is responsible for generating the authorization header with the parameters provided to it.The authorization is a 
* mandatory header which is used to identify the developer.
* This class implements the Authorization mechanism defined by the Ocutag specification.
* @author Pradeep Bohra
*
*/
public class OcutagAuthScheme
{
// ------------------------------------------------------------------------------------------------------------------------------
// Private Variables
// ------------------------------------------------------------------------------------------------------------------------------		
	private static final String TAG = "OcutagAuthScheme";	
	/**
	 * Placeholder for Application Identifier provided to the developers after they register their application at portal.
	 */		
    private String AppIdentifier;
	/**
	 * Place holder for Developer Identifier provided to the developers after they register at portal.
	 */
    private String DevIdentifier;
	/**
	 * Place holder for Application Shared Secret  provided to the developers after they registers his application at portal.This is a shared secret and developer should store it secretly in the application.
	 */    
    private String AppSharedSecret;
	/**
	 * Place holder for Developer Shared Secret  provided to the developers after they registers at portal.This is a shared secret and developers should store it secretly in the application. 
	 */
    private String DevSharedSecret;
	/**
	 * Place holders for the HTTPVerb parameter - Mandatory Parameter
	 */
    private String HTTPVerb;
	/**
	 * Place holders for the URI parameter - Mandatory Parameter
	 */
    private String URI;    
   	/**
	 * Place holders for the Date parameter - Mandatory Parameter
	 */
    private String Date;
	/**
	 * Place holders for the ContentMD5 parameter - Optional Parameter based on API
	 */     
    private String ContentType;    
	/**
	 * Place holders for the ContentType parameter - - Optional Parameter based on API
	 */
    private String ContentMD5; 
    
    /**
   	 * Place holders for the Age(x-otagopt-age) parameter - - Optional Parameter based on API
   	 */
       private String Age = null;
       /**
   	 * Place holders for the DeviceID(x-otagopt-uid) parameter - - Optional Parameter based on API
   	 */
       private String DeviceID = null;
       /**
   	 * Place holders for the LatAndLong (x-otagopt-latlong) parameter - - Optional Parameter based on API
   	 */
       private String LatAndLong = null;	
     	

    public static class Builder
    {
        	// Mandatory parameters.
        	private String Verb = null;
        	private String URI = null;        	
        	private String AppIdentifier;        	
            private String DevIdentifier;        	    
            private String AppSharedSecret;        	
            private String DevSharedSecret;
            
        	//Mandatory but we can also generate them in our SDK.
        	private String Date = null;        	
        	
        	// Optional parameters - initialized to default values.        	
        	private String ContentMD5 = null;
        	private String ContentType = null;
        	
        	private String Age = null;
        	private String DeviceID = null;
        	private String LatAndLong = null;
        	        	    	
        	//Constructor of the builder design pattern
        	public Builder(String UVerb, String UURI,String UDate,String[] Keys) 
        	{
        		this.Verb = UVerb;
        		this.URI =  UURI;
        		this.Date = UDate;
        		this.DevIdentifier = Keys[0];
        		this.DevSharedSecret = Keys[1];
        		this.AppIdentifier = Keys[2];        		
        		this.AppSharedSecret = Keys[3];
        		
        	}
	
        	public Builder setContentMD5(String UserContentMD5)
        	{ 
        		ContentMD5 = UserContentMD5; 
        		return this; 
        	}
        	
        	public Builder setContentType(String UserContentMD5)
        	{ 
        		ContentType = UserContentMD5; 
        		return this; 
        	} 
        	
        	public Builder setAge(String age)
        	{ 
        		Age = age; 
        		return this; 
        	}
        	public Builder setDeviceID(String deviceID)
        	{ 
        		DeviceID = deviceID; 
        		return this; 
        	}
        	public Builder setLatAndLong(String latandlong)
        	{ 
        		LatAndLong = latandlong; 
        		return this; 
        	}
        	    	
        	public OcutagAuthScheme build() throws MalformedURLException 
        	{
        		return new OcutagAuthScheme(this);
        	}

		public byte[] md5(String string) {
			try {
				// Create MD5 Hash
				MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
				digest.update(string.getBytes());
				byte messageDigest[] = digest.digest();

				// Create Hex String
//				StringBuffer hexString = new StringBuffer();
//				for (int i=0; i<messageDigest.length; i++)
//					hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
//
				return messageDigest;
			}catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
//			return messageDigest;
			return new byte[0];
		}

		public String getBase64Value(byte[] EncodedString) {
			return (Base64.encodeToString(EncodedString,Base64.DEFAULT)).replaceAll("\\r|\\n", "");
		}
	}

    public OcutagAuthScheme(Builder builder) throws MalformedURLException,IllegalArgumentException,NullPointerException
    {
       		 Log.v(TAG, "Initializing Authorization Parameters");
    	     //Defensive copying and checking the parameters for invariants    		
           	 HTTPVerb = builder.Verb.toUpperCase();
       		 if(HTTPVerb != "POST" && HTTPVerb != "GET" && HTTPVerb != "PUT" && HTTPVerb != "DELETE")
       		   	throw new IllegalArgumentException("Invalid Input HTTP Verb");
            	 
       		 URI = builder.URI;
       		 if(URI==null)
       		    throw new NullPointerException("Invalid Input HTTP URL Cannot be null");
         		
       		 //Mandatory but we can also generate them in our SDK.
           	 Date = builder.Date;    	     
           	 if(Date==null)
       		    throw new NullPointerException("Invalid Date Cannot be null");	 
             // Optional parameters - initialized to default values.           	 
           	 ContentMD5 = builder.ContentMD5;
             ContentType = builder.ContentType;
             
             Age = builder.Age;
             DeviceID = builder.DeviceID;
             LatAndLong = builder.LatAndLong;
             
      
             //Keys             
     		DevIdentifier = builder.DevIdentifier;
     		AppIdentifier = builder.AppIdentifier;
     		DevSharedSecret = builder.DevSharedSecret;
     		AppSharedSecret = builder.AppSharedSecret;
     		
     		if(DevIdentifier == null ||AppIdentifier == null ||DevSharedSecret == null ||AppSharedSecret == null)
				throw new NullPointerException("Keys cannot be null!!");
			
		    if((DevIdentifier.length() != 44) || (AppIdentifier.length() != 44) || (DevSharedSecret.length() != 62) ||(AppSharedSecret.length() != 62)  )
		    	throw new IllegalArgumentException("Key Lengths are not as per OpenRVS Specifications - The input to this API should be in hex string format");
		                   	 
             Log.v(TAG, "Initializing Completed");
    }
/**
*This API will create Authorization header value as per Ocutag specification.
*This should only be call after setting all the mandatory setters method of the Class else it throws exception.This API does not checks the content headers
*they depends on HTTP Verb being used.
*@exception IllegalStateException - If the mandatory parameters are used without initializing them.
*@exception InvalidKeyException - If the used keys are not valid keys for generating the HMAC-SHA1.These error are propagated to the user of this API for further analysis.
*@exception UnsupportedEncodingException - The API may throws this error which it picks on runtime from SHA1 method.These error are propagated to the user of this API for further analysis.
*@exception NoSuchAlgorithmException - The API may throws this error which it picks on runtime , when it tries to create Message Authentication Instance using HMAC.These error are propagated to the user of this API for further analysis.
*@return String
*/
	public String getAuthorizedHeader() throws IllegalStateException,UnsupportedEncodingException,NoSuchAlgorithmException,InvalidKeyException,NullPointerException
	{
		if(HTTPVerb==null || URI ==null ||Date == null)
	    	throw new IllegalStateException("Mandatory Parameters are not initialized before use.Please initialize the mandatory parameters.");
		
		if(DevIdentifier == null ||AppIdentifier == null || DevSharedSecret == null ||AppSharedSecret == null)
			throw new IllegalStateException("Key are not initialized before use.Please set the keys before calling this method");
		
	  	String[] authkeyparam= {"realm","oas_version","oas_client_id","oas_access_id","oas_nonce"};
		String[] authvalueparam={"ocutag","2.0",getBase64Value(AppIdentifier),getBase64Value(DevIdentifier),getNonce()};
		String Signature = null;
        
        Signature = OcuTagSignature(authkeyparam,authvalueparam);
        
        // Header Initialization
    	String[][] OcuTagHdrParam = 
		{
        	{authkeyparam[0],authvalueparam[0]},
            {authkeyparam[1],authvalueparam[1]},
            {authkeyparam[2],authvalueparam[2]},
            {authkeyparam[3],authvalueparam[3]},
            {authkeyparam[4],authvalueparam[4]},
        	{"oas_signature",Signature},
		};
		//Appending the RAS keyword in header
	    String AuthStr = "OAS";	    
	    for(String[] looper : OcuTagHdrParam) 
	    {
	    	AuthStr += " " + looper[0] + "=" + looper[1] + ",";
        }	   
	    AuthStr = AuthStr.substring(0, AuthStr.length()-1);
		return AuthStr;
	}
/**
* This API will create OcutagSignature which will be a part of Authorization header value.This API  
* follows the Ocutag specification.This is a private API for internal use of this class.
*@param authkeyparam - A String array containing the authorization parameters keys.
*@param authvalueparam - A String array containing the authorization parameters values corresponding to respective authorization keys.
*@exception InvalidKeyException
*@exception UnsupportedEncodingException
*@exception NoSuchAlgorithmException
*@return String - A string containing the Signature of the request.
*/
	private String OcuTagSignature(String[] authkeyparam,String[] authvalueparam ) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException
	{
		String sign=null;
		
		byte[] AppSharedSec = hexToByteArray(AppSharedSecret);
		byte[] DevSharedSec = hexToByteArray(DevSharedSecret);
		byte Ampersand = 38;//Decimal value of &
				
		byte[] Key = new byte[DevSharedSec.length + 2 + AppSharedSec.length ];
		System.arraycopy(DevSharedSec , 0,Key, 0, DevSharedSec.length);
		Key[DevSharedSec.length] = Ampersand;
		Key[DevSharedSec.length+1] = Ampersand;
		System.arraycopy(AppSharedSec , 0,Key, DevSharedSec.length+2, AppSharedSec.length);		
		
		ArrayList<String> SignatureKey = new ArrayList<String>();
		ArrayList<String> SignatureValue = new ArrayList<String>();
		
		//STEP 1 Collecting the parameters
		if(URI.contains("?"))//Addressing the splitting if query params are there
		{
			String[] Query={"",""};	
		    String[] LocURI = URI.split("\\?");
			if(LocURI[1].contains("="))//Addressing the splitting if query parmas has key and value both
			{
				Query = LocURI[1].split("\\=");
				SignatureKey.add(Query[0]);
				SignatureValue.add(Query[1]);
			}
			else//Addressing the splitting if query parmas has only key
			{
				SignatureKey.add(LocURI[1]);
				SignatureValue.add(null);
			}

		}
		
		SignatureKey.add("Date");
		SignatureValue.add(Date);
		
		if(ContentMD5 != null) // Exception Handling for GET type requests
		{
			SignatureKey.add("Content-MD5");
			SignatureValue.add(ContentMD5);
			
		}
		if(ContentType!=null)// Exception Handling for GET type requests
		{
			SignatureKey.add("Content-Type");
			SignatureValue.add(ContentType);
		}
		 
		if(Age!= null)
		 {
			 SignatureKey.add("X-Otagopt-Age");
			 SignatureValue.add(Age);
		 }
		 if(DeviceID!= null)
		 {
			 SignatureKey.add("X-Otagopt-Uid");
			 SignatureValue.add(DeviceID);
		 }
		 if(LatAndLong!= null)
		 {
			 SignatureKey.add("X-Otagopt-Latlong");
			 SignatureValue.add(LatAndLong);
		 }
		
		SignatureKey.add(authkeyparam[0]);
		SignatureValue.add(authvalueparam[0]);
		
		SignatureKey.add(authkeyparam[1]);
		SignatureValue.add(authvalueparam[1]);
		
		SignatureKey.add(authkeyparam[2]);
		SignatureValue.add(authvalueparam[2]);
		
		SignatureKey.add(authkeyparam[3]);
		SignatureValue.add(authvalueparam[3]);
		
		SignatureKey.add(authkeyparam[4]);
		SignatureValue.add(authvalueparam[4]);

		//STEP 2 Normalizing the keys and percent encoding the keys
		for (int i=0; i < SignatureKey.size(); i++) 
		{
			SignatureKey.set(i, URLEncoder.encode(SignatureKey.get(i).toLowerCase()).replace("+", "%20"));
		}
		
		//STEP 3 PERCENT ENCODE ALL VALUES
		int j=0;
		
		if(URI.contains("?"))
		{
			j=1;	//Do not percent encode the already encoded query param value					
		}
		for (; j < SignatureValue.size(); j++) 
		{	
			if(SignatureValue.get(j)!=null)
			SignatureValue.set(j, URLEncoder.encode(SignatureValue.get(j)).replace("+", "%20"));
		}
		
		//STEP 4 SORT THE KEYS 

			Map<String, String> m = new TreeMap<String, String>();
		    
			for (int i = 0; i < SignatureKey.size(); i++)
			{
		    	m.put(SignatureKey.get(i),SignatureValue.get(i));
		    }			

		
		//STEP 5 Formation of parameter string
		StringBuffer Parameter = new StringBuffer();
		for (Map.Entry<String,String> entry : m.entrySet())
		{
			Parameter.append(entry.getKey());
			if(entry.getValue()!=null)
			{
				Parameter.append("=");
				Parameter.append(entry.getValue());				
			}
			Parameter.append("&");		
		}
		
		Parameter.delete(Parameter.length()-1,Parameter.length());
		
		//STEP 6 Formation of Signature base String
		StringBuffer signaturebase = new StringBuffer();
		signaturebase.append(HTTPVerb.toUpperCase());
		signaturebase.append("&");
		if(URI.contains("?"))
			signaturebase.append(URLEncoder.encode((URI.split("\\?"))[0]));
		else
			signaturebase.append(URLEncoder.encode(URI));
		signaturebase.append("&");
		signaturebase.append(URLEncoder.encode(Parameter.toString()));

		Log.d(TAG, "Base String : "+ signaturebase.toString());
		//STEP 7 Calculation of signature header value				
		sign = shaOne(signaturebase.toString(),Key);

		Log.d(TAG, "Ocutag Signature: "+sign.replaceAll("\\r|\\n", "") );
		return sign.replaceAll("\\r|\\n", "");
	}	
/**
* This API returns the base64 encoded value of the input hex string.
* @param EncodeString - Hex String 
* @return String - A base64 encoded string
*/		
	public static String getBase64Value(String EncodeString)
	{
		return (Base64.encodeToString(hexToByteArray(EncodeString),Base64.DEFAULT)).replaceAll("\\r|\\n", "");
	}
/**
* This API returns a random alphanumeric string of length between 10 - 150.
* @return String - A base64 encoded string
*/	
	private String getNonce()
	{
		Random RANGEN = new Random();		    
	    int randomlength = RANGEN.nextInt(140)+10;	//Upper limit is excluded in this, actual randomlength will be between 10-149.
	    
	    String ALPHA_NUM =  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    StringBuffer sb = new StringBuffer(randomlength);
	    for (int i=0;  i<randomlength;  i++) 
	    {  
	         int ndx = (int)(Math.random()*ALPHA_NUM.length());  
	         sb.append(ALPHA_NUM.charAt(ndx));  
	    }  
	    return sb.toString();
	}
/**
* This API will run the HMAC-SHA1 encryption algorithm on the data String passed as parameter with the keystring.
* The result is a string of Base64 value of the SHA-1 data.This is a private API for internal use of this class.
*@param  s - Signature base string formatted as per OpenRVS Authentication document.
*@param  keyString -byte[] of keyString which is formatted as defined in OpenRVS Authentication document.
*@exception InvalidKeyException
*@exception UnsupportedEncodingException
*@exception NoSuchAlgorithmException
*@return Signed value
*/
	private String shaOne(String s, byte[] keyString) throws InvalidKeyException,UnsupportedEncodingException, NoSuchAlgorithmException
	{

		SecretKeySpec key = new SecretKeySpec(keyString,"HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);
	
		byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
	
		return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
/**
* This API is responsible to convert a given hexstring into byte array.This is a private API for internal use of this class.
*@param   s - A hex String.
*@return byte[] of s.
*/	
	public static byte[] hexToByteArray(String s)
	{
	    int len = s.length();
		    
	    if ((len % 2) != 0)
		    throw new IllegalArgumentException("Invalid Input ODD Number of Characters in Hex String");
		    
	    if(s.matches("[0-9A-Fa-f]+"))
	    {	    
		    byte[] data = new byte[len / 2];
		    for (int i = 0; i < len; i += 2) 
		    {
		        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i+1), 16));
		    }
		    return data;
	    }
	   else
	    {
	    	throw new IllegalArgumentException("Invalid Input Wrong Characters in Hex String");
	    }
	}

//	public static String bytesToHex(byte[] bytes) {
//		char[] hexChars = new char[bytes.length * 2];
//		for ( int j = 0; j < bytes.length; j++ ) {
//			int v = bytes[j] & 0xFF;
////			hexChars[j * 2] = hexArray[v >>> 4];
////			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//		}
//		return new String(hexChars);
//	}
}