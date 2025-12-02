package com.example.subscription.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Value Object que representa um hash de certificado para registro em Blockchain.
 * 
 * <h2>Princípios DDD - Value Object:</h2>
 * <ul>
 *   <li><b>Imutabilidade</b>: Hash não pode ser alterado após criação</li>
 *   <li><b>Auto-validação</b>: Valida formato SHA-256 (64 hex chars)</li>
 *   <li><b>Sem identidade</b>: Igualdade baseada no valor do hash</li>
 * </ul>
 * 
 * <h2>Integração com Blockchain:</h2>
 * <p>Este Value Object pode ser usado para registrar certificados em uma
 * rede blockchain (Ethereum, Polygon, Hyperledger), garantindo:</p>
 * <ul>
 *   <li><b>Imutabilidade</b>: Registros não podem ser alterados</li>
 *   <li><b>Rastreabilidade</b>: Histórico completo de certificações</li>
 *   <li><b>Verificabilidade</b>: Qualquer pessoa pode verificar autenticidade</li>
 * </ul>
 * 
 * <h2>Arquitetura Clean Architecture:</h2>
 * <p>O blockchain entra como um <b>Adapter de Infraestrutura</b>, implementando
 * um Port (interface) como AuditLedger, mantendo o domínio independente da
 * tecnologia blockchain específica.</p>
 * 
 * @author Rickelme
 * @see Credits Outro Value Object do domínio
 */
@Embeddable
@Getter
@EqualsAndHashCode(exclude = "registeredAt") // Exclui timestamp da comparação
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificateHash implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final String SHA256_PATTERN = "^[a-fA-F0-9]{64}$";
    
    private String hash;
    private LocalDateTime registeredAt; // Não participa do equals/hashCode
    private String blockchainNetwork;
    private String transactionId;
    
    private CertificateHash(String hash, String network, String txId) {
        validate(hash);
        this.hash = hash.toLowerCase();
        this.blockchainNetwork = network;
        this.transactionId = txId;
        this.registeredAt = LocalDateTime.now();
    }
    
    /**
     * Factory method para criar hash de certificado registrado em blockchain.
     * 
     * @param hash Hash SHA-256 do certificado (64 caracteres hex)
     * @param network Rede blockchain (ethereum, polygon, sepolia)
     * @param transactionId ID da transação no blockchain
     * @return CertificateHash imutável
     */
    public static CertificateHash of(String hash, String network, String transactionId) {
        return new CertificateHash(hash, network, transactionId);
    }
    
    /**
     * Cria hash ainda não registrado em blockchain (pendente).
     * Útil para criar o hash localmente antes de enviar para a rede.
     * 
     * @param hash Hash SHA-256 do certificado
     * @return CertificateHash com status pendente
     */
    public static CertificateHash pending(String hash) {
        CertificateHash cert = new CertificateHash();
        if (hash == null || !hash.matches(SHA256_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid SHA-256 hash format. Expected 64 hex characters."
            );
        }
        cert.hash = hash.toLowerCase();
        cert.registeredAt = LocalDateTime.now();
        cert.blockchainNetwork = "pending";
        cert.transactionId = null;
        return cert;
    }
    
    /**
     * Valida formato SHA-256 (64 caracteres hexadecimais).
     */
    private void validate(String hash) {
        if (hash == null || !hash.matches(SHA256_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid SHA-256 hash format. Expected 64 hex characters, got: " + 
                (hash != null ? hash.length() : "null")
            );
        }
    }
    
    /**
     * Verifica se o certificado foi registrado em blockchain.
     * 
     * @return true se possui transactionId válido
     */
    public boolean isRegistered() {
        return transactionId != null && !"pending".equals(blockchainNetwork);
    }
    
    /**
     * Verifica se está pendente de registro.
     */
    public boolean isPending() {
        return "pending".equals(blockchainNetwork);
    }
    
    /**
     * Retorna URL do explorer blockchain para verificação pública.
     * 
     * @return URL do explorer ou null se não registrado
     */
    public String getExplorerUrl() {
        if (!isRegistered()) {
            return null;
        }
        
        return switch (blockchainNetwork) {
            case "ethereum" -> "https://etherscan.io/tx/" + transactionId;
            case "polygon" -> "https://polygonscan.com/tx/" + transactionId;
            case "sepolia" -> "https://sepolia.etherscan.io/tx/" + transactionId;
            case "goerli" -> "https://goerli.etherscan.io/tx/" + transactionId;
            default -> null;
        };
    }
    
    /**
     * Retorna versão abreviada do hash para exibição.
     */
    public String getShortHash() {
        if (hash == null || hash.length() < 16) {
            return hash;
        }
        return hash.substring(0, 8) + "..." + hash.substring(hash.length() - 8);
    }
    
    @Override
    public String toString() {
        return String.format("CertificateHash[%s @ %s, registered=%s]", 
            getShortHash(),
            blockchainNetwork,
            isRegistered()
        );
    }
}

