/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dh;

import java.math.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;

/**
 *
 * @author Dany
 */
public class DH {

	int bitLength=512;	
	int certainty=20;// probabilistic prime generator 1-2^-certainty => practically 'almost sure'

    private static final SecureRandom rnd = new SecureRandom();
// byte[] randomBytes = new byte[32];
// csprng.nextBytes(randombytes);
// Important: Despite its name, don't use SecureRandom.getInstanceStrong()!
// On Linux, this is the equivalent to reading /dev/random which is a pointless performance killer. The default for new SecureRandom() in Java 8 is to read from /dev/urandom, which is what you want

	public static void main(String [] args) throws Exception
	{
		new DH();
	}

	public DH() throws Exception{
	    Random randomGenerator = new Random();
	    BigInteger generatorValue,primeValue,publicA,publicB,secretA,secretB,sharedKeyA,sharedKeyB;

	    primeValue = findPrime();// BigInteger.valueOf((long)g);
	    System.out.println("the prime is "+primeValue);
	     generatorValue	= findPrimeRoot(primeValue);//BigInteger.valueOf((long)p);
	    System.out.println("the generator of the prime is "+generatorValue);

		// on machine 1
	    secretA = new BigInteger(bitLength-2,randomGenerator);
		// on machine 2
	    secretB = new BigInteger(bitLength-2,randomGenerator);

		// to be published:
	    publicA=generatorValue.modPow(secretA, primeValue);
	    publicB=generatorValue.modPow(secretB, primeValue);
	    sharedKeyA = publicB.modPow(secretA,primeValue);// should always be same as:
	    sharedKeyB = publicA.modPow(secretB,primeValue);

	    System.out.println("the public key of A is "+publicA);
	    System.out.println("the public key of B is "+publicB);
	    System.out.println("the shared key for A is "+sharedKeyA);
	    System.out.println("the shared key for B is "+sharedKeyB);
	    System.out.println("The secret key for A is "+secretA);
	    System.out.println("The secret key for B is "+secretB);
            
            System.out.println("Ingrese texto: ");
            Scanner sc = new Scanner(System.in);
            String cadena = sc.nextLine();  
            
            int block = 10;
            
            String encriptado = encriptar(cadena, block,sharedKeyA);
            System.out.println("El mensaje encriptado es: "+encriptado);

	}

	private static boolean miller_rabin_pass(BigInteger a, BigInteger n) {
	    BigInteger n_minus_one = n.subtract(BigInteger.ONE);
	    BigInteger d = n_minus_one;
		int s = d.getLowestSetBit();
		d = d.shiftRight(s);
	    BigInteger a_to_power = a.modPow(d, n);
	    if (a_to_power.equals(BigInteger.ONE)) return true;
	    for (int i = 0; i < s-1; i++) {
	        if (a_to_power.equals(n_minus_one)) return true;
	        a_to_power = a_to_power.multiply(a_to_power).mod(n);
	    }
	    if (a_to_power.equals(n_minus_one)) return true;
	    return false;
	}

