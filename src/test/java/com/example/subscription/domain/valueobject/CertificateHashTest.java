package com.example.subscription.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para o Value Object CertificateHash.
 * 
 * <h2>Cenários testados:</h2>
 * <ul>
 *   <li>Criação com hash válido</li>
 *   <li>Validação de formato SHA-256</li>
 *   <li>Status de registro em blockchain</li>
 *   <li>URLs de explorers</li>
 * </ul>
 */
@DisplayName("CertificateHash Value Object Tests")
class CertificateHashTest {

    // Hash SHA-256 válido de exemplo (64 caracteres hexadecimais)
    private static final String VALID_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    private static final String VALID_TX_ID = "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("Should create registered certificate with of()")
        void shouldCreateRegisteredCertificate() {
            // When
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            // Then
            assertThat(cert.getHash()).isEqualTo(VALID_HASH.toLowerCase());
            assertThat(cert.getBlockchainNetwork()).isEqualTo("ethereum");
            assertThat(cert.getTransactionId()).isEqualTo(VALID_TX_ID);
            assertThat(cert.isRegistered()).isTrue();
            assertThat(cert.isPending()).isFalse();
        }

        @Test
        @DisplayName("Should create pending certificate with pending()")
        void shouldCreatePendingCertificate() {
            // When
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            // Then
            assertThat(cert.getHash()).isEqualTo(VALID_HASH.toLowerCase());
            assertThat(cert.getBlockchainNetwork()).isEqualTo("pending");
            assertThat(cert.getTransactionId()).isNull();
            assertThat(cert.isRegistered()).isFalse();
            assertThat(cert.isPending()).isTrue();
        }

        @Test
        @DisplayName("Should normalize hash to lowercase")
        void shouldNormalizeHashToLowercase() {
            // Given - hash em maiúsculo
            String uppercaseHash = VALID_HASH.toUpperCase();
            
            // When
            CertificateHash cert = CertificateHash.of(uppercaseHash, "ethereum", VALID_TX_ID);
            
            // Then
            assertThat(cert.getHash()).isEqualTo(VALID_HASH.toLowerCase());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject null hash")
        void shouldRejectNullHash() {
            assertThatThrownBy(() -> CertificateHash.of(null, "ethereum", VALID_TX_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SHA-256 hash format");
        }

        @Test
        @DisplayName("Should reject hash with wrong length")
        void shouldRejectHashWithWrongLength() {
            String shortHash = "e3b0c44298fc1c149afbf4c8996fb924"; // 32 chars (MD5)
            
            assertThatThrownBy(() -> CertificateHash.of(shortHash, "ethereum", VALID_TX_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SHA-256 hash format");
        }

        @Test
        @DisplayName("Should reject hash with invalid characters")
        void shouldRejectHashWithInvalidCharacters() {
            String invalidHash = "g3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // 'g' inválido
            
            assertThatThrownBy(() -> CertificateHash.of(invalidHash, "ethereum", VALID_TX_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SHA-256 hash format");
        }

        @Test
        @DisplayName("Should reject null hash in pending()")
        void shouldRejectNullHashInPending() {
            assertThatThrownBy(() -> CertificateHash.pending(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SHA-256 hash format");
        }
    }

    @Nested
    @DisplayName("Explorer URLs")
    class ExplorerUrls {

        @Test
        @DisplayName("Should return Etherscan URL for Ethereum")
        void shouldReturnEtherscanUrl() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            assertThat(cert.getExplorerUrl()).isEqualTo("https://etherscan.io/tx/" + VALID_TX_ID);
        }

        @Test
        @DisplayName("Should return Polygonscan URL for Polygon")
        void shouldReturnPolygonscanUrl() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "polygon", VALID_TX_ID);
            
            assertThat(cert.getExplorerUrl()).isEqualTo("https://polygonscan.com/tx/" + VALID_TX_ID);
        }

        @Test
        @DisplayName("Should return Sepolia URL for testnet")
        void shouldReturnSepoliaUrl() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "sepolia", VALID_TX_ID);
            
            assertThat(cert.getExplorerUrl()).isEqualTo("https://sepolia.etherscan.io/tx/" + VALID_TX_ID);
        }

        @Test
        @DisplayName("Should return null URL for pending certificate")
        void shouldReturnNullUrlForPending() {
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            assertThat(cert.getExplorerUrl()).isNull();
        }

        @Test
        @DisplayName("Should return null URL for unknown network")
        void shouldReturnNullUrlForUnknownNetwork() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "unknown-chain", VALID_TX_ID);
            
            assertThat(cert.getExplorerUrl()).isNull();
        }
    }

    @Nested
    @DisplayName("Utility Methods")
    class UtilityMethods {

        @Test
        @DisplayName("Should return short hash representation")
        void shouldReturnShortHash() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            String shortHash = cert.getShortHash();
            
            assertThat(shortHash).hasSize(19); // 8 + 3 + 8
            assertThat(shortHash).startsWith(VALID_HASH.substring(0, 8));
            assertThat(shortHash).contains("...");
            assertThat(shortHash).endsWith(VALID_HASH.substring(56));
        }

        @Test
        @DisplayName("Should have meaningful toString()")
        void shouldHaveMeaningfulToString() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            String str = cert.toString();
            
            assertThat(str).contains("CertificateHash");
            assertThat(str).contains("ethereum");
            assertThat(str).contains("registered=true");
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("Should be equal when hash is the same")
        void shouldBeEqualWhenHashIsSame() {
            CertificateHash cert1 = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            CertificateHash cert2 = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            assertThat(cert1).isEqualTo(cert2);
            assertThat(cert1.hashCode()).isEqualTo(cert2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when hash is different")
        void shouldNotBeEqualWhenHashIsDifferent() {
            String differentHash = "a3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
            
            CertificateHash cert1 = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            CertificateHash cert2 = CertificateHash.of(differentHash, "ethereum", VALID_TX_ID);
            
            assertThat(cert1).isNotEqualTo(cert2);
        }
    }
}

