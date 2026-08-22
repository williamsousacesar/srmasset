-- ==============================================================================
-- 1. TABELA CEDENTE
-- ==============================================================================
CREATE TABLE cedente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    data_inclusao DATETIME(6),
    CONSTRAINT uk_cedente_documento UNIQUE (documento)
);

-- ==============================================================================
-- 2. TABELA RECEBIVEL
-- ==============================================================================
CREATE TABLE recebivel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cedente_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    valor_face DECIMAL(19, 2) NOT NULL,
    moeda_titulo VARCHAR(3) NOT NULL,
    prazo_meses INT NOT NULL,
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    versao BIGINT,
    data_inclusao DATETIME(6),
    data_ultima_alteracao DATETIME(6),
    CONSTRAINT fk_recebivel_cedente FOREIGN KEY (cedente_id) 
        REFERENCES cedente (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Índices de performance para buscas frequentes
CREATE INDEX idx_recebivel_cedente_id ON recebivel(cedente_id);
CREATE INDEX idx_recebivel_status ON recebivel(status);

-- ==============================================================================
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