package br.ufsm.csi.maico;

import br.ufsm.csi.seguranca.pila.model.Mensagem;
import br.ufsm.csi.seguranca.pila.model.PilaCoin;
import br.ufsm.csi.seguranca.util.RSAUtil;
import br.ufsm.csi.seguranca.util.Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Date;
import static br.ufsm.csi.seguranca.util.Utils.serealizarObjeto;

public class MineraPilacoin implements Runnable {

    final String meuId="Maico C.";
    final BigInteger NUMERO_VERIFICADOR_SERVIDOR = new BigInteger("99999998000000000000000000000000000000000000000000000000000000000000000");
    private static PublicKey publicKey;
    private BigInteger hash_bigInteger;
    private Mensagem response_servidor;

    public MineraPilacoin(Mensagem response_servidor) {
        this.response_servidor = response_servidor;
    }

    @Override
    public void run() {
        try {
            System.out.println("minerando !");
            SecureRandom random = new SecureRandom();
            publicKey = RSAUtil.getPublicKey("public_key.der");

            PilaCoin pilaCoin = new PilaCoin();
            pilaCoin.setAssinaturaMaster(RSAUtil.getMasterPublicKey().getEncoded());
            pilaCoin.setIdCriador(meuId);
            pilaCoin.setDataCriacao(new Date());
            pilaCoin.setChaveCriador(publicKey);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            pilaCoin.setNumeroMagico(random.nextLong());
            byte[] hash = digest.digest(serealizarObjeto(pilaCoin));

            hash_bigInteger = new BigInteger(1, hash);
            //verifica se o numero do hash não é maior que o numero magico caso for ele vai gerar outro numero aleatorio e verificar novamente até achar um numero menor
            while (NUMERO_VERIFICADOR_SERVIDOR.compareTo(hash_bigInteger) < 0){
                pilaCoin.setNumeroMagico(random.nextLong());
                hash = digest.digest(Utils.serealizarObjeto(pilaCoin));
                hash_bigInteger = new BigInteger(1, hash);
            }
            System.out.println("minerado pilacoin !");
            //deppois de encontrado um numero menor que o numero magico iniciasse a validacao do pila minerado
            new Thread(new ValidarPilaUtil(pilaCoin, response_servidor)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
