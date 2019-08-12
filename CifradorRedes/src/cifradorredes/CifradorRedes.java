package cifradorredes;


import java.util.Scanner;

public class CifradorRedes {


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

   
    public static void main(String[] args) {
        
        
        System.out.println("Ingrese texto: ");
        Scanner sc = new Scanner(System.in);
        String cadena = sc.nextLine();  
        
        System.out.println("Ingrese tamaño de bloque: ");
        int block = sc.nextInt();
        
        //long startTime = System.nanoTime();        
        String encriptado = encriptar(cadena, block,73573165);
        //long endTime = System.nanoTime();
        
        System.out.println("El mensaje encriptado es: "+encriptado);
        String desencriptado = desencriptar(encriptado, block,73573165);
        System.out.println("El mensaje desencriptado es: "+desencriptado);
        
        //long tiempoTotal = (endTime - startTime);
        //System.out.println("Tiempo de encriptación en nanosegundos = "+tiempoTotal);
    }
    
}
