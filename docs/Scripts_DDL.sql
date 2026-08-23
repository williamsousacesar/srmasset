-- 3. TABELA LIQUIDACAO
-- ==============================================================================
CREATE TABLE liquidacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recebivel_id BIGINT NOT NULL,
    valor_presente_moeda_titulo DECIMAL(19, 2) NOT NULL,
    moeda_pagamento VARCHAR(3) NOT NULL,
    valor_presente_moeda_pagamento DECIMAL(19, 2) NOT NULL,
    taxa_cambio_utilizada DECIMAL(19, 8),
    spread_aplicado DECIMAL(9, 6) NOT NULL,
    taxa_base_aplicada DECIMAL(9, 6) NOT NULL,
    data_liquidacao DATETIME(6) NOT NULL,
    CONSTRAINT uk_liquidacao_recebivel UNIQUE (recebivel_id),
    CONSTRAINT fk_liquidacao_recebivel FOREIGN KEY (recebivel_id) 
        REFERENCES recebivel (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ==============================================================================
-- 4. TABELA TAXA_CAMBIO
-- ==============================================================================
CREATE TABLE taxa_cambio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moeda_origem VARCHAR(3) NOT NULL,
    moeda_destino VARCHAR(3) NOT NULL,
    valor DECIMAL(19, 8) NOT NULL,
    data_atualizacao DATETIME(6) NOT NULL,
    CONSTRAINT uk_taxa_cambio_origem_destino UNIQUE (moeda_origem, moeda_destino)
);