	public static boolean miller_rabin(BigInteger n) {
	    for (int repeat = 0; repeat < 20; repeat++) {
	        BigInteger a;
	        do {
	            a = new BigInteger(n.bitLength(), rnd);
	        } while (a.equals(BigInteger.ZERO));
	        if (!miller_rabin_pass(a, n)) {
	            return false;
	        }
	    }
	    return true;
	}

boolean isPrime(BigInteger r){
	return miller_rabin(r);
	// return BN_is_prime_fasttest_ex(r,bitLength)==1;
}

public List<BigInteger> primeFactors(BigInteger number) {
    BigInteger n = number;
	BigInteger i=BigInteger.valueOf(2);
	BigInteger limit=BigInteger.valueOf(10000);// speed hack! -> consequences ???
   	List<BigInteger> factors = new ArrayList<BigInteger>();
   	while (!n.equals(BigInteger.ONE)){
		while (n.mod(i).equals(BigInteger.ZERO)){
        factors.add(i);
		n=n.divide(i);
		// System.out.println(i);
		// System.out.println(n);
		if(isPrime(n)){
			factors.add(n);// yes?
			return factors;
		}
     	}
		i=i.add(BigInteger.ONE);
		if(i.equals(limit))return factors;// hack! -> consequences ???
		// System.out.print(i+"    \r");
	}
		System.out.println(factors);
   return factors;
 }

boolean isPrimeRoot(BigInteger g, BigInteger p)
{
    BigInteger totient = p.subtract(BigInteger.ONE); //p-1 for primes;// factor.phi(p);
    List<BigInteger> factors = primeFactors(totient);
    int i = 0;
    int j = factors.size();
    for(;i < j; i++)
    {
        BigInteger factor = factors.get(i);//elementAt
        BigInteger t = totient.divide( factor);
		if(g.modPow(t, p).equals(BigInteger.ONE))return false;
    }
    return true;
}

BigInteger findPrimeRoot(BigInteger p){
	int start= 2001;// first best probably precalculated by NSA?
	// preferably  3, 17 and 65537

	for(int i=start;i<100000000;i++)
		if(isPrimeRoot(BigInteger.valueOf(i),p))
			return BigInteger.valueOf(i);
			// if(isPrimeRoot(i,p))return BigInteger.valueOf(i);
	return BigInteger.valueOf(0);
}

BigInteger findPrime(){
	Random rnd=new Random();
	BigInteger p=BigInteger.ZERO;
	// while(!isPrime(p))
	p= new BigInteger(bitLength, certainty, rnd);// sufficiently NSA SAFE?!!
	return p;
}

    public static String encriptar(String mensaje, int bloque, BigInteger clave){
        BigInteger x;
        char c;
        String str;
        StringBuilder sb = new StringBuilder();
        
        BigInteger cont = BigInteger.valueOf(bloque);
        for(int i=0;i<mensaje.length();i++){
            
            x = BigInteger.valueOf(mensaje.charAt(i));
            BigInteger aux1 = cont.add(clave);
            BigInteger aux2 = BigInteger.valueOf(26);
            BigInteger aux3 = aux1.mod(aux2);
            x = x.add(aux1); // A diferencia del cifrador entregado para el lab 2, se le suma una clave que viene como parametro, para poder operar con diffie hellman
            int tempx = x.intValue();
            c = (char)tempx;
            sb.append(c);
            cont = cont.subtract(BigInteger.ONE);
                if(cont.equals(BigInteger.ZERO)){
                    cont = BigInteger.valueOf(bloque);
                }
            }
        str=sb.toString();
        
        char a;
        char[] strchars = str.toCharArray();
        for(int j=0;j<str.length()-1;j=j+2){
            
            a = strchars[j];            
            strchars[j]= strchars[j+1];
            strchars[j+1]=a;
                  
        }   
     
        
        str = String.valueOf(strchars);
        
        return str;
        }
        
        
        public static String desencriptar(String mensaje, int bloque, int clave){
        int x;
        String str;        
        char a;
        
        char[] strchars = mensaje.toCharArray();
        for(int k=0;k<mensaje.length()-1;k=k+2){
            
            a = strchars[k];            
            strchars[k]= strchars[k+1];
            strchars[k+1]=a;                    
        } 
        str = String.valueOf(strchars);
        
        StringBuilder sb = new StringBuilder();
        
        int cont=bloque;
        for(int i=0;i<str.length();i++){
            x = str.charAt(i);
            x=x-((cont+clave)%26); // A diferencia del cifrador entregado para el lab 2, se le suma una clave que viene como parametro, para poder operar con diffie hellman
            char c = (char)x;
            sb.append(c);
            cont--;
                if(cont==0){
                    cont = bloque;
                }            
        }
        str=sb.toString();         
               
        return str;
        }


}
