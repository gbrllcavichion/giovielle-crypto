package com.giovielle.crypto;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import com.giovielle.crypto.service.RsaKeyService;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== Giovielle Crypto ===");

            System.out.print("Digite seu nome: ");
            String name = scanner.nextLine().trim();

            if (!name.matches("[\\p{L}\\p{N}_-]+")) {
                System.out.println("Informe um nome válido, sem espaços.");
                return;
            }

            RsaKeyService rsaKeyService = new RsaKeyService();

            try {
                KeyPair keyPair = rsaKeyService.generateKeyPair();

                rsaKeyService.saveKeyPair(keyPair, name);

                System.out.println("Par de chaves RSA gerado com sucesso!");
                System.out.println("Chave pública: " + name + ".pu");
                System.out.println("Chave privada: " + name + ".pr");

            } catch (NoSuchAlgorithmException | IOException e) {
                System.err.println("Erro ao gerar ou salvar as chaves: " + e.getMessage());
            }
        }
    }
}