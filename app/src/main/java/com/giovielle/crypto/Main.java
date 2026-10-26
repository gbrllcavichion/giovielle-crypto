package com.giovielle.crypto;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

import com.giovielle.crypto.service.RsaKeyService;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== Giovielle Crypto ===");
            System.out.println("1. Gerar par de chaves RSA");
            System.out.println("2. Carregar chaves RSA existentes");

            System.out.print("Escolha uma opção: ");
            String option = scanner.nextLine().trim();

            if (!option.equals("1") && !option.equals("2")) {
                System.out.println("Opção inválida.");
                return;
            }

            System.out.print("Digite seu nome: ");
            String name = scanner.nextLine().trim();

            if (!name.matches("[\\p{L}\\p{N}_-]+")) {
                System.out.println("Nome inválido.");
                return;
            }

            RsaKeyService rsaKeyService = new RsaKeyService();

            try {

                if (option.equals("1")) {

                    KeyPair keyPair = rsaKeyService.generateKeyPair();

                    rsaKeyService.saveKeyPair(keyPair, name);

                    System.out.println("Par de chaves gerado com sucesso!");

                } else {

                    Path publicPath = Path.of("keys", name + ".pu");
                    Path privatePath = Path.of("keys", name + ".pr");

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
                }

            } catch (IOException | GeneralSecurityException e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }
}