
package com.giovielle.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.giovielle.crypto.service.AesService;
import com.giovielle.crypto.service.RsaKeyService;
import com.giovielle.crypto.utils.FileUtils;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== Giovielle Crypto ===");
            System.out.println("1. Gerar par de chaves RSA");
            System.out.println("2. Carregar chaves RSA existentes");
            System.out.println("3. Criptografar arquivo com AES-CTR");
            System.out.println("4. Proteger chave AES ou IV com RSA");
            System.out.println("5. Recuperar chave AES ou IV com RSA");
            System.out.println("6. Descriptografar arquivo com AES-CTR");

            System.out.print("Escolha uma opção: ");
            String option = scanner.nextLine().trim();

            if (!option.matches("[1-6]")) {
                System.out.println("Opção inválida.");
                return;
            }

            RsaKeyService rsaKeyService = new RsaKeyService();
            AesService aesService = new AesService();

            try {

                // OPÇÕES 1 E 2 — CHAVES RSA

                if (option.equals("1") || option.equals("2")) {

                    System.out.print("Digite seu nome: ");
                    String name = scanner.nextLine().trim();

                    if (!name.matches("[\\p{L}\\p{N}_-]+")) {
                        System.out.println("Nome inválido.");
                        return;
                    }

                    if (option.equals("1")) {

                        KeyPair keyPair =
                                rsaKeyService.generateKeyPair();

                        rsaKeyService.saveKeyPair(keyPair, name);

                        System.out.println(
                                "Par de chaves gerado com sucesso!"
                        );

                    } else {

                        Path publicPath =
                                Path.of("keys", name + ".pu");

                        Path privatePath =
                                Path.of("keys", name + ".pr");

                        PublicKey publicKey =
                                rsaKeyService.loadPublicKey(publicPath);

                        PrivateKey privateKey =
                                rsaKeyService.loadPrivateKey(privatePath);

                        System.out.println(
                                "Chaves carregadas com sucesso!"
                        );

                        System.out.println(
                                "Chave pública: " + publicKey.getFormat()
                        );

                        System.out.println(
                                "Chave privada: " + privateKey.getFormat()
                        );

                        boolean matches = rsaKeyService.keysMatch(
                                publicKey,
                                privateKey
                        );

                        if (matches) {
                            System.out.println(
                                    "As chaves pertencem ao mesmo par RSA!"
                            );
                        } else {
                            System.out.println(
                                    "As chaves RSA não correspondem!"
                            );
                        }
                    }

                // OPÇÃO 3 — CRIPTOGRAFAR COM AES-CTR

                } else if (option.equals("3")) {

                    System.out.print("Caminho do arquivo TXT: ");
                    Path inputPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(inputPath)) {
                        System.out.println("Arquivo não encontrado.");
                        return;
                    }

                    System.out.print(
                            "Tamanho da chave AES (128 ou 256): "
                    );

                    String keySizeInput = scanner.nextLine().trim();

                    if (!keySizeInput.equals("128") &&
                        !keySizeInput.equals("256")) {

                        System.out.println("Tamanho de chave inválido.");
                        return;
                    }

                    int keySize = Integer.parseInt(keySizeInput);

                    SecretKey key = aesService.generateKey(keySize);
                    byte[] iv = aesService.generateIv();

                    Path outputDir = Path.of(
                            "output",
                            UUID.randomUUID().toString()
                    );

                    Files.createDirectories(outputDir);

                    Path encryptedFile = outputDir.resolve("Y.txt");
                    Path keyFile = outputDir.resolve("K.txt");
                    Path ivFile = outputDir.resolve("IV.txt");

                    aesService.encryptFile(
                            inputPath,
                            encryptedFile,
                            key,
                            iv
                    );

                    FileUtils.saveHex(keyFile, key.getEncoded());
                    FileUtils.saveHex(ivFile, iv);

                    System.out.println(
                            "Arquivo criptografado com sucesso!"
                    );

                    System.out.println(
                            "Arquivos salvos em: " +
                            outputDir.toAbsolutePath()
                    );

                // OPÇÃO 4 — PROTEGER COM RSA

                } else if (option.equals("4")) {

                    System.out.print(
                            "Caminho do arquivo K.txt ou IV.txt: "
                    );

                    Path inputPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(inputPath)) {
                        System.out.println("Arquivo não encontrado.");
                        return;
                    }

                    System.out.print(
                            "Caminho da chave pública RSA (.pu): "
                    );

                    Path publicKeyPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(publicKeyPath)) {
                        System.out.println(
                                "Chave pública não encontrada."
                        );
                        return;
                    }

                    PublicKey publicKey =
                            rsaKeyService.loadPublicKey(publicKeyPath);

                    byte[] originalData =
                            FileUtils.readHex(inputPath);

                    byte[] encryptedData =
                            rsaKeyService.encryptBytes(
                                    publicKey,
                                    originalData
                            );

                    Path outputPath = inputPath.resolveSibling(
                            inputPath.getFileName().toString() + ".enc"
                    );

                    FileUtils.saveHex(outputPath, encryptedData);

                    System.out.println("Arquivo protegido com RSA!");

                    System.out.println(
                            "Arquivo gerado: " +
                            outputPath.toAbsolutePath()
                    );

                // OPÇÃO 5 — RECUPERAR COM RSA

                } else if (option.equals("5")) {

                    System.out.print(
                            "Caminho do arquivo protegido (.enc): "
                    );

                    Path inputPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(inputPath)) {
                        System.out.println(
                                "Arquivo protegido não encontrado."
                        );
                        return;
                    }

                    System.out.print(
                            "Caminho da chave privada RSA (.pr): "
                    );

                    Path privateKeyPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(privateKeyPath)) {
                        System.out.println(
                                "Chave privada não encontrada."
                        );
                        return;
                    }

                    PrivateKey privateKey =
                            rsaKeyService.loadPrivateKey(privateKeyPath);

                    byte[] encryptedData =
                            FileUtils.readHex(inputPath);

                    byte[] decryptedData =
                            rsaKeyService.decryptBytes(
                                    privateKey,
                                    encryptedData
                            );

                    String fileName =
                            inputPath.getFileName().toString();

                    if (!fileName.endsWith(".enc")) {
                        System.out.println(
                                "O arquivo deve ter extensão .enc."
                        );
                        return;
                    }

                    String recoveredName = fileName.substring(
                            0,
                            fileName.length() - 4
                    ) + ".recovered";

                    Path outputPath = inputPath.resolveSibling(
                            recoveredName
                    );

                    FileUtils.saveHex(outputPath, decryptedData);

                    System.out.println(
                            "Arquivo recuperado com RSA!"
                    );

                    System.out.println(
                            "Arquivo gerado: " +
                            outputPath.toAbsolutePath()
                    );

                // OPÇÃO 6 — DESCRIPTOGRAFAR COM AES-CTR

                } else if (option.equals("6")) {

                    System.out.print(
                            "Caminho do arquivo criptografado (Y.txt): "
                    );

                    Path encryptedPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(encryptedPath)) {
                        System.out.println(
                                "Arquivo criptografado não encontrado."
                        );
                        return;
                    }

                    System.out.print(
                            "Caminho da chave AES recuperada: "
                    );

                    Path keyPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(keyPath)) {
                        System.out.println(
                                "Arquivo da chave AES não encontrado."
                        );
                        return;
                    }

                    System.out.print(
                            "Caminho do IV recuperado: "
                    );

                    Path ivPath =
                            Path.of(scanner.nextLine().trim());

                    if (!Files.isRegularFile(ivPath)) {
                        System.out.println(
                                "Arquivo do IV não encontrado."
                        );
                        return;
                    }

                    // Converter os arquivos hexadecimais em bytes
                    byte[] keyBytes = FileUtils.readHex(keyPath);
                    byte[] iv = FileUtils.readHex(ivPath);

                    // Validar o tamanho da chave AES
                    if (keyBytes.length != 16 &&
                        keyBytes.length != 32) {

                        System.out.println(
                                "Chave AES inválida: esperado 16 ou 32 bytes."
                        );
                        return;
                    }

                    // Validar o tamanho do IV
                    if (iv.length != 16) {
                        System.out.println(
                                "IV inválido: esperado 16 bytes."
                        );
                        return;
                    }

                    // Reconstruir o objeto SecretKey
                    SecretKey key = new SecretKeySpec(
                            keyBytes,
                            "AES"
                    );

                    Path outputPath = encryptedPath.resolveSibling(
                            "recuperado.txt"
                    );

                    // Descriptografar usando AES-CTR
                    aesService.decryptFile(
                            encryptedPath,
                            outputPath,
                            key,
                            iv
                    );

                    System.out.println(
                            "Arquivo descriptografado com sucesso!"
                    );

                    System.out.println(
                            "Arquivo recuperado: " +
                            outputPath.toAbsolutePath()
                    );
                }

            } catch (IOException | GeneralSecurityException |
                     IllegalArgumentException e) {

                System.err.println("Erro: " + e.getMessage());
            }
        }
    }
}
