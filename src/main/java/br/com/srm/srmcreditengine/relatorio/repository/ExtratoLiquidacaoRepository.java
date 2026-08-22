package br.com.srm.srmcreditengine.relatorio.repository;

import br.com.srm.srmcreditengine.relatorio.dto.FiltroExtratoLiquidacaoDTO;
import br.com.srm.srmcreditengine.relatorio.dto.ItemExtratoLiquidacaoDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ExtratoLiquidacaoRepository {

    private static final String SQL_BASE = """
            from liquidacao liq
            inner join recebivel rec on rec.id = liq.recebivel_id
            inner join cedente ced on ced.id = rec.cedente_id
            where 1 = 1
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ExtratoLiquidacaoRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ItemExtratoLiquidacaoDTO> buscarExtrato(FiltroExtratoLiquidacaoDTO filtro) {
        StringBuilder sql = new StringBuilder("""
                select
                    liq.id as liquidacao_id,
                    rec.id as recebivel_id,
                    ced.id as cedente_id,
                    ced.nome as nome_cedente,
                    rec.tipo as tipo_recebivel,
                    rec.moeda_titulo as moeda_titulo,
                    liq.moeda_pagamento as moeda_pagamento,
                    liq.valor_presente_moeda_titulo as valor_presente_moeda_titulo,
                    liq.valor_presente_moeda_pagamento as valor_presente_moeda_pagamento,
                    liq.taxa_cambio_utilizada as taxa_cambio_utilizada,
                    liq.spread_aplicado as spread_aplicado,
                    liq.taxa_base_aplicada as taxa_base_aplicada,
                    liq.data_liquidacao as data_liquidacao
                """).append(SQL_BASE);

        Map<String, Object> parametros = new HashMap<>();
        aplicarFiltros(filtro, sql, parametros);

        sql.append(" order by liq.data_liquidacao desc ");
        sql.append(" limit :tamanho offset :offset ");
        parametros.put("tamanho", filtro.tamanho());
        parametros.put("offset", filtro.pagina() * filtro.tamanho());

        MapSqlParameterSource paramSource = new MapSqlParameterSource(parametros);

        return jdbcTemplate.query(sql.toString(), paramSource, (resultSet, numeroLinha) -> new ItemExtratoLiquidacaoDTO(
                resultSet.getLong("liquidacao_id"),
                resultSet.getLong("recebivel_id"),
                resultSet.getLong("cedente_id"),
                resultSet.getString("nome_cedente"),
                resultSet.getString("tipo_recebivel"),
                resultSet.getString("moeda_titulo"),
                resultSet.getString("moeda_pagamento"),
                resultSet.getBigDecimal("valor_presente_moeda_titulo"),
                resultSet.getBigDecimal("valor_presente_moeda_pagamento"),
                resultSet.getBigDecimal("taxa_cambio_utilizada"),
                resultSet.getBigDecimal("spread_aplicado"),
                resultSet.getBigDecimal("taxa_base_aplicada"),
                resultSet.getTimestamp("data_liquidacao").toLocalDateTime()));
    }

    public long contarExtrato(FiltroExtratoLiquidacaoDTO filtro) {
        StringBuilder sql = new StringBuilder("select count(*) ").append(SQL_BASE);
        Map<String, Object> parametros = new HashMap<>();
        aplicarFiltros(filtro, sql, parametros);

        MapSqlParameterSource paramSource = new MapSqlParameterSource(parametros);
        Long total = jdbcTemplate.queryForObject(sql.toString(), paramSource, Long.class);
        return total == null ? 0L : total;
    }

    private void aplicarFiltros(FiltroExtratoLiquidacaoDTO filtro, StringBuilder sql, Map<String, Object> parametros) {
        if (filtro.dataInicio() != null) {
            sql.append(" and liq.data_liquidacao >= :dataInicio ");
            parametros.put("dataInicio", filtro.dataInicio().atStartOfDay());
        }
        if (filtro.dataFim() != null) {
            sql.append(" and liq.data_liquidacao < :dataFim ");
            parametros.put("dataFim", filtro.dataFim().plusDays(1).atStartOfDay());
        }
        if (filtro.cedenteId() != null) {
            sql.append(" and ced.id = :cedenteId ");
            parametros.put("cedenteId", filtro.cedenteId());
        }
        if (filtro.moeda() != null && !filtro.moeda().isBlank()) {
            sql.append(" and liq.moeda_pagamento = :moeda ");
            parametros.put("moeda", filtro.moeda());
        }
    }
}
