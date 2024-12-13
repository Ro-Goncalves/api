package com.rgbrain.api.domain.consulta.validacoes;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import com.rgbrain.api.domain.consulta.ValidacaoException;

public class ValidadorHorarioFuncionamentoClinica {
    
    public static void validar(LocalDateTime dataConsulta) {
        var isDomingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SATURDAY);
        var isAntesAbertura = dataConsulta.getHour() < 7;
        var isDepoisEncerramento = dataConsulta.getHour() > 18;

        if (isDomingo || isAntesAbertura || isDepoisEncerramento) {
            throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica");
        }
        
    }
}