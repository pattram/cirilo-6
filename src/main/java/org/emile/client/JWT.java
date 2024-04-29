package org.emile.client;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JWT
{
    // Sample id_token that needs validation. This is probably the only field you need to change to test your id_token.
    // If it doesn't work, try making sure the MODULUS and EXPONENT constants are what you're using, as detailed below.
    public static final String id_token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDZnlJbkMtRHZzcmpHeW1hcWhiNkpUeUczcFlscVByMHBnelFqMWpnSE9ZIn0.eyJqdGkiOiIxOTkwMTMwMC1iNTc1LTRlYmEtOGJkMC01MmQ4YzY3NzkxYTYiLCJleHAiOjE1OTg0NTQ0NzMsIm5iZiI6MCwiaWF0IjoxNTk4NDU0NDEzLCJpc3MiOiJodHRwczovL2dhbXMudW5pLWdyYXouYXQvYXV0aC9yZWFsbXMvR2FtcyIsInN1YiI6IjU2MzdiNWIzLTVhOTItNGY0ZS05N2Y4LWNiNGZlMDQyNThhOSIsInR5cCI6IkJlYXJlciIsImF6cCI6IkNpcmlsbyIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjRhZjZkZTk0LTA1MWQtNDdjNC1iMmMxLWVlNTA4NmU3YjA2ZCIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsic3VwZXJ1c2VyIl19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImdyb3VwcyI6WyJnX2FhaWYiLCJnX2dyYWxpcyIsImdfbXdzIiwiZ19wYWRhd2FuIl0sInByZWZlcnJlZF91c2VybmFtZSI6InBhZGF3YW4ifQ.Hhz4gwx9RStQfgPgBxG7JszGgsQEbpUSL0Gqfa8UZkAeNAAKxXTIg4a5mN37gZL1Lhg9OPJhARJonfCyat8IbKE27O-f1D0Wn9V-CBOJEWpXxxBRtWDJgrQ0el5wPQGVVwIi0JIc__g8idhUldTEWMqMqn-bDSlTVBPgiDW-2rnevaoHo3gd5VaVSJh2QW07iXvQw4UBaV5ej-UYc3YKQJWzX5UmFUUr3aVVLheAxOqWr9_CqYAh56LiRxmVJxCD2UdM8116lB3plXZPI2aiuMJ_X1Pq2xRoJmwrPEf6cUsNa27HNhn4YIbycN5RqVzhBoYYat6aHgxQ2m3MtcVwcQ";
    public static final String[] id_token_parts = id_token.split("\\.");
    
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSK9HugMgsFP0EcW+jKX3xCJnDRsTf6K8YlE5QJfYI+bOBxffKQN4GdPdipLjHITnKczLjnDG173AlsEhzuG/UxMPvrOr4PtPPu1j+uQr76OrHp+0HPqmHsbECrlRY+mmb+J2AAQphDRdVB8ogynraRyKSx12gF3lx3Lxu577O28dVnUsBlaQ/0JhkwHpN+7rLz/M+RV6O2kgTHETdeafc6yfHWRqt1zkVNghJiRZUW6x2XoIUw0miZcr0mgRD+fGM6ScnJVEci4hURD9EPgFgWkVXZqRbIGz4CM4ePtf/17tLDNLzyXn8FFExLA91zYEFVaQfRUgEPLlY8PnLnp1wIDAQAB";
    
    public static final String ID_TOKEN_HEADER = base64UrlDecode(id_token_parts[0]);
    public static final String ID_TOKEN_PAYLOAD = base64UrlDecode(id_token_parts[1]);
    public static final byte[] ID_TOKEN_SIGNATURE = base64UrlDecodeToBytes(id_token_parts[2]);

    public static void main(String args[])
    {
        dumpJwtInfo();
        validateToken();
    }

    
    public static String base64UrlDecode(String s)
    {
        byte[] decodedBytes = base64UrlDecodeToBytes(s);
        String result = new String(decodedBytes, StandardCharsets.UTF_8);
        
        return result;
    }

    public static byte[] base64UrlDecodeToBytes(String s)
    {
        Base64 decoder = new Base64(-1, null, true);
        byte[] decodedBytes = decoder.decode(s);

        return decodedBytes;
    }

 
    public static void dump(String data)
    {
        System.out.println(data);
    }

    public static void dumpJwtInfo()
    {
        dump(ID_TOKEN_HEADER);
        dump(ID_TOKEN_PAYLOAD);
    }

    public static void validateToken()
    {
        byte[] data = (id_token_parts[0] + "." + id_token_parts[1]).getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKey(PUBLIC_KEY);
        
        try {
            boolean isSignatureValid = verifyUsingPublicKey(data, ID_TOKEN_SIGNATURE, publicKey);
            
            System.out.println("isSignatureValid: " + isSignatureValid);
                       
            JSONParser parser = new JSONParser(); 
            
            JSONObject json = (JSONObject) parser.parse(ID_TOKEN_PAYLOAD);
            
            String username = (String) json.get("preferred_username");
            Long exp = (Long) json.get("exp");
      
            System.out.println(username);
            
            JSONObject realm_access = (JSONObject) json.get("realm_access");     
          
            JSONArray roles = (JSONArray) realm_access.get("roles");
            for (int i = 0; i < roles.size(); i++) {
                System.out.println(roles.get(i));
            }
            JSONArray groups = (JSONArray) json.get("groups");
            for (int i = 0; i < groups.size(); i++) {
                System.out.println(groups.get(i));
            }
             
            if (exp > (new Date().getTime() + 1) / 1000)  {
            	System.out.println("Token ok!");
            } else {
              	System.out.println("Token expired!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static PublicKey getPublicKey(String s)
    {
        byte[] key = base64UrlDecodeToBytes(s);
 
        try {
        	X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        	KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        	PublicKey publicKey = keyFactory.generatePublic(spec);        
 
            return publicKey;
        } catch (Exception e) {
            throw new RuntimeException("Cant create public key", e);
        }
    }

    private static boolean verifyUsingPublicKey(byte[] data, byte[] signature, PublicKey pubKey) throws GeneralSecurityException
    {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pubKey);
        sig.update(data);

        return sig.verify(signature);
    }
}