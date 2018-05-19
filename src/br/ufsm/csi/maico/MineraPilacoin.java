package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;

import static br.ufsm.csi.seguranca.util.Utils.serealizarObjeto;

public class MineraPilacoin implements Runnable {

    final BigInteger NUMERO_VERIFICADOR_SERVIDOR = new BigInteger("99999998000000000000000000000000000000000000000000000000000000000000000");

    BigInteger objSerealizado_BigInterger;//Objeto serealizado transformado em biginteger

    SecureRandom secureRandom = new SecureRandom();

    public void validaPilaCoin(PilaCoin coin){
        new Thread(new  EnviaRecebeDataGrama(coin)).start();
    }

    @Override
    public void run() {
        try {
            PilaCoin pilaCoin = new PilaCoin();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            pilaCoin.setChaveCriador(RSAUtil.getPublicKey("public_key.der"));
            pilaCoin.setIdCriador("M. Camargo");
            pilaCoin.setDataCriacao(new Date());
            System.out.println("          \n\n   ###   Iniciando minerar de Pilacoin  ##3");
            System.out.println("minerando . .. . ");



            while (true) {
                pilaCoin.setNumeroMagico(secureRandom.nextLong());
                byte[] hashComNumeroMagico = digest.digest(serealizarObjeto(pilaCoin));
                objSerealizado_BigInterger = new BigInteger(1, hashComNumeroMagico); //transformando o hash em um bigInterger
                if (NUMERO_VERIFICADOR_SERVIDOR.compareTo(objSerealizado_BigInterger) > 0) {
                    System.out.println(" ---------------- Pilacoin minerado !");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
