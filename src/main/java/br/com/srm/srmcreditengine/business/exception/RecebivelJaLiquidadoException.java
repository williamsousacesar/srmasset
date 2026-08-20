package br.com.srm.srmcreditengine.business.exception;

public class RecebivelJaLiquidadoException extends RegraDeNegocioException {

    public RecebivelJaLiquidadoException(Long recebivelId) {
        super("Recebivel de id " + recebivelId + " ja foi liquidado anteriormente.");
    }
}
