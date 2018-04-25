package br.ufsm.csi.seguranca;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws Exception {
        enviarDataGrama();
        PilaCoin pilaCoin = new PilaCoin();
        pilaCoin.setChaveCriador(RSAUtil.getPublicKey("public_key.der"));
        pilaCoin.setIdCriador("MCamargo");
        pilaCoin.setDataCriacao(new Date());
    }

    /**
     * serealiza o obj recebido por parametro
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serealizarObjeto(Serializable obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * deserealizar o obj recebido por parametro
     * @param obj
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Serializable deserializarObjeto(byte[] obj) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (Serializable) objectInputStream.readObject();
    }

    public static void enviarDataGrama() throws Exception {
        final String meuId="Maico C.";
        final int porta= 3333;
        Mensagem mensagem = new Mensagem();
        mensagem.setIdOrigem(meuId);
        mensagem.setTipo(Mensagem.TipoMensagem.DISCOVER);
        mensagem.setChavePublica(RSAUtil.getPublicKey("public_key.der"));
        mensagem.setPorta(44447);

        //socket cliente
        DatagramSocket clientSocket = new DatagramSocket(porta);
        String  servidor = "127.0.0.1";

        InetAddress IPAddress = InetAddress.getByName(servidor);
        System.out.println("   -- mensagem criada  para mandar para o datagrama -> \n   ");
        byte[] mensagemSerealizada = serealizarObjeto(mensagem);

        //envia pacote UDP para servidor
        DatagramPacket sendPacket = new DatagramPacket(mensagemSerealizada,mensagemSerealizada.length, IPAddress, porta);
        System.out.println("Enviando pacote UDP para: " + servidor + ":" + porta);
        clientSocket.send(sendPacket);

        byte[] response = new byte[1500];
        while (true){

            //recebe de volta pacote UDP do servidor
            DatagramPacket receivePacket = new DatagramPacket(response, response.length);
            clientSocket.receive(receivePacket);
            Mensagem resposta = (Mensagem) deserializarObjeto(receivePacket.getData());
            //System.out.println("Resposta: "+mensagem.getIdOrigem() + "ip: "+mensagem.getEndereco() + "Porta: "+mensagem.getPorta());

            if (resposta.getTipo().equals(Mensagem.TipoMensagem.DISCOVER)){
                System.out.println("minha mensagem !");
            }else{
                System.out.println("não é minha mensagem *-*");
            }
            System.out.println("final do socket !");
        }


    }
}
