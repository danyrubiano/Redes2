package laboratorio3;

import java.math.*;
import java.util.*;
import java.security.*;
import java.util.Random;

/**
 *
 * @author Dany
 */
public class Laboratorio3 {

    private static final SecureRandom rnd = new SecureRandom();

	public static void main(String [] args) throws Exception
	{
		new Laboratorio3();
	}
        
	public Laboratorio3() throws Exception{
	    Random randomGenerator = new Random();
	    int generatorValue,primeValue,publicA,publicB,secretA,secretB,sharedKeyA,sharedKeyB;

	    primeValue = encontrarPrimo();
	    System.out.println("El primo is "+primeValue);
	     generatorValue	= encontrarRaizPrimo(primeValue);

		// Dispositivo A
	    secretA = randomGenerator.nextInt(99999);
		// Dispositivo B
	    secretB = randomGenerator.nextInt(99999);

		// Diffie Hellman
            BigInteger auxSecretA = BigInteger.valueOf(secretA);
            BigInteger auxSecretB = BigInteger.valueOf(secretB);
            BigInteger auxPrimeValue = BigInteger.valueOf(primeValue);
            BigInteger auxGeneratorValue = BigInteger.valueOf(generatorValue);
            BigInteger auxPublicA,auxPublicB, auxSharedKeyA,auxSharedKeyB;
	        auxPublicA=auxGeneratorValue.modPow(auxSecretA, auxPrimeValue);
	        auxPublicB=auxGeneratorValue.modPow(auxSecretB, auxPrimeValue);
	        auxSharedKeyA = auxPublicB.modPow(auxSecretA,auxPrimeValue);
	        auxSharedKeyB = auxPublicA.modPow(auxSecretB,auxPrimeValue);
            
            sharedKeyA = auxSharedKeyA.intValue();
            sharedKeyB = auxSharedKeyB.intValue();
            
            System.out.println("La clave secreta de A es "+secretA);
	        System.out.println("La clave secreta de B es "+secretB);
	        System.out.println("La clave publica de A is "+auxPublicA);
	        System.out.println("La clave publica de B is "+auxPublicB);
            System.out.println("La clave de encriptacion de A is "+sharedKeyA);
	        System.out.println("La clave de encriptacion de B es "+sharedKeyB+ "\n");
            
            int opcion = 0;
            int block = 5;
            Scanner sc = new Scanner(System.in);
            
            List<String> mensajes_encriptados = new ArrayList<String>();
            List<String> dispositivos = new ArrayList<String>();

            while(opcion!=3){
                System.out.println("-------------------Menu-------------------");
                System.out.println("Opcion 1: A envia mensaje a B");
                System.out.println("Opcion 2: B envia mensaje a A");
                System.out.println("Opcion 3: Cerrar comunicacion\n");
                System.out.print("Elija una opcion: ");
               
                opcion = sc.nextInt();
               
                if(opcion == 2){
                    System.out.println("\nDispositivo B, ingrese texto: ");
                    Scanner sca = new Scanner(System.in);
                    String cadena = sca.nextLine();  
                   
                    //int length = cadena.length();
                    //int block = 5; //randomGenerator.nextInt(length);
             
                    String encriptado = encriptar(cadena, block, sharedKeyB); // Encriptacion con clave obtenida
                    System.out.println("El mensaje encriptado es: "+encriptado+ "\n");
                    mensajes_encriptados.add(encriptado);
                    dispositivos.add("B: ");
                }
               
                else if(opcion == 1){
                    System.out.println("\nDispositivo A, ingrese texto: ");
                    Scanner sca = new Scanner(System.in);
                    String cadena = sca.nextLine();  
                   
                    //int length = cadena.length();
                    //int block = 5; //randomGenerator.nextInt(length);
             
                    String encriptado = encriptar(cadena, block, sharedKeyA);        
                    System.out.println("El mensaje encriptado es: "+encriptado+ "\n"); 
                    mensajes_encriptados.add(encriptado);
                    dispositivos.add("A: ");
                }
            }
            
            //desencritacion
            String desencriptado = "";
            System.out.println("\n------------------------------------------");
            for (int i = 0; i <= mensajes_encriptados.size() - 1; i++) {
                desencriptado = desencriptar(mensajes_encriptados.get(i), block, sharedKeyA); // da lo mismo la clave de cifrado pues son iguales
                System.out.print(dispositivos.get(i));
                System.out.println(desencriptado);
            }

            
	}

    boolean esPrimo(int r){
        int contador = 2;
        boolean primo=true;
        while ((primo) && (contador!=r)){
            if (r % contador == 0)
            primo = false;
        contador++;
        }
        return primo;
    }

    public List<Integer> factoresPrimo(int number) {
        int n = number;
	int i=2;
	int limit=10000;// speed hack! -> consequences ???
   	List<Integer> factores = new ArrayList<Integer>();
   	while (n!=1){
	    while((n%i)==0){
                factores.add(i);
		n=n/i;
		if(esPrimo(n)){
		    factores.add(n);// yes?
		    return factores;
		}
     	    }
	    i=i+1;
	    if(i==limit)
                return factores;
	}
	System.out.println(factores);
        return factores;
    }

    boolean esRaizPrimo(int g, int p) {
        int totient = p-1; // factor.phi(p);
        List<Integer> factores = factoresPrimo(totient);
        int i = 0;
        int j = factores.size();
        for(;i < j; i++) {
            int factor = factores.get(i);//elementAt
            int t = totient/factor;
            BigInteger auxg = BigInteger.valueOf(g);
            BigInteger auxt = BigInteger.valueOf(t);
            BigInteger auxp = BigInteger.valueOf(p);
	    if(auxg.modPow(auxt, auxp).equals(BigInteger.ONE))
                return false;
        }
        return true;
    }

    int encontrarRaizPrimo(int p){
	int start= 2001;
        for(int i=start;i<100000000;i++)
            if(esRaizPrimo(i,p))
                return i;
	return 0;
    }

    int encontrarPrimo(){
	Random rnd=new Random();
	int p=0;
	// while(!esPrimo(p))
	p= rnd.nextInt(99999);
	return p;
    }

    public static String encriptar(String mensaje, int bloque, int clave){
        int x;
        char c;
        String str;
        StringBuilder sb = new StringBuilder();
        int cont=bloque;
        for(int i=0;i<mensaje.length();i++){
            x = mensaje.charAt(i);
            x=x+((cont+clave)%26); // A diferencia del cifrador entregado para el lab 2, se le suma una clave que viene como parametro, para poder operar con diffie hellman
             c = (char)x;
            sb.append(c);
            cont--;
                if(cont==0){
                    cont = bloque;
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