package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.pila.model.Transacao;
import br.ufsm.csi.seguranca.server.model.Usuario;
import br.ufsm.csi.seguranca.util.RSAUtil;
import br.ufsm.csi.seguranca.util.Utils;

import javax.crypto.Cipher;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransferirPila implements Runnable {

    private Usuario usuario;
    private DatagramSocket clientSocket;
    private String meuId;
    public TransferirPila(Usuario usuario, DatagramSocket clientSocket, String meuId) {
        this.usuario = usuario;
        this.clientSocket = clientSocket;
        this.meuId = meuId;
    }

    @Override
    public void run() {
        try{
            if (verificaPilaTrans() == -1) {
                System.out.println("NÃO TEM PILA DISPONIVEL.");
            } else {
                FileInputStream filePila = new FileInputStream("/home/camargo/IdeaProjects/PilaCoin/src/br/ufsm/csi/maico/pila/"+verificaPilaTrans());
                ObjectInputStream stream = new ObjectInputStream(filePila);
                PilaCoin pilaTransferir = (PilaCoin) stream.readObject();//recebo pila que eu vou transferir

                System.out.println("Transferir Pilacoin para: "+usuario.getId());

                //criando a transação
                Transacao transacao = new Transacao();
                transacao.setIdNovoDono(usuario.getId());
                transacao.setDataTransacao(new Date());
                transacao.setAssinaturaDono(null);
                List<Transacao> transacaos = new ArrayList<Transacao>();
                transacaos.add(transacao);
                pilaTransferir.setTransacoes(transacaos);

                //criando minha assinatura para por no pila que vai ser transferido
                MessageDigest algh = MessageDigest.getInstance("SHA-256");
                byte[]  hashTransacao = algh.digest(Utils.serealizarObjeto(transacao));
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, RSAUtil.getPrivateKey("private_key.der"));
                byte[] minhaAssinatura = cipher.doFinal(hashTransacao);

                transacao.setAssinaturaDono(minhaAssinatura);

                pilaTransferir.setTransacoes(transacaos);
                File file = new File("/home/camargo/IdeaProjects/PilaCoin/src/br/ufsm/csi/maico/pila/"+pilaTransferir.getId());
                FileOutputStream out = new FileOutputStream(file);
                out.write(Utils.serealizarObjeto(pilaTransferir));

                Mensagem msg = new Mensagem();
                msg.setTipo(Mensagem.TipoMensagem.PILA_TRANSF);
                msg.setChavePublica(RSAUtil.getPublicKey("public_key.der"));
                msg.setIdOrigem(meuId);
                msg.setMaster(false);
                msg.setPorta(3333);
                msg.setAssinatura(null);
                msg.setPilaCoin(pilaTransferir);
                byte[] buffer = Utils.serealizarObjeto(msg);

                System.out.println("Enviado o packet com Transf! " + buffer.length+" tamanho");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 3333);
                //clientSocket.send(packet);
                stream.close();
            }

        }catch (Exception e){

        }
    }

    /**
     * verifica qual os pila da lista de pilas validos pode ser transferido
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private int verificaPilaTrans() throws IOException, ClassNotFoundException {

        File folder = new File("/home/camargo/IdeaProjects/PilaCoin/src/br/ufsm/csi/maico/pila/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream stream = new ObjectInputStream(fin);
            PilaCoin pilaEnvio = (PilaCoin) stream.readObject();
            //System.out.println("ID = "+pilaEnvio.getId()+"Ver as transações = "+pilaEnvio.getTransacoes());
            if (pilaEnvio.getTransacoes() == null) {
                return Math.toIntExact(pilaEnvio.getId());
            }
        }
        return -1;
    }
}
