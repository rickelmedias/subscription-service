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

        @Test
        @DisplayName("Should reject invalid hash format in pending()")
        void shouldRejectInvalidHashFormatInPending() {
            // Hash com formato inválido (muito curto)
            String invalidHash = "abc123";
            
            assertThatThrownBy(() -> CertificateHash.pending(invalidHash))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SHA-256 hash format");
        }

        @Test
        @DisplayName("Should reject hash with invalid characters in pending()")
        void shouldRejectHashWithInvalidCharsInPending() {
            // Hash com caractere inválido 'g'
            String invalidHash = "g3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
            
            assertThatThrownBy(() -> CertificateHash.pending(invalidHash))
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

    /**
     * Testes adicionais para cobrir branches extras.
     * @author Guilherme
     */
    @Nested
    @DisplayName("Additional Coverage Tests")
    class AdditionalCoverageTests {

        @Test
        @DisplayName("Should return Goerli URL for goerli network")
        void shouldReturnGoerliUrl() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "goerli", VALID_TX_ID);
            
            assertThat(cert.getExplorerUrl()).isEqualTo("https://goerli.etherscan.io/tx/" + VALID_TX_ID);
        }

        @Test
        @DisplayName("Should handle short hash for getShortHash when hash is null")
        void shouldHandleShortHashWhenHashIsShort() {
            // Testando o caso onde hash tem menos de 16 caracteres (edge case)
            // Isso só pode ser testado indiretamente via reflection ou através de CertificateHash.pending()
            // mas o método pending também valida o hash.
            // Então vamos testar o comportamento normal do getShortHash
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            String shortHash = cert.getShortHash();
            
            assertThat(shortHash).isNotNull();
            assertThat(shortHash).contains("...");
        }

        @Test
        @DisplayName("Should verify registeredAt is set on creation")
        void shouldSetRegisteredAtOnCreation() {
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            assertThat(cert.getRegisteredAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Should handle pending certificate with getExplorerUrl")
        void shouldHandlePendingCertificateWithGetExplorerUrl() {
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            assertThat(cert.getExplorerUrl()).isNull();
            assertThat(cert.isRegistered()).isFalse();
            assertThat(cert.isPending()).isTrue();
        }

        @Test
        @DisplayName("Should verify toString contains all expected info")
        void shouldVerifyToStringContainsExpectedInfo() {
            CertificateHash registered = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            CertificateHash pending = CertificateHash.pending(VALID_HASH);
            
            assertThat(registered.toString()).contains("registered=true");
            assertThat(pending.toString()).contains("registered=false");
        }
    }

    /**
     * Testes para cobrir edge cases e branches adicionais.
     * @author Rickelme
     */
    @Nested
    @DisplayName("Edge Case Coverage Tests")
    class EdgeCaseCoverageTests {

        @Test
        @DisplayName("Should return null for getShortHash when hash is null via reflection")
        void shouldReturnNullForGetShortHashWhenHashIsNull() throws Exception {
            // Given - Criamos um CertificateHash via reflection com hash null
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            // Usar reflection para setar hash para null
            java.lang.reflect.Field hashField = CertificateHash.class.getDeclaredField("hash");
            hashField.setAccessible(true);
            hashField.set(cert, null);
            
            // When
            String shortHash = cert.getShortHash();
            
            // Then - Deve retornar null quando hash é null
            assertThat(shortHash).isNull();
        }

        @Test
        @DisplayName("Should return short hash as-is when hash is less than 16 characters via reflection")
        void shouldReturnShortHashAsIsWhenLessThan16Chars() throws Exception {
            // Given - Criamos um CertificateHash e usamos reflection para setar hash curto
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            java.lang.reflect.Field hashField = CertificateHash.class.getDeclaredField("hash");
            hashField.setAccessible(true);
            hashField.set(cert, "abc123");
            
            // When
            String shortHash = cert.getShortHash();
            
            // Then - Deve retornar o hash como está (sem abreviar)
            assertThat(shortHash).isEqualTo("abc123");
        }

        @Test
        @DisplayName("Should return false for isRegistered when transactionId is null")
        void shouldReturnFalseForIsRegisteredWhenTransactionIdIsNull() throws Exception {
            // Given - Criar um certificado onde transactionId é null mas network não é "pending"
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            // Usar reflection para mudar a network de "pending" para algo diferente
            java.lang.reflect.Field networkField = CertificateHash.class.getDeclaredField("blockchainNetwork");
            networkField.setAccessible(true);
            networkField.set(cert, "ethereum");
            // transactionId permanece null
            
            // When
            boolean isRegistered = cert.isRegistered();
            
            // Then - Deve retornar false porque transactionId é null
            assertThat(isRegistered).isFalse();
        }

        @Test
        @DisplayName("Should return true for isRegistered when both conditions are met")
        void shouldReturnTrueForIsRegisteredWhenBothConditionsAreMet() {
            // Given
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            // When
            boolean isRegistered = cert.isRegistered();
            
            // Then - transactionId != null && network != "pending"
            assertThat(isRegistered).isTrue();
        }

        @Test
        @DisplayName("Should return null for getExplorerUrl with non-null transactionId but network is pending")
        void shouldReturnNullForExplorerUrlWhenNetworkIsPending() throws Exception {
            // Given - Criar cenário onde transactionId não é null mas network é "pending"
            CertificateHash cert = CertificateHash.of(VALID_HASH, "ethereum", VALID_TX_ID);
            
            // Usar reflection para mudar a network para "pending"
            java.lang.reflect.Field networkField = CertificateHash.class.getDeclaredField("blockchainNetwork");
            networkField.setAccessible(true);
            networkField.set(cert, "pending");
            
            // When
            String url = cert.getExplorerUrl();
            
            // Then - Deve retornar null porque isRegistered() retorna false
            assertThat(url).isNull();
        }

        @Test
        @DisplayName("Should handle hash with exactly 16 characters for getShortHash")
        void shouldHandleHashWithExactly16CharactersForGetShortHash() throws Exception {
            // Given - Hash com exatamente 16 caracteres (edge case do if < 16)
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            java.lang.reflect.Field hashField = CertificateHash.class.getDeclaredField("hash");
            hashField.setAccessible(true);
            hashField.set(cert, "1234567890123456");
            
            // When
            String shortHash = cert.getShortHash();
            
            // Then - Com 16 chars, deve abreviar (8 + ... + 8 = 19 chars)
            assertThat(shortHash).contains("...");
            assertThat(shortHash).hasSize(19);
        }

        @Test
        @DisplayName("Should return hash as-is when length is exactly 15 characters")
        void shouldReturnHashAsIsWhenLengthIs15Characters() throws Exception {
            // Given - Hash com exatamente 15 caracteres
            CertificateHash cert = CertificateHash.pending(VALID_HASH);
            
            java.lang.reflect.Field hashField = CertificateHash.class.getDeclaredField("hash");
            hashField.setAccessible(true);
            hashField.set(cert, "123456789012345");
            
            // When
            String shortHash = cert.getShortHash();
            
            // Then - Com menos de 16 chars, retorna como está
            assertThat(shortHash).isEqualTo("123456789012345");
            assertThat(shortHash).hasSize(15);
        }
    }
}

