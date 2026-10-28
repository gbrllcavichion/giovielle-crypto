
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

            System.out.print("Escolha uma opção: ");
            String option = scanner.nextLine().trim();

            if (!option.equals("1") &&
                !option.equals("2") &&
                !option.equals("3")) {

                System.out.println("Opção inválida.");
                return;
            }

            RsaKeyService rsaKeyService = new RsaKeyService();
            AesService aesService = new AesService();

            try {

                if (option.equals("1") || option.equals("2")) {

                    System.out.print("Digite seu nome: ");
                    String name = scanner.nextLine().trim();

                    if (!name.matches("[\\p{L}\\p{N}_-]+")) {
                        System.out.println("Nome inválido.");
                        return;
                    }

                    if (option.equals("1")) {

                        KeyPair keyPair = rsaKeyService.generateKeyPair();

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

                        System.out.println("Chaves carregadas com sucesso!");

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

                } else if (option.equals("3")) {

                    System.out.print("Caminho do arquivo TXT: ");
                    String input = scanner.nextLine().trim();

                    Path inputPath = Path.of(input);

                    if (!Files.isRegularFile(inputPath)) {
                        System.out.println("Arquivo não encontrado.");
                        return;
                    }

                    System.out.print("Tamanho da chave AES (128 ou 256): ");
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

                    System.out.println("Arquivo criptografado com sucesso!");
                    System.out.println(
                            "Arquivos salvos em: " +
                            outputDir.toAbsolutePath()
                    );
                }

            } catch (IOException | GeneralSecurityException e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }
}
