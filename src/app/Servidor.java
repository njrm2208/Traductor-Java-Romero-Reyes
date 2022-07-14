package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor {

    public static void main(String[] args) {

        try {
            System.out.println("Iniciado el servidor...");

            //Escuchando en el puerto 5001
            DatagramSocket ServerUdp = new DatagramSocket(5001);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket datos = new DatagramPacket(buffer, buffer.length);

                //Recibo la respuesta como BYTES
                ServerUdp.receive(datos);

                //Convierto de bytes A objeto dataTR usando la clase serializar
                dataTR ConvTR = (dataTR) Serializar.deserialize(datos.getData());

                
                System.out.println(ConvTR.palabra);
           

                //Hago llamdo a la funcion Busc_Trad para realizar la traduccion
                dataTR Recv_Trad = Busc_Trad(ConvTR);
               
                //Convierto el objeto que obtuve de la traduccion
                byte[] mensaje = Serializar.serialize(Recv_Trad);        
                
                //Creo un nuevo Datagrama
                DatagramPacket responde = new DatagramPacket(mensaje, mensaje.length, datos.getAddress(), datos.getPort());

                //Envia la traduccion
                ServerUdp.send(responde);
              
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Esta funcion realizara la busqueda y traduccion
     private static dataTR Busc_Trad(dataTR Conv_TR) {
        //Creo un nuevo objeto de la clase dataTR
         dataTR objeto = new dataTR();
        //fichero donde guardo las palabras
        File fichero = new File("traductor.txt");
        boolean error = false;

        try {
            //Leo el fichero
            BufferedReader br = new BufferedReader(new FileReader(fichero));
            String linea = "";
            //Establezco un limite 
            int limite = 0;

            while ((linea = br.readLine()) != null) {
                
                if (linea.contains(Conv_TR.palabra)) {
                    //El limite sera hasta llegar a los : de cada linea del fichero
                    limite = linea.indexOf(":");
                    objeto.tipo = 1;
                    error = true;

                    if (Conv_TR.tipo == 0) {
                        //DE INGLES A ESPANOL
                        objeto.palabra = linea.substring(limite + 1, linea.length());
                       
                    } else {
                        //DE ESPANOL A INGLES
                    
                          objeto.palabra = linea.substring(0, limite);
                    }

                    break;
                }
            }

            //Si error es falso entonces no se contro la palabra por lo cual tipo es igual a 2
            if (!error) {
                objeto.tipo = 2;
                objeto.palabra = "No se encontro esta palabra";
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        //Retornamos un Objeto
        return objeto;
    }

}
