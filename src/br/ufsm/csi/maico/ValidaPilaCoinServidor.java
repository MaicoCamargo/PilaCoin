package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.ObjetoTroca;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;
import br.ufsm.csi.seguranca.util.Utils;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ValidaPilaCoinServidor  implements Runnable{
    private PilaCoin coin;
    private InetAddress ip_servidor;
    private int porta;

    public ValidaPilaCoinServidor(PilaCoin coin, InetAddress ip_servidor, int porta) {
        this.coin = coin;
        this.ip_servidor = ip_servidor;
        this.porta = porta;
    }

    @Override
    public void run() {
//        try {
//            System.out.println("validando pilacoin: porta:"+porta+" ip: "+ip_servidor);
//            Socket socket = new Socket(ip_servidor,porta);
//            //criando chave de sessao
//            KeyGenerator keyGenerator =  KeyGenerator.getInstance("AES");
//            keyGenerator.init(128);//size da chave
//            Key minhaChaveDaSessao = keyGenerator.generateKey();
//
//            byte[] coin_serealizado = Utils.serealizarObjeto(coin);
//
//            //criando hash do obj serealizado
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash_objSerializado = digest.digest(coin_serealizado);
//
//            byte[] hash_criptografado = Utils.cripografar("RSA",RSAUtil.getPrivateKey("private_key.der"),hash_objSerializado);
//
//            byte[] hash_chaveDeSessao = Utils.cripografar("RSA",RSAUtil.getMasterPublicKey(), minhaChaveDaSessao.getEncoded());
//
//            byte[] coin_serealizado_criptografado = Utils.cripografar("AES",minhaChaveDaSessao,coin_serealizado);
//
//            //criado obj que vai ser mandado pro servidor
//            ObjetoTroca objetoTroca = new ObjetoTroca();
//            objetoTroca.setObjetoSerializadoCriptografado(coin_serealizado_criptografado);
//            objetoTroca.setChaveSessao(hash_chaveDeSessao);
//            objetoTroca.setAssinatura(hash_criptografado);
//            objetoTroca.setChavePublica(RSAUtil.getPublicKey("public_key.der"));
//
//            //escreve o obj para mandar pro servidor
//            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//            outputStream.writeObject(objetoTroca);
//
//            //recebe a resposta do servidor
//            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
//            ObjetoTroca objetoResposta = (ObjetoTroca) inputStream.readObject();
//            //PilaCoin coinResposta = (PilaCoin) Utils.deserializarObjeto(Utils.descriptografar("AES", minhaChaveDaSessao,objetoResposta.getObjetoSerializadoCriptografado()));
//            System.out.println("Objeto resposta do servidor "+Utils.deserializarObjeto(objetoResposta.getObjetoSerializadoCriptografado()));
//            /*File pilaCoinFile = new File("/home/camargo/IdeaProjects/PilaCoin/src/br/ufsm/csi/maico/pila");
//            FileOutputStream fileOutputStream = new FileOutputStream(pilaCoinFile);
//            fileOutputStream.write(Utils.serealizarObjeto(coinResposta));*/
//            socket.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
