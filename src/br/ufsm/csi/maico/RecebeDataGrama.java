package br.ufsm.csi.maico;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.InvalidKeyException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.server.model.Usuario;
import br.ufsm.csi.seguranca.util.RSAUtil;
import javax.crypto.*;

import static br.ufsm.csi.seguranca.util.Utils.deserializarObjeto;
import static br.ufsm.csi.seguranca.util.Utils.serealizarObjeto;

public class RecebeDataGrama implements Runnable {

    DatagramSocket clientSocket;
    String meuId;
    public RecebeDataGrama(DatagramSocket clientSocket, String meuId) {
        this.clientSocket = clientSocket;
        this.meuId = meuId;
    }
    @Override
    public void run() {
        try {
            while (true){
                byte[] response = new byte[1500];//resposta que recebo do servidor UDP
                //recebe de volta pacote UDP do servidor
                DatagramPacket receivePacket = new DatagramPacket(response, response.length);
                clientSocket.receive(receivePacket);
                Mensagem respostaServidor = (Mensagem) deserializarObjeto(receivePacket.getData());//desserealizo a msg recebida

                if (respostaServidor.getTipo().equals(Mensagem.TipoMensagem.DISCOVER) && !respostaServidor.getIdOrigem().equals(meuId)) {
                    //realiza a transferencia dos pila!
                    System.out.println("fazer transf.: "+respostaServidor.getIdOrigem());
                    Usuario usuario = new Usuario();
                    usuario.setChavePublica(respostaServidor.getChavePublica());
                    usuario.setEndereco(respostaServidor.getEndereco());
                    usuario.setId(respostaServidor.getIdOrigem());
                    new Thread(new TransferirPila(usuario, clientSocket, meuId)).start();
                }
                if(respostaServidor.getTipo().equals(Mensagem.TipoMensagem.DISCOVER_RESP)){
                    System.out.println("TIPO:" + respostaServidor.getTipo()+"\n");
                    byte[] assinatura = respostaServidor.getAssinatura();
                    respostaServidor.setAssinatura(null);

                    MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
                    byte messageDigest[] = algorithm.digest(serealizarObjeto(respostaServidor));

                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, RSAUtil.getMasterPublicKey());
                    byte[] assinaturaServidor = cipher.doFinal(assinatura);
                    if (Arrays.equals(messageDigest, assinaturaServidor)) {
                        System.out.println("MASTER DESCOBERTO! porta: "+respostaServidor.getPorta() + " end:"+ respostaServidor.getEndereco());
                        new Thread(new MineraPilacoin(respostaServidor, meuId)).start();
                    } else {
                        System.out.println("Não são iguais "+messageDigest +"!="+assinaturaServidor);
                    }
                }
        }
    } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
