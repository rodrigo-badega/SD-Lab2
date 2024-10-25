
/**
 *
 * @Rodrigo Oliveira Badega 2207273
 * @Guilherme Henrique Soeiro Fontes 2320657
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());

            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // System.out.println("Digite um numero: ");
            // int valor = Integer.parseInt(br.readLine());
            System.out.println("Insira o tipo desejado (read/write):");
            String tipo = br.readLine();
            String json;
            switch (tipo) {
                case "read":
                    json= "{\"method\":\"read\",\"args\":[\"\"]}\n";
                    // System.out.println(json);
                    saida.writeUTF(json);
                    break;
            
                case "write":
                    System.out.println("Digite a fortuna a ser inserida:");
                    String argumentos = br.readLine();
                    json = "{\"method\":\"write\",\"args\":[\""+argumentos+"\"]}\n";
                    // System.out.println(json);
                    saida.writeUTF(json);
                    break;

                default:
                    System.out.println("Tipo inválido!");
                    break;
            }
            //O valor eh enviado ao servidor
            // saida.writeInt(valor);
            
            //Recebe-se o resultado do servidor
            String resultado = entrada.readUTF();
            
            //Mostra o resultado na tela
            System.out.println(resultado);
            
            socket.close();
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}
