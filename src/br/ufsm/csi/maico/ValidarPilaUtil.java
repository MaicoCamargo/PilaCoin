package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.pila.model.ObjetoTroca;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;
import br.ufsm.csi.seguranca.util.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ValidarPilaUtil implements Runnable {
    private PilaCoin pilaCoinMinerado;
    private Mensagem response_servidor;

    /**
     * recebe o pilacoin minerado e a msg do servidor
     * @param pilaCoin
     * @param response_servidor
     */
    public ValidarPilaUtil(PilaCoin pilaCoin, Mensagem response_servidor) {
        this.pilaCoinMinerado = pilaCoin;
        this.response_servidor = response_servidor;
    }

    @Override
    public void run() {
        try {

            Socket socket = new Socket(response_servidor.getEndereco(), response_servidor.getPorta());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Key chaveSessao = kgen.generateKey();//chave de sessao

            byte[] objetoSerializado = Utils.serealizarObjeto(pilaCoinMinerado);//serealiza o pila minerado

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashPila = digest.digest(objetoSerializado); // hash do pila minerado

            byte[] hashPilaCripto = Utils.cripografar("RSA", RSAUtil.getPrivateKey("private_key.der"), hashPila);//hash cripografado com minha private key

            byte[] chaveSessaoCripto = Utils.cripografar("RSA", RSAUtil.getMasterPublicKey(), chaveSessao.getEncoded());//criptografada chave de sessao com a private key do servidor

            byte[] pila_criptografado_serelizado = Utils.cripografar("AES", chaveSessao, objetoSerializado);// criptogrado meu pila serializado com minha chave de sessao

            ObjetoTroca objetoTroca = new ObjetoTroca();
            objetoTroca.setObjetoSerializadoCriptografado(pila_criptografado_serelizado);
            objetoTroca.setChaveSessao(chaveSessaoCripto);
            objetoTroca.setAssinatura(hashPilaCripto);
            objetoTroca.setChavePublica(RSAUtil.getPublicKey("public_key.der"));//seto minha chave publica

            //envio o objeto troca com meu pila pra ser validado
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(objetoTroca);

            //recebo a resposta do servidor
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjetoTroca objetoResposta = (ObjetoTroca) in.readObject();
            //desrealiza o obj recebido do servidor usando minha chave de sessao
            PilaCoin pilaResposta = (PilaCoin) Utils.deserializarObjeto(Utils.descriptografar("AES", chaveSessao,objetoResposta.getObjetoSerializadoCriptografado()));

            //guardo meu pila no meu computador
            File meuPilaValido = new File("/home/camargo/IdeaProjects/PilaCoin/src/br/ufsm/csi/maico/pila/"+pilaResposta.getId());
            FileOutputStream fileOutputStream = new FileOutputStream(meuPilaValido);
            fileOutputStream.write(Utils.serealizarObjeto(pilaResposta));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
